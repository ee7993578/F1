/**
 * DefineHolidays.jsx
 * Folder: src/pages/Configuration/DefineHolidays.jsx
 *
 * Converts legacy ASPX "define_holidays.aspx" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Session dropdown
 *  - Holiday name input
 *  - From date / To date pickers (native date input)
 *  - Apply For Class checkboxes (select-all toggle + individual)
 *  - Save holiday
 *  - List of saved holidays in desktop table / mobile cards
 *  - Delete holiday
 *  - Dark/light theme via Tailwind dark: variants
 *  - No horizontal scroll on mobile
 */

import { useState, useMemo, useCallback } from 'react'
import {
  CalendarDays, Trash2, Plus, RefreshCw,
  AlertCircle, X, Check, Loader2, ChevronDown,
  BookOpen, CalendarRange, Tag, School2,
  CheckSquare, Square, ChevronRight, Info,
  SlidersHorizontal, CalendarCheck2, Building2,
  Layers, Search
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ─────────────────────────────────────────────────────

const SESSIONS = ['2022-23', '2023-24', '2024-25', '2025-26']

const CLASS_LIST = [
  'Nursery', 'LKG', 'UKG',
  'Class I', 'Class II', 'Class III', 'Class IV', 'Class V',
  'Class VI', 'Class VII', 'Class VIII',
  'Class IX', 'Class X', 'Class XI', 'Class XII',
]

// Pre-seeded dummy holidays
const INITIAL_HOLIDAYS = [
  {
    id: 1,
    session: '2025-26',
    holiday_name: 'Republic Day',
    classes: ['All Classes'],
    from_date: '2026-01-26',
    to_date: '2026-01-26',
  },
  {
    id: 2,
    session: '2025-26',
    holiday_name: 'Holi',
    classes: ['Nursery', 'LKG', 'UKG', 'Class I', 'Class II', 'Class III'],
    from_date: '2026-03-25',
    to_date: '2026-03-26',
  },
  {
    id: 3,
    session: '2025-26',
    holiday_name: 'Summer Vacation',
    classes: ['All Classes'],
    from_date: '2026-05-15',
    to_date: '2026-06-30',
  },
  {
    id: 4,
    session: '2024-25',
    holiday_name: 'Diwali Break',
    classes: ['All Classes'],
    from_date: '2024-11-01',
    to_date: '2024-11-05',
  },
]

// ─── HELPERS ─────────────────────────────────────────────────────────────────

const formatDate = (dateStr) => {
  if (!dateStr) return '—'
  const d = new Date(dateStr)
  if (isNaN(d)) return dateStr
  return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
}

const classLabel = (classes = []) => {
  if (!classes.length) return '—'
  if (classes.includes('All Classes')) return 'All Classes'
  if (classes.length <= 3) return classes.join(', ')
  return `${classes.slice(0, 2).join(', ')} +${classes.length - 2} more`
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
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-[9999] flex items-center gap-3 px-5 py-3
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

// ─── CONFIRM MODAL ────────────────────────────────────────────────────────────

function ConfirmModal({ open, onConfirm, onCancel, holidayName }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50" onClick={onCancel} />
      <div
        className="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-50
          w-[90vw] max-w-sm rounded-2xl bg-white dark:bg-[#1a1f35]
          border border-slate-200 dark:border-[rgba(99,102,241,0.2)]
          shadow-2xl p-6"
        style={{ animation: 'popIn .2s ease' }}
      >
        <style>{`@keyframes popIn{from{opacity:0;transform:translate(-50%,-50%) scale(.93)}to{opacity:1;transform:translate(-50%,-50%) scale(1)}}`}</style>
        <div className="w-12 h-12 rounded-2xl bg-rose-100 dark:bg-rose-500/15 flex items-center justify-center mx-auto mb-4">
          <Trash2 className="w-6 h-6 text-rose-600 dark:text-rose-400" />
        </div>
        <h3 className="text-[16px] font-bold text-slate-800 dark:text-slate-100 text-center mb-1">Delete Holiday?</h3>
        <p className="text-[13px] text-slate-500 dark:text-slate-400 text-center mb-6">
          "<span className="font-semibold text-slate-700 dark:text-slate-200">{holidayName}</span>" will be permanently removed.
        </p>
        <div className="flex gap-3">
          <button
            onClick={onCancel}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-rose-600 text-white hover:bg-rose-700 transition-colors"
          >
            Delete
          </button>
        </div>
      </div>
    </>
  )
}

// ─── CLASS SELECTOR COMPONENT ─────────────────────────────────────────────────

function ClassSelector({ selected, onChange, error }) {
  const allSelected = selected.includes('All Classes')

  const toggleAll = () => {
    if (allSelected) {
      onChange([])
    } else {
      onChange(['All Classes'])
    }
  }

  const toggleClass = (cls) => {
    // If "All Classes" was selected, switch to individual
    if (allSelected) {
      onChange(CLASS_LIST.filter(c => c !== cls))
      return
    }
    if (selected.includes(cls)) {
      const next = selected.filter(c => c !== cls)
      onChange(next)
    } else {
      const next = [...selected, cls]
      if (next.length === CLASS_LIST.length) {
        onChange(['All Classes'])
      } else {
        onChange(next)
      }
    }
  }

  // Effective individual selections for checkbox display
  const effectiveSelected = allSelected ? CLASS_LIST : selected

  return (
    <div className={`rounded-xl border p-3 ${error ? 'border-rose-400' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}>
      {/* Select All toggle */}
      <button
        type="button"
        onClick={toggleAll}
        className="flex items-center gap-2 mb-3 w-full"
      >
        {allSelected
          ? <CheckSquare className="w-4 h-4 text-blue-600 dark:text-indigo-400 flex-shrink-0" />
          : <Square className="w-4 h-4 text-slate-400 flex-shrink-0" />
        }
        <span className="text-[13px] font-bold text-slate-700 dark:text-slate-200">Select All Classes</span>
      </button>

      {/* Individual class checkboxes */}
      <div className="grid grid-cols-2 sm:grid-cols-3 gap-1.5">
        {CLASS_LIST.map(cls => {
          const checked = effectiveSelected.includes(cls)
          return (
            <button
              key={cls}
              type="button"
              onClick={() => toggleClass(cls)}
              className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-[12px] font-semibold transition-all text-left
                ${checked
                  ? 'bg-blue-50 text-blue-700 border border-blue-200 dark:bg-indigo-500/15 dark:text-indigo-300 dark:border-indigo-500/30'
                  : 'bg-slate-50 text-slate-600 border border-slate-200 hover:border-blue-200 hover:bg-blue-50/40 dark:bg-slate-800/60 dark:text-slate-400 dark:border-[rgba(99,102,241,0.15)]'
                }`}
            >
              {checked
                ? <Check className="w-3 h-3 flex-shrink-0 text-blue-600 dark:text-indigo-400" />
                : <div className="w-3 h-3 rounded border border-slate-300 dark:border-slate-600 flex-shrink-0" />
              }
              <span className="truncate">{cls}</span>
            </button>
          )
        })}
      </div>

      {/* Selected count */}
      {(selected.length > 0 || allSelected) && (
        <p className="mt-2.5 text-[11px] font-semibold text-blue-600 dark:text-indigo-400 flex items-center gap-1">
          <Check className="w-3 h-3" />
          {allSelected ? 'All classes selected' : `${selected.length} class${selected.length !== 1 ? 'es' : ''} selected`}
        </p>
      )}
    </div>
  )
}

// ─── MOBILE HOLIDAY CARD ─────────────────────────────────────────────────────

function MobileHolidayCard({ holiday, idx, onDelete }) {
  const [expanded, setExpanded] = useState(false)
  const days = useMemo(() => {
    const from = new Date(holiday.from_date)
    const to = new Date(holiday.to_date)
    const diff = Math.ceil((to - from) / (1000 * 60 * 60 * 24)) + 1
    return diff
  }, [holiday.from_date, holiday.to_date])

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        {/* Icon */}
        <div className="w-9 h-9 rounded-xl bg-blue-50 dark:bg-indigo-500/15 flex items-center justify-center flex-shrink-0">
          <CalendarDays className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
        </div>

        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 truncate">{holiday.holiday_name}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5">
            {formatDate(holiday.from_date)}
            {holiday.from_date !== holiday.to_date && <> — {formatDate(holiday.to_date)}</>}
            <span className="ml-1.5 text-blue-500 dark:text-indigo-400 font-semibold">({days}d)</span>
          </p>
        </div>

        {/* Session badge */}
        <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400 flex-shrink-0">
          {holiday.session}
        </span>

        <span className={`transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4 text-slate-400" />
        </span>
      </button>

      {/* Expanded */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 py-4 space-y-3">
          {/* Classes */}
          <div>
            <p className="text-[11px] font-semibold uppercase tracking-wide text-slate-400 dark:text-slate-500 mb-1.5">Apply For Class</p>
            <div className="flex flex-wrap gap-1.5">
              {holiday.classes.map(cls => (
                <span key={cls} className="px-2 py-0.5 rounded-lg text-[11px] font-semibold bg-blue-50 text-blue-700 dark:bg-indigo-500/15 dark:text-indigo-300 border border-blue-100 dark:border-indigo-500/20">
                  {cls}
                </span>
              ))}
            </div>
          </div>

          {/* Date row */}
          <div className="grid grid-cols-2 gap-2">
            <div className="rounded-lg bg-slate-50 dark:bg-slate-800/60 p-2.5">
              <p className="text-[10px] font-semibold uppercase text-slate-400 dark:text-slate-500 mb-0.5">From</p>
              <p className="text-[13px] font-bold text-slate-700 dark:text-slate-200">{formatDate(holiday.from_date)}</p>
            </div>
            <div className="rounded-lg bg-slate-50 dark:bg-slate-800/60 p-2.5">
              <p className="text-[10px] font-semibold uppercase text-slate-400 dark:text-slate-500 mb-0.5">To</p>
              <p className="text-[13px] font-bold text-slate-700 dark:text-slate-200">{formatDate(holiday.to_date)}</p>
            </div>
          </div>

          {/* Delete */}
          <button
            type="button"
            onClick={() => onDelete(holiday)}
            className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
              bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20 border border-rose-100 dark:border-rose-500/20 transition-colors"
          >
            <Trash2 className="w-4 h-4" />
            Delete Holiday
          </button>
        </div>
      )}
    </div>
  )
}

// ─── MAIN COMPONENT ──────────────────────────────────────────────────────────

export default function DefineHolidays() {
  // ── Form state ─────────────────────────────────────────────────────────────
  const [session,      setSession]      = useState('')
  const [holidayName,  setHolidayName]  = useState('')
  const [fromDate,     setFromDate]     = useState('')
  const [toDate,       setToDate]       = useState('')
  const [selectedCls,  setSelectedCls]  = useState([])
  const [errors,       setErrors]       = useState({})
  const [saving,       setSaving]       = useState(false)

  // ── List state ─────────────────────────────────────────────────────────────
  const [holidays,     setHolidays]     = useState(INITIAL_HOLIDAYS)
  const [search,       setSearch]       = useState('')
  const [filterSes,    setFilterSes]    = useState('')

  // ── UI state ───────────────────────────────────────────────────────────────
  const [toast,        setToast]        = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [nextId,       setNextId]       = useState(INITIAL_HOLIDAYS.length + 1)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Validate ───────────────────────────────────────────────────────────────
  const validate = () => {
    const err = {}
    if (!session)                             err.session     = 'Select a session'
    if (!holidayName.trim())                  err.holidayName = 'Enter holiday name'
    if (!fromDate)                            err.fromDate    = 'Select from date'
    if (!toDate)                              err.toDate      = 'Select to date'
    if (fromDate && toDate && toDate < fromDate) err.toDate  = 'To date must be ≥ From date'
    if (selectedCls.length === 0)             err.classes     = 'Select at least one class'
    return err
  }

  // ── Save ───────────────────────────────────────────────────────────────────
  const handleSave = useCallback(() => {
    const err = validate()
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setSaving(true)

    setTimeout(() => {
      const newHoliday = {
        id: nextId,
        session,
        holiday_name: holidayName.trim(),
        classes: selectedCls.includes('All Classes') ? ['All Classes'] : [...selectedCls],
        from_date: fromDate,
        to_date: toDate,
      }
      setHolidays(p => [newHoliday, ...p])
      setNextId(p => p + 1)
      // Reset form
      setSession('')
      setHolidayName('')
      setFromDate('')
      setToDate('')
      setSelectedCls([])
      setSaving(false)
      showToast('Holiday saved successfully!')
    }, 700)
  }, [session, holidayName, fromDate, toDate, selectedCls, nextId])

  // ── Reset form ────────────────────────────────────────────────────────────
  const handleReset = () => {
    setSession(''); setHolidayName(''); setFromDate(''); setToDate('')
    setSelectedCls([]); setErrors({})
  }

  // ── Delete ────────────────────────────────────────────────────────────────
  const confirmDelete = (holiday) => setDeleteTarget(holiday)
  const handleDelete = () => {
    setHolidays(p => p.filter(h => h.id !== deleteTarget.id))
    showToast(`"${deleteTarget.holiday_name}" deleted.`, 'error')
    setDeleteTarget(null)
  }

  // ── Filtered list ─────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    return holidays.filter(h => {
      const q = search.toLowerCase()
      const matchSearch = !q ||
        h.holiday_name.toLowerCase().includes(q) ||
        h.session.includes(q) ||
        classLabel(h.classes).toLowerCase().includes(q)
      const matchSes = !filterSes || h.session === filterSes
      return matchSearch && matchSes
    })
  }, [holidays, search, filterSes])

  // ─── RENDER ────────────────────────────────────────────────────────────────
  return (
    <div className="space-y-5 pb-12">

      {/* ── Page Title ─────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <CalendarCheck2 className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Define Holidays
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Add and manage session-wise holidays with class-level controls.
          </p>
        </div>
        <div className="flex items-center gap-2 text-[12px] font-semibold text-slate-500 dark:text-slate-400 bg-slate-100 dark:bg-slate-800 rounded-xl px-3 py-2 self-start sm:self-auto flex-shrink-0">
          <CalendarDays className="w-4 h-4 text-blue-500 flex-shrink-0" />
          {holidays.length} holiday{holidays.length !== 1 ? 's' : ''} defined
        </div>
      </div>

      {/* ══════════════════════════════════════════════════════════════════
          FORM CARD
      ══════════════════════════════════════════════════════════════════ */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        {/* Card Header */}
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <Plus className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">Add New Holiday</span>
          <button
            type="button"
            onClick={handleReset}
            className="flex items-center gap-1.5 text-[12px] font-semibold text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200 px-2.5 py-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">Reset</span>
          </button>
        </div>

        <div className="p-5 space-y-5">
          {/* Row 1: Session + Holiday Name + From + To */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
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

            {/* Holiday Name */}
            <Field label="Holiday Name" error={errors.holidayName} required>
              <div className="relative">
                <Tag className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  type="text"
                  value={holidayName}
                  onChange={e => { setHolidayName(e.target.value); setErrors(p => ({ ...p, holidayName: undefined })) }}
                  placeholder="e.g. Republic Day"
                  className={`w-full pl-8 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                    bg-white text-slate-800 placeholder-slate-300
                    focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                    ${errors.holidayName ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
                />
              </div>
            </Field>

            {/* From Date */}
            <Field label="From Date" error={errors.fromDate} required>
              <div className="relative">
                <CalendarDays className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  type="date"
                  value={fromDate}
                  onChange={e => { setFromDate(e.target.value); setErrors(p => ({ ...p, fromDate: undefined })) }}
                  className={`w-full pl-8 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                    bg-white text-slate-800
                    focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                    dark:[color-scheme:dark]
                    ${errors.fromDate ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
                />
              </div>
            </Field>

            {/* To Date */}
            <Field label="To Date" error={errors.toDate} required>
              <div className="relative">
                <CalendarRange className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <input
                  type="date"
                  value={toDate}
                  min={fromDate || undefined}
                  onChange={e => { setToDate(e.target.value); setErrors(p => ({ ...p, toDate: undefined })) }}
                  className={`w-full pl-8 pr-3 py-2 text-[13px] rounded-lg border outline-none transition-all
                    bg-white text-slate-800
                    focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:bg-[#1e2238] dark:text-slate-200 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                    dark:[color-scheme:dark]
                    ${errors.toDate ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20' : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'}`}
                />
              </div>
            </Field>
          </div>

          {/* Row 2: Apply For Class */}
          <Field label="Apply For Class" error={errors.classes} required>
            <ClassSelector
              selected={selectedCls}
              onChange={(v) => { setSelectedCls(v); setErrors(p => ({ ...p, classes: undefined })) }}
              error={errors.classes}
            />
          </Field>
        </div>

        {/* Footer */}
        <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3 px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/40 dark:bg-white/[0.01]">
          <button
            type="button"
            onClick={handleSave}
            disabled={saving}
            className="flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl text-[13px] font-semibold text-white
              bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20
              dark:bg-indigo-600 dark:hover:bg-indigo-700 dark:shadow-indigo-500/20
              transition-all active:scale-95 disabled:opacity-70"
          >
            {saving
              ? <><Loader2 className="w-4 h-4 animate-spin" /> Saving…</>
              : <><Check className="w-4 h-4" /> Save Holiday</>
            }
          </button>

          <button
            type="button"
            onClick={handleReset}
            className="flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl text-[13px] font-semibold
              bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            Reset Form
          </button>

          {/* Info hint */}
          <div className="hidden sm:flex items-center gap-1.5 text-[12px] text-slate-400 dark:text-slate-500 ml-auto">
            <Info className="w-3.5 h-3.5 flex-shrink-0" />
            All fields marked * are required.
          </div>
        </div>
      </div>

      {/* ══════════════════════════════════════════════════════════════════
          HOLIDAYS LIST
      ══════════════════════════════════════════════════════════════════ */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        {/* Card Header */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
            <span className="w-1 h-5 rounded-full bg-emerald-500 flex-shrink-0" />
            <BookOpen className="w-4 h-4 text-emerald-600 dark:text-emerald-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Holidays List</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
              {filtered.length} record{filtered.length !== 1 ? 's' : ''}
            </span>
          </div>

          {/* Search + Filter */}
          <div className="flex gap-2 w-full sm:w-auto">
            {/* Search */}
            <div className="relative flex-1 sm:w-52">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search holidays…"
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

            {/* Session filter */}
            <div className="w-32 flex-shrink-0">
              <NativeSelect value={filterSes} onChange={e => setFilterSes(e.target.value)} placeholder="All Sessions">
                {SESSIONS.map(s => <option key={s} value={s}>{s}</option>)}
              </NativeSelect>
            </div>
          </div>
        </div>

        {/* ── DESKTOP TABLE ── */}
        <div className="hidden md:block overflow-x-auto">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-28 gap-2 text-slate-400 dark:text-slate-600">
              <CalendarDays className="w-7 h-7 opacity-40" />
              <span className="text-[13px]">No holidays found.</span>
            </div>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                  {['S.No.', 'Holiday Name', 'Session', 'Apply For Class', 'From Date', 'To Date', 'Action'].map((h, i) => (
                    <th key={i} className="px-4 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap first:text-center first:w-12">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.map((h, i) => (
                  <tr
                    key={h.id}
                    className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
                  >
                    {/* S.No */}
                    <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums">{i + 1}</td>

                    {/* Holiday Name */}
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2.5">
                        <div className="w-7 h-7 rounded-lg bg-blue-50 dark:bg-indigo-500/15 flex items-center justify-center flex-shrink-0">
                          <CalendarDays className="w-3.5 h-3.5 text-blue-600 dark:text-indigo-400" />
                        </div>
                        <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200 whitespace-nowrap">{h.holiday_name}</span>
                      </div>
                    </td>

                    {/* Session */}
                    <td className="px-4 py-3">
                      <span className="inline-flex items-center px-2.5 py-1 rounded-full text-[11px] font-bold bg-amber-50 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400 whitespace-nowrap">
                        {h.session}
                      </span>
                    </td>

                    {/* Classes */}
                    <td className="px-4 py-3">
                      <div className="flex flex-wrap gap-1 max-w-[220px]">
                        {(h.classes.includes('All Classes') ? ['All Classes'] : h.classes.slice(0, 4)).map(cls => (
                          <span key={cls} className="px-1.5 py-0.5 rounded text-[10px] font-semibold bg-blue-50 text-blue-700 dark:bg-indigo-500/15 dark:text-indigo-300 whitespace-nowrap">
                            {cls}
                          </span>
                        ))}
                        {!h.classes.includes('All Classes') && h.classes.length > 4 && (
                          <span className="px-1.5 py-0.5 rounded text-[10px] font-semibold bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400">
                            +{h.classes.length - 4} more
                          </span>
                        )}
                      </div>
                    </td>

                    {/* From */}
                    <td className="px-4 py-3 whitespace-nowrap">
                      <span className="text-[12px] font-semibold text-slate-600 dark:text-slate-300">{formatDate(h.from_date)}</span>
                    </td>

                    {/* To */}
                    <td className="px-4 py-3 whitespace-nowrap">
                      <span className="text-[12px] font-semibold text-slate-600 dark:text-slate-300">{formatDate(h.to_date)}</span>
                    </td>

                    {/* Action */}
                    <td className="px-4 py-3">
                      <button
                        type="button"
                        onClick={() => confirmDelete(h)}
                        className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
                          bg-rose-50 text-rose-600 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20 border border-rose-100 dark:border-rose-500/20
                          transition-colors"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* ── MOBILE CARDS ── */}
        <div className="md:hidden p-4 space-y-3">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-24 gap-2 text-slate-400 dark:text-slate-600">
              <CalendarDays className="w-6 h-6 opacity-40" />
              <span className="text-[13px]">No holidays found.</span>
            </div>
          ) : (
            <>
              <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5 pb-1">
                <Info className="w-3.5 h-3.5 flex-shrink-0" />
                Tap a card to view details or delete.
              </p>
              {filtered.map((h, i) => (
                <MobileHolidayCard key={h.id} holiday={h} idx={i + 1} onDelete={confirmDelete} />
              ))}
            </>
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{holidays.length}</span> records
          </p>
          {(search || filterSes) && (
            <button
              onClick={() => { setSearch(''); setFilterSes('') }}
              className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1"
            >
              <X className="w-3 h-3" /> Clear filters
            </button>
          )}
        </div>
      </div>

      {/* ── Confirm Delete Modal ─────────────────────────────────────────── */}
      <ConfirmModal
        open={!!deleteTarget}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        holidayName={deleteTarget?.holiday_name || ''}
      />

      {/* ── Toast ────────────────────────────────────────────────────────── */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
