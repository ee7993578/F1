import { useEffect, useMemo, useState } from 'react'
import { apiFetch } from '../../../../lib/api'

const input = 'w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm outline-none focus:border-blue-500 dark:border-slate-600 dark:bg-slate-800'
const button = 'rounded-lg px-4 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:opacity-50'

function Field({ label, children, required = false }) {
  return <label className="block text-xs font-semibold text-slate-600 dark:text-slate-300"><span className="mb-1 block">{label}{required && <span className="text-rose-500"> *</span>}</span>{children}</label>
}
function Select({ value, onChange, children, disabled = false }) { return <select className={input} value={value ?? ''} onChange={onChange} disabled={disabled}><option value="">&lt;-select-&gt;</option>{children}</select> }
function Button({ children, onClick, disabled = false, tone = 'primary' }) { const colour = tone === 'danger' ? 'bg-rose-600 hover:bg-rose-700' : tone === 'muted' ? 'bg-slate-600 hover:bg-slate-700' : 'bg-blue-600 hover:bg-blue-700'; return <button type="button" className={`${button} ${colour}`} onClick={onClick} disabled={disabled}>{children}</button> }
function Card({ children, className = '' }) { return <div className={`rounded-xl border border-slate-200 bg-white p-4 shadow-sm dark:border-slate-700 dark:bg-slate-900 ${className}`}>{children}</div> }
function Notice({ message, error = false }) { if (!message) return null; return <div className={`rounded-lg border px-3 py-2 text-sm ${error ? 'border-rose-200 bg-rose-50 text-rose-700' : 'border-emerald-200 bg-emerald-50 text-emerald-700'}`}>{message}</div> }
function Empty({ children = 'No Record Found' }) { return <div className="px-4 py-10 text-center text-sm text-slate-500">{children}</div> }
function TableShell({ children }) { return <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm dark:border-slate-700 dark:bg-slate-900">{children}</div> }
function Options({ items = [] }) { return items.map(item => <option key={item.id ?? item.value} value={item.id ?? item.value}>{item.name ?? item.label ?? item.value}</option>) }
function classwiseKey(row) { return String(row.stuId ?? row.stu_id) }
function studentwiseKey(row) { return `${row.stuId ?? row.stu_id}-${row.subjectId ?? row.Subject_id}-${row.termId ?? row.Term_id}-${row.examTypeId ?? row.Exam_type_id}` }

export default function DeleteMarks() {
  const [mode, setMode] = useState('StudentWise')
  const [marksType, setMarksType] = useState('Marks')
  const [session, setSession] = useState('')
  const [classes, setClasses] = useState([])
  const [classId, setClassId] = useState('')
  const [terms, setTerms] = useState([])
  const [termId, setTermId] = useState('')
  const [examTypes, setExamTypes] = useState([])
  const [examTypeId, setExamTypeId] = useState('')
  const [subjects, setSubjects] = useState([])
  const [subjectId, setSubjectId] = useState('')
  const [registrationNo, setRegistrationNo] = useState('')
  const [studentName, setStudentName] = useState('')
  const [suggestions, setSuggestions] = useState([])
  const [selectedStudent, setSelectedStudent] = useState(null)
  const [classRows, setClassRows] = useState([])
  const [studentRows, setStudentRows] = useState([])
  const [selected, setSelected] = useState(new Set())
  const [maxMin, setMaxMin] = useState(null)
  const [shown, setShown] = useState(false)
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState(false)

  const rows = mode === 'ClassWise' ? classRows : studentRows
  const allKeys = useMemo(() => rows.map(row => mode === 'ClassWise' ? classwiseKey(row) : studentwiseKey(row)), [rows, mode])
  const allSelected = allKeys.length > 0 && allKeys.every(key => selected.has(key))
  const notify = (text, failed = false) => { setError(failed); setMessage(text || '') }
  const clearResults = () => { setShown(false); setClassRows([]); setStudentRows([]); setSelected(new Set()); setMaxMin(null) }

  useEffect(() => {
    let alive = true
    apiFetch('/examination/delete-marks/bootstrap').then(data => { if (alive) { setSession(data.session || ''); setClasses(data.classes || []) } }).catch(err => alive && notify(err.message || 'Delete Marks could not be loaded.', true))
    return () => { alive = false }
  }, [])

  useEffect(() => {
    if (!session || !classId) { setTerms([]); setSubjects([]); return undefined }
    let alive = true
    Promise.all([
      apiFetch(`/examination/delete-marks/terms?${new URLSearchParams({ session, sectionId: classId })}`),
      apiFetch(`/examination/delete-marks/subjects?${new URLSearchParams({ session, sectionId: classId })}`),
    ]).then(([termRows, subjectRows]) => { if (alive) { setTerms(Array.isArray(termRows) ? termRows : []); setSubjects(Array.isArray(subjectRows) ? subjectRows : []) } }).catch(err => alive && notify(err.message || 'Class options could not be loaded.', true))
    return () => { alive = false }
  }, [session, classId])

  useEffect(() => {
    if (!session || !classId || !termId) { setExamTypes([]); return undefined }
    let alive = true
    apiFetch(`/examination/delete-marks/exam-types?${new URLSearchParams({ session, sectionId: classId, termId })}`).then(data => alive && setExamTypes(Array.isArray(data) ? data : [])).catch(err => alive && notify(err.message || 'Exam types could not be loaded.', true))
    return () => { alive = false }
  }, [session, classId, termId])

  useEffect(() => {
    setSuggestions([])
    if (mode !== 'StudentWise' || !session || registrationNo.trim().length < 1 || selectedStudent?.registrationNo === registrationNo.trim()) return undefined
    const timer = window.setTimeout(() => apiFetch(`/examination/delete-marks/student-search?${new URLSearchParams({ session, prefix: registrationNo.trim() })}`).then(data => setSuggestions(Array.isArray(data) ? data : [])).catch(() => setSuggestions([])), 250)
    return () => window.clearTimeout(timer)
  }, [session, registrationNo, mode, selectedStudent])

  const resetClassFilters = () => { setClassId(''); setTermId(''); setExamTypeId(''); setSubjectId(''); setTerms([]); setExamTypes([]); setSubjects([]); clearResults() }
  const changeMode = next => { setMode(next); setSelected(new Set()); clearResults(); notify(''); if (next === 'StudentWise') { setClassId(''); setTermId(''); setExamTypeId(''); setSubjectId('') } }

  const showClassMarks = async () => {
    if (!classId || !termId || !examTypeId || !subjectId) { notify('Class, Term, Exam Type and Subject are required.', true); return }
    setLoading(true); notify('')
    try { const query = new URLSearchParams({ session, sectionId: classId, marksType, examTypeId, subjectId }); const data = await apiFetch(`/examination/delete-marks/rows?${query}`); setClassRows(Array.isArray(data.rows) ? data.rows : []); setMaxMin(data.maxMin || null); setSelected(new Set()); setShown(true) } catch (err) { notify(err.message || 'Marks could not be loaded.', true); clearResults() } finally { setLoading(false) }
  }
  const showStudentMarks = async () => {
    const reg = selectedStudent?.registrationNo || registrationNo.trim()
    if (!reg) { notify('Registration No is required.', true); return }
    setLoading(true); notify('')
    try { const data = await apiFetch(`/examination/delete-marks/student-rows?${new URLSearchParams({ session, registrationNo: reg })}`); setStudentRows(Array.isArray(data) ? data : []); setSelected(new Set()); setShown(true) } catch (err) { notify(err.message || 'Student marks could not be loaded.', true); clearResults() } finally { setLoading(false) }
  }
  const toggle = key => setSelected(current => { const next = new Set(current); if (next.has(key)) next.delete(key); else next.add(key); return next })
  const toggleAll = () => setSelected(allSelected ? new Set() : new Set(allKeys))
  const deleteRows = async (records, selectedKeys) => {
    const target = records.filter(row => selectedKeys.has(mode === 'ClassWise' ? classwiseKey(row) : studentwiseKey(row)))
    if (!target.length) { notify('Select at least one record.', true); return }
    if (!window.confirm(`Are you sure you want to delete marks for ${target.length} record(s)?`)) return
    setSaving(true); notify('')
    try {
      const body = { session, mode, rows: target.map(row => mode === 'ClassWise' ? { stuId: row.stuId, sectionId: classId, subjectId, examTypeId } : { stuId: row.stuId, sectionId: row.secId, subjectId: row.subjectId, examTypeId: row.examTypeId }) }
      await apiFetch('/examination/delete-marks/delete', { method: 'POST', body: JSON.stringify(body) })
      notify('Record Saved Successfully')
      if (mode === 'ClassWise') await showClassMarks(); else await showStudentMarks()
    } catch (err) { notify(err.message || 'Something went wrong.', true) } finally { setSaving(false) }
  }
  const selectStudent = student => { setSelectedStudent(student); setRegistrationNo(student.registrationNo || ''); setStudentName(`${student.name || ''}${student.classSection ? ` (${student.classSection})` : ''}`); setSuggestions([]); clearResults() }

  return <section className="space-y-4 p-1">
    <div><h1 className="text-xl font-bold text-slate-800 dark:text-slate-100">Delete Marks</h1><p className="text-xs text-slate-500">Exam Master · Update</p></div>
    <Notice message={message} error={error} />
    <Card>
      <div className="grid gap-3 md:grid-cols-3"><Field label="Session"><select className={input} value={session} disabled><option value={session}>{session}</option></select></Field><div className="flex items-end gap-5 pb-2 text-sm"><label className="flex items-center gap-2"><input type="radio" checked={mode === 'StudentWise'} onChange={() => changeMode('StudentWise')} /> Student Wise</label><label className="flex items-center gap-2"><input type="radio" checked={mode === 'ClassWise'} onChange={() => changeMode('ClassWise')} /> Class Wise</label></div></div>
      {mode === 'ClassWise' && <div className="mt-4 grid gap-3 md:grid-cols-5"><div className="flex items-end gap-4 pb-2 text-sm md:col-span-5"><span className="font-semibold">Marks Type</span><label className="flex items-center gap-2"><input type="radio" checked={marksType === 'Marks'} onChange={() => { setMarksType('Marks'); clearResults() }} /> Marks</label><label className="flex items-center gap-2"><input type="radio" checked={marksType === 'Grade'} onChange={() => { setMarksType('Grade'); clearResults() }} /> Grade</label></div><Field label="Class" required><Select value={classId} onChange={e => { setClassId(e.target.value); setTermId(''); setExamTypeId(''); setSubjectId(''); clearResults() }}><Options items={classes} /></Select></Field><Field label="Term" required><Select value={termId} onChange={e => { setTermId(e.target.value); setExamTypeId(''); clearResults() }}><Options items={terms} /></Select></Field><Field label="Exam Type" required><Select value={examTypeId} onChange={e => { setExamTypeId(e.target.value); clearResults() }}><Options items={examTypes} /></Select></Field><Field label="Subject" required><Select value={subjectId} onChange={e => { setSubjectId(e.target.value); clearResults() }}><Options items={subjects} /></Select></Field><div className="flex items-end gap-2"><Button onClick={showClassMarks} disabled={loading || saving}>{loading ? 'Loading…' : 'Show'}</Button><Button tone="muted" onClick={resetClassFilters}>Reset</Button></div></div>}
      {mode === 'StudentWise' && <div className="mt-4 grid gap-3 md:grid-cols-3"><div className="relative"><Field label="Registration No" required><input className={input} value={registrationNo} onChange={e => { setRegistrationNo(e.target.value); setSelectedStudent(null); setStudentName(''); clearResults() }} /></Field>{suggestions.length > 0 && <div className="absolute z-20 mt-1 max-h-72 w-full overflow-y-auto rounded-lg border border-slate-200 bg-white shadow-lg dark:border-slate-700 dark:bg-slate-900">{suggestions.map(student => <button type="button" className="block w-full border-b p-3 text-left text-sm hover:bg-slate-50 dark:border-slate-700 dark:hover:bg-slate-800" key={student.stuId} onClick={() => selectStudent(student)}><div className="font-semibold">{student.registrationNo}</div><div>{student.name}</div><div className="text-xs text-slate-500">{student.classSection}</div></button>)}</div>}</div><Field label="Student Name"><input className={`${input} bg-slate-50`} value={studentName} disabled /></Field><div className="flex items-end"><Button onClick={showStudentMarks} disabled={loading || saving}>{loading ? 'Loading…' : 'Show'}</Button></div></div>}
    </Card>
    {shown && mode === 'ClassWise' && <Card>{maxMin && <div className="mb-4 flex gap-6 text-sm font-semibold"><span>Min Marks: {maxMin.minMarks ?? '0'}</span><span>Max Marks: {maxMin.maxMarks ?? '0'}</span></div>}{classRows.length ? <><TableShell><table className="min-w-full text-sm"><thead className="bg-slate-50 text-left text-xs uppercase text-slate-500 dark:bg-slate-800"><tr><th className="p-3"><input type="checkbox" checked={allSelected} onChange={toggleAll} /></th><th className="p-3">Adm No.</th><th className="p-3">Student Name</th><th className="p-3">Marks</th><th className="p-3">Delete</th></tr></thead><tbody>{classRows.map((row, index) => { const key = classwiseKey(row); return <tr className="border-t border-slate-100 dark:border-slate-700" key={key}><td className="p-3"><input type="checkbox" checked={selected.has(key)} onChange={() => toggle(key)} /> <span className="ml-2">{index + 1}</span></td><td className="p-3">{row.registrationNo}</td><td className="p-3">{row.name}</td><td className="p-3 text-center">{row.marks ?? ''}</td><td className="p-3"><Button tone="danger" onClick={() => deleteRows([row], new Set([key]))} disabled={saving}>Delete</Button></td></tr> })}</tbody></table></TableShell><div className="mt-4 flex justify-center"><Button tone="danger" onClick={() => deleteRows(classRows, selected)} disabled={saving || selected.size === 0}>Delete</Button></div></> : <Empty />}</Card>}
    {shown && mode === 'StudentWise' && <Card>{studentRows.length ? <><TableShell><table className="min-w-full text-sm"><thead className="bg-slate-50 text-left text-xs uppercase text-slate-500 dark:bg-slate-800"><tr><th className="p-3"><input type="checkbox" checked={allSelected} onChange={toggleAll} /></th><th className="p-3">Subject</th><th className="p-3">Term</th><th className="p-3">Exam Name</th><th className="p-3">Marks</th><th className="p-3">Max Marks</th><th className="p-3">Faculty Name</th></tr></thead><tbody>{studentRows.map((row, index) => { const key = studentwiseKey(row); return <tr className="border-t border-slate-100 dark:border-slate-700" key={key}><td className="p-3"><input type="checkbox" checked={selected.has(key)} onChange={() => toggle(key)} /> <span className="ml-2">{index + 1}</span></td><td className="p-3">{row.subject}</td><td className="p-3">{row.termId}</td><td className="p-3">{row.examType}</td><td className="p-3 text-center">{row.marks ?? ''}</td><td className="p-3 text-center">{row.maxMarks ?? ''}</td><td className="p-3">{row.facultyName ?? ''}</td></tr> })}</tbody></table></TableShell><div className="mt-4 flex justify-center"><Button tone="danger" onClick={() => deleteRows(studentRows, selected)} disabled={saving || selected.size === 0}>Delete</Button></div></> : <Empty />}</Card>}
  </section>
}
