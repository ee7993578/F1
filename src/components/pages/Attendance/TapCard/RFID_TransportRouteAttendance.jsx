/**
 * RFID_TransportRouteAttendance.jsx
 * Folder: src/pages/Transport/RFID_TransportRouteAttendance.jsx
 *
 * Converts legacy ASPX "RFID Transport Route Attendance Report" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session dropdown + Date picker filter
 *  - Show Report button
 *  - School name / address / session / date header in report
 *  - Route-wise RFID attendance table with student-level detail
 *  - Present / Absent / Total summary per route
 *  - Grand total footer row
 *  - Mobile: collapsible route cards with student list
 *  - Desktop: dense ERP-style table with expand per route
 *  - Export Excel placeholder
 *  - Toast notifications, loading skeleton, empty state
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Filter, RefreshCw, Eye,
  AlertCircle, X, Check, Loader2, ChevronDown,
  ChevronRight, Bus, MapPin, Building2,
  SlidersHorizontal, Info, Search,
  FileSpreadsheet, Users, UserCheck, UserX,
  Calendar, Clock, Wifi, Shield,
  TrendingUp, BookOpen, BarChart3,
  CheckCircle2, XCircle, MinusCircle
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ───────────────────────────────────────────────────────

const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const SCHOOL_INFO = {
  name: 'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

// Status badge config
const STATUS = {
  P: { label: 'Present', color: 'emerald', icon: CheckCircle2 },
  A: { label: 'Absent',  color: 'rose',    icon: XCircle      },
  L: { label: 'Late',    color: 'amber',   icon: MinusCircle  },
}

// Dummy route-wise RFID attendance data
const ROUTES = [
  { id: 1, route: 'Route 01 – Rajpur Road',    vehicle: 'UK-07-1234', driver: 'Ramesh Kumar',   helper: 'Suresh Lal'   },
  { id: 2, route: 'Route 02 – Clement Town',   vehicle: 'UK-07-5678', driver: 'Mohan Singh',    helper: 'Ravi Yadav'   },
  { id: 3, route: 'Route 03 – Prem Nagar',     vehicle: 'UK-07-9101', driver: 'Anil Verma',     helper: 'Dinesh Negi'  },
  { id: 4, route: 'Route 04 – Dalanwala',      vehicle: 'UK-07-1121', driver: 'Vijay Rawat',    helper: 'Ashok Thapa'  },
  { id: 5, route: 'Route 05 – Haridwar Road',  vehicle: 'UK-07-3141', driver: 'Satish Bisht',   helper: 'Gopal Dhiman' },
]

// Generate students per route
const STUDENTS_BY_ROUTE = {
  1: [
    { roll: 'SVM001', name: 'Aarav Sharma',       class: 'Class X-A',   time: '07:42 AM', status: 'P' },
    { roll: 'SVM002', name: 'Priya Dobhal',        class: 'Class IX-B',  time: '07:43 AM', status: 'P' },
    { roll: 'SVM003', name: 'Rohan Negi',          class: 'Class VIII-A',time: '—',        status: 'A' },
    { roll: 'SVM004', name: 'Sneha Rawat',         class: 'Class VII-A', time: '07:45 AM', status: 'P' },
    { roll: 'SVM005', name: 'Karan Thapa',         class: 'Class VI-B',  time: '07:47 AM', status: 'L' },
    { roll: 'SVM006', name: 'Ananya Bisht',        class: 'Class X-A',   time: '07:44 AM', status: 'P' },
    { roll: 'SVM007', name: 'Vikram Uniyal',       class: 'Class XI-A',  time: '—',        status: 'A' },
    { roll: 'SVM008', name: 'Ritika Joshi',        class: 'Class XII-B', time: '07:46 AM', status: 'P' },
  ],
  2: [
    { roll: 'SVM009', name: 'Deepak Pokhriyal',   class: 'Class IX-A',  time: '07:55 AM', status: 'P' },
    { roll: 'SVM010', name: 'Pooja Semwal',        class: 'Class VIII-A',time: '07:56 AM', status: 'P' },
    { roll: 'SVM011', name: 'Harsh Dimri',         class: 'Class XI-B',  time: '—',        status: 'A' },
    { roll: 'SVM012', name: 'Kavya Gusain',        class: 'Class VII-A', time: '07:58 AM', status: 'P' },
    { roll: 'SVM013', name: 'Nikhil Bahuguna',     class: 'Class X-A',   time: '08:01 AM', status: 'L' },
    { roll: 'SVM014', name: 'Simran Arora',        class: 'Class VI-A',  time: '07:57 AM', status: 'P' },
  ],
  3: [
    { roll: 'SVM015', name: 'Tarun Chamoli',       class: 'Class XII-A', time: '08:10 AM', status: 'P' },
    { roll: 'SVM016', name: 'Neha Naithani',       class: 'Class X-B',   time: '08:11 AM', status: 'P' },
    { roll: 'SVM017', name: 'Amit Rana',           class: 'Class IX-B',  time: '—',        status: 'A' },
    { roll: 'SVM018', name: 'Divya Kimothi',       class: 'Class VIII-A',time: '08:13 AM', status: 'P' },
    { roll: 'SVM019', name: 'Saurabh Tiwari',      class: 'Class VII-A', time: '08:14 AM', status: 'P' },
    { roll: 'SVM020', name: 'Megha Kapoor',        class: 'Class XI-A',  time: '08:12 AM', status: 'P' },
    { roll: 'SVM021', name: 'Ankit Belwal',        class: 'Class VI-B',  time: '—',        status: 'A' },
  ],
  4: [
    { roll: 'SVM022', name: 'Riya Kandpal',        class: 'Class X-A',   time: '08:05 AM', status: 'P' },
    { roll: 'SVM023', name: 'Sumit Bangari',       class: 'Class IX-A',  time: '08:06 AM', status: 'P' },
    { roll: 'SVM024', name: 'Prateek Lakhera',     class: 'Class XII-B', time: '08:07 AM', status: 'L' },
    { roll: 'SVM025', name: 'Khushi Negi',         class: 'Class VII-B', time: '08:08 AM', status: 'P' },
    { roll: 'SVM026', name: 'Abhishek Panwar',     class: 'Class VIII-A',time: '—',        status: 'A' },
    { roll: 'SVM027', name: 'Pallavi Rautela',     class: 'Class XI-B',  time: '08:06 AM', status: 'P' },
    { roll: 'SVM028', name: 'Yash Dhaundiyal',     class: 'Class VI-A',  time: '08:09 AM', status: 'P' },
    { roll: 'SVM029', name: 'Tanvi Barthwal',      class: 'Class X-B',   time: '—',        status: 'A' },
    { roll: 'SVM030', name: 'Mohit Butola',        class: 'Class IX-B',  time: '08:07 AM', status: 'P' },
  ],
  5: [
    { roll: 'SVM031', name: 'Shivangi Badoni',     class: 'Class XII-A', time: '08:20 AM', status: 'P' },
    { roll: 'SVM032', name: 'Vishal Aswal',        class: 'Class XI-A',  time: '—',        status: 'A' },
    { roll: 'SVM033', name: 'Pallak Rana',         class: 'Class X-A',   time: '08:22 AM', status: 'P' },
    { roll: 'SVM034', name: 'Gaurav Pangti',       class: 'Class IX-A',  time: '08:21 AM', status: 'P' },
    { roll: 'SVM035', name: 'Kritika Bhandari',    class: 'Class VIII-A',time: '08:23 AM', status: 'L' },
    { roll: 'SVM036', name: 'Lalit Sati',          class: 'Class VII-A', time: '08:24 AM', status: 'P' },
  ],
}

// ─── HELPERS ──────────────────────────────────────────────────────────────────

// Format a Date object to "dd MMM yyyy" display string
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  try {
    const d = new Date(dateStr)
    return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
  } catch { return dateStr }
}

// Compute route summary from student list
const routeSummary = (students) => {
  const present = students.filter(s => s.status === 'P').length
  const absent  = students.filter(s => s.status === 'A').length
  const late    = students.filter(s => s.status === 'L').length
  return { present, absent, late, total: students.length }
}

// Status color classes
const statusClasses = {
  P: {
    badge: 'bg-emerald-50 text-emerald-700 border border-emerald-200 dark:bg-emerald-500/10 dark:text-emerald-400 dark:border-emerald-500/20',
    dot:   'bg-emerald-500',
  },
  A: {
    badge: 'bg-rose-50 text-rose-700 border border-rose-200 dark:bg-rose-500/10 dark:text-rose-400 dark:border-rose-500/20',
    dot:   'bg-rose-500',
  },
  L: {
    badge: 'bg-amber-50 text-amber-700 border border-amber-200 dark:bg-amber-500/10 dark:text-amber-400 dark:border-amber-500/20',
    dot:   'bg-amber-400',
  },
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
            ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20'
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

function DateInput({ value, onChange, error }) {
  return (
    <div className="relative">
      <input
        type="date"
        value={value}
        onChange={onChange}
        className={`w-full pl-3 pr-8 py-2 text-[13px] rounded-lg border outline-none transition-all cursor-pointer
          bg-white text-slate-800
          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
          dark:bg-[#1e2238] dark:text-slate-200 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
          dark:[color-scheme:dark]
          ${error
            ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20'
            : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
          }`}
      />
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

// ─── SCHOOL HEADER BANNER ─────────────────────────────────────────────────────
function SchoolHeader({ session, date }) {
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
        {date && (
          <div className="inline-flex items-center gap-1.5 px-4 py-1 rounded-full bg-blue-100 dark:bg-blue-500/15 border border-blue-200 dark:border-blue-500/25">
            <Calendar className="w-3 h-3 text-blue-600 dark:text-blue-400" />
            <span className="text-[12px] font-bold text-blue-700 dark:text-blue-400">{formatDate(date)}</span>
          </div>
        )}
      </div>
      <p className="mt-2 text-[13px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400 flex items-center justify-center gap-2">
        <Wifi className="w-4 h-4" />
        RFID Transport Route Attendance Report
      </p>
    </div>
  )
}

// ─── STATUS BADGE ─────────────────────────────────────────────────────────────
function StatusBadge({ status }) {
  const cfg = STATUS[status] || STATUS['A']
  return (
    <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-[11px] font-bold ${statusClasses[status]?.badge || statusClasses['A'].badge}`}>
      <span className={`w-1.5 h-1.5 rounded-full flex-shrink-0 ${statusClasses[status]?.dot || 'bg-slate-400'}`} />
      {cfg.label}
    </span>
  )
}

// ─── DESKTOP STUDENT ROWS (sub-table inside expanded route) ───────────────────
function StudentSubTable({ students }) {
  return (
    <div className="bg-slate-50/60 dark:bg-white/[0.015] border-t border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
      <table className="w-full">
        <thead>
          <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
            {['#', 'Roll No.', 'Student Name', 'Class', 'RFID Time', 'Status'].map((h, i) => (
              <th key={i} className="px-4 py-2 text-left text-[10px] font-bold uppercase tracking-wide text-slate-400 dark:text-slate-500">
                {h}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {students.map((s, i) => (
            <tr key={s.roll} className={`border-b border-slate-100 dark:border-[rgba(99,102,241,0.05)] ${i % 2 === 0 ? '' : 'bg-white/40 dark:bg-white/[0.01]'}`}>
              <td className="px-4 py-2.5 text-[11px] text-slate-400 dark:text-slate-500 tabular-nums w-8">{i + 1}</td>
              <td className="px-4 py-2.5">
                <span className="text-[12px] font-bold text-slate-600 dark:text-slate-300 font-mono">{s.roll}</span>
              </td>
              <td className="px-4 py-2.5">
                <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200">{s.name}</span>
              </td>
              <td className="px-4 py-2.5">
                <span className="text-[12px] text-slate-500 dark:text-slate-400">{s.class}</span>
              </td>
              <td className="px-4 py-2.5">
                <span className={`inline-flex items-center gap-1 text-[12px] font-semibold tabular-nums ${s.status === 'A' ? 'text-slate-400 dark:text-slate-600' : 'text-slate-700 dark:text-slate-300'}`}>
                  <Clock className="w-3 h-3 flex-shrink-0 opacity-60" />
                  {s.time}
                </span>
              </td>
              <td className="px-4 py-2.5">
                <StatusBadge status={s.status} />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

// ─── DESKTOP ROUTE ROW ────────────────────────────────────────────────────────
function DesktopRouteRow({ route, students, sno, isExpanded, onToggle }) {
  const { present, absent, late, total } = routeSummary(students)
  const pct = total ? Math.round((present / total) * 100) : 0

  return (
    <>
      {/* Route Summary Row */}
      <tr
        className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] cursor-pointer hover:bg-blue-50/30 dark:hover:bg-white/[0.015] transition-colors group"
        onClick={onToggle}
      >
        {/* S.No */}
        <td className="px-4 py-3.5 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-10">{sno}</td>

        {/* Route */}
        <td className="px-4 py-3.5">
          <div className="flex items-center gap-2.5">
            <span className="w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center bg-blue-50 dark:bg-blue-500/10">
              <Bus className="w-4 h-4 text-blue-600 dark:text-blue-400" />
            </span>
            <div>
              <p className="text-[13px] font-bold text-slate-700 dark:text-slate-200 leading-tight">{route.route}</p>
              <p className="text-[11px] text-slate-400 dark:text-slate-500">{route.vehicle} · {route.driver}</p>
            </div>
          </div>
        </td>

        {/* Total */}
        <td className="px-4 py-3.5 text-center">
          <span className="inline-flex items-center justify-center w-9 h-9 rounded-xl text-[14px] font-bold bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-200 tabular-nums">{total}</span>
        </td>

        {/* Present */}
        <td className="px-4 py-3.5 text-center">
          <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-400 tabular-nums">{present}</span>
        </td>

        {/* Absent */}
        <td className="px-4 py-3.5 text-center">
          <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-rose-50 text-rose-700 dark:bg-rose-500/10 dark:text-rose-400 tabular-nums">{absent}</span>
        </td>

        {/* Late */}
        <td className="px-4 py-3.5 text-center">
          <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-amber-50 text-amber-700 dark:bg-amber-500/10 dark:text-amber-400 tabular-nums">{late}</span>
        </td>

        {/* Attendance % */}
        <td className="px-4 py-3.5">
          <div className="flex items-center gap-2">
            <div className="flex-1 h-1.5 rounded-full bg-slate-200 dark:bg-slate-700 overflow-hidden min-w-[60px]">
              <div
                className={`h-full rounded-full transition-all duration-500 ${pct >= 80 ? 'bg-emerald-500' : pct >= 60 ? 'bg-amber-400' : 'bg-rose-500'}`}
                style={{ width: `${pct}%` }}
              />
            </div>
            <span className={`text-[12px] font-bold tabular-nums flex-shrink-0 ${pct >= 80 ? 'text-emerald-600 dark:text-emerald-400' : pct >= 60 ? 'text-amber-600 dark:text-amber-400' : 'text-rose-600 dark:text-rose-400'}`}>
              {pct}%
            </span>
          </div>
        </td>

        {/* Expand toggle */}
        <td className="px-4 py-3.5 text-center">
          <span className={`inline-flex items-center justify-center w-7 h-7 rounded-lg bg-blue-50 dark:bg-blue-500/10 text-blue-600 dark:text-blue-400 transition-transform duration-200 ${isExpanded ? 'rotate-90' : ''}`}>
            <ChevronRight className="w-4 h-4" />
          </span>
        </td>
      </tr>

      {/* Expanded Student Sub-table */}
      {isExpanded && (
        <tr>
          <td colSpan={8} className="p-0">
            <StudentSubTable students={students} />
          </td>
        </tr>
      )}
    </>
  )
}

// ─── DESKTOP TOTAL ROW ────────────────────────────────────────────────────────
function DesktopTotalRow({ totals }) {
  const pct = totals.total ? Math.round((totals.present / totals.total) * 100) : 0
  return (
    <tr className="bg-blue-50 dark:bg-indigo-500/[0.07] border-t-2 border-blue-200 dark:border-indigo-500/30">
      <td className="px-4 py-3 text-center text-[12px] text-blue-500 dark:text-blue-400">—</td>
      <td className="px-4 py-3">
        <span className="text-[13px] font-bold text-blue-700 dark:text-blue-300 flex items-center gap-2">
          <TrendingUp className="w-4 h-4" /> Grand Total
        </span>
      </td>
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-slate-200 text-slate-800 dark:bg-slate-700 dark:text-slate-200 tabular-nums">{totals.total}</span>
      </td>
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-emerald-100 text-emerald-800 dark:bg-emerald-500/20 dark:text-emerald-300 tabular-nums">{totals.present}</span>
      </td>
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-rose-100 text-rose-800 dark:bg-rose-500/20 dark:text-rose-300 tabular-nums">{totals.absent}</span>
      </td>
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center justify-center px-3 py-1 rounded-lg text-[13px] font-bold bg-amber-100 text-amber-800 dark:bg-amber-500/20 dark:text-amber-300 tabular-nums">{totals.late}</span>
      </td>
      <td className="px-4 py-3">
        <div className="flex items-center gap-2">
          <div className="flex-1 h-1.5 rounded-full bg-blue-200 dark:bg-blue-900 overflow-hidden min-w-[60px]">
            <div className="h-full rounded-full bg-blue-500" style={{ width: `${pct}%` }} />
          </div>
          <span className="text-[12px] font-bold text-blue-700 dark:text-blue-400 tabular-nums">{pct}%</span>
        </div>
      </td>
      <td />
    </tr>
  )
}

// ─── MOBILE STUDENT LIST (inside expanded card) ───────────────────────────────
function MobileStudentList({ students }) {
  return (
    <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] pt-3 mt-1 space-y-2">
      {students.map((s, i) => (
        <div key={s.roll} className="flex items-center gap-3 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.06)] last:border-0">
          <span className="w-7 h-7 rounded-lg bg-slate-100 dark:bg-slate-800 flex items-center justify-center text-[10px] font-bold text-slate-500 dark:text-slate-400 flex-shrink-0 tabular-nums">{i + 1}</span>
          <div className="flex-1 min-w-0">
            <p className="text-[13px] font-bold text-slate-700 dark:text-slate-200 leading-tight truncate">{s.name}</p>
            <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">{s.roll} · {s.class}</p>
          </div>
          <div className="flex flex-col items-end gap-1 flex-shrink-0">
            <StatusBadge status={s.status} />
            {s.status !== 'A' && (
              <span className="text-[10px] text-slate-400 dark:text-slate-500 font-mono tabular-nums flex items-center gap-0.5">
                <Clock className="w-2.5 h-2.5" />{s.time}
              </span>
            )}
          </div>
        </div>
      ))}
    </div>
  )
}

// ─── MOBILE ROUTE CARD ────────────────────────────────────────────────────────
function MobileRouteCard({ route, students, sno }) {
  const [expanded, setExpanded] = useState(false)
  const { present, absent, late, total } = routeSummary(students)
  const pct = total ? Math.round((present / total) * 100) : 0

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-start gap-3 px-4 py-4 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        <span className="w-9 h-9 rounded-xl bg-blue-50 dark:bg-blue-500/10 flex items-center justify-center flex-shrink-0 mt-0.5">
          <Bus className="w-4 h-4 text-blue-600 dark:text-blue-400" />
        </span>
        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight">{route.route}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">{route.vehicle} · {route.driver}</p>
          {/* Mini stats row */}
          <div className="flex items-center gap-3 mt-2">
            <span className="text-[11px] font-semibold text-emerald-600 dark:text-emerald-400">✓ {present} Present</span>
            <span className="text-[11px] font-semibold text-rose-600 dark:text-rose-400">✗ {absent} Absent</span>
            {late > 0 && <span className="text-[11px] font-semibold text-amber-600 dark:text-amber-400">~ {late} Late</span>}
          </div>
        </div>
        <div className="flex flex-col items-end flex-shrink-0 gap-1">
          <span className="text-[20px] font-bold text-slate-700 dark:text-slate-200 tabular-nums leading-tight">{total}</span>
          <span className="text-[10px] text-slate-400">students</span>
          <span className={`text-[11px] font-bold ${pct >= 80 ? 'text-emerald-600 dark:text-emerald-400' : pct >= 60 ? 'text-amber-600 dark:text-amber-400' : 'text-rose-600 dark:text-rose-400'}`}>{pct}%</span>
        </div>
        <span className={`w-5 h-5 flex items-center justify-center text-slate-400 transition-transform duration-200 flex-shrink-0 mt-1 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Attendance bar */}
      <div className="px-4 pb-3">
        <div className="flex text-[10px] font-semibold justify-between mb-1">
          <span className="text-emerald-600 dark:text-emerald-400">Present {pct}%</span>
          <span className="text-rose-600 dark:text-rose-400">Absent {100 - pct}%</span>
        </div>
        <div className="h-1.5 rounded-full bg-rose-200 dark:bg-rose-500/20 overflow-hidden">
          <div
            className={`h-full rounded-full transition-all duration-500 ${pct >= 80 ? 'bg-emerald-500' : pct >= 60 ? 'bg-amber-400' : 'bg-rose-500'}`}
            style={{ width: `${pct}%` }}
          />
        </div>
      </div>

      {/* Expanded student list */}
      {expanded && (
        <div className="px-4 pb-4">
          <MobileStudentList students={students} />
        </div>
      )}
    </div>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, session, setSession, date, setDate, onShow, loading, errors }) {
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
        <div className="px-5 py-5 space-y-4">
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
          <Field label="Date" error={errors.date}>
            <DateInput value={date} onChange={e => setDate(e.target.value)} error={errors.date} />
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
    <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
      <div className="h-24 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-4" />
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-4">
        {[...Array(4)].map((_, i) => <div key={i} className="h-16 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" />)}
      </div>
      {[...Array(5)].map((_, i) => (
        <div key={i} className="h-14 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.15 }} />
      ))}
    </div>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function RFIDTransportRouteAttendance() {
  const [session,       setSession]       = useState('')
  const [date,          setDate]          = useState('')
  const [reportData,    setReportData]    = useState([])
  const [loading,       setLoading]       = useState(false)
  const [exporting,     setExporting]     = useState(false)
  const [filterOpen,    setFilterOpen]    = useState(false)
  const [search,        setSearch]        = useState('')
  const [errors,        setErrors]        = useState({})
  const [toast,         setToast]         = useState(null)
  const [shown,         setShown]         = useState(false)
  const [shownSession,  setShownSession]  = useState('')
  const [shownDate,     setShownDate]     = useState('')
  const [expandedRoutes, setExpandedRoutes] = useState({})

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Toggle route expansion ────────────────────────────────────────────────
  const toggleRoute = useCallback((id) => {
    setExpandedRoutes(prev => ({ ...prev, [id]: !prev[id] }))
  }, [])

  // ── Fetch / Show Report ───────────────────────────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}
    if (!session) err.session = 'Please select a session'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)
    setSearch('')
    setExpandedRoutes({})

    // Simulate API call
    setTimeout(() => {
      // Attach students to each route
      const data = ROUTES.map(r => ({
        ...r,
        students: STUDENTS_BY_ROUTE[r.id] || [],
      }))
      setReportData(data)
      setShownSession(session)
      setShownDate(date)
      setShown(true)
      setLoading(false)
      const totalStudents = data.reduce((s, r) => s + r.students.length, 0)
      showToast(`Loaded RFID attendance for ${data.length} routes · ${totalStudents} students.`)
    }, 750)
  }, [session, date])

  const handleReset = () => {
    setSession(''); setDate(''); setReportData([])
    setSearch(''); setErrors({}); setShown(false)
    setShownSession(''); setShownDate('')
    setExpandedRoutes({})
  }

  // ── Excel Export placeholder ──────────────────────────────────────────────
  const handleExcel = () => {
    if (reportData.length === 0) { showToast('No data to export. Show report first.', 'error'); return }
    setExporting(true)
    setTimeout(() => { setExporting(false); showToast('Excel export ready! (API integration pending)') }, 1200)
  }

  // ── Search filter ─────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    if (!search) return reportData
    const q = search.toLowerCase()
    return reportData.filter(r =>
      r.route.toLowerCase().includes(q) ||
      r.vehicle.toLowerCase().includes(q) ||
      r.driver.toLowerCase().includes(q) ||
      r.students.some(s => s.name.toLowerCase().includes(q) || s.roll.toLowerCase().includes(q))
    )
  }, [reportData, search])

  // ── Grand totals ──────────────────────────────────────────────────────────
  const grandTotals = useMemo(() => {
    return filtered.reduce((acc, r) => {
      const s = routeSummary(r.students)
      return {
        total:   acc.total   + s.total,
        present: acc.present + s.present,
        absent:  acc.absent  + s.absent,
        late:    acc.late    + s.late,
      }
    }, { total: 0, present: 0, absent: 0, late: 0 })
  }, [filtered])

  const hasResults = shown && reportData.length > 0
  const activeFilters = [session, date].filter(Boolean).length

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Wifi className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            RFID Attendance Report
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Route-wise transport RFID scan attendance — daily tracking.
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
                onChange={e => { setSession(e.target.value); setErrors(p => ({ ...p, session: undefined })) }}
                placeholder="-- Select Session --"
                error={errors.session}
              >
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>

            {/* Date */}
            <Field label="Enter Date">
              <DateInput value={date} onChange={e => setDate(e.target.value)} />
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

      {/* ── MOBILE Filter Bar ────────────────────────────────────────────── */}
      <div className="flex sm:hidden gap-2">
        <button type="button" onClick={() => setFilterOpen(true)}
          className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20">
          <SlidersHorizontal className="w-4 h-4" />
          {session ? `Session: ${session}` : 'Select Filters'}
          {activeFilters > 0 && (
            <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilters}</span>
          )}
        </button>
        {hasResults && (
          <button type="button" onClick={handleExcel} disabled={exporting}
            className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl text-[13px] font-semibold bg-emerald-600 text-white disabled:opacity-70">
            {exporting ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileSpreadsheet className="w-4 h-4" />}
          </button>
        )}
        {hasResults && (
          <button type="button" onClick={handleReset}
            className="px-3.5 py-2.5 rounded-xl bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300">
            <RefreshCw className="w-4 h-4" />
          </button>
        )}
      </div>

      <FilterDrawer
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        session={session} setSession={setSession}
        date={date} setDate={setDate}
        onShow={handleShow}
        loading={loading}
        errors={errors}
      />

      {/* ── Loading Skeleton ─────────────────────────────────────────────── */}
      {loading && <LoadingSkeleton />}

      {/* ── Results ─────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School Header */}
          <SchoolHeader session={shownSession} date={shownDate} />

          {/* Summary Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={Users}      label="Total Students" value={grandTotals.total}   color="blue"    />
            <SummaryCard icon={UserCheck}  label="Present"        value={grandTotals.present} color="emerald" />
            <SummaryCard icon={UserX}      label="Absent"         value={grandTotals.absent}  color="rose"    />
            <SummaryCard icon={Clock}      label="Late Arrivals"  value={grandTotals.late}    color="amber"   />
          </div>

          {/* Results Card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <Bus className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Route-wise RFID Attendance</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownSession}</span>
                {shownDate && (
                  <span className="text-[13px] text-slate-400 dark:text-slate-500">· {formatDate(shownDate)}</span>
                )}
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                  {filtered.length} route{filtered.length !== 1 ? 's' : ''}
                </span>
              </div>

              {/* Search */}
              <div className="relative w-full sm:w-60 flex-shrink-0">
                <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  value={search}
                  onChange={e => setSearch(e.target.value)}
                  placeholder="Search route, driver, student…"
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
                Click any route row to expand and view individual student RFID scan details.
              </p>
            </div>

            {/* ── DESKTOP TABLE ── */}
            <div className="hidden md:block overflow-x-auto">
              {filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No routes match your search.</span>
                </div>
              ) : (
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                      {['S.No.', 'Route / Vehicle', 'Total', 'Present', 'Absent', 'Late', 'Attendance %', ''].map((h, i) => (
                        <th key={i} className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:w-10">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((r, i) => (
                      <DesktopRouteRow
                        key={r.id}
                        route={r}
                        students={r.students}
                        sno={i + 1}
                        isExpanded={!!expandedRoutes[r.id]}
                        onToggle={() => toggleRoute(r.id)}
                      />
                    ))}
                    <DesktopTotalRow totals={grandTotals} />
                  </tbody>
                </table>
              )}
            </div>

            {/* ── MOBILE CARDS ── */}
            <div className="md:hidden p-4 space-y-3">
              {filtered.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
                  <Search className="w-6 h-6 opacity-40" />
                  <span className="text-[13px]">No routes match your search.</span>
                </div>
              ) : (
                <>
                  <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5 pb-1">
                    <Info className="w-3.5 h-3.5 flex-shrink-0" />
                    Tap a route card to see individual student RFID details.
                  </p>

                  {filtered.map((r, i) => (
                    <MobileRouteCard key={r.id} route={r} students={r.students} sno={i + 1} />
                  ))}

                  {/* Mobile Grand Total */}
                  <div className="rounded-xl border-2 border-blue-200 dark:border-indigo-500/30 bg-blue-50 dark:bg-indigo-500/[0.07] p-4">
                    <p className="text-[12px] font-bold uppercase tracking-wide text-blue-700 dark:text-blue-400 mb-3 flex items-center gap-2">
                      <TrendingUp className="w-4 h-4" /> Grand Total — {filtered.length} Routes
                    </p>
                    <div className="grid grid-cols-2 gap-2 mb-3">
                      <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                        <p className="text-[22px] font-bold text-blue-700 dark:text-blue-300 tabular-nums">{grandTotals.total}</p>
                        <p className="text-[10px] font-semibold text-blue-600 dark:text-blue-400">Total Students</p>
                      </div>
                      <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                        <p className="text-[22px] font-bold text-emerald-700 dark:text-emerald-300 tabular-nums">{grandTotals.present}</p>
                        <p className="text-[10px] font-semibold text-emerald-600 dark:text-emerald-400">Present</p>
                      </div>
                      <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                        <p className="text-[22px] font-bold text-rose-700 dark:text-rose-300 tabular-nums">{grandTotals.absent}</p>
                        <p className="text-[10px] font-semibold text-rose-600 dark:text-rose-400">Absent</p>
                      </div>
                      <div className="rounded-lg bg-white/70 dark:bg-white/5 p-2.5 text-center">
                        <p className="text-[22px] font-bold text-amber-700 dark:text-amber-300 tabular-nums">{grandTotals.late}</p>
                        <p className="text-[10px] font-semibold text-amber-600 dark:text-amber-400">Late</p>
                      </div>
                    </div>
                    {/* Attendance bar */}
                    <div>
                      <div className="flex text-[10px] font-semibold justify-between mb-1">
                        <span className="text-emerald-600 dark:text-emerald-400">
                          Present {grandTotals.total ? Math.round((grandTotals.present / grandTotals.total) * 100) : 0}%
                        </span>
                        <span className="text-rose-600 dark:text-rose-400">
                          Absent {grandTotals.total ? Math.round((grandTotals.absent / grandTotals.total) * 100) : 0}%
                        </span>
                      </div>
                      <div className="h-1.5 rounded-full bg-rose-200 dark:bg-rose-500/20 overflow-hidden">
                        <div
                          className="h-full rounded-full bg-emerald-500 transition-all duration-500"
                          style={{ width: `${grandTotals.total ? Math.round((grandTotals.present / grandTotals.total) * 100) : 0}%` }}
                        />
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
                <span className="font-semibold text-slate-700 dark:text-slate-300">{reportData.length}</span> routes
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
              Select a session and click <strong>Show</strong> to generate the RFID attendance report.
            </p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
