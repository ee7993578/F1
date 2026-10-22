/**
 * UploadNotice.jsx
 * Folder: src/pages/Uploads/UploadNotice.jsx
 *
 * Converts legacy ASPX "Upload Notice" page to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session dropdown
 *  - Notice Date (date picker)
 *  - Notice Type (School / Fee) with dynamic class/student selectors
 *  - File upload with drag & drop
 *  - Category input
 *  - Description (rich text area)
 *  - Upload button with validation
 *  - GridView of uploaded notices with Delete
 *  - Desktop: ERP-style dense layout
 *  - Mobile: stacked card-based layout, filter drawer, thumb-friendly
 */

import { useState, useMemo, useCallback, useRef } from 'react'
import {
  Upload, Calendar, FileText, Trash2, Eye, RefreshCw,
  AlertCircle, X, Check, Loader2, ChevronDown,
  Bell, School, DollarSign, Filter, Search,
  Users, CheckSquare, Square, PlusCircle,
  Paperclip, FileUp, Info, SlidersHorizontal,
  ClipboardList, Building2, ChevronRight, BookOpen,
  Home, ChevronLeft
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ───────────────────────────────────────────────────────

const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const CLASSES = [
  'Nursery', 'LKG', 'UKG',
  'Class I', 'Class II', 'Class III', 'Class IV', 'Class V',
  'Class VI', 'Class VII', 'Class VIII', 'Class IX', 'Class X',
  'Class XI', 'Class XII'
]

const STUDENTS_BY_CLASS = {
  'Nursery': ['Aarav Sharma', 'Priya Singh', 'Riya Gupta', 'Arjun Patel', 'Ananya Mehta', 'Karan Joshi'],
  'LKG':     ['Vivaan Kumar', 'Diya Nair', 'Aryan Verma', 'Pooja Reddy', 'Rohan Das', 'Sneha Tiwari'],
  'UKG':     ['Ishaan Yadav', 'Kavya Sinha', 'Aditya Dubey', 'Nisha Mishra', 'Siddharth Pillai', 'Meera Jain'],
  'Class I': ['Reyansh Agarwal', 'Tanvi Bose', 'Dhruv Pandey', 'Ritika Chaudhary', 'Nakul Sharma', 'Priyanka Rawat'],
  'Class II':['Vihaan Kapoor', 'Simran Kaur', 'Arun Bajaj', 'Neha Saxena', 'Suraj Rao', 'Pallavi Dixit'],
  'Class III':['Kabir Malhotra', 'Anjali Singh', 'Rahul Bansal', 'Shreya Tripathi', 'Kunal Garg', 'Divya Kohli'],
  'Class IV': ['Arnav Mathur', 'Mansi Bhatt', 'Aakash Srivastava', 'Swati Chandra', 'Nikhil Yadav', 'Ruchi Gupta'],
  'Class V':  ['Shivam Tomar', 'Nidhi Pande', 'Jayant Misra', 'Sonal Joshi', 'Hemant Dubey', 'Gauri Verma'],
  'Class VI': ['Yash Chauhan', 'Aditi Rana', 'Chirag Shah', 'Bindu Nair', 'Varun Aggarwal', 'Deepa Choudhary'],
  'Class VII':['Udit Gupta', 'Manya Bhatia', 'Rishabh Roy', 'Aparna Saha', 'Tarun Menon', 'Alka Thakur'],
  'Class VIII':['Pranav Jha', 'Kriti Khanna', 'Ankit Shukla', 'Riya Sood', 'Harsh Kulkarni', 'Ankita Pandey'],
  'Class IX': ['Aditya Lal', 'Divya Mukherjee', 'Sachin Arora', 'Payal Desai', 'Manish Singh', 'Chandni Mishra'],
  'Class X':  ['Nitin Sharma', 'Preeti Varshney', 'Gaurav Awasthi', 'Pooja Naik', 'Rohit Pandey', 'Sunita Tiwari'],
  'Class XI': ['Mohit Agarwal', 'Ruchi Singh', 'Pankaj Mehta', 'Sapna Rao', 'Alok Bose', 'Nandita Kumar'],
  'Class XII':['Vijay Chandra', 'Geeta Patel', 'Suresh Bhat', 'Meghna Khanna', 'Deepak Srivastava', 'Kavitha Nair'],
}

// Initial dummy notices in grid
const INITIAL_NOTICES = [
  { id: 1, session: '2025-26', type: 'school', description: 'Annual Sports Day notice for all students and parents.', notice_date: '01 Jun 2025', file_path: 'sports_notice.pdf', category: 'Events' },
  { id: 2, session: '2025-26', type: 'fee',    description: 'Last date for fee payment - June 2025 installment.', notice_date: '05 Jun 2025', file_path: 'fee_june.pdf',    category: 'Finance' },
  { id: 3, session: '2024-25', type: 'school', description: 'Holiday notice for Diwali - school closed 3 days.',  notice_date: '20 Oct 2024', file_path: 'holiday.pdf',    category: 'Holiday' },
]

// ─── HELPERS ──────────────────────────────────────────────────────────────────

const noticeTypeConfig = {
  school: { label: 'School',  icon: School,     color: 'blue',   bg: 'bg-blue-50 dark:bg-blue-500/10',   text: 'text-blue-700 dark:text-blue-300',   border: 'border-blue-200 dark:border-blue-500/30' },
  fee:    { label: 'Fee',     icon: DollarSign, color: 'emerald',bg: 'bg-emerald-50 dark:bg-emerald-500/10', text: 'text-emerald-700 dark:text-emerald-300', border: 'border-emerald-200 dark:border-emerald-500/30' },
}

function today() {
  const d = new Date()
  return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

// ─── PRIMITIVE COMPONENTS ─────────────────────────────────────────────────────

function NativeSelect({ value, onChange, children, placeholder, error, disabled, className = '' }) {
  return (
    <div className="relative">
      <select
        value={value}
        onChange={onChange}
        disabled={disabled}
        className={`w-full appearance-none pl-3 pr-8 py-2.5 text-[13px] rounded-xl border outline-none transition-all cursor-pointer
          bg-white text-slate-800
          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
          dark:bg-[#1e2238] dark:text-slate-200 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
          disabled:opacity-50 disabled:cursor-not-allowed
          ${error
            ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20'
            : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
          } ${className}`}
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
    <div className="flex flex-col gap-1.5">
      <label className="text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide flex items-center gap-1">
        {label}{required && <span className="text-rose-500">*</span>}
        {hint && (
          <span className="normal-case font-normal text-slate-400 dark:text-slate-500 ml-1">({hint})</span>
        )}
      </label>
      {children}
      {error && (
        <p className="flex items-center gap-1 text-[11px] text-rose-500">
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
        ${type === 'success' ? 'bg-emerald-600 text-white' : type === 'error' ? 'bg-rose-600 text-white' : 'bg-amber-500 text-white'}`}
      style={{ animation: 'slideUp .25s ease' }}
    >
      {type === 'success'
        ? <Check className="w-4 h-4 flex-shrink-0" />
        : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75 hover:opacity-100" /></button>
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(10px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
    </div>
  )
}

// ─── BREADCRUMB ───────────────────────────────────────────────────────────────

function Breadcrumb() {
  return (
    <nav className="flex items-center gap-1.5 text-[12px] text-slate-400 dark:text-slate-500 mb-4">
      <Home className="w-3.5 h-3.5" />
      <span className="text-slate-300 dark:text-slate-600">/</span>
      <span className="text-slate-500 dark:text-slate-400 font-medium">Upload Notice</span>
    </nav>
  )
}

// ─── FILE DROP ZONE ───────────────────────────────────────────────────────────

function FileDropZone({ file, onFile, error }) {
  const inputRef = useRef()
  const [dragging, setDragging] = useState(false)

  const handleDrop = (e) => {
    e.preventDefault(); setDragging(false)
    const f = e.dataTransfer.files[0]
    if (f) onFile(f)
  }

  const handleChange = (e) => {
    const f = e.target.files[0]
    if (f) onFile(f)
  }

  if (file) {
    return (
      <div className="flex items-center gap-3 px-4 py-3 rounded-xl border border-emerald-200 dark:border-emerald-500/30 bg-emerald-50 dark:bg-emerald-500/10">
        <div className="w-8 h-8 rounded-lg bg-emerald-100 dark:bg-emerald-500/20 flex items-center justify-center flex-shrink-0">
          <FileText className="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-[13px] font-semibold text-emerald-700 dark:text-emerald-300 truncate">{file.name}</p>
          <p className="text-[11px] text-emerald-500 dark:text-emerald-400/70">{(file.size / 1024).toFixed(1)} KB</p>
        </div>
        <button
          type="button"
          onClick={() => { onFile(null); inputRef.current.value = '' }}
          className="p-1.5 rounded-lg hover:bg-emerald-100 dark:hover:bg-emerald-500/20 text-emerald-500"
        >
          <X className="w-3.5 h-3.5" />
        </button>
      </div>
    )
  }

  return (
    <div
      onDragOver={(e) => { e.preventDefault(); setDragging(true) }}
      onDragLeave={() => setDragging(false)}
      onDrop={handleDrop}
      onClick={() => inputRef.current.click()}
      className={`flex flex-col items-center justify-center gap-2 px-4 py-6 rounded-xl border-2 border-dashed cursor-pointer transition-all
        ${dragging
          ? 'border-blue-400 bg-blue-50 dark:bg-blue-500/10 dark:border-blue-500/50'
          : error
            ? 'border-rose-300 bg-rose-50 dark:bg-rose-500/5 dark:border-rose-500/30'
            : 'border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-slate-50/50 dark:bg-white/[0.02] hover:border-blue-300 hover:bg-blue-50/40 dark:hover:border-indigo-500/40 dark:hover:bg-indigo-500/5'
        }`}
    >
      <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${dragging ? 'bg-blue-100 dark:bg-blue-500/20' : 'bg-slate-100 dark:bg-slate-800'}`}>
        <FileUp className={`w-5 h-5 ${dragging ? 'text-blue-600 dark:text-blue-400' : 'text-slate-400'}`} />
      </div>
      <div className="text-center">
        <p className="text-[13px] font-semibold text-slate-600 dark:text-slate-300">
          {dragging ? 'Drop file here' : 'Click to upload'}
        </p>
        <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">
          PDF, DOC, DOCX, JPG, PNG up to 10MB
        </p>
      </div>
      <input ref={inputRef} type="file" className="hidden" onChange={handleChange}
        accept=".pdf,.doc,.docx,.jpg,.jpeg,.png" />
    </div>
  )
}

// ─── CHECKBOX LIST ────────────────────────────────────────────────────────────

function CheckboxList({ items, selected, onToggle, onToggleAll, label, maxHeight = 'max-h-40' }) {
  const allSelected = items.length > 0 && items.every(i => selected.includes(i))
  const someSelected = items.some(i => selected.includes(i))

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] overflow-hidden">
      {/* Select All header */}
      <div
        className="flex items-center gap-2.5 px-3 py-2 bg-slate-50 dark:bg-white/[0.03] border-b border-slate-200 dark:border-[rgba(99,102,241,0.15)] cursor-pointer hover:bg-slate-100 dark:hover:bg-white/[0.05] transition-colors"
        onClick={onToggleAll}
      >
        <div className={`w-4 h-4 rounded flex items-center justify-center border transition-all flex-shrink-0
          ${allSelected
            ? 'bg-blue-600 dark:bg-indigo-500 border-blue-600 dark:border-indigo-500'
            : someSelected
              ? 'bg-blue-100 dark:bg-indigo-500/20 border-blue-400 dark:border-indigo-400'
              : 'border-slate-300 dark:border-slate-600'
          }`}>
          {allSelected && <Check className="w-2.5 h-2.5 text-white" />}
          {someSelected && !allSelected && <div className="w-2 h-0.5 bg-blue-600 dark:bg-indigo-400 rounded" />}
        </div>
        <span className="text-[12px] font-bold text-slate-600 dark:text-slate-300 uppercase tracking-wide">Select All {label}</span>
        {someSelected && (
          <span className="ml-auto text-[10px] font-semibold text-blue-600 dark:text-indigo-400 bg-blue-50 dark:bg-indigo-500/15 px-2 py-0.5 rounded-full">
            {selected.length} selected
          </span>
        )}
      </div>
      {/* Items */}
      <div className={`${maxHeight} overflow-y-auto`}>
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-0.5 p-2">
          {items.map(item => {
            const checked = selected.includes(item)
            return (
              <label
                key={item}
                className={`flex items-center gap-2 px-2.5 py-2 rounded-lg cursor-pointer transition-all text-[12px]
                  ${checked
                    ? 'bg-blue-50 dark:bg-indigo-500/15 text-blue-700 dark:text-indigo-300'
                    : 'hover:bg-slate-50 dark:hover:bg-white/[0.03] text-slate-600 dark:text-slate-300'
                  }`}
              >
                <div className={`w-3.5 h-3.5 rounded border flex items-center justify-center flex-shrink-0 transition-all
                  ${checked
                    ? 'bg-blue-600 dark:bg-indigo-500 border-blue-600 dark:border-indigo-500'
                    : 'border-slate-300 dark:border-slate-600'
                  }`}>
                  {checked && <Check className="w-2 h-2 text-white" />}
                </div>
                <span className="truncate font-medium">{item}</span>
                <input type="checkbox" className="hidden" checked={checked} onChange={() => onToggle(item)} />
              </label>
            )
          })}
        </div>
      </div>
    </div>
  )
}

// ─── NOTICE TYPE BADGE ────────────────────────────────────────────────────────

function NoticeTypeBadge({ type }) {
  const cfg = noticeTypeConfig[type]
  if (!cfg) return <span className="text-slate-400 text-[12px]">—</span>
  const Icon = cfg.icon
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-bold border ${cfg.bg} ${cfg.text} ${cfg.border}`}>
      <Icon className="w-3 h-3" />
      {cfg.label}
    </span>
  )
}

// ─── NOTICE GRID ROW (Desktop) ────────────────────────────────────────────────

function GridRow({ notice, idx, onDelete }) {
  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors group">
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{idx}</td>
      <td className="px-4 py-3 text-center"><NoticeTypeBadge type={notice.type} /></td>
      <td className="px-4 py-3">
        <p className="text-[13px] text-slate-700 dark:text-slate-200 font-medium line-clamp-2 leading-snug">{notice.description}</p>
        {notice.category && (
          <span className="inline-block mt-1 text-[10px] font-semibold text-slate-400 dark:text-slate-500 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded-full">
            {notice.category}
          </span>
        )}
      </td>
      <td className="px-4 py-3 text-center">
        <span className="text-[12px] text-slate-600 dark:text-slate-400 whitespace-nowrap font-medium">{notice.notice_date}</span>
      </td>
      <td className="px-4 py-3 text-center">
        {notice.file_path ? (
          <div className="flex items-center justify-center gap-1.5">
            <Paperclip className="w-3.5 h-3.5 text-slate-400" />
            <span className="text-[12px] text-blue-600 dark:text-blue-400 font-medium truncate max-w-[120px]">{notice.file_path}</span>
          </div>
        ) : (
          <span className="text-[12px] text-slate-300 dark:text-slate-600">No file</span>
        )}
      </td>
      <td className="px-4 py-3 text-center">
        <button
          type="button"
          onClick={() => onDelete(notice.id)}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
            bg-rose-50 text-rose-600 hover:bg-rose-100 border border-rose-200
            dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20 dark:border-rose-500/25
            transition-all active:scale-95"
        >
          <Trash2 className="w-3.5 h-3.5" />
          <span className="hidden sm:inline">Delete</span>
        </button>
      </td>
    </tr>
  )
}

// ─── NOTICE MOBILE CARD ───────────────────────────────────────────────────────

function NoticeMobileCard({ notice, idx, onDelete }) {
  const [expanded, setExpanded] = useState(false)
  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-start gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        <div className="w-7 h-7 rounded-lg bg-slate-100 dark:bg-slate-800 flex items-center justify-center flex-shrink-0 text-[11px] font-bold text-slate-500 dark:text-slate-400 mt-0.5">
          {idx}
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 flex-wrap mb-1">
            <NoticeTypeBadge type={notice.type} />
            {notice.category && (
              <span className="text-[10px] font-semibold text-slate-400 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded-full">
                {notice.category}
              </span>
            )}
          </div>
          <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 line-clamp-2 leading-snug">{notice.description}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-1 flex items-center gap-1">
            <Calendar className="w-3 h-3" />
            {notice.notice_date}
          </p>
        </div>
        <ChevronRight className={`w-4 h-4 text-slate-400 flex-shrink-0 mt-1 transition-transform duration-200 ${expanded ? 'rotate-90' : ''}`} />
      </button>

      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 py-3 space-y-3">
          {notice.file_path && (
            <div className="flex items-center gap-2 rounded-lg bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20 px-3 py-2">
              <Paperclip className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <span className="text-[12px] font-medium text-blue-700 dark:text-blue-300 truncate">{notice.file_path}</span>
            </div>
          )}
          <button
            type="button"
            onClick={() => onDelete(notice.id)}
            className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
              bg-rose-50 text-rose-600 hover:bg-rose-100 border border-rose-200
              dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20 dark:border-rose-500/25
              transition-all active:scale-95"
          >
            <Trash2 className="w-4 h-4" />
            Delete Notice
          </button>
        </div>
      )}
    </div>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────

export { UploadNotice as default } from '../WebsiteModule'
/* legacy implementation retained below for reference; the live page is WebsiteModule.UploadNotice. */
function LegacyUploadNotice() {
  // Form state
  const [session,     setSession]     = useState('')
  const [noticeDate,  setNoticeDate]  = useState(today())
  const [noticeType,  setNoticeType]  = useState('0')
  const [file,        setFile]        = useState(null)
  const [category,    setCategory]    = useState('')
  const [description, setDescription] = useState('')

  // Class / Student selection (visible when noticeType === '1' = school)
  const [selectedClasses,  setSelectedClasses]  = useState([])
  const [selectedStudents, setSelectedStudents] = useState([])

  // Grid state
  const [notices,    setNotices]    = useState(INITIAL_NOTICES)
  const [search,     setSearch]     = useState('')

  // UI state
  const [uploading,  setUploading]  = useState(false)
  const [errors,     setErrors]     = useState({})
  const [toast,      setToast]      = useState(null)
  const [nextId,     setNextId]     = useState(4)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // Derived student list from selected classes
  const availableStudents = useMemo(() => {
    const students = []
    selectedClasses.forEach(cls => {
      if (STUDENTS_BY_CLASS[cls]) {
        STUDENTS_BY_CLASS[cls].forEach(s => students.push(`${s} (${cls})`))
      }
    })
    return students
  }, [selectedClasses])

  // When classes change, drop students not in new pool
  const handleClassToggle = (cls) => {
    setSelectedClasses(prev => {
      const next = prev.includes(cls) ? prev.filter(c => c !== cls) : [...prev, cls]
      // Clean up students
      const pool = next.flatMap(c => (STUDENTS_BY_CLASS[c] || []).map(s => `${s} (${c})`))
      setSelectedStudents(sel => sel.filter(s => pool.includes(s)))
      return next
    })
  }

  const handleToggleAllClasses = () => {
    const allSel = CLASSES.every(c => selectedClasses.includes(c))
    if (allSel) {
      setSelectedClasses([])
      setSelectedStudents([])
    } else {
      setSelectedClasses(CLASSES)
    }
  }

  const handleStudentToggle = (s) => {
    setSelectedStudents(prev => prev.includes(s) ? prev.filter(x => x !== s) : [...prev, s])
  }

  const handleToggleAllStudents = () => {
    const allSel = availableStudents.every(s => selectedStudents.includes(s))
    setSelectedStudents(allSel ? [] : [...availableStudents])
  }

  // Notice type change — reset class/student selections
  const handleNoticeTypeChange = (val) => {
    setNoticeType(val)
    setSelectedClasses([])
    setSelectedStudents([])
  }

  // Validate
  const validate = () => {
    const errs = {}
    if (!session)          errs.session     = 'Please select a session'
    if (!noticeDate)       errs.noticeDate  = 'Please enter notice date'
    if (noticeType === '0') errs.noticeType = 'Please select notice type'
    if (!category.trim()) errs.category    = 'Category is required'
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  const handleUpload = useCallback(() => {
    if (!validate()) return
    setUploading(true)

    setTimeout(() => {
      const newNotice = {
        id:          nextId,
        session,
        type:        noticeType === '1' ? 'school' : 'fee',
        description: description || '(No description)',
        notice_date: noticeDate,
        file_path:   file ? file.name : '',
        category:    category,
      }
      setNotices(prev => [newNotice, ...prev])
      setNextId(prev => prev + 1)

      // Reset form
      setSession('')
      setNoticeDate(today())
      setNoticeType('0')
      setFile(null)
      setCategory('')
      setDescription('')
      setSelectedClasses([])
      setSelectedStudents([])
      setErrors({})
      setUploading(false)
      showToast('Notice uploaded successfully!')
    }, 900)
  }, [session, noticeDate, noticeType, file, category, description, nextId])

  const handleDelete = (id) => {
    setNotices(prev => prev.filter(n => n.id !== id))
    showToast('Notice deleted.', 'error')
  }

  const handleReset = () => {
    setSession(''); setNoticeDate(today()); setNoticeType('0')
    setFile(null); setCategory(''); setDescription('')
    setSelectedClasses([]); setSelectedStudents([])
    setErrors({})
  }

  // Filtered grid
  const filteredNotices = useMemo(() => {
    if (!search) return notices
    const q = search.toLowerCase()
    return notices.filter(n =>
      n.description.toLowerCase().includes(q) ||
      n.type.toLowerCase().includes(q) ||
      n.category?.toLowerCase().includes(q) ||
      n.notice_date?.toLowerCase().includes(q)
    )
  }, [notices, search])

  const showClassStudentSection = noticeType === '1'   // school
  const showFeeClassSection     = noticeType === '2'   // fee

  return (
    <div className="space-y-5 pb-12">
      <style>{`
        .line-clamp-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
        .scrollbar-thin::-webkit-scrollbar { width: 5px; height: 5px; }
        .scrollbar-thin::-webkit-scrollbar-track { background: transparent; }
        .scrollbar-thin::-webkit-scrollbar-thumb { background: #1765B5; border-radius: 4px; }
      `}</style>

      {/* Breadcrumb */}
      <Breadcrumb />

      {/* Page Title */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Bell className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Upload Notice
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Create and manage school/fee notices for students and parents.
          </p>
        </div>
      </div>

      {/* ── UPLOAD FORM CARD ──────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Card Header */}
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Upload className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Upload Notice</span>
        </div>

        <div className="p-5 space-y-5">

          {/* Row 1: Session | Date | Notice Type */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">

            <Field label="Session" required error={errors.session}>
              <NativeSelect
                value={session}
                onChange={e => { setSession(e.target.value); setErrors(p => ({ ...p, session: undefined })) }}
                placeholder="-- Select Session --"
                error={errors.session}
              >
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>

            <Field label="Notice Date" required error={errors.noticeDate}>
              <div className="relative">
                <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  type="date"
                  value={noticeDate}
                  onChange={e => { setNoticeDate(e.target.value); setErrors(p => ({ ...p, noticeDate: undefined })) }}
                  className={`w-full pl-9 pr-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all
                    bg-white text-slate-800 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                    ${errors.noticeDate ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
                />
              </div>
            </Field>

            <Field label="Notice Type" required error={errors.noticeType}>
              <NativeSelect
                value={noticeType}
                onChange={e => { handleNoticeTypeChange(e.target.value); setErrors(p => ({ ...p, noticeType: undefined })) }}
                error={errors.noticeType}
              >
                <option value="0">-- Select --</option>
                <option value="1">School</option>
                <option value="2">Fee</option>
              </NativeSelect>
            </Field>
          </div>

          {/* Row 2: File Upload | Category */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Field label="Upload File" hint="PDF, DOC, JPG, PNG">
              <FileDropZone file={file} onFile={setFile} error={errors.file} />
            </Field>

            <Field label="Category" required error={errors.category}>
              <div className="relative">
                <ClipboardList className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  type="text"
                  value={category}
                  onChange={e => { setCategory(e.target.value); setErrors(p => ({ ...p, category: undefined })) }}
                  placeholder="e.g. Events, Finance, Holiday"
                  className={`w-full pl-9 pr-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all
                    bg-white text-slate-800 placeholder-slate-300
                    focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                    ${errors.category ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
                />
              </div>
            </Field>
          </div>

          {/* Notice Type Info Banner */}
          {noticeType !== '0' && (
            <div className={`flex items-start gap-3 rounded-xl border px-4 py-3
              ${noticeType === '1'
                ? 'bg-blue-50 border-blue-200 dark:bg-blue-500/10 dark:border-blue-500/25'
                : 'bg-emerald-50 border-emerald-200 dark:bg-emerald-500/10 dark:border-emerald-500/25'
              }`}>
              {noticeType === '1'
                ? <School className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0 mt-0.5" />
                : <DollarSign className="w-4 h-4 text-emerald-600 dark:text-emerald-400 flex-shrink-0 mt-0.5" />
              }
              <div>
                <p className={`text-[13px] font-semibold ${noticeType === '1' ? 'text-blue-700 dark:text-blue-300' : 'text-emerald-700 dark:text-emerald-300'}`}>
                  {noticeType === '1' ? 'School Notice' : 'Fee Notice'}
                </p>
                <p className={`text-[12px] mt-0.5 ${noticeType === '1' ? 'text-blue-600/70 dark:text-blue-400/70' : 'text-emerald-600/70 dark:text-emerald-400/70'}`}>
                  {noticeType === '1'
                    ? 'Select classes and students below to target this notice.'
                    : 'This fee notice will be sent school-wide. Optionally select classes below.'
                  }
                </p>
              </div>
            </div>
          )}

          {/* Class / Student Selectors — School type */}
          {showClassStudentSection && (
            <div className="space-y-4">
              <div className="flex items-center gap-2">
                <div className="h-px flex-1 bg-slate-100 dark:bg-[rgba(99,102,241,0.1)]" />
                <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500 flex items-center gap-1.5">
                  <Users className="w-3.5 h-3.5" /> Target Audience
                </span>
                <div className="h-px flex-1 bg-slate-100 dark:bg-[rgba(99,102,241,0.1)]" />
              </div>

              <Field label="Select Classes">
                <CheckboxList
                  items={CLASSES}
                  selected={selectedClasses}
                  onToggle={handleClassToggle}
                  onToggleAll={handleToggleAllClasses}
                  label="Classes"
                  maxHeight="max-h-48"
                />
              </Field>

              {selectedClasses.length > 0 && availableStudents.length > 0 && (
                <Field label="Select Students" hint={`${availableStudents.length} available`}>
                  <CheckboxList
                    items={availableStudents}
                    selected={selectedStudents}
                    onToggle={handleStudentToggle}
                    onToggleAll={handleToggleAllStudents}
                    label="Students"
                    maxHeight="max-h-48"
                  />
                </Field>
              )}
            </div>
          )}

          {/* Class Selector — Fee type */}
          {showFeeClassSection && (
            <div className="space-y-4">
              <div className="flex items-center gap-2">
                <div className="h-px flex-1 bg-slate-100 dark:bg-[rgba(99,102,241,0.1)]" />
                <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400 dark:text-slate-500 flex items-center gap-1.5">
                  <BookOpen className="w-3.5 h-3.5" /> Class Filter (Optional)
                </span>
                <div className="h-px flex-1 bg-slate-100 dark:bg-[rgba(99,102,241,0.1)]" />
              </div>
              <Field label="Select Classes" hint="Leave blank to send to all">
                <CheckboxList
                  items={CLASSES}
                  selected={selectedClasses}
                  onToggle={handleClassToggle}
                  onToggleAll={handleToggleAllClasses}
                  label="Classes"
                  maxHeight="max-h-36"
                />
              </Field>
            </div>
          )}

          {/* Description */}
          <Field label="Description">
            <textarea
              value={description}
              onChange={e => setDescription(e.target.value)}
              rows={4}
              placeholder="Enter notice description or content..."
              className="w-full px-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all resize-none
                bg-white text-slate-800 placeholder-slate-300
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                border-slate-200 dark:border-[rgba(99,102,241,0.25)]"
            />
          </Field>

          {/* Form Actions */}
          <div className="flex flex-col sm:flex-row gap-3 pt-2">
            <button
              type="button"
              onClick={handleUpload}
              disabled={uploading}
              className="flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl text-[13px] font-bold text-white
                bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700
                shadow-md shadow-blue-500/20 dark:shadow-indigo-500/20
                transition-all active:scale-95 disabled:opacity-70 disabled:cursor-not-allowed"
            >
              {uploading
                ? <><Loader2 className="w-4 h-4 animate-spin" /> Uploading...</>
                : <><Upload className="w-4 h-4" /> Upload Notice</>
              }
            </button>

            <button
              type="button"
              onClick={handleReset}
              disabled={uploading}
              className="flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl text-[13px] font-semibold
                bg-slate-100 text-slate-600 hover:bg-slate-200
                dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700
                transition-all active:scale-95"
            >
              <RefreshCw className="w-4 h-4" /> Reset
            </button>
          </div>
        </div>
      </div>

      {/* ── NOTICES GRID ──────────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Card Header */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0">
            <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
            <ClipboardList className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Uploaded Notices</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
              {filteredNotices.length} record{filteredNotices.length !== 1 ? 's' : ''}
            </span>
          </div>

          {/* Search */}
          <div className="relative w-full sm:w-56 flex-shrink-0">
            <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
            <input
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Search notices…"
              className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                bg-white text-slate-700 border-slate-200 placeholder-slate-300
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                dark:placeholder-slate-600 dark:focus:border-indigo-400"
            />
            {search && (
              <button onClick={() => setSearch('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                <X className="w-3.5 h-3.5" />
              </button>
            )}
          </div>
        </div>

        {/* Desktop Table */}
        <div className="hidden md:block overflow-x-auto">
          {filteredNotices.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 gap-3 text-slate-400 dark:text-slate-600">
              <Bell className="w-8 h-8 opacity-30" />
              <p className="text-[13px]">{search ? 'No notices match your search.' : 'No notices uploaded yet.'}</p>
            </div>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                  {['S.No.', 'Notice Type', 'Description', 'Notice Date', 'File', 'Action'].map((h, i) => (
                    <th key={i} className={`px-4 py-2.5 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap
                      ${i === 0 ? 'text-center w-12' : i === 1 || i === 3 || i === 4 || i === 5 ? 'text-center' : 'text-left'}`}>
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filteredNotices.map((notice, i) => (
                  <GridRow key={notice.id} notice={notice} idx={i + 1} onDelete={handleDelete} />
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* Mobile Cards */}
        <div className="md:hidden p-4 space-y-3">
          {filteredNotices.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12 gap-3 text-slate-400 dark:text-slate-600">
              <Bell className="w-8 h-8 opacity-30" />
              <p className="text-[13px]">{search ? 'No notices match your search.' : 'No notices uploaded yet.'}</p>
            </div>
          ) : (
            <>
              <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5 pb-1">
                <Info className="w-3.5 h-3.5 flex-shrink-0" />
                Tap a notice to view details or delete.
              </p>
              {filteredNotices.map((notice, i) => (
                <NoticeMobileCard key={notice.id} notice={notice} idx={i + 1} onDelete={handleDelete} />
              ))}
            </>
          )}
        </div>

        {/* Table Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-3 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filteredNotices.length}</span> of{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{notices.length}</span> notices
          </p>
          {search && (
            <button onClick={() => setSearch('')}
              className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
              <X className="w-3 h-3" /> Clear search
            </button>
          )}
        </div>
      </div>

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
