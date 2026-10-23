import { useEffect, useMemo, useState } from 'react'
import { apiFetch } from '../../../../lib/api'
import { AlertCircle, Eye, FileSpreadsheet, Loader2, Mail, Printer, RefreshCw } from 'lucide-react'

const input = 'w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-800 outline-none focus:border-indigo-400 focus:ring-2 focus:ring-indigo-100 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 dark:focus:border-indigo-500 dark:focus:ring-indigo-500/20'
const button = 'inline-flex items-center justify-center gap-2 rounded-lg bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-60'
const secondary = 'inline-flex items-center justify-center gap-2 rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200 dark:hover:bg-slate-800'

function today() { return new Date().toISOString().slice(0, 10) }
function text(value) { return value == null ? '' : String(value) }
function money(value) { return Number(value || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }
function downloadCsv(name, rows) {
  if (!rows.length) return
  const keys = Object.keys(rows[0])
  const csv = [keys.join(','), ...rows.map(row => keys.map(key => `"${text(row[key]).replaceAll('"', '""')}"`).join(','))].join('\n')
  const url = URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' }))
  const a = document.createElement('a'); a.href = url; a.download = `${name}.csv`; a.click(); URL.revokeObjectURL(url)
}

function Field({ label, required, children, error }) {
  return <label className="block min-w-0"><span className="mb-1 block text-xs font-semibold text-slate-500 dark:text-slate-400">{label}{required && <b className="ml-1 text-rose-500">*</b>}</span>{children}{error && <span className="mt-1 flex items-center gap-1 text-xs text-rose-500"><AlertCircle className="h-3 w-3" />{error}</span>}</label>
}

function Select({ value, onChange, children, disabled }) {
  return <select className={input} value={value} onChange={onChange} disabled={disabled}>{children}</select>
}

function NoticeTable({ rows }) {
  if (!rows.length) return <div className="rounded-lg border border-dashed border-slate-300 p-10 text-center text-sm text-slate-500 dark:border-slate-700 dark:text-slate-400">No students found for the selected filters.</div>
  return <div className="overflow-x-auto rounded-lg border border-slate-200 dark:border-slate-700"><table className="min-w-[1050px] w-full text-left text-xs"><thead className="bg-slate-50 text-slate-600 dark:bg-slate-800 dark:text-slate-300"><tr>{['S.No.', 'Admission No.', 'Student Name', 'Father Name', 'Class', 'Installment', 'Charges', 'Concession', 'Received', 'Late Fee', 'Balance'].map(h => <th key={h} className="whitespace-nowrap px-3 py-2 font-semibold">{h}</th>)}</tr></thead><tbody className="divide-y divide-slate-100 dark:divide-slate-800">{rows.map((row, index) => <tr key={`${row.admNo}-${index}`} className="text-slate-700 hover:bg-slate-50 dark:text-slate-200 dark:hover:bg-slate-800/60"><td className="px-3 py-2">{index + 1}</td><td className="px-3 py-2 font-medium">{text(row.admNo)}</td><td className="px-3 py-2">{text(row.studentName)}</td><td className="px-3 py-2">{text(row.fatherName)}</td><td className="px-3 py-2">{text(row.className)}</td><td className="px-3 py-2">{text(row.installmentNo)}{row.installmentName ? ` (${row.installmentName})` : ''}</td><td className="px-3 py-2 text-right">{money(row.charges)}</td><td className="px-3 py-2 text-right">{money(row.concession)}</td><td className="px-3 py-2 text-right">{money(row.receiveAmount)}</td><td className="px-3 py-2 text-right">{money(row.lateFee)}</td><td className="px-3 py-2 text-right font-bold text-rose-600 dark:text-rose-400">{money(Number(row.balance || 0) + Number(row.lateFee || 0))}</td></tr>)}</tbody></table></div>
}

export default function FeeNotice() {
  const [boot, setBoot] = useState(null)
  const [installments, setInstallments] = useState([])
  const [students, setStudents] = useState([])
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [working, setWorking] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [emailSubject, setEmailSubject] = useState('')
  const [form, setForm] = useState({ feeType: '0', session: '', classId: '0', installment: '0', admissionNo: '', date: today(), dueAmount: '0', dueDate: '', orderBy: 'balance asc', lateFee: true })

  useEffect(() => {
    apiFetch('/fees/legacy-reports/defaulter/bootstrap').then(data => {
      setBoot(data); setForm(prev => ({ ...prev, session: prev.session || data.session || '' }))
    }).catch(e => setError(e.message || 'Fee notice filters could not be loaded')).finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    if (!form.session) return
    setInstallments([]); setForm(prev => ({ ...prev, installment: '0' }))
    apiFetch(`/fees/legacy-reports/defaulter/installments?session=${encodeURIComponent(form.session)}&feeTypeId=${form.feeType}`).then(setInstallments).catch(e => setError(e.message || 'Installments could not be loaded'))
  }, [form.session, form.feeType])

  useEffect(() => {
    if (!form.session || form.installment === '0') { setStudents([]); return }
    const qs = new URLSearchParams({ session: form.session, feeType: form.feeType, installment: form.installment, classId: form.classId, admissionNo: '', minimumBalance: form.dueAmount || '0', orderBy: form.orderBy, includeLateFee: form.lateFee ? '1' : '0' })
    apiFetch(`/communication/fee-notice?${qs}`).then(setStudents).catch(e => setError(e.message || 'Students could not be loaded'))
  }, [form.session, form.feeType, form.installment, form.classId, form.dueAmount, form.orderBy, form.lateFee])

  const classes = boot?.classes || []
  const sessionOptions = boot?.sessions || []
  const selectedStudent = useMemo(() => students.find(row => text(row.admNo) === form.admissionNo), [students, form.admissionNo])
  const update = (key, value) => { setForm(prev => ({ ...prev, [key]: value })); setError(''); setMessage('') }

  async function showReport() {
    if (!form.session) return setError('Please select Session')
    if (form.installment === '0') return setError('Please select Installments')
    setWorking(true); setError(''); setMessage('')
    try {
      const qs = new URLSearchParams({ session: form.session, feeType: form.feeType, installment: form.installment, classId: form.classId, admissionNo: form.admissionNo, minimumBalance: form.dueAmount || '0', orderBy: form.orderBy, includeLateFee: form.lateFee ? '1' : '0' })
      const data = await apiFetch(`/communication/fee-notice?${qs}`); setRows(data); setMessage(`${data.length} student(s) found.`)
    } catch (e) { setError(e.message || 'Fee notice could not be generated') } finally { setWorking(false) }
  }

  async function sendEmail() {
    if (!emailSubject.trim()) return setError('Please enter Email Subject')
    if (!rows.length) return setError('Show the report before sending email')
    setWorking(true); setError(''); setMessage('')
    try {
      const result = await apiFetch('/communication/fee-notice/email', { method: 'POST', body: JSON.stringify({ session: form.session, installment: form.installment, subject: emailSubject, content: `Fee notice for ${form.session}`, admissionNos: rows.map(row => row.admNo) }) })
      setMessage(result.message || 'Email queued successfully')
    } catch (e) { setError(e.message || 'Email could not be queued') } finally { setWorking(false) }
  }

  function reset() { setRows([]); setStudents([]); setMessage(''); setError(''); setEmailSubject(''); setForm({ feeType: '0', session: boot?.session || '', classId: '0', installment: '0', admissionNo: '', date: today(), dueAmount: '0', dueDate: '', orderBy: 'balance asc', lateFee: true }) }

  if (loading) return <div className="flex min-h-48 items-center justify-center text-sm text-slate-500"><Loader2 className="mr-2 h-5 w-5 animate-spin" />Loading Fee Notice...</div>
  if (!boot) return <div className="rounded-lg border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">{error || 'Fee Notice could not be loaded'}</div>

  return <div className="space-y-4 p-1 text-slate-800 dark:text-slate-100">
    <div className="flex flex-wrap items-center justify-between gap-3"><div><h1 className="text-lg font-bold">Fee Notice</h1><p className="text-xs text-slate-500 dark:text-slate-400">Generate fee notice using the selected session, class and installment.</p></div><div className="flex flex-wrap gap-2"><button className={secondary} onClick={reset}><RefreshCw className="h-4 w-4" />Reset</button>{rows.length > 0 && <><button className={secondary} onClick={() => downloadCsv('fee-notice', rows)}><FileSpreadsheet className="h-4 w-4" />Export</button><button className={secondary} onClick={() => window.print()}><Printer className="h-4 w-4" />Print</button></>}</div></div>
    {error && <div className="rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700 dark:border-rose-900/60 dark:bg-rose-950/30 dark:text-rose-300">{error}</div>}
    {message && <div className="rounded-lg border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm text-emerald-700 dark:border-emerald-900/60 dark:bg-emerald-950/30 dark:text-emerald-300">{message}</div>}
    <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm dark:border-slate-700 dark:bg-slate-950"><div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
      <Field label="Select Fee Type"><div className="flex flex-wrap gap-3 pt-2 text-sm">{(boot.feeTypes || [{ value: 0, label: 'Regular' }, { value: 6, label: 'Transport' }, { value: 7, label: 'Hostel' }]).filter(option => option.value !== '-1' && option.value !== -1 && (option.value === 0 || option.value === '0' || option.value === 6 || option.value === '6' || option.value === 7 || option.value === '7')).map(option => <label key={option.value} className="flex items-center gap-1.5"><input type="radio" name="fee-type" checked={String(form.feeType) === String(option.value)} onChange={() => { update('feeType', String(option.value)); update('installment', '0') }} />{option.label}</label>)}</div></Field>
      <Field label="Session" required><Select value={form.session} onChange={e => update('session', e.target.value)}>{sessionOptions.map(option => <option key={option.value || option.session} value={option.value || option.session}>{option.label || option.session}</option>)}</Select></Field>
      <Field label="Select Class"><Select value={form.classId} onChange={e => update('classId', e.target.value)}><option value="0">--All Classes--</option>{classes.map(option => <option key={option.value || option.id} value={option.value || option.id}>{option.label || `${option.className || ''} (${option.section || ''})`}</option>)}</Select></Field>
      <Field label="Select Installments" required><Select value={form.installment} onChange={e => update('installment', e.target.value)}><option value="0">--Select Installment--</option>{installments.map(option => <option key={option.value} value={option.value}>{option.label}</option>)}</Select></Field>
      <Field label="Select Student"><Select value={form.admissionNo} onChange={e => update('admissionNo', e.target.value)}><option value="">--Select Student--</option>{students.map((row, index) => <option key={`${row.admNo}-${index}`} value={row.admNo}>{row.studentName} [{row.admNo}]</option>)}</Select></Field>
      <Field label="Search Student"><input className={input} value={form.admissionNo} onChange={e => update('admissionNo', e.target.value)} placeholder="Admission No." /></Field>
      <Field label="Date"><input className={input} type="date" value={form.date} onChange={e => update('date', e.target.value)} /></Field>
      <Field label="Due Amount >="><input className={input} type="number" min="0" value={form.dueAmount} onChange={e => update('dueAmount', e.target.value)} /></Field>
      <Field label="Due Date"><input className={input} type="date" value={form.dueDate} onChange={e => update('dueDate', e.target.value)} /></Field>
      <Field label="Order By"><Select value={form.orderBy} onChange={e => update('orderBy', e.target.value)}><option value="balance asc">Balance Asc</option><option value="balance desc">Balance Desc</option><option value="name">Student Name</option></Select></Field>
      <Field label="Enter Email Subject"><input className={input} value={emailSubject} onChange={e => setEmailSubject(e.target.value)} placeholder="Email subject" /></Field>
      <Field label="Late Fees"><label className="flex h-10 items-center gap-2 text-sm"><input type="checkbox" checked={form.lateFee} onChange={e => update('lateFee', e.target.checked)} />Consider Late Fee</label></Field>
    </div><div className="mt-4 flex flex-wrap gap-2"><button className={button} onClick={showReport} disabled={working}>{working ? <Loader2 className="h-4 w-4 animate-spin" /> : <Eye className="h-4 w-4" />}Show</button><button className={secondary} onClick={sendEmail} disabled={working || !rows.length}><Mail className="h-4 w-4" />Email</button></div></div>
    {selectedStudent && !rows.length && <div className="rounded-lg border border-indigo-200 bg-indigo-50 p-3 text-sm text-indigo-800 dark:border-indigo-900/60 dark:bg-indigo-950/30 dark:text-indigo-200">Selected: {selectedStudent.studentName} [{selectedStudent.admNo}]</div>}
    {rows.length > 0 && <div className="rounded-xl border border-slate-200 bg-white p-3 shadow-sm dark:border-slate-700 dark:bg-slate-950"><div className="mb-3 flex items-center justify-between"><h2 className="font-semibold">Fee Notice Report</h2><span className="text-xs text-slate-500 dark:text-slate-400">Session: {form.session}</span></div><NoticeTable rows={rows} /></div>}
  </div>
}
