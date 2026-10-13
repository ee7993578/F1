/**
 * ArrangementReport.jsx
 * Folder: src/pages/Reports/Configuration/ArrangementReport.jsx
 *
 * Converts legacy ASPX "Arrangement Report" page to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Timetable dropdown filter
 *  - Show report button
 *  - Excel export
 *  - Desktop: dense ERP-style data table
 *  - Mobile: card-based layout with expandable details
 *  - Loading states, empty states, toast notifications
 *  - Mobile filter drawer
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Filter, RefreshCw, Eye,
  AlertCircle, X, Check, Loader2, ChevronDown,
  SlidersHorizontal, Search, FileSpreadsheet,
  CalendarDays, Clock, BookOpen, User2,
  BarChart3, ChevronRight, Info, TrendingUp,
  School2, Building2, MapPin, Hash,
  Layers, BookMarked, UserCheck
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ───────────────────────────────────────────────────────

const TIMETABLES = [
  { id: '1', label: '2024-25 Morning Shift' },
  { id: '2', label: '2024-25 Afternoon Shift' },
  { id: '3', label: '2025-26 Morning Shift' },
  { id: '4', label: '2025-26 Afternoon Shift' },
]

const SCHOOL_INFO = {
  name: 'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

// Arrangement data keyed by timetable id
const ARRANGEMENT_DATA = {
  '1': [
    { period: 1, day: 'Monday',    class: 'Class IX-A',  subject: 'Mathematics',    originalFaculty: 'Mr. Sharma',   arrangementFaculty: 'Mrs. Gupta',    reason: 'Leave',     room: '101' },
    { period: 2, day: 'Monday',    class: 'Class VIII-B',subject: 'Science',        originalFaculty: 'Mrs. Verma',   arrangementFaculty: 'Mr. Patel',     reason: 'Training',  room: '205' },
    { period: 3, day: 'Tuesday',   class: 'Class X-A',   subject: 'English',        originalFaculty: 'Mr. Singh',    arrangementFaculty: 'Mrs. Mehta',    reason: 'Leave',     room: '302' },
    { period: 1, day: 'Tuesday',   class: 'Class VII-A', subject: 'Hindi',          originalFaculty: 'Mrs. Joshi',   arrangementFaculty: 'Mr. Sharma',    reason: 'Meeting',   room: '110' },
    { period: 4, day: 'Wednesday', class: 'Class VI-B',  subject: 'Social Science', originalFaculty: 'Mr. Rawat',    arrangementFaculty: 'Mrs. Joshi',    reason: 'Leave',     room: '202' },
    { period: 2, day: 'Wednesday', class: 'Class XI-A',  subject: 'Physics',        originalFaculty: 'Mrs. Pande',   arrangementFaculty: 'Mr. Rawat',     reason: 'Sick',      room: 'Lab1' },
    { period: 5, day: 'Thursday',  class: 'Class XII-B', subject: 'Chemistry',      originalFaculty: 'Mr. Kumar',    arrangementFaculty: 'Mrs. Pande',    reason: 'Leave',     room: 'Lab2' },
    { period: 3, day: 'Thursday',  class: 'Class V-A',   subject: 'EVS',            originalFaculty: 'Mrs. Negi',    arrangementFaculty: 'Mr. Kumar',     reason: 'Training',  room: '105' },
    { period: 1, day: 'Friday',    class: 'Class IV-A',  subject: 'Mathematics',    originalFaculty: 'Mr. Tiwari',   arrangementFaculty: 'Mrs. Verma',    reason: 'Leave',     room: '106' },
    { period: 6, day: 'Friday',    class: 'Class III-A', subject: 'English',        originalFaculty: 'Mrs. Bhatt',   arrangementFaculty: 'Mr. Tiwari',    reason: 'Meeting',   room: '107' },
  ],
  '2': [
    { period: 1, day: 'Monday',    class: 'Class IX-B',  subject: 'Science',        originalFaculty: 'Mrs. Chauhan', arrangementFaculty: 'Mr. Mishra',    reason: 'Leave',     room: '201' },
    { period: 3, day: 'Monday',    class: 'Class X-B',   subject: 'Mathematics',    originalFaculty: 'Mr. Mishra',   arrangementFaculty: 'Mrs. Chauhan',  reason: 'Sick',      room: '301' },
    { period: 2, day: 'Tuesday',   class: 'Class XI-B',  subject: 'Biology',        originalFaculty: 'Mrs. Tripathi',arrangementFaculty: 'Mr. Dubey',     reason: 'Training',  room: 'Lab3' },
    { period: 4, day: 'Wednesday', class: 'Class XII-A', subject: 'Accountancy',    originalFaculty: 'Mr. Dubey',    arrangementFaculty: 'Mrs. Tripathi', reason: 'Leave',     room: '401' },
    { period: 5, day: 'Thursday',  class: 'Class VII-B', subject: 'Computer',       originalFaculty: 'Mrs. Yadav',   arrangementFaculty: 'Mr. Soni',      reason: 'Meeting',   room: 'Lab4' },
    { period: 1, day: 'Friday',    class: 'Class VI-A',  subject: 'Art',            originalFaculty: 'Mr. Soni',     arrangementFaculty: 'Mrs. Yadav',    reason: 'Leave',     room: '108' },
  ],
  '3': [
    { period: 2, day: 'Monday',    class: 'Class IX-A',  subject: 'Mathematics',    originalFaculty: 'Mr. Arora',    arrangementFaculty: 'Mrs. Batra',    reason: 'Leave',     room: '101' },
    { period: 1, day: 'Tuesday',   class: 'Class X-A',   subject: 'Physics',        originalFaculty: 'Mrs. Batra',   arrangementFaculty: 'Mr. Arora',     reason: 'Training',  room: 'Lab1' },
    { period: 3, day: 'Wednesday', class: 'Class VIII-A',subject: 'Hindi',          originalFaculty: 'Mr. Kapoor',   arrangementFaculty: 'Mrs. Malhotra', reason: 'Sick',      room: '205' },
    { period: 4, day: 'Thursday',  class: 'Class XI-A',  subject: 'Chemistry',      originalFaculty: 'Mrs. Malhotra',arrangementFaculty: 'Mr. Kapoor',    reason: 'Leave',     room: 'Lab2' },
    { period: 5, day: 'Friday',    class: 'Class XII-A', subject: 'English',        originalFaculty: 'Mr. Khanna',   arrangementFaculty: 'Mrs. Saxena',   reason: 'Meeting',   room: '302' },
    { period: 2, day: 'Friday',    class: 'Class VII-A', subject: 'Social Science', originalFaculty: 'Mrs. Saxena',  arrangementFaculty: 'Mr. Khanna',    reason: 'Leave',     room: '203' },
    { period: 1, day: 'Monday',    class: 'Class VI-A',  subject: 'Science',        originalFaculty: 'Mr. Chandra',  arrangementFaculty: 'Mrs. Iyer',     reason: 'Leave',     room: '110' },
    { period: 6, day: 'Tuesday',   class: 'Class V-A',   subject: 'Mathematics',    originalFaculty: 'Mrs. Iyer',    arrangementFaculty: 'Mr. Chandra',   reason: 'Training',  room: '105' },
  ],
  '4': [
    { period: 1, day: 'Monday',    class: 'Class IX-B',  subject: 'English',        originalFaculty: 'Mrs. Das',     arrangementFaculty: 'Mr. Roy',       reason: 'Leave',     room: '201' },
    { period: 2, day: 'Wednesday', class: 'Class X-B',   subject: 'Biology',        originalFaculty: 'Mr. Roy',      arrangementFaculty: 'Mrs. Das',      reason: 'Sick',      room: 'Lab3' },
    { period: 3, day: 'Thursday',  class: 'Class XI-B',  subject: 'Mathematics',    originalFaculty: 'Mrs. Sen',     arrangementFaculty: 'Mr. Bose',      reason: 'Training',  room: '301' },
    { period: 4, day: 'Friday',    class: 'Class XII-B', subject: 'Physics',        originalFaculty: 'Mr. Bose',     arrangementFaculty: 'Mrs. Sen',      reason: 'Leave',     room: 'Lab1' },
  ],
}

// ─── HELPERS ──────────────────────────────────────────────────────────────────

const DAY_ORDER = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']

const REASON_STYLES = {
  Leave:    'bg-rose-50 text-rose-700 border border-rose-200 dark:bg-rose-500/10 dark:text-rose-400 dark:border-rose-500/20',
  Training: 'bg-blue-50 text-blue-700 border border-blue-200 dark:bg-blue-500/10 dark:text-blue-400 dark:border-blue-500/20',
  Sick:     'bg-amber-50 text-amber-700 border border-amber-200 dark:bg-amber-500/10 dark:text-amber-400 dark:border-amber-500/20',
  Meeting:  'bg-violet-50 text-violet-700 border border-violet-200 dark:bg-violet-500/10 dark:text-violet-400 dark:border-violet-500/20',
}

const SUBJECT_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' },
  { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' },
  { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' },
  { fg: '#dc2626', bg: '#fee2e2' },
  { fg: '#0369a1', bg: '#e0f2fe' },
  { fg: '#be185d', bg: '#fce7f3' },
]

const subjectColor = (name = '') => SUBJECT_COLORS[(name.charCodeAt(0) ?? 0) % SUBJECT_COLORS.length]

const formatSubjectAbbr = (name = '') => name.slice(0, 3).toUpperCase()

// Sort rows by day order then period
const sortRows = (rows) =>
  [...rows].sort((a, b) => {
    const dayDiff = DAY_ORDER.indexOf(a.day) - DAY_ORDER.indexOf(b.day)
    return dayDiff !== 0 ? dayDiff : a.period - b.period
  })

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
          ${error
            ? 'border-rose-400 ring-2 ring-rose-100'
            : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
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

// ─── SUMMARY STAT CARD ────────────────────────────────────────────────────────
function SummaryCard({ icon: Icon, label, value, color }) {
  const colors = {
    blue:    'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400',
    emerald: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400',
    amber:   'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400',
    violet:  'bg-violet-50 text-violet-600 dark:bg-violet-500/10 dark:text-violet-400',
    rose:    'bg-rose-50 text-rose-600 dark:bg-rose-500/10 dark:text-rose-400',
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

// ─── SCHOOL HEADER BANNER ─────────────────────────────────────────────────────
function SchoolHeader({ timetableLabel }) {
  return (
    <div className="rounded-2xl border border-blue-100 dark:border-[rgba(99,102,241,0.2)] bg-gradient-to-r from-blue-50 via-white to-indigo-50 dark:from-[#1a1f35] dark:via-[#1e2238] dark:to-[#1a1f35] px-6 py-5 text-center shadow-sm">
      <div className="flex items-center justify-center gap-2 mb-1">
        <Building2 className="w-5 h-5 text-blue-600 dark:text-blue-400 flex-shrink-0" />
        <h2 className="text-[16px] sm:text-[18px] font-extrabold text-slate-800 dark:text-slate-100 tracking-tight leading-snug">
          {SCHOOL_INFO.name}
        </h2>
      </div>
      <div className="flex items-center justify-center gap-1.5 text-[12px] text-slate-500 dark:text-slate-400 mb-2">
        <MapPin className="w-3.5 h-3.5 flex-shrink-0" />
        <span>{SCHOOL_INFO.address}</span>
      </div>
      <div className="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-amber-100 dark:bg-amber-500/15 border border-amber-200 dark:border-amber-500/25">
        <CalendarDays className="w-3.5 h-3.5 text-amber-700 dark:text-amber-400" />
        <span className="text-[12px] font-bold text-amber-700 dark:text-amber-400">{timetableLabel}</span>
      </div>
      <p className="mt-2 text-[13px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400">
        Arrangement Report
      </p>
    </div>
  )
}

// ─── REASON BADGE ─────────────────────────────────────────────────────────────
function ReasonBadge({ reason }) {
  const style = REASON_STYLES[reason] || 'bg-slate-100 text-slate-600 border border-slate-200'
  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold ${style}`}>
      {reason}
    </span>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ row, idx }) {
  const { fg, bg } = subjectColor(row.subject)

  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
      {/* S.No */}
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{idx}</td>

      {/* Period */}
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center justify-center w-7 h-7 rounded-lg text-[12px] font-bold bg-blue-50 dark:bg-blue-500/10 text-blue-700 dark:text-blue-400">
          {row.period}
        </span>
      </td>

      {/* Day */}
      <td className="px-4 py-3 text-center">
        <span className="text-[12px] font-semibold text-slate-600 dark:text-slate-300 whitespace-nowrap">{row.day}</span>
      </td>

      {/* Class */}
      <td className="px-4 py-3">
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[12px] font-semibold bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 whitespace-nowrap">
          <Layers className="w-3 h-3 flex-shrink-0" />
          {row.class}
        </span>
      </td>

      {/* Subject */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-2">
          <span
            className="w-7 h-7 rounded-lg flex-shrink-0 flex items-center justify-center text-[9px] font-bold"
            style={{ background: bg, color: fg }}
          >
            {formatSubjectAbbr(row.subject)}
          </span>
          <span className="text-[12px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{row.subject}</span>
        </div>
      </td>

      {/* Original Faculty */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-1.5">
          <User2 className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
          <span className="text-[12px] text-slate-500 dark:text-slate-400 whitespace-nowrap line-through decoration-rose-400">{row.originalFaculty}</span>
        </div>
      </td>

      {/* Arrangement Faculty */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-1.5">
          <UserCheck className="w-3.5 h-3.5 text-emerald-500 flex-shrink-0" />
          <span className="text-[12px] font-semibold text-emerald-700 dark:text-emerald-400 whitespace-nowrap">{row.arrangementFaculty}</span>
        </div>
      </td>

      {/* Reason */}
      <td className="px-4 py-3 text-center">
        <ReasonBadge reason={row.reason} />
      </td>

      {/* Room */}
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-lg text-[11px] font-bold bg-indigo-50 dark:bg-indigo-500/10 text-indigo-700 dark:text-indigo-400 border border-indigo-100 dark:border-indigo-500/20">
          <Hash className="w-2.5 h-2.5" />{row.room}
        </span>
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────
function MobileCard({ row }) {
  const [expanded, setExpanded] = useState(false)
  const { fg, bg } = subjectColor(row.subject)

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header — always visible */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        {/* Subject badge */}
        <span
          className="w-10 h-10 rounded-xl flex-shrink-0 flex items-center justify-center text-[10px] font-bold"
          style={{ background: bg, color: fg }}
        >
          {formatSubjectAbbr(row.subject)}
        </span>

        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">
            {row.subject}
            <span className="ml-2 text-[11px] font-semibold text-slate-400 dark:text-slate-500">{row.class}</span>
          </p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5 truncate">
            <span className="font-medium text-blue-600 dark:text-blue-400">{row.day}</span>
            {' · '}Period {row.period}
            {' · '}
            <UserCheck className="w-3 h-3 inline text-emerald-500 mr-0.5" />
            <span className="text-emerald-600 dark:text-emerald-400 font-semibold">{row.arrangementFaculty}</span>
          </p>
        </div>

        <div className="flex flex-col items-end gap-1 flex-shrink-0">
          <ReasonBadge reason={row.reason} />
        </div>

        <span className={`w-5 h-5 flex items-center justify-center ml-1 text-slate-400 transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Expanded detail */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 py-4 space-y-3">
          <div className="grid grid-cols-2 gap-3">

            {/* Period */}
            <div className="rounded-xl bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20 p-3 text-center">
              <Clock className="w-4 h-4 text-blue-600 dark:text-blue-400 mx-auto mb-1" />
              <p className="text-[22px] font-bold text-blue-700 dark:text-blue-300 tabular-nums leading-tight">{row.period}</p>
              <p className="text-[10px] font-bold uppercase tracking-wide text-blue-600 dark:text-blue-400 mt-0.5">Period</p>
            </div>

            {/* Room */}
            <div className="rounded-xl bg-indigo-50 dark:bg-indigo-500/10 border border-indigo-100 dark:border-indigo-500/20 p-3 text-center">
              <BookMarked className="w-4 h-4 text-indigo-600 dark:text-indigo-400 mx-auto mb-1" />
              <p className="text-[22px] font-bold text-indigo-700 dark:text-indigo-300 tabular-nums leading-tight">{row.room}</p>
              <p className="text-[10px] font-bold uppercase tracking-wide text-indigo-600 dark:text-indigo-400 mt-0.5">Room</p>
            </div>
          </div>

          {/* Faculty swap */}
          <div className="rounded-xl bg-slate-50 dark:bg-slate-800/40 border border-slate-100 dark:border-slate-700/50 p-3">
            <p className="text-[10px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 mb-2">Faculty Change</p>
            <div className="flex items-center gap-2">
              <div className="flex-1 flex items-center gap-2 bg-rose-50 dark:bg-rose-500/10 rounded-lg p-2">
                <User2 className="w-3.5 h-3.5 text-rose-500 flex-shrink-0" />
                <div className="min-w-0">
                  <p className="text-[10px] text-slate-400 dark:text-slate-500">Original</p>
                  <p className="text-[12px] font-semibold text-slate-600 dark:text-slate-300 truncate line-through decoration-rose-400">{row.originalFaculty}</p>
                </div>
              </div>
              <ChevronRight className="w-4 h-4 text-slate-400 flex-shrink-0" />
              <div className="flex-1 flex items-center gap-2 bg-emerald-50 dark:bg-emerald-500/10 rounded-lg p-2">
                <UserCheck className="w-3.5 h-3.5 text-emerald-500 flex-shrink-0" />
                <div className="min-w-0">
                  <p className="text-[10px] text-slate-400 dark:text-slate-500">Arranged</p>
                  <p className="text-[12px] font-semibold text-emerald-700 dark:text-emerald-400 truncate">{row.arrangementFaculty}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, timetable, setTimetable, onShow, loading, errors }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl"
        style={{ animation: 'drawerUp .25s ease' }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
        <div className="flex justify-center pt-3 pb-1">
          <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
        </div>
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Select Time Table</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5">
          <Field label="Time Table" error={errors.timetable} required>
            <NativeSelect
              value={timetable}
              onChange={e => setTimetable(e.target.value)}
              placeholder="-- Select Time Table --"
              error={errors.timetable}
            >
              {TIMETABLES.map(t => <option key={t.id} value={t.id}>{t.label}</option>)}
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
            Show Report
          </button>
        </div>
      </div>
    </>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function ArrangementReport() {
  const [timetable,      setTimetable]      = useState('')
  const [rows,           setRows]           = useState([])
  const [loading,        setLoading]        = useState(false)
  const [exporting,      setExporting]      = useState(false)
  const [filterOpen,     setFilterOpen]     = useState(false)
  const [search,         setSearch]         = useState('')
  const [errors,         setErrors]         = useState({})
  const [toast,          setToast]          = useState(null)
  const [shown,          setShown]          = useState(false)
  const [shownLabel,     setShownLabel]     = useState('')

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Fetch (simulate API) ─────────────────────────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}
    if (!timetable) err.timetable = 'Please select a time table'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')

    setTimeout(() => {
      const data = sortRows(ARRANGEMENT_DATA[timetable] || [])
      const label = TIMETABLES.find(t => t.id === timetable)?.label || timetable
      setRows(data)
      setShownLabel(label)
      setShown(true)
      setLoading(false)
      showToast(`Loaded ${data.length} arrangement records.`)
    }, 650)
  }, [timetable])

  const handleReset = () => {
    setTimetable(''); setRows([]); setSearch('')
    setErrors({}); setShown(false); setShownLabel('')
  }

  // ── Excel Export placeholder ─────────────────────────────────────────────
  const handleExcel = () => {
    if (rows.length === 0) { showToast('No data to export. Show report first.', 'error'); return }
    setExporting(true)
    setTimeout(() => {
      setExporting(false)
      showToast('Excel export ready! (API integration pending)')
    }, 1200)
  }

  // ── Search filter ────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    if (!search) return rows
    const q = search.toLowerCase()
    return rows.filter(r =>
      r.class.toLowerCase().includes(q) ||
      r.subject.toLowerCase().includes(q) ||
      r.day.toLowerCase().includes(q) ||
      r.originalFaculty.toLowerCase().includes(q) ||
      r.arrangementFaculty.toLowerCase().includes(q) ||
      r.reason.toLowerCase().includes(q) ||
      r.room.toLowerCase().includes(q)
    )
  }, [rows, search])

  // ── Summary counts ───────────────────────────────────────────────────────
  const summaryStats = useMemo(() => {
    const uniqueFaculty = new Set(filtered.map(r => r.arrangementFaculty)).size
    const uniqueClasses = new Set(filtered.map(r => r.class)).size
    const reasonCounts = filtered.reduce((acc, r) => { acc[r.reason] = (acc[r.reason] || 0) + 1; return acc }, {})
    const topReason = Object.entries(reasonCounts).sort((a, b) => b[1] - a[1])[0]?.[0] || '—'
    return { total: filtered.length, uniqueFaculty, uniqueClasses, topReason }
  }, [filtered])

  const hasResults = shown && rows.length > 0
  const activeFilters = timetable ? 1 : 0

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ──────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <CalendarDays className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Arrangement Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Period-wise faculty arrangement details for substitute classes.
          </p>
        </div>
        {hasResults && (
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

      {/* ── DESKTOP Filter Card ──────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Filter className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Search Filters</span>
        </div>
        <div className="p-5">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 items-end">
            <Field label="Select Time Table" error={errors.timetable} required>
              <NativeSelect
                value={timetable}
                onChange={e => { setTimetable(e.target.value); setErrors(p => ({ ...p, timetable: undefined })) }}
                placeholder="-- Select Time Table --"
                error={errors.timetable}
              >
                {TIMETABLES.map(t => <option key={t.id} value={t.id}>{t.label}</option>)}
              </NativeSelect>
            </Field>

            {/* Spacers */}
            <div />
            <div />

            <div className="flex gap-2">
              <button type="button" onClick={handleShow} disabled={loading}
                className="flex-1 flex items-center justify-center gap-2 py-2 rounded-xl text-[13px] font-semibold text-white
                  bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700
                  transition-all active:scale-95 disabled:opacity-70">
                {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
                Show
              </button>
              <button type="button" onClick={handleReset}
                className="flex items-center justify-center px-3 py-2 rounded-xl text-[13px] font-semibold
                  bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors">
                <RefreshCw className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* ── MOBILE Filter Bar ────────────────────────────────────────────── */}
      <div className="flex sm:hidden gap-2">
        <button type="button" onClick={() => setFilterOpen(true)}
          className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
            bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {timetable ? TIMETABLES.find(t => t.id === timetable)?.label : 'Select Time Table'}
          {activeFilters > 0 && (
            <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilters}</span>
          )}
        </button>
        {hasResults && (
          <button type="button" onClick={handleExcel} disabled={exporting}
            className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl text-[13px] font-semibold
              bg-emerald-600 text-white hover:bg-emerald-700 shadow-sm disabled:opacity-70">
            {exporting ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileSpreadsheet className="w-4 h-4" />}
          </button>
        )}
        {hasResults && (
          <button type="button" onClick={handleReset}
            className="px-3.5 py-2.5 rounded-xl bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300">
            <RefreshCw className="w-4 h-4" />
          </button>
        )}
      </div>

      <FilterDrawer
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        timetable={timetable}
        setTimetable={setTimetable}
        onShow={handleShow}
        loading={loading}
        errors={errors}
      />

      {/* ── Loading Skeleton ─────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          <div className="h-20 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-4" />
          {[...Array(6)].map((_, i) => (
            <div key={i} className="h-12 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.12 }} />
          ))}
        </div>
      )}

      {/* ── Results ─────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School Header */}
          <SchoolHeader timetableLabel={shownLabel} />

          {/* Summary Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={CalendarDays} label="Total Arrangements"  value={summaryStats.total}          color="blue"    />
            <SummaryCard icon={User2}        label="Faculty Arranged"    value={summaryStats.uniqueFaculty}  color="emerald" />
            <SummaryCard icon={Layers}       label="Classes Covered"    value={summaryStats.uniqueClasses}  color="violet"  />
            <SummaryCard icon={AlertCircle}  label="Top Reason"         value={summaryStats.topReason}      color="amber"   />
          </div>

          {/* Results card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <BookOpen className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Arrangement Details</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownLabel}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                  {filtered.length} record{filtered.length !== 1 ? 's' : ''}
                </span>
              </div>

              {/* Search */}
              <div className="relative w-full sm:w-60 flex-shrink-0">
                <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  value={search}
                  onChange={e => setSearch(e.target.value)}
                  placeholder="Search class, subject, faculty…"
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

            {/* Info hint */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
              <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <p className="text-[12px] text-blue-700 dark:text-blue-400">
                Shows period-wise arrangement details. Strikethrough = original (absent) faculty. Green = arrangement faculty.
              </p>
            </div>

            {/* Reason legend */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 flex-wrap border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)]">
              <span className="text-[11px] text-slate-400 dark:text-slate-500 font-medium mr-1">Reason:</span>
              {Object.entries(REASON_STYLES).map(([reason]) => (
                <ReasonBadge key={reason} reason={reason} />
              ))}
            </div>

            {/* ── DESKTOP TABLE ── */}
            <div className="hidden md:block overflow-x-auto">
              {filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No records match your search.</span>
                </div>
              ) : (
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                      {['S.No.', 'Period', 'Day', 'Class', 'Subject', 'Original Faculty', 'Arrangement Faculty', 'Reason', 'Room'].map((h, i) => (
                        <th key={i} className="px-4 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:w-12">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((row, i) => (
                      <DesktopRow key={`${row.day}-${row.period}-${row.class}`} row={row} idx={i + 1} />
                    ))}

                    {/* Summary footer row */}
                    <tr className="bg-blue-50 dark:bg-indigo-500/[0.07] border-t-2 border-blue-200 dark:border-indigo-500/30">
                      <td className="px-4 py-3 text-center text-[12px] text-blue-500 dark:text-blue-400">—</td>
                      <td className="px-4 py-3" colSpan={8}>
                        <span className="text-[13px] font-bold text-blue-700 dark:text-blue-300 flex items-center gap-2">
                          <TrendingUp className="w-4 h-4" />
                          Total {filtered.length} arrangements · {summaryStats.uniqueFaculty} faculty · {summaryStats.uniqueClasses} classes
                        </span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              )}
            </div>

            {/* ── MOBILE CARDS ── */}
            <div className="md:hidden p-4 space-y-3">
              {filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No records match your search.</span>
                </div>
              ) : (
                <>
                  <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5 pb-1">
                    <Info className="w-3.5 h-3.5 flex-shrink-0" />
                    Tap a card to see detailed faculty & room info.
                  </p>

                  {filtered.map((row, i) => (
                    <MobileCard key={`${row.day}-${row.period}-${row.class}-${i}`} row={row} idx={i + 1} />
                  ))}

                  {/* Mobile Summary Footer */}
                  <div className="rounded-xl border-2 border-blue-200 dark:border-indigo-500/30 bg-blue-50 dark:bg-indigo-500/[0.07] p-4">
                    <p className="text-[12px] font-bold uppercase tracking-wide text-blue-700 dark:text-blue-400 mb-3 flex items-center gap-2">
                      <TrendingUp className="w-4 h-4" /> Summary — {filtered.length} Records
                    </p>
                    <div className="grid grid-cols-3 gap-2">
                      <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                        <p className="text-[20px] font-bold text-blue-700 dark:text-blue-300 tabular-nums">{summaryStats.total}</p>
                        <p className="text-[10px] font-semibold text-blue-600 dark:text-blue-400">Arrangements</p>
                      </div>
                      <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                        <p className="text-[20px] font-bold text-emerald-700 dark:text-emerald-300 tabular-nums">{summaryStats.uniqueFaculty}</p>
                        <p className="text-[10px] font-semibold text-emerald-600 dark:text-emerald-400">Faculty</p>
                      </div>
                      <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                        <p className="text-[20px] font-bold text-violet-700 dark:text-violet-300 tabular-nums">{summaryStats.uniqueClasses}</p>
                        <p className="text-[10px] font-semibold text-violet-600 dark:text-violet-400">Classes</p>
                      </div>
                    </div>
                  </div>
                </>
              )}
            </div>

            {/* Table Footer */}
            <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
              <p className="text-[12px] text-slate-400 dark:text-slate-500">
                Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
                <span className="font-semibold text-slate-700 dark:text-slate-300">{rows.length}</span> records
              </p>
              {search && (
                <button onClick={() => setSearch('')}
                  className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
                  <X className="w-3 h-3" /> Clear search
                </button>
              )}
            </div>
          </div>
        </>
      )}

      {/* ── Empty State ──────────────────────────────────────────────────── */}
      {!hasResults && !loading && (
        <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400 dark:text-slate-600">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
            <BarChart3 className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No report generated yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select a time table and click <strong>Show</strong> to generate the arrangement report.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
