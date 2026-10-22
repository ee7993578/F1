/**
 * PhotoGallery.jsx
 * Folder: src/pages/Admin/PhotoGallery/PhotoGallery.jsx
 *
 * Converts legacy ASPX "Photo Gallery" admin page to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session dropdown filter
 *  - Title, Date, Link, File Upload form
 *  - Upload button with validation
 *  - Grid view of gallery entries with Edit / Delete actions
 *  - Mobile: card-based layout, bottom-sheet filter/form
 *  - Desktop: form + table layout
 *  - Toast notifications
 *  - Loading / empty states
 */

import { useState, useMemo, useCallback, useRef } from 'react'
import {
  Image, Upload, Trash2, Edit3, Search, RefreshCw,
  X, Check, AlertCircle, Loader2, ChevronDown,
  Filter, SlidersHorizontal, Plus, Eye, EyeOff,
  Calendar, Link2, FileImage, Camera, FolderOpen,
  ChevronRight, MoreVertical, Download, Grid3X3,
  List, BookOpen, Building2, MapPin, Layers,
  Save, Ban, ExternalLink, Info
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ──────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const SCHOOL_INFO = {
  name: 'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

const INITIAL_GALLERY = [
  {
    id: 1, title: 'Annual Sports Day 2025', session: '2024-25',
    date: '15 Mar 2025', link: 'https://drive.google.com/sports2025',
    images: ['img1.jpg', 'img2.jpg', 'img3.jpg'],
    thumbnail: 'https://images.unsplash.com/photo-1551958219-acbc18fbdfbe?w=400&q=80',
  },
  {
    id: 2, title: 'Science Exhibition 2025', session: '2024-25',
    date: '10 Feb 2025', link: 'https://drive.google.com/science2025',
    images: ['sci1.jpg', 'sci2.jpg'],
    thumbnail: 'https://images.unsplash.com/photo-1532094349884-543559c47e28?w=400&q=80',
  },
  {
    id: 3, title: 'Independence Day Celebration', session: '2024-25',
    date: '15 Aug 2024', link: '',
    images: ['ind1.jpg'],
    thumbnail: 'https://images.unsplash.com/photo-1532375810709-75b1da00537c?w=400&q=80',
  },
  {
    id: 4, title: 'Republic Day Programme', session: '2023-24',
    date: '26 Jan 2024', link: 'https://drive.google.com/republic2024',
    images: ['rep1.jpg', 'rep2.jpg', 'rep3.jpg', 'rep4.jpg'],
    thumbnail: 'https://images.unsplash.com/photo-1543332164-6e82f355badc?w=400&q=80',
  },
  {
    id: 5, title: 'Annual Prize Distribution', session: '2023-24',
    date: '20 Dec 2023', link: '',
    images: ['prize1.jpg', 'prize2.jpg'],
    thumbnail: 'https://images.unsplash.com/photo-1523580846011-d3a5bc25702b?w=400&q=80',
  },
  {
    id: 6, title: 'Farewell Ceremony 2023', session: '2022-23',
    date: '10 Apr 2023', link: 'https://drive.google.com/farewell2023',
    images: ['fw1.jpg', 'fw2.jpg', 'fw3.jpg'],
    thumbnail: 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=400&q=80',
  },
]

// ─── HELPERS ──────────────────────────────────────────────────────────────────
const SESSION_COLORS = {
  '2025-26': { fg: '#1d4ed8', bg: '#dbeafe', ring: '#93c5fd' },
  '2024-25': { fg: '#7c3aed', bg: '#ede9fe', ring: '#c4b5fd' },
  '2023-24': { fg: '#059669', bg: '#d1fae5', ring: '#6ee7b7' },
  '2022-23': { fg: '#d97706', bg: '#fef3c7', ring: '#fcd34d' },
}
const sessionColor = (s) => SESSION_COLORS[s] || { fg: '#64748b', bg: '#f1f5f9', ring: '#cbd5e1' }

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
          ${error ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
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
          <span className="ml-1 text-[11px] normal-case font-normal text-slate-400 dark:text-slate-500">({hint})</span>
        )}
      </label>
      {children}
      {error && (
        <p className="flex items-center gap-1 text-[11px] text-rose-500">
          <AlertCircle className="w-3 h-3 flex-shrink-0" />{error}
        </p>
      )}
    </div>
  )
}

function TextInput({ value, onChange, placeholder, error, disabled, icon: Icon, type = 'text' }) {
  return (
    <div className="relative">
      {Icon && <Icon className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />}
      <input
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        disabled={disabled}
        className={`w-full ${Icon ? 'pl-9' : 'pl-3'} pr-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all
          bg-white text-slate-800 placeholder-slate-300
          focus:border-blue-400 focus:ring-2 focus:ring-blue-100
          dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600
          dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
          disabled:opacity-50 disabled:cursor-not-allowed
          ${error ? 'border-rose-400 ring-2 ring-rose-100' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
      />
    </div>
  )
}

function Toast({ message, type = 'success', onClose }) {
  return (
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-[100] flex items-center gap-3 px-5 py-3.5
        rounded-2xl shadow-2xl text-[13px] font-semibold min-w-[280px] max-w-[90vw]
        ${type === 'success' ? 'bg-emerald-600 text-white' : type === 'error' ? 'bg-rose-600 text-white' : 'bg-blue-600 text-white'}`}
      style={{ animation: 'slideUp .25s ease' }}
    >
      {type === 'success'
        ? <Check className="w-4 h-4 flex-shrink-0" />
        : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose} className="opacity-75 hover:opacity-100"><X className="w-4 h-4" /></button>
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(12px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
    </div>
  )
}

// ─── FILE UPLOAD ZONE ─────────────────────────────────────────────────────────
function FileUploadZone({ files, onFilesChange, error }) {
  const inputRef = useRef()
  const [dragging, setDragging] = useState(false)

  const handleDrop = (e) => {
    e.preventDefault()
    setDragging(false)
    const dropped = Array.from(e.dataTransfer.files).filter(f => f.type.startsWith('image/'))
    onFilesChange(prev => [...prev, ...dropped])
  }

  const handleChange = (e) => {
    const picked = Array.from(e.target.files)
    onFilesChange(prev => [...prev, ...picked])
    e.target.value = ''
  }

  const removeFile = (idx) => {
    onFilesChange(prev => prev.filter((_, i) => i !== idx))
  }

  return (
    <div className="space-y-2">
      <div
        onDragOver={(e) => { e.preventDefault(); setDragging(true) }}
        onDragLeave={() => setDragging(false)}
        onDrop={handleDrop}
        onClick={() => inputRef.current?.click()}
        className={`relative flex flex-col items-center justify-center gap-2 px-4 py-6 rounded-xl border-2 border-dashed cursor-pointer transition-all
          ${dragging
            ? 'border-blue-400 bg-blue-50 dark:bg-blue-500/10'
            : error
              ? 'border-rose-300 bg-rose-50/50 dark:bg-rose-500/5'
              : 'border-slate-200 hover:border-blue-300 hover:bg-blue-50/40 dark:border-[rgba(99,102,241,0.25)] dark:hover:bg-blue-500/5'
          }`}
      >
        <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${dragging ? 'bg-blue-100 text-blue-600' : 'bg-slate-100 text-slate-400 dark:bg-slate-800 dark:text-slate-500'}`}>
          <FileImage className="w-5 h-5" />
        </div>
        <div className="text-center">
          <p className="text-[13px] font-semibold text-slate-600 dark:text-slate-300">
            {dragging ? 'Drop images here' : 'Click to upload or drag & drop'}
          </p>
          <p className="text-[11px] text-slate-400 mt-0.5">PNG, JPG, WEBP up to 10MB each</p>
        </div>
        <input ref={inputRef} type="file" multiple accept="image/*" onChange={handleChange} className="hidden" />
      </div>

      {/* Preview chips */}
      {files.length > 0 && (
        <div className="flex flex-wrap gap-2 mt-1">
          {files.map((f, i) => (
            <div key={i} className="flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20 max-w-[180px]">
              <FileImage className="w-3 h-3 text-blue-500 flex-shrink-0" />
              <span className="text-[11px] text-blue-700 dark:text-blue-300 truncate font-medium">{f.name}</span>
              <button
                type="button"
                onClick={(e) => { e.stopPropagation(); removeFile(i) }}
                className="ml-0.5 text-blue-400 hover:text-rose-500 flex-shrink-0"
              >
                <X className="w-3 h-3" />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

// ─── GALLERY CARD (MOBILE + GRID) ─────────────────────────────────────────────
function GalleryCard({ item, onEdit, onDelete, compact = false }) {
  const { fg, bg } = sessionColor(item.session)
  const [menuOpen, setMenuOpen] = useState(false)

  return (
    <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm hover:shadow-md transition-all group">
      {/* Thumbnail */}
      <div className="relative aspect-video bg-slate-100 dark:bg-slate-800 overflow-hidden">
        {item.thumbnail ? (
          <img
            src={item.thumbnail}
            alt={item.title}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
            onError={(e) => { e.target.style.display = 'none' }}
          />
        ) : (
          <div className="flex items-center justify-center h-full">
            <Camera className="w-8 h-8 text-slate-300 dark:text-slate-600" />
          </div>
        )}
        {/* Image count badge */}
        <div className="absolute bottom-2 left-2 flex items-center gap-1 px-2 py-0.5 rounded-full bg-black/60 text-white text-[10px] font-semibold backdrop-blur-sm">
          <Image className="w-3 h-3" />
          {item.images.length} photo{item.images.length !== 1 ? 's' : ''}
        </div>
        {/* Action menu */}
        <div className="absolute top-2 right-2">
          <button
            type="button"
            onClick={() => setMenuOpen(p => !p)}
            className="w-7 h-7 rounded-full bg-black/50 backdrop-blur-sm flex items-center justify-center text-white hover:bg-black/70 transition-colors"
          >
            <MoreVertical className="w-3.5 h-3.5" />
          </button>
          {menuOpen && (
            <>
              <div className="fixed inset-0 z-10" onClick={() => setMenuOpen(false)} />
              <div className="absolute right-0 top-8 z-20 w-36 rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1e2238] shadow-xl overflow-hidden">
                <button
                  type="button"
                  onClick={() => { onEdit(item); setMenuOpen(false) }}
                  className="w-full flex items-center gap-2 px-3 py-2.5 text-[13px] text-slate-700 dark:text-slate-200 hover:bg-blue-50 dark:hover:bg-blue-500/10 transition-colors"
                >
                  <Edit3 className="w-3.5 h-3.5 text-blue-500" /> Edit
                </button>
                <button
                  type="button"
                  onClick={() => { onDelete(item.id); setMenuOpen(false) }}
                  className="w-full flex items-center gap-2 px-3 py-2.5 text-[13px] text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-500/10 transition-colors"
                >
                  <Trash2 className="w-3.5 h-3.5" /> Delete
                </button>
              </div>
            </>
          )}
        </div>
      </div>

      {/* Content */}
      <div className="p-3.5">
        <h3 className="text-[13px] font-bold text-slate-800 dark:text-slate-100 leading-snug line-clamp-2 mb-2">{item.title}</h3>
        <div className="flex items-center gap-2 flex-wrap">
          <span
            className="inline-flex items-center px-2 py-0.5 rounded-full text-[10px] font-bold"
            style={{ background: bg, color: fg }}
          >
            {item.session}
          </span>
          <span className="flex items-center gap-1 text-[11px] text-slate-400 dark:text-slate-500">
            <Calendar className="w-3 h-3" />{item.date}
          </span>
        </div>
        {item.link && (
          <a
            href={item.link}
            target="_blank"
            rel="noopener noreferrer"
            className="mt-2 flex items-center gap-1 text-[11px] text-blue-600 dark:text-blue-400 hover:underline"
          >
            <ExternalLink className="w-3 h-3 flex-shrink-0" />
            <span className="truncate">{item.link.replace(/^https?:\/\//, '')}</span>
          </a>
        )}

        {/* Actions */}
        <div className="flex gap-2 mt-3 pt-3 border-t border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
          <button
            type="button"
            onClick={() => onEdit(item)}
            className="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl text-[12px] font-semibold
              bg-blue-50 text-blue-700 hover:bg-blue-100 dark:bg-blue-500/10 dark:text-blue-300 dark:hover:bg-blue-500/20 transition-colors"
          >
            <Edit3 className="w-3.5 h-3.5" /> Edit
          </button>
          <button
            type="button"
            onClick={() => onDelete(item.id)}
            className="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl text-[12px] font-semibold
              bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20 transition-colors"
          >
            <Trash2 className="w-3.5 h-3.5" /> Delete
          </button>
        </div>
      </div>
    </div>
  )
}

// ─── TABLE ROW (DESKTOP) ──────────────────────────────────────────────────────
function TableRow({ item, idx, onEdit, onDelete }) {
  const { fg, bg } = sessionColor(item.session)
  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors group">
      {/* S.No */}
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{idx}</td>
      {/* Title + thumbnail */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-3">
          <div className="w-12 h-9 rounded-lg overflow-hidden bg-slate-100 dark:bg-slate-800 flex-shrink-0">
            {item.thumbnail
              ? <img src={item.thumbnail} alt="" className="w-full h-full object-cover" onError={e => e.target.style.display='none'} />
              : <div className="flex items-center justify-center h-full"><Camera className="w-4 h-4 text-slate-300" /></div>}
          </div>
          <div>
            <p className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 leading-tight">{item.title}</p>
            <p className="text-[11px] text-slate-400 flex items-center gap-1 mt-0.5">
              <Image className="w-3 h-3" />{item.images.length} photo{item.images.length !== 1 ? 's' : ''}
            </p>
          </div>
        </div>
      </td>
      {/* Date */}
      <td className="px-4 py-3 text-[12px] text-slate-600 dark:text-slate-300 whitespace-nowrap">
        <span className="flex items-center gap-1"><Calendar className="w-3.5 h-3.5 text-slate-400" />{item.date}</span>
      </td>
      {/* Session */}
      <td className="px-4 py-3">
        <span className="inline-flex items-center px-2.5 py-1 rounded-full text-[11px] font-bold" style={{ background: bg, color: fg }}>
          {item.session}
        </span>
      </td>
      {/* Link */}
      <td className="px-4 py-3 max-w-[160px]">
        {item.link
          ? <a href={item.link} target="_blank" rel="noopener noreferrer"
              className="flex items-center gap-1 text-[12px] text-blue-600 dark:text-blue-400 hover:underline truncate">
              <Link2 className="w-3 h-3 flex-shrink-0" /><span className="truncate">{item.link.replace(/^https?:\/\//, '')}</span>
            </a>
          : <span className="text-[12px] text-slate-300 dark:text-slate-600">—</span>}
      </td>
      {/* Actions */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => onEdit(item)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
              bg-blue-50 text-blue-700 hover:bg-blue-100 dark:bg-blue-500/10 dark:text-blue-300 dark:hover:bg-blue-500/20 transition-colors"
          >
            <Edit3 className="w-3.5 h-3.5" /> Edit
          </button>
          <button
            type="button"
            onClick={() => onDelete(item.id)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
              bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20 transition-colors"
          >
            <Trash2 className="w-3.5 h-3.5" /> Delete
          </button>
        </div>
      </td>
    </tr>
  )
}

// ─── FORM PANEL ───────────────────────────────────────────────────────────────
function GalleryForm({ initialValues, onSubmit, onCancel, loading }) {
  const isEdit = !!initialValues?.id
  const [form, setForm] = useState({
    session: initialValues?.session || '',
    title: initialValues?.title || '',
    date: initialValues?.date || '',
    link: initialValues?.link || '',
  })
  const [files, setFiles] = useState([])
  const [errors, setErrors] = useState({})

  const set = (key) => (e) => {
    setForm(p => ({ ...p, [key]: e.target.value }))
    setErrors(p => ({ ...p, [key]: undefined }))
  }

  const validate = () => {
    const err = {}
    if (!form.session) err.session = 'Session is required'
    if (!form.title.trim()) err.title = 'Title is required'
    if (!form.date.trim()) err.date = 'Date is required'
    if (!isEdit && files.length === 0) err.files = 'Please select at least one image'
    setErrors(err)
    return Object.keys(err).length === 0
  }

  const handleSubmit = () => {
    if (!validate()) return
    onSubmit({ ...form, files })
  }

  return (
    <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
      {/* Header */}
      <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
        <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
        <span className={`w-7 h-7 rounded-lg flex items-center justify-center flex-shrink-0 ${isEdit ? 'bg-amber-100 text-amber-600 dark:bg-amber-500/15 dark:text-amber-400' : 'bg-blue-100 text-blue-600 dark:bg-blue-500/15 dark:text-blue-400'}`}>
          {isEdit ? <Edit3 className="w-3.5 h-3.5" /> : <Plus className="w-3.5 h-3.5" />}
        </span>
        <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">
          {isEdit ? `Edit — ${initialValues.title}` : 'Add New Gallery'}
        </span>
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400 hover:text-slate-600 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        )}
      </div>

      {/* Fields */}
      <div className="p-5">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <Field label="Session" required error={errors.session}>
            <NativeSelect value={form.session} onChange={set('session')} placeholder="-- Select Session --" error={errors.session}>
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>

          <Field label="Title" required error={errors.title}>
            <TextInput
              value={form.title}
              onChange={set('title')}
              placeholder="e.g. Annual Sports Day 2025"
              icon={BookOpen}
              error={errors.title}
            />
          </Field>

          <Field label="Date" required error={errors.date}>
            <TextInput
              value={form.date}
              onChange={set('date')}
              placeholder="e.g. 15 Mar 2025"
              icon={Calendar}
              error={errors.date}
            />
          </Field>

          <div className="sm:col-span-2 lg:col-span-2">
            <Field label="Gallery Link" hint="optional — Google Drive, YouTube, etc.">
              <TextInput
                value={form.link}
                onChange={set('link')}
                placeholder="https://drive.google.com/..."
                icon={Link2}
              />
            </Field>
          </div>

          <div className="sm:col-span-2 lg:col-span-3">
            <Field label="Upload Photos" required={!isEdit} error={errors.files} hint={isEdit ? 'leave empty to keep existing' : undefined}>
              <FileUploadZone files={files} onFilesChange={setFiles} error={errors.files} />
            </Field>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col-reverse sm:flex-row gap-3 mt-5 pt-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          {onCancel && (
            <button
              type="button"
              onClick={onCancel}
              className="flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl text-[13px] font-semibold
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors"
            >
              <Ban className="w-4 h-4" /> Cancel
            </button>
          )}
          <button
            type="button"
            onClick={handleSubmit}
            disabled={loading}
            className="flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl text-[13px] font-semibold text-white
              bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700
              shadow-md shadow-blue-500/25 transition-all active:scale-95 disabled:opacity-70"
          >
            {loading
              ? <Loader2 className="w-4 h-4 animate-spin" />
              : isEdit ? <Save className="w-4 h-4" /> : <Upload className="w-4 h-4" />}
            {isEdit ? 'Save Changes' : 'Upload Gallery'}
          </button>
        </div>
      </div>
    </div>
  )
}

// ─── MOBILE FORM BOTTOM SHEET ─────────────────────────────────────────────────
function MobileFormSheet({ open, onClose, initialValues, onSubmit, loading }) {
  if (!open) return null
  const isEdit = !!initialValues?.id
  return (
    <>
      <div className="fixed inset-0 bg-black/50 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl max-h-[92vh] overflow-y-auto"
        style={{ animation: 'drawerUp .25s ease' }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
        <div className="flex justify-center pt-3 pb-1 sticky top-0 bg-white dark:bg-[#1a1f35] z-10">
          <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
        </div>
        <div className="px-4 pb-8">
          <GalleryForm
            initialValues={initialValues}
            onSubmit={(data) => { onSubmit(data); onClose() }}
            onCancel={onClose}
            loading={loading}
          />
        </div>
      </div>
    </>
  )
}

// ─── DELETE CONFIRM MODAL ─────────────────────────────────────────────────────
function DeleteModal({ item, onConfirm, onCancel }) {
  if (!item) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/50 z-40 backdrop-blur-sm" onClick={onCancel} />
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div className="w-full max-w-sm rounded-2xl bg-white dark:bg-[#1a1f35] border border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl p-6"
          style={{ animation: 'popIn .2s ease' }}>
          <style>{`@keyframes popIn{from{opacity:0;transform:scale(.9)}to{opacity:1;transform:scale(1)}}`}</style>
          <div className="w-12 h-12 rounded-2xl bg-rose-100 dark:bg-rose-500/15 flex items-center justify-center mx-auto mb-4">
            <Trash2 className="w-6 h-6 text-rose-600 dark:text-rose-400" />
          </div>
          <h3 className="text-[16px] font-bold text-slate-800 dark:text-slate-100 text-center mb-1">Delete Gallery?</h3>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 text-center mb-5">
            "<strong>{item.title}</strong>" and all its photos will be permanently removed.
          </p>
          <div className="flex gap-3">
            <button type="button" onClick={onCancel}
              className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
              Cancel
            </button>
            <button type="button" onClick={onConfirm}
              className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold text-white bg-rose-600 hover:bg-rose-700 shadow-md shadow-rose-500/25 transition-all active:scale-95">
              Delete
            </button>
          </div>
        </div>
      </div>
    </>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export { PhotoGallery as default } from '../WebsiteModule'
function LegacyPhotoGallery() {
  const [gallery, setGallery] = useState(INITIAL_GALLERY)
  const [filterSession, setFilterSession] = useState('')
  const [search, setSearch] = useState('')
  const [viewMode, setViewMode] = useState('grid') // 'grid' | 'list'
  const [editItem, setEditItem] = useState(null)
  const [deleteItem, setDeleteItem] = useState(null)
  const [formOpen, setFormOpen] = useState(false) // for mobile sheet
  const [loading, setLoading] = useState(false)
  const [toast, setToast] = useState(null)
  const [desktopFormVisible, setDesktopFormVisible] = useState(true)
  const [desktopEditMode, setDesktopEditMode] = useState(false)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Upload / Save ─────────────────────────────────────────────────────────
  const handleUpload = useCallback((data) => {
    if (editItem) {
      // Edit mode
      setLoading(true)
      setTimeout(() => {
        setGallery(prev => prev.map(g => g.id === editItem.id
          ? { ...g, ...data, images: data.files.length > 0 ? data.files.map(f => f.name) : g.images }
          : g))
        setEditItem(null)
        setDesktopEditMode(false)
        setLoading(false)
        showToast(`Gallery "${data.title}" updated successfully!`)
      }, 800)
    } else {
      // New upload
      setLoading(true)
      setTimeout(() => {
        const newItem = {
          id: Date.now(),
          ...data,
          images: data.files.map(f => f.name),
          thumbnail: null,
        }
        setGallery(prev => [newItem, ...prev])
        setLoading(false)
        showToast(`Gallery "${data.title}" uploaded with ${data.files.length} photo${data.files.length !== 1 ? 's' : ''}!`)
      }, 900)
    }
  }, [editItem])

  const handleEdit = (item) => {
    setEditItem(item)
    setDesktopEditMode(true)
    // mobile
    setFormOpen(true)
    // scroll to form on desktop
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const handleDelete = (id) => {
    const item = gallery.find(g => g.id === id)
    setDeleteItem(item)
  }

  const confirmDelete = () => {
    setGallery(prev => prev.filter(g => g.id !== deleteItem.id))
    showToast(`"${deleteItem.title}" deleted.`, 'error')
    setDeleteItem(null)
  }

  const handleReset = () => {
    setFilterSession('')
    setSearch('')
    setEditItem(null)
    setDesktopEditMode(false)
  }

  // ── Filter ────────────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    return gallery.filter(g => {
      const matchSession = !filterSession || g.session === filterSession
      const q = search.toLowerCase()
      const matchSearch = !q || g.title.toLowerCase().includes(q) || g.session.toLowerCase().includes(q) || g.date.toLowerCase().includes(q)
      return matchSession && matchSearch
    })
  }, [gallery, filterSession, search])

  const sessionCounts = useMemo(() => {
    const counts = {}
    gallery.forEach(g => { counts[g.session] = (counts[g.session] || 0) + 1 })
    return counts
  }, [gallery])

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ─────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Camera className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Photo Gallery
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Upload and manage school photo galleries by session.
          </p>
        </div>

        {/* Desktop: Add button when form is hidden */}
        {!desktopFormVisible && !desktopEditMode && (
          <button
            type="button"
            onClick={() => { setDesktopFormVisible(true); setEditItem(null) }}
            className="hidden sm:flex items-center gap-2 px-4 py-2 rounded-xl text-[13px] font-semibold text-white
              bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 transition-all active:scale-95"
          >
            <Plus className="w-4 h-4" /> Add New Gallery
          </button>
        )}
      </div>

      {/* ── DESKTOP: Upload / Edit Form ─────────────────────────────────── */}
      <div className="hidden sm:block">
        {(desktopFormVisible || desktopEditMode) && (
          <GalleryForm
            key={editItem?.id || 'new'}
            initialValues={editItem}
            onSubmit={handleUpload}
            onCancel={() => {
              setDesktopFormVisible(false)
              setDesktopEditMode(false)
              setEditItem(null)
            }}
            loading={loading}
          />
        )}
      </div>

      {/* ── MOBILE: Floating Add Button ─────────────────────────────────── */}
      <div className="sm:hidden">
        <button
          type="button"
          onClick={() => { setEditItem(null); setFormOpen(true) }}
          className="w-full flex items-center justify-center gap-2 py-3 rounded-2xl text-[14px] font-bold text-white
            bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 shadow-lg shadow-blue-500/30 transition-all active:scale-95"
        >
          <Plus className="w-5 h-5" /> Add New Gallery
        </button>
      </div>

      {/* ── Stats Strip ─────────────────────────────────────────────────── */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        {[
          { label: 'Total Galleries', value: gallery.length, color: 'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400', icon: FolderOpen },
          { label: 'Total Photos', value: gallery.reduce((s, g) => s + g.images.length, 0), color: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400', icon: Image },
          { label: 'Sessions Covered', value: Object.keys(sessionCounts).length, color: 'bg-violet-50 text-violet-600 dark:bg-violet-500/10 dark:text-violet-400', icon: Layers },
          { label: 'This Session', value: sessionCounts['2024-25'] || 0, color: 'bg-amber-50 text-amber-600 dark:bg-amber-500/10 dark:text-amber-400', icon: Camera },
        ].map(({ label, value, color, icon: Icon }) => (
          <div key={label} className="flex items-center gap-3 rounded-xl border border-slate-100 dark:border-[rgba(99,102,241,0.12)] bg-white dark:bg-[#1a1f35] px-4 py-3 shadow-sm">
            <span className={`w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 ${color}`}>
              <Icon className="w-4.5 h-4.5" style={{ width: 18, height: 18 }} />
            </span>
            <div>
              <p className="text-[20px] font-bold text-slate-800 dark:text-slate-100 tabular-nums leading-tight">{value}</p>
              <p className="text-[11px] text-slate-500 dark:text-slate-400 truncate">{label}</p>
            </div>
          </div>
        ))}
      </div>

      {/* ── Gallery List Card ────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Card Header */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0">
            <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
            <Camera className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">All Galleries</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
              {filtered.length} record{filtered.length !== 1 ? 's' : ''}
            </span>
          </div>

          {/* Controls */}
          <div className="flex items-center gap-2 flex-wrap">
            {/* Session filter */}
            <div className="w-36">
              <NativeSelect value={filterSession} onChange={e => setFilterSession(e.target.value)} placeholder="All Sessions">
                {SESSIONS.map(s => <option key={s} value={s}>{s} ({sessionCounts[s] || 0})</option>)}
              </NativeSelect>
            </div>

            {/* Search */}
            <div className="relative">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search galleries…"
                className="pl-8 pr-7 py-2 text-[12px] rounded-xl border outline-none transition-all w-44
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

            {/* View toggle — desktop only */}
            <div className="hidden sm:flex items-center gap-1 p-1 rounded-xl bg-slate-100 dark:bg-slate-800">
              <button
                type="button"
                onClick={() => setViewMode('grid')}
                className={`p-1.5 rounded-lg transition-all ${viewMode === 'grid' ? 'bg-white dark:bg-slate-700 shadow-sm text-blue-600' : 'text-slate-400 hover:text-slate-600'}`}
              >
                <Grid3X3 className="w-3.5 h-3.5" />
              </button>
              <button
                type="button"
                onClick={() => setViewMode('list')}
                className={`p-1.5 rounded-lg transition-all ${viewMode === 'list' ? 'bg-white dark:bg-slate-700 shadow-sm text-blue-600' : 'text-slate-400 hover:text-slate-600'}`}
              >
                <List className="w-3.5 h-3.5" />
              </button>
            </div>

            {/* Reset */}
            {(filterSession || search) && (
              <button
                type="button"
                onClick={handleReset}
                className="p-2 rounded-xl bg-slate-100 text-slate-500 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 transition-colors"
              >
                <RefreshCw className="w-3.5 h-3.5" />
              </button>
            )}
          </div>
        </div>

        {/* Session Tabs (mobile only) */}
        <div className="sm:hidden flex gap-2 px-4 py-3 overflow-x-auto scrollbar-none border-b border-slate-100 dark:border-[rgba(99,102,241,0.08)]">
          <button
            type="button"
            onClick={() => setFilterSession('')}
            className={`flex-shrink-0 px-3 py-1.5 rounded-full text-[12px] font-semibold transition-all
              ${!filterSession ? 'bg-blue-600 text-white shadow-sm' : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300'}`}
          >
            All ({gallery.length})
          </button>
          {SESSIONS.filter(s => sessionCounts[s]).map(s => {
            const { fg, bg } = sessionColor(s)
            return (
              <button
                key={s}
                type="button"
                onClick={() => setFilterSession(s === filterSession ? '' : s)}
                className={`flex-shrink-0 px-3 py-1.5 rounded-full text-[12px] font-semibold transition-all`}
                style={filterSession === s ? { background: fg, color: '#fff' } : { background: bg, color: fg }}
              >
                {s} ({sessionCounts[s]})
              </button>
            )
          })}
        </div>

        {/* Content */}
        {filtered.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-16 gap-3 text-slate-400 dark:text-slate-600">
            <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
              <Camera className="w-7 h-7 opacity-40" />
            </div>
            <div className="text-center">
              <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No galleries found</p>
              <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
                {search || filterSession ? 'Try clearing filters.' : 'Add your first gallery above.'}
              </p>
            </div>
            {(search || filterSession) && (
              <button
                type="button"
                onClick={handleReset}
                className="mt-1 px-4 py-2 rounded-xl text-[13px] font-semibold bg-blue-50 text-blue-600 hover:bg-blue-100 dark:bg-blue-500/10 dark:text-blue-400 transition-colors"
              >
                Clear Filters
              </button>
            )}
          </div>
        ) : (
          <>
            {/* Mobile: always grid cards */}
            <div className="sm:hidden p-4 grid grid-cols-1 gap-4">
              {filtered.map(item => (
                <GalleryCard key={item.id} item={item} onEdit={handleEdit} onDelete={handleDelete} />
              ))}
            </div>

            {/* Desktop: grid or list */}
            <div className="hidden sm:block">
              {viewMode === 'grid' ? (
                <div className="p-5 grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                  {filtered.map(item => (
                    <GalleryCard key={item.id} item={item} onEdit={handleEdit} onDelete={handleDelete} />
                  ))}
                </div>
              ) : (
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead>
                      <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                        {['S.No.', 'Gallery', 'Date', 'Session', 'Link', 'Actions'].map((h, i) => (
                          <th key={i} className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:text-center first:w-12">
                            {h}
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {filtered.map((item, i) => (
                        <TableRow key={item.id} item={item} idx={i + 1} onEdit={handleEdit} onDelete={handleDelete} />
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </>
        )}

        {/* Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{gallery.length}</span> galleries
          </p>
          {(search || filterSession) && (
            <button
              onClick={handleReset}
              className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1"
            >
              <X className="w-3 h-3" /> Clear filters
            </button>
          )}
        </div>
      </div>

      {/* ── Mobile Bottom Sheet Form ─────────────────────────────────────── */}
      <MobileFormSheet
        open={formOpen}
        onClose={() => { setFormOpen(false); setEditItem(null) }}
        initialValues={editItem}
        onSubmit={handleUpload}
        loading={loading}
      />

      {/* ── Delete Confirm Modal ─────────────────────────────────────────── */}
      <DeleteModal
        item={deleteItem}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteItem(null)}
      />

      {/* ── Toast ───────────────────────────────────────────────────────── */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
