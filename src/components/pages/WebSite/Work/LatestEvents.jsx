/**
 * LatestEvents.jsx
 * Folder: src/pages/Configuration/LatestEvents.jsx
 *
 * Converts legacy ASPX "Latest events upload" page to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session dropdown
 *  - Event Name, Event Date, Description fields
 *  - Multiple image upload with live preview + remove
 *  - Validation before upload (mirrors old validate() JS function)
 *  - Events GridView -> desktop table / mobile cards
 *  - Delete with confirm modal
 *  - Search + session filter on list
 */

import { useState, useMemo, useCallback, useRef } from 'react'
import {
  Megaphone, CalendarDays, Image as ImageIcon, Upload, Trash2, X, Check,
  AlertCircle, Loader2, ChevronDown, Plus, Search, RefreshCw, Filter,
  SlidersHorizontal, Info, FileText, Images, Calendar1, ImageOff
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ───────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

let nextId = 1004
const INITIAL_EVENTS = [
  {
    id: 1001,
    session: '2025-26',
    event_name: 'Annual Sports Day',
    event_date: '2026-02-14',
    description: 'Inter-house athletics, track & field events and prize distribution.',
    images: [
      'https://images.unsplash.com/photo-1517649763962-0c623066013b?w=300&h=200&fit=crop',
      'https://images.unsplash.com/photo-1461896836934-ffe607ba8211?w=300&h=200&fit=crop',
    ],
  },
  {
    id: 1002,
    session: '2025-26',
    event_name: 'Science Exhibition',
    event_date: '2026-01-22',
    description: 'Student projects on renewable energy, robotics & environment.',
    images: [
      'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=300&h=200&fit=crop',
    ],
  },
  {
    id: 1003,
    session: '2024-25',
    event_name: 'Annual Day Celebration',
    event_date: '2025-12-18',
    description: 'Cultural programs, dance performances and award ceremony for the academic year.',
    images: [
      'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=300&h=200&fit=crop',
      'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300&h=200&fit=crop',
      'https://images.unsplash.com/photo-1459749411175-04bf480bdd23?w=300&h=200&fit=crop',
    ],
  },
]

// ─── PRIMITIVE COMPONENTS (matches StrengthReport / StudentRegistration tokens) ──

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

function TextInput({ value, onChange, placeholder, error, type = 'text', icon: Icon, ...rest }) {
  return (
    <div className="relative">
      {Icon && <Icon className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />}
      <input
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className={`w-full ${Icon ? 'pl-9' : 'pl-3'} pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
          bg-white text-slate-800 placeholder-slate-300
          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
          dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
          ${error
            ? 'border-rose-400 ring-2 ring-rose-100'
            : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
          }`}
        {...rest}
      />
    </div>
  )
}

function Field({ label, error, required, children, hint }) {
  return (
    <div className="flex flex-col gap-1">
      <label className="text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">
        {label}{required && <span className="text-rose-500 ml-0.5">*</span>}
      </label>
      {children}
      {hint && !error && (
        <p className="text-[11px] text-slate-400 dark:text-slate-500">{hint}</p>
      )}
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

// ─── HELPERS ────────────────────────────────────────────────────────────────
const formatDate = (iso) => {
  if (!iso) return '—'
  const d = new Date(iso)
  if (isNaN(d.getTime())) return iso
  return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
}

// ─── IMAGE PREVIEW GRID (for new uploads) ────────────────────────────────────
function ImagePreviewGrid({ files, onRemove }) {
  if (files.length === 0) return null
  return (
    <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-6 gap-2 mt-3">
      {files.map((f, i) => (
        <div key={i} className="relative group rounded-lg overflow-hidden border border-slate-200 dark:border-[rgba(99,102,241,0.2)] aspect-[4/3] bg-slate-100 dark:bg-slate-800">
          <img src={f.preview} alt={f.file.name} className="w-full h-full object-cover" />
          <button
            type="button"
            onClick={() => onRemove(i)}
            className="absolute top-1 right-1 w-5 h-5 rounded-full bg-black/60 text-white flex items-center justify-center
              opacity-90 hover:opacity-100 active:scale-90 transition-all"
            aria-label="Remove image"
          >
            <X className="w-3 h-3" />
          </button>
          <span className="absolute bottom-0 inset-x-0 bg-black/50 text-white text-[9px] px-1.5 py-0.5 truncate">
            {f.file.name}
          </span>
        </div>
      ))}
    </div>
  )
}

// ─── EVENT IMAGE STRIP (for existing event row/card) ─────────────────────────
function EventImages({ images, size = 'sm' }) {
  if (!images || images.length === 0) {
    return (
      <span className="inline-flex items-center justify-center w-10 h-10 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-300 dark:text-slate-600 flex-shrink-0">
        <ImageOff className="w-4 h-4" />
      </span>
    )
  }
  const dim = size === 'sm' ? 'w-10 h-10' : 'w-14 h-14'
  return (
    <div className="flex items-center -space-x-2 flex-shrink-0">
      {images.slice(0, 3).map((img, i) => (
        <img
          key={i}
          src={img}
          alt=""
          className={`${dim} rounded-lg object-cover border-2 border-white dark:border-[#1a1f35] shadow-sm`}
          style={{ zIndex: 3 - i }}
        />
      ))}
      {images.length > 3 && (
        <span className={`${dim} rounded-lg border-2 border-white dark:border-[#1a1f35] bg-slate-700 text-white text-[10px] font-bold flex items-center justify-center shadow-sm`}>
          +{images.length - 3}
        </span>
      )}
    </div>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ row, idx, onDelete }) {
  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{idx}</td>
      <td className="px-4 py-3">
        <EventImages images={row.images} />
      </td>
      <td className="px-4 py-3 max-w-xs">
        <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 leading-snug">{row.event_name}</p>
        <p className="text-[12px] text-slate-400 dark:text-slate-500 line-clamp-1 mt-0.5">{row.description}</p>
      </td>
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center justify-center px-2.5 py-1 rounded-lg text-[11px] font-semibold bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400">
          {row.session}
        </span>
      </td>
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center gap-1.5 text-[12px] font-semibold text-slate-600 dark:text-slate-300 tabular-nums">
          <CalendarDays className="w-3.5 h-3.5 text-slate-400" />
          {formatDate(row.event_date)}
        </span>
      </td>
      <td className="px-4 py-3 text-center">
        <button
          type="button"
          onClick={() => onDelete(row)}
          className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
            bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20
            transition-colors active:scale-95"
        >
          <Trash2 className="w-3.5 h-3.5" /> Delete
        </button>
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────
function MobileCard({ row, idx, onDelete }) {
  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      <div className="flex items-start gap-3 px-4 py-3.5">
        <span className="w-7 h-7 flex-shrink-0 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-400 dark:text-slate-500 text-[11px] font-bold flex items-center justify-center">
          {idx}
        </span>

        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight">{row.event_name}</p>
          <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1 line-clamp-2">{row.description}</p>

          <div className="flex items-center gap-2 mt-2 flex-wrap">
            <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-slate-600 dark:text-slate-300 tabular-nums">
              <CalendarDays className="w-3.5 h-3.5 text-slate-400" /> {formatDate(row.event_date)}
            </span>
            <span className="inline-flex items-center justify-center px-2 py-0.5 rounded-md text-[10px] font-semibold bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400">
              {row.session}
            </span>
          </div>
        </div>

        <EventImages images={row.images} />
      </div>

      <div className="px-4 pb-3.5 -mt-1">
        <button
          type="button"
          onClick={() => onDelete(row)}
          className="w-full flex items-center justify-center gap-2 py-2 rounded-xl text-[12px] font-semibold
            bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20
            transition-colors active:scale-95"
        >
          <Trash2 className="w-3.5 h-3.5" /> Delete Event
        </button>
      </div>
    </div>
  )
}

// ─── DELETE CONFIRM MODAL ─────────────────────────────────────────────────────
function ConfirmDeleteModal({ event, onCancel, onConfirm, loading }) {
  if (!event) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onCancel} />
      <div className="fixed inset-0 z-50 flex items-center justify-center px-4">
        <div className="w-full max-w-sm rounded-2xl bg-white dark:bg-[#1a1f35] border border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl p-5"
          style={{ animation: 'popIn .2s ease' }}
        >
          <style>{`@keyframes popIn{from{opacity:0;transform:scale(.96)}to{opacity:1;transform:scale(1)}}`}</style>
          <div className="flex items-center gap-3 mb-3">
            <span className="w-10 h-10 rounded-xl bg-rose-50 dark:bg-rose-500/10 text-rose-600 dark:text-rose-400 flex items-center justify-center flex-shrink-0">
              <Trash2 className="w-5 h-5" />
            </span>
            <div>
              <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Delete event?</p>
              <p className="text-[12px] text-slate-400 dark:text-slate-500">This action cannot be undone.</p>
            </div>
          </div>
          <p className="text-[13px] text-slate-600 dark:text-slate-300 bg-slate-50 dark:bg-white/[0.03] rounded-lg px-3 py-2 mb-4">
            "{event.event_name}" — {formatDate(event.event_date)}
          </p>
          <div className="flex gap-3">
            <button type="button" onClick={onCancel}
              className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
              Cancel
            </button>
            <button type="button" onClick={onConfirm} disabled={loading}
              className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white
                bg-rose-600 hover:bg-rose-700 disabled:opacity-70 transition-all active:scale-95">
              {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Trash2 className="w-4 h-4" />}
              Delete
            </button>
          </div>
        </div>
      </div>
    </>
  )
}

// ─── MOBILE FILTER DRAWER ─────────────────────────────────────────────────────
function FilterDrawer({ open, onClose, sessionFilter, setSessionFilter }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onClose} />
      <div className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl"
        style={{ animation: 'drawerUp .25s ease' }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
        <div className="flex justify-center pt-3 pb-1">
          <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
        </div>
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Filter by Session</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5">
          <Field label="Session">
            <NativeSelect
              value={sessionFilter}
              onChange={e => setSessionFilter(e.target.value)}
              placeholder="All Sessions"
            >
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
        </div>
        <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <button type="button" onClick={onClose}
            className="w-full py-2.5 rounded-xl text-[13px] font-semibold text-white bg-blue-600 dark:bg-indigo-600 hover:bg-blue-700 dark:hover:bg-indigo-700 transition-all">
            Apply
          </button>
        </div>
      </div>
    </>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function LatestEvents() {
  // form state
  const [session, setSession] = useState('')
  const [eventName, setEventName] = useState('')
  const [eventDate, setEventDate] = useState('')
  const [description, setDescription] = useState('')
  const [files, setFiles] = useState([]) // { file, preview }
  const [errors, setErrors] = useState({})
  const [uploading, setUploading] = useState(false)
  const fileInputRef = useRef(null)

  // list state
  const [events, setEvents] = useState(INITIAL_EVENTS)
  const [search, setSearch] = useState('')
  const [sessionFilter, setSessionFilter] = useState('')
  const [filterOpen, setFilterOpen] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleting, setDeleting] = useState(false)

  const [toast, setToast] = useState(null)
  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── File handling ─────────────────────────────────────────────────────────
  const handleFiles = useCallback((selected) => {
    const list = Array.from(selected || [])
    const valid = list.filter(f => /image\/(jpeg|jpg|png)/i.test(f.type))
    if (valid.length !== list.length) {
      showToast('Only JPG / JPEG / PNG images are allowed.', 'error')
    }
    const withPreview = valid.map(f => ({ file: f, preview: URL.createObjectURL(f) }))
    setFiles(prev => [...prev, ...withPreview])
    setErrors(p => ({ ...p, files: undefined }))
  }, [])

  const handleFileInput = (e) => {
    handleFiles(e.target.files)
    e.target.value = '' // allow re-selecting same file
  }

  const handleDrop = (e) => {
    e.preventDefault()
    handleFiles(e.dataTransfer.files)
  }

  const removeFile = (idx) => {
    setFiles(prev => {
      const copy = [...prev]
      URL.revokeObjectURL(copy[idx].preview)
      copy.splice(idx, 1)
      return copy
    })
  }

  // ── Validate (mirrors legacy validate() JS) ────────────────────────────────
  const validate = () => {
    const err = {}
    if (!session) err.session = 'Please select a session'
    if (!eventName.trim()) err.eventName = 'Event name is required'
    if (!eventDate) err.eventDate = 'Event date is required'
    if (!description.trim()) err.description = 'Description is required'
    if (files.length === 0) err.files = 'Please choose at least one image'
    setErrors(err)
    return Object.keys(err).length === 0
  }

  // ── Upload (simulate API) ──────────────────────────────────────────────────
  const handleUpload = useCallback(() => {
    if (!validate()) {
      showToast('Please fill all required fields.', 'error')
      return
    }
    setUploading(true)

    // API integration placeholder:
    // const fd = new FormData()
    // fd.append('session', session) ... fd.append('files', file) per file
    // await fetch('/api/configuration/latest-events', { method: 'POST', body: fd })

    setTimeout(() => {
      const newEvent = {
        id: nextId++,
        session,
        event_name: eventName.trim(),
        event_date: eventDate,
        description: description.trim(),
        images: files.map(f => f.preview),
      }
      setEvents(prev => [newEvent, ...prev])

      // reset form
      setEventName(''); setEventDate(''); setDescription('')
      setFiles([]); setErrors({})
      setUploading(false)
      showToast('Event uploaded successfully!')
    }, 800)
  }, [session, eventName, eventDate, description, files])

  // ── Delete ───────────────────────────────────────────────────────────────
  const confirmDelete = () => {
    setDeleting(true)
    // API integration placeholder:
    // await fetch(`/api/configuration/latest-events/${deleteTarget.id}`, { method: 'DELETE' })
    setTimeout(() => {
      setEvents(prev => prev.filter(e => e.id !== deleteTarget.id))
      setDeleting(false)
      setDeleteTarget(null)
      showToast('Event deleted.')
    }, 600)
  }

  // ── Filtering ───────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    let list = events
    if (sessionFilter) list = list.filter(e => e.session === sessionFilter)
    if (search) {
      const q = search.toLowerCase()
      list = list.filter(e =>
        e.event_name.toLowerCase().includes(q) ||
        e.description.toLowerCase().includes(q)
      )
    }
    return list
  }, [events, search, sessionFilter])

  const handleReset = () => {
    setSession(''); setEventName(''); setEventDate(''); setDescription('')
    setFiles([]); setErrors({})
  }

  const activeFilters = sessionFilter ? 1 : 0

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ───────────────────────────────────────────────────── */}
      <div>
        <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
          <Megaphone className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
          Latest Events
        </h1>
        <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
          Upload school events with photos — shown on the website &amp; mobile app.
        </p>
      </div>

      {/* ── Upload Form Card ─────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Plus className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Add New Event</span>
        </div>

        <div className="p-5 space-y-4">
          {/* Row 1: Session / Event Name / Event Date */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
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

            <Field label="Event Name" error={errors.eventName} required>
              <TextInput
                value={eventName}
                onChange={e => { setEventName(e.target.value); setErrors(p => ({ ...p, eventName: undefined })) }}
                placeholder="e.g. Annual Sports Day"
                icon={Megaphone}
                error={errors.eventName}
              />
            </Field>

            <Field label="Event Date" error={errors.eventDate} required>
              <TextInput
                type="date"
                value={eventDate}
                onChange={e => { setEventDate(e.target.value); setErrors(p => ({ ...p, eventDate: undefined })) }}
                icon={Calendar1}
                error={errors.eventDate}
              />
            </Field>
          </div>

          {/* Row 2: Description */}
          <Field label="Description" error={errors.description} required>
            <textarea
              value={description}
              onChange={e => { setDescription(e.target.value); setErrors(p => ({ ...p, description: undefined })) }}
              rows={3}
              placeholder="Brief details about the event…"
              className={`w-full px-3 py-2 text-[13px] rounded-lg border outline-none transition-all resize-none
                bg-white text-slate-800 placeholder-slate-300
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                ${errors.description
                  ? 'border-rose-400 ring-2 ring-rose-100'
                  : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
                }`}
            />
          </Field>

          {/* Row 3: File Upload */}
          <Field label="Event Photos" error={errors.files} required hint="JPG, JPEG or PNG — multiple files allowed">
            <div
              onDragOver={e => e.preventDefault()}
              onDrop={handleDrop}
              onClick={() => fileInputRef.current?.click()}
              className={`flex flex-col items-center justify-center gap-2 rounded-xl border-2 border-dashed
                px-4 py-6 text-center cursor-pointer transition-colors
                ${errors.files
                  ? 'border-rose-300 bg-rose-50/40 dark:border-rose-500/40 dark:bg-rose-500/5'
                  : 'border-slate-200 hover:border-blue-300 hover:bg-blue-50/30 dark:border-[rgba(99,102,241,0.25)] dark:hover:border-indigo-400 dark:hover:bg-indigo-500/5'
                }`}
            >
              <span className="w-10 h-10 rounded-xl bg-blue-50 dark:bg-indigo-500/10 text-blue-600 dark:text-indigo-400 flex items-center justify-center">
                <Upload className="w-5 h-5" />
              </span>
              <p className="text-[13px] font-semibold text-slate-600 dark:text-slate-300">
                Tap to choose photos <span className="hidden sm:inline">or drag &amp; drop</span>
              </p>
              <p className="text-[11px] text-slate-400 dark:text-slate-500">
                {files.length > 0 ? `${files.length} file${files.length > 1 ? 's' : ''} selected` : 'No files chosen'}
              </p>
              <input
                ref={fileInputRef}
                type="file"
                accept=".jpg,.jpeg,.png"
                multiple
                onChange={handleFileInput}
                className="hidden"
              />
            </div>
            <ImagePreviewGrid files={files} onRemove={removeFile} />
          </Field>

          {/* Actions */}
          <div className="flex flex-col sm:flex-row gap-3 pt-1">
            <button
              type="button"
              onClick={handleUpload}
              disabled={uploading}
              className="flex-1 sm:flex-initial flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl text-[13px] font-semibold text-white
                bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700
                transition-all active:scale-95 disabled:opacity-70"
            >
              {uploading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Upload className="w-4 h-4" />}
              Upload Event
            </button>
            <button
              type="button"
              onClick={handleReset}
              className="flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl text-[13px] font-semibold
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors"
            >
              <RefreshCw className="w-3.5 h-3.5" /> Clear
            </button>
          </div>
        </div>
      </div>

      {/* ── Events List Card ─────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Card Header */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
            <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
            <Images className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Uploaded Events</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
              {filtered.length} record{filtered.length !== 1 ? 's' : ''}
            </span>
          </div>

          {/* Desktop session filter */}
          <div className="hidden sm:block w-44 flex-shrink-0">
            <NativeSelect value={sessionFilter} onChange={e => setSessionFilter(e.target.value)} placeholder="All Sessions">
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </div>

          {/* Search */}
          <div className="relative w-full sm:w-52 flex-shrink-0">
            <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
            <input
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Search events…"
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

          {/* Mobile filter button */}
          <button
            type="button"
            onClick={() => setFilterOpen(true)}
            className="sm:hidden flex items-center justify-center gap-2 px-3 py-2 rounded-xl text-[12px] font-semibold
              bg-blue-50 text-blue-700 dark:bg-indigo-500/10 dark:text-indigo-400 border border-blue-100 dark:border-indigo-500/20"
          >
            <Filter className="w-3.5 h-3.5" />
            {sessionFilter || 'Session'}
            {activeFilters > 0 && (
              <span className="bg-blue-600 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilters}</span>
            )}
          </button>
        </div>

        {/* Info hint */}
        <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
          <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
          <p className="text-[12px] text-blue-700 dark:text-blue-400">
            Events shown here appear on the school website &amp; mobile app, latest first.
          </p>
        </div>

        {/* ── DESKTOP TABLE ── */}
        <div className="hidden md:block overflow-x-auto">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
              <Search className="w-6 h-6 opacity-40" />
              <span className="text-[13px]">No events found.</span>
            </div>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                  {['S.No.', 'Photos', 'Title', 'Session', 'Date', 'Action'].map((h, i) => (
                    <th key={i} className="px-4 py-2.5 text-center text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:w-12">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.map((row, i) => (
                  <DesktopRow key={row.id} row={row} idx={i + 1} onDelete={setDeleteTarget} />
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
              <span className="text-[13px]">No events found.</span>
            </div>
          ) : (
            filtered.map((row, i) => (
              <MobileCard key={row.id} row={row} idx={i + 1} onDelete={setDeleteTarget} />
            ))
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{events.length}</span> events
          </p>
          {(search || sessionFilter) && (
            <button onClick={() => { setSearch(''); setSessionFilter('') }}
              className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
              <X className="w-3 h-3" /> Clear filters
            </button>
          )}
        </div>
      </div>

      <FilterDrawer
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        sessionFilter={sessionFilter}
        setSessionFilter={setSessionFilter}
      />

      <ConfirmDeleteModal
        event={deleteTarget}
        onCancel={() => setDeleteTarget(null)}
        onConfirm={confirmDelete}
        loading={deleting}
      />

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
