/**
 * VideoGallery.jsx
 * Folder: src/pages/Admin/Gallery/VideoGallery.jsx
 *
 * Converts legacy ASPX "Video Gallery" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session dropdown + Title + Date + Video URL form
 *  - Upload button with validation
 *  - Video gallery grid (desktop) / card list (mobile)
 *  - Delete functionality
 *  - Session filter for gallery view
 *  - Embedded YouTube/video preview in modal
 *  - Mobile: bottom-sheet filter drawer
 *  - Toast notifications
 *  - Empty & loading states
 */

import { useState, useMemo, useCallback, useRef } from 'react'
import {
  Video, Upload, Trash2, Eye, X, Check, AlertCircle,
  Loader2, ChevronDown, Search, RefreshCw, Filter,
  SlidersHorizontal, Play, Calendar, Tag,
  Film, ExternalLink, Plus, Info, BookOpen,
  ChevronRight, LayoutGrid, List, Building2, MapPin
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ────────────────────────────────────────────────────
const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const SCHOOL_INFO = {
  name: 'Saraswati Vidya Mandir Senior Secondary School',
  address: 'Civil Lines, Dehradun, Uttarakhand – 248001',
}

/** Helper: extract YouTube video ID from various URL formats */
const getYouTubeId = (url = '') => {
  const match = url.match(
    /(?:youtu\.be\/|youtube\.com\/(?:watch\?v=|embed\/|v\/|shorts\/))([A-Za-z0-9_-]{11})/
  )
  return match ? match[1] : null
}

const getThumb = (url) => {
  const id = getYouTubeId(url)
  return id ? `https://img.youtube.com/vi/${id}/hqdefault.jpg` : null
}

const INITIAL_VIDEOS = [
  {
    id: 1, session: '2024-25', title: 'Annual Day Celebration 2024',
    submission_date: '15 Mar 2024',
    url: 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
    description: 'Annual day highlights'
  },
  {
    id: 2, session: '2024-25', title: 'Science Exhibition Walkthrough',
    submission_date: '22 Feb 2024',
    url: 'https://www.youtube.com/watch?v=9bZkp7q19f0',
    description: 'Science fair projects'
  },
  {
    id: 3, session: '2024-25', title: 'Sports Day 2024 Highlights',
    submission_date: '10 Jan 2024',
    url: 'https://www.youtube.com/watch?v=jNQXAC9IVRw',
    description: 'Sports day events'
  },
  {
    id: 4, session: '2023-24', title: 'Farewell Ceremony Class XII',
    submission_date: '28 Apr 2023',
    url: 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
    description: 'Farewell ceremony'
  },
  {
    id: 5, session: '2023-24', title: 'Republic Day Function 2024',
    submission_date: '26 Jan 2024',
    url: 'https://www.youtube.com/watch?v=9bZkp7q19f0',
    description: 'Republic day'
  },
  {
    id: 6, session: '2023-24', title: 'Teachers Day Special',
    submission_date: '05 Sep 2023',
    url: 'https://www.youtube.com/watch?v=jNQXAC9IVRw',
    description: 'Teachers day'
  },
  {
    id: 7, session: '2022-23', title: 'Independence Day 2022',
    submission_date: '15 Aug 2022',
    url: 'https://www.youtube.com/watch?v=dQw4w9WgXcQ',
    description: 'Independence day celebration'
  },
  {
    id: 8, session: '2025-26', title: 'New Session Orientation',
    submission_date: '02 Apr 2025',
    url: 'https://www.youtube.com/watch?v=9bZkp7q19f0',
    description: 'New session start'
  },
]

// ─── PRIMITIVE COMPONENTS ────────────────────────────────────────────────────

/** Native select with chevron */
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
function Field({ label, error, required, children, hint }) {
  return (
    <div className="flex flex-col gap-1.5">
      <label className="text-[11px] font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider">
        {label}{required && <span className="text-rose-500 ml-0.5">*</span>}
      </label>
      {children}
      {hint && !error && (
        <p className="text-[11px] text-slate-400 dark:text-slate-500">{hint}</p>
      )}
      {error && (
        <p className="flex items-center gap-1 text-[11px] text-rose-500">
          <AlertCircle className="w-3 h-3 flex-shrink-0" />{error}
        </p>
      )}
    </div>
  )
}

/** Toast notification */
function Toast({ message, type = 'success', onClose }) {
  return (
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3
        rounded-2xl shadow-2xl text-[13px] font-semibold min-w-[260px] max-w-[90vw]
        ${type === 'success'
          ? 'bg-emerald-600 text-white'
          : type === 'error'
          ? 'bg-rose-600 text-white'
          : 'bg-slate-800 text-white'}`}
      style={{ animation: 'slideUp .25s ease' }}
    >
      {type === 'success'
        ? <Check className="w-4 h-4 flex-shrink-0" />
        : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose} className="opacity-70 hover:opacity-100 transition-opacity">
        <X className="w-4 h-4" />
      </button>
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(12px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
    </div>
  )
}

/** Confirm delete modal */
function ConfirmModal({ open, title, onConfirm, onCancel, loading }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/50 z-50 backdrop-blur-sm" onClick={onCancel} />
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div
          className="bg-white dark:bg-[#1a1f35] rounded-2xl shadow-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] p-6 w-full max-w-sm"
          style={{ animation: 'popIn .2s ease' }}
        >
          <style>{`@keyframes popIn{from{opacity:0;transform:scale(.95)}to{opacity:1;transform:scale(1)}}`}</style>
          <div className="w-12 h-12 rounded-2xl bg-rose-100 dark:bg-rose-500/15 flex items-center justify-center mx-auto mb-4">
            <Trash2 className="w-5 h-5 text-rose-600 dark:text-rose-400" />
          </div>
          <h3 className="text-[15px] font-bold text-slate-800 dark:text-slate-100 text-center mb-1">Delete Video?</h3>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 text-center mb-6 leading-relaxed">
            "<span className="font-semibold text-slate-700 dark:text-slate-300">{title}</span>" will be permanently removed.
          </p>
          <div className="flex gap-3">
            <button onClick={onCancel} disabled={loading}
              className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700
                hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors disabled:opacity-60">
              Cancel
            </button>
            <button onClick={onConfirm} disabled={loading}
              className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
                bg-rose-600 text-white hover:bg-rose-700 transition-colors shadow-md shadow-rose-500/25 disabled:opacity-60">
              {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Trash2 className="w-4 h-4" />}
              Delete
            </button>
          </div>
        </div>
      </div>
    </>
  )
}

/** Video preview modal */
function VideoModal({ video, onClose }) {
  if (!video) return null
  const ytId = getYouTubeId(video.url)
  return (
    <>
      <div className="fixed inset-0 bg-black/80 z-50 backdrop-blur-sm" onClick={onClose} />
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div
          className="bg-[#0f1117] rounded-2xl shadow-2xl border border-white/10 w-full max-w-2xl overflow-hidden"
          style={{ animation: 'popIn .25s ease' }}
        >
          {/* Header */}
          <div className="flex items-center justify-between px-4 py-3 border-b border-white/10">
            <div className="flex items-center gap-2 min-w-0">
              <Film className="w-4 h-4 text-blue-400 flex-shrink-0" />
              <p className="text-[13px] font-semibold text-white truncate">{video.title}</p>
            </div>
            <div className="flex items-center gap-2 flex-shrink-0 ml-3">
              <a href={video.url} target="_blank" rel="noopener noreferrer"
                className="flex items-center gap-1.5 text-[11px] text-slate-400 hover:text-white transition-colors">
                <ExternalLink className="w-3.5 h-3.5" />
                <span className="hidden sm:inline">Open original</span>
              </a>
              <button onClick={onClose}
                className="w-7 h-7 rounded-lg bg-white/10 hover:bg-white/20 flex items-center justify-center text-white transition-colors">
                <X className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Video */}
          <div className="relative bg-black" style={{ paddingTop: '56.25%' }}>
            {ytId ? (
              <iframe
                className="absolute inset-0 w-full h-full"
                src={`https://www.youtube.com/embed/${ytId}?autoplay=1&rel=0`}
                title={video.title}
                frameBorder="0"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowFullScreen
              />
            ) : (
              <div className="absolute inset-0 flex flex-col items-center justify-center gap-3 text-slate-500">
                <Film className="w-10 h-10 opacity-30" />
                <p className="text-[13px]">Preview not available</p>
                <a href={video.url} target="_blank" rel="noopener noreferrer"
                  className="text-[12px] text-blue-400 hover:underline flex items-center gap-1">
                  <ExternalLink className="w-3.5 h-3.5" /> Open video link
                </a>
              </div>
            )}
          </div>

          {/* Meta */}
          <div className="flex items-center gap-4 px-4 py-3 border-t border-white/10">
            <span className="flex items-center gap-1.5 text-[11px] text-slate-400">
              <Calendar className="w-3.5 h-3.5" /> {video.submission_date}
            </span>
            <span className="flex items-center gap-1.5 text-[11px] text-slate-400">
              <Tag className="w-3.5 h-3.5" /> Session: {video.session}
            </span>
          </div>
        </div>
      </div>
    </>
  )
}

/** Single video card for grid view */
function VideoCard({ video, onDelete, onPreview }) {
  const thumb = getThumb(video.url)
  const ytId = getYouTubeId(video.url)

  return (
    <div className="group rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200">
      {/* Thumbnail */}
      <div className="relative bg-slate-900 overflow-hidden" style={{ paddingTop: '56.25%' }}>
        {thumb ? (
          <img
            src={thumb}
            alt={video.title}
            className="absolute inset-0 w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            onError={e => { e.target.style.display = 'none' }}
          />
        ) : (
          <div className="absolute inset-0 flex items-center justify-center bg-gradient-to-br from-slate-800 to-slate-900">
            <Film className="w-8 h-8 text-slate-600" />
          </div>
        )}

        {/* Dark overlay on hover */}
        <div className="absolute inset-0 bg-black/0 group-hover:bg-black/40 transition-all duration-200" />

        {/* Play button */}
        <button
          onClick={() => onPreview(video)}
          className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200"
          aria-label={`Play ${video.title}`}
        >
          <span className="w-12 h-12 rounded-full bg-white/90 flex items-center justify-center shadow-xl hover:scale-110 transition-transform">
            <Play className="w-5 h-5 text-slate-800 ml-0.5" />
          </span>
        </button>

        {/* Session badge */}
        <span className="absolute top-2 left-2 px-2 py-0.5 rounded-lg text-[10px] font-bold bg-blue-600/90 text-white backdrop-blur-sm">
          {video.session}
        </span>

        {/* YouTube badge */}
        {ytId && (
          <span className="absolute top-2 right-2 px-2 py-0.5 rounded-lg text-[10px] font-bold bg-red-600/90 text-white backdrop-blur-sm">
            YouTube
          </span>
        )}
      </div>

      {/* Info */}
      <div className="p-3.5">
        <h3 className="text-[13px] font-bold text-slate-800 dark:text-slate-100 line-clamp-2 leading-snug mb-2">
          {video.title}
        </h3>
        <div className="flex items-center justify-between gap-2">
          <span className="flex items-center gap-1.5 text-[11px] text-slate-400 dark:text-slate-500">
            <Calendar className="w-3 h-3 flex-shrink-0" />
            {video.submission_date}
          </span>
          <div className="flex items-center gap-1.5 flex-shrink-0">
            <button
              onClick={() => onPreview(video)}
              className="flex items-center gap-1 px-2.5 py-1.5 rounded-lg text-[11px] font-semibold
                bg-blue-50 text-blue-700 hover:bg-blue-100 dark:bg-blue-500/10 dark:text-blue-400 dark:hover:bg-blue-500/20 transition-colors"
              aria-label="Preview"
            >
              <Play className="w-3 h-3" />
              <span className="hidden sm:inline">Preview</span>
            </button>
            <button
              onClick={() => onDelete(video)}
              className="w-7 h-7 rounded-lg flex items-center justify-center
                bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20 transition-colors"
              aria-label="Delete"
            >
              <Trash2 className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

/** Single video row for list/table view */
function VideoRow({ video, idx, onDelete, onPreview }) {
  const thumb = getThumb(video.url)
  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors">
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-10">{idx}</td>
      <td className="px-4 py-3">
        <div className="flex items-center gap-3">
          {/* Mini thumbnail */}
          <div className="w-16 h-10 rounded-lg overflow-hidden bg-slate-900 flex-shrink-0 relative">
            {thumb ? (
              <img src={thumb} alt="" className="w-full h-full object-cover" onError={e => e.target.style.display = 'none'} />
            ) : (
              <div className="w-full h-full flex items-center justify-center">
                <Film className="w-4 h-4 text-slate-600" />
              </div>
            )}
          </div>
          <div className="min-w-0">
            <p className="text-[13px] font-semibold text-slate-800 dark:text-slate-100 truncate max-w-[200px]">{video.title}</p>
            <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5 truncate max-w-[200px]">{video.url}</p>
          </div>
        </div>
      </td>
      <td className="px-4 py-3 text-center">
        <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-[11px] font-semibold bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400">
          <Tag className="w-3 h-3" />{video.session}
        </span>
      </td>
      <td className="px-4 py-3 text-center">
        <span className="flex items-center justify-center gap-1.5 text-[12px] text-slate-500 dark:text-slate-400">
          <Calendar className="w-3.5 h-3.5" />{video.submission_date}
        </span>
      </td>
      <td className="px-4 py-3 text-center">
        <div className="flex items-center justify-center gap-2">
          <button onClick={() => onPreview(video)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[11px] font-semibold
              bg-blue-50 text-blue-700 hover:bg-blue-100 dark:bg-blue-500/10 dark:text-blue-400 dark:hover:bg-blue-500/20 transition-colors">
            <Eye className="w-3.5 h-3.5" /> Preview
          </button>
          <button onClick={() => onDelete(video)}
            className="w-7 h-7 rounded-lg flex items-center justify-center
              bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20 transition-colors"
            aria-label="Delete">
            <Trash2 className="w-3.5 h-3.5" />
          </button>
        </div>
      </td>
    </tr>
  )
}

/** Mobile video list card (compact) */
function MobileVideoCard({ video, onDelete, onPreview }) {
  const thumb = getThumb(video.url)
  return (
    <div className="flex items-center gap-3 p-3.5 rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] shadow-sm">
      {/* Thumb */}
      <button
        onClick={() => onPreview(video)}
        className="w-20 h-14 rounded-xl overflow-hidden bg-slate-900 flex-shrink-0 relative group"
        aria-label={`Play ${video.title}`}
      >
        {thumb ? (
          <img src={thumb} alt="" className="w-full h-full object-cover" onError={e => e.target.style.display = 'none'} />
        ) : (
          <div className="w-full h-full flex items-center justify-center">
            <Film className="w-5 h-5 text-slate-600" />
          </div>
        )}
        <div className="absolute inset-0 bg-black/30 flex items-center justify-center">
          <span className="w-7 h-7 rounded-full bg-white/90 flex items-center justify-center">
            <Play className="w-3 h-3 text-slate-800 ml-0.5" />
          </span>
        </div>
      </button>

      {/* Info */}
      <div className="flex-1 min-w-0">
        <p className="text-[13px] font-bold text-slate-800 dark:text-slate-100 line-clamp-2 leading-snug">{video.title}</p>
        <div className="flex items-center gap-2 mt-1 flex-wrap">
          <span className="px-1.5 py-0.5 rounded-md text-[10px] font-bold bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
            {video.session}
          </span>
          <span className="text-[11px] text-slate-400 dark:text-slate-500">{video.submission_date}</span>
        </div>
      </div>

      {/* Delete */}
      <button
        onClick={() => onDelete(video)}
        className="w-8 h-8 rounded-xl flex items-center justify-center flex-shrink-0
          bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 transition-colors"
        aria-label="Delete"
      >
        <Trash2 className="w-4 h-4" />
      </button>
    </div>
  )
}

/** Upload form section */
function UploadForm({ onUpload }) {
  const today = new Date().toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
  const [form, setForm] = useState({
    session: '', title: '', url: '', date: today
  })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)

  const set = (key, val) => {
    setForm(p => ({ ...p, [key]: val }))
    setErrors(p => ({ ...p, [key]: undefined }))
  }

  const validate = () => {
    const e = {}
    if (!form.session) e.session = 'Select a session'
    if (!form.title.trim()) e.title = 'Title is required'
    if (!form.url.trim()) e.url = 'Video URL is required'
    else if (!form.url.includes('youtube') && !form.url.startsWith('http'))
      e.url = 'Enter a valid URL (YouTube or other)'
    if (!form.date.trim()) e.date = 'Date is required'
    return e
  }

  const handleSubmit = () => {
    const e = validate()
    if (Object.keys(e).length) { setErrors(e); return }
    setLoading(true)
    setTimeout(() => {
      onUpload({ ...form, id: Date.now(), submission_date: form.date })
      setForm({ session: '', title: '', url: '', date: today })
      setErrors({})
      setLoading(false)
    }, 700)
  }

  return (
    <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
      {/* Header */}
      <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
        <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
        <Plus className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
        <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Add New Video</span>
        <div className="flex items-center gap-1.5 text-[11px] text-slate-400 dark:text-slate-500">
          <Info className="w-3.5 h-3.5" />
          <span className="hidden sm:inline">Paste YouTube or direct video URL</span>
        </div>
      </div>

      {/* Fields */}
      <div className="p-5">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 items-end">
          <Field label="Session" error={errors.session} required>
            <NativeSelect
              value={form.session}
              onChange={e => set('session', e.target.value)}
              placeholder="-- Select Session --"
              error={errors.session}
            >
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>

          <Field label="Title" error={errors.title} required>
            <input
              value={form.title}
              onChange={e => set('title', e.target.value)}
              placeholder="e.g. Annual Day 2025"
              className={`w-full px-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all
                bg-white text-slate-800 placeholder-slate-300
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600
                dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                ${errors.title ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
            />
          </Field>

          <Field label="Date" error={errors.date} required>
            <input
              type="date"
              value={form.date}
              onChange={e => set('date', e.target.value)}
              className={`w-full px-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all
                bg-white text-slate-800
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200
                dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                ${errors.date ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
            />
          </Field>

          <Field label="Video URL" error={errors.url} required hint="YouTube, Vimeo or direct link">
            <input
              value={form.url}
              onChange={e => set('url', e.target.value)}
              placeholder="https://youtube.com/watch?v=..."
              className={`w-full px-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all
                bg-white text-slate-800 placeholder-slate-300
                focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600
                dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                ${errors.url ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
            />
          </Field>
        </div>

        {/* Submit row */}
        <div className="flex items-center justify-between mt-5 pt-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500 hidden sm:block flex items-center gap-1.5">
            <span className="text-rose-500 font-bold">*</span> Required fields
          </p>
          <button
            type="button"
            onClick={handleSubmit}
            disabled={loading}
            className="flex items-center gap-2 px-6 py-2.5 rounded-xl text-[13px] font-semibold text-white
              bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700
              shadow-md shadow-blue-500/20 transition-all active:scale-95 disabled:opacity-70 w-full sm:w-auto justify-center"
          >
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Upload className="w-4 h-4" />}
            {loading ? 'Uploading…' : 'Upload Video'}
          </button>
        </div>
      </div>
    </div>
  )
}

/** Mobile filter drawer */
function FilterDrawer({ open, onClose, filterSession, setFilterSession, search, setSearch, viewMode, setViewMode }) {
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
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Filter & Search</span>
          </div>
          <button onClick={onClose}
            className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400 transition-colors">
            <X className="w-4 h-4" />
          </button>
        </div>
        <div className="px-5 py-5 space-y-4">
          <Field label="Filter by Session">
            <NativeSelect value={filterSession} onChange={e => setFilterSession(e.target.value)} placeholder="All Sessions">
              {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
            </NativeSelect>
          </Field>
          <Field label="Search">
            <div className="relative">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search title…"
                className="w-full pl-8 pr-8 py-2.5 text-[13px] rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.25)] outline-none
                  bg-white text-slate-700 placeholder-slate-300 focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                  dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400"
              />
              {search && (
                <button onClick={() => setSearch('')} className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400">
                  <X className="w-3.5 h-3.5" />
                </button>
              )}
            </div>
          </Field>
          <div>
            <p className="text-[11px] font-bold text-slate-500 dark:text-slate-400 uppercase tracking-wider mb-2">View Mode</p>
            <div className="grid grid-cols-2 gap-2">
              {[['grid', LayoutGrid, 'Grid View'], ['list', List, 'List View']].map(([mode, Icon, label]) => (
                <button key={mode} onClick={() => setViewMode(mode)}
                  className={`flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold transition-colors
                    ${viewMode === mode
                      ? 'bg-blue-600 text-white dark:bg-indigo-600'
                      : 'bg-slate-100 text-slate-600 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300'}`}>
                  <Icon className="w-4 h-4" />{label}
                </button>
              ))}
            </div>
          </div>
        </div>
        <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex gap-3">
          <button onClick={() => { setFilterSession(''); setSearch(''); onClose() }}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700
              hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
            Reset
          </button>
          <button onClick={onClose}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-blue-600 text-white
              hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 transition-colors">
            Apply
          </button>
        </div>
      </div>
    </>
  )
}

// ─── MAIN COMPONENT ──────────────────────────────────────────────────────────
export default function VideoGallery() {
  const [videos, setVideos] = useState(INITIAL_VIDEOS)
  const [filterSession, setFilterSession] = useState('')
  const [search, setSearch] = useState('')
  const [viewMode, setViewMode] = useState('grid') // 'grid' | 'list'
  const [previewVideo, setPreviewVideo] = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleteLoading, setDeleteLoading] = useState(false)
  const [filterOpen, setFilterOpen] = useState(false)
  const [toast, setToast] = useState(null)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Upload handler ─────────────────────────────────────────────────────
  const handleUpload = useCallback((newVideo) => {
    setVideos(prev => [newVideo, ...prev])
    showToast(`"${newVideo.title}" added successfully!`)
  }, [])

  // ── Delete flow ────────────────────────────────────────────────────────
  const handleDeleteRequest = (video) => setDeleteTarget(video)
  const handleDeleteConfirm = () => {
    setDeleteLoading(true)
    setTimeout(() => {
      setVideos(prev => prev.filter(v => v.id !== deleteTarget.id))
      showToast(`"${deleteTarget.title}" deleted.`, 'error')
      setDeleteTarget(null)
      setDeleteLoading(false)
    }, 600)
  }

  // ── Filtered list ──────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    return videos.filter(v => {
      const matchSession = !filterSession || v.session === filterSession
      const matchSearch = !search || v.title.toLowerCase().includes(search.toLowerCase())
      return matchSession && matchSearch
    })
  }, [videos, filterSession, search])

  const activeFilters = (filterSession ? 1 : 0) + (search ? 1 : 0)

  // ── Stats ──────────────────────────────────────────────────────────────
  const stats = useMemo(() => ({
    total: videos.length,
    sessions: [...new Set(videos.map(v => v.session))].length,
    filtered: filtered.length,
  }), [videos, filtered])

  return (
    <div className="space-y-5 pb-10">

      {/* ── Page Header ─────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Video className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Video Gallery
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Manage and preview school event videos by session.
          </p>
        </div>

        {/* Quick stats — desktop */}
        <div className="hidden sm:flex items-center gap-3">
          <div className="flex items-center gap-2 px-3 py-2 rounded-xl bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20">
            <Film className="w-4 h-4 text-blue-600 dark:text-blue-400" />
            <span className="text-[13px] font-bold text-blue-700 dark:text-blue-300">{stats.total} Videos</span>
          </div>
          <div className="flex items-center gap-2 px-3 py-2 rounded-xl bg-violet-50 dark:bg-violet-500/10 border border-violet-100 dark:border-violet-500/20">
            <Tag className="w-4 h-4 text-violet-600 dark:text-violet-400" />
            <span className="text-[13px] font-bold text-violet-700 dark:text-violet-300">{stats.sessions} Sessions</span>
          </div>
        </div>
      </div>

      {/* ── School Banner (compact) ─────────────────────────────────────── */}
      <div className="rounded-xl border border-blue-100 dark:border-[rgba(99,102,241,0.2)] bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-[#1a1f35] dark:to-[#1e2238] px-4 py-3 flex items-center gap-3">
        <Building2 className="w-5 h-5 text-blue-600 dark:text-blue-400 flex-shrink-0" />
        <div className="min-w-0 flex-1">
          <p className="text-[13px] font-bold text-slate-800 dark:text-slate-100 truncate">{SCHOOL_INFO.name}</p>
          <p className="text-[11px] text-slate-500 dark:text-slate-400 flex items-center gap-1 truncate">
            <MapPin className="w-3 h-3 flex-shrink-0" />{SCHOOL_INFO.address}
          </p>
        </div>
        <span className="hidden sm:inline-flex items-center px-3 py-1 rounded-full bg-amber-100 dark:bg-amber-500/15 border border-amber-200 dark:border-amber-500/25 text-[11px] font-bold text-amber-700 dark:text-amber-400 flex-shrink-0">
          Video Gallery
        </span>
      </div>

      {/* ── Upload Form ─────────────────────────────────────────────────── */}
      <UploadForm onUpload={handleUpload} />

      {/* ── Gallery Section ─────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Gallery Header */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0">
            <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
            <BookOpen className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Uploaded Videos</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">
              {filtered.length} record{filtered.length !== 1 ? 's' : ''}
            </span>
          </div>

          {/* Desktop controls */}
          <div className="hidden sm:flex items-center gap-2 flex-shrink-0">
            {/* Session filter */}
            <div className="w-36">
              <NativeSelect value={filterSession} onChange={e => setFilterSession(e.target.value)} placeholder="All Sessions">
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </div>

            {/* Search */}
            <div className="relative w-48">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search title…"
                className="w-full pl-8 pr-7 py-1.5 text-[12px] rounded-lg border border-slate-200 dark:border-[rgba(99,102,241,0.25)] outline-none
                  bg-white text-slate-700 placeholder-slate-300
                  focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                  dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400"
              />
              {search && (
                <button onClick={() => setSearch('')} className="absolute right-2 top-1/2 -translate-y-1/2 text-slate-400">
                  <X className="w-3.5 h-3.5" />
                </button>
              )}
            </div>

            {/* Reset */}
            {(filterSession || search) && (
              <button onClick={() => { setFilterSession(''); setSearch('') }}
                className="px-2.5 py-1.5 rounded-lg text-[12px] font-semibold bg-slate-100 text-slate-600 hover:bg-slate-200
                  dark:bg-slate-800 dark:text-slate-400 dark:hover:bg-slate-700 transition-colors flex items-center gap-1.5">
                <RefreshCw className="w-3.5 h-3.5" /> Reset
              </button>
            )}

            {/* View toggle */}
            <div className="flex items-center rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] overflow-hidden">
              {[['grid', LayoutGrid], ['list', List]].map(([mode, Icon]) => (
                <button key={mode} onClick={() => setViewMode(mode)}
                  className={`w-8 h-8 flex items-center justify-center transition-colors
                    ${viewMode === mode
                      ? 'bg-blue-600 text-white dark:bg-indigo-600'
                      : 'bg-white text-slate-500 hover:bg-slate-50 dark:bg-transparent dark:text-slate-400 dark:hover:bg-white/5'}`}>
                  <Icon className="w-3.5 h-3.5" />
                </button>
              ))}
            </div>
          </div>

          {/* Mobile: filter button */}
          <div className="flex sm:hidden items-center gap-2">
            <button onClick={() => setFilterOpen(true)}
              className="flex-1 flex items-center justify-center gap-2 py-2 rounded-xl text-[13px] font-semibold
                bg-blue-600 text-white dark:bg-indigo-600 shadow-md shadow-blue-500/20">
              <Filter className="w-4 h-4" />
              Filter{activeFilters > 0 && (
                <span className="bg-white/25 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">{activeFilters}</span>
              )}
            </button>
          </div>
        </div>

        {/* ── Desktop: Table View (list mode) ─────────────────────────── */}
        {viewMode === 'list' && (
          <div className="hidden md:block overflow-x-auto">
            {filtered.length === 0 ? (
              <EmptyState search={search} filterSession={filterSession} onReset={() => { setSearch(''); setFilterSession('') }} />
            ) : (
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                    {['#', 'Video', 'Session', 'Date', 'Actions'].map((h, i) => (
                      <th key={i}
                        className={`px-4 py-2.5 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap
                          ${i === 0 ? 'text-center w-10' : i === 4 ? 'text-center' : 'text-left'}`}>
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((video, i) => (
                    <VideoRow key={video.id} video={video} idx={i + 1}
                      onDelete={handleDeleteRequest} onPreview={setPreviewVideo} />
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

        {/* ── Desktop: Grid View ───────────────────────────────────────── */}
        {viewMode === 'grid' && (
          <div className="hidden md:block p-5">
            {filtered.length === 0 ? (
              <EmptyState search={search} filterSession={filterSession} onReset={() => { setSearch(''); setFilterSession('') }} />
            ) : (
              <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                {filtered.map(video => (
                  <VideoCard key={video.id} video={video}
                    onDelete={handleDeleteRequest} onPreview={setPreviewVideo} />
                ))}
              </div>
            )}
          </div>
        )}

        {/* ── Mobile: Always list-style cards ─────────────────────────── */}
        <div className="md:hidden p-4 space-y-3">
          {filtered.length === 0 ? (
            <EmptyState search={search} filterSession={filterSession} onReset={() => { setSearch(''); setFilterSession('') }} />
          ) : (
            <>
              <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5">
                <Info className="w-3.5 h-3.5 flex-shrink-0" />
                Tap the thumbnail to preview the video.
              </p>
              {filtered.map(video => (
                <MobileVideoCard key={video.id} video={video}
                  onDelete={handleDeleteRequest} onPreview={setPreviewVideo} />
              ))}
            </>
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            Showing{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span>{' '}
            of{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{videos.length}</span>{' '}
            videos
          </p>
          {(search || filterSession) && (
            <button onClick={() => { setSearch(''); setFilterSession('') }}
              className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
              <X className="w-3 h-3" /> Clear filters
            </button>
          )}
        </div>
      </div>

      {/* Modals & Overlays */}
      <FilterDrawer
        open={filterOpen}
        onClose={() => setFilterOpen(false)}
        filterSession={filterSession}
        setFilterSession={setFilterSession}
        search={search}
        setSearch={setSearch}
        viewMode={viewMode}
        setViewMode={setViewMode}
      />

      <VideoModal video={previewVideo} onClose={() => setPreviewVideo(null)} />

      <ConfirmModal
        open={!!deleteTarget}
        title={deleteTarget?.title}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteTarget(null)}
        loading={deleteLoading}
      />

      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}

// ─── EMPTY STATE ─────────────────────────────────────────────────────────────
function EmptyState({ search, filterSession, onReset }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 gap-4 text-slate-400 dark:text-slate-600">
      <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
        <Film className="w-7 h-7 opacity-50" />
      </div>
      <div className="text-center">
        {search || filterSession ? (
          <>
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No videos match your filters</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Try changing the session or search term.
            </p>
            <button onClick={onReset}
              className="mt-3 flex items-center gap-1.5 mx-auto px-3 py-1.5 rounded-lg text-[12px] font-semibold
                bg-blue-50 text-blue-700 hover:bg-blue-100 dark:bg-blue-500/10 dark:text-blue-400 transition-colors">
              <RefreshCw className="w-3.5 h-3.5" /> Reset filters
            </button>
          </>
        ) : (
          <>
            <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">No videos uploaded yet</p>
            <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
              Use the form above to add your first video.
            </p>
          </>
        )}
      </div>
    </div>
  )
}
