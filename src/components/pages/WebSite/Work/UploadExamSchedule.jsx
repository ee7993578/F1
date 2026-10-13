/**
 * UploadExamSchedule.jsx
 * Folder: src/pages/ExamMaster/UploadExamSchedule.jsx
 *
 * Converts legacy ASPX "Upload Exam Schedule" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Class & Exam dropdowns with validation
 *  - Title input + file upload with drag-and-drop
 *  - Records table (desktop) / cards (mobile)
 *  - Delete with confirmation
 *  - Mobile bottom-drawer filter
 *  - Loading / empty states
 *  - Toast notifications
 */

import { useState, useMemo, useCallback, useRef } from 'react'
import {
  Upload, Trash2, FileText, AlertCircle, X, Check,
  Loader2, ChevronDown, Search, RefreshCw, Eye,
  BookOpen, GraduationCap, Calendar, FileSpreadsheet,
  SlidersHorizontal, Info, Plus, ClipboardList,
  Download, Filter, School2, MoreVertical,
  CheckCircle2, XCircle, FilePlus2, FolderOpen,
  Paperclip, LayoutList, Grid3X3
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ──────────────────────────────────────────────────────

const CLASSES = [
  { value: '1',  label: 'Nursery'    },
  { value: '2',  label: 'LKG'        },
  { value: '3',  label: 'UKG'        },
  { value: '4',  label: 'Class I'    },
  { value: '5',  label: 'Class II'   },
  { value: '6',  label: 'Class III'  },
  { value: '7',  label: 'Class IV'   },
  { value: '8',  label: 'Class V'    },
  { value: '9',  label: 'Class VI'   },
  { value: '10', label: 'Class VII'  },
  { value: '11', label: 'Class VIII' },
  { value: '12', label: 'Class IX'   },
  { value: '13', label: 'Class X'    },
  { value: '14', label: 'Class XI'   },
  { value: '15', label: 'Class XII'  },
]

const EXAMS = [
  { value: '1', label: 'Unit Test – I'    },
  { value: '2', label: 'Unit Test – II'   },
  { value: '3', label: 'Half Yearly Exam' },
  { value: '4', label: 'Pre-Board Exam'   },
  { value: '5', label: 'Annual Exam'      },
  { value: '6', label: 'Practical Exam'   },
]

const INITIAL_RECORDS = [
  { id: 1, class_id: '4',  class_name: 'Class I',    exam_type_id: '1', exam_type: 'Unit Test – I',    title: 'Unit Test Schedule May 2025', fpath: '#', fname: 'UT1_ClassI_May2025.pdf'   },
  { id: 2, class_id: '9',  class_name: 'Class VI',   exam_type_id: '3', exam_type: 'Half Yearly Exam', title: 'Half Yearly Schedule 2025',   fpath: '#', fname: 'HY_ClassVI_2025.pdf'     },
  { id: 3, class_id: '13', class_name: 'Class X',    exam_type_id: '4', exam_type: 'Pre-Board Exam',   title: 'Pre-Board Nov 2025',          fpath: '#', fname: 'PreBoard_ClassX.pdf'     },
  { id: 4, class_id: '14', class_name: 'Class XI',   exam_type_id: '5', exam_type: 'Annual Exam',      title: 'Annual Exam 2024–25',         fpath: '#', fname: 'Annual_ClassXI.pdf'      },
  { id: 5, class_id: '15', class_name: 'Class XII',  exam_type_id: '5', exam_type: 'Annual Exam',      title: 'Board Practical Schedule',    fpath: '#', fname: 'Practical_ClassXII.pdf'  },
]

// ─── CLASS COLOR BADGE ───────────────────────────────────────────────────────
const CLASS_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' },
  { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' },
  { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' },
  { fg: '#dc2626', bg: '#fee2e2' },
  { fg: '#0369a1', bg: '#e0f2fe' },
]
const classColor = (name = '') => CLASS_COLORS[(name.charCodeAt(0) ?? 0) % CLASS_COLORS.length]
const formatAbbr = (name = '') => name.replace('Class ', '').slice(0, 3).toUpperCase()

// ─── PRIMITIVE COMPONENTS ─────────────────────────────────────────────────────

function NativeSelect({ value, onChange, children, placeholder, error, disabled }) {
  return (
    <div className="relative">
      <select
        value={value}
        onChange={onChange}
        disabled={disabled}
        className={`w-full appearance-none pl-3 pr-8 py-2.5 text-[13px] rounded-xl border outline-none transition-all cursor-pointer
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

function Field({ label, error, required, children, hint }) {
  return (
    <div className="flex flex-col gap-1.5">
      <label className="text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide flex items-center gap-1">
        {label}
        {required && <span className="text-rose-500">*</span>}
        {hint && (
          <span className="ml-1 text-[10px] font-normal text-slate-400 normal-case tracking-normal">({hint})</span>
        )}
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
  const colors = type === 'success'
    ? 'bg-emerald-600 text-white'
    : type === 'error'
    ? 'bg-rose-600 text-white'
    : 'bg-blue-600 text-white'
  return (
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3
        rounded-2xl shadow-xl text-[13px] font-semibold min-w-[260px] max-w-[90vw] ${colors}`}
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

// ─── DELETE CONFIRM MODAL ─────────────────────────────────────────────────────
function DeleteModal({ record, onConfirm, onCancel }) {
  return (
    <>
      <div className="fixed inset-0 bg-black/50 z-50 backdrop-blur-sm flex items-center justify-center p-4"
        onClick={onCancel}>
        <div
          className="bg-white dark:bg-[#1a1f35] rounded-2xl shadow-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] w-full max-w-sm p-6"
          onClick={e => e.stopPropagation()}
          style={{ animation: 'popIn .2s ease' }}
        >
          <style>{`@keyframes popIn{from{opacity:0;transform:scale(.95)}to{opacity:1;transform:scale(1)}}`}</style>
          <div className="flex items-center gap-3 mb-4">
            <div className="w-11 h-11 rounded-xl bg-rose-100 dark:bg-rose-500/15 flex items-center justify-center flex-shrink-0">
              <Trash2 className="w-5 h-5 text-rose-600 dark:text-rose-400" />
            </div>
            <div>
              <p className="text-[15px] font-bold text-slate-800 dark:text-slate-100">Delete Schedule?</p>
              <p className="text-[12px] text-slate-500 dark:text-slate-400">This action cannot be undone.</p>
            </div>
          </div>
          <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-700 p-3 mb-5">
            <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 truncate">{record.title}</p>
            <p className="text-[11px] text-slate-400 mt-0.5">{record.class_name} · {record.exam_type}</p>
          </div>
          <div className="flex gap-3">
            <button onClick={onCancel}
              className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700
                hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors">
              Cancel
            </button>
            <button onClick={onConfirm}
              className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-rose-600 text-white
                hover:bg-rose-700 transition-colors shadow-md shadow-rose-500/25">
              Delete
            </button>
          </div>
        </div>
      </div>
    </>
  )
}

// ─── FILE DROP ZONE ───────────────────────────────────────────────────────────
function FileDropZone({ file, onFile, error }) {
  const inputRef = useRef()
  const [dragging, setDragging] = useState(false)

  const handleDrop = (e) => {
    e.preventDefault()
    setDragging(false)
    const f = e.dataTransfer.files[0]
    if (f) onFile(f)
  }

  const ext = file ? file.name.split('.').pop().toUpperCase() : null
  const extColors = {
    PDF: 'bg-rose-100 text-rose-700 dark:bg-rose-500/15 dark:text-rose-400',
    DOC: 'bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400',
    DOCX: 'bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400',
    XLS: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-400',
    XLSX: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-400',
  }

  return (
    <div>
      <input
        ref={inputRef}
        type="file"
        accept=".pdf,.doc,.docx,.xls,.xlsx"
        className="hidden"
        onChange={e => { if (e.target.files[0]) onFile(e.target.files[0]) }}
      />
      {!file ? (
        <div
          onDragOver={e => { e.preventDefault(); setDragging(true) }}
          onDragLeave={() => setDragging(false)}
          onDrop={handleDrop}
          onClick={() => inputRef.current?.click()}
          className={`cursor-pointer rounded-xl border-2 border-dashed transition-all p-5 text-center
            ${dragging
              ? 'border-blue-400 bg-blue-50 dark:bg-blue-500/10 dark:border-blue-500'
              : error
              ? 'border-rose-400 bg-rose-50/50 dark:bg-rose-500/5'
              : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)] hover:border-blue-300 dark:hover:border-indigo-500/50 hover:bg-blue-50/40 dark:hover:bg-indigo-500/5'
            }`}
        >
          <Upload className={`w-7 h-7 mx-auto mb-2 ${dragging ? 'text-blue-500' : 'text-slate-300 dark:text-slate-600'}`} />
          <p className="text-[13px] font-semibold text-slate-500 dark:text-slate-400">
            {dragging ? 'Drop file here' : 'Click to upload or drag & drop'}
          </p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-1">PDF, DOC, DOCX, XLS, XLSX</p>
        </div>
      ) : (
        <div className="flex items-center gap-3 rounded-xl border border-blue-200 dark:border-[rgba(99,102,241,0.3)] bg-blue-50/60 dark:bg-indigo-500/[0.07] p-3">
          <div className={`flex-shrink-0 w-9 h-9 rounded-lg flex items-center justify-center text-[10px] font-extrabold ${extColors[ext] || 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300'}`}>
            {ext}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 truncate">{file.name}</p>
            <p className="text-[11px] text-slate-400 mt-0.5">{(file.size / 1024).toFixed(1)} KB</p>
          </div>
          <button
            type="button"
            onClick={() => onFile(null)}
            className="w-7 h-7 flex items-center justify-center rounded-lg bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 text-slate-400 hover:text-rose-500 transition-colors flex-shrink-0"
          >
            <X className="w-3.5 h-3.5" />
          </button>
        </div>
      )}
      {error && (
        <p className="flex items-center gap-1 text-[11px] text-rose-500 mt-1">
          <AlertCircle className="w-3 h-3 flex-shrink-0" />{error}
        </p>
      )}
    </div>
  )
}

// ─── STAT CARD ────────────────────────────────────────────────────────────────
function StatCard({ icon: Icon, label, value, color }) {
  const colors = {
    blue:    'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400',
    emerald: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400',
    amber:   'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400',
    violet:  'bg-violet-50 text-violet-600 dark:bg-violet-500/10 dark:text-violet-400',
  }
  return (
    <div className="flex items-center gap-3 rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-white dark:bg-[#1a1f35] px-4 py-3 shadow-sm">
      <span className={`w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 ${colors[color]}`}>
        <Icon className="w-4 h-4" />
      </span>
      <div>
        <p className="text-[18px] font-bold text-slate-800 dark:text-slate-100 tabular-nums leading-tight">{value}</p>
        <p className="text-[11px] text-slate-500 dark:text-slate-400">{label}</p>
      </div>
    </div>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ record, idx, onDelete }) {
  const { fg, bg } = classColor(record.class_name)
  const ext = record.fname.split('.').pop().toUpperCase()
  const extColors = {
    PDF: 'bg-rose-100 text-rose-700',
    DOCX: 'bg-blue-100 text-blue-700',
    XLSX: 'bg-emerald-100 text-emerald-700',
  }

  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/70 dark:hover:bg-white/[0.02] transition-colors group">
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{idx}</td>

      <td className="px-4 py-3">
        <div className="flex items-center gap-2.5">
          <span className="w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold" style={{ background: bg, color: fg }}>
            {formatAbbr(record.class_name)}
          </span>
          <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{record.class_name}</span>
        </div>
      </td>

      <td className="px-4 py-3">
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-indigo-50 dark:bg-indigo-500/10 border border-indigo-100 dark:border-indigo-500/20 text-[12px] font-semibold text-indigo-700 dark:text-indigo-400 whitespace-nowrap">
          <Calendar className="w-3 h-3" />{record.exam_type}
        </span>
      </td>

      <td className="px-4 py-3">
        <p className="text-[13px] font-medium text-slate-700 dark:text-slate-200 truncate max-w-[200px]">{record.title}</p>
      </td>

      <td className="px-4 py-3">
        <a
          href={record.fpath}
          className="inline-flex items-center gap-2 px-3 py-1.5 rounded-lg bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20 text-[12px] font-semibold text-blue-700 dark:text-blue-400 hover:bg-blue-100 dark:hover:bg-blue-500/20 transition-colors whitespace-nowrap"
        >
          <span className={`text-[9px] font-extrabold px-1 py-0.5 rounded ${extColors[ext] || 'bg-slate-100 text-slate-600'}`}>{ext}</span>
          <Download className="w-3 h-3" />
          Download
        </a>
      </td>

      <td className="px-4 py-3 text-center">
        <button
          onClick={() => onDelete(record)}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
            bg-rose-50 text-rose-600 border border-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:border-rose-500/20
            hover:bg-rose-100 dark:hover:bg-rose-500/20 transition-colors opacity-0 group-hover:opacity-100"
        >
          <Trash2 className="w-3.5 h-3.5" />
          Delete
        </button>
      </td>
    </tr>
  )
}

// ─── MOBILE RECORD CARD ───────────────────────────────────────────────────────
function MobileRecordCard({ record, idx, onDelete }) {
  const { fg, bg } = classColor(record.class_name)
  const ext = record.fname.split('.').pop().toUpperCase()

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Top color strip */}
      <div className="h-1 w-full" style={{ background: fg }} />

      <div className="p-4">
        {/* Header row */}
        <div className="flex items-start gap-3 mb-3">
          <span className="w-10 h-10 rounded-xl flex-shrink-0 flex items-center justify-center text-[11px] font-bold" style={{ background: bg, color: fg }}>
            {formatAbbr(record.class_name)}
          </span>
          <div className="flex-1 min-w-0">
            <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{record.title}</p>
            <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-0.5">
              {record.class_name} &nbsp;·&nbsp; {record.exam_type}
            </p>
          </div>
          <span className="text-[11px] font-bold text-slate-400 dark:text-slate-500 tabular-nums flex-shrink-0">#{idx}</span>
        </div>

        {/* Badges */}
        <div className="flex items-center gap-2 mb-4 flex-wrap">
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-semibold" style={{ background: bg, color: fg }}>
            <GraduationCap className="w-3 h-3" />{record.class_name}
          </span>
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-semibold bg-indigo-50 dark:bg-indigo-500/10 text-indigo-700 dark:text-indigo-400">
            <Calendar className="w-3 h-3" />{record.exam_type}
          </span>
          <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-extrabold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400">
            {ext}
          </span>
        </div>

        {/* Action buttons */}
        <div className="flex gap-2">
          <a
            href={record.fpath}
            className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
              bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20 text-blue-700 dark:text-blue-400
              hover:bg-blue-100 transition-colors"
          >
            <Download className="w-4 h-4" />
            Download File
          </a>
          <button
            onClick={() => onDelete(record)}
            className="px-4 py-2.5 rounded-xl text-[13px] font-semibold
              bg-rose-50 dark:bg-rose-500/10 border border-rose-100 dark:border-rose-500/20 text-rose-600 dark:text-rose-400
              hover:bg-rose-100 transition-colors"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  )
}

// ─── VIEW TOGGLE ─────────────────────────────────────────────────────────────
function ViewToggle({ view, setView }) {
  return (
    <div className="hidden sm:flex items-center gap-1 p-1 rounded-xl bg-slate-100 dark:bg-slate-800">
      {[
        { k: 'table', Icon: LayoutList, label: 'Table' },
        { k: 'grid',  Icon: Grid3X3,   label: 'Grid'  },
      ].map(({ k, Icon, label }) => (
        <button
          key={k}
          onClick={() => setView(k)}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold transition-all
            ${view === k
              ? 'bg-white dark:bg-[#1a1f35] text-slate-700 dark:text-slate-200 shadow-sm'
              : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'
            }`}
        >
          <Icon className="w-3.5 h-3.5" />{label}
        </button>
      ))}
    </div>
  )
}

// ─── UPLOAD FORM (reused on desktop inline + mobile modal) ───────────────────
function UploadForm({ form, setForm, errors, setErrors, onSubmit, loading, onReset }) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {/* Class */}
      <Field label="Class" error={errors.class_id} required>
        <NativeSelect
          value={form.class_id}
          onChange={e => { setForm(p => ({ ...p, class_id: e.target.value })); setErrors(p => ({ ...p, class_id: undefined })) }}
          placeholder="-- Select Class --"
          error={errors.class_id}
        >
          {CLASSES.map(c => <option key={c.value} value={c.value}>{c.label}</option>)}
        </NativeSelect>
      </Field>

      {/* Exam */}
      <Field label="Exam" error={errors.exam_id} required>
        <NativeSelect
          value={form.exam_id}
          onChange={e => { setForm(p => ({ ...p, exam_id: e.target.value })); setErrors(p => ({ ...p, exam_id: undefined })) }}
          placeholder="-- Select Exam --"
          error={errors.exam_id}
        >
          {EXAMS.map(ex => <option key={ex.value} value={ex.value}>{ex.label}</option>)}
        </NativeSelect>
      </Field>

      {/* Title */}
      <Field label="Title" error={errors.title} required>
        <div className="relative">
          <ClipboardList className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
          <input
            value={form.title}
            onChange={e => { setForm(p => ({ ...p, title: e.target.value })); setErrors(p => ({ ...p, title: undefined })) }}
            placeholder="e.g. Annual Exam 2025"
            className={`w-full pl-8 pr-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all
              bg-white text-slate-800 placeholder-slate-300
              focus:border-blue-400 focus:ring-2 focus:ring-blue-100
              dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600
              dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
              ${errors.title ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
          />
        </div>
        {errors.title && (
          <p className="flex items-center gap-1 text-[11px] text-rose-500">
            <AlertCircle className="w-3 h-3 flex-shrink-0" />{errors.title}
          </p>
        )}
      </Field>

      {/* File */}
      <Field label="File" error={errors.file} required hint="PDF/DOC/XLS">
        <FileDropZone
          file={form.file}
          onFile={f => { setForm(p => ({ ...p, file: f })); setErrors(p => ({ ...p, file: undefined })) }}
          error={errors.file}
        />
      </Field>

      {/* Actions */}
      <div className="sm:col-span-2 lg:col-span-4 flex justify-end gap-2 pt-1">
        <button
          type="button"
          onClick={onReset}
          className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-[13px] font-semibold
            bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors"
        >
          <RefreshCw className="w-3.5 h-3.5" /> Reset
        </button>
        <button
          type="button"
          onClick={onSubmit}
          disabled={loading}
          className="flex items-center gap-2 px-6 py-2.5 rounded-xl text-[13px] font-semibold text-white
            bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700
            shadow-md shadow-blue-500/20 transition-all active:scale-95 disabled:opacity-70"
        >
          {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Upload className="w-4 h-4" />}
          Upload Schedule
        </button>
      </div>
    </div>
  )
}

// ─── MOBILE UPLOAD DRAWER ────────────────────────────────────────────────────
function UploadDrawer({ open, onClose, form, setForm, errors, setErrors, onSubmit, loading, onReset }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/50 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl max-h-[92vh] overflow-y-auto"
        style={{ animation: 'drawerUp .25s ease' }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
        <div className="flex justify-center pt-3 pb-1">
          <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
        </div>
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <div className="flex items-center gap-2">
            <FilePlus2 className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Upload Schedule</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5 space-y-4">
          {/* Class */}
          <Field label="Class" error={errors.class_id} required>
            <NativeSelect value={form.class_id} onChange={e => { setForm(p => ({ ...p, class_id: e.target.value })); setErrors(p => ({ ...p, class_id: undefined })) }} placeholder="-- Select Class --" error={errors.class_id}>
              {CLASSES.map(c => <option key={c.value} value={c.value}>{c.label}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Exam" error={errors.exam_id} required>
            <NativeSelect value={form.exam_id} onChange={e => { setForm(p => ({ ...p, exam_id: e.target.value })); setErrors(p => ({ ...p, exam_id: undefined })) }} placeholder="-- Select Exam --" error={errors.exam_id}>
              {EXAMS.map(ex => <option key={ex.value} value={ex.value}>{ex.label}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Title" error={errors.title} required>
            <div className="relative">
              <ClipboardList className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                value={form.title}
                onChange={e => { setForm(p => ({ ...p, title: e.target.value })); setErrors(p => ({ ...p, title: undefined })) }}
                placeholder="e.g. Annual Exam 2025"
                className={`w-full pl-8 pr-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all
                  bg-white text-slate-800 placeholder-slate-300
                  focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                  dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600
                  dark:focus:border-indigo-400
                  ${errors.title ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
              />
            </div>
          </Field>
          <Field label="Upload File" error={errors.file} required hint="PDF/DOC/XLS">
            <FileDropZone file={form.file} onFile={f => { setForm(p => ({ ...p, file: f })); setErrors(p => ({ ...p, file: undefined })) }} error={errors.file} />
          </Field>
        </div>
        <div className="sticky bottom-0 px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-white dark:bg-[#1a1f35] flex gap-3">
          <button onClick={() => { onReset(); onClose() }}
            className="flex-1 py-3 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300 transition-colors">
            Reset
          </button>
          <button onClick={() => { onSubmit(); }} disabled={loading}
            className="flex-1 flex items-center justify-center gap-2 py-3 rounded-xl text-[13px] font-semibold text-white
              bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 shadow-md shadow-blue-500/20 disabled:opacity-70 transition-all">
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Upload className="w-4 h-4" />}
            Upload
          </button>
        </div>
      </div>
    </>
  )
}

// ─── GRID VIEW CARD (desktop) ─────────────────────────────────────────────────
function GridCard({ record, idx, onDelete }) {
  const { fg, bg } = classColor(record.class_name)
  const ext = record.fname.split('.').pop().toUpperCase()

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm hover:shadow-md transition-shadow group">
      <div className="h-1.5 w-full" style={{ background: fg }} />
      <div className="p-4">
        <div className="flex items-center gap-3 mb-3">
          <span className="w-9 h-9 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold" style={{ background: bg, color: fg }}>
            {formatAbbr(record.class_name)}
          </span>
          <div className="flex-1 min-w-0">
            <p className="text-[13px] font-bold text-slate-800 dark:text-slate-100 truncate">{record.title}</p>
            <p className="text-[11px] text-slate-500 dark:text-slate-400">{record.class_name}</p>
          </div>
        </div>
        <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full bg-indigo-50 dark:bg-indigo-500/10 text-[11px] font-semibold text-indigo-700 dark:text-indigo-400 mb-4">
          <Calendar className="w-3 h-3" />{record.exam_type}
        </span>
        <div className="flex gap-2 mt-3">
          <a href={record.fpath}
            className="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-lg bg-blue-50 dark:bg-blue-500/10 text-[12px] font-semibold text-blue-700 dark:text-blue-400 hover:bg-blue-100 transition-colors">
            <Download className="w-3.5 h-3.5" /> {ext}
          </a>
          <button onClick={() => onDelete(record)}
            className="px-3 py-2 rounded-lg bg-rose-50 dark:bg-rose-500/10 text-rose-600 dark:text-rose-400 hover:bg-rose-100 transition-colors">
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>
    </div>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function UploadExamSchedule() {
  // ── State ───────────────────────────────────────────────────────────────────
  const emptyForm = { class_id: '', exam_id: '', title: '', file: null }
  const [form,        setForm]        = useState(emptyForm)
  const [errors,      setErrors]      = useState({})
  const [records,     setRecords]     = useState(INITIAL_RECORDS)
  const [loading,     setLoading]     = useState(false)
  const [toast,       setToast]       = useState(null)
  const [search,      setSearch]      = useState('')
  const [filterClass, setFilterClass] = useState('')
  const [filterExam,  setFilterExam]  = useState('')
  const [deleteTarget,setDeleteTarget]= useState(null)
  const [drawerOpen,  setDrawerOpen]  = useState(false)
  const [view,        setView]        = useState('table')

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Validate & Submit ────────────────────────────────────────────────────────
  const handleSubmit = useCallback(() => {
    const err = {}
    if (!form.class_id) err.class_id = 'Select a class'
    if (!form.exam_id)  err.exam_id  = 'Select an exam'
    if (!form.title.trim()) err.title = 'Enter a title'
    if (!form.file)     err.file     = 'Upload a file'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)

    setTimeout(() => {
      const className  = CLASSES.find(c => c.value === form.class_id)?.label  || ''
      const examName   = EXAMS.find(e => e.value === form.exam_id)?.label    || ''
      const newRecord  = {
        id:           Date.now(),
        class_id:     form.class_id,
        class_name:   className,
        exam_type_id: form.exam_id,
        exam_type:    examName,
        title:        form.title.trim(),
        fpath:        '#',
        fname:        form.file.name,
      }
      setRecords(prev => [newRecord, ...prev])
      setForm(emptyForm)
      setLoading(false)
      setDrawerOpen(false)
      showToast(`"${newRecord.title}" uploaded successfully.`)
    }, 900)
  }, [form])

  const handleReset = () => { setForm(emptyForm); setErrors({}) }

  // ── Delete ───────────────────────────────────────────────────────────────────
  const confirmDelete = useCallback(() => {
    if (!deleteTarget) return
    setRecords(prev => prev.filter(r => r.id !== deleteTarget.id))
    showToast(`"${deleteTarget.title}" deleted.`, 'error')
    setDeleteTarget(null)
  }, [deleteTarget])

  // ── Filtered records ─────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    let r = records
    if (search)      r = r.filter(x => x.title.toLowerCase().includes(search.toLowerCase()) || x.class_name.toLowerCase().includes(search.toLowerCase()) || x.exam_type.toLowerCase().includes(search.toLowerCase()))
    if (filterClass) r = r.filter(x => x.class_id === filterClass)
    if (filterExam)  r = r.filter(x => x.exam_type_id === filterExam)
    return r
  }, [records, search, filterClass, filterExam])

  const activeFilters = [filterClass, filterExam].filter(Boolean).length

  // ── Stats ────────────────────────────────────────────────────────────────────
  const totalClasses = useMemo(() => new Set(records.map(r => r.class_id)).size, [records])
  const totalExams   = useMemo(() => new Set(records.map(r => r.exam_type_id)).size, [records])

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ─────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <FileSpreadsheet className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Upload Exam Schedule
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Attach exam timetables for each class &amp; exam type.
          </p>
        </div>
        {/* Mobile: FAB upload button */}
        <button
          type="button"
          onClick={() => setDrawerOpen(true)}
          className="sm:hidden flex items-center justify-center gap-2 py-3 px-5 rounded-2xl text-[13px] font-bold
            bg-blue-600 text-white shadow-lg shadow-blue-500/30 active:scale-95 transition-all"
        >
          <FilePlus2 className="w-4 h-4" /> Upload New Schedule
        </button>
      </div>

      {/* ── Stat Cards ─────────────────────────────────────────────────────── */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <StatCard icon={ClipboardList}  label="Total Schedules" value={records.length}  color="blue"    />
        <StatCard icon={GraduationCap}  label="Classes Covered" value={totalClasses}    color="emerald" />
        <StatCard icon={Calendar}       label="Exam Types"      value={totalExams}      color="violet"  />
        <StatCard icon={FileText}       label="Showing Now"     value={filtered.length} color="amber"   />
      </div>

      {/* ── DESKTOP Upload Form ────────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <FilePlus2 className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Upload New Schedule</span>
          <span className="text-[11px] text-slate-400 dark:text-slate-500">All fields required</span>
        </div>
        <div className="p-5">
          <UploadForm
            form={form}
            setForm={setForm}
            errors={errors}
            setErrors={setErrors}
            onSubmit={handleSubmit}
            loading={loading}
            onReset={handleReset}
          />
        </div>
      </div>

      {/* ── MOBILE Upload Drawer ───────────────────────────────────────────── */}
      <UploadDrawer
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        form={form}
        setForm={setForm}
        errors={errors}
        setErrors={setErrors}
        onSubmit={handleSubmit}
        loading={loading}
        onReset={handleReset}
      />

      {/* ── Records Table/Grid ─────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Card Header */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0">
            <span className="w-1 h-5 rounded-full bg-indigo-500 flex-shrink-0" />
            <BookOpen className="w-4 h-4 text-indigo-600 dark:text-indigo-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Uploaded Schedules</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-indigo-100 text-indigo-700 dark:bg-indigo-500/15 dark:text-indigo-400 flex-shrink-0">
              {filtered.length} record{filtered.length !== 1 ? 's' : ''}
            </span>
          </div>
          <div className="flex items-center gap-2 flex-wrap">
            <ViewToggle view={view} setView={setView} />
          </div>
        </div>

        {/* Filters row */}
        <div className="flex flex-wrap items-center gap-2 px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
          {/* Search */}
          <div className="relative flex-1 min-w-[160px] max-w-xs">
            <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
            <input
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Search title, class, exam…"
              className="w-full pl-8 pr-7 py-2 text-[12px] rounded-xl border outline-none transition-all
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

          {/* Class filter */}
          <div className="relative min-w-[130px]">
            <select
              value={filterClass}
              onChange={e => setFilterClass(e.target.value)}
              className="w-full appearance-none pl-3 pr-7 py-2 text-[12px] rounded-xl border outline-none transition-all
                bg-white text-slate-700 border-slate-200 cursor-pointer
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]"
            >
              <option value="">All Classes</option>
              {CLASSES.map(c => <option key={c.value} value={c.value}>{c.label}</option>)}
            </select>
            <ChevronDown className="absolute right-2 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
          </div>

          {/* Exam filter */}
          <div className="relative min-w-[150px]">
            <select
              value={filterExam}
              onChange={e => setFilterExam(e.target.value)}
              className="w-full appearance-none pl-3 pr-7 py-2 text-[12px] rounded-xl border outline-none transition-all
                bg-white text-slate-700 border-slate-200 cursor-pointer
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:border-[rgba(99,102,241,0.25)]"
            >
              <option value="">All Exams</option>
              {EXAMS.map(ex => <option key={ex.value} value={ex.value}>{ex.label}</option>)}
            </select>
            <ChevronDown className="absolute right-2 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
          </div>

          {/* Clear filters */}
          {(search || filterClass || filterExam) && (
            <button
              onClick={() => { setSearch(''); setFilterClass(''); setFilterExam('') }}
              className="flex items-center gap-1.5 px-3 py-2 rounded-xl text-[12px] font-semibold
                bg-slate-100 text-slate-600 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors"
            >
              <X className="w-3 h-3" /> Clear
              {activeFilters > 0 && (
                <span className="bg-blue-600 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilters}</span>
              )}
            </button>
          )}
        </div>

        {/* ── DESKTOP TABLE ── */}
        {view === 'table' && (
          <div className="hidden md:block overflow-x-auto">
            {filtered.length === 0 ? (
              <EmptyState search={search || filterClass || filterExam} />
            ) : (
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                    {['S.No.', 'Class', 'Exam Type', 'Title', 'File', 'Action'].map((h, i) => (
                      <th key={i} className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap">
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((rec, i) => (
                    <DesktopRow key={rec.id} record={rec} idx={i + 1} onDelete={setDeleteTarget} />
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

        {/* ── DESKTOP GRID ── */}
        {view === 'grid' && (
          <div className="hidden md:block p-5">
            {filtered.length === 0 ? (
              <EmptyState search={search || filterClass || filterExam} />
            ) : (
              <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                {filtered.map(rec => (
                  <GridCard key={rec.id} record={rec} onDelete={setDeleteTarget} />
                ))}
              </div>
            )}
          </div>
        )}

        {/* ── MOBILE CARDS ── */}
        <div className="md:hidden p-4 space-y-3">
          {filtered.length === 0 ? (
            <EmptyState search={search || filterClass || filterExam} />
          ) : (
            filtered.map((rec, i) => (
              <MobileRecordCard key={rec.id} record={rec} idx={i + 1} onDelete={setDeleteTarget} />
            ))
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-3 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{records.length}</span> records
          </p>
          {(search || filterClass || filterExam) && (
            <button onClick={() => { setSearch(''); setFilterClass(''); setFilterExam('') }}
              className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
              <X className="w-3 h-3" /> Clear filters
            </button>
          )}
        </div>
      </div>

      {/* Delete Confirmation Modal */}
      {deleteTarget && (
        <DeleteModal
          record={deleteTarget}
          onConfirm={confirmDelete}
          onCancel={() => setDeleteTarget(null)}
        />
      )}

      {/* Loading overlay for mobile */}
      {loading && (
        <div className="fixed inset-0 bg-black/30 z-50 flex items-center justify-center backdrop-blur-sm">
          <div className="bg-white dark:bg-[#1a1f35] rounded-2xl px-8 py-6 flex flex-col items-center gap-3 shadow-2xl">
            <Loader2 className="w-8 h-8 animate-spin text-blue-600 dark:text-indigo-400" />
            <p className="text-[13px] font-semibold text-slate-600 dark:text-slate-300">Uploading…</p>
          </div>
        </div>
      )}

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}

// ─── EMPTY STATE ──────────────────────────────────────────────────────────────
function EmptyState({ search }) {
  return (
    <div className="flex flex-col items-center justify-center py-14 gap-4 text-slate-400 dark:text-slate-600">
      <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
        {search
          ? <Search className="w-7 h-7 opacity-50" />
          : <FolderOpen className="w-7 h-7 opacity-50" />}
      </div>
      <div className="text-center">
        <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">
          {search ? 'No matching records' : 'No schedules uploaded yet'}
        </p>
        <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
          {search
            ? 'Try different keywords or clear your filters.'
            : 'Fill the form above to upload the first exam schedule.'}
        </p>
      </div>
    </div>
  )
}
