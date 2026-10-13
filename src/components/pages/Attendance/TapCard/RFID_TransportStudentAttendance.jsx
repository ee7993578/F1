/**
 * RFID_TransportStudentAttendance.jsx
 * Folder: src/pages/Transport/Reports/RFID_TransportStudentAttendance.jsx
 *
 * Converts legacy ASPX "RFID Transport Student Attendance" to fully-responsive React + Tailwind.
 *
 * Filters: Session, Route, Date
 * Columns: S.No, Student Name, Roll No, Class, Section, Route, Stop, RFID Tag, In Time, Out Time, Status
 * Features:
 *  - Session / Route / Date filters
 *  - Show report button
 *  - Desktop: dense ERP-style sticky-header table
 *  - Mobile: smart cards with expandable details + bottom filter drawer
 *  - Summary stat cards
 *  - Search across student/class/route
 *  - Grand total / present-absent counts
 *  - Loading skeleton + empty state + toast notifications
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Filter, RefreshCw, Eye, AlertCircle, X, Check, Loader2,
  ChevronDown, ChevronRight, SlidersHorizontal, Search, Info,
  Bus, MapPin, Clock, UserCheck, UserX, Users, Tag,
  CalendarDays, BookOpen, TrendingUp, BarChart3, Building2,
  BadgeCheck, ShieldAlert, Navigation, Radio
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ──────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const ROUTES = [
  { id: '0', label: '-- All Routes --' },
  { id: 'R1', label: 'Route 1 – Rajpur Road' },
  { id: 'R2', label: 'Route 2 – Chakrata Road' },
  { id: 'R3', label: 'Route 3 – Saharanpur Road' },
  { id: 'R4', label: 'Route 4 – Haridwar Road' },
  { id: 'R5', label: 'Route 5 – Mussoorie Road' },
]

const SCHOOL_INFO = {
  name: 'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

// dummy attendance records
const ATTENDANCE_ROWS = [
  { id: 1,  name: 'Aarav Sharma',       roll: 'SVM001', cls: 'Class IX',   section: 'A', route: 'R1', stop: 'Clock Tower',    rfid: 'RF-00112', inTime: '07:42 AM', outTime: '02:18 PM', status: 'Present' },
  { id: 2,  name: 'Priya Negi',         roll: 'SVM002', cls: 'Class IX',   section: 'B', route: 'R1', stop: 'ISBT Dehradun',  rfid: 'RF-00113', inTime: '07:45 AM', outTime: '02:20 PM', status: 'Present' },
  { id: 3,  name: 'Rohit Rawat',        roll: 'SVM003', cls: 'Class X',    section: 'A', route: 'R1', stop: 'Paltan Bazaar',  rfid: 'RF-00114', inTime: '',         outTime: '',         status: 'Absent'  },
  { id: 4,  name: 'Sneha Bisht',        roll: 'SVM004', cls: 'Class VIII', section: 'A', route: 'R2', stop: 'Balliwala Chowk',rfid: 'RF-00115', inTime: '07:50 AM', outTime: '02:15 PM', status: 'Present' },
  { id: 5,  name: 'Akash Dobhal',       roll: 'SVM005', cls: 'Class VII',  section: 'A', route: 'R2', stop: 'Clement Town',   rfid: 'RF-00116', inTime: '07:53 AM', outTime: '02:22 PM', status: 'Present' },
  { id: 6,  name: 'Kavya Thapliyal',    roll: 'SVM006', cls: 'Class VI',   section: 'A', route: 'R2', stop: 'Balliwala Chowk',rfid: 'RF-00117', inTime: '',         outTime: '',         status: 'Absent'  },
  { id: 7,  name: 'Vishal Juyal',       roll: 'SVM007', cls: 'Class XI',   section: 'A', route: 'R3', stop: 'Rispana Bridge', rfid: 'RF-00118', inTime: '07:38 AM', outTime: '02:10 PM', status: 'Present' },
  { id: 8,  name: 'Mansi Panwar',       roll: 'SVM008', cls: 'Class XII',  section: 'A', route: 'R3', stop: 'Kargi Chowk',   rfid: 'RF-00119', inTime: '07:40 AM', outTime: '02:12 PM', status: 'Present' },
  { id: 9,  name: 'Deepak Semwal',      roll: 'SVM009', cls: 'Class X',    section: 'A', route: 'R3', stop: 'Rispana Bridge', rfid: 'RF-00120', inTime: '07:41 AM', outTime: '',         status: 'Present' },
  { id: 10, name: 'Anita Koranga',      roll: 'SVM010', cls: 'Class IX',   section: 'A', route: 'R4', stop: 'Haridwar Bypass',rfid: 'RF-00121', inTime: '',         outTime: '',         status: 'Absent'  },
  { id: 11, name: 'Suraj Chandola',     roll: 'SVM011', cls: 'Class VIII', section: 'A', route: 'R4', stop: 'Mothorowala',   rfid: 'RF-00122', inTime: '07:55 AM', outTime: '02:25 PM', status: 'Present' },
  { id: 12, name: 'Ritu Gusain',        roll: 'SVM012', cls: 'Class VII',  section: 'A', route: 'R4', stop: 'Haridwar Bypass',rfid: 'RF-00123', inTime: '07:57 AM', outTime: '02:27 PM', status: 'Present' },
  { id: 13, name: 'Pankaj Uniyal',      roll: 'SVM013', cls: 'Class VI',   section: 'B', route: 'R5', stop: 'Mussoorie Road', rfid: 'RF-00124', inTime: '07:33 AM', outTime: '02:05 PM', status: 'Present' },
  { id: 14, name: 'Suman Lata',         roll: 'SVM014', cls: 'Class XI',   section: 'B', route: 'R5', stop: 'Kempty Chowk',  rfid: 'RF-00125', inTime: '07:35 AM', outTime: '02:08 PM', status: 'Present' },
  { id: 15, name: 'Rahul Joshi',        roll: 'SVM015', cls: 'Class XII',  section: 'B', route: 'R5', stop: 'Mussoorie Road', rfid: 'RF-00126', inTime: '',         outTime: '',         status: 'Absent'  },
  { id: 16, name: 'Pooja Bhandari',     roll: 'SVM016', cls: 'Class X',    section: 'A', route: 'R1', stop: 'ISBT Dehradun',  rfid: 'RF-00127', inTime: '07:44 AM', outTime: '02:19 PM', status: 'Present' },
  { id: 17, name: 'Nitin Arya',         roll: 'SVM017', cls: 'Class IX',   section: 'B', route: 'R2', stop: 'Clement Town',   rfid: 'RF-00128', inTime: '07:52 AM', outTime: '02:21 PM', status: 'Present' },
  { id: 18, name: 'Geeta Verma',        roll: 'SVM018', cls: 'Class VIII', section: 'A', route: 'R3', stop: 'Kargi Chowk',   rfid: 'RF-00129', inTime: '',         outTime: '',         status: 'Absent'  },
  { id: 19, name: 'Lalit Mohan',        roll: 'SVM019', cls: 'Class VII',  section: 'A', route: 'R4', stop: 'Mothorowala',   rfid: 'RF-00130', inTime: '07:56 AM', outTime: '02:26 PM', status: 'Present' },
  { id: 20, name: 'Archana Singh',      roll: 'SVM020', cls: 'Class VI',   section: 'A', route: 'R5', stop: 'Kempty Chowk',  rfid: 'RF-00131', inTime: '07:34 AM', outTime: '02:07 PM', status: 'Present' },
]

// ─── HELPERS ──────────────────────────────────────────────────────────────────
const STATUS_CONFIG = {
  Present: {
    bg:    'bg-emerald-50 dark:bg-emerald-500/10',
    text:  'text-emerald-700 dark:text-emerald-400',
    dot:   'bg-emerald-500',
    icon:  BadgeCheck,
    badge: 'bg-emerald-100 text-emerald-800 dark:bg-emerald-500/20 dark:text-emerald-300',
  },
  Absent: {
    bg:   'bg-rose-50 dark:bg-rose-500/10',
    text: 'text-rose-700 dark:text-rose-400',
    dot:  'bg-rose-500',
    icon: ShieldAlert,
    badge:'bg-rose-100 text-rose-800 dark:bg-rose-500/20 dark:text-rose-300',
  },
}

const ROUTE_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' },
  { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' },
  { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' },
]
const routeColor = (id) =>
  ROUTE_COLORS[(id?.charCodeAt(id.length - 1) ?? 0) % ROUTE_COLORS.length]

const routeLabel = (id) => ROUTES.find(r => r.id === id)?.label?.split('–')[1]?.trim() ?? id

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
function SummaryCard({ icon: Icon, label, value, color, subtitle }) {
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
        <p className="text-[20px] font-bold text-slate-800 dark:text-slate-100 tabular-nums leading-tight">{value}</p>
        <p className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{label}</p>
        {subtitle && <p className="text-[10px] text-slate-400 dark:text-slate-600 truncate">{subtitle}</p>}
      </div>
    </div>
  )
}

// ─── SCHOOL HEADER BANNER ─────────────────────────────────────────────────────
function SchoolHeader({ session, date, route }) {
  const routeName = route && route !== '0'
    ? ROUTES.find(r => r.id === route)?.label
    : 'All Routes'

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
        <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-100 dark:bg-amber-500/15 border border-amber-200 dark:border-amber-500/25 text-[12px] font-bold text-amber-700 dark:text-amber-400">
          <CalendarDays className="w-3.5 h-3.5" /> Session: {session}
        </span>
        {date && (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-blue-100 dark:bg-blue-500/15 border border-blue-200 dark:border-blue-500/25 text-[12px] font-bold text-blue-700 dark:text-blue-400">
            <Clock className="w-3.5 h-3.5" /> Date: {date}
          </span>
        )}
        {routeName && (
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-violet-100 dark:bg-violet-500/15 border border-violet-200 dark:border-violet-500/25 text-[12px] font-bold text-violet-700 dark:text-violet-400">
            <Bus className="w-3.5 h-3.5" /> {routeName}
          </span>
        )}
      </div>
      <p className="mt-3 text-[13px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400">
        RFID Transport Attendance Report
      </p>
    </div>
  )
}

// ─── STATUS BADGE ─────────────────────────────────────────────────────────────
function StatusBadge({ status }) {
  const cfg = STATUS_CONFIG[status] ?? STATUS_CONFIG.Absent
  const Icon = cfg.icon
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-bold ${cfg.badge}`}>
      <Icon className="w-3.5 h-3.5" />
      {status}
    </span>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ row, idx, isTotal, totalPresent, totalAbsent, totalRows }) {
  if (isTotal) {
    return (
      <tr className="bg-blue-50 dark:bg-indigo-500/[0.07] border-t-2 border-blue-200 dark:border-indigo-500/30">
        <td className="px-4 py-3 text-center text-[12px] text-blue-500 dark:text-blue-400">—</td>
        <td className="px-4 py-3" colSpan={7}>
          <span className="text-[13px] font-bold text-blue-700 dark:text-blue-300 flex items-center gap-2">
            <TrendingUp className="w-4 h-4" /> Grand Total — {totalRows} Students
          </span>
        </td>
        <td className="px-4 py-3 text-center">
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-[12px] font-bold bg-emerald-100 text-emerald-800 dark:bg-emerald-500/20 dark:text-emerald-300">
            <UserCheck className="w-3.5 h-3.5" />{totalPresent}
          </span>
        </td>
        <td className="px-4 py-3 text-center">
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-[12px] font-bold bg-rose-100 text-rose-800 dark:bg-rose-500/20 dark:text-rose-300">
            <UserX className="w-3.5 h-3.5" />{totalAbsent}
          </span>
        </td>
      </tr>
    )
  }

  const { fg, bg } = routeColor(row.route)
  const isPresent = row.status === 'Present'

  return (
    <tr className={`border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] transition-colors
      ${isPresent
        ? 'hover:bg-emerald-50/30 dark:hover:bg-emerald-500/[0.03]'
        : 'hover:bg-rose-50/30 dark:hover:bg-rose-500/[0.03] bg-rose-50/20 dark:bg-rose-500/[0.02]'
      }`}
    >
      {/* S.No */}
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-10">{idx}</td>

      {/* Student Name */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-2.5">
          <span className={`w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center text-[11px] font-bold
            ${isPresent
              ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/20 dark:text-emerald-400'
              : 'bg-rose-100 text-rose-700 dark:bg-rose-500/20 dark:text-rose-400'
            }`}
          >
            {row.name.split(' ').map(n => n[0]).join('').slice(0, 2)}
          </span>
          <div>
            <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{row.name}</p>
            <p className="text-[11px] text-slate-400 dark:text-slate-500">{row.roll}</p>
          </div>
        </div>
      </td>

      {/* Class + Section */}
      <td className="px-4 py-3 text-center">
        <div className="flex flex-col items-center gap-0.5">
          <span className="text-[12px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{row.cls}</span>
          <span className="inline-flex items-center justify-center w-6 h-6 rounded-md text-[11px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-500 dark:text-slate-400">
            {row.section}
          </span>
        </div>
      </td>

      {/* Route */}
      <td className="px-4 py-3">
        <span
          className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-bold whitespace-nowrap"
          style={{ background: bg, color: fg }}
        >
          <Bus className="w-3 h-3" />
          {routeLabel(row.route)}
        </span>
      </td>

      {/* Stop */}
      <td className="px-4 py-3">
        <span className="flex items-center gap-1 text-[12px] text-slate-600 dark:text-slate-300 whitespace-nowrap">
          <Navigation className="w-3 h-3 text-slate-400 flex-shrink-0" />
          {row.stop}
        </span>
      </td>

      {/* RFID Tag */}
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center gap-1 text-[11px] font-mono font-semibold text-violet-700 dark:text-violet-400 bg-violet-50 dark:bg-violet-500/10 px-2 py-1 rounded-lg">
          <Radio className="w-3 h-3" />{row.rfid}
        </span>
      </td>

      {/* In Time */}
      <td className="px-4 py-3 text-center">
        {row.inTime
          ? <span className="text-[12px] font-semibold text-emerald-700 dark:text-emerald-400 tabular-nums">{row.inTime}</span>
          : <span className="text-[12px] text-slate-300 dark:text-slate-700">—</span>
        }
      </td>

      {/* Out Time */}
      <td className="px-4 py-3 text-center">
        {row.outTime
          ? <span className="text-[12px] font-semibold text-amber-700 dark:text-amber-400 tabular-nums">{row.outTime}</span>
          : <span className="text-[12px] text-slate-300 dark:text-slate-700">—</span>
        }
      </td>

      {/* Status */}
      <td className="px-4 py-3 text-center"><StatusBadge status={row.status} /></td>

      {/* Absent placeholder (for colspan alignment) */}
      <td className="hidden" />
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────
function MobileCard({ row }) {
  const [expanded, setExpanded] = useState(false)
  const { fg, bg } = routeColor(row.route)
  const isPresent = row.status === 'Present'

  return (
    <div className={`rounded-xl border overflow-hidden shadow-sm transition-colors
      ${isPresent
        ? 'border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35]'
        : 'border-rose-200 dark:border-rose-500/20 bg-rose-50/30 dark:bg-rose-500/[0.03]'
      }`}
    >
      {/* Card Header */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left"
      >
        {/* Avatar */}
        <span className={`w-10 h-10 rounded-xl flex-shrink-0 flex items-center justify-center text-[12px] font-bold
          ${isPresent
            ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/20 dark:text-emerald-400'
            : 'bg-rose-100 text-rose-700 dark:bg-rose-500/20 dark:text-rose-400'
          }`}
        >
          {row.name.split(' ').map(n => n[0]).join('').slice(0, 2)}
        </span>

        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 flex-wrap">
            <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight">{row.name}</p>
            <StatusBadge status={row.status} />
          </div>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5 flex items-center gap-1.5 flex-wrap">
            <span>{row.roll}</span>
            <span>·</span>
            <span>{row.cls} – Sec {row.section}</span>
          </p>
        </div>

        <span className={`w-5 h-5 flex items-center justify-center text-slate-400 transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Quick info row */}
      <div className="px-4 pb-3 flex items-center gap-3 flex-wrap">
        <span
          className="inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[11px] font-bold"
          style={{ background: bg, color: fg }}
        >
          <Bus className="w-3 h-3" /> {routeLabel(row.route)}
        </span>
        <span className="flex items-center gap-1 text-[11px] text-slate-400 dark:text-slate-500">
          <Navigation className="w-3 h-3" /> {row.stop}
        </span>
        {row.inTime && (
          <span className="flex items-center gap-1 text-[11px] font-semibold text-emerald-600 dark:text-emerald-400">
            <Clock className="w-3 h-3" /> In: {row.inTime}
          </span>
        )}
      </div>

      {/* Expanded Details */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 pt-3 pb-4 space-y-3">
          <div className="grid grid-cols-2 gap-2">
            {/* RFID */}
            <div className="rounded-xl bg-violet-50 dark:bg-violet-500/10 border border-violet-100 dark:border-violet-500/20 p-3 text-center col-span-2">
              <Radio className="w-4 h-4 text-violet-600 dark:text-violet-400 mx-auto mb-1" />
              <p className="text-[14px] font-mono font-bold text-violet-700 dark:text-violet-300">{row.rfid}</p>
              <p className="text-[10px] font-bold uppercase tracking-wide text-violet-600 dark:text-violet-400 mt-0.5">RFID Tag</p>
            </div>

            {/* In Time */}
            <div className="rounded-xl bg-emerald-50 dark:bg-emerald-500/10 border border-emerald-100 dark:border-emerald-500/20 p-3 text-center">
              <Clock className="w-4 h-4 text-emerald-600 dark:text-emerald-400 mx-auto mb-1" />
              <p className="text-[16px] font-bold text-emerald-700 dark:text-emerald-300 tabular-nums leading-tight">
                {row.inTime || '—'}
              </p>
              <p className="text-[10px] font-bold uppercase tracking-wide text-emerald-600 dark:text-emerald-400 mt-0.5">In Time</p>
            </div>

            {/* Out Time */}
            <div className="rounded-xl bg-amber-50 dark:bg-amber-500/10 border border-amber-100 dark:border-amber-500/20 p-3 text-center">
              <Clock className="w-4 h-4 text-amber-600 dark:text-amber-400 mx-auto mb-1" />
              <p className="text-[16px] font-bold text-amber-700 dark:text-amber-300 tabular-nums leading-tight">
                {row.outTime || '—'}
              </p>
              <p className="text-[10px] font-bold uppercase tracking-wide text-amber-600 dark:text-amber-400 mt-0.5">Out Time</p>
            </div>
          </div>

          {/* Route + Stop */}
          <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-700/50 p-3 flex items-start gap-3">
            <Bus className="w-4 h-4 text-blue-500 flex-shrink-0 mt-0.5" />
            <div>
              <p className="text-[12px] font-bold text-slate-700 dark:text-slate-200">
                {ROUTES.find(r => r.id === row.route)?.label}
              </p>
              <p className="text-[11px] text-slate-400 dark:text-slate-500 flex items-center gap-1 mt-0.5">
                <Navigation className="w-3 h-3" /> Stop: {row.stop}
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, session, setSession, route, setRoute, date, setDate, onShow, loading, errors }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-3xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl"
        style={{ animation: 'drawerUp .25s ease' }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
        <div className="flex justify-center pt-3 pb-1">
          <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
        </div>
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Attendance Filters</span>
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
          <Field label="Route">
            <NativeSelect value={route} onChange={e => setRoute(e.target.value)}>
              {ROUTES.map(r => <option key={r.id} value={r.id}>{r.label}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Date">
            <input
              type="date"
              value={date}
              onChange={e => setDate(e.target.value)}
              className="w-full px-3 py-2 text-[13px] rounded-lg border border-slate-200 dark:border-[rgba(99,102,241,0.25)] outline-none
                bg-white text-slate-800 dark:bg-[#1e2238] dark:text-slate-200
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100 dark:focus:border-indigo-400"
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

// ─── MOBILE STATUS TAB ────────────────────────────────────────────────────────
function MobileStatusTab({ active, onClick, label, count, color }) {
  const colors = {
    all:     active ? 'bg-blue-600 text-white shadow-md shadow-blue-500/25' : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400',
    present: active ? 'bg-emerald-600 text-white shadow-md shadow-emerald-500/25' : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400',
    absent:  active ? 'bg-rose-600 text-white shadow-md shadow-rose-500/25' : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400',
  }
  return (
    <button onClick={onClick}
      className={`flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl text-[12px] font-bold transition-all ${colors[color]}`}>
      {label}
      <span className={`text-[11px] px-1.5 py-0.5 rounded-full font-bold
        ${active ? 'bg-white/25' : 'bg-slate-200 text-slate-600 dark:bg-slate-700 dark:text-slate-400'}`}>
        {count}
      </span>
    </button>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function RFIDTransportStudentAttendance() {
  // ── Filter state ────────────────────────────────────────────────────────────
  const [session,    setSession]    = useState('')
  const [route,      setRoute]      = useState('0')
  const [date,       setDate]       = useState('')

  // ── Report state ────────────────────────────────────────────────────────────
  const [rows,       setRows]       = useState([])
  const [loading,    setLoading]    = useState(false)
  const [shown,      setShown]      = useState(false)
  const [shownMeta,  setShownMeta]  = useState({ session: '', route: '', date: '' })

  // ── UI state ────────────────────────────────────────────────────────────────
  const [filterOpen, setFilterOpen] = useState(false)
  const [search,     setSearch]     = useState('')
  const [mobileTab,  setMobileTab]  = useState('all') // 'all' | 'Present' | 'Absent'
  const [errors,     setErrors]     = useState({})
  const [toast,      setToast]      = useState(null)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Show report ─────────────────────────────────────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}
    if (!session) err.session = 'Please select a session'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')
    setMobileTab('all')

    setTimeout(() => {
      // Filter dummy data by route if selected
      let data = [...ATTENDANCE_ROWS]
      if (route && route !== '0') data = data.filter(r => r.route === route)
      setRows(data)
      setShownMeta({ session, route, date })
      setShown(true)
      setLoading(false)
      showToast(`Loaded ${data.length} student records.`)
    }, 700)
  }, [session, route, date])

  const handleReset = () => {
    setSession(''); setRoute('0'); setDate('')
    setRows([]); setSearch(''); setErrors({})
    setShown(false); setShownMeta({ session: '', route: '', date: '' })
    setMobileTab('all')
  }

  // ── Filtered rows (search + mobile tab) ─────────────────────────────────────
  const searched = useMemo(() => {
    if (!search) return rows
    const q = search.toLowerCase()
    return rows.filter(r =>
      r.name.toLowerCase().includes(q) ||
      r.roll.toLowerCase().includes(q) ||
      r.cls.toLowerCase().includes(q) ||
      r.section.toLowerCase().includes(q) ||
      r.stop.toLowerCase().includes(q) ||
      r.rfid.toLowerCase().includes(q)
    )
  }, [rows, search])

  const filtered = useMemo(() => {
    if (mobileTab === 'all') return searched
    return searched.filter(r => r.status === mobileTab)
  }, [searched, mobileTab])

  // ── Stats ───────────────────────────────────────────────────────────────────
  const stats = useMemo(() => {
    const present = searched.filter(r => r.status === 'Present').length
    const absent  = searched.filter(r => r.status === 'Absent').length
    const pct     = searched.length ? Math.round((present / searched.length) * 100) : 0
    return { total: searched.length, present, absent, pct }
  }, [searched])

  const hasResults    = shown && rows.length > 0
  const activeFilters = [session, route !== '0' ? route : null, date].filter(Boolean).length

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ──────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Bus className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            RFID Attendance Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Transport route-wise daily student attendance via RFID scan.
          </p>
        </div>
        {hasResults && (
          <div className="hidden sm:flex items-center gap-2 px-4 py-2 rounded-xl bg-emerald-50 dark:bg-emerald-500/10 border border-emerald-200 dark:border-emerald-500/20 text-[13px] font-bold text-emerald-700 dark:text-emerald-400 flex-shrink-0">
            <BadgeCheck className="w-4 h-4" />
            {stats.pct}% Attendance
          </div>
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

            {/* Route */}
            <Field label="Select Route">
              <NativeSelect value={route} onChange={e => setRoute(e.target.value)}>
                {ROUTES.map(r => <option key={r.id} value={r.id}>{r.label}</option>)}
              </NativeSelect>
            </Field>

            {/* Date */}
            <Field label="Enter Date">
              <input
                type="date"
                value={date}
                onChange={e => setDate(e.target.value)}
                className="w-full px-3 py-2 text-[13px] rounded-lg border border-slate-200 dark:border-[rgba(99,102,241,0.25)] outline-none
                  bg-white text-slate-800 dark:bg-[#1e2238] dark:text-slate-200
                  focus:border-blue-400 focus:ring-2 focus:ring-blue-100 dark:focus:border-indigo-400"
              />
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
                title="Reset filters"
              >
                <RefreshCw className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* ── MOBILE Filter Bar ──────────────────────────────────────────────── */}
      <div className="flex sm:hidden gap-2">
        <button type="button" onClick={() => setFilterOpen(true)}
          className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
            bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {session ? `${session}${route !== '0' ? ` · ${routeLabel(route)}` : ''}` : 'Set Filters'}
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
        session={session}       setSession={setSession}
        route={route}           setRoute={setRoute}
        date={date}             setDate={setDate}
        onShow={handleShow}
        loading={loading}
        errors={errors}
      />

      {/* ── Loading Skeleton ──────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          <div className="h-20 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-4" />
          {[...Array(6)].map((_, i) => (
            <div key={i} className="h-14 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.12 }} />
          ))}
        </div>
      )}

      {/* ── Results ────────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School Header */}
          <SchoolHeader session={shownMeta.session} date={shownMeta.date} route={shownMeta.route} />

          {/* Summary Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={Users}     label="Total Students"  value={stats.total}   color="blue"    />
            <SummaryCard icon={UserCheck} label="Present"         value={stats.present} color="emerald" subtitle={`${stats.pct}% attendance`} />
            <SummaryCard icon={UserX}     label="Absent"          value={stats.absent}  color="rose"    />
            <SummaryCard icon={Radio}     label="RFID Scanned"    value={stats.present} color="violet"  />
          </div>

          {/* Attendance bar */}
          <div className="rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-white dark:bg-[#1a1f35] px-5 py-3 shadow-sm">
            <div className="flex justify-between text-[11px] font-semibold mb-1.5">
              <span className="text-emerald-600 dark:text-emerald-400 flex items-center gap-1"><UserCheck className="w-3 h-3" /> Present {stats.pct}%</span>
              <span className="text-rose-500 dark:text-rose-400 flex items-center gap-1"><UserX className="w-3 h-3" /> Absent {100 - stats.pct}%</span>
            </div>
            <div className="h-2 rounded-full bg-rose-100 dark:bg-rose-500/20 overflow-hidden">
              <div className="h-full rounded-full bg-emerald-500 transition-all duration-700" style={{ width: `${stats.pct}%` }} />
            </div>
          </div>

          {/* Results Card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <BookOpen className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Student Attendance</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownMeta.session}</span>
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
                  placeholder="Search student, class, stop…"
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

            {/* Info hint (desktop) */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
              <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <p className="text-[12px] text-blue-700 dark:text-blue-400">
                Green rows = Present · Red tint = Absent · In/Out Time recorded via RFID scan at bus stop.
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
                  <thead className="sticky top-0 z-10">
                    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50 dark:bg-[#1a1f35]">
                      {['S.No.', 'Student', 'Class', 'Route', 'Stop', 'RFID Tag', 'In Time', 'Out Time', 'Status'].map((h, i) => (
                        <th key={i} className="px-4 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:w-10">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((row, i) => (
                      <DesktopRow key={row.id} row={row} idx={i + 1} />
                    ))}
                    {/* Grand Total row */}
                    <DesktopRow
                      isTotal
                      totalPresent={stats.present}
                      totalAbsent={stats.absent}
                      totalRows={filtered.length}
                    />
                  </tbody>
                </table>
              )}
            </div>

            {/* ── MOBILE CARDS ── */}
            <div className="md:hidden">
              {/* Status Tabs */}
              <div className="flex gap-2 p-4 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
                <MobileStatusTab active={mobileTab === 'all'}     onClick={() => setMobileTab('all')}     label="All"     count={searched.length}  color="all"     />
                <MobileStatusTab active={mobileTab === 'Present'} onClick={() => setMobileTab('Present')} label="Present" count={stats.present}     color="present" />
                <MobileStatusTab active={mobileTab === 'Absent'}  onClick={() => setMobileTab('Absent')}  label="Absent"  count={stats.absent}      color="absent"  />
              </div>

              <div className="p-4 space-y-3">
                {filtered.length === 0 ? (
                  <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                    <Search className="w-6 h-6 opacity-40" />
                    <span className="text-[13px]">No records match your search.</span>
                  </div>
                ) : (
                  <>
                    <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5 pb-1">
                      <Info className="w-3.5 h-3.5 flex-shrink-0" />
                      Tap a card to see RFID scan time details.
                    </p>

                    {filtered.map(row => (
                      <MobileCard key={row.id} row={row} />
                    ))}

                    {/* Mobile Grand Total */}
                    <div className="rounded-xl border-2 border-blue-200 dark:border-indigo-500/30 bg-blue-50 dark:bg-indigo-500/[0.07] p-4">
                      <p className="text-[12px] font-bold uppercase tracking-wide text-blue-700 dark:text-blue-400 mb-3 flex items-center gap-2">
                        <TrendingUp className="w-4 h-4" /> Summary — {filtered.length} Students
                      </p>
                      <div className="grid grid-cols-3 gap-2">
                        <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                          <p className="text-[22px] font-bold text-blue-700 dark:text-blue-300 tabular-nums">{stats.total}</p>
                          <p className="text-[10px] font-semibold text-blue-600 dark:text-blue-400">Total</p>
                        </div>
                        <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                          <p className="text-[22px] font-bold text-emerald-700 dark:text-emerald-300 tabular-nums">{stats.present}</p>
                          <p className="text-[10px] font-semibold text-emerald-600 dark:text-emerald-400">Present</p>
                        </div>
                        <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                          <p className="text-[22px] font-bold text-rose-700 dark:text-rose-300 tabular-nums">{stats.absent}</p>
                          <p className="text-[10px] font-semibold text-rose-600 dark:text-rose-400">Absent</p>
                        </div>
                      </div>
                      {/* Attendance bar */}
                      <div className="mt-3">
                        <div className="flex text-[10px] font-semibold justify-between mb-1">
                          <span className="text-emerald-600 dark:text-emerald-400">Present {stats.pct}%</span>
                          <span className="text-rose-500 dark:text-rose-400">Absent {100 - stats.pct}%</span>
                        </div>
                        <div className="h-1.5 rounded-full bg-rose-200 dark:bg-rose-500/20 overflow-hidden">
                          <div className="h-full rounded-full bg-emerald-500 transition-all duration-700" style={{ width: `${stats.pct}%` }} />
                        </div>
                      </div>
                    </div>
                  </>
                )}
              </div>
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

      {/* ── Empty State ────────────────────────────────────────────────────── */}
      {!hasResults && !loading && (
        <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400 dark:text-slate-600">
          <div className="w-16 h-16 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
            <Bus className="w-8 h-8 opacity-40" />
          </div>
          <div className="text-center">
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No report generated yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select a session, route and date, then click <strong>Show</strong> to generate the attendance report.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
