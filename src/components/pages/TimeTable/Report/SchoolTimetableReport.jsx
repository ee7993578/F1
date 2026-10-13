/**
 * SchoolTimetableReport.jsx
 * Folder: src/pages/Reports/Configuration/SchoolTimetableReport.jsx
 *
 * Converts legacy ASPX "School Timetable Report" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session dropdown + Timetable dropdown (cascading)
 *  - Show report button
 *  - School name / address / session header in report
 *  - Classwise timetable: periods across columns, days across rows
 *  - Mobile: card-per-day with period chips
 *  - Desktop: compact ERP-style timetable grid
 *  - Print-friendly layout
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Filter, RefreshCw, Eye,
  AlertCircle, X, Check, Loader2, ChevronDown,
  Clock, Calendar, BookOpen, SlidersHorizontal,
  Building2, MapPin, School2, ChevronRight,
  Printer, Info, Search, BarChart3, GraduationCap,
  BookMarked, Users, LayoutGrid, List
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ──────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const TIMETABLES_BY_SESSION = {
  '2022-23': [
    { id: 'TT-22-A', label: 'Primary Timetable (2022-23)' },
    { id: 'TT-22-B', label: 'Secondary Timetable (2022-23)' },
  ],
  '2023-24': [
    { id: 'TT-23-A', label: 'Primary Timetable (2023-24)' },
    { id: 'TT-23-B', label: 'Secondary Timetable (2023-24)' },
    { id: 'TT-23-C', label: 'Senior Secondary Timetable (2023-24)' },
  ],
  '2024-25': [
    { id: 'TT-24-A', label: 'Primary Timetable (2024-25)' },
    { id: 'TT-24-B', label: 'Secondary Timetable (2024-25)' },
    { id: 'TT-24-C', label: 'Senior Secondary Timetable (2024-25)' },
  ],
  '2025-26': [
    { id: 'TT-25-A', label: 'Primary Timetable (2025-26)' },
    { id: 'TT-25-B', label: 'Secondary Timetable (2025-26)' },
    { id: 'TT-25-C', label: 'Senior Secondary Timetable (2025-26)' },
  ],
}

const SCHOOL_INFO = {
  name: 'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
const DAY_SHORT = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']

// Period timings
const PERIODS = [
  { no: 1, time: '8:00–8:45' },
  { no: 2, time: '8:45–9:30' },
  { no: 3, time: '9:30–10:15' },
  { no: 4, time: '10:30–11:15' }, // after break
  { no: 5, time: '11:15–12:00' },
  { no: 6, time: '12:00–12:45' },
  { no: 7, time: '1:30–2:15' },   // after lunch
  { no: 8, time: '2:15–3:00' },
]

// Subject pool per timetable type
const PRIMARY_SUBJECTS = ['English', 'Hindi', 'Maths', 'EVS', 'G.K.', 'Drawing', 'Computer', 'P.T.']
const SECONDARY_SUBJECTS = ['English', 'Hindi', 'Maths', 'Science', 'S.St.', 'Sanskrit', 'Computer', 'P.T.']
const SENIOR_SUBJECTS = ['English', 'Physics', 'Chemistry', 'Maths', 'Biology', 'Comp.Sc.', 'P.E.', 'Hindi']

// Generates a deterministic but varied timetable grid
function generateTimetable(ttId, session) {
  const subjectPool = ttId.endsWith('A') ? PRIMARY_SUBJECTS
    : ttId.endsWith('B') ? SECONDARY_SUBJECTS
    : SENIOR_SUBJECTS

  // Classes covered
  const classes = ttId.endsWith('A')
    ? ['Nursery', 'LKG', 'UKG', 'Class I', 'Class II', 'Class III', 'Class IV', 'Class V']
    : ttId.endsWith('B')
    ? ['Class VI', 'Class VII', 'Class VIII', 'Class IX', 'Class X']
    : ['Class XI (Sci)', 'Class XI (Com)', 'Class XII (Sci)', 'Class XII (Com)']

  // Build rows: one per class per day
  const rows = []
  classes.forEach((cls, ci) => {
    DAYS.forEach((day, di) => {
      const periods = {}
      // Simple deterministic subject assignment
      PERIODS.forEach((p, pi) => {
        const idx = (ci * 7 + di * 3 + pi * 5 + p.no) % subjectPool.length
        periods[p.no] = subjectPool[idx]
      })
      rows.push({ class: cls, day, periods })
    })
  })
  return { classes, rows, session, ttId }
}

// ─── SUBJECT COLOR MAP ────────────────────────────────────────────────────────
const SUBJECT_COLORS = {
  'English':   { bg: '#dbeafe', fg: '#1d4ed8' },
  'Hindi':     { bg: '#fce7f3', fg: '#be185d' },
  'Maths':     { bg: '#d1fae5', fg: '#065f46' },
  'EVS':       { bg: '#ecfccb', fg: '#365314' },
  'G.K.':      { bg: '#fef3c7', fg: '#92400e' },
  'Drawing':   { bg: '#ede9fe', fg: '#5b21b6' },
  'Computer':  { bg: '#e0f2fe', fg: '#0c4a6e' },
  'P.T.':      { bg: '#ffedd5', fg: '#9a3412' },
  'Science':   { bg: '#cffafe', fg: '#155e75' },
  'S.St.':     { bg: '#fef9c3', fg: '#713f12' },
  'Sanskrit':  { bg: '#f0fdf4', fg: '#14532d' },
  'Physics':   { bg: '#eff6ff', fg: '#1e40af' },
  'Chemistry': { bg: '#fdf4ff', fg: '#6b21a8' },
  'Biology':   { bg: '#f0fdf4', fg: '#166534' },
  'Comp.Sc.':  { bg: '#f0f9ff', fg: '#0c4a6e' },
  'P.E.':      { bg: '#fff7ed', fg: '#9a3412' },
  'default':   { bg: '#f1f5f9', fg: '#475569' },
}
const subjectColor = (sub) => SUBJECT_COLORS[sub] || SUBJECT_COLORS['default']

const DAY_COLORS = [
  { bg: '#dbeafe', fg: '#1d4ed8', border: '#bfdbfe' },
  { bg: '#d1fae5', fg: '#065f46', border: '#a7f3d0' },
  { bg: '#fce7f3', fg: '#9d174d', border: '#fbcfe8' },
  { bg: '#fef3c7', fg: '#92400e', border: '#fde68a' },
  { bg: '#ede9fe', fg: '#5b21b6', border: '#ddd6fe' },
  { bg: '#cffafe', fg: '#155e75', border: '#a5f3fc' },
]

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
function SchoolHeader({ session, ttLabel }) {
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
          <span className="text-[12px] font-bold text-blue-700 dark:text-blue-400">{ttLabel}</span>
        </div>
      </div>
      <p className="mt-2 text-[13px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400">
        Classwise TimeTable Report
      </p>
    </div>
  )
}

// ─── PERIOD CHIP ──────────────────────────────────────────────────────────────
function PeriodChip({ subject, size = 'sm' }) {
  const { bg, fg } = subjectColor(subject)
  return (
    <span
      className={`inline-flex items-center justify-center font-semibold rounded-lg whitespace-nowrap
        ${size === 'xs' ? 'text-[10px] px-1.5 py-0.5' : 'text-[11px] px-2 py-1'}`}
      style={{ background: bg, color: fg }}
    >
      {subject}
    </span>
  )
}

// ─── DESKTOP TIMETABLE TABLE (per class) ─────────────────────────────────────
function ClassTimetableDesktop({ cls, rows }) {
  // rows for this class only
  const classRows = rows.filter(r => r.class === cls)
  return (
    <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.18)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden mb-4">
      {/* Class Header */}
      <div className="flex items-center gap-3 px-5 py-3 bg-slate-50/80 dark:bg-white/[0.03] border-b border-slate-100 dark:border-[rgba(99,102,241,0.12)]">
        <div className="w-8 h-8 rounded-xl bg-blue-100 dark:bg-blue-500/20 flex items-center justify-center">
          <GraduationCap className="w-4 h-4 text-blue-600 dark:text-blue-400" />
        </div>
        <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">{cls}</span>
        <span className="text-[11px] text-slate-400 dark:text-slate-500">— {classRows.length} days</span>
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="w-full min-w-[700px]">
          <thead>
            <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/40 dark:bg-white/[0.01]">
              <th className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 w-24 sticky left-0 bg-slate-50/80 dark:bg-[#1a1f35] z-10">
                Day
              </th>
              {PERIODS.map(p => (
                <th key={p.no} className="px-2 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 min-w-[88px]">
                  <div>P{p.no}</div>
                  <div className="text-[9px] font-normal text-slate-400 dark:text-slate-600 normal-case">{p.time}</div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {classRows.map((row, di) => {
              const dc = DAY_COLORS[di % DAY_COLORS.length]
              return (
                <tr
                  key={row.day}
                  className="border-b border-slate-50 dark:border-[rgba(99,102,241,0.05)] hover:bg-slate-50/50 dark:hover:bg-white/[0.015] transition-colors"
                >
                  {/* Day label — sticky */}
                  <td className="px-4 py-2.5 sticky left-0 bg-white dark:bg-[#1a1f35] z-10">
                    <span
                      className="inline-flex items-center justify-center px-2.5 py-1 rounded-lg text-[11px] font-bold whitespace-nowrap"
                      style={{ background: dc.bg, color: dc.fg, border: `1px solid ${dc.border}` }}
                    >
                      {DAY_SHORT[di]}
                    </span>
                  </td>
                  {PERIODS.map(p => (
                    <td key={p.no} className="px-2 py-2.5 text-center">
                      <PeriodChip subject={row.periods[p.no]} size="xs" />
                    </td>
                  ))}
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>
    </div>
  )
}

// ─── MOBILE CLASS CARD ────────────────────────────────────────────────────────
function ClassTimetableMobile({ cls, rows }) {
  const [expanded, setExpanded] = useState(false)
  const [activeDay, setActiveDay] = useState(0)
  const classRows = rows.filter(r => r.class === cls)
  const dayRow = classRows[activeDay] || classRows[0]

  return (
    <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm mb-3">
      {/* Class header — tap to expand */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        <div className="w-9 h-9 rounded-xl bg-blue-100 dark:bg-blue-500/20 flex items-center justify-center flex-shrink-0">
          <GraduationCap className="w-4 h-4 text-blue-600 dark:text-blue-400" />
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100">{cls}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">{PERIODS.length} periods/day · {classRows.length} days</p>
        </div>
        <span className={`w-5 h-5 flex items-center justify-center text-slate-400 transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Expanded content */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          {/* Day tabs */}
          <div className="flex overflow-x-auto gap-1.5 px-4 pt-3 pb-2 scrollbar-none">
            {classRows.map((row, di) => {
              const dc = DAY_COLORS[di % DAY_COLORS.length]
              return (
                <button
                  key={row.day}
                  type="button"
                  onClick={() => setActiveDay(di)}
                  className="flex-shrink-0 px-3 py-1.5 rounded-xl text-[12px] font-bold transition-all"
                  style={activeDay === di
                    ? { background: dc.bg, color: dc.fg, border: `1.5px solid ${dc.border}` }
                    : { background: '#f1f5f9', color: '#64748b', border: '1.5px solid #e2e8f0' }
                  }
                >
                  {DAY_SHORT[di]}
                </button>
              )
            })}
          </div>

          {/* Day full name */}
          <div className="px-4 pb-2">
            <span className="text-[12px] font-bold text-slate-600 dark:text-slate-400">{dayRow?.day}</span>
          </div>

          {/* Periods grid */}
          {dayRow && (
            <div className="px-4 pb-4 grid grid-cols-2 gap-2">
              {PERIODS.map(p => (
                <div
                  key={p.no}
                  className="flex items-start gap-2.5 rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/70 dark:bg-white/[0.02] px-3 py-2.5"
                >
                  <div className="flex-shrink-0 w-7 h-7 rounded-lg bg-blue-100 dark:bg-blue-500/15 flex items-center justify-center">
                    <span className="text-[10px] font-bold text-blue-700 dark:text-blue-400">P{p.no}</span>
                  </div>
                  <div className="min-w-0">
                    <PeriodChip subject={dayRow.periods[p.no]} size="sm" />
                    <p className="text-[9px] text-slate-400 mt-0.5">{p.time}</p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
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

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, session, setSession, timetable, setTimetable, onShow, loading, errors }) {
  if (!open) return null
  const ttOptions = TIMETABLES_BY_SESSION[session] || []

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
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Select Timetable</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5 space-y-4">
          <Field label="Session" error={errors.session} required>
            <NativeSelect
              value={session}
              onChange={e => { setSession(e.target.value); setTimetable('') }}
              placeholder="-- Select Session --"
              error={errors.session}
            >
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Timetable" error={errors.timetable} required>
            <NativeSelect
              value={timetable}
              onChange={e => setTimetable(e.target.value)}
              placeholder="-- Select Timetable --"
              error={errors.timetable}
              disabled={!session}
            >
              {ttOptions.map(t => <option key={t.id} value={t.id}>{t.label}</option>)}
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
export default function SchoolTimetableReport() {
  const [session,      setSession]      = useState('')
  const [timetable,    setTimetable]    = useState('')
  const [ttData,       setTtData]       = useState(null)
  const [loading,      setLoading]      = useState(false)
  const [filterOpen,   setFilterOpen]   = useState(false)
  const [errors,       setErrors]       = useState({})
  const [toast,        setToast]        = useState(null)
  const [shown,        setShown]        = useState(false)
  const [shownSession, setShownSession] = useState('')
  const [shownTtLabel, setShownTtLabel] = useState('')
  const [viewMode,     setViewMode]     = useState('class') // 'class' | 'day'
  const [search,       setSearch]       = useState('')

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Cascading: when session changes, reset timetable ─────────────────────
  const handleSessionChange = (val) => {
    setSession(val)
    setTimetable('')
    setErrors(p => ({ ...p, session: undefined, timetable: undefined }))
  }

  const ttOptions = TIMETABLES_BY_SESSION[session] || []

  // ── Show / fetch timetable ────────────────────────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}
    if (!session)   err.session   = 'Please select a session'
    if (!timetable) err.timetable = 'Please select a timetable'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')

    setTimeout(() => {
      const data = generateTimetable(timetable, session)
      const label = ttOptions.find(t => t.id === timetable)?.label || timetable
      setTtData(data)
      setShownSession(session)
      setShownTtLabel(label)
      setShown(true)
      setLoading(false)
      showToast(`Timetable loaded — ${data.classes.length} classes, ${DAYS.length} days.`)
    }, 750)
  }, [session, timetable, ttOptions])

  const handleReset = () => {
    setSession(''); setTimetable(''); setTtData(null)
    setErrors({}); setShown(false); setShownSession(''); setShownTtLabel('')
    setSearch('')
  }

  // ── Search filter (class name) ────────────────────────────────────────────
  const filteredClasses = useMemo(() => {
    if (!ttData) return []
    if (!search) return ttData.classes
    const q = search.toLowerCase()
    return ttData.classes.filter(c => c.toLowerCase().includes(q))
  }, [ttData, search])

  const hasResults = shown && ttData !== null
  const activeFilters = [session, timetable].filter(Boolean).length

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ───────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <School2 className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            School TimeTable Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Classwise period-wise timetable — select session &amp; timetable to view.
          </p>
        </div>
        {hasResults && (
          <button
            type="button"
            onClick={() => window.print()}
            className="hidden sm:flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
              bg-slate-700 text-white hover:bg-slate-800 shadow-md shadow-slate-500/20
              transition-all active:scale-95 flex-shrink-0"
          >
            <Printer className="w-4 h-4" />
            Print
          </button>
        )}
      </div>

      {/* ── DESKTOP Filter Card ───────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Filter className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Search Filters</span>
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

            {/* Timetable — cascading on session */}
            <Field label="Timetable" error={errors.timetable} required>
              <NativeSelect
                value={timetable}
                onChange={e => { setTimetable(e.target.value); setErrors(p => ({ ...p, timetable: undefined })) }}
                placeholder={session ? '-- Select Timetable --' : '-- Select Session First --'}
                error={errors.timetable}
                disabled={!session}
              >
                {ttOptions.map(t => <option key={t.id} value={t.id}>{t.label}</option>)}
              </NativeSelect>
            </Field>

            {/* Spacer */}
            <div />

            {/* Buttons */}
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

      {/* ── MOBILE Filter Bar ─────────────────────────────────────────────── */}
      <div className="flex sm:hidden gap-2">
        <button type="button" onClick={() => setFilterOpen(true)}
          className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
            bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {session && timetable
            ? `${session} · ${ttOptions.find(t => t.id === timetable)?.label?.split(' ')[0] ?? 'TT'}`
            : 'Select Session & Timetable'}
          {activeFilters > 0 && (
            <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilters}</span>
          )}
        </button>
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
        session={session}
        setSession={handleSessionChange}
        timetable={timetable}
        setTimetable={setTimetable}
        onShow={handleShow}
        loading={loading}
        errors={errors}
      />

      {/* ── Loading Skeleton ──────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          <div className="h-20 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-4" />
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-16 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.15 }} />
          ))}
        </div>
      )}

      {/* ── Results ───────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School header */}
          <SchoolHeader session={shownSession} ttLabel={shownTtLabel} />

          {/* Summary cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={GraduationCap} label="Total Classes"  value={ttData.classes.length}  color="blue"    />
            <SummaryCard icon={Calendar}      label="Working Days"   value={DAYS.length}             color="emerald" />
            <SummaryCard icon={Clock}         label="Periods / Day"  value={PERIODS.length}          color="amber"   />
            <SummaryCard icon={BookMarked}    label="Total Periods"  value={ttData.classes.length * DAYS.length * PERIODS.length} color="violet" />
          </div>

          {/* Results container */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

            {/* Card header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <BookOpen className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Classwise Timetable</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownSession}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                  {filteredClasses.length} class{filteredClasses.length !== 1 ? 'es' : ''}
                </span>
              </div>

              <div className="flex items-center gap-2 flex-shrink-0">
                {/* Search */}
                <div className="relative w-full sm:w-48">
                  <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                  <input
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                    placeholder="Search class…"
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

                {/* View mode toggle — desktop only */}
                <div className="hidden sm:flex items-center gap-1 p-1 rounded-xl bg-slate-100 dark:bg-slate-800">
                  <button
                    type="button"
                    onClick={() => setViewMode('class')}
                    className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-[11px] font-semibold transition-all ${
                      viewMode === 'class'
                        ? 'bg-white dark:bg-slate-700 text-blue-700 dark:text-blue-400 shadow-sm'
                        : 'text-slate-500 dark:text-slate-400 hover:text-slate-700'
                    }`}
                  >
                    <LayoutGrid className="w-3.5 h-3.5" />
                    Classwise
                  </button>
                  <button
                    type="button"
                    onClick={() => setViewMode('day')}
                    className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-[11px] font-semibold transition-all ${
                      viewMode === 'day'
                        ? 'bg-white dark:bg-slate-700 text-blue-700 dark:text-blue-400 shadow-sm'
                        : 'text-slate-500 dark:text-slate-400 hover:text-slate-700'
                    }`}
                  >
                    <List className="w-3.5 h-3.5" />
                    Daywise
                  </button>
                </div>
              </div>
            </div>

            {/* Info hint */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
              <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <p className="text-[12px] text-blue-700 dark:text-blue-400">
                P1–P8 are periods. Break after P3 (10:15–10:30). Lunch after P6 (12:45–1:30). Each row = one day's schedule for a class.
              </p>
            </div>

            {/* ── DESKTOP VIEW ── */}
            <div className="hidden md:block p-5">
              {filteredClasses.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No classes match your search.</span>
                </div>
              ) : viewMode === 'class' ? (
                filteredClasses.map(cls => (
                  <ClassTimetableDesktop key={cls} cls={cls} rows={ttData.rows} />
                ))
              ) : (
                /* Day-wise view: for each day, show all classes as a table */
                DAYS.map((day, di) => {
                  const dc = DAY_COLORS[di % DAY_COLORS.length]
                  const dayRows = ttData.rows.filter(r => r.day === day && filteredClasses.includes(r.class))
                  return (
                    <div key={day} className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.18)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden mb-4">
                      <div className="flex items-center gap-3 px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.12)]"
                        style={{ background: dc.bg + '55' }}>
                        <span className="px-3 py-1 rounded-xl text-[13px] font-bold"
                          style={{ background: dc.bg, color: dc.fg, border: `1px solid ${dc.border}` }}>{day}</span>
                        <span className="text-[12px] text-slate-500 dark:text-slate-400">{dayRows.length} classes</span>
                      </div>
                      <div className="overflow-x-auto">
                        <table className="w-full min-w-[700px]">
                          <thead>
                            <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/40">
                              <th className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 w-32 sticky left-0 bg-slate-50/80 dark:bg-[#1a1f35]">Class</th>
                              {PERIODS.map(p => (
                                <th key={p.no} className="px-2 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 min-w-[88px]">
                                  <div>P{p.no}</div>
                                  <div className="text-[9px] font-normal text-slate-400 normal-case">{p.time}</div>
                                </th>
                              ))}
                            </tr>
                          </thead>
                          <tbody>
                            {dayRows.map(row => (
                              <tr key={row.class} className="border-b border-slate-50 dark:border-[rgba(99,102,241,0.05)] hover:bg-slate-50/50 dark:hover:bg-white/[0.015] transition-colors">
                                <td className="px-4 py-2.5 sticky left-0 bg-white dark:bg-[#1a1f35]">
                                  <span className="text-[12px] font-semibold text-slate-700 dark:text-slate-200">{row.class}</span>
                                </td>
                                {PERIODS.map(p => (
                                  <td key={p.no} className="px-2 py-2.5 text-center">
                                    <PeriodChip subject={row.periods[p.no]} size="xs" />
                                  </td>
                                ))}
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>
                  )
                })
              )}
            </div>

            {/* ── MOBILE CARDS ── */}
            <div className="md:hidden p-4">
              {filteredClasses.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No classes match your search.</span>
                </div>
              ) : (
                <>
                  <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5 pb-3">
                    <Info className="w-3.5 h-3.5 flex-shrink-0" />
                    Tap a class to expand. Use day tabs to switch days.
                  </p>
                  {filteredClasses.map(cls => (
                    <ClassTimetableMobile key={cls} cls={cls} rows={ttData.rows} />
                  ))}
                </>
              )}
            </div>

            {/* Footer */}
            <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
              <p className="text-[12px] text-slate-400 dark:text-slate-500">
                Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filteredClasses.length}</span> of{' '}
                <span className="font-semibold text-slate-700 dark:text-slate-300">{ttData.classes.length}</span> classes
              </p>
              {search && (
                <button onClick={() => setSearch('')}
                  className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
                  <X className="w-3 h-3" /> Clear search
                </button>
              )}
            </div>
          </div>

          {/* Subject legend */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.18)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
            <div className="flex items-center gap-3 px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-slate-50/70 dark:bg-white/[0.02]">
              <span className="w-1 h-5 rounded-full bg-amber-400 flex-shrink-0" />
              <BookMarked className="w-4 h-4 text-amber-600 dark:text-amber-400 flex-shrink-0" />
              <span className="text-[13px] font-bold text-slate-700 dark:text-slate-200">Subject Colour Legend</span>
            </div>
            <div className="p-4 flex flex-wrap gap-2">
              {Object.entries(SUBJECT_COLORS).filter(([k]) => k !== 'default').map(([sub, { bg, fg }]) => (
                <span key={sub}
                  className="inline-flex items-center px-3 py-1 rounded-xl text-[11px] font-semibold"
                  style={{ background: bg, color: fg }}
                >
                  {sub}
                </span>
              ))}
            </div>
          </div>
        </>
      )}

      {/* ── Empty State ───────────────────────────────────────────────────── */}
      {!hasResults && !loading && (
        <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400 dark:text-slate-600">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
            <BarChart3 className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No timetable generated yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select a <strong>Session</strong> and a <strong>Timetable</strong>, then click <strong>Show</strong>.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
