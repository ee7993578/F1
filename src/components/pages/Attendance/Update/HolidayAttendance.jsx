/**
 * HolidayAttendance.jsx
 * Folder: src/pages/Configuration/HolidayAttendance.jsx
 *
 * Converts legacy ASPX "Holiday Attendance" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Date picker (calendar-style input)
 *  - Select All checkbox + CheckboxList for classes (horizontal on desktop, grid on mobile)
 *  - Save button with validation
 *  - Toast feedback
 *  - Loading overlay
 *  - Mobile: stacked card-based layout with drawer-style filter
 *  - Desktop: clean ERP-style panel
 */

import { useState, useCallback, useMemo } from 'react'
import {
  CalendarDays, CheckSquare, Square, ChevronDown, Save,
  AlertCircle, Check, X, Loader2, Info, RefreshCw,
  BookOpen, GraduationCap, School, ClipboardList,
  SlidersHorizontal, ChevronRight, Calendar
} from 'lucide-react'

// ─── STATIC DATA ──────────────────────────────────────────────────────────────
const CLASS_LIST = [
  'Nursery', 'LKG', 'UKG',
  'Class I', 'Class II', 'Class III', 'Class IV', 'Class V',
  'Class VI', 'Class VII', 'Class VIII',
  'Class IX', 'Class X', 'Class XI', 'Class XII',
]

// Color palette per class (cycles)
const CLASS_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' },
  { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' },
  { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' },
  { fg: '#dc2626', bg: '#fee2e2' },
  { fg: '#0369a1', bg: '#e0f2fe' },
]
const classColor = (name) =>
  CLASS_COLORS[(name?.charCodeAt(0) ?? 0) % CLASS_COLORS.length]

// ─── HELPERS ──────────────────────────────────────────────────────────────────
const getTodayStr = () => {
  const d = new Date()
  return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

// ─── REUSABLE PRIMITIVES ──────────────────────────────────────────────────────

/** Toast notification */
function Toast({ message, type = 'success', onClose }) {
  return (
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3
        rounded-2xl shadow-2xl text-[13px] font-semibold min-w-[260px] max-w-[90vw]
        ${type === 'success' ? 'bg-emerald-600 text-white' : 'bg-rose-600 text-white'}`}
      style={{ animation: 'slideUp .25s ease' }}
    >
      {type === 'success'
        ? <Check className="w-4 h-4 flex-shrink-0" />
        : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose} className="opacity-75 hover:opacity-100 transition-opacity">
        <X className="w-4 h-4" />
      </button>
      <style>{`
        @keyframes slideUp {
          from { opacity:0; transform:translateX(-50%) translateY(10px) }
          to   { opacity:1; transform:translateX(-50%) translateY(0) }
        }
      `}</style>
    </div>
  )
}

/** Loading overlay */
function LoadingOverlay() {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/30 backdrop-blur-sm">
      <div className="bg-white dark:bg-[#1a1f35] rounded-2xl px-8 py-6 flex flex-col items-center gap-3 shadow-2xl border border-slate-100 dark:border-[rgba(99,102,241,0.2)]">
        <Loader2 className="w-8 h-8 text-blue-600 dark:text-indigo-400 animate-spin" />
        <p className="text-[13px] font-semibold text-slate-600 dark:text-slate-300">Saving attendance…</p>
      </div>
    </div>
  )
}

/** Class chip / badge for the checklist */
function ClassChip({ label, checked, onToggle }) {
  const { fg, bg } = classColor(label)
  return (
    <button
      type="button"
      onClick={onToggle}
      className={`flex items-center gap-2 px-3 py-2 rounded-xl border text-[13px] font-semibold transition-all duration-150 select-none
        ${checked
          ? 'border-blue-400 bg-blue-50 text-blue-700 dark:bg-indigo-500/15 dark:border-indigo-400 dark:text-indigo-300 shadow-sm'
          : 'border-slate-200 bg-white text-slate-600 dark:bg-[#1e2238] dark:border-[rgba(99,102,241,0.2)] dark:text-slate-400 hover:border-blue-300 hover:bg-blue-50/40 dark:hover:border-indigo-400/40'
        }`}
    >
      {/* colored dot */}
      <span
        className="w-6 h-6 rounded-lg flex items-center justify-center text-[9px] font-bold flex-shrink-0"
        style={{ background: bg, color: fg }}
      >
        {label.replace('Class ', '').slice(0, 2).toUpperCase()}
      </span>
      <span className="whitespace-nowrap">{label}</span>
      <span className="ml-auto">
        {checked
          ? <CheckSquare className="w-4 h-4 text-blue-500 dark:text-indigo-400" />
          : <Square className="w-4 h-4 text-slate-300 dark:text-slate-600" />
        }
      </span>
    </button>
  )
}

/** Summary badge – shows selected class pill */
function SelectedBadge({ label, onRemove }) {
  const { fg, bg } = classColor(label)
  return (
    <span
      className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-semibold"
      style={{ background: bg, color: fg }}
    >
      {label}
      <button
        type="button"
        onClick={onRemove}
        className="hover:opacity-70 transition-opacity ml-0.5"
      >
        <X className="w-3 h-3" />
      </button>
    </span>
  )
}

// ─── MOBILE CLASS DRAWER ──────────────────────────────────────────────────────
function ClassDrawer({ open, onClose, classes, selected, onToggle, onSelectAll, allSelected }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onClose} />
      <div
        className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35] border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl max-h-[80vh] flex flex-col"
        style={{ animation: 'drawerUp .25s ease' }}
      >
        <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>

        {/* Handle */}
        <div className="flex justify-center pt-3 pb-1 flex-shrink-0">
          <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
        </div>

        {/* Header */}
        <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex-shrink-0">
          <div className="flex items-center gap-2">
            <GraduationCap className="w-4 h-4 text-blue-600 dark:text-indigo-400" />
            <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Select Classes</span>
          </div>
          <button onClick={onClose} className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400">
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Select All toggle */}
        <div className="px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.08)] flex-shrink-0">
          <button
            type="button"
            onClick={onSelectAll}
            className={`flex items-center gap-2 w-full px-4 py-2.5 rounded-xl border text-[13px] font-semibold transition-all
              ${allSelected
                ? 'bg-blue-600 border-blue-600 text-white dark:bg-indigo-600 dark:border-indigo-600'
                : 'bg-slate-100 border-slate-200 text-slate-700 dark:bg-slate-800 dark:border-slate-700 dark:text-slate-300'
              }`}
          >
            {allSelected
              ? <CheckSquare className="w-4 h-4" />
              : <Square className="w-4 h-4" />
            }
            Select All Classes
            <span className="ml-auto text-[11px] opacity-75">{selected.length}/{classes.length}</span>
          </button>
        </div>

        {/* Class grid – scrollable */}
        <div className="overflow-y-auto flex-1 px-5 py-4">
          <div className="grid grid-cols-1 gap-2">
            {classes.map(cls => (
              <ClassChip
                key={cls}
                label={cls}
                checked={selected.includes(cls)}
                onToggle={() => onToggle(cls)}
              />
            ))}
          </div>
        </div>

        {/* Footer */}
        <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex-shrink-0">
          <button
            type="button"
            onClick={onClose}
            className="w-full py-3 rounded-xl text-[13px] font-semibold bg-blue-600 text-white hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 transition-colors"
          >
            Done ({selected.length} selected)
          </button>
        </div>
      </div>
    </>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function HolidayAttendance() {
  const [selectedDate,    setSelectedDate]    = useState('')
  const [selectedClasses, setSelectedClasses] = useState([])
  const [loading,         setLoading]         = useState(false)
  const [toast,           setToast]           = useState(null)
  const [errors,          setErrors]          = useState({})
  const [drawerOpen,      setDrawerOpen]      = useState(false)
  const [savedRecords,    setSavedRecords]    = useState([]) // history of saved entries

  const allSelected = selectedClasses.length === CLASS_LIST.length

  // ── Toast helper ──────────────────────────────────────────────────────────
  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }

  // ── Select All toggle ────────────────────────────────────────────────────
  const handleSelectAll = useCallback(() => {
    setSelectedClasses(prev =>
      prev.length === CLASS_LIST.length ? [] : [...CLASS_LIST]
    )
    setErrors(p => ({ ...p, classes: undefined }))
  }, [])

  // ── Individual class toggle ──────────────────────────────────────────────
  const handleToggleClass = useCallback((cls) => {
    setSelectedClasses(prev =>
      prev.includes(cls) ? prev.filter(c => c !== cls) : [...prev, cls]
    )
    setErrors(p => ({ ...p, classes: undefined }))
  }, [])

  // ── Remove a badge ───────────────────────────────────────────────────────
  const handleRemoveClass = useCallback((cls) => {
    setSelectedClasses(prev => prev.filter(c => c !== cls))
  }, [])

  // ── Reset ────────────────────────────────────────────────────────────────
  const handleReset = () => {
    setSelectedDate('')
    setSelectedClasses([])
    setErrors({})
  }

  // ── Validate & Save ──────────────────────────────────────────────────────
  const handleSave = useCallback(() => {
    const err = {}
    if (!selectedDate) err.date = 'Please select a date'
    if (selectedClasses.length === 0) err.classes = 'Please select at least one class'
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setLoading(true)

    // Simulate API call
    setTimeout(() => {
      setSavedRecords(prev => [
        { date: selectedDate, classes: [...selectedClasses], id: Date.now() },
        ...prev,
      ])
      setLoading(false)
      showToast(`Holiday attendance saved for ${selectedClasses.length} class(es) on ${selectedDate}!`)
      handleReset()
    }, 1200)
  }, [selectedDate, selectedClasses])

  // ── Today's quick fill ───────────────────────────────────────────────────
  const fillToday = () => {
    setSelectedDate(getTodayStr())
    setErrors(p => ({ ...p, date: undefined }))
  }

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-[#111827] p-4 sm:p-6 space-y-5 pb-16">

      {loading && <LoadingOverlay />}

      {/* ── Breadcrumb ─────────────────────────────────────────────────── */}
      <nav className="flex items-center gap-1.5 text-[12px] text-slate-400 dark:text-slate-500">
        <span className="hover:text-blue-600 dark:hover:text-indigo-400 cursor-pointer transition-colors">Home</span>
        <ChevronRight className="w-3.5 h-3.5" />
        <span className="text-slate-600 dark:text-slate-300 font-semibold">Holiday Attendance</span>
      </nav>

      {/* ── Page Title ─────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <CalendarDays className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Holiday Attendance
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Mark attendance as holiday for selected classes on a specific date.
          </p>
        </div>

        {/* Info chip */}
        <div className="flex items-center gap-2 px-3 py-2 rounded-xl bg-amber-50 dark:bg-amber-500/10 border border-amber-200 dark:border-amber-500/25 self-start sm:self-auto">
          <Info className="w-3.5 h-3.5 text-amber-600 dark:text-amber-400 flex-shrink-0" />
          <span className="text-[12px] font-semibold text-amber-700 dark:text-amber-400">
            All selected classes will be marked as holiday
          </span>
        </div>
      </div>

      {/* ── Main Form Card ─────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Card Header */}
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          <ClipboardList className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Holiday Attendance</span>
        </div>

        <div className="p-5 space-y-6">

          {/* ── DATE FIELD ─────────────────────────────────────────────── */}
          <div>
            <label className="block text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide mb-2">
              Select Date <span className="text-rose-500">*</span>
            </label>
            <div className="flex flex-col sm:flex-row gap-2 max-w-sm">
              <div className="relative flex-1">
                <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 pointer-events-none" />
                <input
                  type="date"
                  value={selectedDate
                    ? (() => {
                        // parse "dd MMM yyyy" back to yyyy-mm-dd for the input
                        try {
                          const parts = selectedDate.split(' ')
                          const months = { Jan:'01',Feb:'02',Mar:'03',Apr:'04',May:'05',Jun:'06',Jul:'07',Aug:'08',Sep:'09',Oct:'10',Nov:'11',Dec:'12' }
                          return `${parts[2]}-${months[parts[1]]}-${parts[0]}`
                        } catch { return '' }
                      })()
                    : ''
                  }
                  onChange={e => {
                    const v = e.target.value
                    if (!v) { setSelectedDate(''); return }
                    const d = new Date(v)
                    const formatted = d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
                    setSelectedDate(formatted)
                    setErrors(p => ({ ...p, date: undefined }))
                  }}
                  className={`w-full pl-9 pr-3 py-2.5 text-[13px] rounded-xl border outline-none transition-all
                    bg-white text-slate-800 dark:bg-[#1e2238] dark:text-slate-200
                    focus:border-blue-400 focus:ring-2 focus:ring-blue-100
                    dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
                    ${errors.date
                      ? 'border-rose-400 ring-2 ring-rose-100'
                      : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
                    }`}
                />
              </div>
              {/* Today quick fill */}
              <button
                type="button"
                onClick={fillToday}
                className="flex items-center gap-1.5 px-4 py-2.5 rounded-xl text-[12px] font-semibold
                  bg-slate-100 text-slate-700 hover:bg-slate-200
                  dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700
                  border border-slate-200 dark:border-slate-700 transition-colors whitespace-nowrap"
              >
                <CalendarDays className="w-3.5 h-3.5" />
                Today
              </button>
            </div>

            {/* Date error */}
            {errors.date && (
              <p className="flex items-center gap-1.5 text-[11px] text-rose-500 mt-1.5">
                <AlertCircle className="w-3 h-3 flex-shrink-0" />
                {errors.date}
              </p>
            )}

            {/* Date display pill */}
            {selectedDate && (
              <div className="mt-2 inline-flex items-center gap-2 px-3 py-1.5 rounded-lg bg-blue-50 dark:bg-blue-500/10 border border-blue-100 dark:border-blue-500/20">
                <CalendarDays className="w-3.5 h-3.5 text-blue-600 dark:text-blue-400" />
                <span className="text-[12px] font-semibold text-blue-700 dark:text-blue-300">{selectedDate}</span>
              </div>
            )}
          </div>

          {/* ── CLASS SELECTION FIELD ───────────────────────────────────── */}
          <div>
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2 mb-3">
              <label className="text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">
                Apply For Class <span className="text-rose-500">*</span>
              </label>

              {/* Select All — desktop */}
              <button
                type="button"
                onClick={handleSelectAll}
                className={`hidden sm:flex items-center gap-2 px-4 py-1.5 rounded-xl text-[12px] font-semibold border transition-all
                  ${allSelected
                    ? 'bg-blue-600 text-white border-blue-600 dark:bg-indigo-600 dark:border-indigo-600'
                    : 'bg-white text-slate-600 border-slate-200 hover:border-blue-400 hover:text-blue-600 dark:bg-[#1e2238] dark:text-slate-400 dark:border-[rgba(99,102,241,0.25)]'
                  }`}
              >
                {allSelected
                  ? <CheckSquare className="w-3.5 h-3.5" />
                  : <Square className="w-3.5 h-3.5" />
                }
                {allSelected ? 'Deselect All' : 'Select All'}
                <span className="ml-0.5 text-[10px] opacity-70">{selectedClasses.length}/{CLASS_LIST.length}</span>
              </button>
            </div>

            {/* ── MOBILE: Open drawer button ──────────────────────────── */}
            <div className="sm:hidden">
              <button
                type="button"
                onClick={() => setDrawerOpen(true)}
                className={`w-full flex items-center justify-between px-4 py-3 rounded-xl border text-[13px] font-semibold transition-all
                  ${errors.classes
                    ? 'border-rose-400 bg-rose-50 text-rose-600 dark:bg-rose-500/10'
                    : selectedClasses.length > 0
                      ? 'border-blue-400 bg-blue-50 text-blue-700 dark:bg-indigo-500/10 dark:border-indigo-400 dark:text-indigo-300'
                      : 'border-slate-200 bg-white text-slate-500 dark:bg-[#1e2238] dark:border-[rgba(99,102,241,0.25)]'
                  }`}
              >
                <div className="flex items-center gap-2">
                  <GraduationCap className="w-4 h-4 flex-shrink-0" />
                  {selectedClasses.length === 0
                    ? 'Tap to select classes'
                    : selectedClasses.length === CLASS_LIST.length
                      ? 'All Classes Selected'
                      : `${selectedClasses.length} class${selectedClasses.length > 1 ? 'es' : ''} selected`
                  }
                </div>
                <ChevronRight className="w-4 h-4 text-slate-400" />
              </button>

              {/* Selected class badges – mobile */}
              {selectedClasses.length > 0 && (
                <div className="flex flex-wrap gap-1.5 mt-2">
                  {selectedClasses.map(cls => (
                    <SelectedBadge key={cls} label={cls} onRemove={() => handleRemoveClass(cls)} />
                  ))}
                </div>
              )}
            </div>

            {/* ── DESKTOP: Chip grid ──────────────────────────────────── */}
            <div className="hidden sm:grid grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-2">
              {CLASS_LIST.map(cls => (
                <ClassChip
                  key={cls}
                  label={cls}
                  checked={selectedClasses.includes(cls)}
                  onToggle={() => handleToggleClass(cls)}
                />
              ))}
            </div>

            {/* Classes error */}
            {errors.classes && (
              <p className="flex items-center gap-1.5 text-[11px] text-rose-500 mt-2">
                <AlertCircle className="w-3 h-3 flex-shrink-0" />
                {errors.classes}
              </p>
            )}
          </div>

          {/* ── PREVIEW SUMMARY ────────────────────────────────────────── */}
          {(selectedDate || selectedClasses.length > 0) && (
            <div className="rounded-xl border border-blue-100 dark:border-indigo-500/20 bg-blue-50/60 dark:bg-indigo-500/[0.06] p-4 space-y-3">
              <p className="text-[12px] font-bold uppercase tracking-wide text-blue-700 dark:text-blue-400 flex items-center gap-2">
                <Info className="w-3.5 h-3.5" /> Attendance Preview
              </p>
              <div className="flex flex-wrap gap-4 text-[13px]">
                <div>
                  <span className="text-slate-500 dark:text-slate-400 font-medium">Date: </span>
                  <span className="font-semibold text-slate-800 dark:text-slate-100">{selectedDate || '—'}</span>
                </div>
                <div>
                  <span className="text-slate-500 dark:text-slate-400 font-medium">Classes: </span>
                  <span className="font-semibold text-slate-800 dark:text-slate-100">
                    {selectedClasses.length === 0 ? '—' : selectedClasses.length === CLASS_LIST.length ? 'All' : selectedClasses.length}
                  </span>
                </div>
              </div>
              {selectedClasses.length > 0 && (
                <div className="flex flex-wrap gap-1.5">
                  {selectedClasses.map(cls => (
                    <SelectedBadge key={cls} label={cls} onRemove={() => handleRemoveClass(cls)} />
                  ))}
                </div>
              )}
            </div>
          )}
        </div>

        {/* ── Card Footer – Actions ─────────────────────────────────────── */}
        <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-3 px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <button
            type="button"
            onClick={handleSave}
            disabled={loading}
            className="flex items-center justify-center gap-2 px-6 py-2.5 rounded-xl text-[13px] font-bold
              bg-emerald-600 hover:bg-emerald-700 text-white shadow-md shadow-emerald-500/20
              transition-all active:scale-95 disabled:opacity-70"
          >
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Save className="w-4 h-4" />}
            Save
          </button>

          <button
            type="button"
            onClick={handleReset}
            className="flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl text-[13px] font-semibold
              bg-white text-slate-700 border border-slate-200 hover:bg-slate-100
              dark:bg-[#1e2238] dark:text-slate-300 dark:border-[rgba(99,102,241,0.2)] dark:hover:bg-slate-800
              transition-colors"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            Reset
          </button>

          {selectedClasses.length > 0 && (
            <p className="text-[12px] text-slate-400 dark:text-slate-500 sm:ml-auto text-center sm:text-right">
              <span className="font-semibold text-blue-600 dark:text-indigo-400">{selectedClasses.length}</span> class{selectedClasses.length > 1 ? 'es' : ''} selected
            </p>
          )}
        </div>
      </div>

      {/* ── Saved History ──────────────────────────────────────────────────── */}
      {savedRecords.length > 0 && (
        <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
          <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
            <span className="w-1 h-5 rounded-full bg-emerald-500 flex-shrink-0" />
            <BookOpen className="w-4 h-4 text-emerald-600 dark:text-emerald-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Saved Records</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-400">
              {savedRecords.length} entr{savedRecords.length > 1 ? 'ies' : 'y'}
            </span>
          </div>

          {/* Desktop table */}
          <div className="hidden md:block overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                  {['S.No.', 'Date', 'Classes Applied', 'Total Classes'].map((h, i) => (
                    <th key={i} className="px-5 py-2.5 text-left text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {savedRecords.map((rec, i) => (
                  <tr key={rec.id} className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/50 dark:hover:bg-white/[0.02] transition-colors">
                    <td className="px-5 py-3 text-[12px] text-slate-400 dark:text-slate-500 tabular-nums">{i + 1}</td>
                    <td className="px-5 py-3">
                      <div className="flex items-center gap-2">
                        <CalendarDays className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
                        <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200">{rec.date}</span>
                      </div>
                    </td>
                    <td className="px-5 py-3">
                      <div className="flex flex-wrap gap-1">
                        {rec.classes.length === CLASS_LIST.length
                          ? <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">All Classes</span>
                          : rec.classes.slice(0, 5).map(cls => {
                              const { fg, bg } = classColor(cls)
                              return (
                                <span key={cls} className="inline-flex px-2 py-0.5 rounded-full text-[11px] font-semibold" style={{ background: bg, color: fg }}>
                                  {cls}
                                </span>
                              )
                            })
                        }
                        {rec.classes.length > 5 && rec.classes.length !== CLASS_LIST.length && (
                          <span className="inline-flex px-2 py-0.5 rounded-full text-[11px] font-semibold bg-slate-100 text-slate-500 dark:bg-slate-800 dark:text-slate-400">
                            +{rec.classes.length - 5} more
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-5 py-3">
                      <span className="inline-flex items-center justify-center w-8 h-8 rounded-lg text-[13px] font-bold bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-400">
                        {rec.classes.length}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Mobile cards for saved records */}
          <div className="md:hidden p-4 space-y-3">
            {savedRecords.map((rec, i) => (
              <div key={rec.id} className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-slate-50 dark:bg-[#1e2238] p-4">
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center gap-2">
                    <CalendarDays className="w-4 h-4 text-blue-500" />
                    <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">{rec.date}</span>
                  </div>
                  <span className="text-[11px] font-semibold px-2.5 py-1 rounded-lg bg-emerald-100 text-emerald-700 dark:bg-emerald-500/15 dark:text-emerald-400">
                    {rec.classes.length} class{rec.classes.length > 1 ? 'es' : ''}
                  </span>
                </div>
                <div className="flex flex-wrap gap-1.5">
                  {rec.classes.length === CLASS_LIST.length
                    ? <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-semibold bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400">All Classes</span>
                    : rec.classes.map(cls => {
                        const { fg, bg } = classColor(cls)
                        return (
                          <span key={cls} className="inline-flex px-2 py-0.5 rounded-full text-[11px] font-semibold" style={{ background: bg, color: fg }}>
                            {cls}
                          </span>
                        )
                      })
                  }
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ── Mobile Class Drawer ─────────────────────────────────────────────── */}
      <ClassDrawer
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        classes={CLASS_LIST}
        selected={selectedClasses}
        onToggle={handleToggleClass}
        onSelectAll={handleSelectAll}
        allSelected={allSelected}
      />

      {/* ── Toast ──────────────────────────────────────────────────────────── */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
