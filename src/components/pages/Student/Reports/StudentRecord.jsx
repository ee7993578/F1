/**
 * StudentRecord.jsx
 * Folder: src/components/pages/Student/Reports/StudentRecord.jsx
 *
 * Legacy: registration/Studentrecord.aspx (+ Studentrecord.rdlc)
 * Flow (same as old):
 *  - Page_Load: bindsession proc -> session dropdown (currentSession default),
 *    tb_class + class_name -> class/section dropdown
 *  - ddlclass_SelectedIndexChanged: EXEC ddlstudent @school_id,@sec_id,@session
 *    -> student dropdown (Name, registration_no)
 *  - btnsubmit: EXEC bind_studentrecord @session,@school_id,@sec_id,@adm_no
 *    -> single student registration record (print view)
 */

import { useState, useCallback, useEffect } from 'react'
import { fetchStudentDetails } from '../../../../hooks/useStudents'
import {
  ChevronDown, AlertCircle, X, Check, Loader2,
  RefreshCw, Eye, Printer, GraduationCap, ClipboardList,
  Info, School2, User, Hash,
} from 'lucide-react'

function NativeSelect({ value, onChange, children, placeholder, error, disabled }) {
  return (
    <div className="relative">
      <select
        value={value}
        onChange={onChange}
        disabled={disabled}
        className={`w-full appearance-none pl-3 pr-8 py-2 text-[13px] rounded-lg border outline-none transition-all cursor-pointer
          bg-white text-slate-800 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
          dark:bg-[#1e2238] dark:text-slate-200 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
          disabled:opacity-50 disabled:cursor-not-allowed
          ${error ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {children}
      </select>
      <ChevronDown className="absolute right-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
    </div>
  )
}

function Field({ label, error, required, children }) {
  return (
    <div className="flex flex-col gap-1">
      <label className="text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">
        {label}{required && <span className="text-rose-500 ml-0.5">*</span>}
      </label>
      {children}
      {error && (
        <p className="flex items-center gap-1 text-[11px] text-rose-500 mt-0.5">
          <AlertCircle className="w-3 h-3 flex-shrink-0" />{error}
        </p>
      )}
    </div>
  )
}

function Toast({ message, type = 'success', onClose }) {
  return (
    <div className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3 rounded-2xl shadow-xl text-[13px] font-semibold min-w-[260px] max-w-[90vw] ${type === 'success' ? 'bg-emerald-600 text-white' : 'bg-rose-600 text-white'}`}>
      {type === 'success' ? <Check className="w-4 h-4 flex-shrink-0" /> : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75 hover:opacity-100" /></button>
    </div>
  )
}

const pick = (obj, ...keys) => {
  for (const k of keys) {
    if (obj[k] !== undefined && obj[k] !== null && String(obj[k]).trim() !== '') return obj[k]
  }
  return ''
}

export default function StudentRecord() {
  const [sessions, setSessions] = useState([])
  const [session, setSession] = useState('')
  const [sections, setSections] = useState([])
  const [secId, setSecId] = useState('')
  const [students, setStudents] = useState([])
  const [admNo, setAdmNo] = useState('')
  const [record, setRecord] = useState(null)
  const [loading, setLoading] = useState(false)
  const [loadingStudents, setLoadingStudents] = useState(false)
  const [errors, setErrors] = useState({})
  const [toast, setToast] = useState(null)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // Page_Load: bindsession + currentSession + tb_class/class_name (sections-all)
  useEffect(() => {
    (async () => {
      try {
        const [sess, me] = await Promise.all([
          api('/master/sessions').catch(() => []),
          api('/menu/session').catch(() => null),
        ])
        const list = (sess || []).map((s) => s.session || s.session_name || s).filter(Boolean)
        setSessions(list)
        const cur = me?.session || list[0] || ''
        if (cur) setSession(cur)
      } catch { /* keep empty */ }
      try {
        const secs = await api('/admission/sections-all')
        setSections(secs || [])
      } catch {
        setSections([])
      }
    })()
  }, [])

  // ddlclass_SelectedIndexChanged -> ddlstudent proc via /ddl-students
  useEffect(() => {
    if (!secId || !session) { setStudents([]); setAdmNo(''); setRecord(null); return }
    (async () => {
      setLoadingStudents(true)
      try {
        const rows = await api(`/admission/ddl-students?session=${encodeURIComponent(session)}&secId=${encodeURIComponent(secId)}`)
        setStudents(rows || [])
      } catch (e) {
        setStudents([])
        showToast(e.message || 'Failed to load students', 'error')
      } finally {
        setLoadingStudents(false)
      }
    })()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [secId, session])

  const handleSessionChange = (v) => {
    setSession(v); setSecId(''); setAdmNo(''); setRecord(null); setStudents([])
    setErrors((p) => ({ ...p, session: undefined }))
  }
  const handleSecChange = (v) => {
    setSecId(v); setAdmNo(''); setRecord(null)
    setErrors((p) => ({ ...p, sec: undefined }))
  }

  // btnsubmit -> bind_studentrecord proc via /student-record?session&secId&admNo
  const handleSubmit = useCallback(() => {
    const err = {}; if (!session) err.session = 'Please select a session'; if (!cls) err.cls = 'Please select a class'; if (!studentId) err.student = 'Please select a student'; if (Object.keys(err).length) { setErrors(err); return }
    setErrors({}); setLoading(true)
    fetchStudentDetails(studentId).then(stu => { setStudent(stu); setReport({ student: stu, marks: [], attendance: [], coscholastic: [] }); setLoading(false); setActiveTab('marks'); showToast('Report Card loaded for ' + (stu?.name || 'student') + '.') }).catch(err => { setLoading(false); showToast(err.message || 'Request failed', 'error') })
  }, [session, cls, studentId])

  const handleReset = () => {
    setSecId(''); setAdmNo(''); setRecord(null); setStudents([]); setErrors({})
  }

  const secLabel = (s) => s.class_name || s.class || s.Section || s.section || s.sec_id
  const stuName = (s) => pick(s, 'Name', 'name', 'StudentName', 'student_name')
  const stuReg = (s) => pick(s, 'registration_no', 'Registration_no', 'Registration_NO', 'admNo', 'reg_no')

  const entries = record ? Object.entries(record).filter(([, v]) => v !== null && v !== undefined && String(v).trim() !== '') : []
  const title_name = record ? pick(record, 'Name', 'name', 'First_name', 'first_name', 'StudentName') : ''
  const title_adm = record ? pick(record, 'registration_no', 'Registration_NO', 'admNo', 'adm_no', 'form_no') : admNo
  const title_cls = record ? pick(record, 'class', 'class_name', 'Class') : ''

  return (
    <div className="space-y-4 pb-10">
      <div>
        <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
          <GraduationCap className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
          Student Record
        </h1>
        <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
          Session → Class → Student select karke registration record dekhein (bind_studentrecord).
        </p>
      </div>

      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <ClipboardList className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Select Criteria</span>
        </div>
        <div className="p-5">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 items-end">
            <Field label="Session" error={errors.session} required>
              <NativeSelect value={session} onChange={(e) => handleSessionChange(e.target.value)} placeholder="-- Select Session --" error={errors.session}>
                {sessions.map((s) => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Class" error={errors.sec} required>
              <NativeSelect value={secId} onChange={(e) => handleSecChange(e.target.value)} placeholder="-- Select Class --" error={errors.sec} disabled={!session}>
                {sections.map((s) => <option key={s.sec_id} value={s.sec_id}>{secLabel(s)}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Student" error={errors.adm} required>
              <NativeSelect value={admNo} onChange={(e) => setAdmNo(e.target.value)} placeholder={loadingStudents ? '-- Loading... --' : '-- Select Student --'} error={errors.adm} disabled={!secId || loadingStudents}>
                {students.map((s, i) => {
                  const r = stuReg(s)
                  return <option key={`${r}-${i}`} value={r}>{stuName(s)} ({r})</option>
                })}
              </NativeSelect>
            </Field>
            <div className="flex gap-2">
              <button type="button" onClick={handleSubmit} disabled={loading}
                className="flex-1 flex items-center justify-center gap-2 py-2 rounded-xl text-[13px] font-semibold text-white bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700 transition-all active:scale-95 disabled:opacity-70">
                {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />} Show
              </button>
              <button type="button" onClick={handleReset}
                className="flex items-center justify-center px-3 py-2 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors" title="Reset">
                <RefreshCw className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
          {!session && (
            <div className="mt-3 flex items-center gap-2 text-[12px] text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-500/10 rounded-xl px-4 py-2.5 border border-blue-100 dark:border-blue-500/20">
              <Info className="w-3.5 h-3.5 flex-shrink-0" />
              Pehle Session, phir Class, phir Student select karke Show dabayein.
            </div>
          )}
        </div>
      </div>

      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          <div className="h-20 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" />
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-10 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.12 }} />
          ))}
        </div>
      )}

      {record && !loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
          <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-4 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-gradient-to-r from-blue-50 via-white to-indigo-50 dark:from-[#1a1f35] dark:via-[#1e2238] dark:to-[#1a1f35]">
            <div className="flex items-center gap-3 flex-1 min-w-0">
              <span className="w-11 h-11 rounded-xl bg-blue-100 dark:bg-blue-500/15 flex items-center justify-center flex-shrink-0">
                <User className="w-5 h-5 text-blue-600 dark:text-blue-400" />
              </span>
              <div className="min-w-0">
                <p className="text-[15px] font-bold text-slate-800 dark:text-slate-100 truncate">{title_name || 'Student Record'}</p>
                <p className="text-[12px] text-slate-500 dark:text-slate-400 flex items-center gap-2 flex-wrap">
                  <span className="inline-flex items-center gap-1"><Hash className="w-3 h-3" />{title_adm}</span>
                  {title_cls && <span>· {title_cls}</span>}
                  <span>· {session}</span>
                </p>
              </div>
            </div>
            <button type="button" onClick={() => window.print?.()}
              className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold bg-slate-900 text-white hover:bg-slate-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 transition-colors flex-shrink-0">
              <Printer className="w-4 h-4" /> Print
            </button>
          </div>
          <div className="p-5 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
            {entries.map(([k, v]) => (
              <div key={k} className="rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/60 dark:bg-white/[0.02] px-3 py-2 min-w-0">
                <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 dark:text-slate-500 truncate">{k}</p>
                <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 break-words">{String(v)}</p>
              </div>
            ))}
          </div>
          <div className="px-5 py-3 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex items-center gap-2 text-[12px] text-slate-400 dark:text-slate-500">
            <School2 className="w-3.5 h-3.5" />
            Source: bind_studentrecord @session, @school_id, @sec_id, @adm_no (same as legacy).
          </div>
        </div>
      )}

      {!record && !loading && (
        <div className="flex flex-col items-center justify-center py-14 gap-3 text-slate-400 dark:text-slate-600">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
            <GraduationCap className="w-7 h-7 opacity-50" />
          </div>
          <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No record loaded</p>
          <p className="text-[12px]">Session, Class aur Student select karke Show dabayein.</p>
        </div>
      )}

      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
