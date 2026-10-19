import { useCallback, useEffect, useMemo, useState } from 'react'
import { AlertCircle, Download, Loader2, RefreshCw } from 'lucide-react'
import { apiFetch } from '../../../../lib/api'
import { downloadExcel } from '../../../../lib/exportExcel'

const box = 'rounded-xl border border-slate-200 bg-white shadow-sm dark:border-slate-700 dark:bg-slate-900'
const control = 'w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-700 outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-200 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-100'

function valueOf(row) { return String(row?.value ?? row?.session ?? row?.id ?? '') }
function labelOf(row) { return String(row?.label ?? row?.name ?? row?.className ?? row?.class_name ?? row?.value ?? '') }

function Field({ label, children, required = false }) {
  return <label className="block space-y-1.5 text-sm font-medium text-slate-700 dark:text-slate-200">
    <span>{label}{required && <span className="ml-1 text-rose-500">*</span>}</span>{children}
  </label>
}

function Button({ children, onClick, disabled = false, kind = 'primary' }) {
  const styles = kind === 'secondary'
    ? 'border border-slate-300 bg-white text-slate-700 hover:bg-slate-50 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-200'
    : 'bg-emerald-600 text-white hover:bg-emerald-700'
  return <button type="button" onClick={onClick} disabled={disabled} className={`inline-flex items-center justify-center gap-2 rounded-lg px-4 py-2 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-50 ${styles}`}>
    {children}
  </button>
}

function Notice({ message, error = false }) {
  if (!message) return null
  return <div className={`flex items-start gap-2 rounded-lg border px-3 py-2 text-sm ${error ? 'border-rose-200 bg-rose-50 text-rose-700' : 'border-emerald-200 bg-emerald-50 text-emerald-700'}`}>
    <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />{message}
  </div>
}

function ReportTable({ report, title, filename }) {
  const columns = report?.columns || []
  const rows = report?.rows || []
  return <div className={`${box} overflow-hidden`}>
    <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-200 px-4 py-3 dark:border-slate-700">
      <div>
        <h2 className="text-sm font-bold text-slate-800 dark:text-slate-100">{title}</h2>
        <p className="mt-0.5 text-xs text-slate-500">{rows.length} record{rows.length === 1 ? '' : 's'}</p>
      </div>
      <Button kind="secondary" disabled={!rows.length} onClick={() => downloadExcel(filename, rows, columns)}>
        <Download className="h-4 w-4" /> Export Excel
      </Button>
    </div>
    <div className="overflow-x-auto">
      <table className="min-w-full text-sm">
        <thead className="bg-slate-100 text-left text-xs uppercase tracking-wide text-slate-600 dark:bg-slate-800 dark:text-slate-300">
          <tr>{columns.map(column => <th key={column.key} className="whitespace-nowrap px-3 py-3">{column.label}</th>)}</tr>
        </thead>
        <tbody>
          {rows.map((row, index) => <tr key={`${row.studentId || row.registrationNo || row.sectionId || row.className || 'row'}-${index}`} className="border-t border-slate-100 dark:border-slate-700">
            {columns.map(column => <td key={column.key} className={`whitespace-nowrap px-3 py-2.5 ${row.className === 'Total' ? 'font-bold' : ''}`}>{row[column.key] ?? ''}</td>)}
          </tr>)}
          {!rows.length && <tr><td colSpan={Math.max(columns.length, 1)} className="px-4 py-10 text-center text-slate-500">No Record Found.</td></tr>}
        </tbody>
      </table>
    </div>
  </div>
}

function useReportBootstrap(session) {
  const [meta, setMeta] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const load = useCallback(async (selected = session) => {
    setLoading(true); setError('')
    try { setMeta(await apiFetch(`/attendance/reports/bootstrap?${new URLSearchParams(selected ? { session: selected } : {})}`)) }
    catch (e) { setError(e.message || 'Attendance report setup could not be loaded.') }
    finally { setLoading(false) }
  }, [session])
  useEffect(() => { load(session) }, [])
  return { meta, loading, error, load }
}

function SessionSelect({ meta, value, onChange }) {
  return <Field label="Select Session" required><select className={control} value={value} onChange={onChange}>
    <option value="">&lt;-select-&gt;</option>
    {(meta?.sessions || []).map(row => <option key={valueOf(row)} value={valueOf(row)}>{labelOf(row)}</option>)}
  </select></Field>
}

function PercentageReport() {
  const [session, setSession] = useState('')
  const [sectionId, setSectionId] = useState('')
  const [report, setReport] = useState(null)
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const { meta, error, load } = useReportBootstrap(session)

  useEffect(() => { if (meta?.session && !session) { setSession(meta.session); setSectionId('') } }, [meta, session])
  const classes = meta?.percentageClasses || []
  const changeSession = async event => { const next = event.target.value; setSession(next); setSectionId(''); setReport(null); await load(next) }
  const show = async () => {
    setMessage(''); setReport(null)
    if (!session || !sectionId) return setMessage('Please select session and class.')
    setLoading(true)
    try { setReport(await apiFetch(`/attendance/reports/percentage?${new URLSearchParams({ session, sectionId })}`)) }
    catch (e) { setMessage(e.message || 'Report could not be loaded.') }
    finally { setLoading(false) }
  }
  return <Page title="100% Attendance Report" error={error} loading={loading && !meta}>
    <FilterCard>
      <div className="grid gap-4 md:grid-cols-3">
        <SessionSelect meta={meta} value={session} onChange={changeSession} />
        <Field label="Select Class" required><select className={control} value={sectionId} onChange={e => { setSectionId(e.target.value); setReport(null) }}>
          <option value="">&lt;-select-&gt;</option>{classes.map(row => <option key={row.sectionId} value={row.sectionId}>{row.label}</option>)}
        </select></Field>
        <div className="flex items-end"><Button onClick={show} disabled={loading}>{loading ? <Loader2 className="h-4 w-4 animate-spin" /> : null}{loading ? 'Loading...' : 'Show'}</Button></div>
      </div>
      <Notice message={message} error />
    </FilterCard>
    {report && <ReportTable report={report} title="100% Attendance Report" filename="Attendance Report (100%).xls" />}
  </Page>
}

function StatusReport() {
  const [session, setSession] = useState('')
  const [classId, setClassId] = useState('')
  const [date, setDate] = useState('')
  const [report, setReport] = useState(null)
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const { meta, error, load } = useReportBootstrap(session)
  const dayWise = Number(meta?.attendanceType) === 2

  useEffect(() => { if (meta?.session && !session) setSession(meta.session) }, [meta, session])
  const changeSession = async event => { const next = event.target.value; setSession(next); setClassId(''); setReport(null); await load(next) }
  const show = async () => {
    setMessage(''); setReport(null)
    if (!session || !classId || (dayWise && !date)) return setMessage(dayWise ? 'Please select session, class and attendance date.' : 'Please select session and class.')
    setLoading(true)
    try { setReport(await apiFetch(`/attendance/reports/status?${new URLSearchParams({ session, classId, date: dayWise ? date : '' })}`)) }
    catch (e) { setMessage(e.message || 'Report could not be loaded.') }
    finally { setLoading(false) }
  }
  return <Page title="Attendance Status Report" error={error} loading={loading && !meta}>
    <FilterCard>
      <div className="grid gap-4 md:grid-cols-4">
        <SessionSelect meta={meta} value={session} onChange={changeSession} />
        <Field label="Select Class" required><select className={control} value={classId} onChange={e => { setClassId(e.target.value); setReport(null) }}>
          <option value="">&lt;-select-&gt;</option>{(meta?.statusClasses || []).map(row => <option key={row.id} value={row.id}>{row.label}</option>)}
        </select></Field>
        {dayWise && <Field label="Attendance Date" required><input type="date" className={control} value={date} onChange={e => { setDate(e.target.value); setReport(null) }} /></Field>}
        <div className="flex items-end"><Button onClick={show} disabled={loading}>{loading ? <Loader2 className="h-4 w-4 animate-spin" /> : null}{loading ? 'Loading...' : 'show'}</Button></div>
      </div>
      <Notice message={message} error />
    </FilterCard>
    {report && <ReportTable report={report} title="Attendance Status Report" filename="Attendacne_Status_Report.xls" />}
  </Page>
}

function TotalReport() {
  const [session, setSession] = useState('')
  const [sectionId, setSectionId] = useState('')
  const [report, setReport] = useState(null)
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)
  const { meta, error, load } = useReportBootstrap(session)
  const classes = meta?.totalClasses || []

  useEffect(() => { if (meta?.session && !session) setSession(meta.session) }, [meta, session])
  const changeSession = async event => { const next = event.target.value; setSession(next); setSectionId(''); setReport(null); await load(next) }
  const show = async () => {
    setMessage(''); setReport(null)
    if (!session || !sectionId) return setMessage('Please select session and class.')
    setLoading(true)
    try { setReport(await apiFetch(`/attendance/reports/total?${new URLSearchParams({ session, sectionId })}`)) }
    catch (e) { setMessage(e.message || 'Report could not be loaded.') }
    finally { setLoading(false) }
  }
  const caption = report?.className ? `Attendance Register - ${report.className}` : 'Total Attendance Report'
  return <Page title="Total Attendance" error={error} loading={loading && !meta}>
    <FilterCard>
      <div className="grid gap-4 md:grid-cols-4">
        <SessionSelect meta={meta} value={session} onChange={changeSession} />
        <Field label="Select Class" required><select className={control} value={sectionId} onChange={e => { setSectionId(e.target.value); setReport(null) }}>
          <option value="">&lt;-select-&gt;</option>{classes.map(row => <option key={row.sectionId} value={row.sectionId}>{row.label}</option>)}
        </select></Field>
        <div className="flex items-end"><Button onClick={show} disabled={loading}>{loading ? <Loader2 className="h-4 w-4 animate-spin" /> : null}{loading ? 'Loading...' : 'Show'}</Button></div>
      </div>
      <Notice message={message} error />
    </FilterCard>
    {report && <div className="space-y-3">
      <div className="flex flex-wrap items-center justify-between gap-3 rounded-lg border border-emerald-100 bg-emerald-50 px-4 py-3 text-sm text-emerald-900">
        <div><strong>{report.schoolName}</strong><span className="mx-2">·</span>{caption}<span className="mx-2">·</span>Session: {report.session}</div>
      </div>
      <ReportTable report={report} title={caption} filename={`Total Attendance Report(${report.className || 'Class'}).xls`} />
    </div>}
  </Page>
}

function FilterCard({ children }) { return <div className={`${box} space-y-4 p-4`}>{children}</div> }
function Page({ title, children, error, loading }) {
  return <div className="min-h-full space-y-4 p-4 text-slate-800 dark:text-slate-100 sm:p-6">
    <div><div className="text-xs text-slate-500">Home / Attendance / Report</div><h1 className="mt-1 text-xl font-bold">{title}</h1></div>
    <Notice message={error} error />
    {loading ? <div className={`${box} flex items-center gap-2 p-8 text-sm text-slate-500`}><RefreshCw className="h-4 w-4 animate-spin" /> Loading attendance report setup...</div> : children}
  </div>
}

export { PercentageReport, StatusReport, TotalReport }
