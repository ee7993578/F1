/**
 * RFIDAttendance.jsx
 * RFID Attendance Report — fully responsive React + Tailwind
 * Mirrors the legacy ASPX page logic with premium ERP UI
 */

import { useState, useMemo, useCallback, useEffect } from 'react'
import {
  Filter, RefreshCw, Eye, AlertCircle, X, Check, Loader2,
  ChevronDown, ChevronRight, SlidersHorizontal, Search,
  Calendar, FileSpreadsheet, Radio, Clock, Users,
  CheckCircle2, XCircle, BookOpen, School2, MapPin,
  Building2, TrendingUp, Info, BarChart3, Wifi,
  UserCheck, UserX, CalendarDays, Hash
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ──────────────────────────────────────────────────────

const SESSIONS = [
  { value: '2022-23', label: '2022-23' },
  { value: '2023-24', label: '2023-24' },
  { value: '2024-25', label: '2024-25' },
  { value: '2025-26', label: '2025-26' },
]

const CLASSES = [
  { value: 'Nursery', label: 'Nursery' },
  { value: 'LKG',     label: 'LKG' },
  { value: 'UKG',     label: 'UKG' },
  { value: 'Class I',    label: 'Class I' },
  { value: 'Class II',   label: 'Class II' },
  { value: 'Class III',  label: 'Class III' },
  { value: 'Class IV',   label: 'Class IV' },
  { value: 'Class V',    label: 'Class V' },
  { value: 'Class VI',   label: 'Class VI' },
  { value: 'Class VII',  label: 'Class VII' },
  { value: 'Class VIII', label: 'Class VIII' },
  { value: 'Class IX',   label: 'Class IX' },
  { value: 'Class X',    label: 'Class X' },
  { value: 'Class XI',   label: 'Class XI' },
  { value: 'Class XII',  label: 'Class XII' },
]

const YEARS  = ['2021','2022','2023','2024','2025','2026']
const MONTHS = [
  'January','February','March','April','May','June',
  'July','August','September','October','November','December'
]

const SCHOOL_INFO = {
  name:    'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

/** Generate dummy RFID attendance rows for a class + date */
const generateAttendance = (className, dateStr) => {
  const sections   = ['A','B']
  const firstNames = ['Aarav','Priya','Rahul','Sneha','Amit','Pooja','Vikram','Ananya','Rohan','Meera',
                      'Karan','Nisha','Arjun','Divya','Siddharth','Lakshmi','Yash','Riya','Nikhil','Kavya']
  const lastNames  = ['Sharma','Gupta','Verma','Singh','Kumar','Joshi','Patel','Mishra','Yadav','Tiwari']
  const seed = (className.charCodeAt(0) + (dateStr?.length ?? 0)) % 7
  const count = 18 + seed * 2
  const rows = []
  for (let i = 0; i < count; i++) {
    const fn  = firstNames[i % firstNames.length]
    const ln  = lastNames[(i + seed) % lastNames.length]
    const sec = sections[i % 2]
    const roll = String(i + 1).padStart(3, '0')
    const rfid = `RF${String(100000 + i * 137 + seed * 31).slice(0,6)}`
    // Vary in/out times slightly
    const inH  = 7 + (i % 3 === 0 ? 1 : 0)
    const inM  = String((i * 7) % 60).padStart(2,'0')
    const outH = 13 + (i % 4)
    const outM = String((i * 11) % 60).padStart(2,'0')
    const status = i % 9 === 0 ? 'Absent' : i % 11 === 0 ? 'Late' : 'Present'
    rows.push({
      sno:      i + 1,
      roll,
      rfid,
      name:     `${fn} ${ln}`,
      section:  sec,
      inTime:   status === 'Absent' ? '—' : `${String(inH).padStart(2,'0')}:${inM} AM`,
      outTime:  status === 'Absent' ? '—' : `${String(outH).padStart(2,'0')}:${outM} PM`,
      status,
    })
  }
  return rows
}

// ─── HELPERS ─────────────────────────────────────────────────────────────────

const STATUS_CONFIG = {
  Present: {
    pill:  'bg-emerald-50 text-emerald-700 border border-emerald-200 dark:bg-emerald-500/10 dark:text-emerald-400 dark:border-emerald-500/25',
    icon:  CheckCircle2,
    dot:   'bg-emerald-500',
  },
  Absent: {
    pill:  'bg-rose-50 text-rose-700 border border-rose-200 dark:bg-rose-500/10 dark:text-rose-400 dark:border-rose-500/25',
    icon:  XCircle,
    dot:   'bg-rose-500',
  },
  Late: {
    pill:  'bg-amber-50 text-amber-700 border border-amber-200 dark:bg-amber-500/10 dark:text-amber-400 dark:border-amber-500/25',
    icon:  Clock,
    dot:   'bg-amber-500',
  },
}

const CLASS_COLORS = [
  { fg:'#1d4ed8', bg:'#dbeafe' },{ fg:'#7c3aed', bg:'#ede9fe' },
  { fg:'#0891b2', bg:'#cffafe' },{ fg:'#059669', bg:'#d1fae5' },
  { fg:'#d97706', bg:'#fef3c7' },{ fg:'#dc2626', bg:'#fee2e2' },
  { fg:'#0369a1', bg:'#e0f2fe' },
]
const classColor = (name='') => CLASS_COLORS[(name.charCodeAt(0)??0) % CLASS_COLORS.length]

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

function Toast({ message, type='success', onClose }) {
  return (
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3
        rounded-2xl shadow-xl text-[13px] font-semibold min-w-[260px] max-w-[90vw]
        ${type==='success' ? 'bg-emerald-600 text-white' : 'bg-rose-600 text-white'}`}
      style={{ animation:'slideUp .25s ease' }}
    >
      {type==='success'
        ? <Check className="w-4 h-4 flex-shrink-0" />
        : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75 hover:opacity-100" /></button>
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(10px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
    </div>
  )
}

// ─── SUMMARY STAT CARD ────────────────────────────────────────────────────────

function SummaryCard({ icon:Icon, label, value, color }) {
  const colors = {
    blue:    'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400',
    emerald: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400',
    rose:    'bg-rose-50 text-rose-600 dark:bg-rose-500/10 dark:text-rose-400',
    amber:   'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400',
    violet:  'bg-violet-50 text-violet-600 dark:bg-violet-500/10 dark:text-violet-400',
  }
  return (
    <div className="flex items-center gap-3 rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-white dark:bg-[#1a1f35] px-4 py-3 shadow-sm flex-1 min-w-0">
      <span className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 ${colors[color]}`}>
        <Icon className="w-5 h-5" />
      </span>
      <div className="min-w-0">
        <p className="text-[20px] font-bold text-slate-800 dark:text-slate-100 tabular-nums leading-tight">{value.toLocaleString()}</p>
        <p className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{label}</p>
      </div>
    </div>
  )
}

// ─── SCHOOL HEADER ────────────────────────────────────────────────────────────

function SchoolHeader({ params }) {
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
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-blue-100 dark:bg-blue-500/15 border border-blue-200 dark:border-blue-500/25 text-[12px] font-bold text-blue-700 dark:text-blue-400">
          <CalendarDays className="w-3.5 h-3.5" /> Session: {params.session}
        </span>
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-violet-100 dark:bg-violet-500/15 border border-violet-200 dark:border-violet-500/25 text-[12px] font-bold text-violet-700 dark:text-violet-400">
          <School2 className="w-3.5 h-3.5" /> {params.className}
        </span>
        {params.date && (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-100 dark:bg-emerald-500/15 border border-emerald-200 dark:border-emerald-500/25 text-[12px] font-bold text-emerald-700 dark:text-emerald-400">
            <Calendar className="w-3.5 h-3.5" /> {params.date}
          </span>
        )}
      </div>
      <p className="mt-2.5 text-[13px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400 flex items-center justify-center gap-2">
        <Wifi className="w-4 h-4" /> RFID Attendance Report
      </p>
    </div>
  )
}

// ─── STATUS BADGE ─────────────────────────────────────────────────────────────

function StatusBadge({ status }) {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG.Absent
  const Icon = cfg.icon
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-semibold ${cfg.pill}`}>
      <Icon className="w-3 h-3 flex-shrink-0" />
      {status}
    </span>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────

function DesktopRow({ row }) {
  const { fg, bg } = classColor(row.name)
  const initials = row.name.split(' ').map(w=>w[0]).join('').slice(0,2).toUpperCase()

  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
      {/* S.No */}
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{row.sno}</td>
      {/* Roll */}
      <td className="px-4 py-3 text-center">
        <span className="text-[12px] font-mono font-semibold text-slate-600 dark:text-slate-300">{row.roll}</span>
      </td>
      {/* RFID */}
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center gap-1 text-[11px] font-mono font-semibold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 px-2 py-0.5 rounded-md">
          <Radio className="w-3 h-3 text-blue-500 flex-shrink-0" />{row.rfid}
        </span>
      </td>
      {/* Name */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-2.5">
          <span className="w-7 h-7 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold"
            style={{ background:bg, color:fg }}>{initials}</span>
          <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{row.name}</span>
        </div>
      </td>
      {/* Section */}
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center justify-center w-7 h-7 rounded-lg text-[12px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300">
          {row.section}
        </span>
      </td>
      {/* In Time */}
      <td className="px-4 py-3 text-center">
        <span className={`text-[12px] font-semibold tabular-nums ${row.inTime==='—' ? 'text-slate-400' : 'text-blue-600 dark:text-blue-400'}`}>
          {row.inTime}
        </span>
      </td>
      {/* Out Time */}
      <td className="px-4 py-3 text-center">
        <span className={`text-[12px] font-semibold tabular-nums ${row.outTime==='—' ? 'text-slate-400' : 'text-indigo-600 dark:text-indigo-400'}`}>
          {row.outTime}
        </span>
      </td>
      {/* Status */}
      <td className="px-4 py-3 text-center">
        <StatusBadge status={row.status} />
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────

function MobileCard({ row }) {
  const [expanded, setExpanded] = useState(false)
  const { fg, bg } = classColor(row.name)
  const initials = row.name.split(' ').map(w=>w[0]).join('').slice(0,2).toUpperCase()
  const cfg = STATUS_CONFIG[row.status] || STATUS_CONFIG.Absent

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header */}
      <button
        type="button"
        onClick={() => setExpanded(p=>!p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        <span className="w-9 h-9 rounded-xl flex-shrink-0 flex items-center justify-center text-[11px] font-bold"
          style={{ background:bg, color:fg }}>{initials}</span>
        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{row.name}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5 flex items-center gap-2">
            <span className="font-mono">Roll {row.roll}</span>
            <span>·</span>
            <span className="flex items-center gap-1"><Radio className="w-2.5 h-2.5 text-blue-400" />{row.rfid}</span>
            <span>· Sec {row.section}</span>
          </p>
        </div>
        <div className="flex flex-col items-end gap-1 flex-shrink-0">
          <StatusBadge status={row.status} />
        </div>
        <span className={`w-5 h-5 flex items-center justify-center ml-1 text-slate-400 transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Expanded */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 pt-3 pb-4">
          <div className="grid grid-cols-2 gap-3">
            <div className="rounded-xl bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20 p-3 text-center">
              <Clock className="w-4 h-4 text-blue-600 dark:text-blue-400 mx-auto mb-1" />
              <p className="text-[15px] font-bold text-blue-700 dark:text-blue-300 tabular-nums leading-tight">{row.inTime}</p>
              <p className="text-[10px] font-bold uppercase tracking-wide text-blue-600 dark:text-blue-400 mt-0.5">In Time</p>
            </div>
            <div className="rounded-xl bg-indigo-50 dark:bg-indigo-500/10 border border-indigo-100 dark:border-indigo-500/20 p-3 text-center">
              <Clock className="w-4 h-4 text-indigo-600 dark:text-indigo-400 mx-auto mb-1" />
              <p className="text-[15px] font-bold text-indigo-700 dark:text-indigo-300 tabular-nums leading-tight">{row.outTime}</p>
              <p className="text-[10px] font-bold uppercase tracking-wide text-indigo-600 dark:text-indigo-400 mt-0.5">Out Time</p>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────

function FilterDrawer({ open, onClose, filters, setFilter, onShow, loading, errors }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl"
        style={{ animation:'drawerUp .25s ease', maxHeight:'90vh', overflowY:'auto' }}
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
        <div className="px-5 py-4 space-y-4">
          <Field label="Session" error={errors.session} required>
            <NativeSelect value={filters.session} onChange={e=>setFilter('session',e.target.value)}
              placeholder="-- Select Session --" error={errors.session}>
              {SESSIONS.map(s=><option key={s.value} value={s.value}>{s.label}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Class" error={errors.className} required>
            <NativeSelect value={filters.className} onChange={e=>setFilter('className',e.target.value)}
              placeholder="-- Select Class --" error={errors.className}>
              {CLASSES.map(c=><option key={c.value} value={c.value}>{c.label}</option>)}
            </NativeSelect>
          </Field>
          <div className="grid grid-cols-2 gap-3">
            <Field label="Year">
              <NativeSelect value={filters.year} onChange={e=>setFilter('year',e.target.value)} placeholder="-- Year --">
                {YEARS.map(y=><option key={y} value={y}>{y}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Month">
              <NativeSelect value={filters.month} onChange={e=>setFilter('month',e.target.value)} placeholder="-- Month --">
                {MONTHS.map(m=><option key={m} value={m}>{m}</option>)}
              </NativeSelect>
            </Field>
          </div>
          <Field label="Date">
            <input
              type="date"
              value={filters.date}
              onChange={e=>setFilter('date',e.target.value)}
              className="w-full px-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                bg-white text-slate-800 border-slate-200
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]"
            />
          </Field>
        </div>
        <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex gap-3">
          <button type="button" onClick={onClose}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
            Cancel
          </button>
          <button type="button" onClick={()=>{onShow();onClose()}} disabled={loading}
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

export default function RFIDAttendance() {
  const [filters, setFiltersState] = useState({
    session:'', className:'', year:'', month:'', date:''
  })
  const [rows,        setRows]        = useState([])
  const [loading,     setLoading]     = useState(false)
  const [exporting,   setExporting]   = useState(false)
  const [filterOpen,  setFilterOpen]  = useState(false)
  const [search,      setSearch]      = useState('')
  const [errors,      setErrors]      = useState({})
  const [toast,       setToast]       = useState(null)
  const [shown,       setShown]       = useState(false)
  const [shownParams, setShownParams] = useState({})
  const [statusFilter, setStatusFilter] = useState('All')

  const showToast = (msg, type='success') => {
    setToast({ msg, type })
    setTimeout(()=>setToast(null), 3500)
  }

  const setFilter = (key, value) => {
    setFiltersState(p=>({ ...p, [key]:value }))
    setErrors(p=>({ ...p, [key]:undefined }))
  }

  // ── Validate & Load ───────────────────────────────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}
    if (!filters.session)   err.session   = 'Please select a session'
    if (!filters.className) err.className = 'Please select a class'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')
    setStatusFilter('All')

    // Simulate API call
    setTimeout(() => {
      const dateLabel = filters.date
        ? new Date(filters.date).toLocaleDateString('en-IN',{day:'2-digit',month:'short',year:'numeric'})
        : (filters.month && filters.year ? `${filters.month} ${filters.year}` : 'All Dates')
      const data = generateAttendance(filters.className, filters.date || filters.month)
      setRows(data)
      setShownParams({
        session:   filters.session,
        className: filters.className,
        date:      dateLabel,
        year:      filters.year,
        month:     filters.month,
      })
      setShown(true)
      setLoading(false)
      showToast(`Loaded ${data.length} attendance records.`)
    }, 700)
  }, [filters])

  const handleReset = () => {
    setFiltersState({ session:'', className:'', year:'', month:'', date:'' })
    setRows([]); setSearch(''); setErrors({})
    setShown(false); setShownParams({}); setStatusFilter('All')
  }

  const handleExcel = () => {
    if (!rows.length) { showToast('No data to export. Show report first.','error'); return }
    setExporting(true)
    setTimeout(()=>{ setExporting(false); showToast('Excel export ready! (API integration pending)') }, 1200)
  }

  // ── Search + Status filter ────────────────────────────────────────────────
  const filtered = useMemo(() => {
    let r = rows
    if (statusFilter !== 'All') r = r.filter(x=>x.status===statusFilter)
    if (search) {
      const q = search.toLowerCase()
      r = r.filter(x=>
        x.name.toLowerCase().includes(q) ||
        x.roll.includes(q) ||
        x.rfid.toLowerCase().includes(q) ||
        x.section.toLowerCase().includes(q)
      )
    }
    return r
  }, [rows, search, statusFilter])

  // ── Totals ────────────────────────────────────────────────────────────────
  const totals = useMemo(() => ({
    total:   rows.length,
    present: rows.filter(r=>r.status==='Present').length,
    absent:  rows.filter(r=>r.status==='Absent').length,
    late:    rows.filter(r=>r.status==='Late').length,
  }), [rows])

  const hasResults  = shown && rows.length > 0
  const activeCount = [filters.session, filters.className, filters.year, filters.month, filters.date].filter(Boolean).length

  const COLS = ['S.No.','Roll No','RFID','Student Name','Section','In Time','Out Time','Status']

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Radio className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            RFID Attendance Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            View student attendance captured via RFID scanner — filter by session, class &amp; date.
          </p>
        </div>
        {hasResults && (
          <button type="button" onClick={handleExcel} disabled={exporting}
            className="hidden sm:flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold
              bg-emerald-600 text-white hover:bg-emerald-700 shadow-md shadow-emerald-500/20
              transition-all active:scale-95 disabled:opacity-70 flex-shrink-0">
            {exporting ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileSpreadsheet className="w-4 h-4" />}
            Export Excel
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
          <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-4 items-end">
            <Field label="Session" error={errors.session} required>
              <NativeSelect value={filters.session} onChange={e=>setFilter('session',e.target.value)}
                placeholder="-- Select Session --" error={errors.session}>
                {SESSIONS.map(s=><option key={s.value} value={s.value}>{s.label}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Class" error={errors.className} required>
              <NativeSelect value={filters.className} onChange={e=>setFilter('className',e.target.value)}
                placeholder="-- Select Class --" error={errors.className}>
                {CLASSES.map(c=><option key={c.value} value={c.value}>{c.label}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Year">
              <NativeSelect value={filters.year} onChange={e=>setFilter('year',e.target.value)} placeholder="-- Select Year --">
                {YEARS.map(y=><option key={y} value={y}>{y}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Month">
              <NativeSelect value={filters.month} onChange={e=>setFilter('month',e.target.value)} placeholder="-- Select Month --">
                {MONTHS.map(m=><option key={m} value={m}>{m}</option>)}
              </NativeSelect>
            </Field>
            <Field label="Date">
              <input
                type="date"
                value={filters.date}
                onChange={e=>setFilter('date',e.target.value)}
                className="w-full px-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                  bg-white text-slate-800 border-slate-200
                  focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                  dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]"
              />
            </Field>
          </div>
          <div className="flex items-center gap-3 mt-4 pt-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
            <button type="button" onClick={handleShow} disabled={loading}
              className="flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl text-[13px] font-semibold text-white
                bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700
                transition-all active:scale-95 disabled:opacity-70">
              {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Eye className="w-4 h-4" />}
              Show Report
            </button>
            <button type="button" onClick={handleReset}
              className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-[13px] font-semibold
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors">
              <RefreshCw className="w-3.5 h-3.5" /> Reset
            </button>
          </div>
        </div>
      </div>

      {/* ── MOBILE Filter Bar ─────────────────────────────────────────────── */}
      <div className="flex sm:hidden gap-2">
        <button type="button" onClick={()=>setFilterOpen(true)}
          className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
            bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {filters.session && filters.className
            ? `${filters.className} · ${filters.session}`
            : 'Select Filters'}
          {activeCount > 0 && (
            <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeCount}</span>
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
        open={filterOpen} onClose={()=>setFilterOpen(false)}
        filters={filters} setFilter={setFilter}
        onShow={handleShow} loading={loading} errors={errors}
      />

      {/* ── Loading Skeleton ───────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          <div className="h-24 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-4" />
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-4">
            {[...Array(4)].map((_,i)=>(
              <div key={i} className="h-16 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" />
            ))}
          </div>
          {[...Array(5)].map((_,i)=>(
            <div key={i} className="h-12 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity:1-i*0.15 }} />
          ))}
        </div>
      )}

      {/* ── Results ───────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School Header */}
          <SchoolHeader params={shownParams} />

          {/* Summary Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={Users}       label="Total Students"  value={totals.total}   color="blue"    />
            <SummaryCard icon={UserCheck}   label="Present"         value={totals.present} color="emerald" />
            <SummaryCard icon={UserX}       label="Absent"          value={totals.absent}  color="rose"    />
            <SummaryCard icon={Clock}       label="Late"            value={totals.late}    color="amber"   />
          </div>

          {/* Results card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <BookOpen className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Attendance Records</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownParams.className}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                  {filtered.length} record{filtered.length!==1?'s':''}
                </span>
              </div>
              {/* Search */}
              <div className="relative w-full sm:w-56 flex-shrink-0">
                <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  value={search}
                  onChange={e=>setSearch(e.target.value)}
                  placeholder="Search name, roll, RFID…"
                  className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                    bg-white text-slate-700 border-slate-200 placeholder-slate-300
                    focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]
                    dark:placeholder-slate-600 dark:focus:border-indigo-400"
                />
                {search && (
                  <button onClick={()=>setSearch('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                    <X className="w-3.5 h-3.5" />
                  </button>
                )}
              </div>
            </div>

            {/* Status filter tabs */}
            <div className="flex items-center gap-2 px-5 py-2.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] overflow-x-auto no-scrollbar">
              {['All','Present','Absent','Late'].map(s=>{
                const active = statusFilter === s
                const colors = {
                  All:     active ? 'bg-blue-600 text-white dark:bg-indigo-600'     : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300',
                  Present: active ? 'bg-emerald-600 text-white'                     : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300',
                  Absent:  active ? 'bg-rose-600 text-white'                        : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300',
                  Late:    active ? 'bg-amber-500 text-white'                       : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300',
                }
                const cnt = s==='All' ? rows.length : rows.filter(r=>r.status===s).length
                return (
                  <button key={s} onClick={()=>setStatusFilter(s)}
                    className={`flex-shrink-0 flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold transition-all ${colors[s]}`}>
                    {s}
                    <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded-full ${active ? 'bg-white/25' : 'bg-slate-200 dark:bg-slate-700 text-slate-500 dark:text-slate-400'}`}>{cnt}</span>
                  </button>
                )
              })}
            </div>

            {/* Info hint */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
              <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <p className="text-[12px] text-blue-700 dark:text-blue-400">
                RFID attendance records. In/Out times captured automatically by scanner at entry &amp; exit.
              </p>
            </div>

            {/* ── DESKTOP TABLE ── */}
            <div className="hidden md:block overflow-x-auto">
              {filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No records match your filter.</span>
                </div>
              ) : (
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                      {COLS.map((h,i)=>(
                        <th key={i} className="px-4 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:w-12">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map(row=><DesktopRow key={`${row.roll}-${row.rfid}`} row={row} />)}
                  </tbody>
                  {/* Footer totals */}
                  <tfoot>
                    <tr className="bg-blue-50 dark:bg-indigo-500/[0.07] border-t-2 border-blue-200 dark:border-indigo-500/30">
                      <td colSpan={2} className="px-4 py-3">
                        <span className="text-[13px] font-bold text-blue-700 dark:text-blue-300 flex items-center gap-2">
                          <TrendingUp className="w-4 h-4" /> Summary
                        </span>
                      </td>
                      <td colSpan={2} className="px-4 py-3 text-center">
                        <span className="text-[12px] font-bold text-blue-700 dark:text-blue-300">Total: {filtered.length}</span>
                      </td>
                      <td className="px-4 py-3 text-center" />
                      <td className="px-4 py-3 text-center" />
                      <td className="px-4 py-3 text-center" />
                      <td className="px-4 py-3 text-center">
                        <div className="flex flex-col gap-1 items-center">
                          <StatusBadge status="Present" />
                          <span className="text-[11px] font-bold text-emerald-700 dark:text-emerald-400">{filtered.filter(r=>r.status==='Present').length} students</span>
                        </div>
                      </td>
                    </tr>
                  </tfoot>
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
                    Tap a card to see In/Out time details.
                  </p>
                  {filtered.map(row=>(
                    <MobileCard key={`${row.roll}-${row.rfid}`} row={row} />
                  ))}

                  {/* Mobile Summary */}
                  <div className="rounded-xl border-2 border-blue-200 dark:border-indigo-500/30 bg-blue-50 dark:bg-indigo-500/[0.07] p-4 mt-2">
                    <p className="text-[12px] font-bold uppercase tracking-wide text-blue-700 dark:text-blue-400 mb-3 flex items-center gap-2">
                      <TrendingUp className="w-4 h-4" /> Summary — {filtered.length} Records
                    </p>
                    <div className="grid grid-cols-3 gap-2">
                      <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                        <p className="text-[20px] font-bold text-emerald-700 dark:text-emerald-300 tabular-nums">{filtered.filter(r=>r.status==='Present').length}</p>
                        <p className="text-[10px] font-semibold text-emerald-600 dark:text-emerald-400">Present</p>
                      </div>
                      <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                        <p className="text-[20px] font-bold text-rose-700 dark:text-rose-300 tabular-nums">{filtered.filter(r=>r.status==='Absent').length}</p>
                        <p className="text-[10px] font-semibold text-rose-600 dark:text-rose-400">Absent</p>
                      </div>
                      <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                        <p className="text-[20px] font-bold text-amber-700 dark:text-amber-300 tabular-nums">{filtered.filter(r=>r.status==='Late').length}</p>
                        <p className="text-[10px] font-semibold text-amber-600 dark:text-amber-400">Late</p>
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
              {(search || statusFilter!=='All') && (
                <button onClick={()=>{setSearch('');setStatusFilter('All')}}
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
            <Wifi className="w-7 h-7 opacity-50" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No attendance report generated yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select a <strong>Session</strong> and <strong>Class</strong>, then click <strong>Show Report</strong>.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={()=>setToast(null)} />}

      {/* Scrollbar hide style */}
      <style>{`.no-scrollbar::-webkit-scrollbar{display:none}.no-scrollbar{-ms-overflow-style:none;scrollbar-width:none}`}</style>
    </div>
  )
}
