/**
 * UploadNews.jsx
 * Folder: src/pages/Admin/News/UploadNews.jsx
 *
 * Converts legacy ASPX "Upload News" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session dropdown
 *  - News Title, Link, Description, Date, File Upload
 *  - Client-side validation with error states
 *  - News list grid view (Desktop table / Mobile cards)
 *  - Delete action per record
 *  - Toast notifications
 *  - Loading states
 *  - Mobile-first responsive layout
 */

import { useState, useCallback, useRef, useMemo } from 'react'
import {
  Newspaper, Upload, Trash2, Calendar, Link2,
  FileText, AlignLeft, BookOpen, Plus, X, Check,
  AlertCircle, Loader2, ChevronDown, RefreshCw,
  Search, Eye, SlidersHorizontal, ExternalLink,
  Clock, Filter, Info, ChevronRight, Tag, Hash
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ──────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const INITIAL_NEWS = [
  {
    title_id: 1,
    news_title: 'Annual Sports Day Celebration 2025',
    new_link: 'https://school.edu/sports-day-2025',
    news_description: 'Annual sports day will be held on 15th March 2025. All students are requested to participate.',
    NewsDate: '10 Jan 2025',
    session: '2024-25',
    fileName: 'sports_day_notice.pdf',
  },
  {
    title_id: 2,
    news_title: 'Parent-Teacher Meeting Schedule',
    new_link: '',
    news_description: 'PTM is scheduled for 20th January 2025. Parents are requested to attend without fail.',
    NewsDate: '05 Jan 2025',
    session: '2024-25',
    fileName: 'ptm_schedule.pdf',
  },
  {
    title_id: 3,
    news_title: 'Fee Submission Last Date Extended',
    new_link: 'https://school.edu/fee-notice',
    news_description: 'Last date for fee submission has been extended to 28th January 2025 for all classes.',
    NewsDate: '02 Jan 2025',
    session: '2024-25',
    fileName: 'fee_extension.pdf',
  },
  {
    title_id: 4,
    news_title: 'Winter Vacation Notice',
    new_link: '',
    news_description: 'School will remain closed from 25th Dec 2024 to 5th Jan 2025 for winter vacation.',
    NewsDate: '20 Dec 2024',
    session: '2023-24',
    fileName: 'winter_vacation.pdf',
  },
  {
    title_id: 5,
    news_title: 'Exam Time Table Released',
    new_link: 'https://school.edu/exam-2024',
    news_description: 'Annual exam time table for classes IX-XII has been released. Check the school notice board.',
    NewsDate: '15 Dec 2024',
    session: '2023-24',
    fileName: 'exam_timetable.pdf',
  },
]

// ─── HELPERS ──────────────────────────────────────────────────────────────────
const BADGE_COLORS = [
  { bg: 'bg-blue-100 dark:bg-blue-500/15', text: 'text-blue-700 dark:text-blue-400', dot: 'bg-blue-500' },
  { bg: 'bg-emerald-100 dark:bg-emerald-500/15', text: 'text-emerald-700 dark:text-emerald-400', dot: 'bg-emerald-500' },
  { bg: 'bg-violet-100 dark:bg-violet-500/15', text: 'text-violet-700 dark:text-violet-400', dot: 'bg-violet-500' },
  { bg: 'bg-amber-100 dark:bg-amber-500/15', text: 'text-amber-700 dark:text-amber-400', dot: 'bg-amber-500' },
  { bg: 'bg-cyan-100 dark:bg-cyan-500/15', text: 'text-cyan-700 dark:text-cyan-400', dot: 'bg-cyan-500' },
]
const sessionColor = (s = '') => BADGE_COLORS[s.charCodeAt(0) % BADGE_COLORS.length]

let nextId = 6

// ─── PRIMITIVE COMPONENTS ─────────────────────────────────────────────────────

/** Native select with chevron icon */
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

/** Form field wrapper with label + error */
function Field({ label, error, required, hint, children }) {
  return (
    <div className="flex flex-col gap-1.5">
      <label className="text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">
        {label}
        {required && <span className="text-rose-500 ml-0.5">*</span>}
        {hint && <span className="ml-1 text-[11px] font-normal normal-case text-slate-400 dark:text-slate-500">({hint})</span>}
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

/** Text input */
function TextInput({ value, onChange, placeholder, error, icon: Icon, type = 'text' }) {
  return (
    <div className="relative">
      {Icon && (
        <span className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-500 pointer-events-none">
          <Icon className="w-3.5 h-3.5" />
        </span>
      )}
      <input
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className={`w-full ${Icon ? 'pl-8' : 'pl-3'} pr-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all
          bg-white text-slate-800 placeholder-slate-300
          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
          dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600
          dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
          ${error
            ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20'
            : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
          }`}
      />
    </div>
  )
}

/** Textarea */
function TextArea({ value, onChange, placeholder, error, rows = 3 }) {
  return (
    <textarea
      value={value}
      onChange={onChange}
      placeholder={placeholder}
      rows={rows}
      className={`w-full px-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all resize-none
        bg-white text-slate-800 placeholder-slate-300
        focus:border-blue-400 focus:ring-2 focus:ring-blue-100
        dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600
        dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
        ${error
          ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20'
          : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
        }`}
    />
  )
}

/** Toast notification */
function Toast({ message, type = 'success', onClose }) {
  return (
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3
        rounded-2xl shadow-xl text-[13px] font-semibold min-w-[280px] max-w-[90vw]
        ${type === 'success' ? 'bg-emerald-600 text-white' : 'bg-rose-600 text-white'}`}
      style={{ animation: 'slideUpToast .25s ease' }}
    >
      {type === 'success'
        ? <Check className="w-4 h-4 flex-shrink-0" />
        : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose} className="opacity-75 hover:opacity-100 transition-opacity">
        <X className="w-4 h-4" />
      </button>
      <style>{`
        @keyframes slideUpToast {
          from { opacity:0; transform:translateX(-50%) translateY(12px); }
          to   { opacity:1; transform:translateX(-50%) translateY(0); }
        }
      `}</style>
    </div>
  )
}

/** Confirm delete modal */
function DeleteModal({ item, onConfirm, onCancel, loading }) {
  if (!item) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/50 z-40 backdrop-blur-sm" onClick={onCancel} />
      <div
        className="fixed inset-x-4 top-1/2 -translate-y-1/2 z-50 max-w-md mx-auto
          rounded-2xl bg-white dark:bg-[#1a1f35] border border-slate-200 dark:border-[rgba(99,102,241,0.2)]
          shadow-2xl p-6"
        style={{ animation: 'modalPop .2s ease' }}
      >
        <style>{`@keyframes modalPop{from{opacity:0;transform:translateY(-50%) scale(0.95)}to{opacity:1;transform:translateY(-50%) scale(1)}}`}</style>
        <div className="w-12 h-12 rounded-2xl bg-rose-100 dark:bg-rose-500/15 flex items-center justify-center mb-4">
          <Trash2 className="w-6 h-6 text-rose-600 dark:text-rose-400" />
        </div>
        <h3 className="text-[16px] font-bold text-slate-800 dark:text-slate-100 mb-1">Delete News</h3>
        <p className="text-[13px] text-slate-500 dark:text-slate-400 mb-5">
          Are you sure you want to delete <strong className="text-slate-700 dark:text-slate-200">"{item.news_title}"</strong>? This action cannot be undone.
        </p>
        <div className="flex gap-3">
          <button onClick={onCancel}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700
              hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors">
            Cancel
          </button>
          <button onClick={onConfirm} disabled={loading}
            className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
              bg-rose-600 text-white hover:bg-rose-700 disabled:opacity-70 transition-colors">
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Trash2 className="w-4 h-4" />}
            Delete
          </button>
        </div>
      </div>
    </>
  )
}

// ─── FILE UPLOAD ZONE ─────────────────────────────────────────────────────────
function FileUploadZone({ files, onChange, error }) {
  const inputRef = useRef(null)
  const [dragging, setDragging] = useState(false)

  const handleDrop = (e) => {
    e.preventDefault()
    setDragging(false)
    const dropped = Array.from(e.dataTransfer.files)
    onChange(dropped)
  }

  const handleChange = (e) => {
    onChange(Array.from(e.target.files))
  }

  const removeFile = (idx) => {
    onChange(files.filter((_, i) => i !== idx))
  }

  return (
    <div className="space-y-2">
      <div
        onDragOver={e => { e.preventDefault(); setDragging(true) }}
        onDragLeave={() => setDragging(false)}
        onDrop={handleDrop}
        onClick={() => inputRef.current?.click()}
        className={`relative flex flex-col items-center justify-center gap-2 p-5 rounded-xl border-2 border-dashed
          cursor-pointer transition-all select-none
          ${dragging
            ? 'border-blue-400 bg-blue-50 dark:bg-blue-500/10 scale-[1.01]'
            : error
              ? 'border-rose-400 bg-rose-50/40 dark:bg-rose-500/5'
              : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)] bg-slate-50/60 dark:bg-white/[0.02] hover:border-blue-300 hover:bg-blue-50/40 dark:hover:bg-blue-500/5'
          }`}
      >
        <input
          ref={inputRef}
          type="file"
          multiple
          className="hidden"
          onChange={handleChange}
          accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
        />
        <div className={`w-10 h-10 rounded-xl flex items-center justify-center transition-colors
          ${dragging ? 'bg-blue-100 dark:bg-blue-500/20' : 'bg-slate-100 dark:bg-slate-800'}`}>
          <Upload className={`w-5 h-5 ${dragging ? 'text-blue-600 dark:text-blue-400' : 'text-slate-400'}`} />
        </div>
        <div className="text-center">
          <p className="text-[13px] font-semibold text-slate-600 dark:text-slate-300">
            {dragging ? 'Drop files here' : 'Click or drag files to upload'}
          </p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">PDF, DOC, JPG, PNG supported</p>
        </div>
      </div>

      {/* File list */}
      {files.length > 0 && (
        <div className="space-y-1.5">
          {files.map((file, idx) => (
            <div key={idx}
              className="flex items-center gap-2.5 px-3 py-2 rounded-lg bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20">
              <FileText className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
              <span className="flex-1 text-[12px] font-medium text-slate-700 dark:text-slate-200 truncate">{file.name}</span>
              <span className="text-[10px] text-slate-400 flex-shrink-0">{(file.size / 1024).toFixed(0)} KB</span>
              <button onClick={(e) => { e.stopPropagation(); removeFile(idx) }}
                className="p-0.5 rounded text-slate-400 hover:text-rose-500 transition-colors flex-shrink-0">
                <X className="w-3.5 h-3.5" />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

// ─── NEWS TABLE ROW (Desktop) ─────────────────────────────────────────────────
function DesktopNewsRow({ item, idx, onDelete }) {
  const sc = sessionColor(item.session)
  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors group">
      {/* S.No */}
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12 font-medium">{idx}</td>

      {/* Title */}
      <td className="px-4 py-3 max-w-[200px]">
        <div className="flex items-start gap-2">
          <div className="w-7 h-7 rounded-lg bg-blue-50 dark:bg-blue-500/10 flex items-center justify-center flex-shrink-0 mt-0.5">
            <Newspaper className="w-3.5 h-3.5 text-blue-600 dark:text-blue-400" />
          </div>
          <div>
            <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 leading-snug line-clamp-2">{item.news_title}</p>
            <span className={`inline-flex items-center gap-1 mt-1 px-2 py-0.5 rounded-full text-[10px] font-bold ${sc.bg} ${sc.text}`}>
              <span className={`w-1.5 h-1.5 rounded-full ${sc.dot}`} />
              {item.session}
            </span>
          </div>
        </div>
      </td>

      {/* Link */}
      <td className="px-4 py-3 max-w-[160px]">
        {item.new_link ? (
          <a href={item.new_link} target="_blank" rel="noreferrer"
            className="flex items-center gap-1.5 text-[12px] text-blue-600 dark:text-blue-400 hover:underline truncate group/link">
            <ExternalLink className="w-3 h-3 flex-shrink-0 group-hover/link:translate-x-0.5 transition-transform" />
            <span className="truncate">{item.new_link.replace(/^https?:\/\//, '')}</span>
          </a>
        ) : (
          <span className="text-[12px] text-slate-300 dark:text-slate-600 italic">No link</span>
        )}
      </td>

      {/* Description */}
      <td className="px-4 py-3 max-w-[220px]">
        <p className="text-[12px] text-slate-500 dark:text-slate-400 line-clamp-2 leading-relaxed">{item.news_description}</p>
      </td>

      {/* Date */}
      <td className="px-4 py-3 whitespace-nowrap">
        <div className="flex items-center gap-1.5">
          <Calendar className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
          <span className="text-[12px] font-medium text-slate-600 dark:text-slate-300">{item.NewsDate}</span>
        </div>
      </td>

      {/* Action */}
      <td className="px-4 py-3 text-center">
        <button onClick={() => onDelete(item)}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
            text-rose-600 bg-rose-50 hover:bg-rose-100 dark:text-rose-400 dark:bg-rose-500/10 dark:hover:bg-rose-500/20
            transition-all active:scale-95">
          <Trash2 className="w-3.5 h-3.5" />
          Delete
        </button>
      </td>
    </tr>
  )
}

// ─── NEWS MOBILE CARD ─────────────────────────────────────────────────────────
function MobileNewsCard({ item, idx, onDelete }) {
  const [expanded, setExpanded] = useState(false)
  const sc = sessionColor(item.session)

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-start gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        <div className="w-8 h-8 rounded-xl bg-blue-50 dark:bg-blue-500/10 flex items-center justify-center flex-shrink-0 mt-0.5">
          <Newspaper className="w-4 h-4 text-blue-600 dark:text-blue-400" />
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-snug line-clamp-2 pr-2">{item.news_title}</p>
          <div className="flex items-center gap-2 mt-1.5 flex-wrap">
            <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-bold ${sc.bg} ${sc.text}`}>
              <span className={`w-1.5 h-1.5 rounded-full ${sc.dot}`} />
              {item.session}
            </span>
            <span className="flex items-center gap-1 text-[11px] text-slate-400">
              <Calendar className="w-3 h-3" />{item.NewsDate}
            </span>
          </div>
        </div>
        <span className={`w-5 h-5 flex items-center justify-center text-slate-400 transition-transform duration-200 flex-shrink-0 mt-1 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Short description preview */}
      <div className="px-4 pb-3">
        <p className="text-[12px] text-slate-400 dark:text-slate-500 line-clamp-1 leading-relaxed">{item.news_description}</p>
      </div>

      {/* Expanded detail */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 pt-3 pb-4 space-y-3">
          {/* Full description */}
          <div className="rounded-lg bg-slate-50 dark:bg-white/[0.03] border border-slate-100 dark:border-[rgba(99,102,241,0.1)] p-3">
            <p className="text-[11px] font-bold uppercase tracking-wide text-slate-400 mb-1 flex items-center gap-1">
              <AlignLeft className="w-3 h-3" /> Description
            </p>
            <p className="text-[13px] text-slate-600 dark:text-slate-300 leading-relaxed">{item.news_description}</p>
          </div>

          {/* Link */}
          {item.new_link && (
            <div className="rounded-lg bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20 p-3">
              <p className="text-[11px] font-bold uppercase tracking-wide text-blue-600 dark:text-blue-400 mb-1 flex items-center gap-1">
                <Link2 className="w-3 h-3" /> Link
              </p>
              <a href={item.new_link} target="_blank" rel="noreferrer"
                className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline break-all flex items-center gap-1">
                <ExternalLink className="w-3 h-3 flex-shrink-0" />
                {item.new_link}
              </a>
            </div>
          )}

          {/* Delete button */}
          <button onClick={() => onDelete(item)}
            className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
              text-rose-600 bg-rose-50 hover:bg-rose-100 dark:text-rose-400 dark:bg-rose-500/10 dark:hover:bg-rose-500/20
              transition-all active:scale-95 border border-rose-100 dark:border-rose-500/20">
            <Trash2 className="w-4 h-4" />
            Delete this news
          </button>
        </div>
      )}
    </div>
  )
}

// ─── MAIN PAGE COMPONENT ──────────────────────────────────────────────────────
export default function UploadNews() {
  // Form state
  const [form, setForm] = useState({
    session: '',
    title: '',
    link: '',
    description: '',
    date: '',
  })
  const [files, setFiles] = useState([])
  const [errors, setErrors] = useState({})
  const [submitting, setSubmitting] = useState(false)

  // News list state
  const [newsList, setNewsList] = useState(INITIAL_NEWS)
  const [search, setSearch] = useState('')
  const [filterSession, setFilterSession] = useState('')

  // UI state
  const [toast, setToast] = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleteLoading, setDeleteLoading] = useState(false)
  const [formOpen, setFormOpen] = useState(true) // mobile: form section open/close

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Field update helper ──
  const setField = (key) => (e) => {
    setForm(p => ({ ...p, [key]: e.target.value }))
    setErrors(p => ({ ...p, [key]: undefined }))
  }

  // ── Validate form ──
  const validate = () => {
    const err = {}
    if (!form.session)      err.session     = 'Please select a session'
    if (!form.title.trim()) err.title       = 'News title is required'
    if (!form.description.trim()) err.description = 'Description is required'
    if (files.length === 0) err.files       = 'Please upload at least one file'
    return err
  }

  // ── Submit upload ──
  const handleSubmit = useCallback(() => {
    const err = validate()
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setSubmitting(true)

    // Simulate API call
    setTimeout(() => {
      const newItem = {
        title_id: nextId++,
        news_title: form.title,
        new_link: form.link,
        news_description: form.description,
        NewsDate: form.date || new Date().toLocaleDateString('en-GB', { day:'2-digit', month:'short', year:'numeric' }),
        session: form.session,
        fileName: files[0]?.name || 'document.pdf',
      }
      setNewsList(p => [newItem, ...p])
      setForm({ session: '', title: '', link: '', description: '', date: '' })
      setFiles([])
      setSubmitting(false)
      showToast('News uploaded successfully!')
    }, 900)
  }, [form, files])

  // ── Delete news ──
  const handleDeleteConfirm = useCallback(() => {
    if (!deleteTarget) return
    setDeleteLoading(true)
    setTimeout(() => {
      setNewsList(p => p.filter(n => n.title_id !== deleteTarget.title_id))
      setDeleteLoading(false)
      setDeleteTarget(null)
      showToast('News deleted successfully!')
    }, 700)
  }, [deleteTarget])

  // ── Filtered list ──
  const filtered = useMemo(() => {
    return newsList.filter(item => {
      const matchSearch = !search || item.news_title.toLowerCase().includes(search.toLowerCase()) ||
        item.news_description.toLowerCase().includes(search.toLowerCase())
      const matchSession = !filterSession || item.session === filterSession
      return matchSearch && matchSession
    })
  }, [newsList, search, filterSession])

  const formHasData = form.session || form.title || form.description || files.length > 0

  // ── Reset form ──
  const resetForm = () => {
    setForm({ session: '', title: '', link: '', description: '', date: '' })
    setFiles([])
    setErrors({})
  }

  return (
    <div className="space-y-5 pb-10">

      {/* ── Page Title ─────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2.5">
            <span className="w-8 h-8 rounded-xl bg-blue-600 flex items-center justify-center flex-shrink-0">
              <Newspaper className="w-4 h-4 text-white" />
            </span>
            Upload News
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-1 ml-10">
            Publish school news, notices &amp; announcements for students and parents.
          </p>
        </div>
        <div className="flex items-center gap-2 ml-10 sm:ml-0">
          <span className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-[12px] font-semibold
            bg-blue-50 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 border border-blue-100 dark:border-blue-500/20">
            <Hash className="w-3.5 h-3.5" />
            {newsList.length} total
          </span>
        </div>
      </div>

      {/* ── UPLOAD FORM CARD ─────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Card Header — clickable on mobile to collapse */}
        <button
          type="button"
          onClick={() => setFormOpen(p => !p)}
          className="w-full flex items-center gap-3 px-5 py-4 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)]
            bg-gradient-to-r from-blue-50/80 via-white to-indigo-50/40 dark:from-[#1e2238] dark:via-[#1a1f35] dark:to-[#1e2238]
            text-left hover:bg-blue-50/50 transition-colors sm:cursor-default"
        >
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Upload className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Upload New News / Notice</span>
          {formHasData && (
            <span className="hidden sm:inline text-[11px] font-semibold px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400">
              Unsaved
            </span>
          )}
          {/* Mobile toggle */}
          <ChevronDown className={`sm:hidden w-4 h-4 text-slate-400 transition-transform duration-200 ${formOpen ? 'rotate-180' : ''}`} />
        </button>

        {/* Form body */}
        <div className={`${formOpen ? 'block' : 'hidden sm:block'}`}>
          <div className="p-5 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">

            {/* Session */}
            <Field label="Session" error={errors.session} required>
              <NativeSelect value={form.session} onChange={setField('session')} placeholder="-- Select Session --" error={errors.session}>
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </Field>

            {/* News Title */}
            <Field label="News Title" error={errors.title} required>
              <TextInput
                value={form.title}
                onChange={setField('title')}
                placeholder="Enter news title..."
                error={errors.title}
                icon={Tag}
              />
            </Field>

            {/* News Link */}
            <Field label="News Link" hint="optional">
              <TextInput
                value={form.link}
                onChange={setField('link')}
                placeholder="https://example.com/news"
                icon={Link2}
                type="url"
              />
            </Field>

            {/* Date */}
            <Field label="Date" hint="optional">
              <TextInput
                value={form.date}
                onChange={setField('date')}
                placeholder="dd/mm/yyyy"
                icon={Calendar}
                type="date"
              />
            </Field>

            {/* Description — spans 2 cols on lg */}
            <div className="sm:col-span-2">
              <Field label="News Description" error={errors.description} required>
                <TextArea
                  value={form.description}
                  onChange={setField('description')}
                  placeholder="Write full news description here..."
                  error={errors.description}
                  rows={3}
                />
              </Field>
            </div>

            {/* File Upload — full width */}
            <div className="sm:col-span-2 lg:col-span-3">
              <Field label="Upload Files" error={errors.files} required hint="PDF, DOC, images">
                <FileUploadZone files={files} onChange={setFiles} error={errors.files} />
              </Field>
            </div>
          </div>

          {/* Form footer */}
          <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/40 dark:bg-white/[0.01] flex flex-col sm:flex-row gap-3 sm:items-center">
            <button
              type="button"
              onClick={handleSubmit}
              disabled={submitting}
              className="flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl text-[13px] font-semibold text-white
                bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700
                shadow-md shadow-blue-500/20 transition-all active:scale-95 disabled:opacity-70"
            >
              {submitting
                ? <><Loader2 className="w-4 h-4 animate-spin" /> Uploading...</>
                : <><Upload className="w-4 h-4" /> Upload News</>}
            </button>
            <button
              type="button"
              onClick={resetForm}
              disabled={submitting}
              className="flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl text-[13px] font-semibold
                text-slate-600 bg-slate-100 hover:bg-slate-200 dark:text-slate-300 dark:bg-slate-800 dark:hover:bg-slate-700
                transition-colors disabled:opacity-50"
            >
              <RefreshCw className="w-3.5 h-3.5" /> Reset
            </button>
            <p className="text-[11px] text-slate-400 dark:text-slate-500 sm:ml-auto flex items-center gap-1">
              <Info className="w-3 h-3 flex-shrink-0" />
              Fields marked <span className="text-rose-500 font-bold mx-0.5">*</span> are required
            </p>
          </div>
        </div>
      </div>

      {/* ── NEWS LIST ─────────────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* List header with search + filter */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-4 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/60 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0">
            <span className="w-1 h-5 rounded-full bg-emerald-500 flex-shrink-0" />
            <BookOpen className="w-4 h-4 text-emerald-600 dark:text-emerald-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Published News</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-400 flex-shrink-0">
              {filtered.length} record{filtered.length !== 1 ? 's' : ''}
            </span>
          </div>

          {/* Filter row */}
          <div className="flex gap-2 flex-wrap sm:flex-nowrap">
            {/* Session filter */}
            <div className="relative w-36 flex-shrink-0">
              <select
                value={filterSession}
                onChange={e => setFilterSession(e.target.value)}
                className="w-full appearance-none pl-3 pr-7 py-1.5 text-[12px] rounded-lg border outline-none transition-all
                  bg-white text-slate-700 border-slate-200 dark:bg-[#1e2238] dark:text-slate-200
                  dark:border-[rgba(99,102,241,0.25)] focus:border-blue-400 focus:ring-2 focus:ring-blue-100 cursor-pointer"
              >
                <option value="">All Sessions</option>
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
              <ChevronDown className="absolute right-2 top-1/2 -translate-y-1/2 w-3 h-3 text-slate-400 pointer-events-none" />
            </div>

            {/* Search */}
            <div className="relative flex-1 min-w-[160px]">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search news..."
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

        {/* ── DESKTOP TABLE ── */}
        <div className="hidden md:block overflow-x-auto">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 gap-3 text-slate-400 dark:text-slate-600">
              <div className="w-12 h-12 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
                <Newspaper className="w-6 h-6 opacity-40" />
              </div>
              <div className="text-center">
                <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">
                  {search || filterSession ? 'No matching news found' : 'No news published yet'}
                </p>
                <p className="text-[12px] mt-1">
                  {search || filterSession ? 'Try adjusting your search or filter.' : 'Upload a news item using the form above.'}
                </p>
              </div>
              {(search || filterSession) && (
                <button onClick={() => { setSearch(''); setFilterSession('') }}
                  className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline">
                  Clear filters
                </button>
              )}
            </div>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                  {['S.No.', 'News Title', 'News Link', 'Description', 'Date', 'Action'].map((h, i) => (
                    <th key={i} className={`px-4 py-3 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap
                      ${i === 0 ? 'text-center w-12' : i === 5 ? 'text-center' : 'text-left'}`}>
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.map((item, i) => (
                  <DesktopNewsRow key={item.title_id} item={item} idx={i + 1} onDelete={setDeleteTarget} />
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* ── MOBILE CARDS ── */}
        <div className="md:hidden p-4 space-y-3">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12 gap-3 text-slate-400 dark:text-slate-600">
              <div className="w-12 h-12 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
                <Newspaper className="w-6 h-6 opacity-40" />
              </div>
              <div className="text-center">
                <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">
                  {search || filterSession ? 'No matching news found' : 'No news yet'}
                </p>
                <p className="text-[12px] mt-1">
                  {search || filterSession ? 'Try adjusting your search.' : 'Upload using the form above.'}
                </p>
              </div>
            </div>
          ) : (
            <>
              <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5">
                <Info className="w-3.5 h-3.5 flex-shrink-0" />
                Tap a card to see full details &amp; actions.
              </p>
              {filtered.map((item, i) => (
                <MobileNewsCard key={item.title_id} item={item} idx={i + 1} onDelete={setDeleteTarget} />
              ))}
            </>
          )}
        </div>

        {/* Table footer */}
        {filtered.length > 0 && (
          <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
            <p className="text-[12px] text-slate-400 dark:text-slate-500">
              Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
              <span className="font-semibold text-slate-700 dark:text-slate-300">{newsList.length}</span> records
            </p>
            {(search || filterSession) && (
              <button onClick={() => { setSearch(''); setFilterSession('') }}
                className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
                <X className="w-3 h-3" /> Clear filters
              </button>
            )}
          </div>
        )}
      </div>

      {/* ── Delete Confirm Modal ──────────────────────────────────────────── */}
      <DeleteModal
        item={deleteTarget}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteTarget(null)}
        loading={deleteLoading}
      />

      {/* ── Toast ────────────────────────────────────────────────────────── */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
