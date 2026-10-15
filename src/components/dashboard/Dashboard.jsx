import { useEffect, useMemo, useState } from 'react'
import { Users, UserCheck, DollarSign, CheckSquare, Loader2, RefreshCw } from 'lucide-react'
import { apiFetch } from '../../lib/api'
import StatCard from '../ui/StatCard'
import { Card, CardHeader, CardTitle, CardBody } from '../ui/Card'
import SchoolBrand from '../layout/SchoolBrand'

const QUICK_LINKS = [
  { label: 'Marks Entry', mod: 'exam-master', grp: 'marks-entry', page: 'define-marks-new' },
  { label: 'Fee Collection', mod: 'fee', grp: 'fee-collection', page: 'collect-fee' },
  { label: 'Attendance', mod: 'reports', grp: 'attendance-reports', page: 'daily-attendance' },
  { label: 'Send SMS', mod: 'communication', grp: 'messaging', page: 'send-sms' },
  { label: 'Report Cards', mod: 'reports', grp: 'academic-reports', page: 'report-card-prim' },
  { label: 'Certificates', mod: 'certificates', grp: 'gen-certs', page: 'bonafide' },
]

const number = (value) => {
  if (value && typeof value === 'object') return number(value.value ?? value.total ?? 0)
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : 0
}

const firstNumber = (value) => Array.isArray(value) ? number(value[0]) : number(value)
const formatNumber = (value) => number(value).toLocaleString('en-IN')
const formatMoney = (value) => `₹${number(value).toLocaleString('en-IN', { maximumFractionDigits: 0 })}`

function dashboardType(user) {
  if (user?.typeStatus === 'F' && user?.dashboard === '27') return 'management'
  return 'academic'
}

export default function Dashboard({ onPageClick, user, menuMeta }) {
  const [data, setData] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const type = useMemo(() => dashboardType(user), [user])

  const load = async () => {
    setLoading(true)
    setError('')
    const types = [...new Set([type, 'fees', 'attendance', 'management'])]
    const responses = await Promise.allSettled(types.map((item) => apiFetch(`/dashboard/${item}`)))
    const next = {}
    responses.forEach((response, index) => {
      if (response.status === 'fulfilled') next[types[index]] = response.value
    })
    if (!Object.keys(next).length) setError('Dashboard data could not be loaded.')
    setData(next)
    setLoading(false)
  }

  useEffect(() => { load() }, [type])

  const academic = data.academic || data.management || {}
  const management = data.management || {}
  const fee = data.fees || {}
  const attendance = data.attendance || {}
  const summary = academic.summary || {}
  const feeSummary = fee.summary || management.fees || {}
  const attendanceSummary = attendance.summary || {}
  const present = number(attendanceSummary.present)
  const absent = number(attendanceSummary.absent)
  const attendanceRate = present + absent ? `${((present / (present + absent)) * 100).toFixed(1)}%` : '—'
  const schoolName = user?.schoolName || menuMeta?.schoolName || 'School ERP'
  const session = academic.session || management.session || menuMeta?.session || '—'
  const activity = (academic.admissions || []).slice(-5).reverse()

  const stats = [
    { icon: Users, value: formatNumber(summary.total), label: 'Total Students', color: 'blue' },
    { icon: UserCheck, value: formatNumber(firstNumber(management.faculty)), label: 'Teaching Staff', color: 'green' },
    { icon: DollarSign, value: formatMoney(feeSummary.received), label: 'Fee Collected', color: 'orange' },
    { icon: CheckSquare, value: attendanceRate, label: 'Attendance', color: 'purple' },
  ]

  return (
    <div className="page-animate space-y-4">
      <div className="relative rounded-[14px] p-6 text-white overflow-hidden welcome-card bg-gradient-to-br from-blue-900 via-indigo-900 to-blue-700">
        <div className="absolute right-[-40px] top-[-40px] w-48 h-48 rounded-full bg-indigo-400/20" />
        <div className="relative z-10">
          <div className="flex items-center gap-3 mb-2">
            <SchoolBrand schoolName={schoolName} logoPath={user?.logoPath} markClassName="w-11 h-11" />
          </div>
          <h2 className="font-display text-[20px] font-bold mb-1">Welcome back, {user?.username || 'User'}!</h2>
          <p className="text-[13px] text-white/80">Manage your school using live records from the current session.</p>
          <p className="mt-3 text-[12px] text-white/60">Academic Session: {session}</p>
        </div>
      </div>

      {error && <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}
      <div className="flex items-center justify-end gap-2 text-xs text-slate-500">
        {loading && <><Loader2 size={14} className="animate-spin" /> Loading dashboard...</>}
        <button onClick={load} className="inline-flex items-center gap-1 rounded-md border border-slate-200 px-2 py-1 hover:bg-white" title="Refresh dashboard"><RefreshCw size={13} /> Refresh</button>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3.5">
        {stats.map((stat) => <StatCard key={stat.label} {...stat} />)}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <Card>
          <CardHeader><CardTitle>Quick Access</CardTitle></CardHeader>
          <CardBody>
            <div className="grid grid-cols-3 gap-2.5">
              {QUICK_LINKS.map((link) => <button key={link.label} onClick={() => onPageClick(link.mod, link.grp, link.page)} className="flex flex-col items-center gap-2 py-3.5 px-2 bg-slate-50 dark:bg-white/[0.03] hover:bg-blue-50 border border-slate-200 dark:border-[rgba(99,102,241,0.1)] rounded-[10px] transition-all">
                <div className="w-9 h-9 rounded-[9px] flex items-center justify-center bg-gradient-to-br from-blue-900 to-indigo-600"><span className="text-white text-[11px] font-bold">{link.label.charAt(0)}</span></div>
                <span className="text-[11.5px] font-medium text-slate-600 dark:text-slate-300 text-center leading-tight">{link.label}</span>
              </button>)}
            </div>
          </CardBody>
        </Card>

        <Card>
          <CardHeader><CardTitle>Recent Admissions</CardTitle><span className="text-[12px] text-slate-400">Live</span></CardHeader>
          <CardBody className="p-0">
            {activity.length ? <div className="divide-y divide-slate-100 dark:divide-[rgba(99,102,241,0.06)]">{activity.map((item, index) => <div key={`${item.label}-${index}`} className="flex items-start gap-3 px-4 py-3">
              <div className="w-2 h-2 rounded-full mt-[5px] flex-shrink-0 bg-blue-500" />
              <div><p className="text-[13px] text-slate-800 dark:text-slate-200">{formatNumber(item.value)} admission{number(item.value) === 1 ? '' : 's'} recorded</p><p className="text-[11px] text-slate-400 mt-0.5">{item.label}</p></div>
            </div>)}</div> : <p className="px-4 py-6 text-sm text-slate-400">No admission records for the selected period.</p>}
          </CardBody>
        </Card>
      </div>
    </div>
  )
}
