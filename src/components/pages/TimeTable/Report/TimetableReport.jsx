/**
 * TimetableReport.jsx
 * Folder: src/pages/Reports/Configuration/TimetableReport.jsx
 *
 * Converts legacy ASPX "Time Table Report (Class-Wise / Faculty-Wise)" to
 * fully-responsive React + Tailwind.
 *
 * Features:
 *  - Type toggle: Class-Wise / Faculty-Wise  (RadioButtonList3)
 *  - Session dropdown
 *  - Class OR Faculty dropdown (switches based on type)
 *  - Timetable Name dropdown
 *  - Show + Export Excel buttons
 *  - Desktop: dense ERP-style grid (Days as cols, Periods as rows)
 *  - Mobile: day-tabs + period cards (no horizontal scroll)
 *  - Dark / Light theme via Tailwind dark: variants
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Filter, RefreshCw, Eye, AlertCircle, X, Check, Loader2,
  ChevronDown, SlidersHorizontal, FileSpreadsheet, BookOpen,
  Building2, MapPin, Calendar, Clock, Users, GraduationCap,
  ChevronRight, Info, Search, LayoutGrid, Table2, TrendingUp
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ────────────────────────────────────────────────────

const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const CLASSES = [
  'Nursery', 'LKG', 'UKG',
  'Class I', 'Class II', 'Class III', 'Class IV', 'Class V',
  'Class VI', 'Class VII', 'Class VIII',
  'Class IX', 'Class X', 'Class XI', 'Class XII',
]

const FACULTIES = [
  'Mr. Rajesh Kumar (Mathematics)',
  'Mrs. Sunita Sharma (Science)',
  'Mr. Anil Verma (English)',
  'Mrs. Priya Singh (Hindi)',
  'Mr. Deepak Gupta (Social Studies)',
  'Mrs. Kavita Joshi (Computer Science)',
  'Mr. Suresh Yadav (Physical Education)',
  'Mrs. Neha Patel (Art & Craft)',
]

const TIMETABLE_NAMES = [
  'Regular Timetable 2025-26',
  'Summer Schedule',
  'Exam Timetable',
  'Revised Timetable Q3',
]

const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
const DAYS_SHORT = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']

// Periods list
const PERIODS = [
  { id: 1, label: 'Period 1', time: '08:00 – 08:45' },
  { id: 2, label: 'Period 2', time: '08:45 – 09:30' },
  { id: 3, label: 'Break',    time: '09:30 – 09:45', isBreak: true },
  { id: 4, label: 'Period 3', time: '09:45 – 10:30' },
  { id: 5, label: 'Period 4', time: '10:30 – 11:15' },
  { id: 6, label: 'Lunch',    time: '11:15 – 11:45', isBreak: true },
  { id: 7, label: 'Period 5', time: '11:45 – 12:30' },
  { id: 8, label: 'Period 6', time: '12:30 – 01:15' },
  { id: 9, label: 'Period 7', time: '01:15 – 02:00' },
]

const SUBJECTS = ['Mathematics', 'Science', 'English', 'Hindi', 'Social Studies', 'Computer Science', 'Physical Education', 'Art & Craft', 'Moral Science', 'Drawing']
const TEACHERS = ['Mr. Rajesh', 'Mrs. Sunita', 'Mr. Anil', 'Mrs. Priya', 'Mr. Deepak', 'Mrs. Kavita', 'Mr. Suresh', 'Mrs. Neha']
const ROOMS = ['Room 101', 'Room 102', 'Room 103', 'Lab 1', 'Lab 2', 'Hall A', 'PT Ground']

// Deterministic "random" from seed
const seededVal = (seed, arr) => arr[seed % arr.length]

function generateTimetable(classOrFaculty, session, timetableName) {
  const seed = (classOrFaculty + session + timetableName).split('').reduce((a, c) => a + c.charCodeAt(0), 0)
  const grid = {}
  DAYS.forEach((day, di) => {
    grid[day] = {}
    PERIODS.forEach((period, pi) => {
      if (period.isBreak) {
        grid[day][period.id] = null
      } else {
        const s = seed + di * 13 + pi * 7
        grid[day][period.id] = {
          subject: seededVal(s, SUBJECTS),
          teacher: seededVal(s + 3, TEACHERS),
          room: seededVal(s + 5, ROOMS),
        }
      }
    })
  })
  return grid
}

const SCHOOL_INFO = {
  name: 'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

// ─── SUBJECT COLOR MAP ───────────────────────────────────────────────────────
const SUBJECT_COLORS = {
  'Mathematics':        { fg: '#1d4ed8', bg: '#dbeafe', dark: 'bg-blue-500/15 text-blue-300 border-blue-500/20' },
  'Science':            { fg: '#059669', bg: '#d1fae5', dark: 'bg-emerald-500/15 text-emerald-300 border-emerald-500/20' },
  'English':            { fg: '#7c3aed', bg: '#ede9fe', dark: 'bg-violet-500/15 text-violet-300 border-violet-500/20' },
  'Hindi':              { fg: '#dc2626', bg: '#fee2e2', dark: 'bg-rose-500/15 text-rose-300 border-rose-500/20' },
  'Social Studies':     { fg: '#d97706', bg: '#fef3c7', dark: 'bg-amber-500/15 text-amber-300 border-amber-500/20' },
  'Computer Science':   { fg: '#0891b2', bg: '#cffafe', dark: 'bg-cyan-500/15 text-cyan-300 border-cyan-500/20' },
  'Physical Education': { fg: '#16a34a', bg: '#dcfce7', dark: 'bg-green-500/15 text-green-300 border-green-500/20' },
  'Art & Craft':        { fg: '#db2777', bg: '#fce7f3', dark: 'bg-pink-500/15 text-pink-300 border-pink-500/20' },
  'Moral Science':      { fg: '#7c3aed', bg: '#f5f3ff', dark: 'bg-purple-500/15 text-purple-300 border-purple-500/20' },
  'Drawing':            { fg: '#0369a1', bg: '#e0f2fe', dark: 'bg-sky-500/15 text-sky-300 border-sky-500/20' },
}
const subjectColor = (sub) => SUBJECT_COLORS[sub] || { fg: '#64748b', bg: '#f1f5f9', dark: 'bg-slate-500/15 text-slate-300 border-slate-500/20' }

// ─── PRIMITIVE COMPONENTS ────────────────────────────────────────────────────

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

// ─── SCHOOL HEADER ───────────────────────────────────────────────────────────
function SchoolHeader({ session, type, classOrFaculty, timetableName }) {
  return (
    <div className="rounded-2xl border border-blue-100 dark:border-[rgba(99,102,241,0.2)] bg-gradient-to-r from-blue-50 via-white to-indigo-50 dark:from-[#1a1f35] dark:via-[#1e2238] dark:to-[#1a1f35] px-6 py-5 text-center shadow-sm">
      <div className="flex items-center justify-center gap-2 mb-1">
        <Building2 className="w-5 h-5 text-blue-600 dark:text-blue-400 flex-shrink-0" />
        <h2 className="text-[16px] sm:text-[18px] font-extrabold text-slate-800 dark:text-slate-100 tracking-tight leading-snug">
          {SCHOOL_INFO.name}
        </h2>
      </div>
      <div className="flex items-center justify-center gap-1.5 text-[12px] text-slate-500 dark:text-slate-400 mb-3">
        <MapPin className="w-3.5 h-3.5 flex-shrink-0" />
        <span>{SCHOOL_INFO.address}</span>
      </div>
      <div className="flex flex-wrap items-center justify-center gap-2">
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-100 dark:bg-amber-500/15 border border-amber-200 dark:border-amber-500/25 text-[12px] font-bold text-amber-700 dark:text-amber-400">
          <Calendar className="w-3 h-3" /> Session: {session}
        </span>
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-blue-100 dark:bg-blue-500/15 border border-blue-200 dark:border-blue-500/25 text-[12px] font-bold text-blue-700 dark:text-blue-400">
          {type === 'Class Wise'
            ? <><GraduationCap className="w-3 h-3" /> Class: {classOrFaculty}</>
            : <><Users className="w-3 h-3" /> Faculty: {classOrFaculty}</>}
        </span>
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-violet-100 dark:bg-violet-500/15 border border-violet-200 dark:border-violet-500/25 text-[12px] font-bold text-violet-700 dark:text-violet-400">
          <BookOpen className="w-3 h-3" /> {timetableName}
        </span>
      </div>
      <p className="mt-3 text-[13px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400">
        Time Table Report ({type})
      </p>
    </div>
  )
}

// ─── SUBJECT CELL ────────────────────────────────────────────────────────────
function SubjectCell({ cell }) {
  if (!cell) return null
  const { fg, bg } = subjectColor(cell.subject)
  return (
    <div
      className="rounded-lg px-2 py-1.5 text-center border"
      style={{ background: bg, borderColor: fg + '30' }}
    >
      <p className="text-[11px] font-bold leading-tight" style={{ color: fg }}>{cell.subject}</p>
      <p className="text-[10px] text-slate-500 mt-0.5 truncate">{cell.teacher}</p>
      <p className="text-[10px] text-slate-400 truncate">{cell.room}</p>
    </div>
  )
}

function SubjectCellDark({ cell }) {
  if (!cell) return null
  const dc = subjectColor(cell.subject).dark
  return (
    <div className={`rounded-lg px-2 py-1.5 text-center border ${dc}`}>
      <p className="text-[11px] font-bold leading-tight truncate">{cell.subject}</p>
      <p className="text-[10px] opacity-70 mt-0.5 truncate">{cell.teacher}</p>
      <p className="text-[10px] opacity-50 truncate">{cell.room}</p>
    </div>
  )
}

// ─── DESKTOP TIMETABLE GRID ──────────────────────────────────────────────────
function DesktopGrid({ grid }) {
  return (
    <div className="overflow-x-auto">
      <table className="w-full min-w-[720px] border-collapse">
        <thead>
          <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
            <th className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 w-[120px] whitespace-nowrap sticky left-0 bg-slate-50 dark:bg-[#1a1f35] z-10">
              Period / Day
            </th>
            {DAYS.map(day => (
              <th key={day} className="px-3 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap min-w-[120px]">
                {day}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {PERIODS.map(period => (
            <tr
              key={period.id}
              className={`border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] transition-colors
                ${period.isBreak
                  ? 'bg-slate-50/80 dark:bg-white/[0.015]'
                  : 'hover:bg-slate-50/60 dark:hover:bg-white/[0.02]'}`}
            >
              {/* Period label — sticky */}
              <td className="px-3 py-2 sticky left-0 bg-white dark:bg-[#1a1f35] z-10 border-r border-slate-100 dark:border-[rgba(99,102,241,0.07)]">
                {period.isBreak ? (
                  <div className="flex flex-col items-center justify-center py-1">
                    <span className="text-[11px] font-bold uppercase tracking-wide text-slate-400 dark:text-slate-500">{period.label}</span>
                    <span className="text-[10px] text-slate-400 dark:text-slate-600 flex items-center gap-0.5 mt-0.5">
                      <Clock className="w-2.5 h-2.5" />{period.time}
                    </span>
                  </div>
                ) : (
                  <div className="flex flex-col">
                    <span className="text-[12px] font-bold text-slate-700 dark:text-slate-200">{period.label}</span>
                    <span className="text-[10px] text-slate-400 dark:text-slate-500 flex items-center gap-0.5 mt-0.5">
                      <Clock className="w-2.5 h-2.5" />{period.time}
                    </span>
                  </div>
                )}
              </td>

              {/* Cells per day */}
              {DAYS.map(day => (
                <td key={day} className="px-2 py-2 text-center align-middle">
                  {period.isBreak ? (
                    <div className="flex items-center justify-center">
                      <span className="text-[10px] text-slate-400 dark:text-slate-600 italic">
                        {period.label === 'Lunch' ? '🍽 Lunch Break' : '☕ Short Break'}
                      </span>
                    </div>
                  ) : (
                    <>
                      {/* Light mode */}
                      <div className="dark:hidden">
                        <SubjectCell cell={grid[day]?.[period.id]} />
                      </div>
                      {/* Dark mode */}
                      <div className="hidden dark:block">
                        <SubjectCellDark cell={grid[day]?.[period.id]} />
                      </div>
                    </>
                  )}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

// ─── MOBILE: DAY TABS + PERIOD CARDS ────────────────────────────────────────
function MobileTimetable({ grid }) {
  const [activeDay, setActiveDay] = useState(0)
  const day = DAYS[activeDay]
  const { fg: _fg, bg: _bg } = subjectColor('Mathematics')

  return (
    <div className="space-y-3">
      {/* Day tabs — scrollable horizontally (only tabs, not content) */}
      <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-hide px-4 pt-1">
        {DAYS_SHORT.map((d, i) => (
          <button
            key={d}
            type="button"
            onClick={() => setActiveDay(i)}
            className={`flex-shrink-0 px-3.5 py-2 rounded-xl text-[12px] font-bold transition-all
              ${activeDay === i
                ? 'bg-blue-600 text-white shadow-md shadow-blue-500/30 dark:bg-indigo-600 dark:shadow-indigo-500/30'
                : 'bg-slate-100 text-slate-500 dark:bg-slate-800/60 dark:text-slate-400'}`}
          >
            {d}
          </button>
        ))}
      </div>

      {/* Full day name */}
      <div className="px-4">
        <p className="text-[13px] font-bold text-blue-700 dark:text-indigo-400 flex items-center gap-1.5">
          <Calendar className="w-3.5 h-3.5" />{DAYS[activeDay]}
        </p>
      </div>

      {/* Period cards */}
      <div className="px-4 space-y-2.5">
        {PERIODS.map(period => {
          if (period.isBreak) {
            return (
              <div key={period.id} className="flex items-center gap-3 py-2 px-4 rounded-xl bg-slate-50 dark:bg-white/[0.02] border border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
                <Clock className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                <span className="text-[11px] text-slate-400 dark:text-slate-500 italic font-medium">{period.label} · {period.time}</span>
              </div>
            )
          }

          const cell = grid[day]?.[period.id]
          if (!cell) return null
          const { fg, bg } = subjectColor(cell.subject)
          const dc = subjectColor(cell.subject).dark

          return (
            <div key={period.id} className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
              {/* Period label strip */}
              <div className="flex items-center gap-2 px-4 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/70 dark:bg-white/[0.02]">
                <span className="text-[11px] font-bold text-slate-600 dark:text-slate-400 uppercase tracking-wide">{period.label}</span>
                <span className="flex items-center gap-1 text-[10px] text-slate-400 dark:text-slate-500 ml-auto">
                  <Clock className="w-2.5 h-2.5" />{period.time}
                </span>
              </div>
              {/* Cell content */}
              <div className="flex items-center gap-3 px-4 py-3">
                {/* Subject color badge */}
                <span
                  className="w-10 h-10 rounded-xl flex-shrink-0 flex items-center justify-center text-[10px] font-bold dark:hidden"
                  style={{ background: bg, color: fg }}
                >
                  {cell.subject.slice(0, 2).toUpperCase()}
                </span>
                <span className={`w-10 h-10 rounded-xl flex-shrink-0 hidden dark:flex items-center justify-center text-[10px] font-bold border ${dc}`}>
                  {cell.subject.slice(0, 2).toUpperCase()}
                </span>
                <div className="flex-1 min-w-0">
                  <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{cell.subject}</p>
                  <p className="text-[12px] text-slate-500 dark:text-slate-400 mt-0.5 truncate">{cell.teacher}</p>
                </div>
                <div className="flex-shrink-0 text-right">
                  <span className="text-[11px] font-semibold text-slate-400 dark:text-slate-500 bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded-md">{cell.room}</span>
                </div>
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, fields, errors, onShow, loading }) {
  const { type, setType, session, setSession, cls, setCls, faculty, setFaculty, timetable, setTimetable } = fields
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl max-h-[92vh] overflow-y-auto"
        style={{ animation: 'drawerUp .25s ease' }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
        <div className="flex justify-center pt-3 pb-1 sticky top-0 bg-white dark:bg-[#1a1f35] z-10">
          <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
        </div>
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] sticky top-5 bg-white dark:bg-[#1a1f35] z-10">
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Filters</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5 space-y-4">
          {/* Type */}
          <div>
            <label className="text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide block mb-2">Type</label>
            <div className="flex gap-2">
              {['Class Wise', 'Faculty Wise'].map(t => (
                <button
                  key={t}
                  type="button"
                  onClick={() => setType(t)}
                  className={`flex-1 py-2.5 rounded-xl text-[13px] font-semibold transition-all
                    ${type === t
                      ? 'bg-blue-600 text-white shadow-md shadow-blue-500/20 dark:bg-indigo-600'
                      : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300'}`}
                >
                  {t}
                </button>
              ))}
            </div>
          </div>
          <Field label="Session" error={errors.session} required>
            <NativeSelect value={session} onChange={e => setSession(e.target.value)} placeholder="-- Select Session --" error={errors.session}>
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
          {type === 'Class Wise' ? (
            <Field label="Class" error={errors.classOrFaculty} required>
              <NativeSelect value={cls} onChange={e => setCls(e.target.value)} placeholder="-- Select Class --" error={errors.classOrFaculty}>
                {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
              </NativeSelect>
            </Field>
          ) : (
            <Field label="Faculty" error={errors.classOrFaculty} required>
              <NativeSelect value={faculty} onChange={e => setFaculty(e.target.value)} placeholder="-- Select Faculty --" error={errors.classOrFaculty}>
                {FACULTIES.map(f => <option key={f} value={f}>{f}</option>)}
              </NativeSelect>
            </Field>
          )}
          <Field label="Timetable" error={errors.timetable} required>
            <NativeSelect value={timetable} onChange={e => setTimetable(e.target.value)} placeholder="-- Select Timetable --" error={errors.timetable}>
              {TIMETABLE_NAMES.map(t => <option key={t} value={t}>{t}</option>)}
            </NativeSelect>
          </Field>
        </div>
        <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex gap-3 sticky bottom-0 bg-white dark:bg-[#1a1f35]">
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

// ─── STATS BAR ───────────────────────────────────────────────────────────────
function StatsBar({ grid }) {
  const totalPeriods = PERIODS.filter(p => !p.isBreak).length
  const totalSlots = DAYS.length * totalPeriods

  // Count unique subjects
  const subjects = new Set()
  const teachers = new Set()
  DAYS.forEach(day => {
    PERIODS.forEach(p => {
      if (!p.isBreak && grid[day]?.[p.id]) {
        subjects.add(grid[day][p.id].subject)
        teachers.add(grid[day][p.id].teacher)
      }
    })
  })

  const stats = [
    { label: 'Days/Week', value: DAYS.length, icon: Calendar, color: 'blue' },
    { label: 'Periods/Day', value: totalPeriods, icon: Clock, color: 'violet' },
    { label: 'Total Slots', value: totalSlots, icon: LayoutGrid, color: 'emerald' },
    { label: 'Subjects', value: subjects.size, icon: BookOpen, color: 'amber' },
  ]

  const colorMap = {
    blue:    'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400',
    violet:  'bg-violet-50 text-violet-600 dark:bg-violet-500/10 dark:text-violet-400',
    emerald: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400',
    amber:   'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400',
  }

  return (
    <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
      {stats.map(s => (
        <div key={s.label} className="flex items-center gap-3 rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-white dark:bg-[#1a1f35] px-4 py-3 shadow-sm">
          <span className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 ${colorMap[s.color]}`}>
            <s.icon className="w-5 h-5" />
          </span>
          <div>
            <p className="text-[20px] font-bold text-slate-800 dark:text-slate-100 tabular-nums leading-tight">{s.value}</p>
            <p className="text-[11px] text-slate-500 dark:text-slate-400">{s.label}</p>
          </div>
        </div>
      ))}
    </div>
  )
}

// ─── LEGEND ──────────────────────────────────────────────────────────────────
function SubjectLegend({ grid }) {
  const seen = new Map()
  DAYS.forEach(day => {
    PERIODS.forEach(p => {
      if (!p.isBreak && grid[day]?.[p.id]) {
        const cell = grid[day][p.id]
        if (!seen.has(cell.subject)) seen.set(cell.subject, cell)
      }
    })
  })

  return (
    <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
      <div className="flex items-center gap-3 px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/70 dark:bg-white/[0.02]">
        <span className="w-1 h-5 rounded-full bg-violet-500 flex-shrink-0" />
        <BookOpen className="w-4 h-4 text-violet-600 dark:text-violet-400 flex-shrink-0" />
        <span className="text-[13px] font-bold text-slate-700 dark:text-slate-200">Subject Legend</span>
      </div>
      <div className="flex flex-wrap gap-2 p-4">
        {[...seen.entries()].map(([sub, cell]) => {
          const { fg, bg } = subjectColor(sub)
          const dc = subjectColor(sub).dark
          return (
            <div key={sub} className="flex items-center gap-1.5">
              {/* Light */}
              <span className="dark:hidden inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-semibold border"
                style={{ background: bg, color: fg, borderColor: fg + '30' }}>
                <span className="w-2 h-2 rounded-full flex-shrink-0" style={{ background: fg }} />
                {sub}
              </span>
              {/* Dark */}
              <span className={`hidden dark:inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-semibold border ${dc}`}>
                <span className="w-2 h-2 rounded-full flex-shrink-0 bg-current opacity-70" />
                {sub}
              </span>
            </div>
          )
        })}
      </div>
    </div>
  )
}

// ─── MAIN COMPONENT ──────────────────────────────────────────────────────────
export default function TimetableReport() {
  const [type,        setType]        = useState('Class Wise')
  const [session,     setSession]     = useState('')
  const [cls,         setCls]         = useState('')
  const [faculty,     setFaculty]     = useState('')
  const [timetable,   setTimetable]   = useState('')
  const [grid,        setGrid]        = useState(null)
  const [loading,     setLoading]     = useState(false)
  const [exporting,   setExporting]   = useState(false)
  const [filterOpen,  setFilterOpen]  = useState(false)
  const [errors,      setErrors]      = useState({})
  const [toast,       setToast]       = useState(null)
  const [shown,       setShown]       = useState(false)
  const [shownMeta,   setShownMeta]   = useState(null)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  const classOrFaculty = type === 'Class Wise' ? cls : faculty
  const setClassOrFaculty = type === 'Class Wise' ? setCls : setFaculty

  // ── Validate & Fetch ────────────────────────────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}
    if (!session)         err.session         = 'Please select a session'
    if (!classOrFaculty)  err.classOrFaculty  = type === 'Class Wise' ? 'Please select a class' : 'Please select a faculty'
    if (!timetable)       err.timetable       = 'Please select a timetable'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)

    // Simulate API call
    setTimeout(() => {
      const result = generateTimetable(classOrFaculty, session, timetable)
      setGrid(result)
      setShownMeta({ type, session, classOrFaculty, timetable })
      setShown(true)
      setLoading(false)
      showToast(`Timetable loaded for ${classOrFaculty} · ${session}`)
    }, 750)
  }, [type, session, cls, faculty, timetable, classOrFaculty])

  const handleReset = () => {
    setSession(''); setCls(''); setFaculty(''); setTimetable('')
    setErrors({}); setShown(false); setGrid(null); setShownMeta(null)
  }

  // Switch type — reset class/faculty
  const handleTypeSwitch = (t) => {
    setType(t)
    setCls(''); setFaculty('')
    setErrors(p => ({ ...p, classOrFaculty: undefined }))
    if (shown) { setShown(false); setGrid(null) }
  }

  const handleExcel = () => {
    if (!grid) { showToast('No data to export. Show report first.', 'error'); return }
    setExporting(true)
    setTimeout(() => {
      setExporting(false)
      showToast('Excel export ready! (API integration pending)')
    }, 1200)
  }

  const hasResults = shown && grid !== null

  // Active filter count for mobile badge
  const activeFilters = [session, classOrFaculty, timetable].filter(Boolean).length

  // ── Filter fields object for drawer ───────────────────────────────────────
  const drawerFields = { type, setType: handleTypeSwitch, session, setSession, cls, setCls, faculty, setFaculty, timetable, setTimetable }

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ──────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Table2 className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Time Table Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Class-wise or Faculty-wise scheduled timetable.
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

      {/* ── DESKTOP Filter Card ─────────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Filter className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Search Filters</span>
        </div>
        <div className="p-5">
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4 items-end">
            {/* Type toggle */}
            <div className="flex flex-col gap-1 lg:col-span-1">
              <label className="text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">Type</label>
              <div className="flex gap-1.5 p-1 rounded-xl bg-slate-100 dark:bg-slate-800/60">
                {['Class Wise', 'Faculty Wise'].map(t => (
                  <button
                    key={t}
                    type="button"
                    onClick={() => handleTypeSwitch(t)}
                    className={`flex-1 py-1.5 rounded-lg text-[12px] font-semibold transition-all whitespace-nowrap
                      ${type === t
                        ? 'bg-white dark:bg-[#1e2238] text-blue-700 dark:text-indigo-300 shadow-sm'
                        : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-300'}`}
                  >
                    {t}
                  </button>
                ))}
              </div>
            </div>

            {/* Session */}
            <Field label="Session" error={errors.session} required>
              <NativeSelect
                value={session}
                onChange={e => { setSession(e.target.value); setErrors(p => ({ ...p, session: undefined })) }}
                placeholder="-- Select --"
                error={errors.session}
              >
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>

            {/* Class / Faculty */}
            {type === 'Class Wise' ? (
              <Field label="Class" error={errors.classOrFaculty} required>
                <NativeSelect
                  value={cls}
                  onChange={e => { setCls(e.target.value); setErrors(p => ({ ...p, classOrFaculty: undefined })) }}
                  placeholder="-- Select Class --"
                  error={errors.classOrFaculty}
                >
                  {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
                </NativeSelect>
              </Field>
            ) : (
              <Field label="Faculty" error={errors.classOrFaculty} required>
                <NativeSelect
                  value={faculty}
                  onChange={e => { setFaculty(e.target.value); setErrors(p => ({ ...p, classOrFaculty: undefined })) }}
                  placeholder="-- Select Faculty --"
                  error={errors.classOrFaculty}
                >
                  {FACULTIES.map(f => <option key={f} value={f}>{f}</option>)}
                </NativeSelect>
              </Field>
            )}

            {/* Timetable */}
            <Field label="Timetable" error={errors.timetable} required>
              <NativeSelect
                value={timetable}
                onChange={e => { setTimetable(e.target.value); setErrors(p => ({ ...p, timetable: undefined })) }}
                placeholder="-- Select --"
                error={errors.timetable}
              >
                {TIMETABLE_NAMES.map(t => <option key={t} value={t}>{t}</option>)}
              </NativeSelect>
            </Field>

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
                  bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors">
                <RefreshCw className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* ── MOBILE Filter Bar ───────────────────────────────────────────────── */}
      <div className="flex sm:hidden gap-2">
        <button type="button" onClick={() => setFilterOpen(true)}
          className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
            bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {classOrFaculty ? classOrFaculty.split('(')[0].trim() : 'Select Filters'}
          {activeFilters > 0 && (
            <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilters}/3</span>
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

      {/* Mobile Filter Drawer */}
      <FilterDrawer
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        fields={drawerFields}
        errors={errors}
        onShow={handleShow}
        loading={loading}
      />

      {/* ── Loading Skeleton ─────────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          <div className="h-24 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-4" />
          <div className="grid grid-cols-4 gap-3 mb-4">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="h-16 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" />
            ))}
          </div>
          {[...Array(7)].map((_, i) => (
            <div key={i} className="h-10 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.1 }} />
          ))}
        </div>
      )}

      {/* ── Results ──────────────────────────────────────────────────────────── */}
      {hasResults && !loading && shownMeta && (
        <>
          {/* School Header */}
          <SchoolHeader
            session={shownMeta.session}
            type={shownMeta.type}
            classOrFaculty={shownMeta.classOrFaculty}
            timetableName={shownMeta.timetable}
          />

          {/* Stats Bar */}
          <StatsBar grid={grid} />

          {/* Main Grid Card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <Table2 className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Timetable Grid</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownMeta.classOrFaculty}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
                  {shownMeta.type}
                </span>
              </div>
              {/* Desktop-only hint */}
              <div className="hidden sm:flex items-center gap-1.5 text-[11px] text-slate-400 dark:text-slate-500">
                <Info className="w-3.5 h-3.5 flex-shrink-0" />
                <span>Scroll right to see all days</span>
              </div>
            </div>

            {/* Info bar */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
              <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <p className="text-[12px] text-blue-700 dark:text-blue-400">
                Showing {PERIODS.filter(p => !p.isBreak).length} periods × {DAYS.length} days.
                Break rows are marked separately. Subject colors are consistent across the grid.
              </p>
            </div>

            {/* DESKTOP grid */}
            <div className="hidden md:block">
              <DesktopGrid grid={grid} />
            </div>

            {/* MOBILE day-tabs */}
            <div className="md:hidden py-4">
              <MobileTimetable grid={grid} />
            </div>

            {/* Footer */}
            <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
              <p className="text-[12px] text-slate-400 dark:text-slate-500">
                Session: <span className="font-semibold text-slate-700 dark:text-slate-300">{shownMeta.session}</span>
                &nbsp;·&nbsp;
                {shownMeta.type === 'Class Wise' ? 'Class' : 'Faculty'}:{' '}
                <span className="font-semibold text-slate-700 dark:text-slate-300">{shownMeta.classOrFaculty}</span>
              </p>
              <TrendingUp className="w-4 h-4 text-slate-300 dark:text-slate-600" />
            </div>
          </div>

          {/* Subject Legend */}
          <SubjectLegend grid={grid} />
        </>
      )}

      {/* ── Empty State ───────────────────────────────────────────────────────── */}
      {!hasResults && !loading && (
        <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400 dark:text-slate-600">
          <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
            <Table2 className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No timetable generated yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select type, session, class/faculty, and timetable — then click <strong>Show</strong>.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
