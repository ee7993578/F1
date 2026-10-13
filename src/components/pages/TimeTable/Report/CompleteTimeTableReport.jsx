/**
 * CompleteTimeTableReport.jsx
 * Folder: src/pages/Reports/Configuration/CompleteTimeTableReport.jsx
 *
 * Converts legacy ASPX "Complete TimeTable Report" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session dropdown → loads timetable dropdown
 *  - Timetable dropdown → select specific timetable
 *  - Show Report button with client-side validation
 *  - Desktop: dense professional timetable grid (periods as columns, days as rows, per faculty)
 *  - Mobile: collapsible faculty cards with day-wise accordion
 *  - Print / Export Excel placeholders
 *  - Loading skeleton, empty state, toast notifications
 *  - Same design system as StrengthReport.jsx
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Filter, RefreshCw, Eye, AlertCircle, X, Check, Loader2,
  ChevronDown, ChevronRight, SlidersHorizontal, Search,
  Calendar, Clock, BookOpen, Users, School2, User,
  FileSpreadsheet, Printer, Info, BarChart3, MapPin,
  Building2, GraduationCap, TrendingUp, BookMarked
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ──────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const TIMETABLES_BY_SESSION = {
  '2022-23': [
    { value: 'TT_2022_A', label: 'Annual Timetable 2022-23' },
    { value: 'TT_2022_B', label: 'Revised Timetable (Nov 2022)' },
  ],
  '2023-24': [
    { value: 'TT_2023_A', label: 'Annual Timetable 2023-24' },
    { value: 'TT_2023_B', label: 'Mid-Term Timetable 2023-24' },
    { value: 'TT_2023_C', label: 'Revised Timetable (Feb 2024)' },
  ],
  '2024-25': [
    { value: 'TT_2024_A', label: 'Annual Timetable 2024-25' },
    { value: 'TT_2024_B', label: 'Revised Timetable (Oct 2024)' },
  ],
  '2025-26': [
    { value: 'TT_2025_A', label: 'Annual Timetable 2025-26' },
    { value: 'TT_2025_B', label: 'Interim Timetable (Aug 2025)' },
    { value: 'TT_2025_C', label: 'Current Timetable (Jan 2026)' },
  ],
}

const SCHOOL_INFO = {
  name: 'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
const DAYS_SHORT = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
const PERIODS = ['P1', 'P2', 'P3', 'P4', 'P5', 'P6', 'P7', 'P8']
const PERIOD_TIMES = {
  P1: '8:00–8:45',
  P2: '8:45–9:30',
  P3: '9:30–10:15',
  P4: '10:30–11:15',
  P5: '11:15–12:00',
  P6: '12:00–12:45',
  P7: '1:30–2:15',
  P8: '2:15–3:00',
}

const SUBJECTS = [
  'Mathematics', 'English', 'Hindi', 'Science', 'Social Studies',
  'Physics', 'Chemistry', 'Biology', 'History', 'Geography',
  'Computer', 'Physical Ed', 'Art', 'Music', 'Free Period',
]
const CLASSES = [
  'Nursery-A', 'LKG-A', 'UKG-A',
  'Class I-A', 'Class I-B', 'Class II-A', 'Class III-A',
  'Class IV-A', 'Class V-A', 'Class VI-A', 'Class VI-B',
  'Class VII-A', 'Class VIII-A', 'Class IX-A', 'Class X-A',
  'Class XI-A', 'Class XI-B', 'Class XII-A',
]

// Generate deterministic timetable data per faculty
const rand = (seed, max) => {
  let s = seed * 9301 + 49297
  return (s % 233280) % max
}

const FACULTIES = [
  { id: 1, name: 'Dr. Ramesh Sharma',    subject: 'Mathematics',    dept: 'Science' },
  { id: 2, name: 'Mrs. Priya Nair',       subject: 'English',        dept: 'Languages' },
  { id: 3, name: 'Mr. Suresh Verma',      subject: 'Physics',        dept: 'Science' },
  { id: 4, name: 'Ms. Anjali Singh',      subject: 'Hindi',          dept: 'Languages' },
  { id: 5, name: 'Mr. Vikram Joshi',      subject: 'Chemistry',      dept: 'Science' },
  { id: 6, name: 'Mrs. Kavita Gupta',     subject: 'Biology',        dept: 'Science' },
  { id: 7, name: 'Mr. Deepak Yadav',      subject: 'Social Studies', dept: 'Humanities' },
  { id: 8, name: 'Ms. Pooja Tiwari',      subject: 'Computer',       dept: 'Technology' },
  { id: 9, name: 'Mr. Arun Kumar',        subject: 'Physical Ed',    dept: 'Sports' },
  { id: 10, name: 'Mrs. Sunita Mehta',    subject: 'Geography',      dept: 'Humanities' },
]

function generateTimetable(facultyId, ttId) {
  return DAYS.reduce((acc, day, di) => {
    acc[day] = PERIODS.reduce((pacc, period, pi) => {
      const seed = facultyId * 1000 + di * 100 + pi + ttId.charCodeAt(3)
      const isFree = rand(seed, 5) === 0
      pacc[period] = isFree
        ? { subject: 'Free Period', class: '—', isFree: true }
        : {
            subject: SUBJECTS[rand(seed + 1, SUBJECTS.length - 1)],
            class: CLASSES[rand(seed + 2, CLASSES.length)],
            isFree: false,
          }
      return pacc
    }, {})
    return acc
  }, {})
}

// ─── SUBJECT COLOR MAP ────────────────────────────────────────────────────────
const SUBJECT_COLORS = {
  'Mathematics':    { fg: '#1d4ed8', bg: '#dbeafe', border: '#bfdbfe' },
  'English':        { fg: '#0369a1', bg: '#e0f2fe', border: '#bae6fd' },
  'Hindi':          { fg: '#7c3aed', bg: '#ede9fe', border: '#ddd6fe' },
  'Science':        { fg: '#059669', bg: '#d1fae5', border: '#a7f3d0' },
  'Social Studies': { fg: '#d97706', bg: '#fef3c7', border: '#fde68a' },
  'Physics':        { fg: '#0891b2', bg: '#cffafe', border: '#a5f3fc' },
  'Chemistry':      { fg: '#dc2626', bg: '#fee2e2', border: '#fecaca' },
  'Biology':        { fg: '#16a34a', bg: '#dcfce7', border: '#bbf7d0' },
  'History':        { fg: '#92400e', bg: '#fef3c7', border: '#fde68a' },
  'Geography':      { fg: '#065f46', bg: '#d1fae5', border: '#a7f3d0' },
  'Computer':       { fg: '#1e40af', bg: '#dbeafe', border: '#bfdbfe' },
  'Physical Ed':    { fg: '#b45309', bg: '#fef3c7', border: '#fde68a' },
  'Art':            { fg: '#9d174d', bg: '#fce7f3', border: '#fbcfe8' },
  'Music':          { fg: '#6d28d9', bg: '#ede9fe', border: '#ddd6fe' },
  'Free Period':    { fg: '#6b7280', bg: '#f3f4f6', border: '#e5e7eb' },
}
const getSubjectColor = (subject) =>
  SUBJECT_COLORS[subject] || { fg: '#374151', bg: '#f9fafb', border: '#e5e7eb' }

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

// ─── SCHOOL HEADER BANNER ─────────────────────────────────────────────────────
function SchoolHeader({ session, timetableLabel }) {
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
      <div className="flex flex-wrap items-center justify-center gap-2">
        <div className="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-amber-100 dark:bg-amber-500/15 border border-amber-200 dark:border-amber-500/25">
          <span className="text-[12px] font-bold text-amber-700 dark:text-amber-400">Session: {session}</span>
        </div>
        <div className="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-blue-100 dark:bg-blue-500/15 border border-blue-200 dark:border-blue-500/25">
          <Calendar className="w-3 h-3 text-blue-600 dark:text-blue-400" />
          <span className="text-[12px] font-bold text-blue-700 dark:text-blue-400">{timetableLabel}</span>
        </div>
      </div>
      <p className="mt-2 text-[13px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400">
        Complete TimeTable Report
      </p>
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

// ─── PERIOD CELL (desktop) ────────────────────────────────────────────────────
function PeriodCell({ cell }) {
  if (!cell) return <td className="px-1 py-1.5 text-center"><span className="text-[11px] text-slate-300">—</span></td>
  const { fg, bg } = getSubjectColor(cell.subject)
  if (cell.isFree) {
    return (
      <td className="px-1 py-1.5 text-center">
        <span className="inline-flex items-center justify-center px-1.5 py-0.5 rounded text-[10px] font-medium text-slate-400 dark:text-slate-600 bg-slate-100 dark:bg-slate-800/50 border border-dashed border-slate-200 dark:border-slate-700">
          Free
        </span>
      </td>
    )
  }
  return (
    <td className="px-1 py-1.5 text-center">
      <div
        className="rounded-lg px-1.5 py-1 inline-flex flex-col items-center min-w-[64px] max-w-[80px]"
        style={{ background: bg, border: `1px solid ${getSubjectColor(cell.subject).border}` }}
      >
        <span className="text-[10px] font-bold leading-tight truncate w-full text-center" style={{ color: fg }}>{cell.subject}</span>
        <span className="text-[9px] font-medium text-slate-500 dark:text-slate-400 truncate w-full text-center mt-0.5">{cell.class}</span>
      </div>
    </td>
  )
}

// ─── DESKTOP FACULTY TABLE ────────────────────────────────────────────────────
function FacultyDesktopTable({ faculty, timetable, searchDay }) {
  const days = searchDay ? DAYS.filter(d => d.toLowerCase().includes(searchDay.toLowerCase())) : DAYS

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.12)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm mb-4">
      {/* Faculty Header */}
      <div className="flex items-center gap-3 px-4 py-3 bg-slate-50 dark:bg-white/[0.02] border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
        <div className="w-9 h-9 rounded-xl bg-blue-100 dark:bg-blue-500/15 flex items-center justify-center flex-shrink-0">
          <User className="w-4 h-4 text-blue-600 dark:text-blue-400" />
        </div>
        <div>
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100">{faculty.name}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500">{faculty.subject} · {faculty.dept} Dept.</p>
        </div>
        <div className="ml-auto flex items-center gap-2">
          <span className="text-[11px] font-semibold px-2.5 py-1 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
            {DAYS.length} Days · {PERIODS.length} Periods
          </span>
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="w-full text-left">
          <thead>
            <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.08)] bg-slate-50/50 dark:bg-white/[0.01]">
              <th className="px-4 py-2 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap w-24 sticky left-0 bg-slate-50 dark:bg-[#1a1f35] z-10 border-r border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
                Day
              </th>
              {PERIODS.map(p => (
                <th key={p} className="px-1 py-2 text-center min-w-[72px]">
                  <div className="text-[10px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400">{p}</div>
                  <div className="text-[9px] text-slate-400 dark:text-slate-600 font-normal">{PERIOD_TIMES[p]}</div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {days.map((day, di) => (
              <tr key={day} className={`border-b border-slate-100 dark:border-[rgba(99,102,241,0.06)] ${di % 2 === 0 ? '' : 'bg-slate-50/30 dark:bg-white/[0.01]'} hover:bg-blue-50/30 dark:hover:bg-blue-500/[0.03] transition-colors`}>
                <td className="px-4 py-2 sticky left-0 bg-white dark:bg-[#1a1f35] border-r border-slate-100 dark:border-[rgba(99,102,241,0.1)] z-10">
                  <div className="flex items-center gap-2">
                    <span className={`w-1.5 h-1.5 rounded-full flex-shrink-0 ${di < 5 ? 'bg-blue-400' : 'bg-amber-400'}`} />
                    <span className="text-[12px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{day}</span>
                  </div>
                </td>
                {PERIODS.map(p => (
                  <PeriodCell key={p} cell={timetable[day]?.[p]} />
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

// ─── MOBILE FACULTY CARD ──────────────────────────────────────────────────────
function FacultyMobileCard({ faculty, timetable }) {
  const [expanded, setExpanded] = useState(false)
  const [activeDay, setActiveDay] = useState(DAYS[0])

  const dayPeriods = timetable[activeDay] || {}
  const activeCount = Object.values(dayPeriods).filter(c => !c.isFree).length

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        <div className="w-9 h-9 rounded-xl bg-blue-100 dark:bg-blue-500/15 flex items-center justify-center flex-shrink-0">
          <User className="w-4 h-4 text-blue-600 dark:text-blue-400" />
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{faculty.name}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5 truncate">{faculty.subject} · {faculty.dept}</p>
        </div>
        <span className={`w-6 h-6 flex items-center justify-center text-slate-400 transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Expanded */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          {/* Day tabs */}
          <div className="flex gap-1 px-3 py-2 overflow-x-auto scrollbar-hide border-b border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
            {DAYS.map((day, di) => (
              <button
                key={day}
                type="button"
                onClick={() => setActiveDay(day)}
                className={`flex-shrink-0 px-3 py-1.5 rounded-lg text-[11px] font-semibold transition-all ${
                  activeDay === day
                    ? 'bg-blue-600 text-white shadow-md shadow-blue-500/20 dark:bg-indigo-600'
                    : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-700'
                }`}
              >
                {DAYS_SHORT[di]}
              </button>
            ))}
          </div>

          {/* Day label + active count */}
          <div className="flex items-center justify-between px-4 pt-3 pb-2">
            <span className="text-[13px] font-bold text-slate-700 dark:text-slate-200">{activeDay}</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
              {activeCount} classes
            </span>
          </div>

          {/* Periods list */}
          <div className="px-3 pb-4 space-y-2">
            {PERIODS.map(p => {
              const cell = dayPeriods[p]
              if (!cell) return null
              const { fg, bg, border } = getSubjectColor(cell.subject)
              return (
                <div
                  key={p}
                  className={`flex items-center gap-3 rounded-xl px-3 py-2.5 border ${cell.isFree ? 'border-dashed border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/40' : ''}`}
                  style={cell.isFree ? {} : { background: bg, borderColor: border, borderWidth: '1px' }}
                >
                  {/* Period badge */}
                  <div className="flex flex-col items-center flex-shrink-0 w-10">
                    <span className={`text-[11px] font-bold ${cell.isFree ? 'text-slate-400' : ''}`} style={cell.isFree ? {} : { color: fg }}>{p}</span>
                    <span className="text-[9px] text-slate-400 dark:text-slate-600 font-normal text-center leading-tight">{PERIOD_TIMES[p]}</span>
                  </div>
                  {/* Divider */}
                  <div className={`w-px h-8 rounded-full flex-shrink-0 ${cell.isFree ? 'bg-slate-200 dark:bg-slate-700' : 'bg-current opacity-20'}`} style={cell.isFree ? {} : { color: fg }} />
                  {/* Content */}
                  {cell.isFree ? (
                    <span className="text-[12px] font-medium text-slate-400 dark:text-slate-600 flex-1">Free Period</span>
                  ) : (
                    <div className="flex-1 min-w-0">
                      <p className="text-[13px] font-bold leading-tight truncate" style={{ color: fg }}>{cell.subject}</p>
                      <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5 truncate">{cell.class}</p>
                    </div>
                  )}
                </div>
              )
            })}
          </div>
        </div>
      )}
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, session, setSession, timetable, setTimetable, timetableOptions, onShow, loading, errors }) {
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
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Select Filters</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5 space-y-4">
          <Field label="Session" error={errors.session} required>
            <NativeSelect value={session} onChange={e => setSession(e.target.value)} placeholder="-- Select Session --" error={errors.session}>
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
          <Field label="TimeTable" error={errors.timetable} required>
            <NativeSelect value={timetable} onChange={e => setTimetable(e.target.value)} placeholder="-- Select TimeTable --" error={errors.timetable} disabled={!session}>
              {timetableOptions.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
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

// ─── LOADING SKELETON ─────────────────────────────────────────────────────────
function LoadingSkeleton() {
  return (
    <div className="space-y-4">
      <div className="h-28 rounded-2xl bg-slate-100 dark:bg-slate-800 animate-pulse" />
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        {[...Array(4)].map((_, i) => (
          <div key={i} className="h-16 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" />
        ))}
      </div>
      {[...Array(3)].map((_, i) => (
        <div key={i} className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm" style={{ opacity: 1 - i * 0.2 }}>
          <div className="h-14 bg-slate-100 dark:bg-slate-800 animate-pulse m-3 rounded-lg" />
          <div className="h-40 bg-slate-50 dark:bg-slate-800/50 animate-pulse m-3 rounded-lg" />
        </div>
      ))}
    </div>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function CompleteTimeTableReport() {
  const [session,         setSession]         = useState('')
  const [timetable,       setTimetable]       = useState('')
  const [reportData,      setReportData]      = useState([])
  const [loading,         setLoading]         = useState(false)
  const [exporting,       setExporting]       = useState(false)
  const [filterOpen,      setFilterOpen]      = useState(false)
  const [searchFaculty,   setSearchFaculty]   = useState('')
  const [searchDay,       setSearchDay]       = useState('')
  const [errors,          setErrors]          = useState({})
  const [toast,           setToast]           = useState(null)
  const [shown,           setShown]           = useState(false)
  const [shownMeta,       setShownMeta]       = useState({ session: '', timetable: '', timetableLabel: '' })

  const timetableOptions = session ? (TIMETABLES_BY_SESSION[session] || []) : []

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Handle session change → reset timetable ───────────────────────────────
  const handleSessionChange = (val) => {
    setSession(val)
    setTimetable('')
    setErrors(p => ({ ...p, session: undefined, timetable: undefined }))
  }

  // ── Validate & Show ───────────────────────────────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}
    if (!session)   err.session   = 'Please select a session'
    if (!timetable) err.timetable = 'Please select a timetable'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearchFaculty('')
    setSearchDay('')

    setTimeout(() => {
      // Generate timetable for each faculty
      const data = FACULTIES.map(f => ({
        faculty: f,
        timetable: generateTimetable(f.id, timetable),
      }))
      const label = timetableOptions.find(t => t.value === timetable)?.label || timetable
      setReportData(data)
      setShownMeta({ session, timetable, timetableLabel: label })
      setShown(true)
      setLoading(false)
      showToast(`Loaded timetable for ${data.length} faculty members.`)
    }, 800)
  }, [session, timetable, timetableOptions])

  const handleReset = () => {
    setSession(''); setTimetable(''); setReportData([])
    setSearchFaculty(''); setSearchDay('')
    setErrors({}); setShown(false)
    setShownMeta({ session: '', timetable: '', timetableLabel: '' })
  }

  // ── Export placeholder ────────────────────────────────────────────────────
  const handleExcel = () => {
    if (!reportData.length) { showToast('No data to export. Show report first.', 'error'); return }
    setExporting(true)
    setTimeout(() => { setExporting(false); showToast('Excel export ready! (API integration pending)') }, 1200)
  }

  const handlePrint = () => {
    if (!reportData.length) { showToast('No data to print. Show report first.', 'error'); return }
    showToast('Print dialog opening... (API integration pending)')
  }

  // ── Filtered data ─────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    if (!searchFaculty) return reportData
    const q = searchFaculty.toLowerCase()
    return reportData.filter(r =>
      r.faculty.name.toLowerCase().includes(q) ||
      r.faculty.subject.toLowerCase().includes(q) ||
      r.faculty.dept.toLowerCase().includes(q)
    )
  }, [reportData, searchFaculty])

  const hasResults = shown && reportData.length > 0
  const activeFilters = [session, timetable].filter(Boolean).length

  // Total active periods across all faculties for stats
  const totalActivePeriods = useMemo(() => {
    return reportData.reduce((total, { timetable: tt }) => {
      return total + DAYS.reduce((dt, day) => {
        return dt + PERIODS.reduce((pt, p) => pt + (tt[day]?.[p]?.isFree ? 0 : 1), 0)
      }, 0)
    }, 0)
  }, [reportData])

  return (
    <div className="space-y-4 pb-10">

      {/* ── Breadcrumb (desktop only) ─────────────────────────────────────── */}
      <nav className="hidden sm:flex items-center gap-1.5 text-[12px] text-slate-400 dark:text-slate-500">
        <span className="hover:text-blue-600 dark:hover:text-blue-400 cursor-pointer transition-colors">Home</span>
        <ChevronRight className="w-3 h-3" />
        <span className="hover:text-blue-600 dark:hover:text-blue-400 cursor-pointer transition-colors">Report</span>
        <ChevronRight className="w-3 h-3" />
        <span className="hover:text-blue-600 dark:hover:text-blue-400 cursor-pointer transition-colors">Configuration</span>
        <ChevronRight className="w-3 h-3" />
        <span className="text-slate-700 dark:text-slate-300 font-semibold">Complete TimeTable Report</span>
      </nav>

      {/* ── Page Title ────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Calendar className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Complete TimeTable Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Faculty-wise complete timetable — all periods, days &amp; assigned classes.
          </p>
        </div>
        {hasResults && (
          <div className="hidden sm:flex items-center gap-2">
            <button type="button" onClick={handlePrint}
              className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-all">
              <Printer className="w-4 h-4" />
              Print
            </button>
            <button type="button" onClick={handleExcel} disabled={exporting}
              className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
                bg-emerald-600 text-white hover:bg-emerald-700 shadow-md shadow-emerald-500/20
                transition-all active:scale-95 disabled:opacity-70">
              {exporting ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileSpreadsheet className="w-4 h-4" />}
              Export Excel
            </button>
          </div>
        )}
      </div>

      {/* ── DESKTOP Filter Card ───────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Filter className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Search Filters</span>
          {activeFilters > 0 && (
            <span className="text-[11px] font-bold px-2.5 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
              {activeFilters} active
            </span>
          )}
        </div>
        <div className="p-5">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 items-end">
            {/* Session */}
            <Field label="Session" error={errors.session} required>
              <NativeSelect
                value={session}
                onChange={e => handleSessionChange(e.target.value)}
                placeholder="-- Select Session --"
                error={errors.session}
              >
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>

            {/* TimeTable */}
            <Field label="TimeTable" error={errors.timetable} required>
              <NativeSelect
                value={timetable}
                onChange={e => { setTimetable(e.target.value); setErrors(p => ({ ...p, timetable: undefined })) }}
                placeholder={session ? '-- Select TimeTable --' : '-- Select Session First --'}
                error={errors.timetable}
                disabled={!session}
              >
                {timetableOptions.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
              </NativeSelect>
            </Field>

            {/* Spacer */}
            <div />

            {/* Actions */}
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
                  bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors"
                title="Reset">
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
            bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {session && timetable ? `${session} · ${timetableOptions.find(t => t.value === timetable)?.label?.slice(0, 18) || timetable}` : session ? `${session} · Select TT` : 'Select Filters'}
          {activeFilters > 0 && (
            <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilters}</span>
          )}
        </button>
        {hasResults && (
          <>
            <button type="button" onClick={handlePrint}
              className="px-3.5 py-2.5 rounded-xl bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300">
              <Printer className="w-4 h-4" />
            </button>
            <button type="button" onClick={handleExcel} disabled={exporting}
              className="flex items-center gap-1.5 px-3.5 py-2.5 rounded-xl text-[13px] font-semibold
                bg-emerald-600 text-white hover:bg-emerald-700 shadow-sm disabled:opacity-70">
              {exporting ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileSpreadsheet className="w-4 h-4" />}
            </button>
            <button type="button" onClick={handleReset}
              className="px-3.5 py-2.5 rounded-xl bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300">
              <RefreshCw className="w-4 h-4" />
            </button>
          </>
        )}
      </div>

      <FilterDrawer
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        session={session}
        setSession={handleSessionChange}
        timetable={timetable}
        setTimetable={setTimetable}
        timetableOptions={timetableOptions}
        onShow={handleShow}
        loading={loading}
        errors={errors}
      />

      {/* ── Loading ────────────────────────────────────────────────────────── */}
      {loading && <LoadingSkeleton />}

      {/* ── Results ───────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School Header */}
          <SchoolHeader session={shownMeta.session} timetableLabel={shownMeta.timetableLabel} />

          {/* Summary Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={Users}        label="Total Faculty"    value={reportData.length}       color="blue"    />
            <SummaryCard icon={Clock}        label="Active Periods"   value={totalActivePeriods}      color="emerald" />
            <SummaryCard icon={BookOpen}     label="Periods/Faculty"  value={`${DAYS.length}×${PERIODS.length}`} color="amber"   />
            <SummaryCard icon={GraduationCap}label="Days/Week"        value={DAYS.length}             color="violet"  />
          </div>

          {/* Results card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <BookMarked className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Faculty-wise Timetable</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownMeta.session}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                  {filtered.length} of {reportData.length} faculty
                </span>
              </div>

              {/* Search by faculty */}
              <div className="flex flex-col sm:flex-row gap-2 flex-shrink-0 w-full sm:w-auto">
                <div className="relative w-full sm:w-52">
                  <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                  <input
                    value={searchFaculty}
                    onChange={e => setSearchFaculty(e.target.value)}
                    placeholder="Search faculty / subject…"
                    className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                      bg-white text-slate-700 border-slate-200 placeholder-slate-300
                      focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                      dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                      dark:placeholder-slate-600 dark:focus:border-indigo-400"
                  />
                  {searchFaculty && (
                    <button onClick={() => setSearchFaculty('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                      <X className="w-3.5 h-3.5" />
                    </button>
                  )}
                </div>
                {/* Day filter (desktop) */}
                <div className="relative hidden sm:block w-40">
                  <select
                    value={searchDay}
                    onChange={e => setSearchDay(e.target.value)}
                    className="w-full appearance-none pl-3 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                      bg-white text-slate-700 border-slate-200 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                      dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]"
                  >
                    <option value="">All Days</option>
                    {DAYS.map(d => <option key={d} value={d}>{d}</option>)}
                  </select>
                  <ChevronDown className="absolute right-2 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
                </div>
              </div>
            </div>

            {/* Info hint */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
              <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <p className="text-[12px] text-blue-700 dark:text-blue-400">
                Each row represents a school day. Columns P1–P8 show the subject and class assigned to each period. Break after P3 (10:15–10:30) and lunch after P6 (12:45–1:30).
              </p>
            </div>

            {/* Period legend (desktop) */}
            <div className="hidden sm:flex flex-wrap gap-2 px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)]">
              {Object.entries(PERIOD_TIMES).map(([p, t]) => (
                <div key={p} className="flex items-center gap-1.5">
                  <span className="text-[10px] font-bold text-slate-600 dark:text-slate-300 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded">{p}</span>
                  <span className="text-[10px] text-slate-400 dark:text-slate-500">{t}</span>
                </div>
              ))}
              <div className="flex items-center gap-1.5 ml-4">
                <span className="w-3 h-3 rounded border border-dashed border-slate-300 dark:border-slate-600 inline-block" />
                <span className="text-[10px] text-slate-400 dark:text-slate-500">Free Period</span>
              </div>
            </div>

            {/* ── DESKTOP: one table per faculty ── */}
            {filtered.length === 0 ? (
              <div className="hidden md:flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                <Search className="w-6 h-6 opacity-40" />
                <span className="text-[13px]">No faculty matches your search.</span>
              </div>
            ) : (
              <div className="hidden md:block p-4">
                {filtered.map(({ faculty, timetable: tt }) => (
                  <FacultyDesktopTable
                    key={faculty.id}
                    faculty={faculty}
                    timetable={tt}
                    searchDay={searchDay}
                  />
                ))}
              </div>
            )}

            {/* ── MOBILE: collapsible faculty cards ── */}
            <div className="md:hidden p-4 space-y-3">
              {/* Mobile day filter */}
              <div className="flex gap-1 overflow-x-auto scrollbar-hide pb-1">
                <button
                  type="button"
                  onClick={() => setSearchDay('')}
                  className={`flex-shrink-0 px-3 py-1.5 rounded-lg text-[11px] font-semibold transition-all ${
                    !searchDay
                      ? 'bg-blue-600 text-white shadow-md shadow-blue-500/20 dark:bg-indigo-600'
                      : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400'
                  }`}
                >
                  All Days
                </button>
                {DAYS.map((d, di) => (
                  <button
                    key={d}
                    type="button"
                    onClick={() => setSearchDay(d === searchDay ? '' : d)}
                    className={`flex-shrink-0 px-3 py-1.5 rounded-lg text-[11px] font-semibold transition-all ${
                      searchDay === d
                        ? 'bg-blue-600 text-white shadow-md shadow-blue-500/20 dark:bg-indigo-600'
                        : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400'
                    }`}
                  >
                    {DAYS_SHORT[di]}
                  </button>
                ))}
              </div>

              {filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No faculty matches your search.</span>
                </div>
              ) : (
                <>
                  <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5 pb-1">
                    <Info className="w-3.5 h-3.5 flex-shrink-0" />
                    Tap a faculty card to view their timetable.
                  </p>
                  {filtered.map(({ faculty, timetable: tt }) => (
                    <FacultyMobileCard key={faculty.id} faculty={faculty} timetable={tt} />
                  ))}
                </>
              )}
            </div>

            {/* Footer */}
            <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
              <p className="text-[12px] text-slate-400 dark:text-slate-500">
                Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
                <span className="font-semibold text-slate-700 dark:text-slate-300">{reportData.length}</span> faculty members
              </p>
              {(searchFaculty || searchDay) && (
                <button
                  onClick={() => { setSearchFaculty(''); setSearchDay('') }}
                  className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
                  <X className="w-3 h-3" /> Clear filters
                </button>
              )}
            </div>
          </div>
        </>
      )}

      {/* ── Empty State ────────────────────────────────────────────────────── */}
      {!hasResults && !loading && (
        <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400 dark:text-slate-600">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
            <Calendar className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No report generated yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select a <strong>Session</strong> and <strong>TimeTable</strong>, then click <strong>Show</strong> to generate the report.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
