/**
 * DownloadRegistrationForm.jsx
 * Folder: src/pages/Reports/Registration/DownloadRegistrationForm.jsx
 *
 * Converts legacy ASPX "Download Online Registration Form" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session + Class dropdown filter
 *  - Download button (with loading state)
 *  - Result table: Registration No, Student Name, Father Name, Mobile, Class, DOB, Category, Status, Actions
 *  - Mobile: card-based layout with expandable details
 *  - Desktop: dense ERP-style table
 *  - Search filter on results
 *  - Toast notifications
 *  - Report preview section (placeholder for PDF/RDLC viewer)
 */

import { useState, useMemo, useCallback, useEffect } from 'react'
import { fetchStudentReport } from '../../../../hooks/useStudents'
import { studentApi as api } from '../../../../lib/studentApi'
import { downloadExcel } from '../../../../lib/exportExcel'
import {
  Filter, RefreshCw, Download, AlertCircle, X, Check,
  Loader2, ChevronDown, Search, SlidersHorizontal, Info,
  FileText, School2, User, Phone, Calendar, BookOpen,
  ChevronRight, Eye, Printer, FileDown, Users, ClipboardList,
  GraduationCap, BadgeCheck, Clock, Building2, MapPin,
  BarChart3, TrendingUp, FileSpreadsheet
} from 'lucide-react'

// ─── STATIC DATA ─────────────────────────────────────────────────────────────

const CATEGORIES = ['General', 'OBC', 'SC', 'ST', 'EWS']
const STATUS_LIST = ['Pending', 'Approved', 'Rejected', 'Waitlisted']

// Status color map
const STATUS_STYLE = {
  Pending:    { bg: 'bg-amber-100 dark:bg-amber-500/15',   text: 'text-amber-700 dark:text-amber-400',   dot: 'bg-amber-500'   },
  Approved:   { bg: 'bg-emerald-100 dark:bg-emerald-500/15', text: 'text-emerald-700 dark:text-emerald-400', dot: 'bg-emerald-500' },
  Rejected:   { bg: 'bg-rose-100 dark:bg-rose-500/15',     text: 'text-rose-700 dark:text-rose-400',     dot: 'bg-rose-500'    },
  Waitlisted: { bg: 'bg-blue-100 dark:bg-blue-500/15',     text: 'text-blue-700 dark:text-blue-400',     dot: 'bg-blue-500'    },
}

// Dummy registration data

// LIVE DATA (server-driven; get_registerd_student via API)
const normRow = (r) => ({
  id: r.uid, form_no: String(r.form_no ?? ''), name: r.name || '',
  father: r.father_name || '', mobile: r.Phone || '', class: r.class || '',
  dob: (r.dob || '').slice(0, 10), category: '', status: r.status || '',
  Registration_date: (r.Registration_date || '').slice(0, 10),
})


// ─── HELPER FUNCTIONS ─────────────────────────────────────────────────────────

const CLASS_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' }, { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' }, { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' }, { fg: '#dc2626', bg: '#fee2e2' },
  { fg: '#0369a1', bg: '#e0f2fe' },
]
const classColor = (name = '') => CLASS_COLORS[(name.charCodeAt(0) ?? 0) % CLASS_COLORS.length]
const formatAbbr = (name = '') => name.replace('Class ', '').slice(0, 3).toUpperCase()
const formatDOB  = (dob = '') => {
  const d = new Date(dob)
  return isNaN(d) ? dob : d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
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

// ─── STATUS BADGE ─────────────────────────────────────────────────────────────

function StatusBadge({ status }) {
  const s = STATUS_STYLE[status] || STATUS_STYLE['Pending']
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-bold ${s.bg} ${s.text}`}>
      <span className={`w-1.5 h-1.5 rounded-full flex-shrink-0 ${s.dot}`} />
      {status}
    </span>
  )
}

// ─── SUMMARY STAT CARD ────────────────────────────────────────────────────────

function SummaryCard({ icon: Icon, label, value, color }) {
  const colors = {
    blue:    'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400',
    emerald: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400',
    amber:   'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400',
    violet:  'bg-violet-50 text-violet-600 dark:bg-violet-500/10 dark:text-violet-400',
    rose:    'bg-rose-50 text-rose-600 dark:bg-rose-500/10 dark:text-rose-400',
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

function SchoolHeader({ session, selectedClass }) {
  let schoolName = 'School'
  let schoolAddress = ''
  try {
    const u = JSON.parse(localStorage.getItem('erp_user') || '{}')
    schoolName = u.schoolName || schoolName
    schoolAddress = u.schoolAddress || ''
  } catch { /* ignore */ }
  return (
    <div className="rounded-2xl border border-blue-100 dark:border-[rgba(99,102,241,0.2)] bg-gradient-to-r from-blue-50 via-white to-indigo-50 dark:from-[#1a1f35] dark:via-[#1e2238] dark:to-[#1a1f35] px-6 py-5 text-center shadow-sm">
      <div className="flex items-center justify-center gap-2 mb-1">
        <Building2 className="w-5 h-5 text-blue-600 dark:text-blue-400 flex-shrink-0" />
        <h2 className="text-[16px] sm:text-[18px] font-extrabold text-slate-800 dark:text-slate-100 tracking-tight leading-snug">
          {schoolName}
        </h2>
      </div>
      <div className="flex items-center justify-center gap-1.5 text-[12px] text-slate-500 dark:text-slate-400 mb-2">
        <MapPin className="w-3.5 h-3.5 flex-shrink-0" />
        <span>{schoolAddress}</span>
      </div>
      <div className="flex items-center justify-center gap-2 flex-wrap">
        <div className="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-amber-100 dark:bg-amber-500/15 border border-amber-200 dark:border-amber-500/25">
          <span className="text-[12px] font-bold text-amber-700 dark:text-amber-400">Session: {session}</span>
        </div>
        {selectedClass && selectedClass !== 'All Classes' && (
          <div className="inline-flex items-center gap-2 px-4 py-1 rounded-full bg-blue-100 dark:bg-blue-500/15 border border-blue-200 dark:border-blue-500/25">
            <span className="text-[12px] font-bold text-blue-700 dark:text-blue-400">Class: {selectedClass}</span>
          </div>
        )}
      </div>
      <p className="mt-2 text-[13px] font-bold uppercase tracking-widest text-blue-700 dark:text-indigo-400">
        Online Registration Forms
      </p>
    </div>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────

function DesktopRow({ row, idx, onDownload, onPreview }) {
  const { fg, bg } = classColor(row.class)
  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors group">
      {/* S.No */}
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-10">{idx}</td>

      {/* Reg No */}
      <td className="px-4 py-3">
        <span className="text-[12px] font-bold text-blue-600 dark:text-blue-400 font-mono">{row.id}</span>
      </td>

      {/* Student Name */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-2">
          <span className="w-7 h-7 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold" style={{ background: bg, color: fg }}>
            {row.name.charAt(0)}
          </span>
          <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{row.name}</span>
        </div>
      </td>

      {/* Father Name */}
      <td className="px-4 py-3">
        <span className="text-[12px] text-slate-500 dark:text-slate-400 whitespace-nowrap">{row.father}</span>
      </td>

      {/* Mobile */}
      <td className="px-4 py-3">
        <span className="text-[12px] text-slate-600 dark:text-slate-300 font-mono tabular-nums">{row.mobile}</span>
      </td>

      {/* Class */}
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center px-2 py-1 rounded-lg text-[11px] font-bold" style={{ background: bg, color: fg }}>
          {formatAbbr(row.class)}
        </span>
      </td>

      {/* DOB */}
      <td className="px-4 py-3 text-center">
        <span className="text-[12px] text-slate-500 dark:text-slate-400 whitespace-nowrap">{formatDOB(row.dob)}</span>
      </td>

      {/* Category */}
      <td className="px-4 py-3 text-center">
        <span className="text-[12px] font-semibold text-slate-600 dark:text-slate-300">{row.category}</span>
      </td>

      {/* Status */}
      <td className="px-4 py-3 text-center">
        <StatusBadge status={row.status} />
      </td>

      {/* Actions */}
      <td className="px-4 py-3 text-center">
        <div className="flex items-center justify-center gap-1.5 opacity-80 group-hover:opacity-100 transition-opacity">
          <button
            type="button"
            onClick={() => onPreview(row)}
            title="Preview"
            className="p-1.5 rounded-lg bg-blue-50 text-blue-600 hover:bg-blue-100 dark:bg-blue-500/10 dark:text-blue-400 dark:hover:bg-blue-500/20 transition-colors"
          >
            <Eye className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => onDownload(row)}
            title="Download"
            className="p-1.5 rounded-lg bg-emerald-50 text-emerald-600 hover:bg-emerald-100 dark:bg-emerald-500/10 dark:text-emerald-400 dark:hover:bg-emerald-500/20 transition-colors"
          >
            <FileDown className="w-3.5 h-3.5" />
          </button>
          <button
            type="button"
            onClick={() => onDownload(row)}
            title="Print"
            className="p-1.5 rounded-lg bg-slate-100 text-slate-500 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700 transition-colors"
          >
            <Printer className="w-3.5 h-3.5" />
          </button>
        </div>
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────

function MobileCard({ row, idx, onDownload, onPreview }) {
  const [expanded, setExpanded] = useState(false)
  const { fg, bg } = classColor(row.class)
  const statusStyle = STATUS_STYLE[row.status] || STATUS_STYLE['Pending']

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Card Header */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        {/* Avatar */}
        <span
          className="w-10 h-10 rounded-xl flex-shrink-0 flex items-center justify-center text-[13px] font-bold"
          style={{ background: bg, color: fg }}
        >
          {row.name.charAt(0)}
        </span>

        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 flex-wrap">
            <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{row.name}</p>
            <StatusBadge status={row.status} />
          </div>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">
            <span className="font-mono text-blue-600 dark:text-blue-400">{row.id}</span>
            &nbsp;·&nbsp;
            <span style={{ color: fg }}>{row.class}</span>
          </p>
        </div>

        <span className={`w-5 h-5 flex items-center justify-center text-slate-400 transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Compact info strip */}
      <div className="flex items-center gap-4 px-4 pb-3 flex-wrap">
        <span className="flex items-center gap-1 text-[11px] text-slate-500 dark:text-slate-400">
          <User className="w-3 h-3 flex-shrink-0" />
          {row.father}
        </span>
        <span className="flex items-center gap-1 text-[11px] text-slate-500 dark:text-slate-400">
          <Phone className="w-3 h-3 flex-shrink-0" />
          {row.mobile}
        </span>
      </div>

      {/* Expanded detail */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 pt-3 pb-4 space-y-3">
          {/* Details grid */}
          <div className="grid grid-cols-2 gap-2 text-[12px]">
            <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 p-2.5">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-0.5">Date of Birth</p>
              <p className="font-semibold text-slate-700 dark:text-slate-200">{formatDOB(row.dob)}</p>
            </div>
            <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 p-2.5">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-0.5">Category</p>
              <p className="font-semibold text-slate-700 dark:text-slate-200">{row.category}</p>
            </div>
            <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 p-2.5">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-0.5">Class Applied</p>
              <span className="inline-flex items-center px-2 py-0.5 rounded-lg text-[11px] font-bold" style={{ background: bg, color: fg }}>
                {row.class}
              </span>
            </div>
            <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 p-2.5">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 mb-0.5">Status</p>
              <StatusBadge status={row.status} />
            </div>
          </div>

          {/* Action buttons */}
          <div className="flex gap-2 pt-1">
            <button
              type="button"
              onClick={() => onPreview(row)}
              className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
                bg-blue-50 text-blue-700 hover:bg-blue-100 border border-blue-200
                dark:bg-blue-500/10 dark:text-blue-400 dark:border-blue-500/25 dark:hover:bg-blue-500/20 transition-colors"
            >
              <Eye className="w-4 h-4" /> Preview
            </button>
            <button
              type="button"
              onClick={() => onDownload(row)}
              className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
                bg-emerald-600 text-white hover:bg-emerald-700 shadow-sm transition-colors"
            >
              <FileDown className="w-4 h-4" /> Download
            </button>
            <button
              type="button"
              onClick={() => onDownload(row)}
              className="px-3.5 py-2.5 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors"
            >
              <Printer className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── PREVIEW MODAL ────────────────────────────────────────────────────────────

function PreviewModal({ row, session, onClose, onDownload }) {
  if (!row) return null
  const { fg, bg } = classColor(row.class)
  let schoolName = 'School'
  let schoolAddress = ''
  try {
    const u = JSON.parse(localStorage.getItem('erp_user') || '{}')
    schoolName = u.schoolName || schoolName
    schoolAddress = u.schoolAddress || ''
  } catch { /* ignore */ }

  return (
    <>
      <div className="fixed inset-0 bg-black/50 z-50 backdrop-blur-sm" onClick={onClose} />
      <div className="fixed inset-x-4 top-1/2 -translate-y-1/2 z-50 max-w-lg mx-auto rounded-2xl bg-white dark:bg-[#1a1f35] border border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl overflow-hidden"
        style={{ animation: 'modalIn .2s ease' }}>
        <style>{`@keyframes modalIn{from{opacity:0;transform:translateY(-48%) scale(.97)}to{opacity:1;transform:translateY(-50%) scale(1)}}`}</style>

        {/* Modal Header */}
        <div className="flex items-center justify-between px-5 py-4 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-[#1e2238] dark:to-[#1a1f35]">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl flex items-center justify-center bg-blue-100 dark:bg-blue-500/20">
              <FileText className="w-5 h-5 text-blue-600 dark:text-blue-400" />
            </div>
            <div>
              <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Registration Form</p>
              <p className="text-[11px] text-slate-500 dark:text-slate-400 font-mono">{row.id}</p>
            </div>
          </div>
          <button onClick={onClose} className="p-2 rounded-xl hover:bg-black/5 dark:hover:bg-white/5 text-slate-400 transition-colors">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Preview content */}
        <div className="p-5 space-y-4 max-h-[60vh] overflow-y-auto">
          {/* School info */}
          <div className="text-center pb-3 border-b border-dashed border-slate-200 dark:border-slate-700">
            <p className="text-[15px] font-extrabold text-slate-800 dark:text-slate-100">{schoolName}</p>
            <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">{schoolAddress}</p>
            <p className="text-[12px] font-bold text-blue-700 dark:text-blue-400 mt-1.5">
              ONLINE REGISTRATION FORM – SESSION {session}
            </p>
          </div>

          {/* Student details */}
          <div className="space-y-2">
            {[
              { label: 'Registration No.',  value: row.id,            icon: ClipboardList },
              { label: 'Student Name',      value: row.name,          icon: User          },
              { label: "Father's Name",     value: row.father,        icon: Users         },
              { label: 'Mobile No.',        value: row.mobile,        icon: Phone         },
              { label: 'Class Applied',     value: row.class,         icon: GraduationCap },
              { label: 'Date of Birth',     value: formatDOB(row.dob),icon: Calendar      },
              { label: 'Category',          value: row.category,      icon: BadgeCheck    },
              { label: 'Status',            value: row.status,        icon: Clock         },
            ].map(({ label, value, icon: Icon }) => (
              <div key={label} className="flex items-start gap-3 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] last:border-0">
                <div className="w-6 h-6 rounded-lg bg-slate-100 dark:bg-slate-800 flex items-center justify-center flex-shrink-0 mt-0.5">
                  <Icon className="w-3.5 h-3.5 text-slate-500 dark:text-slate-400" />
                </div>
                <div className="flex-1 flex items-start justify-between gap-2">
                  <span className="text-[12px] text-slate-500 dark:text-slate-400">{label}</span>
                  {label === 'Status'
                    ? <StatusBadge status={value} />
                    : <span className="text-[12px] font-semibold text-slate-700 dark:text-slate-200 text-right">{value}</span>
                  }
                </div>
              </div>
            ))}
          </div>

          {/* Report viewer placeholder */}
          <div className="rounded-xl border-2 border-dashed border-blue-200 dark:border-blue-500/25 bg-blue-50/50 dark:bg-blue-500/[0.04] p-6 text-center">
            <FileText className="w-8 h-8 text-blue-400 dark:text-blue-500 mx-auto mb-2 opacity-60" />
            <p className="text-[12px] font-semibold text-blue-600 dark:text-blue-400">PDF Report Preview</p>
            <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">
              RDLC/PDF report viewer renders here after API integration
            </p>
          </div>
        </div>

        {/* Footer actions */}
        <div className="flex gap-3 px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.01]">
          <button type="button" onClick={onClose}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
            Close
          </button>
          <button type="button" onClick={() => { onDownload(row); onClose() }}
            className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white bg-emerald-600 hover:bg-emerald-700 transition-colors">
            <FileDown className="w-4 h-4" /> Download PDF
          </button>
        </div>
      </div>
    </>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────

function FilterDrawer({ open, onClose, session, setSession, selectedClass, setSelectedClass, sessions, classes, onShow, loading, errors }) {
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
        <div className="px-5 py-5 space-y-4">
          <Field label="Session" error={errors.session} required>
            <NativeSelect
              value={session}
              onChange={e => setSession(e.target.value)}
              placeholder="-- Select Session --"
              error={errors.session}
            >
              {sessions.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Class">
            <NativeSelect
              value={selectedClass}
              onChange={e => setSelectedClass(e.target.value)}
              placeholder="-- All Classes --"
            >
              {['All Classes', ...classes.map(c => c.className)].map(c => <option key={c} value={c}>{c}</option>)}
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
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Download className="w-4 h-4" />}
            Load Records
          </button>
        </div>
      </div>
    </>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────

export default function DownloadRegistrationForm() {
  const [session,       setSession]       = useState('')
  const [selectedClass, setSelectedClass] = useState('')
  const [rows,          setRows]          = useState([])
  const [loading,       setLoading]       = useState(false)
  const [downloading,   setDownloading]   = useState(null) // reg id being downloaded
  const [filterOpen,    setFilterOpen]    = useState(false)
  const [search,        setSearch]        = useState('')
  const [errors,        setErrors]        = useState({})
  const [toast,         setToast]         = useState(null)
  const [shown,         setShown]         = useState(false)
  const [shownSession,  setShownSession]  = useState('')
  const [shownClass,    setShownClass]    = useState('')
  const [previewRow,    setPreviewRow]    = useState(null)
  const [statusFilter,  setStatusFilter]  = useState('All')
  const [sessions,      setSessions]      = useState([])
  const [classes,       setClasses]       = useState([])

  // Sessions + classes (mirrors Page_Load)
  useEffect(() => {
    (async () => {
      try {
        const [sess, sessions, classes] = await Promise.all([
          api('/menu/session').catch(() => ({ session: '' })),
          api('/master/sessions').catch(() => []),
          api('/master/classes').catch(() => []),
        ])
        setSessions((sessions || []).map((s) => s.session))
        setClasses(classes || [])
        if (sess?.session) setSession(sess.session)
      } catch { /* ignore */ }
    })()
  }, [])

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Fetch (mirrors get_registerd_student bind) ───────────────────────────────
  const handleShow = useCallback(() => {
    const err = {}; if (!session) err.session = 'Please select a session'; if (Object.keys(err).length) { setErrors(err); return }
    setErrors({}); setLoading(true); setSearch(''); setStatusFilter('All')
    fetchStudentReport('registration-details', { session }).then(data => { const result = selectedClass && selectedClass !== 'All Classes' ? data.filter(r => r.className === selectedClass || r.class_name === selectedClass) : data; setRows(result); setShownSession(session); setShownClass(selectedClass || 'All Classes'); setShown(true); showToast(result.length + ' registration record(s) loaded.') }).catch(err => showToast(err.message || 'Request failed', 'error')).finally(() => setLoading(false))
  }, [session, selectedClass])

  const handleReset = () => {
    setSession(''); setSelectedClass(''); setRows([]); setSearch('')
    setErrors({}); setShown(false); setShownSession(''); setShownClass(''); setStatusFilter('All')
  }

  // ── Download full registration detail as a printable form file ───────────────
  const handleDownload = useCallback(async (row) => {
    setDownloading(row.id)
    try {
      const detail = await api(`/admission/registrations/${row.id}/${encodeURIComponent(row.form_no)}?session=${encodeURIComponent(session)}`)
      const f = detail.fields || {}
      const esc = (v) => String(v ?? '').replace(/</g, '&lt;')
      const rowsHtml = Object.entries(f)
        .map(([k, v]) => `<tr><td style="border:1px solid #999;padding:4px 8px;font-weight:bold;">${esc(k)}</td><td style="border:1px solid #999;padding:4px 8px;">${esc(v)}</td></tr>`)
        .join('')
      const html = `<html><head><meta charset="utf-8"><title>Registration ${esc(row.form_no)}</title></head><body><h2>Registration Form No: ${esc(row.form_no)} (${esc(session)})</h2><table cellspacing="0">${rowsHtml}</table></body></html>`
      const blob = new Blob([html], { type: 'text/html' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `RegistrationForm_${row.form_no}.html`
      a.click()
      URL.revokeObjectURL(url)
      showToast(`Form for ${row.name} downloaded!`)
    } catch (err) {
      showToast(err.message || 'Download failed', 'error')
    } finally {
      setDownloading(null)
    }
  }, [session])

  // ── Download All (CSV of filtered rows) ───────────────────────────────────────
  const handleDownloadAll = () => {
    if (filtered.length === 0) { showToast('No records to download.', 'error'); return }
    setLoading(true)
    try {
      const head = ['Form No', 'Name', 'Father', 'Mobile', 'Class', 'DOB', 'Status']
      const lines = filtered.map((r) =>
        [r.form_no, r.name, r.father, r.mobile, r.class, r.dob, r.status]
          .map((v) => `"${String(v ?? '').replace(/"/g, '""')}"`).join(','))
      const blob = new Blob([[head.join(','), ...lines].join('\n')], { type: 'text/csv' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `RegistrationForms_${session}.csv`
      a.click()
      URL.revokeObjectURL(url)
      showToast(`${filtered.length} forms downloaded!`)
    } finally {
      setLoading(false)
    }
  }

  const handleExcel = () => {
    if (!filtered.length) { showToast('No records to export.', 'error'); return }
    downloadExcel(`RegistrationForms_${session}`, filtered, [
      { key: 'form_no', label: 'Form No' },
      { key: 'name', label: 'Name' },
      { key: 'father', label: 'Father' },
      { key: 'mobile', label: 'Mobile' },
      { key: 'class', label: 'Class' },
      { key: 'dob', label: 'DOB' },
      { key: 'status', label: 'Status' },
    ])
    showToast(`${filtered.length} records exported to Excel.`)
  }

  // ── Search + Status filter ────────────────────────────────────────────────
  const filtered = useMemo(() => {
    let data = rows
    if (statusFilter !== 'All') data = data.filter(r => r.status === statusFilter)
    if (!search) return data
    const q = search.toLowerCase()
    return data.filter(r =>
      r.name.toLowerCase().includes(q) ||
      r.father.toLowerCase().includes(q) ||
      String(r.id ?? '').toLowerCase().includes(q) ||
      r.mobile.includes(q) ||
      r.class.toLowerCase().includes(q)
    )
  }, [rows, search, statusFilter])

  // ── Summary counts ────────────────────────────────────────────────────────
  const summary = useMemo(() => ({
    total:      rows.length,
    approved:   rows.filter(r => r.status === 'Approved').length,
    pending:    rows.filter(r => r.status === 'Pending').length,
    rejected:   rows.filter(r => r.status === 'Rejected').length,
    waitlisted: rows.filter(r => r.status === 'Waitlisted').length,
  }), [rows])

  const hasResults = shown && rows.length > 0
  const activeFilters = [session, selectedClass && selectedClass !== 'All Classes'].filter(Boolean).length

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ───────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <FileText className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Download Registration Forms
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            View and download online registration forms session &amp; class-wise.
          </p>
        </div>
        {hasResults && (
          <div className="hidden sm:flex items-center gap-2 flex-shrink-0">
            <button type="button" onClick={handleExcel}
              className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold bg-emerald-600 text-white hover:bg-emerald-700 shadow-md shadow-emerald-500/20 transition-all active:scale-95">
              <FileSpreadsheet className="w-4 h-4" /> Excel
            </button>
            <button type="button" onClick={handleDownloadAll} disabled={loading}
              className="flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold bg-blue-600 text-white hover:bg-blue-700 shadow-md shadow-blue-500/20 transition-all active:scale-95 disabled:opacity-70">
              {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Download className="w-4 h-4" />}
              Download All ({filtered.length})
            </button>
          </div>
        )}
      </div>

      {/* ── DESKTOP Filter Card ──────────────────────────────────────────── */}
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
                {sessions.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>

            <Field label="Class">
              <NativeSelect
                value={selectedClass}
                onChange={e => setSelectedClass(e.target.value)}
                placeholder="-- All Classes --"
              >
                {['All Classes', ...classes.map(c => c.className)].map(c => <option key={c} value={c}>{c}</option>)}
              </NativeSelect>
            </Field>

            {/* Spacer */}
            <div />

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
          {session ? `${session}${selectedClass && selectedClass !== 'All Classes' ? ` · ${selectedClass}` : ''}` : 'Select Filters'}
          {activeFilters > 0 && (
            <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilters}</span>
          )}
        </button>
        {hasResults && (
          <button type="button" onClick={handleDownloadAll} disabled={loading}
            className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl text-[13px] font-semibold
              bg-emerald-600 text-white hover:bg-emerald-700 shadow-sm disabled:opacity-70">
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Download className="w-4 h-4" />}
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
        session={session}
        setSession={setSession}
        selectedClass={selectedClass}
        setSelectedClass={setSelectedClass}
        sessions={sessions} classes={classes}
        onShow={handleShow}
        loading={loading}
        errors={errors}
      />

      {/* ── Loading Skeleton ──────────────────────────────────────────────── */}
      {loading && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] p-6 space-y-3">
          <div className="h-20 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse mb-4" />
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-12 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse" style={{ opacity: 1 - i * 0.15 }} />
          ))}
        </div>
      )}

      {/* ── Results ───────────────────────────────────────────────────────── */}
      {hasResults && !loading && (
        <>
          {/* School Header */}
          <SchoolHeader session={shownSession} selectedClass={shownClass} />

          {/* Summary Cards */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <SummaryCard icon={Users}      label="Total Records"  value={summary.total}      color="blue"    />
            <SummaryCard icon={BadgeCheck} label="Approved"       value={summary.approved}   color="emerald" />
            <SummaryCard icon={Clock}      label="Pending"        value={summary.pending}    color="amber"   />
            <SummaryCard icon={TrendingUp} label="Waitlisted"     value={summary.waitlisted} color="violet"  />
          </div>

          {/* Results Card */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

            {/* Card Header */}
            <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
                <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
                <ClipboardList className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
                <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Registration Records</span>
                <span className="text-[13px] text-slate-400 dark:text-slate-500">· {shownSession}</span>
                <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
                  {filtered.length} record{filtered.length !== 1 ? 's' : ''}
                </span>
              </div>

              {/* Search */}
              <div className="relative w-full sm:w-56 flex-shrink-0">
                <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  value={search}
                  onChange={e => setSearch(e.target.value)}
                  placeholder="Search name, ID, mobile…"
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

            {/* Status filter tabs */}
            <div className="flex items-center gap-1.5 px-5 py-2.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] overflow-x-auto scrollbar-hide">
              {['All', ...STATUS_LIST].map(s => (
                <button
                  key={s}
                  type="button"
                  onClick={() => setStatusFilter(s)}
                  className={`flex-shrink-0 px-3 py-1 rounded-lg text-[12px] font-semibold transition-all
                    ${statusFilter === s
                      ? 'bg-blue-600 text-white dark:bg-indigo-600 shadow-sm'
                      : 'bg-slate-100 text-slate-600 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700'
                    }`}
                >
                  {s}
                  {s !== 'All' && (
                    <span className={`ml-1.5 text-[10px] font-bold ${statusFilter === s ? 'opacity-80' : 'opacity-60'}`}>
                      {rows.filter(r => r.status === s).length}
                    </span>
                  )}
                </button>
              ))}
            </div>

            {/* Info hint */}
            <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
              <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
              <p className="text-[12px] text-blue-700 dark:text-blue-400">
                Click <Eye className="w-3 h-3 inline mx-0.5" /> to preview the form, <FileDown className="w-3 h-3 inline mx-0.5" /> to download PDF, or <Printer className="w-3 h-3 inline mx-0.5" /> to print directly.
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
                      {['S.No.', 'Reg No.', 'Student Name', "Father's Name", 'Mobile', 'Class', 'DOB', 'Category', 'Status', 'Actions'].map((h, i) => (
                        <th key={i} className="px-4 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:w-10">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((row, i) => (
                      <DesktopRow
                        key={row.id}
                        row={row}
                        idx={i + 1}
                        onDownload={handleDownload}
                        onPreview={setPreviewRow}
                      />
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
                    Tap a card to preview and download the form.
                  </p>
                  {filtered.map((row, i) => (
                    <MobileCard
                      key={row.id}
                      row={row}
                      idx={i + 1}
                      onDownload={handleDownload}
                      onPreview={setPreviewRow}
                    />
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
                <button onClick={() => { setSearch(''); setStatusFilter('All') }}
                  className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
                  <X className="w-3 h-3" /> Clear filters
                </button>
              )}
            </div>
          </div>

          {/* ── Report Viewer Placeholder ────────────────────────────────── */}
          <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
            <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
              <span className="w-1 h-5 rounded-full bg-emerald-500 flex-shrink-0" />
              <FileText className="w-4 h-4 text-emerald-600 dark:text-emerald-400 flex-shrink-0" />
              <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Report Viewer</span>
              <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400">
                RDLC Integration Pending
              </span>
            </div>
            <div className="flex flex-col items-center justify-center py-16 px-6 text-center gap-4">
              <div className="w-16 h-16 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
                <FileText className="w-8 h-8 text-slate-400 dark:text-slate-600" />
              </div>
              <div>
                <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">PDF Report Viewer</p>
                <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1 max-w-sm">
                  The RDLC / PDF report viewer (ReportViewer control equivalent) will render the selected registration form here after API integration.
                </p>
              </div>
              <div className="flex items-center gap-2 flex-wrap justify-center">
                <button type="button"
                  className="flex items-center gap-2 px-4 py-2 rounded-xl text-[12px] font-semibold border border-slate-200 dark:border-[rgba(99,102,241,0.25)] text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-white/[0.03] transition-colors">
                  <Eye className="w-3.5 h-3.5" /> Preview in Viewer
                </button>
                <button type="button"
                  className="flex items-center gap-2 px-4 py-2 rounded-xl text-[12px] font-semibold border border-slate-200 dark:border-[rgba(99,102,241,0.25)] text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-white/[0.03] transition-colors">
                  <Printer className="w-3.5 h-3.5" /> Print Report
                </button>
              </div>
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
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No records loaded</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Select session &amp; class, then click <strong>Show</strong> to load registration forms.
            </p>
          </div>
        </div>
      )}

      {/* Preview Modal */}
      {previewRow && (
        <PreviewModal
          row={previewRow}
          session={shownSession}
          onClose={() => setPreviewRow(null)}
          onDownload={handleDownload}
        />
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
