/**
 * RFID_CampusStudentAttendance.jsx
 * Folder: src/pages/Student/Attendance/RFID_CampusStudentAttendance.jsx
 *
 * Converts legacy ASPX "RFID Attendance Report" to fully-responsive React + Tailwind.
 *
 * Filters: Session, Class, Date (calendar picker)
 * Features:
 *  - Session & Class dropdowns, date picker filter
 *  - Show report button
 *  - School name/address header in report
 *  - Summary stat cards
 *  - Present / Absent / Late badge counts
 *  - Desktop: ERP-style dense table with sticky header
 *  - Mobile: expandable student cards, no horizontal scroll
 *  - Toast notifications
 *  - Filter drawer on mobile
 */

import { useState, useMemo, useCallback, useRef, useEffect } from 'react'
import {
  Filter, RefreshCw, Eye, AlertCircle, X, Check, Loader2,
  ChevronDown, ChevronRight, SlidersHorizontal, Search, Info,
  Building2, MapPin, CalendarDays, Users, UserCheck, UserX,
  Clock, Tag, BookOpen, School2, Wifi, BarChart3, CheckCircle2,
  XCircle, AlarmClock, Fingerprint, ShieldCheck, TrendingUp,
  ChevronUp
} from 'lucide-react'

// ─── STATIC DATA ──────────────────────────────────────────────────────────────

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

// Status options
const STATUS = { PRESENT: 'Present', ABSENT: 'Absent', LATE: 'Late' }

// Generate dummy attendance rows per class
function generateAttendance(cls, date) {
  const seed = cls.charCodeAt(0) + (date ? date.length : 5)
  const students = [
    { roll: '01', name: 'Aarav Sharma',     rfid: 'RF-001A' },
    { roll: '02', name: 'Priya Singh',      rfid: 'RF-002B' },
    { roll: '03', name: 'Rohan Verma',      rfid: 'RF-003C' },
    { roll: '04', name: 'Sneha Gupta',      rfid: 'RF-004D' },
    { roll: '05', name: 'Arjun Patel',      rfid: 'RF-005E' },
    { roll: '06', name: 'Kavya Joshi',      rfid: 'RF-006F' },
    { roll: '07', name: 'Vikram Nair',      rfid: 'RF-007G' },
    { roll: '08', name: 'Riya Mehta',       rfid: 'RF-008H' },
    { roll: '09', name: 'Aditya Kumar',     rfid: 'RF-009I' },
    { roll: '10', name: 'Pooja Yadav',      rfid: 'RF-010J' },
    { roll: '11', name: 'Rahul Mishra',     rfid: 'RF-011K' },
    { roll: '12', name: 'Anjali Tiwari',    rfid: 'RF-012L' },
    { roll: '13', name: 'Siddharth Rao',   rfid: 'RF-013M' },
    { roll: '14', name: 'Neha Pandey',      rfid: 'RF-014N' },
    { roll: '15', name: 'Harsh Agarwal',    rfid: 'RF-015O' },
    { roll: '16', name: 'Divya Saxena',     rfid: 'RF-016P' },
    { roll: '17', name: 'Manish Dubey',     rfid: 'RF-017Q' },
    { roll: '18', name: 'Swati Tripathi',   rfid: 'RF-018R' },
    { roll: '19', name: 'Kunal Bhatt',      rfid: 'RF-019S' },
    { roll: '20', name: 'Pallavi Choudhary',rfid: 'RF-020T' },
    { roll: '21', name: 'Nikhil Srivastava',rfid: 'RF-021U' },
    { roll: '22', name: 'Isha Kulkarni',    rfid: 'RF-022V' },
    { roll: '23', name: 'Ankit Bansal',     rfid: 'RF-023W' },
    { roll: '24', name: 'Megha Chauhan',    rfid: 'RF-024X' },
    { roll: '25', name: 'Tushar Ghosh',     rfid: 'RF-025Y' },
  ]

  const times = ['07:45 AM','07:52 AM','07:58 AM','08:03 AM','08:07 AM','08:15 AM','08:22 AM','08:30 AM','08:38 AM','08:45 AM']

  return students.map((s, i) => {
    const r = (seed + i * 7) % 10
    let status = STATUS.PRESENT
    if (r === 4 || r === 8) status = STATUS.ABSENT
    else if (r === 3 || r === 7) status = STATUS.LATE
    return {
      ...s,
      section: 'A',
      class: cls,
      status,
      scanTime: status === STATUS.ABSENT ? '—' : times[(seed + i) % times.length],
    }
  })
}

// ─── HELPERS ──────────────────────────────────────────────────────────────────

const STATUS_CONFIG = {
  [STATUS.PRESENT]: {
    label: 'Present',
    icon: CheckCircle2,
    row: 'bg-emerald-50/60 dark:bg-emerald-500/[0.04]',
    badge: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/20 dark:text-emerald-300',
    dot:   'bg-emerald-500',
    card:  'border-l-4 border-l-emerald-400',
    stat:  'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400',
  },
  [STATUS.ABSENT]: {
    label: 'Absent',
    icon: XCircle,
    row: 'bg-rose-50/60 dark:bg-rose-500/[0.04]',
    badge: 'bg-rose-100 text-rose-700 dark:bg-rose-500/20 dark:text-rose-300',
    dot:   'bg-rose-500',
    card:  'border-l-4 border-l-rose-400',
    stat:  'bg-rose-50 text-rose-600 dark:bg-rose-500/10 dark:text-rose-400',
  },
  [STATUS.LATE]: {
    label: 'Late',
    icon: AlarmClock,
    row: 'bg-amber-50/60 dark:bg-amber-500/[0.04]',
    badge: 'bg-amber-100 text-amber-700 dark:bg-amber-500/20 dark:text-amber-300',
    dot:   'bg-amber-500',
    card:  'border-l-4 border-l-amber-400',
    stat:  'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400',
  },
}

// Format today's date as "DD Mon YYYY"
function todayFormatted() {
  const d = new Date()
  return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
}

// Avatar initials
function initials(name = '') {
  return name.split(' ').map(w => w[0]).slice(0, 2).join('').toUpperCase()
}

// Avatar color based on name
const AVATAR_COLORS = [
  ['#1d4ed8','#dbeafe'],['#7c3aed','#ede9fe'],['#0891b2','#cffafe'],
  ['#059669','#d1fae5'],['#d97706','#fef3c7'],['#dc2626','#fee2e2'],
  ['#0369a1','#e0f2fe'],
]
function avatarColor(name = '') {
  return AVATAR_COLORS[(name.charCodeAt(0) ?? 0) % AVATAR_COLORS.length]
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
            : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
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
      <label className="text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">
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

function SummaryCard({ icon: Icon, label, value, color, pct }) {
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
      <div className="min-w-0 flex-1">
        <p className="text-[20px] font-bold text-slate-800 dark:text-slate-100 tabular-nums leading-tight">{value}</p>
        <p className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{label}</p>
      </div>
      {pct !== undefined && (
        <span className={`text-[11px] font-bold px-2 py-0.5 rounded-full flex-shrink-0 ${colors[color]}`}>{pct}%</span>
      )}
    </div>
  )
}

// ─── SCHOOL HEADER BANNER ─────────────────────────────────────────────────────

function SchoolHeader({ session, cls, date }) {
  return (
    <div className="rounded-2xl border border-blue-100 dark:border-[rgba(99,102,241,0.2)] bg-gradient-to-r from-blue-50 via-white to-indigo-50 dark:from-[#1a1f35] dark:via-[#1e2238] dark:to-[#1a1f35] px-6 py-5 text-center shadow-sm">
      <div className="flex items-center justify-center gap-2 mb-1">
        <Building2 className="w-5 h-5 text-blue-600 dark:text-blue-400 flex-shrink-0" />
        <h2 className="text-[15px] sm:text-[18px] font-extrabold text-slate-800 dark:text-slate-100 tracking-tight leading-snug">
          {SCHOOL_INFO.name}
        </h2>
      </div>
      <div className="flex items-center justify-center gap-1.5 text-[12px] text-slate-500 dark:text-slate-400 mb-3">
        <MapPin className="w-3.5 h-3.5 flex-shrink-0" />
        <span>{SCHOOL_INFO.address}</span>
      </div>
      <div className="flex flex-wrap items-center justify-center gap-2">
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-100 dark:bg-amber-500/15 border border-amber-200 dark:border-amber-500/25">
          <Tag className="w-3 h-3 text-amber-600 dark:text-amber-400" />
          <span className="text-[11px] font-bold text-amber-700 dark:text-amber-400">Session: {session}</span>
        </span>
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-blue-100 dark:bg-blue-500/15 border border-blue-200 dark:border-blue-500/25">
          <BookOpen className="w-3 h-3 text-blue-600 dark:text-blue-400" />
          <span className="text-[11px] font-bold text-blue-700 dark:text-blue-400">Class: {cls}</span>
        </span>
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-indigo-100 dark:bg-indigo-500/15 border border-indigo-200 dark:border-indigo-500/25">
          <CalendarDays className="w-3 h-3 text-indigo-600 dark:text-indigo-400" />
          <span className="text-[11px] font-bold text-indigo-700 dark:text-indigo-400">Date: {date}</span>
        </span>
      </div>
      <p className="mt-3 text-[12px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400 flex items-center justify-center gap-2">
        <Wifi className="w-3.5 h-3.5" />
        RFID Attendance Report
      </p>
    </div>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────

function DesktopRow({ row, idx }) {
  const cfg = STATUS_CONFIG[row.status]
  const StatusIcon = cfg.icon
  const [fg, bg] = avatarColor(row.name)

  return (
    <tr className={`border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/80 dark:hover:bg-white/[0.02] transition-colors ${cfg.row}`}>
      {/* S.No */}
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{idx}</td>

      {/* Student */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-2.5">
          <span
            className="w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center text-[11px] font-bold"
            style={{ background: bg, color: fg }}
          >
            {initials(row.name)}
          </span>
          <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{row.name}</span>
        </div>
      </td>

      {/* Roll No */}
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center justify-center w-8 h-7 rounded-lg text-[12px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300">
          {row.roll}
        </span>
      </td>

      {/* Class */}
      <td className="px-4 py-3 text-center">
        <span className="text-[12px] font-semibold text-slate-600 dark:text-slate-300 whitespace-nowrap">{row.class}</span>
      </td>

      {/* Section */}
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center justify-center w-7 h-7 rounded-lg text-[12px] font-bold bg-blue-50 dark:bg-blue-500/10 text-blue-600 dark:text-blue-400">
          {row.section}
        </span>
      </td>

      {/* RFID Tag */}
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-semibold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300">
          <Fingerprint className="w-3 h-3 text-slate-400" />
          {row.rfid}
        </span>
      </td>

      {/* Scan Time */}
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center gap-1 text-[12px] font-semibold text-slate-600 dark:text-slate-300 tabular-nums">
          {row.scanTime !== '—' && <Clock className="w-3 h-3 text-slate-400 flex-shrink-0" />}
          {row.scanTime}
        </span>
      </td>

      {/* Status */}
      <td className="px-4 py-3 text-center">
        <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-[12px] font-bold ${cfg.badge}`}>
          <StatusIcon className="w-3.5 h-3.5 flex-shrink-0" />
          {row.status}
        </span>
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────

function MobileCard({ row, idx }) {
  const [expanded, setExpanded] = useState(false)
  const cfg = STATUS_CONFIG[row.status]
  const StatusIcon = cfg.icon
  const [fg, bg] = avatarColor(row.name)

  return (
    <div className={`rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm ${cfg.card}`}>
      {/* Always-visible header */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        {/* Avatar */}
        <span
          className="w-9 h-9 rounded-xl flex-shrink-0 flex items-center justify-center text-[11px] font-bold"
          style={{ background: bg, color: fg }}
        >
          {initials(row.name)}
        </span>

        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{row.name}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">
            Roll: <span className="font-semibold text-slate-600 dark:text-slate-300">{row.roll}</span>
            &nbsp;·&nbsp;Sec: <span className="font-semibold text-slate-600 dark:text-slate-300">{row.section}</span>
            {row.scanTime !== '—' && (
              <>&nbsp;·&nbsp;<span className="font-semibold text-slate-600 dark:text-slate-300">{row.scanTime}</span></>
            )}
          </p>
        </div>

        {/* Status badge */}
        <span className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-bold flex-shrink-0 ${cfg.badge}`}>
          <StatusIcon className="w-3 h-3 flex-shrink-0" />
          {row.status}
        </span>

        <span className={`w-5 h-5 flex items-center justify-center ml-1 text-slate-400 transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Expanded detail */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 pt-3 pb-4">
          <div className="grid grid-cols-2 gap-2">
            <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 p-3">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-1">RFID Tag</p>
              <p className="text-[13px] font-bold text-slate-700 dark:text-slate-200 flex items-center gap-1.5">
                <Fingerprint className="w-3.5 h-3.5 text-blue-500" />{row.rfid}
              </p>
            </div>
            <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 p-3">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-1">Class</p>
              <p className="text-[13px] font-bold text-slate-700 dark:text-slate-200">{row.class}</p>
            </div>
            <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 p-3">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-1">Scan Time</p>
              <p className="text-[13px] font-bold text-slate-700 dark:text-slate-200 flex items-center gap-1.5">
                {row.scanTime !== '—' && <Clock className="w-3.5 h-3.5 text-slate-400" />}{row.scanTime}
              </p>
            </div>
            <div className={`rounded-xl p-3 ${cfg.badge.replace('text-', 'bg-').split('bg-')[1] ? '' : ''}`} style={{background: bg, opacity: 0.9}}>
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-500 mb-1">Status</p>
              <span className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-[12px] font-bold ${cfg.badge}`}>
                <StatusIcon className="w-3.5 h-3.5" />{row.status}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────

function FilterDrawer({ open, onClose, session, setSession, cls, setCls, date, setDate, onShow, loading, errors, setErrors }) {
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
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Filters</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5 space-y-4 max-h-[55vh] overflow-y-auto">
          <Field label="Session" error={errors.session} required>
            <NativeSelect value={session} onChange={e => { setSession(e.target.value); setErrors(p => ({ ...p, session: undefined })) }}
              placeholder="-- Select Session --" error={errors.session}>
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Class" error={errors.cls} required>
            <NativeSelect value={cls} onChange={e => { setCls(e.target.value); setErrors(p => ({ ...p, cls: undefined })) }}
              placeholder="-- Select Class --" error={errors.cls}>
              {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Date">
            <div className="relative">
              <CalendarDays className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                type="date"
                value={date}
                onChange={e => setDate(e.target.value)}
                className="w-full appearance-none pl-9 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                  bg-white text-slate-800 border-slate-200
                  focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                  dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]"
              />
            </div>
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

// ─── STATUS FILTER TABS ───────────────────────────────────────────────────────

function StatusTabs({ active, onChange, counts }) {
  const tabs = [
    { key: 'All',             label: 'All',     color: 'text-slate-600 dark:text-slate-300',   activeBg: 'bg-slate-800 dark:bg-slate-200', activeText: 'text-white dark:text-slate-900' },
    { key: STATUS.PRESENT,    label: 'Present', color: 'text-emerald-600 dark:text-emerald-400', activeBg: 'bg-emerald-600', activeText: 'text-white' },
    { key: STATUS.ABSENT,     label: 'Absent',  color: 'text-rose-600 dark:text-rose-400',       activeBg: 'bg-rose-600',    activeText: 'text-white' },
    { key: STATUS.LATE,       label: 'Late',    color: 'text-amber-600 dark:text-amber-400',     activeBg: 'bg-amber-500',   activeText: 'text-white' },
  ]
  return (
    <div className="flex gap-1.5 flex-wrap">
      {tabs.map(t => (
        <button
          key={t.key}
          type="button"
          onClick={() => onChange(t.key)}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold transition-all
            ${active === t.key
              ? `${t.activeBg} ${t.activeText} shadow-sm`
              : `bg-slate-100 dark:bg-slate-800 ${t.color} hover:bg-slate-200 dark:hover:bg-slate-700`}`}
        >
          {t.label}
          <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded-full
            ${active === t.key ? 'bg-white/25' : 'bg-slate-200 dark:bg-slate-700 text-slate-600 dark:text-slate-300'}`}>
            {counts[t.key] ?? counts.All}
          </span>
        </button>
      ))}
    </div>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────

export default function RFIDCampusStudentAttendance() {
  const [session,      setSession]      = useState('')
  const [cls,          setCls]          = useState('')
  const [date,         setDate]         = useState(() => new Date().toISOString().split('T')[0])
  const [rows,         setRows]         = useState([])
  const [loading,      setLoading]      = useState(false)
  const [filterOpen,   setFilterOpen]   = useState(false)
  const [search,       setSearch]       = useState('')
  const [statusFilter, setStatusFilter] = useState('All')
  const [errors,       setErrors]       = useState({})
  const [toast,        setToast]        = useState(null)
  const [shown,        setShown]        = useState(false)
  const [shownMeta,    setShownMeta]    = useState({ session: '', cls: '', date: '' })

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // Format date for display
  const displayDate = useMemo(() => {
    if (!date) return todayFormatted()
    const d = new Date(date)
    return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
  }, [date])

  // ── Show report ────────────────────────────────────────────────────────────
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
      const data = generateAttendance(cls, date)
      setRows(data)
      setShownMeta({ session, cls, date: displayDate })
      setShown(true)
      setLoading(false)
      showToast(`Loaded ${data.length} attendance records.`)
    }, 700)
  }, [session, cls, date, displayDate])

  const handleReset = () => {
    setSession(''); setCls(''); setRows([])
    setSearch(''); setStatusFilter('All')
    setErrors({}); setShown(false)
    setShownMeta({ session: '', cls: '', date: '' })
  }

  // ── Search + Status filter ─────────────────────────────────────────────────
  const filtered = useMemo(() => {
    let r = rows
    if (statusFilter !== 'All') r = r.filter(x => x.status === statusFilter)
    if (search) {
      const q = search.toLowerCase()
      r = r.filter(x =>
        x.name.toLowerCase().includes(q) ||
        x.roll.includes(q) ||
        x.rfid.toLowerCase().includes(q)
      )
    }
    return r
  }, [rows, statusFilter, search])

  // ── Counts ────────────────────────────────────────────────────────────────
  const counts = useMemo(() => ({
    All:              rows.length,
    [STATUS.PRESENT]: rows.filter(r => r.status === STATUS.PRESENT).length,
    [STATUS.ABSENT]:  rows.filter(r => r.status === STATUS.ABSENT).length,
    [STATUS.LATE]:    rows.filter(r => r.status === STATUS.LATE).length,
  }), [rows])

  const attendancePct = rows.length
    ? Math.round(((counts[STATUS.PRESENT] + counts[STATUS.LATE]) / rows.length) * 100)
    : 0

  const hasResults = shown && rows.length > 0
  const activeFilters = [session, cls].filter(Boolean).length

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ───────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Wifi className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            RFID Attendance Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Campus student RFID-based daily attendance tracking.
          </p>
        </div>
      </div>

      {/* ── DESKTOP Filter Card ──────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Filter className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Search Filters</span>
          {hasResults && (
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
              {rows.length} records loaded
            </span>
          )}
        </div>
        <div className="p-5">
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 items-end">
            {/* Session */}
            <Field label="Session" error={errors.session} required>
              <NativeSelect value={session} onChange={e => { setSession(e.target.value); setErrors(p => ({ ...p, session: undefined })) }}
                placeholder="-- Select Session --" error={errors.session}>
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>

            {/* Class */}
            <Field label="Class" error={errors.cls} required>
              <NativeSelect value={cls} onChange={e => { setCls(e.target.value); setErrors(p => ({ ...p, cls: undefined })) }}
                placeholder="-- Select Class --" error={errors.cls}>
                {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
              </NativeSelect>
            </Field>

            {/* Date */}
            <Field label="Enter Date">
              <div className="relative">
                <CalendarDays className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  type="date"
                  value={date}
                  onChange={e => setDate(e.target.value)}
                  className="w-full appearance-none pl-9 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                    bg-white text-slate-800 border-slate-200
                    focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                    dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20"
                />
              </div>
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
          {activeFilters > 0 ? `${cls || 'Class'} · ${session || 'Session'}` : 'Select Filters'}
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
        session={session} setSession={setSession}
        cls={cls} setCls={setCls}
        date={date} setDate={setDate}
        onShow={handleShow}
        loading={loading}
        errors={errors}
        setErrors={setErrors}
      />

      {/* ── Loading Skeleton ──────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          <div className="h-20 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-4" />
          {[...Array(6)].map((_, i) => (
            <div key={i} className="h-12 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.12 }} />
          ))}
        </div>
      )}

      {/* ── Results ───────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School Header */}
          <SchoolHeader session={shownMeta.session} cls={shownMeta.cls} date={shownMeta.date} />

          {/* Summary Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={Users}      label="Total Students"   value={counts.All}              color="blue"    />
            <SummaryCard icon={UserCheck}  label="Present"          value={counts[STATUS.PRESENT]}  color="emerald" pct={counts.All ? Math.round((counts[STATUS.PRESENT]/counts.All)*100) : 0} />
            <SummaryCard icon={UserX}      label="Absent"           value={counts[STATUS.ABSENT]}   color="rose"    pct={counts.All ? Math.round((counts[STATUS.ABSENT]/counts.All)*100) : 0} />
            <SummaryCard icon={AlarmClock} label="Late"             value={counts[STATUS.LATE]}     color="amber"   pct={counts.All ? Math.round((counts[STATUS.LATE]/counts.All)*100) : 0} />
          </div>

          {/* Attendance % bar */}
          <div className="rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-white dark:bg-[#1a1f35] px-5 py-3.5 shadow-sm">
            <div className="flex items-center justify-between mb-2">
              <span className="text-[12px] font-semibold text-slate-600 dark:text-slate-300 flex items-center gap-1.5">
                <TrendingUp className="w-3.5 h-3.5 text-blue-500" /> Overall Attendance
              </span>
              <span className={`text-[13px] font-bold ${attendancePct >= 75 ? 'text-emerald-600 dark:text-emerald-400' : 'text-rose-600 dark:text-rose-400'}`}>
                {attendancePct}%
              </span>
            </div>
            <div className="h-2 rounded-full bg-slate-100 dark:bg-slate-800 overflow-hidden">
              <div
                className={`h-full rounded-full transition-all duration-700 ${attendancePct >= 75 ? 'bg-emerald-500' : 'bg-rose-500'}`}
                style={{ width: `${attendancePct}%` }}
              />
            </div>
          </div>

          {/* Main Table Card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

            {/* Card Header */}
            <div className="flex flex-col gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <Fingerprint className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Attendance Records</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownMeta.cls} · {shownMeta.date}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                  {filtered.length} record{filtered.length !== 1 ? 's' : ''}
                </span>
              </div>

              {/* Status Tabs + Search row */}
              <div className="flex flex-col sm:flex-row gap-3 sm:items-center sm:justify-between">
                <StatusTabs active={statusFilter} onChange={setStatusFilter} counts={counts} />

                {/* Search */}
                <div className="relative w-full sm:w-56 flex-shrink-0">
                  <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                  <input
                    value={search}
                    onChange={e => setSearch(e.target.value)}
                    placeholder="Name, Roll No, RFID…"
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
            </div>

            {/* Info hint desktop */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
              <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <p className="text-[12px] text-blue-700 dark:text-blue-400">
                RFID scan data for {shownMeta.cls} · {shownMeta.session} · {shownMeta.date}. Late arrivals are flagged separately.
              </p>
            </div>

            {/* ── DESKTOP TABLE ── */}
            <div className="hidden md:block overflow-x-auto">
              {filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-28 gap-2 text-slate-400 dark:text-slate-600">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No records match your filter.</span>
                </div>
              ) : (
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                      {['S.No.', 'Student Name', 'Roll No.', 'Class', 'Section', 'RFID Tag', 'Scan Time', 'Status'].map((h, i) => (
                        <th key={i} className="px-4 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:w-12">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((row, i) => (
                      <DesktopRow key={`${row.roll}-${row.rfid}`} row={row} idx={i + 1} />
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
                  <span className="text-[13px]">No records match your filter.</span>
                </div>
              ) : (
                <>
                  <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5 pb-1">
                    <Info className="w-3.5 h-3.5 flex-shrink-0" />
                    Tap a card to see full details.
                  </p>
                  {filtered.map((row, i) => (
                    <MobileCard key={`${row.roll}-${row.rfid}`} row={row} idx={i + 1} />
                  ))}
                </>
              )}
            </div>

            {/* Table Footer */}
            <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
              <p className="text-[12px] text-slate-400 dark:text-slate-500">
                Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
                <span className="font-semibold text-slate-700 dark:text-slate-300">{rows.length}</span> records
              </p>
              {(search || statusFilter !== 'All') && (
                <button
                  onClick={() => { setSearch(''); setStatusFilter('All') }}
                  className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
                  <X className="w-3 h-3" /> Clear filters
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
            <Fingerprint className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No report generated yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select a <strong>Session</strong>, <strong>Class</strong> and <strong>Date</strong>, then click <strong>Show</strong>.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
