/**
 * ActivityReport.jsx
 * Folder: src/pages/Reports/ActivityReport.jsx
 *
 * Converts legacy ASPX "Activity Report" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session + Class dropdown filters (both required)
 *  - Show report button + Excel export
 *  - School name / session / class header in report
 *  - Activity log table with S.No auto-numbering
 *  - Mobile: collapsible cards with expandable details
 *  - Desktop: dense ERP-style table
 *  - Mobile filter drawer
 *  - Loading skeleton, empty state, toast notifications
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Filter, RefreshCw, Eye,
  AlertCircle, X, Check, Loader2, ChevronDown,
  SlidersHorizontal, Search, BookOpen,
  FileSpreadsheet, BarChart3, ChevronRight,
  Building2, MapPin, Activity, Calendar,
  User, Clock, Tag, CheckCircle2, XCircle,
  Info, TrendingUp, GraduationCap,
  ListChecks, BookMarked, Pencil, Award
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ───────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const CLASSES = [
  'Nursery', 'LKG', 'UKG',
  'Class I', 'Class II', 'Class III', 'Class IV', 'Class V',
  'Class VI', 'Class VII', 'Class VIII',
  'Class IX', 'Class X', 'Class XI', 'Class XII',
]

const SCHOOL_INFO = {
  name: 'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

// Activity types with icons and colors
const ACTIVITY_TYPES = {
  'Exam':        { color: 'blue',   icon: Pencil      },
  'Assignment':  { color: 'amber',  icon: BookMarked  },
  'Project':     { color: 'violet', icon: ListChecks  },
  'Sports':      { color: 'emerald',icon: Award       },
  'Event':       { color: 'cyan',   icon: Calendar    },
  'Test':        { color: 'rose',   icon: BookOpen    },
}

const STATUS_CONFIG = {
  'Completed':   { bg: 'bg-emerald-100 dark:bg-emerald-500/15', text: 'text-emerald-700 dark:text-emerald-400', dot: 'bg-emerald-500' },
  'Pending':     { bg: 'bg-amber-100 dark:bg-amber-500/15',    text: 'text-amber-700 dark:text-amber-400',    dot: 'bg-amber-500'   },
  'In Progress': { bg: 'bg-blue-100 dark:bg-blue-500/15',      text: 'text-blue-700 dark:text-blue-400',      dot: 'bg-blue-500'    },
  'Cancelled':   { bg: 'bg-rose-100 dark:bg-rose-500/15',      text: 'text-rose-700 dark:text-rose-400',      dot: 'bg-rose-500'    },
}

// Generate dummy activity data for class+session combo
const generateActivities = (cls, session) => {
  const seed = (cls.charCodeAt(0) + session.charCodeAt(0)) % 7
  const activities = [
    { activity: 'Half Yearly Exam',       type: 'Exam',       subject: 'All Subjects',    date: '12 Oct 2024', teacher: 'Mrs. Sharma',    status: 'Completed',   marks: 100, obtained: 87 },
    { activity: 'Science Project',        type: 'Project',    subject: 'Science',         date: '18 Oct 2024', teacher: 'Mr. Verma',      status: 'Completed',   marks: 50,  obtained: 44 },
    { activity: 'Math Assignment',        type: 'Assignment', subject: 'Mathematics',     date: '22 Oct 2024', teacher: 'Mrs. Gupta',     status: 'Pending',     marks: 20,  obtained: null },
    { activity: 'Annual Sports Day',      type: 'Sports',     subject: 'Physical Edu.',   date: '01 Nov 2024', teacher: 'Mr. Singh',      status: 'In Progress', marks: 30,  obtained: null },
    { activity: 'Unit Test - English',    type: 'Test',       subject: 'English',         date: '05 Nov 2024', teacher: 'Mrs. Nair',      status: 'Completed',   marks: 25,  obtained: 21 },
    { activity: 'Hindi Essay Writing',    type: 'Assignment', subject: 'Hindi',           date: '10 Nov 2024', teacher: 'Mr. Tiwari',     status: 'Completed',   marks: 15,  obtained: 13 },
    { activity: 'Annual Function',        type: 'Event',      subject: 'Co-curricular',   date: '15 Nov 2024', teacher: 'Mrs. Mishra',    status: 'Pending',     marks: null, obtained: null },
    { activity: 'Pre-Board Exam',         type: 'Exam',       subject: 'All Subjects',    date: '20 Nov 2024', teacher: 'Mrs. Sharma',    status: 'Pending',     marks: 100, obtained: null },
    { activity: 'Social Science Project', type: 'Project',    subject: 'Social Science',  date: '25 Nov 2024', teacher: 'Mr. Kumar',      status: 'In Progress', marks: 40,  obtained: null },
    { activity: 'Computer Lab Test',      type: 'Test',       subject: 'Computer',        date: '28 Nov 2024', teacher: 'Mr. Pandey',     status: 'Cancelled',   marks: 30,  obtained: null },
    { activity: 'Art & Craft Exhibition', type: 'Event',      subject: 'Art',             date: '02 Dec 2024', teacher: 'Mrs. Rani',      status: 'Pending',     marks: 25,  obtained: null },
    { activity: 'Final Term Exam',        type: 'Exam',       subject: 'All Subjects',    date: '10 Dec 2024', teacher: 'Mrs. Sharma',    status: 'Pending',     marks: 100, obtained: null },
  ]
  // Shuffle based on seed for variety
  const start = seed
  return [...activities.slice(start), ...activities.slice(0, start)]
}

// ─── HELPERS ──────────────────────────────────────────────────────────────────
const TYPE_COLORS = {
  blue:    { fg: '#1d4ed8', bg: '#dbeafe' },
  amber:   { fg: '#d97706', bg: '#fef3c7' },
  violet:  { fg: '#7c3aed', bg: '#ede9fe' },
  emerald: { fg: '#059669', bg: '#d1fae5' },
  cyan:    { fg: '#0891b2', bg: '#cffafe' },
  rose:    { fg: '#e11d48', bg: '#fee2e2' },
}

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
          disabled:opacity-50 disabled:cursor-not-allowed
          ${error
            ? 'border-rose-400 ring-2 ring-rose-100'
            : 'border-slate-200'
          }`}
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
      <label className="text-[12px] font-semibold text-slate-500 uppercase tracking-wide">
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
      {type === 'success'
        ? <Check className="w-4 h-4 flex-shrink-0" />
        : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75 hover:opacity-100" /></button>
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(10px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
    </div>
  )
}

// ─── STATUS BADGE ─────────────────────────────────────────────────────────────
function StatusBadge({ status }) {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG['Pending']
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-bold ${cfg.bg} ${cfg.text}`}>
      <span className={`w-1.5 h-1.5 rounded-full ${cfg.dot} flex-shrink-0`} />
      {status}
    </span>
  )
}

// ─── TYPE BADGE ───────────────────────────────────────────────────────────────
function TypeBadge({ type }) {
  const cfg = ACTIVITY_TYPES[type] || ACTIVITY_TYPES['Event']
  const color = TYPE_COLORS[cfg.color]
  const IconComp = cfg.icon
  return (
    <span
      className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-bold"
      style={{ background: color.bg, color: color.fg }}
    >
      <IconComp className="w-3 h-3 flex-shrink-0" />
      {type}
    </span>
  )
}

// ─── SUMMARY STAT CARD ────────────────────────────────────────────────────────
function SummaryCard({ icon: Icon, label, value, color }) {
  const colors = {
    blue:    'bg-blue-50 text-blue-600',
    emerald: 'bg-emerald-50 text-emerald-600',
    amber:   'bg-amber-50 text-amber-600',
    violet:  'bg-violet-50 text-violet-600',
    rose:    'bg-rose-50 text-rose-600',
  }
  return (
    <div className="flex items-center gap-3 rounded-xl border border-slate-100 bg-white px-4 py-3 shadow-sm flex-1 min-w-0">
      <span className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 ${colors[color]}`}>
        <Icon className="w-5 h-5" />
      </span>
      <div className="min-w-0">
        <p className="text-[20px] font-bold text-slate-800 tabular-nums leading-tight">{value}</p>
        <p className="text-[11px] text-slate-500 truncate">{label}</p>
      </div>
    </div>
  )
}

// ─── SCHOOL HEADER BANNER ─────────────────────────────────────────────────────
function SchoolHeader({ session, cls }) {
  return (
    <div className="rounded-2xl border border-blue-100 bg-gradient-to-r from-blue-50 via-white to-indigo-50 px-6 py-5 text-center shadow-sm">
      <div className="flex items-center justify-center gap-2 mb-1">
        <Building2 className="w-5 h-5 text-blue-600 flex-shrink-0" />
        <h2 className="text-[16px] sm:text-[18px] font-extrabold text-slate-800 tracking-tight leading-snug">
          {SCHOOL_INFO.name}
        </h2>
      </div>
      <div className="flex items-center justify-center gap-1.5 text-[12px] text-slate-500 mb-2">
        <MapPin className="w-3.5 h-3.5 flex-shrink-0" />
        <span>{SCHOOL_INFO.address}</span>
      </div>
      <div className="flex items-center justify-center gap-2 flex-wrap">
        <div className="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-amber-100 border border-amber-200">
          <span className="text-[12px] font-bold text-amber-700">Session: {session}</span>
        </div>
        <div className="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-blue-100 border border-blue-200">
          <GraduationCap className="w-3.5 h-3.5 text-blue-700" />
          <span className="text-[12px] font-bold text-blue-700">Class: {cls}</span>
        </div>
      </div>
      <p className="mt-2 text-[13px] font-bold uppercase tracking-widest text-blue-700">
        Activity Report
      </p>
    </div>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ row, idx }) {
  return (
    <tr className="border-b border-slate-100 hover:bg-slate-50/60 transition-colors">
      {/* S.No */}
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 tabular-nums w-12">{idx}</td>

      {/* Activity Name */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-2">
          <Activity className="w-4 h-4 text-blue-400 flex-shrink-0" />
          <span className="text-[13px] font-semibold text-slate-700 leading-snug">{row.activity}</span>
        </div>
      </td>

      {/* Type */}
      <td className="px-4 py-3">
        <TypeBadge type={row.type} />
      </td>

      {/* Subject */}
      <td className="px-4 py-3">
        <span className="text-[12px] text-slate-600 font-medium">{row.subject}</span>
      </td>

      {/* Date */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-1.5 text-[12px] text-slate-500">
          <Calendar className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
          {row.date}
        </div>
      </td>

      {/* Teacher */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-1.5 text-[12px] text-slate-600">
          <User className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
          {row.teacher}
        </div>
      </td>

      {/* Marks */}
      <td className="px-4 py-3 text-center">
        {row.marks != null ? (
          <span className="inline-flex items-center justify-center px-2.5 py-1 rounded-lg text-[12px] font-semibold bg-blue-50 text-blue-700 tabular-nums">
            {row.obtained != null ? `${row.obtained}/${row.marks}` : `—/${row.marks}`}
          </span>
        ) : (
          <span className="text-[12px] text-slate-300">—</span>
        )}
      </td>

      {/* Status */}
      <td className="px-4 py-3">
        <StatusBadge status={row.status} />
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────
function MobileCard({ row, idx }) {
  const [expanded, setExpanded] = useState(false)
  const cfg = ACTIVITY_TYPES[row.type] || ACTIVITY_TYPES['Event']
  const typeColor = TYPE_COLORS[cfg.color]
  const IconComp = cfg.icon

  return (
    <div className="rounded-xl border border-slate-200 bg-white overflow-hidden shadow-sm">
      {/* Header */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-start gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 transition-colors"
      >
        {/* Type icon badge */}
        <span
          className="w-9 h-9 rounded-xl flex-shrink-0 flex items-center justify-center mt-0.5"
          style={{ background: typeColor.bg, color: typeColor.fg }}
        >
          <IconComp className="w-4 h-4" />
        </span>

        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 leading-tight pr-1">{row.activity}</p>
          <div className="flex items-center gap-2 mt-1 flex-wrap">
            <span className="text-[11px] text-slate-400 font-medium">{row.subject}</span>
            <span className="w-1 h-1 rounded-full bg-slate-300" />
            <span className="text-[11px] text-slate-400">{row.date}</span>
          </div>
        </div>

        <div className="flex flex-col items-end gap-1.5 flex-shrink-0">
          <StatusBadge status={row.status} />
          <span className={`transition-transform duration-200 ${expanded ? 'rotate-90' : ''}`}>
            <ChevronRight className="w-4 h-4 text-slate-400" />
          </span>
        </div>
      </button>

      {/* Expanded detail */}
      {expanded && (
        <div className="border-t border-slate-100 px-4 pt-3 pb-4 space-y-3 bg-slate-50/40">
          <div className="grid grid-cols-2 gap-2">
            {/* Type */}
            <div className="rounded-lg bg-white border border-slate-100 p-2.5">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-1">Type</p>
              <TypeBadge type={row.type} />
            </div>
            {/* Teacher */}
            <div className="rounded-lg bg-white border border-slate-100 p-2.5">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-1">Teacher</p>
              <p className="text-[12px] font-semibold text-slate-700 flex items-center gap-1">
                <User className="w-3 h-3 text-slate-400 flex-shrink-0" />
                {row.teacher}
              </p>
            </div>
            {/* Date */}
            <div className="rounded-lg bg-white border border-slate-100 p-2.5">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-1">Date</p>
              <p className="text-[12px] font-semibold text-slate-700 flex items-center gap-1">
                <Calendar className="w-3 h-3 text-slate-400 flex-shrink-0" />
                {row.date}
              </p>
            </div>
            {/* Marks */}
            <div className="rounded-lg bg-white border border-slate-100 p-2.5">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-1">Marks</p>
              {row.marks != null ? (
                <div>
                  <p className="text-[16px] font-bold text-blue-700 tabular-nums leading-tight">
                    {row.obtained != null ? row.obtained : '—'}
                    <span className="text-[12px] font-medium text-slate-400">/{row.marks}</span>
                  </p>
                  {row.obtained != null && (
                    <div className="mt-1 h-1 rounded-full bg-slate-200 overflow-hidden">
                      <div
                        className="h-full rounded-full bg-blue-500"
                        style={{ width: `${Math.round((row.obtained / row.marks) * 100)}%` }}
                      />
                    </div>
                  )}
                </div>
              ) : (
                <p className="text-[12px] text-slate-400">N/A</p>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, session, setSession, cls, setCls, onShow, loading, errors }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white border-t border-slate-200 shadow-2xl"
        style={{ animation: 'drawerUp .25s ease' }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
        <div className="flex justify-center pt-3 pb-1">
          <div className="w-10 h-1 rounded-full bg-slate-200" />
        </div>
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100">
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-4 h-4 text-blue-600" />
            <span className="text-[14px] font-bold text-slate-800">Filters</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5 space-y-4">
          <Field label="Session" error={errors.session} required>
            <NativeSelect
              value={session}
              onChange={e => setSession(e.target.value)}
              placeholder="-- Select Session --"
              error={errors.session}
            >
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Class" error={errors.cls} required>
            <NativeSelect
              value={cls}
              onChange={e => setCls(e.target.value)}
              placeholder="-- Select Class --"
              error={errors.cls}
            >
              {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
            </NativeSelect>
          </Field>
        </div>
        <div className="px-5 py-4 border-t border-slate-100 flex gap-3">
          <button type="button" onClick={onClose}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 transition-colors">
            Cancel
          </button>
          <button type="button" onClick={() => { onShow(); onClose() }} disabled={loading}
            className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white
              bg-blue-600 hover:bg-blue-700 disabled:opacity-70 transition-all">
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
            Show Report
          </button>
        </div>
      </div>
    </>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function ActivityReport() {
  const [session,      setSession]      = useState('')
  const [cls,          setCls]          = useState('')
  const [rows,         setRows]         = useState([])
  const [loading,      setLoading]      = useState(false)
  const [exporting,    setExporting]    = useState(false)
  const [filterOpen,   setFilterOpen]   = useState(false)
  const [search,       setSearch]       = useState('')
  const [errors,       setErrors]       = useState({})
  const [toast,        setToast]        = useState(null)
  const [shown,        setShown]        = useState(false)
  const [shownSession, setShownSession] = useState('')
  const [shownClass,   setShownClass]   = useState('')
  const [statusFilter, setStatusFilter] = useState('All')

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Fetch (simulate API) ──────────────────────────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}
    if (!session) err.session = 'Please select a session'
    if (!cls)     err.cls     = 'Please select a class'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')
    setStatusFilter('All')

    setTimeout(() => {
      const data = generateActivities(cls, session)
      setRows(data)
      setShownSession(session)
      setShownClass(cls)
      setShown(true)
      setLoading(false)
      showToast(`Loaded ${data.length} activities for ${cls} — ${session}`)
    }, 700)
  }, [session, cls])

  const handleReset = () => {
    setSession(''); setCls(''); setRows([])
    setSearch(''); setErrors({}); setShown(false)
    setShownSession(''); setShownClass(''); setStatusFilter('All')
  }

  // ── Excel Export ─────────────────────────────────────────────────────────
  const handleExport = () => {
    if (rows.length === 0) { showToast('No data to export. Show report first.', 'error'); return }
    setExporting(true)
    setTimeout(() => {
      setExporting(false)
      showToast('Excel export ready! (API integration pending)')
    }, 1200)
  }

  // ── Filters ───────────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    let result = rows
    if (statusFilter !== 'All') result = result.filter(r => r.status === statusFilter)
    if (search) {
      const q = search.toLowerCase()
      result = result.filter(r =>
        r.activity.toLowerCase().includes(q) ||
        r.subject.toLowerCase().includes(q)  ||
        r.teacher.toLowerCase().includes(q)  ||
        r.type.toLowerCase().includes(q)
      )
    }
    return result
  }, [rows, search, statusFilter])

  // ── Summary stats ─────────────────────────────────────────────────────────
  const stats = useMemo(() => ({
    total:      rows.length,
    completed:  rows.filter(r => r.status === 'Completed').length,
    pending:    rows.filter(r => r.status === 'Pending').length,
    inProgress: rows.filter(r => r.status === 'In Progress').length,
  }), [rows])

  const hasResults   = shown && rows.length > 0
  const activeFilters = [session, cls].filter(Boolean).length

  const STATUS_TABS = ['All', 'Completed', 'Pending', 'In Progress', 'Cancelled']

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ───────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 flex items-center gap-2">
            <Activity className="w-5 h-5 text-blue-600" />
            Activity Report
          </h1>
          <p className="text-[13px] text-slate-500 mt-0.5">
            Class-wise activity log — exams, assignments, projects &amp; events.
          </p>
        </div>
        {hasResults && (
          <button
            type="button"
            onClick={handleExport}
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

      {/* ── DESKTOP Filter Card ──────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 bg-slate-50/70">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Filter className="w-4 h-4 text-blue-600 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 flex-1">Search Filters</span>
        </div>
        <div className="p-5">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 items-end">
            {/* Session */}
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

            {/* Class */}
            <Field label="Class" error={errors.cls} required>
              <NativeSelect
                value={cls}
                onChange={e => { setCls(e.target.value); setErrors(p => ({ ...p, cls: undefined })) }}
                placeholder="-- Select Class --"
                error={errors.cls}
              >
                {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
              </NativeSelect>
            </Field>

            {/* Spacer */}
            <div />

            {/* Buttons */}
            <div className="flex gap-2">
              <button type="button" onClick={handleShow} disabled={loading}
                className="flex-1 flex items-center justify-center gap-2 py-2 rounded-xl text-[13px] font-semibold text-white
                  bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20
                  transition-all active:scale-95 disabled:opacity-70">
                {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
                Show
              </button>
              <button type="button" onClick={handleReset}
                className="flex items-center justify-center px-3 py-2 rounded-xl text-[13px] font-semibold
                  bg-slate-100 text-slate-700 hover:bg-slate-200 transition-colors">
                <RefreshCw className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* ── MOBILE Filter Bar ─────────────────────────────────────────────── */}
      <div className="flex sm:hidden gap-2">
        <button type="button" onClick={() => setFilterOpen(true)}
          className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
            bg-blue-600 text-white shadow-md shadow-blue-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {session && cls
            ? `${cls} · ${session}`
            : session
              ? `Session: ${session}`
              : cls
                ? `Class: ${cls}`
                : 'Select Filters'}
          {activeFilters > 0 && (
            <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilters}</span>
          )}
        </button>
        {hasResults && (
          <button type="button" onClick={handleExport} disabled={exporting}
            className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl text-[13px] font-semibold
              bg-emerald-600 text-white hover:bg-emerald-700 shadow-sm disabled:opacity-70">
            {exporting ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileSpreadsheet className="w-4 h-4" />}
          </button>
        )}
        {hasResults && (
          <button type="button" onClick={handleReset}
            className="px-3.5 py-2.5 rounded-xl bg-slate-100 text-slate-700 hover:bg-slate-200">
            <RefreshCw className="w-4 h-4" />
          </button>
        )}
      </div>

      <FilterDrawer
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        session={session} setSession={setSession}
        cls={cls} setCls={setCls}
        onShow={handleShow}
        loading={loading}
        errors={errors}
      />

      {/* ── Loading Skeleton ─────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 bg-white p-6 space-y-3">
          <div className="h-24 rounded-xl bg-slate-100 animate-pulse mb-4" />
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-4">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="h-16 rounded-xl bg-slate-100 animate-pulse" />
            ))}
          </div>
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-12 rounded-xl bg-slate-100 animate-pulse" style={{ opacity: 1 - i * 0.15 }} />
          ))}
        </div>
      )}

      {/* ── Results ──────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School Header */}
          <SchoolHeader session={shownSession} cls={shownClass} />

          {/* Summary Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={ListChecks}   label="Total Activities" value={stats.total}      color="blue"    />
            <SummaryCard icon={CheckCircle2} label="Completed"        value={stats.completed}  color="emerald" />
            <SummaryCard icon={Clock}        label="Pending"          value={stats.pending}    color="amber"   />
            <SummaryCard icon={Activity}     label="In Progress"      value={stats.inProgress} color="violet"  />
          </div>

          {/* Results card */}
          <div className="rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">

            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 bg-slate-50/70">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <Activity className="w-4 h-4 text-blue-600 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700">Activity Log</span>
                <span className="text-[13px] text-slate-400">· {shownClass} · {shownSession}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 flex-shrink-0">
                  {filtered.length} record{filtered.length !== 1 ? 's' : ''}
                </span>
              </div>
              {/* Search */}
              <div className="relative w-full sm:w-56 flex-shrink-0">
                <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  value={search}
                  onChange={e => setSearch(e.target.value)}
                  placeholder="Search activity, subject…"
                  className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                    bg-white text-slate-700 border-slate-200 placeholder-slate-300
                    focus:border-blue-400 focus:ring-2 focus:ring-blue-100"
                />
                {search && (
                  <button onClick={() => setSearch('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                    <X className="w-3.5 h-3.5" />
                  </button>
                )}
              </div>
            </div>

            {/* Status filter tabs */}
            <div className="flex gap-1.5 px-5 py-2.5 border-b border-slate-100 overflow-x-auto no-scrollbar">
              {STATUS_TABS.map(tab => (
                <button
                  key={tab}
                  type="button"
                  onClick={() => setStatusFilter(tab)}
                  className={`flex-shrink-0 px-3 py-1 rounded-lg text-[12px] font-semibold transition-all
                    ${statusFilter === tab
                      ? 'bg-blue-600 text-white shadow-sm'
                      : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                >
                  {tab}
                  {tab !== 'All' && (
                    <span className={`ml-1.5 text-[10px] font-bold px-1 py-0.5 rounded
                      ${statusFilter === tab ? 'bg-white/25 text-white' : 'bg-slate-200 text-slate-500'}`}>
                      {rows.filter(r => r.status === tab).length}
                    </span>
                  )}
                </button>
              ))}
              <style>{`.no-scrollbar::-webkit-scrollbar{display:none}.no-scrollbar{-ms-overflow-style:none;scrollbar-width:none}`}</style>
            </div>

            {/* Info hint */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 bg-blue-50/20">
              <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <p className="text-[12px] text-blue-700">
                Showing all activities for {shownClass} in session {shownSession}. Click column headers to sort.
              </p>
            </div>

            {/* ── DESKTOP TABLE ── */}
            <div className="hidden md:block overflow-x-auto">
              {filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No records match your search.</span>
                </div>
              ) : (
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-slate-100 bg-slate-50/50">
                      {['S.No.', 'Activity', 'Type', 'Subject', 'Date', 'Teacher', 'Marks', 'Status'].map((h, i) => (
                        <th key={i} className="px-4 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 whitespace-nowrap first:w-12">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((row, i) => (
                      <DesktopRow key={`${row.activity}-${i}`} row={row} idx={i + 1} />
                    ))}
                  </tbody>
                </table>
              )}
            </div>

            {/* ── MOBILE CARDS ── */}
            <div className="md:hidden p-4 space-y-3">
              {filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No records match your search.</span>
                </div>
              ) : (
                <>
                  <p className="text-[11px] text-blue-600 font-medium flex items-center gap-1.5 pb-1">
                    <Info className="w-3.5 h-3.5 flex-shrink-0" />
                    Tap a card to see full activity details.
                  </p>
                  {filtered.map((row, i) => (
                    <MobileCard key={`${row.activity}-${i}`} row={row} idx={i + 1} />
                  ))}
                </>
              )}
            </div>

            {/* Table Footer */}
            <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 bg-slate-50/50">
              <p className="text-[12px] text-slate-400">
                Showing <span className="font-semibold text-slate-700">{filtered.length}</span> of{' '}
                <span className="font-semibold text-slate-700">{rows.length}</span> records
              </p>
              {(search || statusFilter !== 'All') && (
                <button onClick={() => { setSearch(''); setStatusFilter('All') }}
                  className="text-[12px] text-blue-600 hover:underline flex items-center gap-1">
                  <X className="w-3 h-3" /> Clear filters
                </button>
              )}
            </div>
          </div>
        </>
      )}

      {/* ── Empty State ───────────────────────────────────────────────────── */}
      {!hasResults && !loading && (
        <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 flex items-center justify-center">
            <BarChart3 className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500">No report generated yet</p>
            <p className="text-[12px] text-slate-400 mt-1">
              Select a <strong>Session</strong> and <strong>Class</strong>, then click <strong>Show</strong> to generate the activity report.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
