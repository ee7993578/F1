/**
 * StudentAttendance.jsx
 * Folder: src/pages/Student/Attendance/StudentAttendance.jsx
 *
 * Converts legacy ASPX "Student Attendance" to fully-responsive React + Tailwind.
 *
 * Modes:
 *  1. Day-wise  — Pick date + class → mark Present/Absent/Leave/Holiday/NA per student
 *  2. Month-wise — Pick class + term + session + month → enter attendance / working days
 *
 * Features:
 *  - Radio-based mode switching (Day-wise / Month-wise)
 *  - Calendar date picker (native input[date])
 *  - Class & session dropdowns
 *  - Bulk-mark header radio (mark all P/A/L/H/NA)
 *  - Per-student radio toggle
 *  - Attendance summary (Total / Present / Absent / Leave)
 *  - Month-wise grid with attendance/working-day textboxes
 *  - Submit + Send SMS buttons (day-wise)
 *  - Mobile: cards with status pills, filter drawer
 *  - Desktop: dense ERP-style table
 */

import { useState, useMemo, useCallback, useEffect, useRef } from 'react'
import {
  CalendarDays, ChevronDown, SlidersHorizontal, X, Check,
  AlertCircle, Loader2, Users, UserCheck, UserX, Clock,
  RefreshCw, Send, Save, Eye, ChevronRight, Info,
  GraduationCap, BookOpen, Filter, Search, Calendar,
  CheckCircle2, XCircle, AlarmClock, Star, PlusCircle,
  BarChart3, MessageSquare, FileText, School
} from 'lucide-react'

// ─── STATIC DATA ──────────────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const CLASSES = [
  'Nursery', 'LKG', 'UKG',
  'Class I', 'Class II', 'Class III', 'Class IV', 'Class V',
  'Class VI', 'Class VII', 'Class VIII',
  'Class IX', 'Class X', 'Class XI', 'Class XII',
]

const TERMS = ['Term 1 (Apr–Sep)', 'Term 2 (Oct–Mar)', 'Annual']

const MONTHS = [
  'April 2025', 'May 2025', 'June 2025', 'July 2025',
  'August 2025', 'September 2025', 'October 2025', 'November 2025',
  'December 2025', 'January 2026', 'February 2026', 'March 2026',
]

const ATT_STATUS = [
  { value: 'P',  label: 'Present',       short: 'P',  color: 'emerald' },
  { value: 'A',  label: 'Absent',        short: 'A',  color: 'rose'    },
  { value: 'L',  label: 'Leave',         short: 'L',  color: 'amber'   },
  { value: 'H',  label: 'Holiday',       short: 'H',  color: 'blue'    },
  { value: 'NA', label: 'New Admission', short: 'NA', color: 'violet'  },
]

// Generate dummy students per class
const generateStudents = (className) => {
  const names = [
    'Aarav Sharma','Priya Singh','Rahul Verma','Ananya Gupta','Rohan Tiwari',
    'Sneha Patel','Arjun Nair','Kavya Mehta','Vivek Joshi','Riya Chauhan',
    'Karan Dubey','Pooja Yadav','Amit Pandey','Divya Srivastava','Nikhil Kumar',
    'Shreya Mishra','Akash Bose','Neha Thakur','Suresh Reddy','Preethi Iyer',
    'Manish Agarwal','Sunita Bhatt','Raj Kapoor','Meena Saxena','Ajay Rawat',
    'Deepika Pande','Sanjay Bhatt','Anjali Negi','Mohit Garg','Leena Malhotra',
    'Tarun Sehgal','Rekha Bisht','Puneet Kohli','Simran Arora','Gaurav Naik',
    'Preeti Ranjan','Vinay Trivedi','Usha Chandra','Sunil Bhatia','Shweta Singh',
  ]
  const count = 20 + Math.floor(Math.random() * 15)
  return Array.from({ length: Math.min(count, names.length) }, (_, i) => ({
    stu_id: `STU${className.replace(/\s/g, '').toUpperCase()}${String(i + 1).padStart(3, '0')}`,
    registration_no: `ADM${2024100 + i}`,
    name: names[i] || `Student ${i + 1}`,
    att_status: 'P',
  }))
}

// Month-wise dummy data
const generateMonthData = (className, month) => {
  const students = generateStudents(className)
  return students.map(s => ({
    ...s,
    attendance: String(Math.floor(Math.random() * 6) + 22),
    work_Day: '26',
  }))
}

// ─── COLOR HELPERS ────────────────────────────────────────────────────────────
const STATUS_STYLES = {
  P:  { bg: 'bg-emerald-50 dark:bg-emerald-500/10', text: 'text-emerald-700 dark:text-emerald-400', border: 'border-emerald-200 dark:border-emerald-500/25', dot: 'bg-emerald-500' },
  A:  { bg: 'bg-rose-50 dark:bg-rose-500/10',     text: 'text-rose-700 dark:text-rose-400',     border: 'border-rose-200 dark:border-rose-500/25',     dot: 'bg-rose-500'    },
  L:  { bg: 'bg-amber-50 dark:bg-amber-500/10',   text: 'text-amber-700 dark:text-amber-400',   border: 'border-amber-200 dark:border-amber-500/25',   dot: 'bg-amber-500'   },
  H:  { bg: 'bg-blue-50 dark:bg-blue-500/10',     text: 'text-blue-700 dark:text-blue-400',     border: 'border-blue-200 dark:border-blue-500/25',     dot: 'bg-blue-500'    },
  NA: { bg: 'bg-violet-50 dark:bg-violet-500/10', text: 'text-violet-700 dark:text-violet-400', border: 'border-violet-200 dark:border-violet-500/25', dot: 'bg-violet-500'  },
}

const CLASS_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' }, { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' }, { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' }, { fg: '#dc2626', bg: '#fee2e2' },
  { fg: '#0369a1', bg: '#e0f2fe' },
]
const classColor = (name = '') => CLASS_COLORS[(name.charCodeAt(0) ?? 0) % CLASS_COLORS.length]
const formatAbbr = (name = '') => name.replace('Class ', '').slice(0, 3).toUpperCase()

// ─── PRIMITIVE COMPONENTS ─────────────────────────────────────────────────────

function NativeSelect({ value, onChange, children, placeholder, error, disabled, className: cx = '' }) {
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
          ${error ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'} ${cx}`}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {children}
      </select>
      <ChevronDown className="absolute right-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
    </div>
  )
}

function Field({ label, error, required, children, className: cx = '' }) {
  return (
    <div className={`flex flex-col gap-1 ${cx}`}>
      <label className="text-[11px] font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wide">
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
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3
        rounded-2xl shadow-xl text-[13px] font-semibold min-w-[260px] max-w-[90vw]
        ${type === 'success' ? 'bg-emerald-600 text-white' : 'bg-rose-600 text-white'}`}
      style={{ animation: 'slideUp .25s ease' }}
    >
      {type === 'success' ? <Check className="w-4 h-4 flex-shrink-0" /> : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75 hover:opacity-100" /></button>
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(10px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
    </div>
  )
}

// ─── STATUS BADGE ─────────────────────────────────────────────────────────────
function StatusBadge({ status }) {
  const s = STATUS_STYLES[status] || STATUS_STYLES['P']
  const label = ATT_STATUS.find(x => x.value === status)?.label || status
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-bold border ${s.bg} ${s.text} ${s.border}`}>
      <span className={`w-1.5 h-1.5 rounded-full flex-shrink-0 ${s.dot}`} />
      {label}
    </span>
  )
}

// ─── SUMMARY CARD ─────────────────────────────────────────────────────────────
function SummaryCard({ icon: Icon, label, value, color }) {
  const colors = {
    blue:    'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400',
    emerald: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400',
    rose:    'bg-rose-50 text-rose-600 dark:bg-rose-500/10 dark:text-rose-400',
    amber:   'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400',
  }
  return (
    <div className="flex items-center gap-3 rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-white dark:bg-[#1a1f35] px-4 py-3 shadow-sm flex-1 min-w-0">
      <span className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 ${colors[color]}`}>
        <Icon className="w-5 h-5" />
      </span>
      <div className="min-w-0">
        <p className="text-[20px] font-bold text-slate-800 dark:text-slate-100 tabular-nums leading-tight">{value}</p>
        <p className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{label}</p>
      </div>
    </div>
  )
}

// ─── STATUS RADIO GROUP (compact pill buttons) ────────────────────────────────
function StatusRadio({ value, onChange, name }) {
  return (
    <div className="flex flex-wrap gap-1">
      {ATT_STATUS.map(opt => {
        const selected = value === opt.value
        const s = STATUS_STYLES[opt.value]
        return (
          <button
            key={opt.value}
            type="button"
            onClick={() => onChange(opt.value)}
            className={`px-2.5 py-1 rounded-lg text-[11px] font-bold border transition-all
              ${selected ? `${s.bg} ${s.text} ${s.border} ring-2 ring-offset-1` : 'bg-slate-50 dark:bg-slate-800 text-slate-500 dark:text-slate-400 border-slate-200 dark:border-slate-700 hover:bg-slate-100 dark:hover:bg-slate-700'}
              ${selected && opt.value === 'P' ? 'ring-emerald-300 dark:ring-emerald-500/40' : ''}
              ${selected && opt.value === 'A' ? 'ring-rose-300 dark:ring-rose-500/40' : ''}
              ${selected && opt.value === 'L' ? 'ring-amber-300 dark:ring-amber-500/40' : ''}
              ${selected && opt.value === 'H' ? 'ring-blue-300 dark:ring-blue-500/40' : ''}
              ${selected && opt.value === 'NA' ? 'ring-violet-300 dark:ring-violet-500/40' : ''}
            `}
          >
            {opt.short}
          </button>
        )
      })}
    </div>
  )
}

// ─── MOBILE STUDENT CARD (day-wise) ──────────────────────────────────────────
function StudentCard({ student, idx, onStatusChange }) {
  const { fg, bg } = classColor(student.name)
  const initial = student.name.charAt(0).toUpperCase()
  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      <div className="flex items-center gap-3 px-4 py-3.5">
        {/* Avatar */}
        <span
          className="w-9 h-9 rounded-xl flex-shrink-0 flex items-center justify-center text-[13px] font-bold"
          style={{ background: bg, color: fg }}
        >
          {initial}
        </span>
        <div className="flex-1 min-w-0">
          <p className="text-[13px] font-bold text-slate-800 dark:text-slate-100 truncate">{student.name}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500">{student.registration_no}</p>
        </div>
        <StatusBadge status={student.att_status} />
      </div>
      {/* Status Selector */}
      <div className="px-4 pb-3.5 flex items-center gap-2">
        <span className="text-[11px] text-slate-400 dark:text-slate-500 font-semibold flex-shrink-0">Mark:</span>
        <StatusRadio value={student.att_status} onChange={(v) => onStatusChange(student.stu_id, v)} />
      </div>
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function DayFilterDrawer({ open, onClose, filters, setFilters, onShow, loading, errors }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl max-h-[90vh] overflow-y-auto"
        style={{ animation: 'drawerUp .25s ease' }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
        <div className="flex justify-center pt-3 pb-1">
          <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
        </div>
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Attendance Filters</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5 space-y-4">
          <Field label="Attendance Date" error={errors.date} required>
            <input
              type="date"
              value={filters.date}
              onChange={e => setFilters(p => ({ ...p, date: e.target.value }))}
              className="w-full pl-3 pr-3 py-2 text-[13px] rounded-lg border outline-none
                bg-white text-slate-800 border-slate-200 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]"
            />
          </Field>
          <Field label="Select Class" error={errors.className} required>
            <NativeSelect
              value={filters.className}
              onChange={e => setFilters(p => ({ ...p, className: e.target.value }))}
              placeholder="-- Select Class --"
              error={errors.className}
            >
              {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Session">
            <NativeSelect
              value={filters.session}
              onChange={e => setFilters(p => ({ ...p, session: e.target.value }))}
              placeholder="-- Select Session --"
            >
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
        </div>
        <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex gap-3">
          <button type="button" onClick={onClose}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
            Cancel
          </button>
          <button type="button" onClick={() => { onShow(); onClose() }} disabled={loading}
            className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white
              bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 disabled:opacity-70 transition-all">
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
            Show Attendance
          </button>
        </div>
      </div>
    </>
  )
}

// ─── DAY-WISE VIEW ────────────────────────────────────────────────────────────
function DayWiseView() {
  const [filters, setFilters]       = useState({ date: '', className: '', session: SESSIONS[3] })
  const [students, setStudents]     = useState([])
  const [loading, setLoading]       = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [smsSending, setSmsSending] = useState(false)
  const [shown, setShown]           = useState(false)
  const [errors, setErrors]         = useState({})
  const [filterOpen, setFilterOpen] = useState(false)
  const [search, setSearch]         = useState('')
  const [toast, setToast]           = useState(null)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // Validate and fetch
  const handleShow = useCallback(() => {
    const err = {}
    if (!filters.date) err.date = 'Select attendance date'
    if (!filters.className) err.className = 'Select a class'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')
    setTimeout(() => {
      setStudents(generateStudents(filters.className))
      setShown(true)
      setLoading(false)
      showToast(`Loaded students for ${filters.className} — ${filters.date}`)
    }, 700)
  }, [filters])

  const handleReset = () => {
    setFilters({ date: '', className: '', session: SESSIONS[3] })
    setStudents([]); setShown(false); setErrors({}); setSearch('')
  }

  // Bulk mark all students
  const bulkMark = (status) => {
    setStudents(prev => prev.map(s => ({ ...s, att_status: status })))
  }

  // Per-student status change
  const onStatusChange = useCallback((stuId, status) => {
    setStudents(prev => prev.map(s => s.stu_id === stuId ? { ...s, att_status: status } : s))
  }, [])

  const handleSubmit = () => {
    setSubmitting(true)
    setTimeout(() => { setSubmitting(false); showToast('Attendance saved successfully!') }, 900)
  }

  const handleSMS = () => {
    setSmsSending(true)
    setTimeout(() => { setSmsSending(false); showToast('SMS sent to absent/leave students!') }, 1100)
  }

  // Filtered students
  const filtered = useMemo(() => {
    if (!search) return students
    const q = search.toLowerCase()
    return students.filter(s =>
      s.name.toLowerCase().includes(q) ||
      s.registration_no.toLowerCase().includes(q)
    )
  }, [students, search])

  // Counts
  const counts = useMemo(() => ({
    total:   students.length,
    present: students.filter(s => s.att_status === 'P').length,
    absent:  students.filter(s => s.att_status === 'A').length,
    leave:   students.filter(s => s.att_status === 'L').length,
  }), [students])

  const hasResults = shown && students.length > 0

  return (
    <div className="space-y-4">
      {/* ── DESKTOP Filter Card ── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <CalendarDays className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Day-wise Attendance</span>
        </div>
        <div className="p-5">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 items-end">
            <Field label="Attendance Date" error={errors.date} required>
              <input
                type="date"
                value={filters.date}
                onChange={e => { setFilters(p => ({ ...p, date: e.target.value })); setErrors(p => ({ ...p, date: undefined })) }}
                className={`w-full pl-3 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                  bg-white text-slate-800 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                  dark:bg-[#1e2238] dark:text-slate-200
                  ${errors.date ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
              />
            </Field>
            <Field label="Select Class" error={errors.className} required>
              <NativeSelect
                value={filters.className}
                onChange={e => { setFilters(p => ({ ...p, className: e.target.value })); setErrors(p => ({ ...p, className: undefined })) }}
                placeholder="-- Select Class --"
                error={errors.className}
              >
                {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Session">
              <NativeSelect
                value={filters.session}
                onChange={e => setFilters(p => ({ ...p, session: e.target.value }))}
                disabled
              >
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>
            <div className="flex gap-2">
              <button type="button" onClick={handleShow} disabled={loading}
                className="flex-1 flex items-center justify-center gap-2 py-2 rounded-xl text-[13px] font-semibold text-white
                  bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700
                  transition-all active:scale-95 disabled:opacity-70">
                {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
                Show
              </button>
              <button type="button" onClick={handleReset}
                className="flex items-center justify-center px-3 py-2 rounded-xl text-[13px]
                  bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
                <RefreshCw className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* ── MOBILE Filter Bar ── */}
      <div className="flex sm:hidden gap-2">
        <button type="button" onClick={() => setFilterOpen(true)}
          className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
            bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {filters.className ? `${filters.className}${filters.date ? ` · ${filters.date}` : ''}` : 'Set Filters'}
        </button>
        {hasResults && (
          <button type="button" onClick={handleReset}
            className="px-3.5 py-2.5 rounded-xl bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300">
            <RefreshCw className="w-4 h-4" />
          </button>
        )}
      </div>

      <DayFilterDrawer
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        filters={filters}
        setFilters={setFilters}
        onShow={handleShow}
        loading={loading}
        errors={errors}
      />

      {/* ── Loading Skeleton ── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-14 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.15 }} />
          ))}
        </div>
      )}

      {/* ── Results ── */}
      {hasResults && !loading && (
        <>
          {/* Summary */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={Users}      label="Total Students" value={counts.total}   color="blue"    />
            <SummaryCard icon={UserCheck}  label="Present"        value={counts.present} color="emerald" />
            <SummaryCard icon={UserX}      label="Absent"         value={counts.absent}  color="rose"    />
            <SummaryCard icon={Clock}      label="On Leave"       value={counts.leave}   color="amber"   />
          </div>

          {/* Attendance Card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <GraduationCap className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">{filters.className}</span>
                <span className="text-[13px] text-slate-400">· {filters.date}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                  {students.length} students
                </span>
              </div>
              <div className="relative w-full sm:w-52 flex-shrink-0">
                <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  value={search}
                  onChange={e => setSearch(e.target.value)}
                  placeholder="Search student…"
                  className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                    bg-white text-slate-700 border-slate-200 placeholder-slate-300 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] dark:placeholder-slate-600"
                />
                {search && <button onClick={() => setSearch('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400"><X className="w-3.5 h-3.5" /></button>}
              </div>
            </div>

            {/* Bulk Mark Header (desktop) */}
            <div className="hidden sm:flex items-center gap-3 px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-amber-50/40 dark:bg-amber-500/[0.04]">
              <span className="text-[12px] font-bold text-slate-600 dark:text-slate-400 flex-shrink-0">Mark All:</span>
              <div className="flex flex-wrap gap-2">
                {ATT_STATUS.map(opt => {
                  const s = STATUS_STYLES[opt.value]
                  return (
                    <button key={opt.value} type="button" onClick={() => bulkMark(opt.value)}
                      className={`px-3 py-1 rounded-lg text-[12px] font-bold border transition-all hover:scale-105 active:scale-95 ${s.bg} ${s.text} ${s.border}`}>
                      {opt.label}
                    </button>
                  )
                })}
              </div>
              <span className="ml-auto flex items-center gap-1 text-[11px] text-slate-400 dark:text-slate-500">
                <Info className="w-3.5 h-3.5" /> Click to bulk-mark all students
              </span>
            </div>

            {/* Mobile Bulk Mark */}
            <div className="sm:hidden px-4 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
              <p className="text-[11px] font-bold text-slate-500 dark:text-slate-400 mb-2 uppercase tracking-wide">Mark All Students:</p>
              <div className="flex flex-wrap gap-2">
                {ATT_STATUS.map(opt => {
                  const s = STATUS_STYLES[opt.value]
                  return (
                    <button key={opt.value} type="button" onClick={() => bulkMark(opt.value)}
                      className={`px-3 py-1.5 rounded-lg text-[12px] font-bold border ${s.bg} ${s.text} ${s.border}`}>
                      {opt.label}
                    </button>
                  )
                })}
              </div>
            </div>

            {/* ── DESKTOP TABLE ── */}
            <div className="hidden md:block overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                    {['S.No.', 'Adm. No.', 'Student Name', 'Attendance Status'].map((h, i) => (
                      <th key={i} className={`px-4 py-2.5 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap
                        ${i === 0 ? 'text-center w-14' : i === 3 ? 'text-left' : 'text-left'}`}>
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((stu, i) => (
                    <tr key={stu.stu_id}
                      className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
                      <td className="px-4 py-3 text-center text-[12px] text-slate-400 tabular-nums">{i + 1}</td>
                      <td className="px-4 py-3 text-[12px] font-mono text-slate-500 dark:text-slate-400">{stu.registration_no}</td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-2.5">
                          <span
                            className="w-7 h-7 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold"
                            style={{ background: classColor(stu.name).bg, color: classColor(stu.name).fg }}
                          >
                            {stu.name.charAt(0)}
                          </span>
                          <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200">{stu.name}</span>
                        </div>
                      </td>
                      <td className="px-4 py-3">
                        <StatusRadio value={stu.att_status} onChange={(v) => onStatusChange(stu.stu_id, v)} />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* ── MOBILE CARDS ── */}
            <div className="md:hidden p-4 space-y-3">
              {filtered.map((stu, i) => (
                <StudentCard key={stu.stu_id} student={stu} idx={i + 1} onStatusChange={onStatusChange} />
              ))}
            </div>

            {/* Table Footer / Actions */}
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
              <p className="text-[12px] text-slate-400 dark:text-slate-500">
                Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
                <span className="font-semibold text-slate-700 dark:text-slate-300">{students.length}</span> students
              </p>
              <div className="flex gap-2 w-full sm:w-auto">
                <button type="button" onClick={handleSubmit} disabled={submitting}
                  className="flex-1 sm:flex-none flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl text-[13px] font-semibold text-white
                    bg-emerald-600 hover:bg-emerald-700 shadow-md shadow-emerald-500/20 transition-all active:scale-95 disabled:opacity-70">
                  {submitting ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />}
                  Submit
                </button>
                <button type="button" onClick={handleSMS} disabled={smsSending}
                  className="flex-1 sm:flex-none flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl text-[13px] font-semibold text-white
                    bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 transition-all active:scale-95 disabled:opacity-70">
                  {smsSending ? <Loader2 className="w-4 h-4 animate-spin" /> : <Send className="w-4 h-4" />}
                  Send SMS
                </button>
              </div>
            </div>
          </div>
        </>
      )}

      {/* ── Empty State ── */}
      {!hasResults && !loading && (
        <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400 dark:text-slate-600">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
            <CalendarDays className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No attendance sheet loaded</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select a date &amp; class, then click <strong>Show</strong>.
            </p>
          </div>
        </div>
      )}

      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}

// ─── MONTH-WISE VIEW ──────────────────────────────────────────────────────────
function MonthWiseView() {
  const [filters, setFilters] = useState({ className: '', term: '', session: '', month: '' })
  const [students, setStudents] = useState([])
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [shown, setShown] = useState(false)
  const [errors, setErrors] = useState({})
  const [search, setSearch] = useState('')
  const [toast, setToast] = useState(null)
  const [filterOpen, setFilterOpen] = useState(false)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  const handleShow = useCallback(() => {
    const err = {}
    if (!filters.className) err.className = 'Select a class'
    if (!filters.month) err.month = 'Select month'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')
    setTimeout(() => {
      setStudents(generateMonthData(filters.className, filters.month))
      setShown(true)
      setLoading(false)
      showToast(`Attendance sheet loaded for ${filters.className} — ${filters.month}`)
    }, 700)
  }, [filters])

  const handleReset = () => {
    setFilters({ className: '', term: '', session: '', month: '' })
    setStudents([]); setShown(false); setErrors({}); setSearch('')
  }

  const updateStudent = useCallback((stuId, field, value) => {
    setStudents(prev => prev.map(s => s.stu_id === stuId ? { ...s, [field]: value } : s))
  }, [])

  const handleSave = () => {
    setSaving(true)
    setTimeout(() => { setSaving(false); showToast('Monthly attendance saved!') }, 900)
  }

  const filtered = useMemo(() => {
    if (!search) return students
    const q = search.toLowerCase()
    return students.filter(s => s.name.toLowerCase().includes(q) || s.registration_no.toLowerCase().includes(q))
  }, [students, search])

  const hasResults = shown && students.length > 0

  // Mobile month filter drawer
  const MonthFilterDrawer = () => {
    if (!filterOpen) return null
    return (
      <>
        <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={() => setFilterOpen(false)} />
        <div
          className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl max-h-[90vh] overflow-y-auto"
          style={{ animation: 'drawerUp .25s ease' }}
        >
          <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
          <div className="flex justify-center pt-3 pb-1">
            <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
          </div>
          <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
            <div className="flex items-center gap-2">
              <SlidersHorizontal className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
              <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Month-wise Filters</span>
            </div>
            <button onClick={() => setFilterOpen(false)} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
              <X className="w-4 h-4" />
            </button>
          </div>
          <div className="px-5 py-5 space-y-4">
            <Field label="Select Class" error={errors.className} required>
              <NativeSelect value={filters.className} onChange={e => setFilters(p => ({ ...p, className: e.target.value }))} placeholder="-- Select Class --">
                {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Term">
              <NativeSelect value={filters.term} onChange={e => setFilters(p => ({ ...p, term: e.target.value }))} placeholder="-- Select Term --">
                {TERMS.map(t => <option key={t} value={t}>{t}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Session">
              <NativeSelect value={filters.session} onChange={e => setFilters(p => ({ ...p, session: e.target.value }))} placeholder="-- Select Session --">
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Month & Year" error={errors.month} required>
              <NativeSelect value={filters.month} onChange={e => setFilters(p => ({ ...p, month: e.target.value }))} placeholder="-- Select Month --">
                {MONTHS.map(m => <option key={m} value={m}>{m}</option>)}
              </NativeSelect>
            </Field>
          </div>
          <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex gap-3">
            <button type="button" onClick={() => setFilterOpen(false)}
              className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300">
              Cancel
            </button>
            <button type="button" onClick={() => { handleShow(); setFilterOpen(false) }} disabled={loading}
              className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white
                bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 disabled:opacity-70 transition-all">
              {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
              Get Sheet
            </button>
          </div>
        </div>
      </>
    )
  }

  return (
    <div className="space-y-4">
      {/* ── DESKTOP Filter ── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-violet-500 flex-shrink-0" />
          <Calendar className="w-4 h-4 text-violet-600 dark:text-violet-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Month-wise Attendance</span>
        </div>
        <div className="p-5">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
            <Field label="Select Class" error={errors.className} required>
              <NativeSelect value={filters.className} onChange={e => { setFilters(p => ({ ...p, className: e.target.value })); setErrors(p => ({...p, className: undefined})) }}
                placeholder="-- Select Class --" error={errors.className}>
                {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Term">
              <NativeSelect value={filters.term} onChange={e => setFilters(p => ({ ...p, term: e.target.value }))} placeholder="-- Select Term --">
                {TERMS.map(t => <option key={t} value={t}>{t}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Session">
              <NativeSelect value={filters.session} onChange={e => setFilters(p => ({ ...p, session: e.target.value }))} placeholder="-- Select Session --" disabled>
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Month & Year" error={errors.month} required>
              <NativeSelect value={filters.month} onChange={e => { setFilters(p => ({ ...p, month: e.target.value })); setErrors(p => ({...p, month: undefined})) }}
                placeholder="-- Select Month --" error={errors.month}>
                {MONTHS.map(m => <option key={m} value={m}>{m}</option>)}
              </NativeSelect>
            </Field>
          </div>
          <div className="flex gap-2">
            <button type="button" onClick={handleShow} disabled={loading}
              className="flex items-center gap-2 px-5 py-2 rounded-xl text-[13px] font-semibold text-white
                bg-violet-600 hover:bg-violet-700 shadow-md shadow-violet-500/20 transition-all active:scale-95 disabled:opacity-70">
              {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileText className="w-4 h-4" />}
              Get Attendance Sheet
            </button>
            <button type="button" onClick={handleReset}
              className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
              <RefreshCw className="w-3.5 h-3.5" /> Reset
            </button>
          </div>
        </div>
      </div>

      {/* ── MOBILE Filter Bar ── */}
      <div className="flex sm:hidden gap-2">
        <button type="button" onClick={() => setFilterOpen(true)}
          className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
            bg-violet-600 text-white shadow-md shadow-violet-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {filters.className && filters.month
            ? `${filters.className} · ${filters.month}`
            : 'Set Filters'}
        </button>
        {hasResults && (
          <button type="button" onClick={handleReset}
            className="px-3.5 py-2.5 rounded-xl bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300">
            <RefreshCw className="w-4 h-4" />
          </button>
        )}
      </div>

      <MonthFilterDrawer />

      {/* Loading */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          {[...Array(6)].map((_, i) => (
            <div key={i} className="h-12 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.12 }} />
          ))}
        </div>
      )}

      {/* Results */}
      {hasResults && !loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
          {/* Header */}
          <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
            <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
              <span className="w-1 h-5 rounded-full bg-violet-500 flex-shrink-0" />
              <BookOpen className="w-4 h-4 text-violet-600 dark:text-violet-400 flex-shrink-0" />
              <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">{filters.className}</span>
              <span className="text-[13px] text-slate-400">· {filters.month}</span>
              <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-violet-100 text-violet-700 dark:bg-violet-500/15 dark:text-violet-400">
                {students.length} students
              </span>
            </div>
            <div className="relative w-full sm:w-52 flex-shrink-0">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search student…"
                className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                  bg-white text-slate-700 border-slate-200 placeholder-slate-300 focus:border-violet-400 focus:ring-2 focus:ring-violet-100
                  dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] dark:placeholder-slate-600"
              />
              {search && <button onClick={() => setSearch('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400"><X className="w-3.5 h-3.5" /></button>}
            </div>
          </div>

          {/* ── DESKTOP TABLE ── */}
          <div className="hidden md:block overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                  <th className="px-4 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 w-14">S.No.</th>
                  <th className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500">Reg. No.</th>
                  <th className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500">Student Name</th>
                  <th className="px-4 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500">Attendance / Working Days</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((stu, i) => (
                  <tr key={stu.stu_id}
                    className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
                    <td className="px-4 py-3 text-center text-[12px] text-slate-400 tabular-nums">{i + 1}</td>
                    <td className="px-4 py-3 text-[12px] font-mono text-slate-500 dark:text-slate-400">{stu.registration_no}</td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2.5">
                        <span
                          className="w-7 h-7 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold"
                          style={{ background: classColor(stu.name).bg, color: classColor(stu.name).fg }}
                        >
                          {stu.name.charAt(0)}
                        </span>
                        <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200">{stu.name}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center justify-center gap-2">
                        <input
                          type="text"
                          value={stu.attendance}
                          onChange={e => updateStudent(stu.stu_id, 'attendance', e.target.value.replace(/[^0-9.]/g, ''))}
                          className="w-16 text-center py-1.5 text-[13px] font-semibold rounded-lg border border-slate-200 dark:border-[rgba(99,102,241,0.25)]
                            bg-white dark:bg-[#1e2238] text-slate-800 dark:text-slate-200 focus:border-violet-400 focus:ring-2 focus:ring-violet-100 outline-none"
                        />
                        <span className="text-[12px] text-slate-400 font-semibold">Out of</span>
                        <input
                          type="text"
                          value={stu.work_Day}
                          onChange={e => updateStudent(stu.stu_id, 'work_Day', e.target.value.replace(/[^0-9.]/g, ''))}
                          className="w-16 text-center py-1.5 text-[13px] font-semibold rounded-lg border border-slate-200 dark:border-[rgba(99,102,241,0.25)]
                            bg-white dark:bg-[#1e2238] text-slate-800 dark:text-slate-200 focus:border-violet-400 focus:ring-2 focus:ring-violet-100 outline-none"
                        />
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* ── MOBILE CARDS (month-wise) ── */}
          <div className="md:hidden p-4 space-y-3">
            {filtered.map((stu, i) => (
              <div key={stu.stu_id}
                className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] p-4 shadow-sm">
                <div className="flex items-center gap-3 mb-3">
                  <span
                    className="w-9 h-9 rounded-xl flex-shrink-0 flex items-center justify-center text-[13px] font-bold"
                    style={{ background: classColor(stu.name).bg, color: classColor(stu.name).fg }}
                  >
                    {stu.name.charAt(0)}
                  </span>
                  <div className="flex-1 min-w-0">
                    <p className="text-[13px] font-bold text-slate-800 dark:text-slate-100 truncate">{stu.name}</p>
                    <p className="text-[11px] text-slate-400">{stu.registration_no}</p>
                  </div>
                  <span className="text-[11px] font-semibold text-slate-400">#{i + 1}</span>
                </div>
                {/* Attendance Input */}
                <div className="flex items-center gap-2 bg-slate-50 dark:bg-slate-800/60 rounded-xl p-3">
                  <div className="flex-1 text-center">
                    <p className="text-[10px] font-bold uppercase tracking-wide text-violet-600 dark:text-violet-400 mb-1">Days Present</p>
                    <input
                      type="text"
                      value={stu.attendance}
                      onChange={e => updateStudent(stu.stu_id, 'attendance', e.target.value.replace(/[^0-9.]/g, ''))}
                      className="w-full text-center py-2 text-[18px] font-bold rounded-lg border border-violet-200 dark:border-violet-500/30
                        bg-white dark:bg-[#1e2238] text-violet-700 dark:text-violet-300 focus:border-violet-400 outline-none"
                    />
                  </div>
                  <div className="text-slate-400 font-bold text-[14px] px-1">of</div>
                  <div className="flex-1 text-center">
                    <p className="text-[10px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 mb-1">Working Days</p>
                    <input
                      type="text"
                      value={stu.work_Day}
                      onChange={e => updateStudent(stu.stu_id, 'work_Day', e.target.value.replace(/[^0-9.]/g, ''))}
                      className="w-full text-center py-2 text-[18px] font-bold rounded-lg border border-slate-200 dark:border-slate-700
                        bg-white dark:bg-[#1e2238] text-slate-700 dark:text-slate-200 focus:border-blue-400 outline-none"
                    />
                  </div>
                  {/* % Badge */}
                  <div className="flex-shrink-0 text-center">
                    <p className="text-[10px] font-bold text-slate-400 mb-1">%</p>
                    <span className={`text-[14px] font-bold tabular-nums ${
                      stu.work_Day && parseFloat(stu.attendance) / parseFloat(stu.work_Day) >= 0.75
                        ? 'text-emerald-600 dark:text-emerald-400'
                        : 'text-rose-600 dark:text-rose-400'
                    }`}>
                      {stu.work_Day ? Math.round((parseFloat(stu.attendance) / parseFloat(stu.work_Day)) * 100) : 0}%
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Footer */}
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
            <p className="text-[12px] text-slate-400 dark:text-slate-500">
              {filtered.length} student{filtered.length !== 1 ? 's' : ''} listed
            </p>
            <button type="button" onClick={handleSave} disabled={saving}
              className="w-full sm:w-auto flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl text-[13px] font-semibold text-white
                bg-violet-600 hover:bg-violet-700 shadow-md shadow-violet-500/20 transition-all active:scale-95 disabled:opacity-70">
              {saving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />}
              Save Attendance
            </button>
          </div>
        </div>
      )}

      {!hasResults && !loading && (
        <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400 dark:text-slate-600">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
            <Calendar className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No attendance sheet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select class &amp; month, then click <strong>Get Attendance Sheet</strong>.
            </p>
          </div>
        </div>
      )}

      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}

// ─── MAIN PAGE ────────────────────────────────────────────────────────────────
export default function StudentAttendance() {
  // 'day' | 'month'
  const [mode, setMode] = useState('day')

  const MODES = [
    { value: 'day',   label: 'Day-wise',   icon: CalendarDays, desc: 'Mark daily attendance', color: 'blue'   },
    { value: 'month', label: 'Month-wise', icon: Calendar,     desc: 'Enter monthly totals',  color: 'violet' },
  ]

  return (
    <div className="space-y-5 pb-10">
      {/* Page Title */}
      <div>
        <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
          <School className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
          Student Attendance
        </h1>
        <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
          Manage day-wise or month-wise student attendance.
        </p>
      </div>

      {/* Mode Selector */}
      <div className="grid grid-cols-2 gap-3">
        {MODES.map(m => {
          const Icon = m.icon
          const active = mode === m.value
          const colorMap = {
            blue:   { active: 'border-blue-400 bg-blue-50 dark:bg-blue-500/10 dark:border-blue-500/50', icon: 'bg-blue-100 dark:bg-blue-500/20 text-blue-600 dark:text-blue-400', title: 'text-blue-700 dark:text-blue-300' },
            violet: { active: 'border-violet-400 bg-violet-50 dark:bg-violet-500/10 dark:border-violet-500/50', icon: 'bg-violet-100 dark:bg-violet-500/20 text-violet-600 dark:text-violet-400', title: 'text-violet-700 dark:text-violet-300' },
          }
          const c = colorMap[m.color]
          return (
            <button
              key={m.value}
              type="button"
              onClick={() => setMode(m.value)}
              className={`relative flex items-center gap-3 p-4 rounded-2xl border-2 text-left transition-all
                ${active
                  ? `${c.active} shadow-md`
                  : 'border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] hover:border-slate-300 dark:hover:border-[rgba(99,102,241,0.3)]'
                }`}
            >
              <span className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 transition-all
                ${active ? c.icon : 'bg-slate-100 dark:bg-slate-800 text-slate-400'}`}>
                <Icon className="w-5 h-5" />
              </span>
              <div className="min-w-0">
                <p className={`text-[14px] font-bold leading-tight transition-colors
                  ${active ? c.title : 'text-slate-700 dark:text-slate-300'}`}>
                  {m.label}
                </p>
                <p className="text-[11px] text-slate-400 dark:text-slate-500 hidden sm:block mt-0.5">{m.desc}</p>
              </div>
              {active && (
                <span className={`absolute top-2.5 right-2.5 w-5 h-5 rounded-full flex items-center justify-center
                  ${m.color === 'blue' ? 'bg-blue-500' : 'bg-violet-500'}`}>
                  <Check className="w-3 h-3 text-white" />
                </span>
              )}
            </button>
          )
        })}
      </div>

      {/* Tab Content */}
      {mode === 'day'   && <DayWiseView   key="day"   />}
      {mode === 'month' && <MonthWiseView key="month" />}
    </div>
  )
}
