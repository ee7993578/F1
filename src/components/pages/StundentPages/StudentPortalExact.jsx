import { useCallback, useEffect, useMemo, useState } from 'react'
import { useAuth } from '../../../auth/AuthContext'
import { apiFetch } from '../../../lib/api'
import { downloadExcel } from '../../../lib/exportExcel'

const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'

const TITLES = {
  dashboard: 'Student Dashboard', syllabus: 'Syllabus', circulars: 'Circular',
  holidayHomework: 'Holiday Homework', dailyHomework: 'Daily Homework', assignments: 'Assignment',
  fees: 'Fees', attendance: 'Attendance Report', timetable: 'TimeTable Report',
  examSchedule: 'Exam Schedule', academicPerformance: 'Student performance',
}

const ENDPOINTS = {
  syllabus: '/student-portal/syllabus', circulars: '/student-portal/circulars',
  holidayHomework: '/student-portal/holiday-homework', assignments: '/student-portal/assignments',
  fees: '/student-portal/fees', attendance: '/student-portal/attendance', timetable: '/student-portal/timetable',
  examSchedule: '/student-portal/exam-schedule', academicPerformance: '/student-portal/academic-performance',
}

const clean = value => String(value ?? '').trim()
const number = value => Number.parseFloat(value) || 0
const dateToday = () => new Date().toISOString().slice(0, 10)

function fileUrl(path) {
  const value = clean(path)
  if (!value) return ''
  if (/^https?:\/\//i.test(value)) return value
  return `${API_BASE}/student-portal/file?path=${encodeURIComponent(value.replace(/^~\//, ''))}`
}

function DownloadLink({ path, label = 'Download' }) {
  const href = fileUrl(path)
  if (!href) return <span className="text-slate-400">—</span>
  return <a className="inline-flex items-center rounded-lg border border-indigo-200 px-3 py-1.5 text-xs font-semibold text-indigo-600 hover:bg-indigo-50 dark:border-indigo-400/30 dark:text-indigo-300 dark:hover:bg-indigo-500/10" href={href} target="_blank" rel="noreferrer">{label}</a>
}

function Loading({ label = 'Loading student data…' }) {
  return <div className="absolute inset-0 z-10 flex min-h-[180px] items-center justify-center rounded-2xl bg-white/75 backdrop-blur-sm dark:bg-slate-950/75"><div className="flex items-center gap-3 rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium text-slate-600 shadow-lg dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200"><span className="h-5 w-5 animate-spin rounded-full border-2 border-indigo-200 border-t-indigo-600" />{label}</div></div>
}

function Empty({ message = 'No records found.' }) {
  return <div className="rounded-xl border border-dashed border-slate-300 px-5 py-10 text-center text-sm text-slate-500 dark:border-slate-700 dark:text-slate-400">{message}</div>
}

function Card({ title, children, actions }) {
  return <section className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm dark:border-slate-700 dark:bg-slate-900 sm:p-5"><div className="mb-4 flex flex-wrap items-center justify-between gap-3"><h2 className="text-base font-bold text-slate-800 dark:text-slate-100">{title}</h2>{actions}</div>{children}</section>
}

function Table({ rows, columns, rowKey }) {
  if (!rows?.length) return <Empty />
  return <>
    <div className="hidden overflow-x-auto rounded-xl border border-slate-200 dark:border-slate-700 sm:block">
      <table className="min-w-full text-left text-sm"><thead className="bg-slate-50 text-xs uppercase tracking-wide text-slate-500 dark:bg-slate-800/80 dark:text-slate-400"><tr>{columns.map(column => <th key={column.key} className="whitespace-nowrap px-3 py-3 font-semibold">{column.label}</th>)}</tr></thead><tbody className="divide-y divide-slate-100 dark:divide-slate-800">{rows.map((row, index) => <tr key={rowKey ? rowKey(row, index) : index} className="align-top hover:bg-slate-50/80 dark:hover:bg-slate-800/40">{columns.map(column => <td key={column.key} className="whitespace-nowrap px-3 py-3 text-slate-700 dark:text-slate-200">{column.render ? column.render(row, index) : clean(row[column.key]) || '—'}</td>)}</tr>)}</tbody></table>
    </div>
    <div className="space-y-3 sm:hidden">
      {rows.map((row, index) => <article key={rowKey ? rowKey(row, index) : index} className="rounded-xl border border-slate-200 bg-slate-50/70 p-3 dark:border-slate-700 dark:bg-slate-800/50">
        <dl className="grid gap-2">
          {columns.map(column => <div key={column.key} className="flex items-start justify-between gap-4 border-b border-slate-200/70 pb-2 last:border-0 last:pb-0 dark:border-slate-700">
            <dt className="shrink-0 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400">{column.label}</dt>
            <dd className="min-w-0 text-right text-sm font-medium text-slate-700 dark:text-slate-200">{column.render ? column.render(row, index) : clean(row[column.key]) || '—'}</dd>
          </div>)}
        </dl>
      </article>)}
    </div>
  </>
}

function StudentSelector({ students, value, onChange }) {
  if (!students?.length) return null
  return <label className="flex min-w-[220px] flex-1 flex-col gap-1 text-xs font-semibold text-slate-500 dark:text-slate-400"><span>Student</span><select value={value || students[0]?.studentId || ''} onChange={event => onChange(event.target.value)} className="rounded-xl border border-slate-300 bg-white px-3 py-2.5 text-sm font-medium text-slate-700 outline-none focus:border-indigo-500 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-100">{students.map(student => <option key={student.studentId} value={student.studentId}>{student.studentName} · {student.className}{student.section ? ` (${student.section})` : ''}</option>)}</select></label>
}

function Profile({ student }) {
  if (!student) return null
  const fields = [['Name', student.name], ['Gender', student.gender], ['Roll', student.rollNo], ['Admission Id', student.registrationNo], ['Admission Date', student.admissionDate], ['Class', student.classSection], ['Class Teacher', student.classTeacher]]
  return <Card title="Profile"><div className="grid gap-5 md:grid-cols-[150px_1fr]"><div className="flex flex-col items-center gap-3"><div className="flex h-28 w-28 items-center justify-center overflow-hidden rounded-2xl bg-indigo-100 text-3xl font-bold text-indigo-700 dark:bg-indigo-500/15 dark:text-indigo-200">{student.imagePath ? <img src={student.imagePath} alt={student.name || 'Student'} className="h-full w-full object-cover" /> : clean(student.name).split(/\s+/).map(word => word[0]).join('').slice(0, 2).toUpperCase()}</div><div className="text-center text-xs text-slate-500 dark:text-slate-400">{student.mobile || student.phone || '—'}</div></div><dl className="grid gap-x-5 gap-y-3 sm:grid-cols-2">{fields.map(([label, value]) => <div key={label} className="border-b border-slate-100 pb-2 dark:border-slate-800"><dt className="text-xs font-semibold uppercase tracking-wide text-slate-400">{label}</dt><dd className="mt-1 text-sm font-medium text-slate-700 dark:text-slate-200">{clean(value) || '—'}</dd></div>)}</dl></div></Card>
}

function ContentReport({ mode, rows }) {
  const config = {
    syllabus: { title: 'Syllabus', columns: [['title', 'Title'], ['subjectName', 'Subject'], ['filePath', 'File', row => <DownloadLink path={row.filePath} />]] },
    circulars: { title: 'Circular', columns: [['circularDate', 'Date'], ['title', 'Title'], ['filePath', 'File', row => <DownloadLink path={row.filePath} />]] },
    holidayHomework: { title: 'Holiday Homework', columns: [['title', 'Title'], ['submissionDate', 'Submission Date'], ['filePath', 'File', row => <DownloadLink path={row.filePath} />]] },
    assignments: { title: 'Assignment', columns: [['postedDate', 'Posted Date'], ['message', 'Message'], ['submissionDate', 'Submission Date'], ['readStatus', 'Status'], ['filePath', 'File', row => <DownloadLink path={row.filePath} />]] },
  }[mode]
  const columns = (config?.columns || []).map(([key, label, render]) => ({ key, label, render }))
  return <Card title={config?.title || TITLES[mode]} actions={<button onClick={() => downloadExcel(`${mode}.xls`, rows, columns)} disabled={!rows?.length} className="rounded-lg border border-emerald-200 px-3 py-2 text-xs font-bold text-emerald-700 disabled:cursor-not-allowed disabled:opacity-40 dark:border-emerald-400/30 dark:text-emerald-300">Excel</button>}><Table rows={rows} columns={columns} /></Card>
}

function FeesPage({ data }) {
  const rows = data?.rows || []
  const transactions = data?.transactions || []
  const [message, setMessage] = useState('')
  const columns = [
    { key: 'installmentNo', label: 'Inst No.' }, { key: 'months', label: 'Installment' }, { key: 'installmentAmount', label: 'Amount', render: row => number(row.installmentAmount).toFixed(2) },
    { key: 'concession', label: 'Concession', render: row => number(row.concession).toFixed(2) }, { key: 'payable', label: 'Payable', render: row => number(row.payable).toFixed(2) },
    { key: 'lateFee', label: 'Late Fee', render: row => number(row.lateFee).toFixed(2) }, { key: 'balanceWithLateFee', label: 'Due / Balance', render: row => number(row.balanceWithLateFee).toFixed(2) },
    { key: 'paid', label: 'Paid', render: row => number(row.paid).toFixed(2) }, { key: 'dueDate', label: 'Due Date' },
    { key: 'status', label: 'Action', render: row => row.status === 'Pay now' ? <button onClick={async () => { try { const result = await apiFetch(`/student-portal/payment-url?studentId=${encodeURIComponent(data?.student?.studentId || '')}&installmentNo=${encodeURIComponent(row.installmentNo)}`); window.open(result.url, '_blank', 'noopener,noreferrer') } catch (cause) { setMessage(cause.message || 'Unable to open payment gateway.') } }} className="rounded-lg bg-indigo-600 px-3 py-1.5 text-xs font-bold text-white hover:bg-indigo-700">Pay Now</button> : <span className={row.status === 'Paid' ? 'font-semibold text-emerald-600' : 'font-semibold text-amber-600'}>{row.status}</span> },
  ]
  const transactionColumns = [{ key: 'installmentNo', label: 'Inst No.' }, { key: 'receiptNo', label: 'Receipt No.' }, { key: 'paymentDate', label: 'Receipt Date' }, { key: 'mop', label: 'MOP' }, { key: 'amount', label: 'Amount' }, { key: 'lateFee', label: 'Late Fee' }]
  return <div className="space-y-4"><Profile student={data?.student} />{message && <div className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800 dark:border-amber-400/30 dark:bg-amber-500/10 dark:text-amber-200">{message}</div>}<Card title="Fee Structure" actions={<button onClick={() => downloadExcel('student-fee-structure.xls', rows, columns)} disabled={!rows.length} className="rounded-lg border border-emerald-200 px-3 py-2 text-xs font-bold text-emerald-700 disabled:opacity-40 dark:border-emerald-400/30 dark:text-emerald-300">Excel</button>}><Table rows={rows} columns={columns} rowKey={row => row.installmentNo} /></Card><Card title="Previous Transaction"><Table rows={transactions} columns={transactionColumns} /></Card></div>
}

function AttendancePage({ data }) {
  const rows = data?.rows || []
  const [detail, setDetail] = useState([])
  const [selected, setSelected] = useState('')
  return <div className="space-y-4"><Card title="Attendance Report" actions={<button onClick={() => downloadExcel('attendance-report.xls', rows)} disabled={!rows.length} className="rounded-lg border border-emerald-200 px-3 py-2 text-xs font-bold text-emerald-700 disabled:opacity-40 dark:border-emerald-400/30 dark:text-emerald-300">Excel</button>}><Table rows={rows} columns={[{ key: 'monthName', label: 'Month' }, { key: 'att', label: 'Attendance' }, { key: 'present', label: 'Present' }, { key: 'absent', label: 'Absent' }, { key: 'total', label: 'Total' }, { key: 'percentage', label: 'Percentage', render: row => <button onClick={async () => { setSelected(row.monthName); setDetail((await apiFetch(`/student-portal/attendance/detail?monthId=${encodeURIComponent(row.monthId)}`)).rows || []) }} className="font-semibold text-indigo-600 underline dark:text-indigo-300">{row.percentage || '0 %'}</button> }]} /></Card>{selected && <Card title={`Attendance detail · ${selected}`}><Table rows={detail} columns={[{ key: 'attendanceDate', label: 'Date' }, { key: 'present', label: 'Present' }, { key: 'absent', label: 'Absent' }]} /></Card>}</div>
}

function TimetablePage({ rows }) {
  return <Card title="TimeTable Report" actions={<button onClick={() => downloadExcel('student-timetable.xls', rows)} disabled={!rows?.length} className="rounded-lg border border-emerald-200 px-3 py-2 text-xs font-bold text-emerald-700 disabled:opacity-40 dark:border-emerald-400/30 dark:text-emerald-300">Excel</button>}><Table rows={rows} columns={[{ key: 'day', label: 'Day' }, { key: 'period', label: 'Period' }, { key: 'subject', label: 'Subject' }]} /></Card>
}

function ExamSchedulePage({ rows }) {
  return <Card title="Exam Schedule" actions={<button onClick={() => downloadExcel('exam-schedule.xls', rows)} disabled={!rows?.length} className="rounded-lg border border-emerald-200 px-3 py-2 text-xs font-bold text-emerald-700 disabled:opacity-40 dark:border-emerald-400/30 dark:text-emerald-300">Excel</button>}><Table rows={rows} columns={[{ key: 'termName', label: 'Term' }, { key: 'examType', label: 'Exam' }, { key: 'examDate', label: 'Date' }, { key: 'subjectName', label: 'Subject' }, { key: 'maxMarks', label: 'Max Marks' }]} /></Card>
}

function AcademicPerformancePage({ rows }) {
  const [view, setView] = useState('table')
  const terms = useMemo(() => [...new Set((rows || []).map(row => clean(row.termId)).filter(Boolean))], [rows])
  return <Card title="Academic Performance" actions={<div className="flex gap-2"><select value={view} onChange={event => setView(event.target.value)} className="rounded-lg border border-slate-300 bg-white px-2 py-2 text-xs dark:border-slate-600 dark:bg-slate-800"><option value="table">Table</option><option value="bars">Bars</option></select><button onClick={() => downloadExcel('academic-performance.xls', rows)} disabled={!rows?.length} className="rounded-lg border border-emerald-200 px-3 py-2 text-xs font-bold text-emerald-700 disabled:opacity-40 dark:border-emerald-400/30 dark:text-emerald-300">Excel</button></div>}><div className="mb-4 flex flex-wrap gap-2 text-xs text-slate-500 dark:text-slate-400">{terms.length ? `Terms: ${terms.join(', ')}` : 'Marks shown from the selected student record.'}</div>{view === 'bars' && rows?.length ? <div className="grid gap-3 sm:grid-cols-2">{rows.map((row, index) => { const percent = number(row.maxMarks) ? Math.max(0, Math.min(100, number(row.marks) * 100 / number(row.maxMarks))) : 0; return <div key={`${row.subjectId}-${row.examTypeId}-${index}`} className="rounded-xl border border-slate-200 p-3 dark:border-slate-700"><div className="flex justify-between gap-3 text-sm font-semibold"><span>{row.subjectName || 'Subject'}</span><span>{number(row.marks)} / {number(row.maxMarks)}</span></div><div className="mt-2 h-2 rounded-full bg-slate-100 dark:bg-slate-800"><div className="h-2 rounded-full bg-indigo-500" style={{ width: `${percent}%` }} /></div><div className="mt-1 text-xs text-slate-500">{row.examType || 'Exam'}</div></div> })}</div> : <Table rows={rows} columns={[{ key: 'examType', label: 'Exam Type' }, { key: 'subjectName', label: 'Subject' }, { key: 'marks', label: 'Marks' }, { key: 'maxMarks', label: 'Max Marks' }, { key: 'weightageMarks', label: 'Weightage' }]} />}</Card>
}

function Dashboard({ data }) {
  const student = data?.student
  const fees = data?.fees || []
  const attendance = data?.attendance || []
  return <div className="space-y-4"><Profile student={student} /><div className="grid gap-4 xl:grid-cols-2"><Card title="Attendance"><Table rows={attendance.slice(-6)} columns={[{ key: 'monthName', label: 'Month' }, { key: 'att', label: 'Attendance' }, { key: 'percentage', label: '%' }]} /></Card><Card title="Fee Summary"><Table rows={fees} columns={[{ key: 'installmentNo', label: 'Inst.' }, { key: 'months', label: 'Installment' }, { key: 'payable', label: 'Payable' }, { key: 'paid', label: 'Paid' }, { key: 'balance', label: 'Balance' }, { key: 'status', label: 'Status' }]} /></Card></div><div className="grid gap-4 xl:grid-cols-2"><Card title="Circulars"><Table rows={(data?.circulars || []).slice(0, 6)} columns={[{ key: 'circularDate', label: 'Date' }, { key: 'title', label: 'Title' }, { key: 'filePath', label: 'File', render: row => <DownloadLink path={row.filePath} /> }]} /></Card><Card title="Latest Events"><Table rows={(data?.events || []).slice(0, 6)} columns={[{ key: 'eventDate', label: 'Date' }, { key: 'name', label: 'Event' }, { key: 'description', label: 'Description' }]} /></Card></div></div>
}

export default function StudentPortalExact({ mode = 'dashboard' }) {
  const { user } = useAuth()
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedStudent, setSelectedStudent] = useState('')
  const [homeworkDate, setHomeworkDate] = useState(dateToday())

  const load = useCallback(async (studentId = selectedStudent, extra = '') => {
    setLoading(true); setError('')
    try {
      if (mode === 'dashboard') setData(await apiFetch(`/student-portal/bootstrap${studentId ? `?studentId=${encodeURIComponent(studentId)}` : ''}`))
      else if (mode === 'dailyHomework') {
        const [page, people] = await Promise.all([
          apiFetch(`/student-portal/daily-homework?date=${encodeURIComponent(extra || homeworkDate)}${studentId ? `&studentId=${encodeURIComponent(studentId)}` : ''}`),
          apiFetch('/student-portal/students'),
        ])
        setData({ ...page, students: people.students || [] })
      } else {
        const query = studentId ? `?studentId=${encodeURIComponent(studentId)}` : ''
        const [page, people] = await Promise.all([apiFetch(`${ENDPOINTS[mode]}${query}`), apiFetch('/student-portal/students')])
        setData({ ...page, students: people.students || [] })
      }
    } catch (cause) { setError(cause.message || 'Unable to load this student page.') } finally { setLoading(false) }
  }, [homeworkDate, mode, selectedStudent])

  useEffect(() => { load() }, [mode]) // eslint-disable-line react-hooks/exhaustive-deps

  const students = data?.students || []
  const onStudentChange = value => { setSelectedStudent(value); load(value) }
  const title = TITLES[mode] || 'Student'
  let body = null
  if (mode === 'dashboard') body = <Dashboard data={data} />
  else if (mode === 'fees') body = <FeesPage data={data} />
  else if (mode === 'attendance') body = <AttendancePage data={data} />
  else if (mode === 'timetable') body = <TimetablePage rows={data?.rows || []} />
  else if (mode === 'examSchedule') body = <ExamSchedulePage rows={data?.rows || []} />
  else if (mode === 'academicPerformance') body = <AcademicPerformancePage rows={data?.rows || []} />
  else if (mode === 'dailyHomework') body = <Card title="Daily Homework" actions={<div className="flex flex-wrap gap-2"><input type="date" value={homeworkDate} onChange={event => setHomeworkDate(event.target.value)} className="rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm dark:border-slate-600 dark:bg-slate-800" /><button onClick={() => load(selectedStudent, homeworkDate)} className="rounded-lg bg-indigo-600 px-3 py-2 text-xs font-bold text-white">Show</button><button onClick={() => downloadExcel('daily-homework.xls', data?.rows || [])} disabled={!data?.rows?.length} className="rounded-lg border border-emerald-200 px-3 py-2 text-xs font-bold text-emerald-700 disabled:opacity-40 dark:border-emerald-400/30 dark:text-emerald-300">Excel</button></div>}><Table rows={data?.rows || []} columns={[{ key: 'homeworkDate', label: 'Date' }, { key: 'subject', label: 'Subject' }, { key: 'homework', label: 'Homework' }, { key: 'filePath', label: 'File', render: row => <DownloadLink path={row.filePath} /> }]} /></Card>
  else body = <ContentReport mode={mode} rows={data?.rows || []} />

  return <div className="relative min-h-[300px] space-y-4 text-slate-800 dark:text-slate-100"><div className="flex flex-wrap items-end justify-between gap-3"><div><div className="text-xs font-semibold uppercase tracking-wider text-indigo-500">Student Portal</div><h1 className="mt-1 text-xl font-bold">{title}</h1><p className="mt-1 text-sm text-slate-500 dark:text-slate-400">{user?.username || 'Student'} · {data?.session || 'Current session'}</p></div><StudentSelector students={students} value={selectedStudent} onChange={onStudentChange} /></div>{error ? <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700 dark:border-rose-400/30 dark:bg-rose-500/10 dark:text-rose-200">{error}<button onClick={() => load()} className="ml-3 font-bold underline">Retry</button></div> : body}{loading && <Loading />}</div>
}
