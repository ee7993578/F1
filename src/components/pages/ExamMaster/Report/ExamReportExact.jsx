import { useEffect, useMemo, useState } from 'react'
import { Download, FileText, Loader2, RefreshCw, Search, Upload, X } from 'lucide-react'
import { Area, AreaChart, Bar, BarChart, CartesianGrid, Cell, Line, LineChart, Pie, PieChart, ResponsiveContainer, Scatter, ScatterChart, Tooltip, XAxis, YAxis, ZAxis } from 'recharts'
import { apiFetch } from '../../../../lib/api'

const REPORTS = {
  CumulativeSheet: { title: 'Cumulative Sheet', fields: ['session', 'class', 'term'], report: 'cumulative' },
  GetClassSubjects: { title: 'Classwise Subjects', fields: ['session'], report: 'subjects' },
  MarksLogReport: { title: 'Marks Log Report', fields: ['mode', 'session', 'class', 'term', 'exam', 'subject'], report: 'marks-log' },
  MarksStatusGraph: { title: 'Marks Status Subject-wise (Graph)', fields: ['session', 'class', 'term', 'exam', 'subject', 'graphMode'], report: 'marks-status-graph' },
  MarkStatusReport: { title: 'MarkStatus Report', fields: ['session', 'class', 'section', 'exam'], report: 'mark-status' },
  OverallExaminationReport: { title: 'Overall Examination Report', fields: ['session', 'class'], report: 'overall' },
  ReportCard: { title: 'Report Card', fields: ['session', 'type', 'class', 'term', 'student', 'date'], report: 'reportcard' },
  ReportCardExamwise: { title: 'Report Card (Exam-wise)', fields: ['session', 'class', 'term', 'exam', 'student', 'date'], report: 'reportcard-examwise' },
  StudentAcademicPerformance: { title: 'Academic Performance', fields: ['session', 'class', 'student', 'graphMode'], report: 'academic-performance' },
  SubjectReport: { title: 'Subject Report', fields: ['session', 'class', 'section'], report: 'subject-report' },
  ToppersReport: { title: 'Toppers Report', fields: ['session', 'class', 'sections', 'term', 'type', 'percentage', 'topN', 'date'], report: 'toppers' },
  CBSE_AISSE_Result: { title: 'CBSE AISSE Result', fields: ['session', 'class', 'type', 'stream', 'section'], report: 'cbse-overall' },
  CBSE_AISSE_Result_MainSubject: { title: 'CBSE AISSE Result (Main Subjects)', fields: ['session', 'class', 'type'], report: 'cbse-main' },
  CBSE_AnnualReport: { title: 'CBSE Annual Report', annual: true, report: 'annual' },
  Class_overall_marks: { title: 'Overall Performance Report', fields: ['session', 'class'], report: 'class-overall' },
  ClasswiseStudentPercentage: { title: 'Mark List In Percentage', fields: ['session', 'class', 'term', 'exam', 'subject', 'fromPercent', 'toPercent'], report: 'percentage' },
  ExamComparativeSheet: { title: 'Exam Comparative Report', fields: ['session', 'class', 'exams', 'calculationMode', 'calculation'], report: 'comparative' },
  ExamwiseGreensheet: { title: 'Green Sheet', fields: ['session', 'class', 'term'], report: 'examwise-greensheet' },
  ExpectedBoardAverageReport: { title: 'Expected Board Average Report', fields: ['session', 'class', 'term', 'exam', 'subjects'], report: 'expected-board' },
  ExpectedSchoolAverageReport: { title: 'Expected School Average Report', fields: ['session', 'class', 'term', 'exam'], report: 'expected-school' },
  FailureStudentReport: { title: 'Failure Student Report', fields: ['session', 'class', 'section', 'term', 'exam', 'subject'], report: 'failure' },
  GreenSheetNew: { title: 'Green Sheet Report', fields: ['session', 'class', 'term'], report: 'green-sheet' },
  GreenSheetPrimary: { title: 'Green Sheet Primary', fields: ['session', 'class', 'header', 'term', 'exam'], report: 'green-primary' },
  GreenSheetSubjectWise: { title: 'Subject Wise Green Sheet', fields: ['session', 'class', 'subject'], report: 'green-subject' },
  MarksListBlank: { title: 'Mark List', fields: ['session', 'class', 'term', 'exam', 'subject'], report: 'marks-list-blank' },
  MarksListJunior: { title: 'Mark List Junior', fields: ['session', 'class', 'term', 'exam', 'subject'], report: 'marks-list-junior' },
  Report_ExamResultDiscussion: { title: 'Result Discussion Report', fields: ['session', 'class', 'term', 'exam'], report: 'result-discussion' },
}

const fallbackConfig = { title: 'Examination Report', fields: ['session', 'class', 'term', 'exam', 'subject'], report: 'exam-report' }
const CLASS_ID_REPORTS = new Set(['MarkStatusReport', 'ToppersReport', 'SubjectReport', 'FailureStudentReport', 'ExpectedSchoolAverageReport', 'CBSE_AISSE_Result', 'CBSE_AISSE_Result_MainSubject'])
const labels = { session: 'Session', class: 'Class', section: 'Section', term: 'Term', exam: 'Exam', subject: 'Subject', student: 'Student', header: 'Header', feehead: 'Fee Head', type: 'Report Type', stream: 'Stream', mode: 'Mode', graphMode: 'View Type', performanceMode: 'Performance Type', calculationMode: 'Calculation Type', calculation: 'Calculation Percentage', fromPercent: 'From Percentage', toPercent: 'To Percentage', topN: 'Top N Students', percentage: 'Percentage Criteria', date: 'Date', exams: 'Exams', subjects: 'Subjects', sections: 'Sections' }
const input = 'w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200'
const text = value => value == null ? '' : String(value)
const display = value => text(value).replace(/([a-z])([A-Z])/g, '$1 $2').replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase())

function valueId(item) {
  return text(item?.id ?? item?.sectionId ?? item?.classId ?? item?.value ??
    item?.session ?? item?.termId ?? item?.term_id ?? item?.exam_type_id ??
    item?.subject_id ?? item?.studentId ?? item?.header_id)
}
function valueName(item) {
  return text(item?.name ?? item?.label ?? item?.className ?? item?.title ??
    item?.subjectName ?? item?.examName ?? item?.session ?? item?.term_name ??
    item?.exam_type ?? item?.Subject ?? item?.subject ?? item?.section ?? item?.header_name)
}
function SelectField({ name, value, onChange, options = [], multiple = false, disabled = false }) {
  return <select className={input} name={name} value={multiple ? (Array.isArray(value) ? value : []) : value} onChange={e => onChange(name, multiple ? [...e.target.selectedOptions].map(o => o.value) : e.target.value)} multiple={multiple} disabled={disabled}>
    {!multiple && <option value="">-- Select {labels[name] || display(name)} --</option>}
    {options.map((item, index) => <option key={`${valueId(item)}-${index}`} value={valueId(item)}>{valueName(item)}</option>)}
  </select>
}

function Field({ name, value, onChange, options, disabled }) {
  if (name === 'date') return <input className={input} type="date" value={value || ''} onChange={e => onChange(name, e.target.value)} />
  if (['fromPercent', 'toPercent', 'topN', 'percentage', 'calculation'].includes(name)) return <input className={input} type="number" min="0" step="0.01" value={value || ''} onChange={e => onChange(name, e.target.value)} placeholder={name === 'topN' ? 'All' : ''} />
  if (['exams', 'subjects', 'sections'].includes(name)) {
    return <SelectField name={name} value={value} onChange={onChange} options={options || []} multiple disabled={disabled} />
  }
  return <SelectField name={name} value={value} onChange={onChange} options={options} disabled={disabled} />
}

function excelDownload(columns, rows, filename) {
  const escape = value => text(value).replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;')
  const html = `<html><head><meta charset="utf-8" /></head><body><table border="1"><thead><tr>${columns.map(column => `<th>${escape(display(column))}</th>`).join('')}</tr></thead><tbody>${rows.map(row => `<tr>${columns.map(column => `<td>${escape(row[column])}</td>`).join('')}</tr>`).join('')}</tbody></table></body></html>`
  const blob = new Blob([html], { type: 'application/vnd.ms-excel;charset=utf-8' })
  const url = URL.createObjectURL(blob); const a = document.createElement('a'); a.href = url; a.download = filename; a.click(); URL.revokeObjectURL(url)
}

export default function ExamReportExact({ pageId = 'CumulativeSheet' }) {
  const config = REPORTS[pageId] || fallbackConfig
  const [boot, setBoot] = useState({ sessions: [], classes: [], subjects: [], terms: [], examTypes: [], streams: [] })
  const [options, setOptions] = useState({ sections: [], students: [], headers: [], exams: [], subjects: [], terms: [] })
  const [filters, setFilters] = useState({ session: '', class: '', section: '', term: '', exam: '', subject: '', student: '', header: '', type: '', stream: '', mode: 'Marks', graphMode: 'column', performanceMode: 'Term Wise', calculationMode: 'Percentage', calculation: '', fromPercent: '', toPercent: '', topN: '', percentage: '', date: new Date().toISOString().slice(0, 10), exams: [], subjects: [], sections: [] })
  const [rows, setRows] = useState([]); const [columns, setColumns] = useState([]); const [reportCard, setReportCard] = useState(null); const [loading, setLoading] = useState(true); const [running, setRunning] = useState(false); const [error, setError] = useState(''); const [shown, setShown] = useState(false)
  const [annual, setAnnual] = useState({ title: '', date: '', file: null, reports: [] })

  const classIdReport = CLASS_ID_REPORTS.has(pageId)
  const classItems = pageId === 'CBSE_AISSE_Result_MainSubject'
    ? [{ id: '12', name: 'Class 12th' }]
    : pageId.startsWith('CBSE')
      ? [{ id: '10', name: 'Class 10th' }, { id: '12', name: 'Class 12th' }]
      : classIdReport ? (boot.classGroups || []) : (boot.classes || [])
  const selectedClass = classItems.find(item => valueId(item) === filters.class)
  const optionMap = useMemo(() => ({
    session: boot.sessions || [], class: classItems, section: options.sections || classItems, term: options.terms?.length ? options.terms : boot.terms || [], exam: options.exams?.length ? options.exams : boot.examTypes || [], subject: options.subjects?.length ? options.subjects : boot.subjects || [], student: options.students || [], header: options.headers || [], stream: pageId.startsWith('CBSE') ? [{ id: 'Science', name: 'Science' }, { id: 'Commerce', name: 'Commerce' }, { id: 'Humanities', name: 'Humanities' }] : boot.streams || [], type: pageId === 'ReportCard' ? [{ id: 'Default', name: 'Default' }, { id: 'HPC', name: 'HPC' }, { id: 'Both', name: 'Both' }] : pageId === 'ToppersReport' ? [{ id: 'ClassWise', name: 'Class Wise' }, { id: 'SectionWise', name: 'Section Wise' }] : pageId === 'CBSE_AISSE_Result_MainSubject' ? [{ id: '4', name: 'Subject Aggregate' }] : pageId.startsWith('CBSE') ? [{ id: '1', name: 'Over All' }, { id: '3', name: 'Top Five Students' }, { id: '4', name: 'Subject Aggregate' }, { id: '5', name: 'Highest Subject Marks' }, { id: '6', name: 'ComparativeResultAnalysis' }, { id: '7', name: 'Result at a Glance' }, { id: '8', name: 'Result Analysis' }, { id: '9', name: 'Section Wise Result Analysis' }] : [{ id: 'TermWise', name: 'Term Wise' }, { id: 'ExamWise', name: 'Exam Wise' }], mode: [{ id: 'Marks', name: 'Marks' }, { id: 'Grade', name: 'Grade' }], graphMode: [{ id: 'bar', name: 'bar' }, { id: 'column', name: 'column' }, { id: 'line', name: 'line' }, { id: 'area', name: 'area' }, { id: 'pie', name: 'pie' }, { id: 'doughnut', name: 'doughnut' }, { id: 'scatter', name: 'scatter' }, { id: 'bubble', name: 'bubble' }], performanceMode: [{ id: 'Term Wise', name: 'Term Wise' }, { id: 'Exam Wise', name: 'Exam Wise' }], calculationMode: options.exams || [],
  }), [boot, classItems, options, pageId])

  useEffect(() => { let alive = true; setLoading(true); apiFetch('/examination/reports/bootstrap').then(data => { if (!alive) return; setBoot(data || {}); setFilters(prev => ({ ...prev, session: prev.session || text(data.session), class: prev.class || (pageId === 'CBSE_AISSE_Result_MainSubject' ? '12' : pageId === 'CBSE_AISSE_Result' ? '10' : ''), type: prev.type || (pageId === 'CBSE_AISSE_Result_MainSubject' ? '4' : pageId === 'CBSE_AISSE_Result' ? '1' : pageId === 'ReportCard' ? 'Default' : '') })); setAnnual(prev => ({ ...prev, reports: data.annualReports || [] })) }).catch(e => alive && setError(e.message)).finally(() => alive && setLoading(false)); return () => { alive = false } }, [])
  useEffect(() => { if (!filters.session) return; let alive = true; const params = new URLSearchParams({ session: filters.session, sectionId: classIdReport ? (filters.section || '0') : (filters.class || '0'), classId: classIdReport ? (filters.class || '0') : (selectedClass?.classId || '0'), termId: filters.term || '' }); apiFetch(`/examination/reports/options?${params}`).then(data => alive && setOptions(data || {})).catch(e => alive && setError(e.message)); return () => { alive = false } }, [filters.session, filters.class, filters.section, filters.term, selectedClass?.classId, classIdReport])
  useEffect(() => { setFilters(prev => ({ ...prev, term: '', exam: '', subject: '', student: '', header: '', section: '', exams: [], subjects: [], sections: [] })); setRows([]); setShown(false) }, [filters.class])

  function change(name, value) {
    setFilters(prev => {
      const next = { ...prev, [name]: value }
      if (name === 'session') Object.assign(next, { class: '', section: '', term: '', exam: '', subject: '', student: '', header: '' })
      if (name === 'term') Object.assign(next, { exam: '', subject: '' })
      return next
    }); setError('')
  }
  async function showReport() {
    if (!filters.session) return setError('Session is required')
    if (config.fields.includes('class') && !filters.class) return setError('Class is required')
    setRunning(true); setError('')
    try { const data = await apiFetch('/examination/reports/data', { method: 'POST', body: JSON.stringify({ ...filters, report: config.report, pageId, classGroup: selectedClass?.classGroup || '', sectionIds: filters.sections || [], sectionId: classIdReport ? (filters.section || '0') : (filters.class || '0'), classId: classIdReport ? (filters.class || '0') : (selectedClass?.classId || '0'), termId: filters.term, examTypeId: filters.exam, subjectId: filters.subject, studentId: filters.student }) }); setRows(data.rows || []); setColumns(data.columns || []); setReportCard(data.reportCard || null); setShown(true) } catch (e) { setError(e.message) } finally { setRunning(false) }
  }
  async function saveAnnual() { if (!annual.title || !annual.file) return setError('Title and file are required'); const body = new FormData(); body.append('title', annual.title); body.append('date', annual.date); body.append('file', annual.file); try { const data = await apiFetch('/examination/reports/annual-report', { method: 'POST', body }); setAnnual(prev => ({ ...prev, title: '', date: '', file: null, reports: data.reports || [] })); setError('Record saved successfully') } catch (e) { setError(e.message) } }
  async function deleteAnnual(id) { try { const data = await apiFetch(`/examination/reports/annual-report/${id}`, { method: 'DELETE' }); setAnnual(prev => ({ ...prev, reports: data.reports || [] })) } catch (e) { setError(e.message) } }

  if (loading) return <div className="flex items-center justify-center rounded-xl bg-white p-12 text-slate-500 dark:bg-slate-900"><Loader2 className="mr-2 h-5 w-5 animate-spin" />Loading report options...</div>
  if (config.annual) return <div className="space-y-5"><Header title={config.title} /><section className="rounded-xl bg-white p-5 shadow-sm dark:bg-slate-900"><div className="grid gap-4 md:grid-cols-3"><label className="text-sm font-semibold">Title<input className={`${input} mt-1`} value={annual.title} onChange={e => setAnnual(p => ({ ...p, title: e.target.value }))} /></label><label className="text-sm font-semibold">Date<input className={`${input} mt-1`} type="date" value={annual.date} onChange={e => setAnnual(p => ({ ...p, date: e.target.value }))} /></label><label className="text-sm font-semibold">File<input className={`${input} mt-1`} type="file" accept=".pdf,.doc,.docx,.xls,.xlsx" onChange={e => setAnnual(p => ({ ...p, file: e.target.files?.[0] || null }))} /></label></div><button className="mt-4 inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white" onClick={saveAnnual}><Upload size={16} />Submit</button></section><ReportTable columns={['id', 'title', 'reportDate', 'filePath']} rows={annual.reports} onDelete={deleteAnnual} /></div>

  const visibleFields = config.fields.filter(name => {
    if (pageId === 'CBSE_AISSE_Result') {
      if (name === 'stream') return filters.type === '8' && filters.class === '12'
      if (name === 'section') return filters.type === '9'
    }
    return true
  })
  return <div className="space-y-5"><Header title={config.title} /><section className="rounded-xl bg-white p-4 shadow-sm dark:bg-slate-900"><div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">{visibleFields.map(name => <label key={name} className="text-xs font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400">{labels[name] || display(name)}<Field name={name} value={filters[name]} onChange={change} options={optionMap[name] || []} disabled={['class', 'term', 'exam', 'subject', 'student', 'header'].includes(name) && ((name !== 'class' && !filters.class) || (name === 'class' && !filters.session))} /></label>)}</div><div className="mt-4 flex flex-wrap gap-2"><button onClick={showReport} disabled={running} className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60">{running ? <Loader2 size={16} className="animate-spin" /> : <Search size={16} />}Show</button><button onClick={() => { setRows([]); setReportCard(null); setShown(false); setError('') }} className="inline-flex items-center gap-2 rounded-lg border border-slate-200 px-4 py-2 text-sm font-semibold text-slate-600 dark:border-slate-700 dark:text-slate-300"><RefreshCw size={15} />Reset</button>{shown && <button onClick={() => excelDownload(columns, rows, `${pageId}.xls`)} className="inline-flex items-center gap-2 rounded-lg bg-emerald-600 px-4 py-2 text-sm font-semibold text-white"><Download size={16} />Excel</button>}</div>{error && <div className="mt-3 rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-700 dark:text-rose-300">{error}</div>}</section>{shown && (pageId === 'ReportCard' || pageId === 'ReportCardExamwise' ? <ReportCardView reportCard={reportCard} /> : pageId === 'StudentAcademicPerformance' ? <AcademicPerformanceView rows={rows} graphType={filters.graphMode} /> : pageId === 'MarksStatusGraph' ? <MarksStatusGraphView rows={rows} graphType={filters.graphMode} /> : <ReportTable columns={columns} rows={rows} />)}</div>
}

function Header({ title }) { return <div className="rounded-xl border border-blue-100 bg-gradient-to-r from-blue-50 to-indigo-50 px-5 py-4 dark:border-indigo-900/40 dark:from-slate-900 dark:to-indigo-950/30"><div className="flex items-center gap-2 text-lg font-bold text-slate-800 dark:text-slate-100"><FileText size={20} className="text-blue-600" />{title}</div><p className="mt-1 text-xs text-slate-500">Dynamic report data from the selected session and class.</p></div> }
function ReportTable({ columns = [], rows = [], onDelete }) { return <section className="overflow-hidden rounded-xl bg-white shadow-sm dark:bg-slate-900"><div className="flex items-center justify-between border-b border-slate-100 px-4 py-3 dark:border-slate-800"><h3 className="text-sm font-bold text-slate-700 dark:text-slate-200">Report Data</h3><span className="text-xs text-slate-400">{rows.length} record(s)</span></div><div className="overflow-x-auto"><table className="min-w-full text-left text-xs"><thead className="bg-slate-50 dark:bg-slate-800/70"><tr>{columns.map(column => <th key={column} className="whitespace-nowrap px-3 py-2 font-bold uppercase tracking-wide text-slate-500">{display(column)}</th>)}{onDelete && <th className="px-3 py-2">Action</th>}</tr></thead><tbody>{rows.length ? rows.map((row, index) => <tr key={row.id || row.studentId || index} className="border-t border-slate-100 dark:border-slate-800">{columns.map(column => <td key={column} className="whitespace-nowrap px-3 py-2 text-slate-700 dark:text-slate-300">{text(row[column])}</td>)}{onDelete && <td className="px-3 py-2"><button onClick={() => onDelete(row.id)} className="text-rose-600"><X size={15} /></button></td>}</tr>) : <tr><td colSpan={Math.max(columns.length, 1)} className="px-4 py-10 text-center text-slate-400">No record found.</td></tr>}</tbody></table></div></section> }

function RichText({ value }) {
  const parts = text(value).split(/<\/?br\s*\/?>/i)
  return <>{parts.map((part, index) => <span key={index}>{part}{index < parts.length - 1 && <br />}</span>)}</>
}

function markDisplay(row, exam) {
  const value = text(row?.[exam])
  if (!value) return '-'
  const maximum = text(row?.[`${exam}_max`])
  return maximum && maximum !== '0' ? `${value} / ${maximum}` : value
}

function ReportCardView({ reportCard }) {
  if (!reportCard?.student || !Object.keys(reportCard.student).length) return <section className="rounded-xl bg-white p-10 text-center text-sm text-slate-400 dark:bg-slate-900">No record found.</section>
  const student = reportCard.student
  const subjects = reportCard.subjects || []
  const exams = (reportCard.examColumns || []).filter(item => !['total', 'maxTotal', 'percentage'].includes(item))
  const overall = reportCard.overall || {}
  const attendance = reportCard.attendance || []
  const health = reportCard.health || []
  const indicators = reportCard.indicators || []
  const categoryRemarks = reportCard.categoryRemarks || []
  const learnerProfile = reportCard.learnerProfile || []
  const logo = student.schoolLogo || student.schoolLogoNew
  const title = text(student.schoolName).replace(/<br\s*\/?>(\s*)/ig, ' ')
  return <section className="overflow-hidden rounded-xl border border-slate-300 bg-white text-slate-800 shadow-sm dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100">
    <div className="border-b-2 border-slate-800 px-5 py-4 text-center dark:border-slate-200">
      <div className="flex items-center justify-center gap-4">
        {logo ? <img src={logo} alt="School logo" className="h-16 w-16 object-contain" /> : <div className="flex h-16 w-16 items-center justify-center rounded-full border-2 border-slate-400 text-xl font-bold">{title.split(/\s+/).filter(Boolean).slice(0, 2).map(part => part[0]).join('')}</div>}
        <div><div className="text-xl font-bold uppercase"><RichText value={student.schoolName || title} /></div><div className="mt-1 text-xs uppercase"><RichText value={student.schoolAddress} />{student.schoolAddressLine1 && <> <RichText value={student.schoolAddressLine1} /></>}</div>{student.affiliationNo && <div className="text-xs">{student.affiliationNo}</div>}</div>
      </div>
      <div className="mt-3 text-base font-bold uppercase">REPORT CARD</div>
      <div className="text-sm font-semibold">ACADEMIC SESSION ({text(student.session)})</div>
    </div>

    <div className="grid gap-x-6 gap-y-2 border-b border-slate-300 p-4 text-xs dark:border-slate-700 sm:grid-cols-2 lg:grid-cols-4">
      <Info label="STUDENT'S NAME" value={student.studentName} wide />
      <Info label="CLASS" value={student.className} />
      <Info label="SECTION" value={student.sectionName} />
      <Info label="ADM. NO." value={student.registrationNo} />
      <Info label="DATE OF BIRTH" value={student.dob} />
      <Info label="HOUSE" value={student.house} />
      <Info label="FATHER'S NAME" value={student.fatherName} wide />
      <Info label="MOTHER'S NAME" value={student.motherName} wide />
    </div>

    <div className="p-4"><div className="mb-2 text-center text-sm font-bold uppercase">Scholastic Areas</div><div className="overflow-x-auto"><table className="min-w-full border-collapse text-xs"><thead><tr className="bg-slate-100 dark:bg-slate-800"><th className="border border-slate-400 px-2 py-2 text-left">SUBJECT NAME</th>{exams.map(exam => <th key={exam} className="border border-slate-400 px-2 py-2 text-center">{display(exam)}</th>)}<th className="border border-slate-400 px-2 py-2 text-center">TOTAL</th><th className="border border-slate-400 px-2 py-2 text-center">PERCENTAGE</th><th className="border border-slate-400 px-2 py-2 text-center">GRADE</th></tr></thead><tbody>{subjects.map((row, index) => <tr key={`${row.subjectId}-${index}`}><td className="border border-slate-400 px-2 py-2 font-semibold">{text(row.subject || row.Subject)}</td>{exams.map(exam => <td key={exam} className="border border-slate-400 px-2 py-2 text-center">{markDisplay(row, exam)}</td>)}<td className="border border-slate-400 px-2 py-2 text-center">{text(row.total ?? row.totalTerm)}</td><td className="border border-slate-400 px-2 py-2 text-center">{text(row.percentage ?? row.Per)}%</td><td className="border border-slate-400 px-2 py-2 text-center font-bold">{text(row.Grade || row.subjectGrade)}</td></tr>)}<tr className="font-bold"><td className="border border-slate-400 px-2 py-2 text-right" colSpan={Math.max(1, exams.length + 1)}>GRAND TOTAL</td><td className="border border-slate-400 px-2 py-2 text-center">{text(overall.total)} / {text(overall.maxTotal)}</td><td className="border border-slate-400 px-2 py-2 text-center">{text(overall.percentage)}%</td><td className="border border-slate-400 px-2 py-2 text-center">{text(overall.grade)}</td></tr></tbody></table></div></div>

    <div className="grid gap-4 border-t border-slate-300 p-4 dark:border-slate-700 lg:grid-cols-2">
      <ReportCardSection title="Attendance"><SimpleTable columns={['Term', 'Attendance', 'Working Days', 'Percentage']} rows={attendance.map(item => ({ Term: `Term ${text(item.term)}`, Attendance: item.attendance, 'Working Days': item.workingDays, Percentage: `${text(item.percentage)}%` }))} /></ReportCardSection>
      <ReportCardSection title="Health Status"><SimpleTable columns={['Term', 'Height', 'Weight', 'Blood Group', 'Vision']} rows={health.map(item => ({ Term: `Term ${text(item.termId)}`, Height: item.height ? `${item.height} cm` : '-', Weight: item.weight ? `${item.weight} kg` : '-', 'Blood Group': item.bloodGroup || item.bloodGp || '-', Vision: item.vision || '-' }))} /></ReportCardSection>
    </div>

    {indicators.length > 0 && <div className="border-t border-slate-300 p-4 dark:border-slate-700"><div className="mb-2 text-center text-sm font-bold uppercase">Co-Scholastic Areas</div><SimpleTable columns={['Category', 'Indicator', 'Term 1', 'Term 2']} rows={indicators.map(item => ({ Category: item.indicatorCategory, Indicator: item.indicatorName, 'Term 1': item.term1 || '-', 'Term 2': item.term2 || '-' }))} /></div>}
    {categoryRemarks.length > 0 && <div className="border-t border-slate-300 p-4 dark:border-slate-700"><div className="mb-2 text-center text-sm font-bold uppercase">Report Card Remarks</div><SimpleTable columns={['Category', 'Sub Category', 'Term 1', 'Term 2']} rows={categoryRemarks.map(item => ({ Category: item.categoryName, 'Sub Category': item.subCategoryName, 'Term 1': item.term1 || '-', 'Term 2': item.term2 || '-' }))} /></div>}
    {learnerProfile.length > 0 && <div className="border-t border-slate-300 p-4 dark:border-slate-700"><div className="mb-2 text-center text-sm font-bold uppercase">Learner Profile</div><SimpleTable columns={['Attribute', 'Remark']} rows={learnerProfile.map(item => ({ Attribute: item.attributeName, Remark: item.attributeRemark || '-' }))} /></div>}

    <div className="grid gap-4 border-t border-slate-300 p-6 text-center text-xs dark:border-slate-700 sm:grid-cols-3"><Signature name={student.classTeacherName} image={student.classTeacherSign} /><Signature name={student.principal} image={student.principalSign} /><Signature name={student.designName || student.vicePrincipal} image={student.designSign} /></div>
  </section>
}

function Info({ label, value, wide }) { return <div className={wide ? 'sm:col-span-2' : ''}><span className="font-bold">{label}:</span> <span><RichText value={value || '-'} /></span></div> }
function Signature({ name, image }) { return <div className="flex min-h-16 flex-col items-center justify-end">{image && <img src={image} alt="Signature" className="mb-1 h-8 max-w-32 object-contain" />}<div className="min-h-5 font-semibold"><RichText value={name || ''} /></div></div> }
function ReportCardSection({ title, children }) { return <div><div className="mb-2 text-center text-sm font-bold uppercase">{title}</div>{children}</div> }
function SimpleTable({ columns = [], rows = [] }) { return <div className="overflow-x-auto"><table className="min-w-full border-collapse text-xs"><thead><tr className="bg-slate-100 dark:bg-slate-800">{columns.map(column => <th key={column} className="border border-slate-400 px-2 py-2 text-left">{column}</th>)}</tr></thead><tbody>{rows.length ? rows.map((row, index) => <tr key={index}>{columns.map(column => <td key={column} className="border border-slate-400 px-2 py-2">{text(row[column]) || '-'}</td>)}</tr>) : <tr><td colSpan={Math.max(columns.length, 1)} className="border border-slate-400 px-2 py-3 text-center text-slate-400">-</td></tr>}</tbody></table></div> }

const chartColors = ['#2563eb', '#7c3aed', '#0891b2', '#059669', '#d97706', '#dc2626', '#db2777', '#4f46e5']
function AcademicPerformanceView({ rows = [], graphType = 'column' }) {
  const groups = rows.reduce((all, row) => {
    const key = text(row.examTypeId || row.examType || 'Exam')
    if (!all[key]) all[key] = { name: text(row.examType || key), rows: [] }
    all[key].rows.push({ ...row, marksValue: Number.parseFloat(row.marks) || 0, maxValue: Number.parseFloat(row.maxMarks) || 0 })
    return all
  }, {})
  const exams = Object.values(groups)
  if (!exams.length) return <section className="rounded-xl bg-white p-10 text-center text-sm text-slate-400 dark:bg-slate-900">No record found.</section>
  return <section className="grid gap-4 lg:grid-cols-2">{exams.map((exam, examIndex) => <article key={`${exam.name}-${examIndex}`} className="overflow-hidden rounded-xl bg-white shadow-sm dark:bg-slate-900"><div className="border-b border-slate-100 px-4 py-3 text-sm font-bold text-slate-700 dark:border-slate-800 dark:text-slate-200">{exam.name}</div><div className="h-[320px] p-3"><ResponsiveContainer width="100%" height="100%"><AcademicChart rows={exam.rows} graphType={graphType} color={chartColors[examIndex % chartColors.length]} /></ResponsiveContainer></div></article>)}</section>
}
function MarksStatusGraphView({ rows = [], graphType = 'column' }) {
  return <section className="rounded-xl bg-white p-4 shadow-sm dark:bg-slate-900"><div className="border-b border-slate-100 pb-3 text-sm font-bold text-slate-700 dark:border-slate-800 dark:text-slate-200">Marks Status Subject-wise</div><div className="h-[420px] pt-4"><ResponsiveContainer width="100%" height="100%"><AcademicChart rows={rows} graphType={graphType} color="#2563eb" labelKey="stu_name" valueKey="marks" /></ResponsiveContainer></div></section>
}
function AcademicChart({ rows, graphType, color, labelKey = 'subjectName', valueKey = 'marks' }) {
  const data = rows.map((row, index) => ({ ...row, label: text(row[labelKey] || row.subjectName || row.subject || `Subject ${index + 1}`), marksValue: Number.parseFloat(row[valueKey]) || 0, maxValue: Number.parseFloat(row.maxMarks) || 0, index }))
  const common = { data, margin: { top: 10, right: 18, left: 0, bottom: 45 } }
  if (graphType === 'pie' || graphType === 'doughnut') return <PieChart><Tooltip /><Pie data={data} dataKey="marksValue" nameKey="label" cx="50%" cy="45%" outerRadius={100} innerRadius={graphType === 'doughnut' ? 55 : 0} label>{data.map((_, index) => <Cell key={index} fill={chartColors[index % chartColors.length]} />)}</Pie></PieChart>
  if (graphType === 'line') return <LineChart {...common}><CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="label" angle={-35} textAnchor="end" interval={0} height={65} /><YAxis allowDecimals={false} /><Tooltip /><Line type="monotone" dataKey="marksValue" name="Marks" stroke={color} strokeWidth={2} dot={{ r: 3 }} /></LineChart>
  if (graphType === 'area') return <AreaChart {...common}><CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="label" angle={-35} textAnchor="end" interval={0} height={65} /><YAxis allowDecimals={false} /><Tooltip /><Area type="monotone" dataKey="marksValue" name="Marks" stroke={color} fill={color} fillOpacity={0.22} /></AreaChart>
  if (graphType === 'scatter' || graphType === 'bubble') return <ScatterChart {...common}><CartesianGrid /><XAxis type="number" dataKey="index" name="Subject" tickFormatter={value => data[value]?.label || value} /><YAxis type="number" dataKey="marksValue" name="Marks" /><ZAxis type="number" dataKey={graphType === 'bubble' ? 'maxValue' : 'marksValue'} range={graphType === 'bubble' ? [80, 500] : [60, 60]} /><Tooltip cursor={{ strokeDasharray: '3 3' }} /><Scatter data={data} fill={color} /></ScatterChart>
  if (graphType === 'bar') return <BarChart layout="vertical" {...common} margin={{ top: 10, right: 18, left: 80, bottom: 10 }}><CartesianGrid strokeDasharray="3 3" /><XAxis type="number" allowDecimals={false} /><YAxis type="category" dataKey="label" width={75} /><Tooltip /><Bar dataKey="marksValue" name="Marks" fill={color} /></BarChart>
  return <BarChart {...common}><CartesianGrid strokeDasharray="3 3" /><XAxis dataKey="label" angle={-35} textAnchor="end" interval={0} height={65} /><YAxis allowDecimals={false} /><Tooltip /><Bar dataKey="marksValue" name="Marks" fill={color} /></BarChart>
}

export { REPORTS }
