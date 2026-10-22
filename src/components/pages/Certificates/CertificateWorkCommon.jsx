import { useCallback, useEffect, useMemo, useState } from 'react'
import { apiFetch } from '../../../lib/api'

const today = () => new Date().toISOString().slice(0, 10)
const displayDate = value => {
  if (!value) return ''
  const s = String(value)
  if (/^\d{4}-\d{2}-\d{2}$/.test(s)) {
    const [y, m, d] = s.split('-')
    return `${d} ${['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'][Number(m) - 1]} ${y}`
  }
  return s
}
const schoolName = (school, fallback = 'School') => school?.schoolname || school?.School_name_Report || fallback
const schoolInitials = name => String(name || 'School').split(/\s+/).filter(Boolean).map(part => part[0]).join('').slice(0, 3).toUpperCase()

function Field({ label, children, className = '' }) {
  return <label className={`block space-y-1.5 ${className}`}><span className="text-xs font-semibold text-slate-600 dark:text-slate-300">{label}</span>{children}</label>
}
function Select({ value, onChange, children, disabled = false, multiple = false }) {
  return <select multiple={multiple} value={value} onChange={onChange} disabled={disabled} className="min-h-10 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-800 outline-none focus:border-indigo-400 focus:ring-2 focus:ring-indigo-100 disabled:cursor-not-allowed disabled:opacity-60 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 dark:focus:ring-indigo-900/40">{children}</select>
}
function Input({ value, onChange, type = 'text', disabled = false }) {
  return <input type={type} value={value ?? ''} onChange={onChange} disabled={disabled} className="min-h-10 w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-800 outline-none focus:border-indigo-400 focus:ring-2 focus:ring-indigo-100 disabled:opacity-60 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 dark:focus:ring-indigo-900/40" />
}
function Button({ children, onClick, disabled = false, variant = 'primary' }) {
  return <button type="button" onClick={onClick} disabled={disabled} className={`rounded-lg px-4 py-2.5 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-60 ${variant === 'secondary' ? 'border border-slate-200 bg-white text-slate-700 hover:bg-slate-50 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200 dark:hover:bg-slate-800' : 'bg-indigo-600 text-white shadow-sm hover:bg-indigo-700'}`}>{children}</button>
}
function ErrorText({ message }) { return message ? <p className="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-700 dark:bg-rose-950/30 dark:text-rose-300">{message}</p> : null }

function CertificatePreview({ type, data, school, onPrint }) {
  const rows = data?.rows || []
  const title = { bonafide: 'Bonafide Certificate', character: 'Character Certificate', income: 'Income Certificate', fee: 'Fee Certificate', tc: 'Transfer Certificate' }[type]
  const values = type === 'bonafide' ? [
    ['Student', data?.name], ['Father Name', data?.father_name], ['Mother Name', data?.mother_name], ['Class', data?.class], ['Date of Birth', data?.DOB], ['Session', data?.session], ['Address', data?.R_Address], ['Issue Date', data?.issue_date],
  ] : type === 'character' ? [
    ['Student', data?.name], ['Father Name', data?.father_name], ['Mother Name', data?.mother_name], ['Class', data?.class], ['Admission Year', data?.adm_year], ['Pass Year', data?.pass_year], ['Issue Date', data?.issue_date], ['Address', data?.address || data?.R_Address],
  ] : type === 'income' ? [
    ['Student', data?.name], ['Father Name', data?.Father_name], ['Relation', data?.SoDo], ['Class', data?.class], ['Session', data?.session], ['Tuition Fee', data?.Tution_Fee], ['Principal', data?.principle], ['Affiliation No.', data?.affiliation_no],
  ] : []
  return <div id="certificate-print-area" className="mx-auto max-w-4xl rounded-xl border border-slate-300 bg-white p-5 text-slate-900 shadow-sm dark:border-slate-500 dark:bg-white dark:text-slate-900 print:max-w-none print:border-0 print:shadow-none">
    <div className="border-b-2 border-slate-800 pb-3 text-center">
      <div className="flex items-center justify-center gap-3">
        {school?.logo_path || school?.School_logo ? <img src={school.logo_path || school.School_logo} alt="" className="h-14 w-14 object-contain" onError={event => { event.currentTarget.style.display = 'none' }} /> : <div className="flex h-14 w-14 items-center justify-center rounded-full border-2 border-slate-700 text-sm font-extrabold">{schoolInitials(schoolName(school))}</div>}
        <div className="text-lg font-extrabold uppercase">{schoolName(school)}</div>
      </div>
      <div className="text-xs">{school?.address || school?.tc_add1 || ''}</div>
      <div className="mt-3 text-base font-bold uppercase underline">{title}</div>
    </div>
    {type === 'fee' ? <>
      <div className="grid grid-cols-2 gap-2 py-4 text-sm md:grid-cols-4"><b>Name:</b><span>{rows[0]?.name}</span><b>Registration No:</b><span>{rows[0]?.registration_no}</span><b>Class:</b><span>{rows[0]?.class_name} ({rows[0]?.section})</span><b>Dated:</b><span>{rows[0]?.dated}</span></div>
      <table className="w-full border-collapse text-sm"><thead><tr>{['Fee Head','Amount','Period','Total'].map(h => <th key={h} className="border border-slate-400 bg-slate-100 p-2 text-left">{h}</th>)}</tr></thead><tbody>{rows.map((r, i) => <tr key={`${r.IndividualHeadName}-${i}`}><td className="border border-slate-300 p-2">{r.IndividualHeadName}</td><td className="border border-slate-300 p-2">{r.head_wise_amount}</td><td className="border border-slate-300 p-2">{r.fromdate1} - {r.todate1}</td><td className="border border-slate-300 p-2">{r.totalfee_charges}</td></tr>)}</tbody></table>
    </> : type === 'tc' ? <div className="grid gap-1 py-4 text-sm md:grid-cols-2">{[['TC No.','sno'],['Admission No.','admissionNo'],['Name','stu_name'],['Mother Name','mother_name'],['Father Name','father_name'],['Category','stu_category'],['DOB','dob'],['Admission Date','admission_date'],['Last Class','last_class'],['Last Exam Result','last_exam_result'],['Subjects','subjects'],['Qualified Status','qualified_status'],['Dues Paid','paid_fee_status'],['Fee Concession','fee_concession'],['Working Days','totalWorkingDays'],['Attendance','total_att'],['NCC','ncc_cadet_status'],['Activities','extra_activity'],['Conduct','general_conduct'],['Application Date','application_date'],['Issue Date','issue_date'],['Reason','reason_for_leaving'],['Removal Date','withdrawn_date'],['Remark','remark'],['PEN No.','PEN_No']].map(([label,key]) => <div key={key} className="grid grid-cols-[minmax(10rem,38%)_1fr] border-b border-slate-200 py-1"><b>{label}</b><span>{data?.[key] ?? ''}</span></div>)}</div> : <div className="space-y-2 py-5 text-sm leading-7">{values.map(([label,value]) => <div key={label} className="border-b border-dotted border-slate-400"><b>{label}: </b>{value || ''}</div>)}<p className="pt-4">This certificate is issued by <b>{schoolName(school)}</b> for official use.</p></div>}
    <div className="mt-8 flex justify-end"><Button onClick={onPrint} variant="secondary">Print</Button></div>
  </div>
}

export default function CertificateWorkCommon({ type }) {
  const isFee = type === 'fee'
  const isTc = type === 'tc'
  const [boot, setBoot] = useState(null)
  const [session, setSession] = useState('')
  const [secId, setSecId] = useState('')
  const [studentId, setStudentId] = useState('')
  const [students, setStudents] = useState([])
  const [studentQuery, setStudentQuery] = useState('')
  const [date, setDate] = useState(today())
  const [admissionYear, setAdmissionYear] = useState('')
  const [admissionClass, setAdmissionClass] = useState('')
  const [lastAttendance, setLastAttendance] = useState('')
  const [promotedClass, setPromotedClass] = useState('')
  const [tcFee, setTcFee] = useState('')
  const [availableFeeHeads, setAvailableFeeHeads] = useState([])
  const [selectedFeeHeads, setSelectedFeeHeads] = useState([])
  const [reportType, setReportType] = useState('Multiple')
  const [tcData, setTcData] = useState(null)
  const [result, setResult] = useState(null)
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState('')

  useEffect(() => { apiFetch('/certificates/bootstrap').then(d => { setBoot(d); setSession(d.session || d.sessions?.[0]?.value || ''); setAdmissionClass(d.admissionClasses?.[0]?.id ? String(d.admissionClasses[0].id) : ''); setPromotedClass(d.promotedClasses?.[0]?.id ? String(d.promotedClasses[0].id) : '') }).catch(e => setMessage(e.message)) }, [])
  useEffect(() => { if (!session || !secId) { setStudents([]); setStudentId(''); return }; const mode = isFee || type === 'income' || type === 'bonafide' ? 'registered' : 'withdrawn'; apiFetch(`/certificates/students?${new URLSearchParams({ session, secId, mode })}`).then(setStudents).catch(e => setMessage(e.message)) }, [session, secId, type, isFee])
  useEffect(() => { if (isFee && session) apiFetch(`/certificates/fee-heads?session=${encodeURIComponent(session)}`).then(setAvailableFeeHeads).catch(e => setMessage(e.message)) }, [isFee, session])
  const classes = boot?.classes || []
  const selectedStudent = useMemo(() => students.find(s => String(s.id) === String(studentId)), [students, studentId])
  const filteredStudents = useMemo(() => { const q = studentQuery.trim().toLowerCase(); return q ? students.filter(s => `${s.name} ${s.admissionNo} ${s.studentName}`.toLowerCase().includes(q)) : students }, [students, studentQuery])
  const reset = () => { setSecId(''); setStudentId(''); setStudents([]); setStudentQuery(''); setSelectedFeeHeads([]); setResult(null); setTcData(null); setMessage('') }
  const generate = useCallback(async () => {
    setMessage(''); if (!session || !secId || !studentId) return setMessage('Session, class and student are required.')
    if (type !== 'tc' && !date) return setMessage('Date is required.')
    if (type === 'character' && !admissionYear) return setMessage('Admission Year is required.')
    if (isFee && !selectedFeeHeads.length) return setMessage('Select at least one fee head.')
    setBusy(true)
    try {
      const body = { session, secId: Number(secId), studentId: Number(studentId), issueDate: displayDate(date), date: displayDate(date), admissionYear, feeHeads: selectedFeeHeads.join(','), reportType }
      if (type === 'bonafide') setResult({ type, data: { ...(await apiFetch('/certificates/bonafide', { method: 'POST', body: JSON.stringify(body) })), school: boot?.school } })
      else if (type === 'character') setResult({ type, data: { ...(await apiFetch('/certificates/character', { method: 'POST', body: JSON.stringify(body) })), school: boot?.school } })
      else if (type === 'income') setResult({ type, data: { ...(await apiFetch('/certificates/income', { method: 'POST', body: JSON.stringify(body) })), school: boot?.school } })
      else if (isFee) setResult({ type, data: { ...(await apiFetch('/certificates/fee', { method: 'POST', body: JSON.stringify(body) })), school: boot?.school } })
      else { const d = await apiFetch(`/certificates/tc/detail?${new URLSearchParams({ session, secId, studentId })}`); setTcData({ ...d, admission_class: admissionClass || d.admission_class, promoted_class: promotedClass || d.promoted_class, last_date_of_att: lastAttendance || d.last_date_of_att, tc_fee: tcFee || d.tc_fee }); setResult(null) }
    } catch (e) { setMessage(e.message || 'Certificate could not be generated.') } finally { setBusy(false) }
  }, [session, secId, studentId, date, admissionYear, selectedFeeHeads, reportType, type, isFee, boot, admissionClass, promotedClass, lastAttendance, tcFee])
  const setTc = (key, value) => setTcData(p => ({ ...p, [key]: value }))
  const saveTcAndPreview = async () => { if (!tcData) return; setBusy(true); setMessage(''); try { const saved = await apiFetch('/certificates/tc', { method: 'POST', body: JSON.stringify({ ...tcData, session, secId: Number(secId), studentId: Number(studentId), admission_class: admissionClass || tcData.admission_class, promoted_class: promotedClass || tcData.promoted_class, last_date_of_att: lastAttendance || tcData.last_date_of_att, tc_fee: tcFee || tcData.tc_fee }) }); setTcData(saved); setResult({ type: 'tc', data: { ...saved, school: boot?.school } }) } catch (e) { setMessage(e.message || 'TC could not be saved.') } finally { setBusy(false) } }
  const exportFee = () => { const rows = result?.data?.rows || []; if (!rows.length) return; const html = `<table><tr><th>Fee Head</th><th>Amount</th><th>From</th><th>To</th></tr>${rows.map(r => `<tr><td>${r.IndividualHeadName || ''}</td><td>${r.head_wise_amount || ''}</td><td>${r.fromdate || ''}</td><td>${r.todate || ''}</td></tr>`).join('')}</table>`; const a = document.createElement('a'); a.href = URL.createObjectURL(new Blob([html], { type: 'application/vnd.ms-excel' })); a.download = 'Fee Certificate.xls'; a.click(); URL.revokeObjectURL(a.href) }
  const title = { bonafide: 'Bonafide Certificate', character: 'Character Certificate', fee: 'Fee Certificate', income: 'Income Certificate', tc: 'Transfer Certificate' }[type]
  return <div className="space-y-4 pb-10">
    <div><h1 className="text-xl font-bold text-slate-800 dark:text-slate-100">{title}</h1><p className="mt-1 text-sm text-slate-500 dark:text-slate-400">Database-driven certificate workflow.</p></div>
    <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm dark:border-slate-700 dark:bg-slate-900">
      <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
        <Field label="Select Session"><Select value={session} onChange={e => { setSession(e.target.value); reset() }}><option value="">-- Select Session --</option>{boot?.sessions?.map(s => <option key={s.value} value={s.value}>{s.label}</option>)}</Select></Field>
        <Field label="Select Class"><Select value={secId} onChange={e => { setSecId(e.target.value); setStudentId(''); setTcData(null); setResult(null) }} disabled={!session}><option value="">-- Select Class --</option>{classes.map(c => <option key={c.id} value={c.id}>{c.label}</option>)}</Select></Field>
        {isFee && <Field label="Search Student"><Input value={studentQuery} onChange={e => setStudentQuery(e.target.value)} /></Field>}
        <Field label="Select Student"><Select value={studentId} onChange={e => setStudentId(e.target.value)} disabled={!secId}><option value="">-- Select Student --</option>{filteredStudents.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}</Select></Field>
        {!isTc && <Field label={type === 'character' ? 'Date' : 'Date'}><Input type="date" value={date} onChange={e => setDate(e.target.value)} /></Field>}
        {type === 'character' && <Field label="Admission Year"><Input value={admissionYear} onChange={e => setAdmissionYear(e.target.value)} /></Field>}
        {isFee && <Field label="Select Fee Head"><Select multiple value={selectedFeeHeads} onChange={e => setSelectedFeeHeads([...e.target.selectedOptions].map(o => o.value))}>{(boot?.feeHeads?.length ? boot.feeHeads : availableFeeHeads).map(h => <option key={h.value} value={h.value}>{h.label}</option>)}</Select></Field>}
        {isFee && <Field label="Select Report Type"><Select value={reportType} onChange={e => setReportType(e.target.value)}>{(boot?.reportTypes || [{ value: 'Multiple', label: 'Multiple' }, { value: 'Composite', label: 'Composite' }]).map(r => <option key={r.value} value={r.value}>{r.label}</option>)}</Select></Field>}
        {isTc && <><Field label="Admission Class"><Select value={admissionClass} onChange={e => setAdmissionClass(e.target.value)}><option value="">-- Select Class --</option>{(boot?.admissionClasses || []).map(c => <option key={c.id} value={c.id}>{c.label}</option>)}</Select></Field><Field label="Last Date Of Attendence"><Input type="date" value={lastAttendance} onChange={e => setLastAttendance(e.target.value)} /></Field><Field label="Promoted To"><Select value={promotedClass} onChange={e => setPromotedClass(e.target.value)}><option value="">-- Select Class --</option>{(boot?.promotedClasses || []).map(c => <option key={c.id} value={c.id}>{c.label}</option>)}</Select></Field></>}
      </div>
      <div className="mt-4 flex flex-wrap gap-2 border-t border-slate-100 pt-4 dark:border-slate-800"><Button onClick={generate} disabled={busy}>{busy ? 'Loading…' : isTc ? 'Get Certificate' : 'Get Certificate'}</Button><Button variant="secondary" onClick={reset}>Reset</Button>{isFee && <Button variant="secondary" onClick={exportFee} disabled={!result?.data?.rows?.length}>Export</Button>}{isTc && tcData && <Button variant="secondary" onClick={saveTcAndPreview} disabled={busy}>Preview / Save</Button>}</div>
      <ErrorText message={message} />
    </div>
    {isTc && tcData && !result && <TcEditor data={tcData} setField={setTc} onPreview={saveTcAndPreview} busy={busy} />}
    {result && <CertificatePreview type={result.type} data={result.data} school={result.data.school || boot?.school} onPrint={() => window.print()} />}
  </div>
}

function TcEditor({ data, setField, onPreview, busy }) {
  const fields = [['sno','SL No.'],['admissionNo','Admission No.'],['enroll_no','Enrollment No.'],['stu_name','Student Name'],['mother_name','Mother Name'],['father_name','Father Name'],['dob','Date of Birth'],['nationality','Nationality'],['PEN_No','PEN No.'],['admission_date','Admission Date'],['last_class','Last Class'],['last_class_word1','Class in Words'],['last_exam_result','Last Exam Result'],['subjects','Subjects Studied'],['paid_fee_status','All Dues Paid'],['fee_concession','Fee Concession'],['totalWorkingDays','Working Days'],['total_att','Total Attendance'],['ncc_cadet_status','NCC Cadet'],['txtncc','NCC Details'],['extra_activity','Extra Activity'],['general_conduct','General Conduct'],['application_date','Application Date'],['issue_date','Issue Date'],['reason_for_leaving','Reason for Leaving'],['withdrawn_date','Removal Date'],['remark','Remark']]
  return <div className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm dark:border-slate-700 dark:bg-slate-900"><h2 className="mb-3 font-semibold text-slate-800 dark:text-slate-100">Transfer Certificate Details</h2><div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">{fields.map(([key,label]) => <Field key={key} label={label}><Input value={data[key] || ''} onChange={e => setField(key,e.target.value)} /></Field>)}</div><div className="mt-4 border-t border-slate-100 pt-4 dark:border-slate-800"><Button onClick={onPreview} disabled={busy}>{busy ? 'Saving…' : 'Preview & Print TC'}</Button></div></div>
}
