import { useCallback, useEffect, useMemo, useState } from 'react'
import { BarChart, Bar, LineChart, Line, CartesianGrid, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import { Activity, BarChart3, BookOpen, Calendar, GraduationCap, RefreshCw, Users, Wallet } from 'lucide-react'
import { apiFetch } from '../../../lib/api'

const number = (value) => Number(value || 0)
const money = (value) => `₹${number(value).toLocaleString('en-IN')}`
const total = (rows, field = 'value') => (rows || []).reduce((sum, row) => sum + number(row[field]), 0)

function Card({ title, value, subtitle, icon: Icon, color = 'blue' }) {
  const colors = { blue: 'bg-blue-50 text-blue-600', green: 'bg-emerald-50 text-emerald-600', amber: 'bg-amber-50 text-amber-600', violet: 'bg-violet-50 text-violet-600' }
  return <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm dark:border-indigo-500/20 dark:bg-[#1a1f35]">
    <div className="flex items-start justify-between gap-3"><div><p className="text-[11px] font-semibold uppercase tracking-wide text-slate-500 dark:text-slate-400">{title}</p><p className="mt-2 text-2xl font-bold text-slate-800 dark:text-slate-100">{value}</p>{subtitle && <p className="mt-1 text-[11px] text-slate-400">{subtitle}</p>}</div><span className={`flex h-10 w-10 items-center justify-center rounded-xl ${colors[color] || colors.blue}`}><Icon className="h-5 w-5" /></span></div>
  </div>
}

function Panel({ title, children }) {
  return <section className="rounded-2xl border border-slate-200 bg-white shadow-sm dark:border-indigo-500/20 dark:bg-[#1a1f35]"><div className="border-b border-slate-100 px-5 py-3 dark:border-indigo-500/10"><h2 className="text-sm font-bold text-slate-700 dark:text-slate-200">{title}</h2></div><div className="p-4">{children}</div></section>
}

function Chart({ rows, bars = false }) {
  if (!rows?.length) return <p className="py-10 text-center text-sm text-slate-400">No records available for the selected school/session.</p>
  return <ResponsiveContainer width="100%" height={260}><>{bars ? <BarChart data={rows}><CartesianGrid strokeDasharray="3 3" vertical={false} /><XAxis dataKey="label" tick={{ fontSize: 10 }} /><YAxis tick={{ fontSize: 10 }} /><Tooltip /><Legend /><Bar dataKey="value" fill="#6366f1" name="Value" radius={[5, 5, 0, 0]} /></BarChart> : <LineChart data={rows}><CartesianGrid strokeDasharray="3 3" vertical={false} /><XAxis dataKey="label" tick={{ fontSize: 10 }} /><YAxis tick={{ fontSize: 10 }} /><Tooltip /><Legend /><Line dataKey="value" stroke="#10b981" strokeWidth={2.5} name="Value" /></LineChart>}</></ResponsiveContainer>
}

function ComparisonChart({ rows }) {
  if (!rows?.length) return <p className="py-10 text-center text-sm text-slate-400">No comparison records available.</p>
  return <ResponsiveContainer width="100%" height={260}><BarChart data={rows}><CartesianGrid strokeDasharray="3 3" vertical={false} /><XAxis dataKey="label" tick={{ fontSize: 10 }} /><YAxis tick={{ fontSize: 10 }} /><Tooltip /><Legend /><Bar dataKey="currentValue" fill="#6366f1" name="Current" radius={[5, 5, 0, 0]} /><Bar dataKey="previousValue" fill="#94a3b8" name="Previous" radius={[5, 5, 0, 0]} /></BarChart></ResponsiveContainer>
}

function Table({ rows }) {
  if (!rows?.length) return <p className="py-8 text-center text-sm text-slate-400">No records available.</p>
  return <div className="overflow-auto"><table className="min-w-full text-left text-xs"><thead><tr className="border-b border-slate-100 text-slate-500 dark:border-indigo-500/10">{Object.keys(rows[0]).slice(0, 6).map((key) => <th key={key} className="px-3 py-2 font-semibold">{key}</th>)}</tr></thead><tbody>{rows.slice(0, 100).map((row, index) => <tr key={index} className="border-b border-slate-50 dark:border-indigo-500/10">{Object.keys(rows[0]).slice(0, 6).map((key) => <td key={key} className="px-3 py-2 text-slate-700 dark:text-slate-300">{String(row[key] ?? '')}</td>)}</tr>)}</tbody></table></div>
}

export default function DashboardModule({ type = 'academic', title = 'Dashboard' }) {
  const [data, setData] = useState(null)
  const [days, setDays] = useState(30)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = useCallback(async () => {
    setLoading(true); setError('')
    try { setData(await apiFetch(`/dashboard/${type}?days=${days}`)) } catch (e) { setError(e.message || 'Dashboard data could not be loaded') } finally { setLoading(false) }
  }, [type, days])
  useEffect(() => { load() }, [load])

  const summary = data?.summary || {}
  const cards = useMemo(() => {
    if (type === 'academic') return [
      ['Total Students', number(summary.total), Users, 'blue'],
      ['Male Students', number(summary.male), Users, 'violet'],
      ['Female Students', number(summary.female), Users, 'amber'],
      ['New Admissions', total(data?.admissions), GraduationCap, 'green'],
    ]
    if (type === 'attendance' || type === 'faculty') return [['Total Students', number(summary.total), Users, 'blue'], ['Present', number(summary.present), Activity, 'green'], ['Absent', number(summary.absent), Activity, 'amber'], ['Attendance Rate', `${summary.total ? Math.round(number(summary.present) * 100 / number(summary.total)) : 0}%`, BarChart3, 'violet']]
    if (type === 'fees') return [['Payable', money(summary.payable), Wallet, 'amber'], ['Received', money(summary.received), Wallet, 'green'], ['Balance', money(summary.balance), Wallet, 'blue'], ['Transactions', total(data?.byMonth), Activity, 'violet']]
    if (type === 'library') return [['Total Books', number(summary.books), BookOpen, 'blue'], ['Issued Today', number(summary.issuedToday), BookOpen, 'green'], ['Returns Due', number(summary.returnsDue), Calendar, 'amber'], ['Fine', money(summary.fine), Wallet, 'violet']]
    if (type === 'management') return [['Registered', number(summary.registered), Users, 'blue'], ['Withdrawn', number(summary.withdrawn), Users, 'amber'], ['Total Students', number(summary.total), GraduationCap, 'violet'], ['Faculty', total(data?.faculty), Users, 'green']]
    return [['Mark Entries', number(summary.total), BarChart3, 'blue'], ['Class Groups', (data?.byClass || []).length, GraduationCap, 'violet'], ['Statuses', (data?.byStatus || []).length, Activity, 'green'], ['Session', data?.session || '-', Calendar, 'amber']]
  }, [data, summary, type])

  if (loading && !data) return <div className="rounded-2xl bg-white p-8 text-center text-slate-500 dark:bg-[#1a1f35]">Loading dashboard data…</div>
  return <div className="space-y-5 pb-10">
    <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-center"><div><h1 className="flex items-center gap-2 text-xl font-bold text-slate-800 dark:text-slate-100"><Activity className="h-5 w-5 text-indigo-500" />{title}</h1><p className="mt-1 text-xs text-slate-500 dark:text-slate-400">Live data · session {data?.session || '-'}</p></div><div className="flex gap-2"><select value={days} onChange={(e) => setDays(Number(e.target.value))} className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs dark:border-indigo-500/20 dark:bg-[#1a1f35] dark:text-slate-200"><option value="7">Last 7 days</option><option value="30">Last 30 days</option><option value="90">Last 90 days</option></select><button onClick={load} className="flex items-center gap-1 rounded-xl bg-indigo-600 px-3 py-2 text-xs font-semibold text-white"><RefreshCw className={`h-3.5 w-3.5 ${loading ? 'animate-spin' : ''}`} />Refresh</button></div></div>
    {error && <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{error}</div>}
    <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">{cards.map(([label, value, Icon, color]) => <Card key={label} title={label} value={value} icon={Icon} color={color} />)}</div>
    {type === 'academic' && <><div className="grid gap-4 lg:grid-cols-2"><Panel title="Student Strength by Class"><ComparisonChart rows={data?.strengthByClass} /></Panel><Panel title="Gender Comparison"><ComparisonChart rows={data?.genderComparison} /></Panel></div><div className="grid gap-4 lg:grid-cols-2"><Panel title="Admissions Trend"><Chart rows={data?.admissions} /></Panel><Panel title="Religion / Category Statistics"><Table rows={[...(data?.religion || []), ...(data?.categories || [])]} /></Panel></div></>}
    {(type === 'attendance' || type === 'faculty') && <><div className="grid gap-4 lg:grid-cols-2"><Panel title="Date-wise Attendance"><Chart rows={(data?.dateWise || []).map((r) => ({ ...r, value: number(r.present) }))} /></Panel><Panel title="Class-wise Attendance"><Table rows={data?.classWise} /></Panel></div><div className="grid gap-4 lg:grid-cols-2"><Panel title="Monthly Attendance"><Table rows={data?.monthly} /></Panel><Panel title="Attendance Authentication"><Table rows={data?.authentication} /></Panel></div></>}
    {type === 'fees' && <><div className="grid gap-4 lg:grid-cols-2"><Panel title="Collection by Month"><Chart rows={data?.byMonth} bars /></Panel><Panel title="Collection by Payment Mode"><Chart rows={data?.byMode} bars /></Panel></div><Panel title="Defaulter Groups"><Table rows={data?.defaulters} /></Panel></>}
    {type === 'marks' && <div className="grid gap-4 lg:grid-cols-2"><Panel title="Marks by Class"><Table rows={data?.byClass} /></Panel><Panel title="Marks Entry Status"><Table rows={data?.byStatus} /></Panel></div>}
    {type === 'library' && <><div className="grid gap-4 lg:grid-cols-2"><Panel title="Circulation"><Table rows={data?.circulation} /></Panel><Panel title="Fine by Month"><Chart rows={data?.fineByMonth} bars /></Panel></div><Panel title="Recent Issues"><Table rows={data?.recentIssues} /></Panel></>}
    {type === 'management' && <><div className="grid gap-4 lg:grid-cols-2"><Panel title="Fee Summary"><Table rows={data?.fees ? [data.fees] : []} /></Panel><Panel title="Holidays"><Table rows={data?.holidays} /></Panel></div><Panel title="Circulars"><Table rows={data?.circulars} /></Panel></>}
  </div>
}
