/**
 * TimetableConfiguration.jsx
 * Folder: src/pages/Configuration/TimetableConfiguration.jsx
 *
 * Converted from: Configuration/lecture_timings.aspx
 *
 * Features:
 *  - Session dropdown + Configuration Title
 *  - Add periods with Start Time (HH:MM AM/PM), Duration (min → auto-calc End Time), Break Time
 *  - "Add More" row dynamically adds periods
 *  - Save config → appears in saved-configs list below
 *  - Saved configs: expandable rows showing period breakdown
 *  - Edit config title inline
 *  - Desktop: dense ERP table
 *  - Mobile: card/accordion layout, filter drawer, no horizontal scroll
 *  - Dark/light theme via Tailwind dark: classes
 */

import { useState, useMemo, useCallback, useRef } from 'react'
import {
  Clock, Plus, Trash2, Save, RefreshCw,
  ChevronDown, ChevronRight, AlertCircle, Check, X,
  SlidersHorizontal, Eye, Loader2, Edit3,
  BookOpen, Calendar, Timer, Coffee,
  Info, Search, Settings2, Building2, MapPin,
  ClipboardList, CheckCircle2, AlarmClock
} from 'lucide-react'

// ─── STATIC DATA ──────────────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const HOURS = Array.from({ length: 13 }, (_, i) => String(i).padStart(2, '0'))   // 00–12
const MINUTES = Array.from({ length: 60 }, (_, i) => String(i).padStart(2, '0')) // 00–59
const BREAK_OPTIONS = ['00', '05', '10', '15', '20', '25', '30', '35', '40', '45', '50', '55']
const DURATION_OPTS = Array.from({ length: 120 }, (_, i) => String(i + 1).padStart(2, '0')) // 01–120
  .concat(['00'])

// Prefill list of period label names
const PERIOD_NAMES = [
  'Period 1', 'Period 2', 'Period 3', 'Period 4', 'Period 5',
  'Period 6', 'Period 7', 'Period 8', 'Period 9', 'Period 10',
  'Lunch Break', 'Short Break', 'Assembly', 'Games', 'Library'
]

// Dummy saved configs matching ASPX gvdconfig / GridView3 pattern
const INITIAL_CONFIGS = [
  {
    tt_uid: 1,
    tt_config: 'Morning Shift Configuration',
    session: '2025-26',
    status: 'Active',
    periods: [
      { lecture_id: 1, lecture_name: 'Assembly',  st_hr: '07', st_min: '30', st_mode: 'AM', duration: '15', end_hr: '07', end_min: '45', end_mode: 'AM', break_time: '00', period_time: '07:30 AM – 07:45 AM' },
      { lecture_id: 2, lecture_name: 'Period 1',  st_hr: '07', st_min: '45', st_mode: 'AM', duration: '45', end_hr: '08', end_min: '30', end_mode: 'AM', break_time: '05', period_time: '07:45 AM – 08:30 AM' },
      { lecture_id: 3, lecture_name: 'Period 2',  st_hr: '08', st_min: '35', st_mode: 'AM', duration: '45', end_hr: '09', end_min: '20', end_mode: 'AM', break_time: '05', period_time: '08:35 AM – 09:20 AM' },
      { lecture_id: 4, lecture_name: 'Period 3',  st_hr: '09', st_min: '25', st_mode: 'AM', duration: '45', end_hr: '10', end_min: '10', end_mode: 'AM', break_time: '10', period_time: '09:25 AM – 10:10 AM' },
      { lecture_id: 5, lecture_name: 'Lunch Break', st_hr: '10', st_min: '20', st_mode: 'AM', duration: '30', end_hr: '10', end_min: '50', end_mode: 'AM', break_time: '00', period_time: '10:20 AM – 10:50 AM' },
      { lecture_id: 6, lecture_name: 'Period 4',  st_hr: '10', st_min: '50', st_mode: 'AM', duration: '45', end_hr: '11', end_min: '35', end_mode: 'AM', break_time: '05', period_time: '10:50 AM – 11:35 AM' },
      { lecture_id: 7, lecture_name: 'Period 5',  st_hr: '11', st_min: '40', st_mode: 'AM', duration: '45', end_hr: '12', end_min: '25', end_mode: 'PM', break_time: '00', period_time: '11:40 AM – 12:25 PM' },
    ]
  },
  {
    tt_uid: 2,
    tt_config: 'Afternoon Shift Configuration',
    session: '2024-25',
    status: 'Active',
    periods: [
      { lecture_id: 1, lecture_name: 'Period 1',  st_hr: '12', st_min: '00', st_mode: 'PM', duration: '45', end_hr: '12', end_min: '45', end_mode: 'PM', break_time: '05', period_time: '12:00 PM – 12:45 PM' },
      { lecture_id: 2, lecture_name: 'Period 2',  st_hr: '12', st_min: '50', st_mode: 'PM', duration: '45', end_hr: '01', end_min: '35', end_mode: 'PM', break_time: '05', period_time: '12:50 PM – 01:35 PM' },
      { lecture_id: 3, lecture_name: 'Short Break', st_hr: '01', st_min: '40', st_mode: 'PM', duration: '20', end_hr: '02', end_min: '00', end_mode: 'PM', break_time: '00', period_time: '01:40 PM – 02:00 PM' },
      { lecture_id: 4, lecture_name: 'Period 3',  st_hr: '02', st_min: '00', st_mode: 'PM', duration: '45', end_hr: '02', end_min: '45', end_mode: 'PM', break_time: '00', period_time: '02:00 PM – 02:45 PM' },
    ]
  },
]

// ─── HELPERS ──────────────────────────────────────────────────────────────────

/** Convert 12h components to 24h minutes-from-midnight for arithmetic */
function to24Minutes(hr, min, mode) {
  let h = parseInt(hr, 10)
  const m = parseInt(min, 10)
  if (mode === 'AM') {
    if (h === 12) h = 0
  } else {
    if (h !== 12) h += 12
  }
  return h * 60 + m
}

/** Convert minutes-from-midnight back to { hr, min, mode } 12h */
function from24Minutes(total) {
  total = ((total % 1440) + 1440) % 1440 // wrap
  const h24 = Math.floor(total / 60)
  const m = total % 60
  const mode = h24 < 12 ? 'AM' : 'PM'
  let h = h24 % 12
  if (h === 0) h = 12
  return {
    hr: String(h).padStart(2, '0'),
    min: String(m).padStart(2, '0'),
    mode,
  }
}

/** Format display string for a period time range */
function formatPeriodTime(row) {
  return `${row.st_hr}:${row.st_min} ${row.st_mode} – ${row.end_hr}:${row.end_min} ${row.end_mode}`
}

/** Generate a blank period row */
let _rowId = 1000
function blankRow(prevRow = null) {
  const id = ++_rowId
  if (prevRow) {
    // Start time = prev end time + prev break
    const prevEndMin = to24Minutes(prevRow.end_hr, prevRow.end_min, prevRow.end_mode)
    const breakMin = parseInt(prevRow.break_time, 10) || 0
    const newStart = from24Minutes(prevEndMin + breakMin)
    const endMin = to24Minutes(newStart.hr, newStart.min, newStart.mode) + 45
    const newEnd = from24Minutes(endMin)
    return {
      _id: id,
      lecture_name: `Period ${id}`,
      st_hr: newStart.hr,
      st_min: newStart.min,
      st_mode: newStart.mode,
      duration: '45',
      end_hr: newEnd.hr,
      end_min: newEnd.min,
      end_mode: newEnd.mode,
      break_time: '05',
    }
  }
  return {
    _id: id,
    lecture_name: 'Period 1',
    st_hr: '07',
    st_min: '30',
    st_mode: 'AM',
    duration: '45',
    end_hr: '08',
    end_min: '15',
    end_mode: 'AM',
    break_time: '05',
  }
}

// ─── PRIMITIVE COMPONENTS ─────────────────────────────────────────────────────

function NativeSelect({ value, onChange, children, placeholder, error, disabled, className = '' }) {
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
          ${error ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}
          ${className}`}
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
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(10px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
      {type === 'success' ? <Check className="w-4 h-4 flex-shrink-0" /> : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75 hover:opacity-100" /></button>
    </div>
  )
}

// ─── TIME PICKER INLINE (3 selects: HH : MM  AM/PM) ──────────────────────────
function TimePicker({ hr, min, mode, onHr, onMin, onMode, disabled = false }) {
  return (
    <div className="flex items-center gap-1">
      <div className="relative">
        <select
          value={hr}
          onChange={e => onHr(e.target.value)}
          disabled={disabled}
          className="appearance-none pl-2 pr-5 py-1.5 text-[12px] rounded-lg border outline-none transition-all
            bg-white text-slate-700 border-slate-200 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
            dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] dark:focus:border-indigo-400
            disabled:opacity-50 cursor-pointer w-[54px]"
        >
          {HOURS.map(h => <option key={h} value={h}>{h}</option>)}
        </select>
        <ChevronDown className="absolute right-1 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
      </div>
      <span className="text-[13px] font-bold text-slate-400 dark:text-slate-500">:</span>
      <div className="relative">
        <select
          value={min}
          onChange={e => onMin(e.target.value)}
          disabled={disabled}
          className="appearance-none pl-2 pr-5 py-1.5 text-[12px] rounded-lg border outline-none transition-all
            bg-white text-slate-700 border-slate-200 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
            dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] dark:focus:border-indigo-400
            disabled:opacity-50 cursor-pointer w-[54px]"
        >
          {MINUTES.map(m => <option key={m} value={m}>{m}</option>)}
        </select>
        <ChevronDown className="absolute right-1 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
      </div>
      <div className="relative">
        <select
          value={mode}
          onChange={e => onMode(e.target.value)}
          disabled={disabled}
          className="appearance-none pl-2 pr-5 py-1.5 text-[12px] rounded-lg border outline-none transition-all
            bg-white text-slate-700 border-slate-200 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
            dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] dark:focus:border-indigo-400
            disabled:opacity-50 cursor-pointer w-[56px]"
        >
          <option value="AM">AM</option>
          <option value="PM">PM</option>
        </select>
        <ChevronDown className="absolute right-1 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
      </div>
    </div>
  )
}

// ─── PERIOD COLOR BADGE ───────────────────────────────────────────────────────
const PERIOD_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' }, { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' }, { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' }, { fg: '#dc2626', bg: '#fee2e2' },
  { fg: '#db2777', bg: '#fce7f3' }, { fg: '#0369a1', bg: '#e0f2fe' },
]
const periodColor = (idx) => PERIOD_COLORS[idx % PERIOD_COLORS.length]

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, session, setSession, configTitle, setConfigTitle, errors }) {
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
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Configuration Details</span>
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
          <Field label="Configuration Title" error={errors.configTitle} required>
            <input
              type="text"
              value={configTitle}
              onChange={e => setConfigTitle(e.target.value)}
              placeholder="e.g. Morning Shift Configuration"
              className={`w-full pl-3 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                bg-white text-slate-800 placeholder-slate-300
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400
                ${errors.configTitle ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
            />
          </Field>
        </div>
        <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <button type="button" onClick={onClose}
            className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white
              bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 transition-all">
            <Check className="w-4 h-4" /> Apply & Close
          </button>
        </div>
      </div>
    </>
  )
}

// ─── MOBILE PERIOD CARD (for add-form) ───────────────────────────────────────
function MobilePeriodCard({ row, idx, onChange, onDelete, canDelete }) {
  const [expanded, setExpanded] = useState(true)
  const { fg, bg } = periodColor(idx)

  const handleStartChange = (field, val) => {
    const updated = { ...row, [field]: val }
    // Recalculate end from start + duration
    const startMin = to24Minutes(
      field === 'st_hr' ? val : row.st_hr,
      field === 'st_min' ? val : row.st_min,
      field === 'st_mode' ? val : row.st_mode,
    )
    const dur = parseInt(updated.duration, 10) || 0
    const end = from24Minutes(startMin + dur)
    onChange({ ...updated, end_hr: end.hr, end_min: end.min, end_mode: end.mode })
  }

  const handleDurationChange = (dur) => {
    const updated = { ...row, duration: dur }
    const startMin = to24Minutes(row.st_hr, row.st_min, row.st_mode)
    const end = from24Minutes(startMin + (parseInt(dur, 10) || 0))
    onChange({ ...updated, end_hr: end.hr, end_min: end.min, end_mode: end.mode })
  }

  const handleEndChange = (field, val) => {
    const updated = { ...row, [field]: val }
    // Recalculate duration from start to new end
    const startMin = to24Minutes(row.st_hr, row.st_min, row.st_mode)
    const endMin = to24Minutes(
      field === 'end_hr' ? val : row.end_hr,
      field === 'end_min' ? val : row.end_min,
      field === 'end_mode' ? val : row.end_mode,
    )
    const diff = Math.max(0, endMin - startMin)
    onChange({ ...updated, duration: String(diff).padStart(2, '0') })
  }

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 py-3 bg-slate-50/70 dark:bg-white/[0.02]">
        <span className="w-8 h-8 rounded-xl flex-shrink-0 flex items-center justify-center text-[11px] font-bold" style={{ background: bg, color: fg }}>
          P{idx + 1}
        </span>
        <div className="flex-1 min-w-0">
          <input
            type="text"
            value={row.lecture_name}
            onChange={e => onChange({ ...row, lecture_name: e.target.value })}
            placeholder="Period name"
            className="w-full bg-transparent text-[14px] font-bold text-slate-800 dark:text-slate-100 outline-none placeholder-slate-300 dark:placeholder-slate-600"
          />
          <p className="text-[11px] text-slate-400 dark:text-slate-500">{row.st_hr}:{row.st_min} {row.st_mode} – {row.end_hr}:{row.end_min} {row.end_mode}</p>
        </div>
        <div className="flex items-center gap-1 flex-shrink-0">
          {canDelete && (
            <button onClick={onDelete} className="p-1.5 rounded-lg hover:bg-rose-50 dark:hover:bg-rose-500/10 text-slate-300 hover:text-rose-500 transition-colors">
              <Trash2 className="w-4 h-4" />
            </button>
          )}
          <button onClick={() => setExpanded(p => !p)}
            className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400 transition-all">
            <ChevronDown className={`w-4 h-4 transition-transform duration-200 ${expanded ? 'rotate-180' : ''}`} />
          </button>
        </div>
      </div>

      {expanded && (
        <div className="px-4 py-4 space-y-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
          {/* Start Time */}
          <div>
            <label className="text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 mb-2 block flex items-center gap-1.5">
              <AlarmClock className="w-3.5 h-3.5" /> Start Time
            </label>
            <TimePicker
              hr={row.st_hr} min={row.st_min} mode={row.st_mode}
              onHr={v => handleStartChange('st_hr', v)}
              onMin={v => handleStartChange('st_min', v)}
              onMode={v => handleStartChange('st_mode', v)}
            />
          </div>
          {/* Duration */}
          <div>
            <label className="text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 mb-2 block flex items-center gap-1.5">
              <Timer className="w-3.5 h-3.5" /> Duration (minutes)
            </label>
            <div className="relative w-28">
              <select
                value={row.duration}
                onChange={e => handleDurationChange(e.target.value)}
                className="w-full appearance-none pl-3 pr-7 py-1.5 text-[13px] rounded-lg border outline-none transition-all
                  bg-white text-slate-700 border-slate-200 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                  dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] cursor-pointer"
              >
                {Array.from({ length: 120 }, (_, i) => String(i + 1).padStart(2, '0')).map(d => (
                  <option key={d} value={d}>{d} min</option>
                ))}
              </select>
              <ChevronDown className="absolute right-2 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
            </div>
          </div>
          {/* End Time (auto-calculated, but editable) */}
          <div>
            <label className="text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 mb-2 block flex items-center gap-1.5">
              <Clock className="w-3.5 h-3.5" /> End Time <span className="text-[10px] normal-case text-blue-500 dark:text-blue-400">(auto-calculated)</span>
            </label>
            <TimePicker
              hr={row.end_hr} min={row.end_min} mode={row.end_mode}
              onHr={v => handleEndChange('end_hr', v)}
              onMin={v => handleEndChange('end_min', v)}
              onMode={v => handleEndChange('end_mode', v)}
            />
          </div>
          {/* Break Time */}
          <div>
            <label className="text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 mb-2 block flex items-center gap-1.5">
              <Coffee className="w-3.5 h-3.5" /> Break After (minutes)
            </label>
            <div className="relative w-28">
              <select
                value={row.break_time}
                onChange={e => onChange({ ...row, break_time: e.target.value })}
                className="w-full appearance-none pl-3 pr-7 py-1.5 text-[13px] rounded-lg border outline-none transition-all
                  bg-white text-slate-700 border-slate-200 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                  dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] cursor-pointer"
              >
                {BREAK_OPTIONS.map(b => <option key={b} value={b}>{b} min</option>)}
              </select>
              <ChevronDown className="absolute right-2 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── SAVED CONFIG ROW (desktop expand) ───────────────────────────────────────
function SavedConfigRow({ config, idx, onEdit, onUpdate, onCancel, editingId, editTitle, setEditTitle }) {
  const [expanded, setExpanded] = useState(false)
  const isEditing = editingId === config.tt_uid

  return (
    <>
      {/* Main row */}
      <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
        <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{idx + 1}</td>
        <td className="px-4 py-3">
          {isEditing ? (
            <input
              type="text"
              value={editTitle}
              onChange={e => setEditTitle(e.target.value)}
              className="w-full max-w-sm pl-3 pr-3 py-1.5 text-[13px] rounded-lg border outline-none
                bg-white text-slate-800 border-blue-300 ring-2 ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:border-indigo-400 dark:ring-indigo-500/20"
              autoFocus
            />
          ) : (
            <div className="flex items-center gap-3">
              <button
                onClick={() => setExpanded(p => !p)}
                className="flex items-center gap-2 group"
              >
                <span className={`transition-transform duration-200 text-slate-400 ${expanded ? 'rotate-90' : ''}`}>
                  <ChevronRight className="w-4 h-4" />
                </span>
                <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                  {config.tt_config}
                </span>
              </button>
              <span className="text-[11px] px-2 py-0.5 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-500 dark:text-slate-400 flex-shrink-0">
                {config.session}
              </span>
              <span className="text-[11px] px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-400 flex-shrink-0">
                {config.periods.length} periods
              </span>
            </div>
          )}
        </td>
        <td className="px-4 py-3 text-center">
          <span className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-semibold
            ${config.status === 'Active'
              ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-400'
              : 'bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400'}`}>
            <span className={`w-1.5 h-1.5 rounded-full ${config.status === 'Active' ? 'bg-emerald-500' : 'bg-slate-400'}`} />
            {config.status}
          </span>
        </td>
        <td className="px-4 py-3 text-center">
          <div className="flex items-center justify-center gap-2">
            {isEditing ? (
              <>
                <button onClick={() => onUpdate(config.tt_uid)}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold bg-emerald-600 text-white hover:bg-emerald-700 transition-colors">
                  <Check className="w-3.5 h-3.5" /> Update
                </button>
                <button onClick={onCancel}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
                  <X className="w-3.5 h-3.5" /> Cancel
                </button>
              </>
            ) : (
              <button onClick={() => onEdit(config)}
                className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold text-blue-600 hover:bg-blue-50 dark:text-blue-400 dark:hover:bg-blue-500/10 transition-colors">
                <Edit3 className="w-3.5 h-3.5" /> Edit
              </button>
            )}
          </div>
        </td>
      </tr>

      {/* Expanded periods sub-table */}
      {expanded && !isEditing && (
        <tr className="bg-slate-50/50 dark:bg-white/[0.01]">
          <td colSpan={4} className="px-6 py-3">
            <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.12)] overflow-hidden">
              <table className="w-full text-[12px]">
                <thead>
                  <tr className="bg-slate-100/70 dark:bg-white/[0.04] border-b border-slate-200 dark:border-[rgba(99,102,241,0.1)]">
                    {['#', 'Period', 'Start Time', 'Duration', 'End Time', 'Break'].map((h, i) => (
                      <th key={i} className="px-3 py-2 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {config.periods.map((p, pi) => {
                    const { fg, bg } = periodColor(pi)
                    return (
                      <tr key={p.lecture_id} className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.06)] last:border-0 hover:bg-slate-100/50 dark:hover:bg-white/[0.02]">
                        <td className="px-3 py-2 text-center text-slate-400 dark:text-slate-500">{pi + 1}</td>
                        <td className="px-3 py-2">
                          <div className="flex items-center gap-2">
                            <span className="w-6 h-6 rounded-lg flex items-center justify-center text-[9px] font-bold flex-shrink-0" style={{ background: bg, color: fg }}>
                              P{pi + 1}
                            </span>
                            <span className="font-semibold text-slate-700 dark:text-slate-200">{p.lecture_name}</span>
                          </div>
                        </td>
                        <td className="px-3 py-2 text-center">
                          <span className="px-2 py-0.5 rounded-md bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400 font-semibold tabular-nums">
                            {p.st_hr}:{p.st_min} {p.st_mode}
                          </span>
                        </td>
                        <td className="px-3 py-2 text-center">
                          <span className="px-2 py-0.5 rounded-md bg-amber-50 text-amber-700 dark:bg-amber-500/10 dark:text-amber-400 font-semibold tabular-nums">
                            {p.duration} min
                          </span>
                        </td>
                        <td className="px-3 py-2 text-center">
                          <span className="px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-400 font-semibold tabular-nums">
                            {p.end_hr}:{p.end_min} {p.end_mode}
                          </span>
                        </td>
                        <td className="px-3 py-2 text-center">
                          <span className={`px-2 py-0.5 rounded-md font-semibold tabular-nums
                            ${p.break_time === '00'
                              ? 'bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400'
                              : 'bg-violet-50 text-violet-700 dark:bg-violet-500/10 dark:text-violet-400'}`}>
                            {p.break_time} min
                          </span>
                        </td>
                      </tr>
                    )
                  })}
                </tbody>
              </table>
            </div>
          </td>
        </tr>
      )}
    </>
  )
}

// ─── SAVED CONFIG MOBILE CARD ─────────────────────────────────────────────────
function SavedConfigCard({ config, idx, onEdit, onUpdate, onCancel, editingId, editTitle, setEditTitle }) {
  const [expanded, setExpanded] = useState(false)
  const isEditing = editingId === config.tt_uid

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header */}
      <div className="flex items-center gap-3 px-4 py-3.5">
        <div className="w-10 h-10 rounded-xl flex-shrink-0 flex items-center justify-center bg-blue-50 dark:bg-blue-500/10">
          <ClipboardList className="w-5 h-5 text-blue-600 dark:text-blue-400" />
        </div>
        <div className="flex-1 min-w-0">
          {isEditing ? (
            <input
              type="text"
              value={editTitle}
              onChange={e => setEditTitle(e.target.value)}
              className="w-full pl-2 pr-2 py-1 text-[13px] rounded-lg border outline-none
                bg-white text-slate-800 border-blue-300 ring-2 ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:border-indigo-400"
              autoFocus
            />
          ) : (
            <>
              <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{config.tt_config}</p>
              <div className="flex items-center gap-2 mt-0.5 flex-wrap">
                <span className="text-[11px] text-slate-400 dark:text-slate-500">{config.session}</span>
                <span className="w-1 h-1 rounded-full bg-slate-300 dark:bg-slate-600" />
                <span className={`text-[11px] font-semibold ${config.status === 'Active' ? 'text-emerald-600 dark:text-emerald-400' : 'text-slate-500'}`}>
                  {config.status}
                </span>
                <span className="w-1 h-1 rounded-full bg-slate-300 dark:bg-slate-600" />
                <span className="text-[11px] text-slate-500 dark:text-slate-400">{config.periods.length} periods</span>
              </div>
            </>
          )}
        </div>
        <div className="flex items-center gap-1 flex-shrink-0">
          {isEditing ? (
            <>
              <button onClick={() => onUpdate(config.tt_uid)}
                className="p-1.5 rounded-lg bg-emerald-50 dark:bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 hover:bg-emerald-100">
                <Check className="w-4 h-4" />
              </button>
              <button onClick={onCancel}
                className="p-1.5 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-500 hover:bg-slate-200">
                <X className="w-4 h-4" />
              </button>
            </>
          ) : (
            <button onClick={() => onEdit(config)}
              className="p-1.5 rounded-lg hover:bg-blue-50 dark:hover:bg-blue-500/10 text-slate-400 hover:text-blue-600 dark:hover:text-blue-400 transition-colors">
              <Edit3 className="w-4 h-4" />
            </button>
          )}
          <button onClick={() => setExpanded(p => !p)}
            className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400 transition-colors">
            <ChevronDown className={`w-4 h-4 transition-transform duration-200 ${expanded ? 'rotate-180' : ''}`} />
          </button>
        </div>
      </div>

      {/* Period timeline preview */}
      <div className="px-4 pb-3">
        <div className="flex gap-1 overflow-hidden">
          {config.periods.slice(0, 8).map((p, pi) => {
            const { fg, bg } = periodColor(pi)
            return (
              <div key={pi} className="flex-1 min-w-0">
                <div
                  className="h-1.5 rounded-full"
                  style={{ background: fg, opacity: 0.7 }}
                />
                <p className="text-[9px] text-center mt-0.5 truncate text-slate-400 dark:text-slate-500">{p.lecture_name.split(' ')[0]}</p>
              </div>
            )
          })}
          {config.periods.length > 8 && (
            <span className="text-[10px] text-slate-400 self-end pb-0.5">+{config.periods.length - 8}</span>
          )}
        </div>
      </div>

      {/* Expanded: period cards */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 pt-3 pb-4 space-y-2">
          {config.periods.map((p, pi) => {
            const { fg, bg } = periodColor(pi)
            return (
              <div key={p.lecture_id} className="flex items-center gap-3 rounded-xl bg-slate-50 dark:bg-white/[0.03] border border-slate-100 dark:border-[rgba(99,102,241,0.08)] px-3 py-2.5">
                <span className="w-7 h-7 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold" style={{ background: bg, color: fg }}>
                  {pi + 1}
                </span>
                <div className="flex-1 min-w-0">
                  <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 truncate">{p.lecture_name}</p>
                  <p className="text-[11px] text-slate-400 dark:text-slate-500">
                    {p.st_hr}:{p.st_min} {p.st_mode} – {p.end_hr}:{p.end_min} {p.end_mode}
                  </p>
                </div>
                <div className="flex flex-col items-end gap-1 flex-shrink-0">
                  <span className="text-[11px] font-semibold px-2 py-0.5 rounded-md bg-amber-50 text-amber-700 dark:bg-amber-500/10 dark:text-amber-400 tabular-nums">
                    {p.duration}m
                  </span>
                  {p.break_time !== '00' && (
                    <span className="text-[10px] text-violet-500 dark:text-violet-400">+{p.break_time}m break</span>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}

// ─── MAIN PAGE ────────────────────────────────────────────────────────────────
export default function TimetableConfiguration() {
  // ── Form State ──────────────────────────────────────────────────────────────
  const [session, setSession]         = useState('')
  const [configTitle, setConfigTitle] = useState('')
  const [periods, setPeriods]         = useState([blankRow()])
  const [errors, setErrors]           = useState({})
  const [saving, setSaving]           = useState(false)

  // ── Saved configs state ─────────────────────────────────────────────────────
  const [savedConfigs, setSavedConfigs] = useState(INITIAL_CONFIGS)
  const [editingId, setEditingId]       = useState(null)
  const [editTitle, setEditTitle]       = useState('')

  // ── UI State ────────────────────────────────────────────────────────────────
  const [filterOpen, setFilterOpen] = useState(false)
  const [toast, setToast]           = useState(null)
  const [savedSearch, setSavedSearch] = useState('')

  const showToast = useCallback((msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }, [])

  // ── Period row CRUD ─────────────────────────────────────────────────────────
  const updateRow = useCallback((id, updated) => {
    setPeriods(prev => prev.map(r => r._id === id ? updated : r))
  }, [])

  const deleteRow = useCallback((id) => {
    setPeriods(prev => prev.filter(r => r._id !== id))
  }, [])

  const addRow = useCallback(() => {
    setPeriods(prev => {
      const last = prev[prev.length - 1] || null
      return [...prev, blankRow(last)]
    })
  }, [])

  // ── Desktop period row update handlers ─────────────────────────────────────
  const updateStartAndRecalc = useCallback((id, field, val, currentRow) => {
    const updated = { ...currentRow, [field]: val }
    const startMin = to24Minutes(
      field === 'st_hr'   ? val : currentRow.st_hr,
      field === 'st_min'  ? val : currentRow.st_min,
      field === 'st_mode' ? val : currentRow.st_mode,
    )
    const dur = parseInt(updated.duration, 10) || 0
    const end = from24Minutes(startMin + dur)
    setPeriods(prev => prev.map(r => r._id === id
      ? { ...updated, end_hr: end.hr, end_min: end.min, end_mode: end.mode }
      : r
    ))
  }, [])

  const updateDurationAndRecalc = useCallback((id, dur, currentRow) => {
    const startMin = to24Minutes(currentRow.st_hr, currentRow.st_min, currentRow.st_mode)
    const end = from24Minutes(startMin + (parseInt(dur, 10) || 0))
    setPeriods(prev => prev.map(r => r._id === id
      ? { ...r, duration: dur, end_hr: end.hr, end_min: end.min, end_mode: end.mode }
      : r
    ))
  }, [])

  const updateEndAndRecalc = useCallback((id, field, val, currentRow) => {
    const updated = { ...currentRow, [field]: val }
    const startMin = to24Minutes(currentRow.st_hr, currentRow.st_min, currentRow.st_mode)
    const endMin = to24Minutes(
      field === 'end_hr'   ? val : currentRow.end_hr,
      field === 'end_min'  ? val : currentRow.end_min,
      field === 'end_mode' ? val : currentRow.end_mode,
    )
    const diff = Math.max(0, endMin - startMin)
    setPeriods(prev => prev.map(r => r._id === id
      ? { ...updated, duration: String(diff).padStart(2, '0') }
      : r
    ))
  }, [])

  // ── Save configuration ──────────────────────────────────────────────────────
  const handleSave = useCallback(() => {
    const err = {}
    if (!session)     err.session     = 'Please select a session'
    if (!configTitle.trim()) err.configTitle = 'Please enter a configuration title'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setSaving(true)

    setTimeout(() => {
      const newConfig = {
        tt_uid: Date.now(),
        tt_config: configTitle.trim(),
        session,
        status: 'Active',
        periods: periods.map((r, i) => ({
          lecture_id: i + 1,
          lecture_name: r.lecture_name || `Period ${i + 1}`,
          st_hr: r.st_hr, st_min: r.st_min, st_mode: r.st_mode,
          duration: r.duration,
          end_hr: r.end_hr, end_min: r.end_min, end_mode: r.end_mode,
          break_time: r.break_time,
          period_time: formatPeriodTime(r),
        }))
      }
      setSavedConfigs(prev => [newConfig, ...prev])
      setSaving(false)
      showToast(`Configuration "${configTitle}" saved successfully!`)
      handleReset()
    }, 800)
  }, [session, configTitle, periods, showToast])

  const handleReset = () => {
    setSession(''); setConfigTitle(''); setPeriods([blankRow()]); setErrors({})
  }

  // ── Edit saved config ───────────────────────────────────────────────────────
  const handleEdit = useCallback((config) => {
    setEditingId(config.tt_uid)
    setEditTitle(config.tt_config)
  }, [])

  const handleUpdate = useCallback((uid) => {
    if (!editTitle.trim()) { showToast('Title cannot be empty', 'error'); return }
    setSavedConfigs(prev => prev.map(c => c.tt_uid === uid ? { ...c, tt_config: editTitle.trim() } : c))
    setEditingId(null); setEditTitle('')
    showToast('Configuration updated!')
  }, [editTitle, showToast])

  const handleCancelEdit = () => { setEditingId(null); setEditTitle('') }

  // ── Filtered saved configs ──────────────────────────────────────────────────
  const filteredConfigs = useMemo(() => {
    if (!savedSearch) return savedConfigs
    const q = savedSearch.toLowerCase()
    return savedConfigs.filter(c =>
      c.tt_config.toLowerCase().includes(q) || c.session.includes(q)
    )
  }, [savedConfigs, savedSearch])

  const activeFilterCount = [session, configTitle].filter(Boolean).length

  return (
    <div className="space-y-5 pb-10">

      {/* ── Page Title ───────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <AlarmClock className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Timetable Configuration
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Configure lecture periods, start/end times, durations and breaks per session.
          </p>
        </div>
      </div>

      {/* ══════════════════════════════════════════════════════════════════════
          SECTION 1 — ADD CONFIGURATION FORM
      ══════════════════════════════════════════════════════════════════════ */}

      {/* ── DESKTOP: Session + Title filter bar ──────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Settings2 className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">New Configuration</span>
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
            <div className="lg:col-span-2">
              <Field label="Configuration Title" error={errors.configTitle} required>
                <input
                  type="text"
                  value={configTitle}
                  onChange={e => { setConfigTitle(e.target.value); setErrors(p => ({ ...p, configTitle: undefined })) }}
                  placeholder="e.g. Morning Shift Configuration"
                  className={`w-full pl-3 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                    bg-white text-slate-800 placeholder-slate-300
                    focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400
                    ${errors.configTitle ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
                />
              </Field>
            </div>
            <div />
          </div>
        </div>
      </div>

      {/* ── MOBILE: filter bar ───────────────────────────────────────────── */}
      <div className="flex sm:hidden gap-2">
        <button
          type="button"
          onClick={() => setFilterOpen(true)}
          className="flex-1 flex items-center gap-2 py-2.5 px-4 rounded-xl text-[13px] font-semibold
            bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20"
        >
          <SlidersHorizontal className="w-4 h-4 flex-shrink-0" />
          <span className="flex-1 text-left truncate">
            {configTitle || session
              ? `${session || '—'} · ${configTitle || 'No title'}`
              : 'Set Session & Title'}
          </span>
          {activeFilterCount > 0 && (
            <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilterCount}</span>
          )}
        </button>
      </div>

      <FilterDrawer
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        session={session}
        setSession={setSession}
        configTitle={configTitle}
        setConfigTitle={setConfigTitle}
        errors={errors}
      />

      {/* ── PERIODS TABLE (Desktop) ───────────────────────────────────────── */}
      <div className="hidden md:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-indigo-500 flex-shrink-0" />
          <BookOpen className="w-4 h-4 text-indigo-600 dark:text-indigo-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Period Schedule</span>
          <span className="text-[11px] px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
            {periods.length} period{periods.length !== 1 ? 's' : ''}
          </span>
        </div>

        {/* Info banner */}
        <div className="flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
          <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
          <p className="text-[12px] text-blue-700 dark:text-blue-400">
            End Time is auto-calculated from Start Time + Duration. You can also edit End Time directly to recalculate Duration.
          </p>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                {['Period', 'Period Name', 'Start Time', 'Duration (min)', 'End Time', 'Break After', ''].map((h, i) => (
                  <th key={i} className={`px-4 py-2.5 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap
                    ${i === 0 ? 'text-center w-16' : i === 6 ? 'text-center w-12' : 'text-center'}`}>
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {periods.map((row, idx) => {
                const { fg, bg } = periodColor(idx)
                return (
                  <tr key={row._id} className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors group">
                    {/* Period # */}
                    <td className="px-4 py-3 text-center">
                      <span className="w-8 h-8 rounded-xl inline-flex items-center justify-center text-[11px] font-bold" style={{ background: bg, color: fg }}>
                        P{idx + 1}
                      </span>
                    </td>

                    {/* Period Name */}
                    <td className="px-4 py-3">
                      <input
                        type="text"
                        value={row.lecture_name}
                        onChange={e => setPeriods(prev => prev.map(r => r._id === row._id ? { ...r, lecture_name: e.target.value } : r))}
                        className="w-full min-w-[130px] pl-3 pr-3 py-1.5 text-[13px] rounded-lg border outline-none transition-all
                          bg-white text-slate-800 border-slate-200 placeholder-slate-300
                          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                          dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] dark:focus:border-indigo-400"
                        placeholder="Period name"
                        list={`period-names-${row._id}`}
                      />
                      <datalist id={`period-names-${row._id}`}>
                        {PERIOD_NAMES.map(n => <option key={n} value={n} />)}
                      </datalist>
                    </td>

                    {/* Start Time */}
                    <td className="px-4 py-3 text-center">
                      <TimePicker
                        hr={row.st_hr} min={row.st_min} mode={row.st_mode}
                        onHr={v => updateStartAndRecalc(row._id, 'st_hr', v, row)}
                        onMin={v => updateStartAndRecalc(row._id, 'st_min', v, row)}
                        onMode={v => updateStartAndRecalc(row._id, 'st_mode', v, row)}
                      />
                    </td>

                    {/* Duration */}
                    <td className="px-4 py-3 text-center">
                      <div className="relative w-24 mx-auto">
                        <select
                          value={row.duration}
                          onChange={e => updateDurationAndRecalc(row._id, e.target.value, row)}
                          className="w-full appearance-none pl-3 pr-7 py-1.5 text-[13px] rounded-lg border outline-none transition-all
                            bg-white text-slate-700 border-slate-200 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                            dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] cursor-pointer"
                        >
                          {Array.from({ length: 120 }, (_, i) => String(i + 1).padStart(2, '0')).map(d => (
                            <option key={d} value={d}>{d}</option>
                          ))}
                        </select>
                        <ChevronDown className="absolute right-2 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
                      </div>
                    </td>

                    {/* End Time */}
                    <td className="px-4 py-3 text-center">
                      <TimePicker
                        hr={row.end_hr} min={row.end_min} mode={row.end_mode}
                        onHr={v => updateEndAndRecalc(row._id, 'end_hr', v, row)}
                        onMin={v => updateEndAndRecalc(row._id, 'end_min', v, row)}
                        onMode={v => updateEndAndRecalc(row._id, 'end_mode', v, row)}
                      />
                    </td>

                    {/* Break Time */}
                    <td className="px-4 py-3 text-center">
                      <div className="relative w-20 mx-auto">
                        <select
                          value={row.break_time}
                          onChange={e => setPeriods(prev => prev.map(r => r._id === row._id ? { ...r, break_time: e.target.value } : r))}
                          className="w-full appearance-none pl-3 pr-7 py-1.5 text-[13px] rounded-lg border outline-none transition-all
                            bg-white text-slate-700 border-slate-200 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                            dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)] cursor-pointer"
                        >
                          {BREAK_OPTIONS.map(b => <option key={b} value={b}>{b}</option>)}
                        </select>
                        <ChevronDown className="absolute right-2 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
                      </div>
                    </td>

                    {/* Delete */}
                    <td className="px-4 py-3 text-center">
                      <button
                        onClick={() => deleteRow(row._id)}
                        disabled={periods.length === 1}
                        className="p-1.5 rounded-lg opacity-0 group-hover:opacity-100 hover:bg-rose-50 dark:hover:bg-rose-500/10 text-slate-300 hover:text-rose-500 dark:hover:text-rose-400 transition-all disabled:opacity-0 disabled:pointer-events-none"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
            <tfoot>
              <tr className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
                <td colSpan={7} className="px-4 py-3">
                  <button
                    type="button"
                    onClick={addRow}
                    className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
                      text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-500/10
                      border border-dashed border-blue-200 dark:border-blue-500/25 transition-colors"
                  >
                    <Plus className="w-4 h-4" /> Add More Period
                  </button>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>

        {/* Save / Reset footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            {periods.length} period{periods.length !== 1 ? 's' : ''} configured
          </p>
          <div className="flex items-center gap-2">
            <button type="button" onClick={handleReset}
              className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors">
              <RefreshCw className="w-3.5 h-3.5" /> Reset
            </button>
            <button type="button" onClick={handleSave} disabled={saving}
              className="flex items-center gap-2 px-5 py-2 rounded-xl text-[13px] font-semibold text-white
                bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20
                dark:bg-indigo-600 dark:hover:bg-indigo-700 transition-all active:scale-95 disabled:opacity-70">
              {saving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />}
              Save Configuration
            </button>
          </div>
        </div>
      </div>

      {/* ── PERIODS MOBILE CARDS ─────────────────────────────────────────── */}
      <div className="md:hidden space-y-3">
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
          <div className="flex items-center gap-3 px-4 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
            <span className="w-1 h-5 rounded-full bg-indigo-500 flex-shrink-0" />
            <BookOpen className="w-4 h-4 text-indigo-600 dark:text-indigo-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Period Schedule</span>
            <span className="text-[11px] px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
              {periods.length}
            </span>
          </div>
          <div className="p-4 space-y-3">
            {/* Mobile session/title summary */}
            {(session || configTitle) && (
              <div className="flex gap-2 flex-wrap">
                {session && <span className="text-[11px] px-2.5 py-1 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 font-semibold">{session}</span>}
                {configTitle && <span className="text-[11px] px-2.5 py-1 rounded-full bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300 font-semibold">{configTitle}</span>}
              </div>
            )}

            {periods.map((row, idx) => (
              <MobilePeriodCard
                key={row._id}
                row={row}
                idx={idx}
                onChange={updated => updateRow(row._id, updated)}
                onDelete={() => deleteRow(row._id)}
                canDelete={periods.length > 1}
              />
            ))}

            {/* Add More */}
            <button
              type="button"
              onClick={addRow}
              className="w-full flex items-center justify-center gap-2 py-3 rounded-xl text-[13px] font-semibold
                text-blue-600 dark:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-500/10
                border border-dashed border-blue-200 dark:border-blue-500/25 transition-colors"
            >
              <Plus className="w-4 h-4" /> Add More Period
            </button>
          </div>

          {/* Save / Reset mobile */}
          <div className="px-4 pb-4 flex gap-3">
            <button type="button" onClick={handleReset}
              className="flex-none flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl text-[13px] font-semibold
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
              <RefreshCw className="w-4 h-4" />
            </button>
            <button type="button" onClick={handleSave} disabled={saving}
              className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white
                bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20
                dark:bg-indigo-600 dark:hover:bg-indigo-700 transition-all active:scale-95 disabled:opacity-70">
              {saving ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />}
              Save Configuration
            </button>
          </div>
        </div>
      </div>

      {/* ══════════════════════════════════════════════════════════════════════
          SECTION 2 — SAVED CONFIGURATIONS LIST
      ══════════════════════════════════════════════════════════════════════ */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0">
            <span className="w-1 h-5 rounded-full bg-emerald-500 flex-shrink-0" />
            <CheckCircle2 className="w-4 h-4 text-emerald-600 dark:text-emerald-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Saved Configurations</span>
            <span className="text-[11px] px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-400 flex-shrink-0">
              {filteredConfigs.length} total
            </span>
          </div>
          {/* Search */}
          <div className="relative w-full sm:w-52 flex-shrink-0">
            <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
            <input
              value={savedSearch}
              onChange={e => setSavedSearch(e.target.value)}
              placeholder="Search configs…"
              className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                bg-white text-slate-700 border-slate-200 placeholder-slate-300
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                dark:placeholder-slate-600 dark:focus:border-indigo-400"
            />
            {savedSearch && (
              <button onClick={() => setSavedSearch('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                <X className="w-3.5 h-3.5" />
              </button>
            )}
          </div>
        </div>

        {/* DESKTOP TABLE */}
        <div className="hidden md:block overflow-x-auto">
          {filteredConfigs.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
              <ClipboardList className="w-6 h-6 opacity-40" />
              <span className="text-[13px]">No saved configurations found.</span>
            </div>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                  {['S.No.', 'Configuration Title', 'Status', 'Actions'].map((h, i) => (
                    <th key={i} className={`px-4 py-2.5 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400
                      ${i === 0 ? 'text-center w-14' : i === 2 || i === 3 ? 'text-center' : 'text-left'}`}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filteredConfigs.map((config, idx) => (
                  <SavedConfigRow
                    key={config.tt_uid}
                    config={config}
                    idx={idx}
                    onEdit={handleEdit}
                    onUpdate={handleUpdate}
                    onCancel={handleCancelEdit}
                    editingId={editingId}
                    editTitle={editTitle}
                    setEditTitle={setEditTitle}
                  />
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* MOBILE CARDS */}
        <div className="md:hidden p-4 space-y-3">
          {filteredConfigs.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
              <ClipboardList className="w-6 h-6 opacity-40" />
              <span className="text-[13px]">No saved configurations found.</span>
            </div>
          ) : (
            <>
              <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5">
                <Info className="w-3.5 h-3.5 flex-shrink-0" />
                Tap a card to expand period details. Tap edit icon to rename.
              </p>
              {filteredConfigs.map((config, idx) => (
                <SavedConfigCard
                  key={config.tt_uid}
                  config={config}
                  idx={idx}
                  onEdit={handleEdit}
                  onUpdate={handleUpdate}
                  onCancel={handleCancelEdit}
                  editingId={editingId}
                  editTitle={editTitle}
                  setEditTitle={setEditTitle}
                />
              ))}
            </>
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filteredConfigs.length}</span> of{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{savedConfigs.length}</span> configurations
          </p>
          {savedSearch && (
            <button onClick={() => setSavedSearch('')}
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
