import { useEffect, useMemo, useState } from 'react'
import { Download, RefreshCw, Search } from 'lucide-react'
import { apiFetch } from '../../../../lib/api'
import { downloadExcel } from '../../../../lib/exportExcel'

const box = 'rounded-xl border border-slate-200 bg-white shadow-sm dark:border-slate-700 dark:bg-slate-900'
const control = 'w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-700 outline-none focus:border-blue-500 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-100'
const months = ['January','February','March','April','May','June','July','August','September','October','November','December']

function ReportTable({ report, search, setSearch }) {
  const columns = report?.columns || []
  const rows = report?.rows || []
  const filtered = useMemo(() => {
    const query = search.trim().toLowerCase()
    if (!query) return rows
    return rows.filter(row => columns.some(column => String(row?.[column.key] ?? '').toLowerCase().includes(query)))
  }, [columns, rows, search])
  return <div className={`${box} overflow-hidden`}>
    <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-200 px-4 py-3 dark:border-slate-700">
      <div><h2 className="text-sm font-bold text-slate-800 dark:text-slate-100">RFID Attendance Report</h2><p className="mt-0.5 text-xs text-slate-500">Showing {filtered.length} of {rows.length} records</p></div>
      <div className="flex flex-wrap items-center gap-2">
        <div className="relative"><Search className="pointer-events-none absolute left-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" /><input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search report..." className={`${control} w-56 pl-8`} /></div>
        <button type="button" disabled={!filtered.length} onClick={() => downloadExcel('RFID Attendance Report.xls', filtered, columns)} className="inline-flex items-center gap-2 rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-50 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-200"><Download className="h-4 w-4" /> Excel</button>
      </div>
    </div>
    <div className="overflow-x-auto"><table className="min-w-full text-sm"><thead className="bg-slate-100 text-left text-xs uppercase tracking-wide text-slate-600 dark:bg-slate-800 dark:text-slate-300"><tr>{columns.map(column => <th key={column.key} className="whitespace-nowrap px-3 py-3">{column.label}</th>)}</tr></thead><tbody>{filtered.map((row, index) => <tr key={`${row.registrationNo || row.sno || 'row'}-${index}`} className="border-t border-slate-100 dark:border-slate-700">{columns.map(column => <td key={column.key} className="whitespace-nowrap px-3 py-2.5">{row[column.key] ?? ''}</td>)}</tr>)}{!filtered.length && <tr><td colSpan={Math.max(columns.length, 1)} className="px-4 py-10 text-center text-slate-500">{rows.length ? 'No matching record found.' : 'No Record Found.'}</td></tr>}</tbody></table></div>
  </div>
}

export default function RFIDAttendanceDynamic() {
  const [boot, setBoot] = useState(null)
  const [error, setError] = useState('')
  const [filters, setFilters] = useState({ session: '', sectionId: '', year: String(new Date().getFullYear()), month: String(new Date().getMonth() + 1), date: '' })
  const [report, setReport] = useState(null)
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(false)
  const years = useMemo(() => { const now = new Date().getFullYear(); return Array.from({ length: Math.max(3, now - 2023 + 1) }, (_, index) => String(now - index)) }, [])

  useEffect(() => { apiFetch('/attendance/bootstrap').then(value => { setBoot(value); setFilters(current => ({ ...current, session: value.session || '' })) }).catch(e => setError(e.message || 'Attendance setup could not be loaded.')) }, [])
  const set = (key, value) => { setFilters(current => ({ ...current, [key]: value })); setReport(null) }
  const show = async () => {
    setError(''); setSearch('')
    if (!filters.session || !filters.sectionId) return setError('Please select session and class.')
    if (!filters.date && (!filters.year || !filters.month)) return setError('Please select year and month, or enter a date.')
    setLoading(true)
    try {
      const query = new URLSearchParams({ session: filters.session, sectionId: filters.sectionId, year: filters.year || '0', month: filters.month || '0', date: filters.date })
      setReport(await apiFetch(`/attendance/rfid/matrix?${query}`))
    } catch (e) { setError(e.message || 'RFID attendance report could not be loaded.') } finally { setLoading(false) }
  }
  const reset = () => { setFilters({ session: boot?.session || '', sectionId: '', year: String(new Date().getFullYear()), month: String(new Date().getMonth() + 1), date: '' }); setReport(null); setSearch(''); setError('') }
  return <div className="min-h-full space-y-4 p-4 text-slate-800 dark:text-slate-100 sm:p-6">
    <div><div className="text-xs text-slate-500">Home / Attendance / Tap card</div><h1 className="mt-1 text-xl font-bold">RFID Attendance Report</h1></div>
    {error && <div className="rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{error}</div>}
    {!boot ? <div className={`${box} p-8 text-center text-sm text-slate-500`}>Loading attendance setup...</div> : <>
      <div className={`${box} space-y-4 p-4`}><div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
        <label className="text-sm font-medium">Session<select className={`${control} mt-1`} value={filters.session} onChange={e => set('session', e.target.value)}>{(boot.sessions || []).map(row => <option key={row.session || row.value} value={row.session || row.value}>{row.session || row.label}</option>)}</select></label>
        <label className="text-sm font-medium">Class<select className={`${control} mt-1`} value={filters.sectionId} onChange={e => set('sectionId', e.target.value)}><option value="">&lt;-select-&gt;</option>{(boot.classes || []).map(row => <option key={row.sectionId} value={row.sectionId}>{row.label}</option>)}</select></label>
        <label className="text-sm font-medium">Year<select className={`${control} mt-1`} value={filters.year} onChange={e => set('year', e.target.value)}>{years.map(year => <option key={year} value={year}>{year}</option>)}</select></label>
        <label className="text-sm font-medium">Month<select className={`${control} mt-1`} value={filters.month} onChange={e => set('month', e.target.value)}>{months.map((month, index) => <option key={month} value={index + 1}>{month}</option>)}</select></label>
        <label className="text-sm font-medium">Date<input type="date" className={`${control} mt-1`} value={filters.date} onChange={e => set('date', e.target.value)} /></label>
      </div><div className="flex gap-2"><button type="button" onClick={show} disabled={loading} className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-50">{loading ? 'Loading...' : 'Show'}</button><button type="button" onClick={reset} className="inline-flex items-center gap-2 rounded-lg bg-slate-100 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-200"><RefreshCw className="h-4 w-4" /> Reset</button></div></div>
      {report && <ReportTable report={report} search={search} setSearch={setSearch} />}
    </>}
  </div>
}
