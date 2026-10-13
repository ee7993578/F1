/**
 * UploadTC.jsx
 * Folder: src/pages/Certificates/UploadTC.jsx
 *
 * Transfer Certificate Upload Page
 * - Session dropdown
 * - TC No input
 * - Admission No with autocomplete → auto-fills student name
 * - File upload (PDF / image)
 * - Save + Excel Export
 * - Grid: S.No, TC No, Adm No, Student Name, Download TC
 * - Mobile: card view with download button
 * - Desktop: clean ERP table
 */

import { useState, useMemo, useCallback, useRef } from 'react'
import {
  Upload, FileText, Search, RefreshCw, Eye,
  AlertCircle, X, Check, Loader2, ChevronDown,
  Download, User, Hash, Calendar,
  FileSpreadsheet, School2, SlidersHorizontal,
  CloudUpload, Trash2, File, CheckCircle2,
  BookOpen, Filter, Info, TrendingUp
} from 'lucide-react'

// ─── STATIC DATA ──────────────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const STUDENT_DB = {
  '2024-001': { name: 'Aarav Sharma',      class: 'Class X-A'   },
  '2024-002': { name: 'Priya Verma',       class: 'Class XII-B' },
  '2024-003': { name: 'Rohan Gupta',       class: 'Class IX-A'  },
  '2024-004': { name: 'Sneha Patel',       class: 'Class XI-A'  },
  '2024-005': { name: 'Arjun Singh',       class: 'Class VIII-A'},
  '2023-101': { name: 'Kavya Reddy',       class: 'Class X-B'   },
  '2023-102': { name: 'Mohit Jain',        class: 'Class XII-A' },
  '2023-103': { name: 'Ananya Mishra',     class: 'Class VII-A' },
  '2025-001': { name: 'Vikas Tiwari',      class: 'Class VI-A'  },
  '2025-002': { name: 'Neha Yadav',        class: 'Class IX-B'  },
}

const SAMPLE_UPLOADED = [
  { id: 1, tc_no: 'TC-2024-001', admission_no: '2024-001', name: 'Aarav Sharma',    class: 'Class X-A',    session: '2024-25', file_path: '#', file_name: 'TC_Aarav_Sharma.pdf'   },
  { id: 2, tc_no: 'TC-2024-002', admission_no: '2024-002', name: 'Priya Verma',     class: 'Class XII-B',  session: '2024-25', file_path: '#', file_name: 'TC_Priya_Verma.pdf'    },
  { id: 3, tc_no: 'TC-2023-101', admission_no: '2023-101', name: 'Kavya Reddy',     class: 'Class X-B',    session: '2023-24', file_path: '#', file_name: 'TC_Kavya_Reddy.pdf'    },
  { id: 4, tc_no: 'TC-2023-102', admission_no: '2023-102', name: 'Mohit Jain',      class: 'Class XII-A',  session: '2023-24', file_path: '#', file_name: 'TC_Mohit_Jain.pdf'     },
  { id: 5, tc_no: 'TC-2024-003', admission_no: '2024-003', name: 'Rohan Gupta',     class: 'Class IX-A',   session: '2024-25', file_path: '#', file_name: 'TC_Rohan_Gupta.pdf'    },
]

// ─── HELPERS ──────────────────────────────────────────────────────────────────
const AVATAR_COLORS = [
  { bg: '#dbeafe', fg: '#1d4ed8' },
  { bg: '#d1fae5', fg: '#059669' },
  { bg: '#ede9fe', fg: '#7c3aed' },
  { bg: '#fef3c7', fg: '#d97706' },
  { bg: '#fce7f3', fg: '#db2777' },
  { bg: '#cffafe', fg: '#0891b2' },
]
const avatarColor = (name = '') => AVATAR_COLORS[name.charCodeAt(0) % AVATAR_COLORS.length]
const initials = (name = '') =>
  name.split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase()

// ─── PRIMITIVE COMPONENTS ─────────────────────────────────────────────────────
function NativeSelect({ value, onChange, children, placeholder, error, disabled }) {
  return (
    <div className="relative">
      <select
        value={value}
        onChange={onChange}
        disabled={disabled}
        className={`w-full appearance-none pl-3 pr-8 py-2 text-[13px] rounded-lg border outline-none transition-all cursor-pointer
          bg-white text-slate-800
          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
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

function Field({ label, error, required, hint, children }) {
  return (
    <div className="flex flex-col gap-1">
      <label className="text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">
        {label}{required && <span className="text-rose-500 ml-0.5">*</span>}
      </label>
      {children}
      {hint && !error && <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">{hint}</p>}
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
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3
        rounded-2xl shadow-xl text-[13px] font-semibold min-w-[260px] max-w-[90vw]
        ${type === 'success' ? 'bg-emerald-600 text-white' : type === 'error' ? 'bg-rose-600 text-white' : 'bg-blue-600 text-white'}`}
      style={{ animation: 'slideUp .25s ease' }}
    >
      {type === 'success' ? <Check className="w-4 h-4 flex-shrink-0" /> : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75 hover:opacity-100" /></button>
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(10px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
    </div>
  )
}

// ─── AUTOCOMPLETE DROPDOWN ────────────────────────────────────────────────────
function AdmNoField({ value, onChange, onSelect, error, disabled }) {
  const [open, setOpen] = useState(false)
  const wrapRef = useRef(null)

  const suggestions = useMemo(() => {
    if (!value || value.length < 2) return []
    const q = value.toLowerCase()
    return Object.entries(STUDENT_DB)
      .filter(([no, s]) => no.toLowerCase().includes(q) || s.name.toLowerCase().includes(q))
      .slice(0, 6)
  }, [value])

  const handleSelect = (no, student) => {
    onSelect(no, student)
    setOpen(false)
  }

  return (
    <div className="relative" ref={wrapRef}>
      <div className="relative">
        <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
        <input
          value={value}
          onChange={e => { onChange(e.target.value); setOpen(true) }}
          onFocus={() => setOpen(true)}
          onBlur={() => setTimeout(() => setOpen(false), 180)}
          disabled={disabled}
          placeholder="e.g. 2024-001"
          className={`w-full pl-8 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
            bg-white text-slate-800 placeholder-slate-300
            focus:border-blue-400 focus:ring-2 focus:ring-blue-100
            dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400
            disabled:opacity-50
            ${error ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
        />
        {value && (
          <button
            type="button"
            onMouseDown={e => { e.preventDefault(); onChange(''); onSelect('', null) }}
            className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
          >
            <X className="w-3.5 h-3.5" />
          </button>
        )}
      </div>

      {open && suggestions.length > 0 && (
        <div className="absolute left-0 right-0 top-full mt-1 z-30 rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.25)] bg-white dark:bg-[#1a1f35] shadow-xl overflow-hidden">
          {suggestions.map(([no, s]) => {
            const { bg, fg } = avatarColor(s.name)
            return (
              <button
                key={no}
                type="button"
                onMouseDown={() => handleSelect(no, s)}
                className="w-full flex items-center gap-3 px-3 py-2.5 hover:bg-blue-50 dark:hover:bg-white/[0.03] transition-colors text-left border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] last:border-0"
              >
                <span
                  className="w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold"
                  style={{ background: bg, color: fg }}
                >
                  {initials(s.name)}
                </span>
                <div className="min-w-0">
                  <p className="text-[13px] font-semibold text-slate-800 dark:text-slate-200 leading-tight">{s.name}</p>
                  <p className="text-[11px] text-slate-400 dark:text-slate-500">{no} · {s.class}</p>
                </div>
              </button>
            )
          })}
        </div>
      )}
    </div>
  )
}

// ─── FILE DROP ZONE ───────────────────────────────────────────────────────────
function FileDropZone({ file, onFile, onRemove, error }) {
  const inputRef = useRef(null)
  const [drag, setDrag] = useState(false)

  const handleDrop = e => {
    e.preventDefault(); setDrag(false)
    const f = e.dataTransfer.files?.[0]
    if (f) onFile(f)
  }

  const handleChange = e => {
    const f = e.target.files?.[0]
    if (f) onFile(f)
  }

  if (file) {
    return (
      <div className={`flex items-center gap-3 p-3 rounded-xl border-2
        bg-emerald-50 border-emerald-200 dark:bg-emerald-500/10 dark:border-emerald-500/30`}>
        <div className="w-9 h-9 rounded-lg bg-emerald-100 dark:bg-emerald-500/20 flex items-center justify-center flex-shrink-0">
          <File className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-[13px] font-semibold text-emerald-800 dark:text-emerald-300 truncate">{file.name}</p>
          <p className="text-[11px] text-emerald-600 dark:text-emerald-500">{(file.size / 1024).toFixed(1)} KB · {file.type || 'file'}</p>
        </div>
        <button
          type="button"
          onClick={onRemove}
          className="p-1.5 rounded-lg hover:bg-emerald-200 dark:hover:bg-emerald-500/20 text-emerald-600 dark:text-emerald-400 transition-colors"
        >
          <X className="w-4 h-4" />
        </button>
      </div>
    )
  }

  return (
    <div>
      <input ref={inputRef} type="file" accept=".pdf,.jpg,.jpeg,.png" onChange={handleChange} className="hidden" />
      <button
        type="button"
        onClick={() => inputRef.current?.click()}
        onDragOver={e => { e.preventDefault(); setDrag(true) }}
        onDragLeave={() => setDrag(false)}
        onDrop={handleDrop}
        className={`w-full flex flex-col items-center gap-2 py-5 px-4 rounded-xl border-2 border-dashed transition-all
          ${drag
            ? 'border-blue-400 bg-blue-50 dark:bg-blue-500/10 dark:border-blue-400'
            : error
              ? 'border-rose-300 bg-rose-50 dark:bg-rose-500/5 dark:border-rose-500/40'
              : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)] hover:border-blue-300 hover:bg-blue-50/50 dark:hover:bg-blue-500/5'
          }`}
      >
        <div className={`w-10 h-10 rounded-xl flex items-center justify-center
          ${drag ? 'bg-blue-100 dark:bg-blue-500/20' : 'bg-slate-100 dark:bg-slate-800'}`}>
          <CloudUpload className={`w-5 h-5 ${drag ? 'text-blue-600 dark:text-blue-400' : 'text-slate-400'}`} />
        </div>
        <div className="text-center">
          <p className={`text-[13px] font-semibold ${drag ? 'text-blue-700 dark:text-blue-300' : 'text-slate-600 dark:text-slate-300'}`}>
            {drag ? 'Drop file here' : 'Upload TC File'}
          </p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">PDF, JPG, PNG · Max 5 MB</p>
        </div>
      </button>
      {error && (
        <p className="flex items-center gap-1 text-[11px] text-rose-500 mt-1.5">
          <AlertCircle className="w-3 h-3 flex-shrink-0" />{error}
        </p>
      )}
    </div>
  )
}

// ─── STATUS BADGE ─────────────────────────────────────────────────────────────
function StatusBadge({ text, color }) {
  const cls = {
    green:  'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-400',
    blue:   'bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400',
    amber:  'bg-amber-100 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400',
  }
  return (
    <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold ${cls[color]}`}>
      {text}
    </span>
  )
}

// ─── SUMMARY CARD ─────────────────────────────────────────────────────────────
function SummaryCard({ icon: Icon, label, value, color }) {
  const c = {
    blue:    'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400',
    emerald: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400',
    amber:   'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400',
    violet:  'bg-violet-50 text-violet-600 dark:bg-violet-500/10 dark:text-violet-400',
  }
  return (
    <div className="flex items-center gap-3 rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-white dark:bg-[#1a1f35] px-4 py-3 shadow-sm flex-1 min-w-0">
      <span className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 ${c[color]}`}>
        <Icon className="w-5 h-5" />
      </span>
      <div className="min-w-0">
        <p className="text-[20px] font-bold text-slate-800 dark:text-slate-100 tabular-nums leading-tight">{value}</p>
        <p className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{label}</p>
      </div>
    </div>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ row, idx, onDelete }) {
  const { bg, fg } = avatarColor(row.name)
  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors group">
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{idx}</td>
      <td className="px-4 py-3">
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-blue-50 dark:bg-blue-500/10 text-blue-700 dark:text-blue-400 text-[12px] font-bold tabular-nums">
          <Hash className="w-3 h-3" />{row.tc_no}
        </span>
      </td>
      <td className="px-4 py-3">
        <span className="inline-flex items-center gap-1.5 text-[12px] font-semibold text-slate-600 dark:text-slate-300 bg-slate-100 dark:bg-slate-800 px-2.5 py-1 rounded-lg tabular-nums">
          {row.admission_no}
        </span>
      </td>
      <td className="px-4 py-3">
        <div className="flex items-center gap-2.5">
          <span
            className="w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold"
            style={{ background: bg, color: fg }}
          >
            {initials(row.name)}
          </span>
          <div>
            <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200">{row.name}</p>
            <p className="text-[11px] text-slate-400 dark:text-slate-500">{row.class}</p>
          </div>
        </div>
      </td>
      <td className="px-4 py-3 text-center">
        <StatusBadge text={row.session} color="blue" />
      </td>
      <td className="px-4 py-3 text-center">
        <div className="flex items-center justify-center gap-2">
          <a
            href={row.file_path}
            className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-emerald-50 hover:bg-emerald-100 dark:bg-emerald-500/10 dark:hover:bg-emerald-500/20 text-emerald-700 dark:text-emerald-400 text-[12px] font-semibold transition-colors"
          >
            <Download className="w-3.5 h-3.5" /> Download
          </a>
          <button
            onClick={() => onDelete(row.id)}
            className="opacity-0 group-hover:opacity-100 p-1.5 rounded-lg hover:bg-rose-50 dark:hover:bg-rose-500/10 text-rose-400 hover:text-rose-600 transition-all"
          >
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        </div>
      </td>
    </tr>
  )
}

// ─── MOBILE TC CARD ───────────────────────────────────────────────────────────
function MobileTCCard({ row, onDelete }) {
  const { bg, fg } = avatarColor(row.name)
  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      <div className="flex items-center gap-3 p-4">
        <span
          className="w-11 h-11 rounded-xl flex-shrink-0 flex items-center justify-center text-[11px] font-bold"
          style={{ background: bg, color: fg }}
        >
          {initials(row.name)}
        </span>
        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{row.name}</p>
          <p className="text-[12px] text-slate-500 dark:text-slate-400 mt-0.5">{row.class}</p>
          <div className="flex items-center gap-2 mt-1.5 flex-wrap">
            <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-blue-700 dark:text-blue-400 bg-blue-50 dark:bg-blue-500/10 px-2 py-0.5 rounded-md">
              <Hash className="w-2.5 h-2.5" />{row.tc_no}
            </span>
            <span className="text-[11px] text-slate-400 dark:text-slate-500">{row.admission_no}</span>
            <StatusBadge text={row.session} color="blue" />
          </div>
        </div>
      </div>
      <div className="flex items-center gap-2 px-4 pb-3 border-t border-slate-100 dark:border-[rgba(99,102,241,0.08)] pt-3">
        <div className="flex items-center gap-1.5 flex-1 min-w-0">
          <FileText className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
          <span className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{row.file_name}</span>
        </div>
        <a
          href={row.file_path}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white text-[12px] font-semibold transition-colors"
        >
          <Download className="w-3.5 h-3.5" /> Download
        </a>
        <button
          onClick={() => onDelete(row.id)}
          className="p-1.5 rounded-lg hover:bg-rose-50 dark:hover:bg-rose-500/10 text-rose-400 hover:text-rose-600 transition-colors"
        >
          <Trash2 className="w-3.5 h-3.5" />
        </button>
      </div>
    </div>
  )
}

// ─── STEP INDICATOR (Mobile) ──────────────────────────────────────────────────
function StepDot({ active, done, label }) {
  return (
    <div className="flex flex-col items-center gap-1 flex-1">
      <div className={`w-7 h-7 rounded-full flex items-center justify-center text-[11px] font-bold transition-all
        ${done
          ? 'bg-emerald-500 text-white'
          : active
            ? 'bg-blue-600 text-white dark:bg-indigo-600'
            : 'bg-slate-200 text-slate-500 dark:bg-slate-700 dark:text-slate-400'
        }`}>
        {done ? <Check className="w-3.5 h-3.5" /> : label}
      </div>
    </div>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function UploadTC() {
  // Form state
  const [session,    setSession]    = useState('')
  const [tcNo,       setTcNo]       = useState('')
  const [admNo,      setAdmNo]      = useState('')
  const [studentName,setStudentName]= useState('')
  const [studentClass, setStudentClass] = useState('')
  const [tcFile,     setTcFile]     = useState(null)
  const [errors,     setErrors]     = useState({})
  const [saving,     setSaving]     = useState(false)

  // List state
  const [records,    setRecords]    = useState(SAMPLE_UPLOADED)
  const [search,     setSearch]     = useState('')
  const [filterSes,  setFilterSes]  = useState('')
  const [exporting,  setExporting]  = useState(false)
  const [toast,      setToast]      = useState(null)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Admission No autocomplete ─────────────────────────────────────────────
  const handleAdmSelect = (no, student) => {
    setAdmNo(no)
    if (student) {
      setStudentName(student.name)
      setStudentClass(student.class)
      setErrors(p => ({ ...p, admNo: undefined, studentName: undefined }))
    } else {
      setStudentName('')
      setStudentClass('')
    }
  }

  // ── Validate ──────────────────────────────────────────────────────────────
  const validate = () => {
    const err = {}
    if (!session)     err.session     = 'Select a session'
    if (!tcNo.trim()) err.tcNo        = 'TC number is required'
    if (!admNo.trim())err.admNo       = 'Admission number is required'
    if (!studentName) err.studentName = 'Student name is required'
    if (!tcFile)      err.tcFile      = 'Please upload a TC file'
    return err
  }

  // ── Save ──────────────────────────────────────────────────────────────────
  const handleSave = useCallback(() => {
    const err = validate()
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setSaving(true)
    setTimeout(() => {
      const newRecord = {
        id:           Date.now(),
        tc_no:        tcNo.trim(),
        admission_no: admNo.trim(),
        name:         studentName,
        class:        studentClass || '—',
        session,
        file_path:    '#',
        file_name:    tcFile.name,
      }
      setRecords(p => [newRecord, ...p])
      // reset form
      setSession(''); setTcNo(''); setAdmNo(''); setStudentName('')
      setStudentClass(''); setTcFile(null); setErrors({})
      setSaving(false)
      showToast(`TC uploaded for ${newRecord.name} successfully!`)
    }, 900)
  }, [session, tcNo, admNo, studentName, studentClass, tcFile])

  const handleReset = () => {
    setSession(''); setTcNo(''); setAdmNo(''); setStudentName('')
    setStudentClass(''); setTcFile(null); setErrors({})
  }

  const handleDelete = (id) => {
    setRecords(p => p.filter(r => r.id !== id))
    showToast('Record deleted.', 'error')
  }

  const handleExcel = () => {
    setExporting(true)
    setTimeout(() => { setExporting(false); showToast('Excel export ready! (API integration pending)') }, 1200)
  }

  // ── Filtered list ─────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    return records.filter(r => {
      const matchSearch = !search || (
        r.name.toLowerCase().includes(search.toLowerCase()) ||
        r.tc_no.toLowerCase().includes(search.toLowerCase()) ||
        r.admission_no.toLowerCase().includes(search.toLowerCase())
      )
      const matchSes = !filterSes || r.session === filterSes
      return matchSearch && matchSes
    })
  }, [records, search, filterSes])

  // ── Form completeness for step indicator ──────────────────────────────────
  const step1Done = !!session && !!tcNo
  const step2Done = !!admNo && !!studentName
  const step3Done = !!tcFile

  return (
    <div className="space-y-5 pb-12">

      {/* ── Page Title ──────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <div className="w-8 h-8 rounded-xl bg-blue-600 dark:bg-indigo-600 flex items-center justify-center">
              <Upload className="w-4 h-4 text-white" />
            </div>
            Upload TC
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Upload Transfer Certificates and maintain a searchable record.
          </p>
        </div>

        {records.length > 0 && (
          <button
            type="button"
            onClick={handleExcel}
            disabled={exporting}
            className="hidden sm:flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
              bg-emerald-600 text-white hover:bg-emerald-700 shadow-md shadow-emerald-500/20
              transition-all active:scale-95 disabled:opacity-70 flex-shrink-0"
          >
            {exporting ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileSpreadsheet className="w-4 h-4" />}
            Export Excel
          </button>
        )}
      </div>

      {/* ── MAIN FORM CARD ──────────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-visible">

        {/* Card Header */}
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02] rounded-t-2xl">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <FileText className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">TC Upload Form</span>
          {/* Mobile step indicator */}
          <div className="flex sm:hidden items-center gap-1">
            <StepDot active={!step1Done} done={step1Done} label="1" />
            <div className={`h-0.5 w-4 rounded-full transition-colors ${step1Done ? 'bg-emerald-400' : 'bg-slate-200 dark:bg-slate-700'}`} />
            <StepDot active={step1Done && !step2Done} done={step2Done} label="2" />
            <div className={`h-0.5 w-4 rounded-full transition-colors ${step2Done ? 'bg-emerald-400' : 'bg-slate-200 dark:bg-slate-700'}`} />
            <StepDot active={step2Done && !step3Done} done={step3Done} label="3" />
          </div>
        </div>

        <div className="p-5">

          {/* ── Row 1: Session + TC No ────────────────────────────────────── */}
          <div className="mb-4">
            <p className="text-[11px] font-bold uppercase tracking-widest text-blue-600 dark:text-indigo-400 mb-3 flex items-center gap-2">
              <span className="w-5 h-5 rounded-full bg-blue-600 dark:bg-indigo-600 text-white flex items-center justify-center text-[9px] font-bold flex-shrink-0">1</span>
              TC Details
            </p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Field label="Session" error={errors.session} required>
                <NativeSelect
                  value={session}
                  onChange={e => { setSession(e.target.value); setErrors(p => ({ ...p, session: undefined })) }}
                  placeholder="-- Select Session --"
                  error={errors.session}
                >
                  {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
                </NativeSelect>
              </Field>

              <Field label="TC Number" error={errors.tcNo} required hint="e.g. TC-2024-001">
                <div className="relative">
                  <Hash className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                  <input
                    value={tcNo}
                    onChange={e => { setTcNo(e.target.value); setErrors(p => ({ ...p, tcNo: undefined })) }}
                    placeholder="TC-2024-001"
                    className={`w-full pl-8 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                      bg-white text-slate-800 placeholder-slate-300
                      focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                      dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400
                      ${errors.tcNo ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
                  />
                </div>
              </Field>
            </div>
          </div>

          {/* Divider */}
          <div className="border-t border-dashed border-slate-200 dark:border-[rgba(99,102,241,0.1)] my-4" />

          {/* ── Row 2: Admission No + Student Name ───────────────────────── */}
          <div className="mb-4">
            <p className="text-[11px] font-bold uppercase tracking-widest text-blue-600 dark:text-indigo-400 mb-3 flex items-center gap-2">
              <span className="w-5 h-5 rounded-full bg-blue-600 dark:bg-indigo-600 text-white flex items-center justify-center text-[9px] font-bold flex-shrink-0">2</span>
              Student Details
            </p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Field label="Admission No" error={errors.admNo} required hint="Start typing to search student">
                <AdmNoField
                  value={admNo}
                  onChange={v => { setAdmNo(v); if (!v) { setStudentName(''); setStudentClass('') } setErrors(p => ({ ...p, admNo: undefined })) }}
                  onSelect={handleAdmSelect}
                  error={errors.admNo}
                />
              </Field>

              <Field label="Student Name" error={errors.studentName} required>
                <div className="relative">
                  {studentName ? (
                    <div className="flex items-center gap-2.5 px-3 py-2 rounded-lg border bg-slate-50 dark:bg-[#1e2238] border-slate-200 dark:border-[rgba(99,102,241,0.25)]">
                      <div
                        className="w-6 h-6 rounded-md flex-shrink-0 flex items-center justify-center text-[9px] font-bold"
                        style={{ background: avatarColor(studentName).bg, color: avatarColor(studentName).fg }}
                      >
                        {initials(studentName)}
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 leading-tight">{studentName}</p>
                        {studentClass && <p className="text-[11px] text-slate-400 dark:text-slate-500">{studentClass}</p>}
                      </div>
                      <CheckCircle2 className="w-4 h-4 text-emerald-500 flex-shrink-0" />
                    </div>
                  ) : (
                    <input
                      value={studentName}
                      onChange={e => { setStudentName(e.target.value); setErrors(p => ({ ...p, studentName: undefined })) }}
                      placeholder="Auto-filled from Adm No"
                      className={`w-full pl-3 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                        bg-white text-slate-800 placeholder-slate-300
                        focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                        dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400
                        ${errors.studentName ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
                    />
                  )}
                </div>
              </Field>
            </div>
          </div>

          {/* Divider */}
          <div className="border-t border-dashed border-slate-200 dark:border-[rgba(99,102,241,0.1)] my-4" />

          {/* ── Row 3: File Upload ────────────────────────────────────────── */}
          <div className="mb-5">
            <p className="text-[11px] font-bold uppercase tracking-widest text-blue-600 dark:text-indigo-400 mb-3 flex items-center gap-2">
              <span className="w-5 h-5 rounded-full bg-blue-600 dark:bg-indigo-600 text-white flex items-center justify-center text-[9px] font-bold flex-shrink-0">3</span>
              Upload TC File
            </p>
            <div className="max-w-md">
              <FileDropZone
                file={tcFile}
                onFile={f => { setTcFile(f); setErrors(p => ({ ...p, tcFile: undefined })) }}
                onRemove={() => setTcFile(null)}
                error={errors.tcFile}
              />
            </div>
          </div>

          {/* ── Actions ──────────────────────────────────────────────────── */}
          <div className="flex flex-col sm:flex-row gap-3 pt-2">
            <button
              type="button"
              onClick={handleSave}
              disabled={saving}
              className="flex-1 sm:flex-none sm:w-40 flex items-center justify-center gap-2 py-2.5 px-6 rounded-xl text-[13px] font-bold text-white
                bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700
                transition-all active:scale-95 disabled:opacity-70"
            >
              {saving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Upload className="w-4 h-4" />}
              {saving ? 'Saving…' : 'Save & Upload'}
            </button>
            <button
              type="button"
              onClick={handleReset}
              className="flex-1 sm:flex-none flex items-center justify-center gap-2 py-2.5 px-5 rounded-xl text-[13px] font-semibold
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors"
            >
              <RefreshCw className="w-3.5 h-3.5" /> Reset Form
            </button>

            {/* Completion indicator */}
            {(step1Done && step2Done && step3Done) && (
              <div className="flex items-center gap-2 px-3 py-2 rounded-xl bg-emerald-50 dark:bg-emerald-500/10 border border-emerald-200 dark:border-emerald-500/25 sm:ml-auto">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400 flex-shrink-0" />
                <span className="text-[12px] font-semibold text-emerald-700 dark:text-emerald-400">All fields complete — ready to save</span>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* ── RECORDS LIST ──────────────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* List Header */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
            <span className="w-1 h-5 rounded-full bg-emerald-500 flex-shrink-0" />
            <BookOpen className="w-4 h-4 text-emerald-600 dark:text-emerald-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Uploaded Records</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
              {filtered.length} record{filtered.length !== 1 ? 's' : ''}
            </span>
          </div>
          <div className="flex items-center gap-2 flex-wrap">
            {/* Session filter */}
            <div className="w-32">
              <NativeSelect
                value={filterSes}
                onChange={e => setFilterSes(e.target.value)}
                placeholder="All Sessions"
              >
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </div>
            {/* Search */}
            <div className="relative w-full sm:w-48">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search name / TC / adm…"
                className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                  bg-white text-slate-700 border-slate-200 placeholder-slate-300
                  focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                  dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] dark:placeholder-slate-600 dark:focus:border-indigo-400"
              />
              {search && (
                <button onClick={() => setSearch('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400">
                  <X className="w-3.5 h-3.5" />
                </button>
              )}
            </div>
            {/* Mobile Excel */}
            <button
              onClick={handleExcel}
              disabled={exporting}
              className="sm:hidden flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-emerald-600 text-white text-[12px] font-semibold disabled:opacity-70"
            >
              {exporting ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <FileSpreadsheet className="w-3.5 h-3.5" />}
            </button>
          </div>
        </div>

        {/* Summary strip */}
        {records.length > 0 && (
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 px-5 py-4 border-b border-slate-100 dark:border-[rgba(99,102,241,0.08)] bg-slate-50/40 dark:bg-white/[0.01]">
            <SummaryCard icon={FileText}     label="Total TCs"       value={records.length}                       color="blue"    />
            <SummaryCard icon={Calendar}     label="This Session"    value={records.filter(r=>r.session==='2024-25').length} color="emerald" />
            <SummaryCard icon={Download}     label="Available"       value={records.length}                       color="amber"   />
            <SummaryCard icon={School2}      label="Sessions"        value={[...new Set(records.map(r=>r.session))].length} color="violet"  />
          </div>
        )}

        {/* ── DESKTOP TABLE ── */}
        <div className="hidden md:block overflow-x-auto">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
              <Search className="w-6 h-6 opacity-40" />
              <span className="text-[13px]">{records.length === 0 ? 'No TCs uploaded yet.' : 'No records match your search.'}</span>
            </div>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                  {['S.No.', 'TC Number', 'Adm No', 'Student Name', 'Session', 'Action'].map((h, i) => (
                    <th key={i} className="px-4 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:w-12">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.map((row, i) => (
                  <DesktopRow key={row.id} row={row} idx={i + 1} onDelete={handleDelete} />
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* ── MOBILE CARDS ── */}
        <div className="md:hidden p-4 space-y-3">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
              <Search className="w-6 h-6 opacity-40" />
              <span className="text-[13px]">{records.length === 0 ? 'No TCs uploaded yet.' : 'No records match your search.'}</span>
            </div>
          ) : (
            filtered.map(row => (
              <MobileTCCard key={row.id} row={row} onDelete={handleDelete} />
            ))
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{records.length}</span> records
          </p>
          {(search || filterSes) && (
            <button
              onClick={() => { setSearch(''); setFilterSes('') }}
              className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1"
            >
              <X className="w-3 h-3" /> Clear filters
            </button>
          )}
        </div>
      </div>

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
