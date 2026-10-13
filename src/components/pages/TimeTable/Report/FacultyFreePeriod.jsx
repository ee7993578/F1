/**
 * FacultyFreePeriod.jsx
 * Folder: src/pages/Reports/Configuration/FacultyFreePeriod.jsx
 *
 * Converts legacy ASPX "Faculty Free Period Report" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session dropdown filter
 *  - Show report button
 *  - School name / session header in report
 *  - Faculty-wise free period breakdown per day
 *  - Mobile: collapsible cards with expandable details
 *  - Desktop: dense ERP-style table
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Filter, RefreshCw, Eye,
  AlertCircle, X, Check, Loader2, ChevronDown,
  SlidersHorizontal, Info, Search,
  BarChart3, BookOpen,
  Building2, MapPin, ChevronRight,
  Clock, User, Calendar,
  Coffee, TrendingUp, Users
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ───────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const SCHOOL_INFO = {
  name: 'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']

// Free period data: each faculty has free periods per day (period numbers 1-8)
const FREE_PERIOD_DATA = {
  '2022-23': [
    { sno: 1, faculty: 'Dr. Sunita Sharma',    subject: 'Mathematics',    monday: [3],    tuesday: [5],    wednesday: [2],    thursday: [],     friday: [4],    saturday: [1, 6], totalFree: 6 },
    { sno: 2, faculty: 'Mr. Ramesh Gupta',     subject: 'Physics',        monday: [1, 7], tuesday: [],     wednesday: [6],    thursday: [3],    friday: [],     saturday: [4],    totalFree: 5 },
    { sno: 3, faculty: 'Ms. Priya Negi',       subject: 'Chemistry',      monday: [],     tuesday: [2, 6], wednesday: [],     thursday: [5],    friday: [3, 7], saturday: [],     totalFree: 5 },
    { sno: 4, faculty: 'Mr. Anil Rawat',       subject: 'Biology',        monday: [4],    tuesday: [3],    wednesday: [7],    thursday: [1],    friday: [],     saturday: [5],    totalFree: 5 },
    { sno: 5, faculty: 'Ms. Kavita Joshi',     subject: 'English',        monday: [2, 6], tuesday: [],     wednesday: [4],    thursday: [],     friday: [1],    saturday: [3],    totalFree: 5 },
    { sno: 6, faculty: 'Mr. Deepak Verma',     subject: 'Hindi',          monday: [],     tuesday: [4],    wednesday: [1],    thursday: [6],    friday: [2],    saturday: [],     totalFree: 4 },
    { sno: 7, faculty: 'Ms. Meena Bisht',      subject: 'Social Science', monday: [5],    tuesday: [1],    wednesday: [3],    thursday: [],     friday: [6],    saturday: [2],    totalFree: 5 },
    { sno: 8, faculty: 'Mr. Suresh Pandey',    subject: 'Computer Sci.',  monday: [],     tuesday: [7],    wednesday: [5],    thursday: [2],    friday: [],     saturday: [4, 6], totalFree: 5 },
    { sno: 9, faculty: 'Ms. Rekha Dobhal',     subject: 'Sanskrit',       monday: [3, 7], tuesday: [],     wednesday: [],     thursday: [4],    friday: [5],    saturday: [],     totalFree: 4 },
    { sno: 10,faculty: 'Mr. Vijay Thapliyal',  subject: 'Physical Ed.',   monday: [1],    tuesday: [6],    wednesday: [4],    thursday: [],     friday: [],     saturday: [3],    totalFree: 4 },
  ],
  '2023-24': [
    { sno: 1, faculty: 'Dr. Sunita Sharma',    subject: 'Mathematics',    monday: [2],    tuesday: [4],    wednesday: [6],    thursday: [1],    friday: [],     saturday: [3],    totalFree: 5 },
    { sno: 2, faculty: 'Mr. Ramesh Gupta',     subject: 'Physics',        monday: [],     tuesday: [3, 7], wednesday: [1],    thursday: [],     friday: [5],    saturday: [2],    totalFree: 5 },
    { sno: 3, faculty: 'Ms. Priya Negi',       subject: 'Chemistry',      monday: [5],    tuesday: [],     wednesday: [3],    thursday: [6],    friday: [1],    saturday: [],     totalFree: 4 },
    { sno: 4, faculty: 'Mr. Anil Rawat',       subject: 'Biology',        monday: [1, 6], tuesday: [5],    wednesday: [],     thursday: [3],    friday: [],     saturday: [4],    totalFree: 5 },
    { sno: 5, faculty: 'Ms. Kavita Joshi',     subject: 'English',        monday: [3],    tuesday: [],     wednesday: [5],    thursday: [2],    friday: [4, 7], saturday: [],     totalFree: 5 },
    { sno: 6, faculty: 'Mr. Deepak Verma',     subject: 'Hindi',          monday: [],     tuesday: [2],    wednesday: [4],    thursday: [],     friday: [3],    saturday: [1, 6], totalFree: 5 },
    { sno: 7, faculty: 'Ms. Meena Bisht',      subject: 'Social Science', monday: [4, 7], tuesday: [],     wednesday: [2],    thursday: [5],    friday: [],     saturday: [3],    totalFree: 5 },
    { sno: 8, faculty: 'Mr. Suresh Pandey',    subject: 'Computer Sci.',  monday: [2],    tuesday: [6],    wednesday: [],     thursday: [4],    friday: [1],    saturday: [],     totalFree: 4 },
    { sno: 9, faculty: 'Ms. Rekha Dobhal',     subject: 'Sanskrit',       monday: [],     tuesday: [1],    wednesday: [7],    thursday: [],     friday: [6],    saturday: [2, 5], totalFree: 5 },
    { sno: 10,faculty: 'Mr. Vijay Thapliyal',  subject: 'Physical Ed.',   monday: [3],    tuesday: [4],    wednesday: [],     thursday: [1],    friday: [2],    saturday: [],     totalFree: 4 },
  ],
  '2024-25': [
    { sno: 1, faculty: 'Dr. Sunita Sharma',    subject: 'Mathematics',    monday: [1, 5], tuesday: [],     wednesday: [3],    thursday: [6],    friday: [],     saturday: [2],    totalFree: 5 },
    { sno: 2, faculty: 'Mr. Ramesh Gupta',     subject: 'Physics',        monday: [4],    tuesday: [2],    wednesday: [],     thursday: [5],    friday: [3, 7], saturday: [],     totalFree: 5 },
    { sno: 3, faculty: 'Ms. Priya Negi',       subject: 'Chemistry',      monday: [],     tuesday: [5, 7], wednesday: [2],    thursday: [],     friday: [1],    saturday: [4],    totalFree: 5 },
    { sno: 4, faculty: 'Mr. Anil Rawat',       subject: 'Biology',        monday: [3],    tuesday: [1],    wednesday: [6],    thursday: [4],    friday: [],     saturday: [],     totalFree: 4 },
    { sno: 5, faculty: 'Ms. Kavita Joshi',     subject: 'English',        monday: [],     tuesday: [3],    wednesday: [4],    thursday: [1],    friday: [5],    saturday: [2, 6], totalFree: 6 },
    { sno: 6, faculty: 'Mr. Deepak Verma',     subject: 'Hindi',          monday: [2, 6], tuesday: [],     wednesday: [5],    thursday: [],     friday: [4],    saturday: [3],    totalFree: 5 },
    { sno: 7, faculty: 'Ms. Meena Bisht',      subject: 'Social Science', monday: [5],    tuesday: [4],    wednesday: [1],    thursday: [3],    friday: [],     saturday: [],     totalFree: 4 },
    { sno: 8, faculty: 'Mr. Suresh Pandey',    subject: 'Computer Sci.',  monday: [],     tuesday: [6],    wednesday: [3],    thursday: [2],    friday: [1, 5], saturday: [],     totalFree: 5 },
    { sno: 9, faculty: 'Ms. Rekha Dobhal',     subject: 'Sanskrit',       monday: [4],    tuesday: [],     wednesday: [7],    thursday: [],     friday: [3],    saturday: [1],    totalFree: 4 },
    { sno: 10,faculty: 'Mr. Vijay Thapliyal',  subject: 'Physical Ed.',   monday: [1],    tuesday: [3],    wednesday: [],     thursday: [5],    friday: [],     saturday: [4, 7], totalFree: 5 },
  ],
  '2025-26': [
    { sno: 1, faculty: 'Dr. Sunita Sharma',    subject: 'Mathematics',    monday: [3],    tuesday: [1],    wednesday: [5],    thursday: [],     friday: [2],    saturday: [4],    totalFree: 5 },
    { sno: 2, faculty: 'Mr. Ramesh Gupta',     subject: 'Physics',        monday: [2, 6], tuesday: [],     wednesday: [4],    thursday: [1],    friday: [],     saturday: [3],    totalFree: 5 },
    { sno: 3, faculty: 'Ms. Priya Negi',       subject: 'Chemistry',      monday: [],     tuesday: [3, 5], wednesday: [],     thursday: [4],    friday: [6],    saturday: [1],    totalFree: 5 },
    { sno: 4, faculty: 'Mr. Anil Rawat',       subject: 'Biology',        monday: [5],    tuesday: [2],    wednesday: [1],    thursday: [],     friday: [3],    saturday: [],     totalFree: 4 },
    { sno: 5, faculty: 'Ms. Kavita Joshi',     subject: 'English',        monday: [1, 4], tuesday: [],     wednesday: [6],    thursday: [3],    friday: [],     saturday: [2],    totalFree: 5 },
    { sno: 6, faculty: 'Mr. Deepak Verma',     subject: 'Hindi',          monday: [],     tuesday: [4],    wednesday: [2],    thursday: [5],    friday: [1, 7], saturday: [],     totalFree: 5 },
    { sno: 7, faculty: 'Ms. Meena Bisht',      subject: 'Social Science', monday: [2],    tuesday: [],     wednesday: [3],    thursday: [1],    friday: [4],    saturday: [5, 7], totalFree: 6 },
    { sno: 8, faculty: 'Mr. Suresh Pandey',    subject: 'Computer Sci.',  monday: [],     tuesday: [1, 7], wednesday: [5],    thursday: [],     friday: [2],    saturday: [3],    totalFree: 5 },
    { sno: 9, faculty: 'Ms. Rekha Dobhal',     subject: 'Sanskrit',       monday: [6],    tuesday: [],     wednesday: [4],    thursday: [2],    friday: [],     saturday: [1, 3], totalFree: 5 },
    { sno: 10,faculty: 'Mr. Vijay Thapliyal',  subject: 'Physical Ed.',   monday: [4],    tuesday: [5],    wednesday: [],     thursday: [3],    friday: [1],    saturday: [],     totalFree: 4 },
  ],
}

// ─── HELPER FUNCTIONS ─────────────────────────────────────────────────────────
const DAY_KEYS = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday']
const DAY_SHORT = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']

const FACULTY_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' },
  { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' },
  { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' },
  { fg: '#dc2626', bg: '#fee2e2' },
  { fg: '#0369a1', bg: '#e0f2fe' },
  { fg: '#7c3aed', bg: '#f3e8ff' },
  { fg: '#0f766e', bg: '#ccfbf1' },
  { fg: '#b45309', bg: '#fef9c3' },
]

const facultyColor = (idx) => FACULTY_COLORS[idx % FACULTY_COLORS.length]

const getInitials = (name = '') => {
  const parts = name.replace('Dr. ', '').replace('Mr. ', '').replace('Ms. ', '').split(' ')
  return parts.slice(0, 2).map(p => p[0]).join('').toUpperCase()
}

// Format free periods array nicely
const formatPeriods = (arr) => {
  if (!arr || arr.length === 0) return '—'
  return arr.map(p => `P${p}`).join(', ')
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
    cyan:    'bg-cyan-50 text-cyan-600 dark:bg-cyan-500/10 dark:text-cyan-400',
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
function SchoolHeader({ session }) {
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
        <span className="text-[12px] font-bold text-amber-700 dark:text-amber-400">Session: {session}</span>
      </div>
      <p className="mt-2 text-[13px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400">
        Faculty Free Period Report
      </p>
    </div>
  )
}

// ─── PERIOD PILL ──────────────────────────────────────────────────────────────
function PeriodPills({ periods }) {
  if (!periods || periods.length === 0) {
    return <span className="text-[12px] text-slate-300 dark:text-slate-600">—</span>
  }
  return (
    <div className="flex flex-wrap gap-1 justify-center">
      {periods.map(p => (
        <span
          key={p}
          className="inline-flex items-center justify-center min-w-[28px] h-6 px-1.5 rounded-md
            text-[10px] font-bold bg-teal-50 text-teal-700 border border-teal-200
            dark:bg-teal-500/10 dark:text-teal-400 dark:border-teal-500/20"
        >
          P{p}
        </span>
      ))}
    </div>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ row, idx, isTotal }) {
  const { fg, bg } = facultyColor(idx - 1)

  if (isTotal) {
    return (
      <tr className="bg-blue-50 dark:bg-indigo-500/[0.07] border-t-2 border-blue-200 dark:border-indigo-500/30">
        <td className="px-3 py-3 text-center text-[12px] text-blue-500 dark:text-blue-400">—</td>
        <td className="px-3 py-3" colSpan={2}>
          <span className="text-[13px] font-bold text-blue-700 dark:text-blue-300 flex items-center gap-2">
            <TrendingUp className="w-4 h-4" /> Grand Total
          </span>
        </td>
        {DAY_KEYS.map(d => (
          <td key={d} className="px-3 py-3 text-center">
            <span className="inline-flex items-center justify-center px-2 py-1 rounded-lg text-[12px] font-bold
              bg-teal-100 text-teal-800 dark:bg-teal-500/20 dark:text-teal-300 tabular-nums">
              {row[d]}
            </span>
          </td>
        ))}
        <td className="px-3 py-3 text-center">
          <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold
            bg-blue-100 text-blue-800 dark:bg-blue-500/20 dark:text-blue-300 tabular-nums">
            {row.totalFree}
          </span>
        </td>
      </tr>
    )
  }

  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
      {/* S.No */}
      <td className="px-3 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-10">{idx}</td>

      {/* Faculty */}
      <td className="px-3 py-3">
        <div className="flex items-center gap-2.5">
          <span
            className="w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold"
            style={{ background: bg, color: fg }}
          >
            {getInitials(row.faculty)}
          </span>
          <div>
            <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{row.faculty}</p>
            <p className="text-[11px] text-slate-400 dark:text-slate-500">{row.subject}</p>
          </div>
        </div>
      </td>

      {/* Subject (for compact reference, hidden at medium since it's in faculty cell) */}
      <td className="px-3 py-3 hidden lg:table-cell">
        <span className="inline-flex items-center px-2.5 py-1 rounded-lg text-[11px] font-semibold bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300">
          {row.subject}
        </span>
      </td>

      {/* Per-day free periods */}
      {DAY_KEYS.map(d => (
        <td key={d} className="px-2 py-3 text-center">
          <PeriodPills periods={row[d]} />
        </td>
      ))}

      {/* Total Free */}
      <td className="px-3 py-3 text-center">
        <span className="inline-flex items-center justify-center w-9 h-9 rounded-xl text-[14px] font-bold
          bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400 tabular-nums">
          {row.totalFree}
        </span>
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────
function MobileCard({ row, idx }) {
  const [expanded, setExpanded] = useState(false)
  const { fg, bg } = facultyColor(idx - 1)

  // Build day summary for quick view
  const activeDays = DAY_KEYS.filter(d => row[d] && row[d].length > 0)

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header — always visible */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        <span
          className="w-10 h-10 rounded-xl flex-shrink-0 flex items-center justify-center text-[11px] font-bold"
          style={{ background: bg, color: fg }}
        >
          {getInitials(row.faculty)}
        </span>

        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">
            {row.faculty}
          </p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">{row.subject}</p>
          <div className="flex flex-wrap gap-1 mt-1.5">
            {activeDays.slice(0, 3).map(d => (
              <span key={d} className="text-[10px] font-semibold px-1.5 py-0.5 rounded-md bg-teal-50 text-teal-700 dark:bg-teal-500/10 dark:text-teal-400 border border-teal-100 dark:border-teal-500/20">
                {d.slice(0, 3)}
              </span>
            ))}
            {activeDays.length > 3 && (
              <span className="text-[10px] font-semibold px-1.5 py-0.5 rounded-md bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400">
                +{activeDays.length - 3}
              </span>
            )}
          </div>
        </div>

        <div className="flex flex-col items-end flex-shrink-0 gap-1">
          <span className="text-[22px] font-bold text-blue-700 dark:text-blue-400 tabular-nums leading-tight">{row.totalFree}</span>
          <span className="text-[10px] text-slate-400">free periods</span>
        </div>

        <span className={`w-5 h-5 flex items-center justify-center ml-1 text-slate-400 transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Expandable day-by-day breakdown */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 pt-3 pb-4">
          <p className="text-[11px] font-bold uppercase tracking-wide text-slate-400 dark:text-slate-500 mb-3 flex items-center gap-1.5">
            <Calendar className="w-3.5 h-3.5" /> Day-wise Free Periods
          </p>
          <div className="grid grid-cols-3 gap-2">
            {DAY_KEYS.map((d, i) => (
              <div key={d} className={`rounded-xl border p-2.5 text-center
                ${row[d] && row[d].length > 0
                  ? 'border-teal-200 bg-teal-50 dark:bg-teal-500/10 dark:border-teal-500/20'
                  : 'border-slate-100 bg-slate-50 dark:bg-slate-800/50 dark:border-slate-700/30'
                }`}>
                <p className={`text-[10px] font-bold uppercase tracking-wide mb-1.5
                  ${row[d] && row[d].length > 0
                    ? 'text-teal-600 dark:text-teal-400'
                    : 'text-slate-400 dark:text-slate-500'
                  }`}>
                  {DAY_SHORT[i]}
                </p>
                {row[d] && row[d].length > 0 ? (
                  <div className="flex flex-wrap gap-1 justify-center">
                    {row[d].map(p => (
                      <span key={p} className="text-[11px] font-bold text-teal-700 dark:text-teal-300 bg-white dark:bg-teal-900/30 px-1.5 py-0.5 rounded-md">
                        P{p}
                      </span>
                    ))}
                  </div>
                ) : (
                  <span className="text-[12px] text-slate-300 dark:text-slate-600">—</span>
                )}
              </div>
            ))}
          </div>

          {/* Total summary */}
          <div className="mt-3 rounded-xl bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20 p-3 flex items-center justify-between">
            <span className="text-[12px] font-semibold text-blue-700 dark:text-blue-400 flex items-center gap-1.5">
              <Clock className="w-3.5 h-3.5" /> Total Free Periods (Week)
            </span>
            <span className="text-[18px] font-bold text-blue-700 dark:text-blue-300 tabular-nums">{row.totalFree}</span>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, session, setSession, onShow, loading, errors }) {
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
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Filter Report</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5">
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
export default function FacultyFreePeriod() {
  const [session,      setSession]      = useState('')
  const [rows,         setRows]         = useState([])
  const [loading,      setLoading]      = useState(false)
  const [filterOpen,   setFilterOpen]   = useState(false)
  const [search,       setSearch]       = useState('')
  const [errors,       setErrors]       = useState({})
  const [toast,        setToast]        = useState(null)
  const [shown,        setShown]        = useState(false)
  const [shownSession, setShownSession] = useState('')

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Fetch (simulate API) ──────────────────────────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}
    if (!session) err.session = 'Please select a session'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')

    setTimeout(() => {
      const data = FREE_PERIOD_DATA[session] || []
      setRows(data)
      setShownSession(session)
      setShown(true)
      setLoading(false)
      showToast(`Loaded ${data.length} faculty records for session ${session}.`)
    }, 650)
  }, [session])

  const handleReset = () => {
    setSession(''); setRows([]); setSearch('')
    setErrors({}); setShown(false); setShownSession('')
  }

  // ── Search filter ─────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    if (!search) return rows
    const q = search.toLowerCase()
    return rows.filter(r =>
      r.faculty.toLowerCase().includes(q) ||
      r.subject.toLowerCase().includes(q)
    )
  }, [rows, search])

  // ── Grand totals (count of free periods per day across all faculty) ────────
  const totals = useMemo(() => {
    const t = { totalFree: 0 }
    DAY_KEYS.forEach(d => {
      t[d] = filtered.reduce((s, r) => s + (r[d]?.length || 0), 0)
    })
    t.totalFree = filtered.reduce((s, r) => s + r.totalFree, 0)
    return t
  }, [filtered])

  // ── Summary stats ─────────────────────────────────────────────────────────
  const stats = useMemo(() => {
    const maxFree = filtered.reduce((max, r) => r.totalFree > max ? r.totalFree : max, 0)
    const avgFree = filtered.length ? (totals.totalFree / filtered.length).toFixed(1) : 0
    const busyDayIdx = DAY_KEYS.reduce((mi, d, i) => totals[d] > (totals[DAY_KEYS[mi]] || 0) ? i : mi, 0)
    return { maxFree, avgFree, busyDay: DAYS[busyDayIdx] }
  }, [filtered, totals])

  const hasResults   = shown && rows.length > 0
  const activeFilters = session ? 1 : 0

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ───────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Coffee className="w-5 h-5 text-teal-600 dark:text-teal-400" />
            Free Period Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Faculty-wise free period schedule — day-by-day period breakdown.
          </p>
        </div>
      </div>

      {/* ── DESKTOP Filter Card ──────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-teal-500 flex-shrink-0" />
          <Filter className="w-4 h-4 text-teal-600 dark:text-teal-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Search Filters</span>
        </div>
        <div className="p-5">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 items-end">
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

            {/* Spacers */}
            <div />
            <div />

            <div className="flex gap-2">
              <button type="button" onClick={handleShow} disabled={loading}
                className="flex-1 flex items-center justify-center gap-2 py-2 rounded-xl text-[13px] font-semibold text-white
                  bg-teal-600 hover:bg-teal-700 shadow-md shadow-teal-500/20
                  transition-all active:scale-95 disabled:opacity-70">
                {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
                Search
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
            bg-teal-600 text-white shadow-md shadow-teal-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {session ? `Session: ${session}` : 'Select Session'}
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
        setSession={setSession}
        onShow={handleShow}
        loading={loading}
        errors={errors}
      />

      {/* ── Loading Skeleton ────────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          <div className="h-20 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-4" />
          {[...Array(6)].map((_, i) => (
            <div key={i} className="h-12 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.12 }} />
          ))}
        </div>
      )}

      {/* ── Results ──────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School Header */}
          <SchoolHeader session={shownSession} />

          {/* Summary Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={Users}    label="Total Faculty"       value={filtered.length}     color="blue"    />
            <SummaryCard icon={Clock}    label="Total Free Periods"  value={totals.totalFree}    color="cyan"    />
            <SummaryCard icon={Coffee}   label="Avg Free / Faculty"  value={stats.avgFree}       color="amber"   />
            <SummaryCard icon={Calendar} label="Busiest Free Day"    value={stats.busyDay}       color="violet"  />
          </div>

          {/* Results card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-teal-500 flex-shrink-0" />
                <BookOpen className="w-4 h-4 text-teal-600 dark:text-teal-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Faculty Free Periods</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownSession}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-teal-100 text-teal-700 dark:bg-teal-500/15 dark:text-teal-400 flex-shrink-0">
                  {filtered.length} faculty
                </span>
              </div>

              {/* Search */}
              <div className="relative w-full sm:w-56 flex-shrink-0">
                <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  value={search}
                  onChange={e => setSearch(e.target.value)}
                  placeholder="Search faculty or subject…"
                  className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                    bg-white text-slate-700 border-slate-200 placeholder-slate-300
                    focus:border-teal-400 focus:ring-2 focus:ring-teal-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                    dark:placeholder-slate-600 dark:focus:border-teal-400"
                />
                {search && (
                  <button onClick={() => setSearch('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                    <X className="w-3.5 h-3.5" />
                  </button>
                )}
              </div>
            </div>

            {/* Info hint */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-teal-50/20 dark:bg-teal-500/[0.03]">
              <Info className="w-3.5 h-3.5 text-teal-600 flex-shrink-0" />
              <p className="text-[12px] text-teal-700 dark:text-teal-400">
                Showing period numbers for each free slot per day. P1–P8 = Period 1 through 8. "—" means no free period on that day.
              </p>
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
                      <th className="px-3 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 w-10">
                        S.No.
                      </th>
                      <th className="px-3 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400">
                        Faculty Name
                      </th>
                      <th className="px-3 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 hidden lg:table-cell">
                        Subject
                      </th>
                      {DAYS.map(d => (
                        <th key={d} className="px-2 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap">
                          {d.slice(0, 3)}
                        </th>
                      ))}
                      <th className="px-3 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400">
                        Total
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((row, i) => (
                      <DesktopRow key={row.sno} row={row} idx={i + 1} />
                    ))}
                    {/* Grand Total */}
                    <DesktopRow row={totals} idx={0} isTotal />
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
                  <p className="text-[11px] text-teal-600 dark:text-teal-400 font-medium flex items-center gap-1.5 pb-1">
                    <Info className="w-3.5 h-3.5 flex-shrink-0" />
                    Tap a card to see day-wise free period breakdown.
                  </p>

                  {filtered.map((row, i) => (
                    <MobileCard key={row.sno} row={row} idx={i + 1} />
                  ))}

                  {/* Mobile Grand Total */}
                  <div className="rounded-xl border-2 border-teal-200 dark:border-teal-500/30 bg-teal-50 dark:bg-teal-500/[0.07] p-4">
                    <p className="text-[12px] font-bold uppercase tracking-wide text-teal-700 dark:text-teal-400 mb-3 flex items-center gap-2">
                      <TrendingUp className="w-4 h-4" /> Grand Total — {filtered.length} Faculty
                    </p>
                    <div className="grid grid-cols-3 gap-2 mb-3">
                      {DAY_KEYS.map((d, i) => (
                        <div key={d} className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                          <p className="text-[10px] font-bold uppercase text-teal-600 dark:text-teal-400 mb-1">{DAY_SHORT[i]}</p>
                          <p className="text-[20px] font-bold text-teal-700 dark:text-teal-300 tabular-nums">{totals[d]}</p>
                        </div>
                      ))}
                    </div>
                    <div className="rounded-lg bg-blue-50 dark:bg-blue-500/10 border border-blue-200 dark:border-blue-500/20 p-3 flex items-center justify-between">
                      <span className="text-[12px] font-semibold text-blue-700 dark:text-blue-400">Total Free Periods (Week)</span>
                      <span className="text-[20px] font-bold text-blue-700 dark:text-blue-300 tabular-nums">{totals.totalFree}</span>
                    </div>
                  </div>
                </>
              )}
            </div>

            {/* Table Footer */}
            <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
              <p className="text-[12px] text-slate-400 dark:text-slate-500">
                Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
                <span className="font-semibold text-slate-700 dark:text-slate-300">{rows.length}</span> faculty
              </p>
              {search && (
                <button onClick={() => setSearch('')}
                  className="text-[12px] text-teal-600 dark:text-teal-400 hover:underline flex items-center gap-1">
                  <X className="w-3 h-3" /> Clear search
                </button>
              )}
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
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No report generated yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select a session and click <strong>Search</strong> to generate the free period report.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
