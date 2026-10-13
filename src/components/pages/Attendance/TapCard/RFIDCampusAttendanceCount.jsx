/**
 * RFIDCampusAttendanceCount.jsx
 * Folder: src/pages/Student/Reports/RFIDCampusAttendanceCount.jsx
 *
 * Converts legacy ASPX "RFID Campus Attendance Report" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session + Class dropdown filters + Date picker
 *  - Show report button
 *  - School name / session / date header in report
 *  - Attendance summary cards (Present, Absent, Late, Total)
 *  - Desktop: dense ERP-style table with RFID tag info
 *  - Mobile: collapsible cards with expandable details
 *  - Toast notifications
 *  - Loading skeleton
 *  - Reset / clear filters
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Filter, RefreshCw, Eye,
  AlertCircle, X, Check, Loader2, ChevronDown,
  ChevronRight, SlidersHorizontal, Info, Search,
  FileSpreadsheet, Building2, MapPin, TrendingUp,
  CalendarDays, Wifi, WifiOff, Clock, Users,
  UserCheck, UserX, ShieldAlert, Tag, School2,
  CheckCircle2, XCircle, Timer, Fingerprint
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ────────────────────────────────────────────────────
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

// Generate dummy RFID attendance data
const generateAttendance = (cls, date) => {
  const sections = ['A', 'B']
  const sectionCount = ['Class VI', 'Class VII', 'Class IX', 'Class XI', 'Nursery', 'LKG', 'UKG'].includes(cls) ? 2 : 1
  const rows = []
  let sno = 1

  const firstNames = ['Aarav', 'Priya', 'Rahul', 'Sneha', 'Vikram', 'Ananya', 'Rohan', 'Kavya',
    'Arjun', 'Divya', 'Karan', 'Pooja', 'Amit', 'Neha', 'Suresh', 'Meera',
    'Nikhil', 'Riya', 'Deepak', 'Simran', 'Harsh', 'Nidhi', 'Saurabh', 'Tanya']
  const lastNames = ['Sharma', 'Gupta', 'Singh', 'Verma', 'Kumar', 'Joshi', 'Patel', 'Tiwari',
    'Mishra', 'Yadav', 'Chauhan', 'Agarwal', 'Negi', 'Rawat', 'Bisht']

  const seed = (date?.replace(/\D/g, '') || '01012024').split('').reduce((a, c) => a + +c, 0)

  for (let s = 0; s < sectionCount; s++) {
    const section = sections[s]
    const count = 18 + (sno % 7)
    for (let i = 0; i < count; i++) {
      const nameIdx = (sno * 3 + s * 7 + i * 11 + seed) % firstNames.length
      const lastIdx = (sno * 7 + s * 3 + i * 5 + seed) % lastNames.length
      const rollNo = `${cls.replace('Class ', '').replace(' ', '').toUpperCase().slice(0, 3)}${section}${String(i + 1).padStart(3, '0')}`
      const rfidTag = `RFID${String((sno * 1000 + i * 37 + seed * 13) % 99999).padStart(5, '0')}`
      const statusRand = (sno + i + seed) % 10
      const status = statusRand < 7 ? 'Present' : statusRand < 9 ? 'Absent' : 'Late'
      const hour = status === 'Present' ? 7 + (i % 2) : status === 'Late' ? 9 + (i % 2) : null
      const min = status !== 'Absent' ? ['00', '05', '10', '15', '20', '30', '45'][i % 7] : null
      const scanTime = status !== 'Absent' ? `${String(hour).padStart(2, '0')}:${min} AM` : '—'

      rows.push({
        sno: sno++,
        rollNo,
        name: `${firstNames[nameIdx]} ${lastNames[lastIdx]}`,
        class: cls,
        section,
        rfidTag,
        scanTime,
        status,
        gate: status !== 'Absent' ? `Gate ${(i % 3) + 1}` : '—',
      })
    }
  }
  return rows
}

// ─── HELPERS ─────────────────────────────────────────────────────────────────
const STATUS_CONFIG = {
  Present: {
    bg: 'bg-emerald-50 dark:bg-emerald-500/10',
    text: 'text-emerald-700 dark:text-emerald-400',
    border: 'border-emerald-200 dark:border-emerald-500/20',
    dot: 'bg-emerald-500',
    icon: CheckCircle2,
  },
  Absent: {
    bg: 'bg-rose-50 dark:bg-rose-500/10',
    text: 'text-rose-700 dark:text-rose-400',
    border: 'border-rose-200 dark:border-rose-500/20',
    dot: 'bg-rose-500',
    icon: XCircle,
  },
  Late: {
    bg: 'bg-amber-50 dark:bg-amber-500/10',
    text: 'text-amber-700 dark:text-amber-400',
    border: 'border-amber-200 dark:border-amber-500/20',
    dot: 'bg-amber-500',
    icon: Timer,
  },
}

const CLASS_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' },
  { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' },
  { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' },
  { fg: '#dc2626', bg: '#fee2e2' },
  { fg: '#0369a1', bg: '#e0f2fe' },
]
const classColor = (name) => CLASS_COLORS[(name?.charCodeAt(0) ?? 0) % CLASS_COLORS.length]
const formatAbbr = (name = '') => name.replace('Class ', '').slice(0, 3).toUpperCase()

// Format today's date
const todayStr = () => {
  const d = new Date()
  return `${String(d.getDate()).padStart(2, '0')} ${d.toLocaleString('en-IN', { month: 'short' })} ${d.getFullYear()}`
}

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

// ─── SUMMARY STAT CARD ───────────────────────────────────────────────────────
function SummaryCard({ icon: Icon, label, value, color, sublabel }) {
  const colors = {
    blue:    'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400',
    emerald: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400',
    rose:    'bg-rose-50 text-rose-600 dark:bg-rose-500/10 dark:text-rose-400',
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
        <p className="text-[20px] font-bold text-slate-800 dark:text-slate-100 tabular-nums leading-tight">{value.toLocaleString()}</p>
        <p className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{label}</p>
        {sublabel && <p className="text-[10px] text-slate-400 dark:text-slate-500">{sublabel}</p>}
      </div>
    </div>
  )
}

// ─── SCHOOL HEADER BANNER ────────────────────────────────────────────────────
function SchoolHeader({ session, cls, date }) {
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
        <div className="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-amber-100 dark:bg-amber-500/15 border border-amber-200 dark:border-amber-500/25">
          <span className="text-[12px] font-bold text-amber-700 dark:text-amber-400">Session: {session}</span>
        </div>
        <div className="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-blue-100 dark:bg-blue-500/15 border border-blue-200 dark:border-blue-500/25">
          <span className="text-[12px] font-bold text-blue-700 dark:text-blue-400">{cls}</span>
        </div>
        <div className="inline-flex items-center gap-1.5 px-4 py-1 rounded-full bg-emerald-100 dark:bg-emerald-500/15 border border-emerald-200 dark:border-emerald-500/25">
          <CalendarDays className="w-3.5 h-3.5 text-emerald-600 dark:text-emerald-400" />
          <span className="text-[12px] font-bold text-emerald-700 dark:text-emerald-400">{date}</span>
        </div>
      </div>
      <p className="mt-2.5 text-[13px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400 flex items-center justify-center gap-2">
        <Wifi className="w-4 h-4" />
        RFID Campus Attendance Report
      </p>
    </div>
  )
}

// ─── STATUS BADGE ─────────────────────────────────────────────────────────────
function StatusBadge({ status }) {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG.Present
  const Icon = cfg.icon
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-bold border ${cfg.bg} ${cfg.text} ${cfg.border}`}>
      <Icon className="w-3.5 h-3.5 flex-shrink-0" />
      {status}
    </span>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ row, isTotal }) {
  if (isTotal) {
    return (
      <tr className="bg-blue-50 dark:bg-indigo-500/[0.07] border-t-2 border-blue-200 dark:border-indigo-500/30">
        <td className="px-3 py-3 text-center text-[12px] text-blue-400">—</td>
        <td className="px-3 py-3 text-center text-[12px] text-blue-400">—</td>
        <td className="px-3 py-3" colSpan={2}>
          <span className="text-[13px] font-bold text-blue-700 dark:text-blue-300 flex items-center gap-2">
            <TrendingUp className="w-4 h-4" /> Grand Total
          </span>
        </td>
        <td className="px-3 py-3 text-center text-[12px] text-slate-400">—</td>
        <td className="px-3 py-3 text-center text-[12px] text-slate-400">—</td>
        <td className="px-3 py-3 text-center text-[12px] text-slate-400">—</td>
        <td className="px-3 py-3 text-center">
          <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-blue-100 text-blue-800 dark:bg-blue-500/20 dark:text-blue-300 tabular-nums">{row.Total}</span>
        </td>
        <td className="px-3 py-3 text-center">
          <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-emerald-100 text-emerald-800 dark:bg-emerald-500/20 dark:text-emerald-300 tabular-nums">{row.Present}</span>
        </td>
        <td className="px-3 py-3 text-center">
          <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-rose-100 text-rose-800 dark:bg-rose-500/20 dark:text-rose-300 tabular-nums">{row.Absent}</span>
        </td>
        <td className="px-3 py-3 text-center">
          <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-500/20 dark:text-amber-300 tabular-nums">{row.Late}</span>
        </td>
      </tr>
    )
  }

  const { fg, bg } = classColor(row.class)

  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
      {/* S.No */}
      <td className="px-3 py-2.5 text-center text-[12px] text-slate-400 tabular-nums w-10">{row.sno}</td>

      {/* Roll No */}
      <td className="px-3 py-2.5 text-center">
        <span className="text-[12px] font-mono font-semibold text-slate-600 dark:text-slate-300">{row.rollNo}</span>
      </td>

      {/* Name */}
      <td className="px-3 py-2.5">
        <div className="flex items-center gap-2">
          <span
            className="w-7 h-7 rounded-lg flex-shrink-0 flex items-center justify-center text-[9px] font-bold"
            style={{ background: bg, color: fg }}
          >
            {row.name.split(' ').map(n => n[0]).join('').slice(0, 2)}
          </span>
          <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{row.name}</span>
        </div>
      </td>

      {/* Section */}
      <td className="px-3 py-2.5 text-center">
        <span className="inline-flex items-center justify-center w-7 h-7 rounded-lg text-[12px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300">
          {row.section}
        </span>
      </td>

      {/* RFID Tag */}
      <td className="px-3 py-2.5 text-center">
        <div className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md bg-cyan-50 dark:bg-cyan-500/10 border border-cyan-200 dark:border-cyan-500/20">
          <Tag className="w-3 h-3 text-cyan-600 dark:text-cyan-400" />
          <span className="text-[11px] font-mono font-semibold text-cyan-700 dark:text-cyan-400">{row.rfidTag}</span>
        </div>
      </td>

      {/* Scan Time */}
      <td className="px-3 py-2.5 text-center">
        <span className="text-[12px] font-mono text-slate-600 dark:text-slate-400">{row.scanTime}</span>
      </td>

      {/* Gate */}
      <td className="px-3 py-2.5 text-center">
        <span className="text-[12px] font-medium text-slate-500 dark:text-slate-400">{row.gate}</span>
      </td>

      {/* Placeholder total col */}
      <td className="px-3 py-2.5 text-center text-[12px] text-slate-300">—</td>
      <td className="px-3 py-2.5 text-center text-[12px] text-slate-300">—</td>
      <td className="px-3 py-2.5 text-center text-[12px] text-slate-300">—</td>

      {/* Status */}
      <td className="px-3 py-2.5 text-center">
        <StatusBadge status={row.status} />
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────
function MobileCard({ row }) {
  const [expanded, setExpanded] = useState(false)
  const { fg, bg } = classColor(row.class)
  const cfg = STATUS_CONFIG[row.status] || STATUS_CONFIG.Present

  return (
    <div className={`rounded-xl border ${cfg.border} bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm`}>
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        {/* Avatar */}
        <span
          className="w-9 h-9 rounded-xl flex-shrink-0 flex items-center justify-center text-[10px] font-bold"
          style={{ background: bg, color: fg }}
        >
          {row.name.split(' ').map(n => n[0]).join('').slice(0, 2)}
        </span>

        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{row.name}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">
            <span className="font-mono">{row.rollNo}</span>
            &nbsp;·&nbsp;Sec {row.section}
            &nbsp;·&nbsp;
            <span className="inline-flex items-center gap-0.5">
              <Tag className="w-2.5 h-2.5" />{row.rfidTag}
            </span>
          </p>
        </div>

        <div className="flex flex-col items-end flex-shrink-0 gap-1">
          <StatusBadge status={row.status} />
          <span className="text-[10px] text-slate-400">{row.scanTime}</span>
        </div>

        <span className={`w-5 h-5 flex items-center justify-center ml-1 text-slate-400 transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 pt-3 pb-4 space-y-3">
          <div className="grid grid-cols-2 gap-2">
            <div className="rounded-xl bg-cyan-50 dark:bg-cyan-500/10 border border-cyan-100 dark:border-cyan-500/20 p-3">
              <p className="text-[10px] font-bold uppercase tracking-wide text-cyan-600 dark:text-cyan-400 mb-1 flex items-center gap-1">
                <Tag className="w-3 h-3" /> RFID Tag
              </p>
              <p className="text-[14px] font-mono font-bold text-cyan-700 dark:text-cyan-300">{row.rfidTag}</p>
            </div>
            <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-700 p-3">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 mb-1 flex items-center gap-1">
                <Clock className="w-3 h-3" /> Scan Time
              </p>
              <p className="text-[14px] font-mono font-bold text-slate-700 dark:text-slate-200">{row.scanTime}</p>
            </div>
            <div className="rounded-xl bg-violet-50 dark:bg-violet-500/10 border border-violet-100 dark:border-violet-500/20 p-3">
              <p className="text-[10px] font-bold uppercase tracking-wide text-violet-600 dark:text-violet-400 mb-1 flex items-center gap-1">
                <Fingerprint className="w-3 h-3" /> Gate
              </p>
              <p className="text-[14px] font-bold text-violet-700 dark:text-violet-300">{row.gate}</p>
            </div>
            <div className={`rounded-xl border p-3 ${cfg.bg} ${cfg.border}`}>
              <p className={`text-[10px] font-bold uppercase tracking-wide mb-1 ${cfg.text}`}>Status</p>
              <StatusBadge status={row.status} />
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, session, setSession, cls, setCls, date, setDate, onShow, loading, errors }) {
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
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Search Filters</span>
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
          <Field label="Class" error={errors.cls} required>
            <NativeSelect value={cls} onChange={e => setCls(e.target.value)} placeholder="-- Select Class --" error={errors.cls}>
              {CLASSES.map(c => <option key={c} value={c}>{c}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Date">
            <input
              type="date"
              value={date}
              onChange={e => setDate(e.target.value)}
              className="w-full pl-3 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                bg-white text-slate-800 border-slate-200
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                dark:focus:border-indigo-400"
            />
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

// ─── ATTENDANCE PERCENTAGE BAR ────────────────────────────────────────────────
function AttendanceBar({ present, total }) {
  const pct = total ? Math.round((present / total) * 100) : 0
  const color = pct >= 90 ? 'bg-emerald-500' : pct >= 75 ? 'bg-amber-500' : 'bg-rose-500'
  return (
    <div className="w-full">
      <div className="flex justify-between text-[11px] font-semibold mb-1">
        <span className="text-emerald-600 dark:text-emerald-400">Present {pct}%</span>
        <span className="text-rose-500 dark:text-rose-400">Absent {100 - pct}%</span>
      </div>
      <div className="h-2 rounded-full bg-rose-200 dark:bg-rose-500/20 overflow-hidden">
        <div className={`h-full rounded-full transition-all duration-700 ${color}`} style={{ width: `${pct}%` }} />
      </div>
    </div>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function RFIDCampusAttendanceCount() {
  const [session,      setSession]      = useState('')
  const [cls,          setCls]          = useState('')
  const [date,         setDate]         = useState(() => new Date().toISOString().split('T')[0])
  const [rows,         setRows]         = useState([])
  const [loading,      setLoading]      = useState(false)
  const [exporting,    setExporting]    = useState(false)
  const [filterOpen,   setFilterOpen]   = useState(false)
  const [search,       setSearch]       = useState('')
  const [errors,       setErrors]       = useState({})
  const [toast,        setToast]        = useState(null)
  const [shown,        setShown]        = useState(false)
  const [shownMeta,    setShownMeta]    = useState({ session: '', cls: '', date: '' })
  const [statusFilter, setStatusFilter] = useState('All')

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Format display date ────────────────────────────────────────────────────
  const formatDate = (d) => {
    if (!d) return todayStr()
    const dt = new Date(d)
    return `${String(dt.getDate()).padStart(2, '0')} ${dt.toLocaleString('en-IN', { month: 'short' })} ${dt.getFullYear()}`
  }

  // ── Show Report ────────────────────────────────────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}
    if (!session) err.session = 'Please select a session'
    if (!cls) err.cls = 'Please select a class'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')
    setStatusFilter('All')

    setTimeout(() => {
      const data = generateAttendance(cls, date)
      setRows(data)
      setShownMeta({ session, cls, date: formatDate(date) })
      setShown(true)
      setLoading(false)
      showToast(`Loaded ${data.length} student records for ${cls} — ${formatDate(date)}.`)
    }, 700)
  }, [session, cls, date])

  const handleReset = () => {
    setSession(''); setCls(''); setDate(new Date().toISOString().split('T')[0])
    setRows([]); setSearch(''); setErrors({})
    setShown(false); setShownMeta({ session: '', cls: '', date: '' })
    setStatusFilter('All')
  }

  const handleExcel = () => {
    if (rows.length === 0) { showToast('No data to export. Show report first.', 'error'); return }
    setExporting(true)
    setTimeout(() => { setExporting(false); showToast('Excel export ready! (API integration pending)') }, 1200)
  }

  // ── Search + Status filter ─────────────────────────────────────────────────
  const filtered = useMemo(() => {
    let r = rows
    if (statusFilter !== 'All') r = r.filter(x => x.status === statusFilter)
    if (search) {
      const q = search.toLowerCase()
      r = r.filter(x =>
        x.name.toLowerCase().includes(q) ||
        x.rollNo.toLowerCase().includes(q) ||
        x.rfidTag.toLowerCase().includes(q) ||
        x.section.toLowerCase().includes(q)
      )
    }
    return r
  }, [rows, search, statusFilter])

  // ── Totals ────────────────────────────────────────────────────────────────
  const totals = useMemo(() => ({
    Total:   rows.length,
    Present: rows.filter(r => r.status === 'Present').length,
    Absent:  rows.filter(r => r.status === 'Absent').length,
    Late:    rows.filter(r => r.status === 'Late').length,
  }), [rows])

  const hasResults   = shown && rows.length > 0
  const activeFilters = [session, cls].filter(Boolean).length

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ──────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Wifi className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            RFID Attendance Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Campus-wide RFID-based student attendance — class &amp; date wise.
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

      {/* ── DESKTOP Filter Card ────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Filter className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
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

            <Field label="Date">
              <div className="relative">
                <CalendarDays className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none z-10" />
                <input
                  type="date"
                  value={date}
                  onChange={e => setDate(e.target.value)}
                  className="w-full pl-8 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                    bg-white text-slate-800 border-slate-200
                    focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                    dark:focus:border-indigo-400"
                />
              </div>
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
          {cls ? cls : 'Select Filters'}
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
        session={session} setSession={setSession}
        cls={cls} setCls={setCls}
        date={date} setDate={setDate}
        onShow={handleShow}
        loading={loading}
        errors={errors}
      />

      {/* ── Loading Skeleton ─────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          <div className="h-20 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-4" />
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-4">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="h-16 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" />
            ))}
          </div>
          {[...Array(7)].map((_, i) => (
            <div key={i} className="h-12 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.1 }} />
          ))}
        </div>
      )}

      {/* ── Results ─────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School Header */}
          <SchoolHeader session={shownMeta.session} cls={shownMeta.cls} date={shownMeta.date} />

          {/* Summary Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={Users}      label="Total Students"  value={totals.Total}   color="blue"    />
            <SummaryCard icon={UserCheck}  label="Present"         value={totals.Present} color="emerald" sublabel={`${totals.Total ? Math.round((totals.Present/totals.Total)*100) : 0}% attendance`} />
            <SummaryCard icon={UserX}      label="Absent"          value={totals.Absent}  color="rose"    />
            <SummaryCard icon={ShieldAlert}label="Late Arrivals"   value={totals.Late}    color="amber"   />
          </div>

          {/* Attendance Percentage Bar */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] px-5 py-4 shadow-sm">
            <p className="text-[12px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 mb-3 flex items-center gap-2">
              <TrendingUp className="w-4 h-4 text-blue-500" /> Attendance Overview
            </p>
            <AttendanceBar present={totals.Present} total={totals.Total} />
            <div className="flex gap-4 mt-3 text-[11px] text-slate-500 dark:text-slate-400">
              <span className="flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-emerald-500 inline-block" />
                Present: {totals.Present}
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-rose-500 inline-block" />
                Absent: {totals.Absent}
              </span>
              <span className="flex items-center gap-1.5">
                <span className="w-2 h-2 rounded-full bg-amber-500 inline-block" />
                Late: {totals.Late}
              </span>
            </div>
          </div>

          {/* Results Card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <Wifi className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Student Attendance</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownMeta.cls} · {shownMeta.date}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                  {filtered.length} record{filtered.length !== 1 ? 's' : ''}
                </span>
              </div>

              {/* Search */}
              <div className="relative w-full sm:w-52 flex-shrink-0">
                <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  value={search}
                  onChange={e => setSearch(e.target.value)}
                  placeholder="Name, roll, tag…"
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

            {/* Status Filter Tabs */}
            <div className="flex items-center gap-2 px-5 py-2.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] overflow-x-auto">
              {['All', 'Present', 'Absent', 'Late'].map(s => {
                const count = s === 'All' ? rows.length : totals[s] || 0
                const active = statusFilter === s
                const tabColor = {
                  All:     active ? 'bg-blue-600 text-white shadow-md shadow-blue-500/20' : 'bg-slate-100 text-slate-600 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400',
                  Present: active ? 'bg-emerald-600 text-white shadow-md shadow-emerald-500/20' : 'bg-emerald-50 text-emerald-700 hover:bg-emerald-100 dark:bg-emerald-500/10 dark:text-emerald-400',
                  Absent:  active ? 'bg-rose-600 text-white shadow-md shadow-rose-500/20' : 'bg-rose-50 text-rose-700 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400',
                  Late:    active ? 'bg-amber-500 text-white shadow-md shadow-amber-500/20' : 'bg-amber-50 text-amber-700 hover:bg-amber-100 dark:bg-amber-500/10 dark:text-amber-400',
                }[s]
                return (
                  <button
                    key={s}
                    onClick={() => setStatusFilter(s)}
                    className={`flex-shrink-0 flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-[12px] font-semibold transition-all ${tabColor}`}
                  >
                    {s} <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded-full ${active ? 'bg-white/20' : 'bg-white dark:bg-slate-900/50'}`}>{count}</span>
                  </button>
                )
              })}
            </div>

            {/* Info hint */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
              <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <p className="text-[12px] text-blue-700 dark:text-blue-400">
                RFID scanner logs student entry at campus gates. Scan time indicates the first detection. Late = arrival after 9:00 AM.
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
                      {['S.No.', 'Roll No.', 'Student Name', 'Sec', 'RFID Tag', 'Scan Time', 'Gate', 'Total', 'Present', 'Absent', 'Status'].map((h, i) => (
                        <th key={i} className="px-3 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((row) => (
                      <DesktopRow key={row.rollNo} row={row} />
                    ))}
                    {/* Grand Total row — shown only when not filtered by status */}
                    {statusFilter === 'All' && (
                      <DesktopRow row={totals} isTotal />
                    )}
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
                    Tap a card to see RFID scan details.
                  </p>

                  {filtered.map((row) => (
                    <MobileCard key={row.rollNo} row={row} />
                  ))}

                  {/* Mobile Grand Total */}
                  {statusFilter === 'All' && (
                    <div className="rounded-xl border-2 border-blue-200 dark:border-indigo-500/30 bg-blue-50 dark:bg-indigo-500/[0.07] p-4">
                      <p className="text-[12px] font-bold uppercase tracking-wide text-blue-700 dark:text-blue-400 mb-3 flex items-center gap-2">
                        <TrendingUp className="w-4 h-4" /> Grand Total — {shownMeta.cls}
                      </p>
                      <div className="grid grid-cols-2 gap-2 mb-3">
                        <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                          <p className="text-[22px] font-bold text-blue-700 dark:text-blue-300 tabular-nums">{totals.Total}</p>
                          <p className="text-[10px] font-semibold text-blue-600 dark:text-blue-400">Total Students</p>
                        </div>
                        <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                          <p className="text-[22px] font-bold text-emerald-700 dark:text-emerald-300 tabular-nums">{totals.Present}</p>
                          <p className="text-[10px] font-semibold text-emerald-600 dark:text-emerald-400">Present</p>
                        </div>
                        <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                          <p className="text-[22px] font-bold text-rose-700 dark:text-rose-300 tabular-nums">{totals.Absent}</p>
                          <p className="text-[10px] font-semibold text-rose-600 dark:text-rose-400">Absent</p>
                        </div>
                        <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                          <p className="text-[22px] font-bold text-amber-700 dark:text-amber-300 tabular-nums">{totals.Late}</p>
                          <p className="text-[10px] font-semibold text-amber-600 dark:text-amber-400">Late</p>
                        </div>
                      </div>
                      <AttendanceBar present={totals.Present} total={totals.Total} />
                    </div>
                  )}
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
                  className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1"
                >
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
            <WifiOff className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No report generated yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select a <strong>session</strong>, <strong>class</strong>, and <strong>date</strong>, then click <strong>Show</strong> to load the RFID attendance report.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
