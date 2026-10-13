/**
 * SupplierCategory.jsx
 * Folder: src/pages/Library/SupplierCategory.jsx
 *
 * Converts legacy ASPX "Define Supplier Category" to fully-responsive React + Tailwind.
 *
 * Features:
 *  - Add / Edit / Delete supplier categories
 *  - Code, Name, Remark fields with validation
 *  - GridView → Desktop table + Mobile card layout
 *  - Toast notifications, loading states, empty states
 *  - Consistent design language with StrengthReport
 */

import { useState, useMemo, useCallback, useRef } from 'react'
import {
  Plus, Edit2, Trash2, Save, X,
  Search, RefreshCw, AlertCircle,
  Check, Loader2, Tag, AlignLeft,
  Hash, Info, Package, ChevronRight,
  SlidersHorizontal, BookOpen, Layers
} from 'lucide-react'

// ─── STATIC DUMMY DATA ────────────────────────────────────────────────────────
const INITIAL_CATEGORIES = [
  { supcat_id: 1, code: 'GEN',  name: 'GENERAL SUPPLIER',        remark: 'All general purpose suppliers'         },
  { supcat_id: 2, code: 'MED',  name: 'MEDICAL SUPPLIER',        remark: 'Pharmaceutical and medical equipment'   },
  { supcat_id: 3, code: 'LAB',  name: 'LABORATORY SUPPLIER',     remark: 'Lab consumables and chemicals'          },
  { supcat_id: 4, code: 'STA',  name: 'STATIONERY SUPPLIER',     remark: 'Paper, pens and office stationery'      },
  { supcat_id: 5, code: 'ELE',  name: 'ELECTRICAL SUPPLIER',     remark: 'Electrical components and wiring'       },
  { supcat_id: 6, code: 'SPT',  name: 'SPORTS SUPPLIER',         remark: 'Sports equipment and uniforms'          },
  { supcat_id: 7, code: 'CAN',  name: 'CANTEEN SUPPLIER',        remark: 'Food and beverages for canteen'         },
]

// ─── COLOR PALETTE FOR CATEGORY BADGES ───────────────────────────────────────
const CAT_COLORS = [
  { fg: '#1d4ed8', bg: '#dbeafe' },
  { fg: '#7c3aed', bg: '#ede9fe' },
  { fg: '#0891b2', bg: '#cffafe' },
  { fg: '#059669', bg: '#d1fae5' },
  { fg: '#d97706', bg: '#fef3c7' },
  { fg: '#dc2626', bg: '#fee2e2' },
  { fg: '#0369a1', bg: '#e0f2fe' },
]
const catColor = (code = '') =>
  CAT_COLORS[(code.charCodeAt(0) ?? 0) % CAT_COLORS.length]

let nextId = INITIAL_CATEGORIES.length + 1

// ─── TOAST COMPONENT ─────────────────────────────────────────────────────────
function Toast({ message, type = 'success', onClose }) {
  return (
    <div
      className={`fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-3 px-5 py-3
        rounded-2xl shadow-xl text-[13px] font-semibold min-w-[260px] max-w-[90vw]
        ${type === 'success' ? 'bg-emerald-600 text-white' : type === 'error' ? 'bg-rose-600 text-white' : 'bg-amber-500 text-white'}`}
      style={{ animation: 'slideUp .25s ease' }}
    >
      {type === 'success' ? <Check className="w-4 h-4 flex-shrink-0" /> : <AlertCircle className="w-4 h-4 flex-shrink-0" />}
      <span className="flex-1">{message}</span>
      <button onClick={onClose}><X className="w-4 h-4 opacity-75 hover:opacity-100" /></button>
      <style>{`@keyframes slideUp{from{opacity:0;transform:translateX(-50%) translateY(10px)}to{opacity:1;transform:translateX(-50%) translateY(0)}}`}</style>
    </div>
  )
}

// ─── CONFIRM MODAL ────────────────────────────────────────────────────────────
function ConfirmModal({ open, onConfirm, onCancel, itemName }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onCancel} />
      <div
        className="fixed inset-1/2 -translate-x-1/2 -translate-y-1/2 z-50 w-[90vw] max-w-sm
          rounded-2xl bg-white dark:bg-[#1a1f35] border border-slate-200 dark:border-[rgba(99,102,241,0.2)]
          shadow-2xl p-6 h-fit"
        style={{ animation: 'popIn .2s ease' }}
      >
        <style>{`@keyframes popIn{from{opacity:0;transform:translate(-50%,-50%) scale(.95)}to{opacity:1;transform:translate(-50%,-50%) scale(1)}}`}</style>
        <div className="flex flex-col items-center gap-4 text-center">
          <div className="w-14 h-14 rounded-2xl bg-rose-100 dark:bg-rose-500/15 flex items-center justify-center">
            <Trash2 className="w-7 h-7 text-rose-600 dark:text-rose-400" />
          </div>
          <div>
            <p className="text-[15px] font-bold text-slate-800 dark:text-slate-100">Delete Category?</p>
            <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-1">
              <span className="font-semibold text-rose-600 dark:text-rose-400">{itemName}</span> will be permanently removed.
            </p>
          </div>
          <div className="flex gap-3 w-full">
            <button
              onClick={onCancel}
              className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors"
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
      </div>
    </>
  )
}

// ─── FORM FIELD ───────────────────────────────────────────────────────────────
function Field({ label, error, required, children, hint }) {
  return (
    <div className="flex flex-col gap-1">
      <label className="text-[12px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide flex items-center gap-1">
        {label}
        {required && <span className="text-rose-500">*</span>}
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

// ─── STYLED INPUT ─────────────────────────────────────────────────────────────
function Input({ error, className = '', ...props }) {
  return (
    <input
      {...props}
      className={`w-full px-3 py-2 text-[13px] rounded-lg border outline-none transition-all
        bg-white text-slate-800 placeholder-slate-300
        focus:border-blue-400 focus:ring-2 focus:ring-blue-100
        dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600
        dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
        disabled:opacity-50 disabled:cursor-not-allowed
        ${error
          ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20'
          : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
        } ${className}`}
    />
  )
}

// ─── STYLED TEXTAREA ─────────────────────────────────────────────────────────
function Textarea({ error, className = '', ...props }) {
  return (
    <textarea
      {...props}
      rows={3}
      className={`w-full px-3 py-2 text-[13px] rounded-lg border outline-none transition-all resize-none
        bg-white text-slate-800 placeholder-slate-300
        focus:border-blue-400 focus:ring-2 focus:ring-blue-100
        dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600
        dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
        ${error
          ? 'border-rose-400 ring-2 ring-rose-100 dark:ring-rose-500/20'
          : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
        } ${className}`}
    />
  )
}

// ─── SUMMARY STAT CARD ────────────────────────────────────────────────────────
function SummaryCard({ icon: Icon, label, value, color }) {
  const colors = {
    blue:    'bg-blue-50 text-blue-600 dark:bg-blue-500/10 dark:text-blue-400',
    emerald: 'bg-emerald-50 text-emerald-600 dark:bg-emerald-500/10 dark:text-emerald-400',
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
      </div>
    </div>
  )
}

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ row, idx, onEdit, onDelete }) {
  const { fg, bg } = catColor(row.code)
  return (
    <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors group">
      {/* S.No */}
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{idx}</td>

      {/* Code */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-2.5">
          <span
            className="w-8 h-8 rounded-lg flex-shrink-0 flex items-center justify-center text-[10px] font-bold"
            style={{ background: bg, color: fg }}
          >
            {row.code?.slice(0, 3)}
          </span>
          <span className="text-[13px] font-bold text-slate-700 dark:text-slate-200 font-mono tracking-wider">{row.code}</span>
        </div>
      </td>

      {/* Name */}
      <td className="px-4 py-3">
        <span className="text-[13px] font-semibold text-slate-700 dark:text-slate-200">{row.name}</span>
      </td>

      {/* Remark */}
      <td className="px-4 py-3">
        <span className="text-[12px] text-slate-500 dark:text-slate-400 line-clamp-2">{row.remark || '—'}</span>
      </td>

      {/* Actions */}
      <td className="px-4 py-3">
        <div className="flex items-center gap-2 opacity-70 group-hover:opacity-100 transition-opacity">
          <button
            onClick={() => onEdit(row)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
              bg-blue-50 text-blue-700 hover:bg-blue-100 dark:bg-blue-500/10 dark:text-blue-400 dark:hover:bg-blue-500/20
              transition-colors"
          >
            <Edit2 className="w-3 h-3" />
            Edit
          </button>
          <button
            onClick={() => onDelete(row)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
              bg-rose-50 text-rose-700 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20
              transition-colors"
          >
            <Trash2 className="w-3 h-3" />
            Delete
          </button>
        </div>
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────
function MobileCard({ row, idx, onEdit, onDelete }) {
  const [expanded, setExpanded] = useState(false)
  const { fg, bg } = catColor(row.code)

  return (
    <div className="rounded-xl border border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35] overflow-hidden shadow-sm">
      {/* Header */}
      <button
        type="button"
        onClick={() => setExpanded(p => !p)}
        className="w-full flex items-center gap-3 px-4 py-3.5 text-left hover:bg-slate-50/60 dark:hover:bg-white/[0.02] transition-colors"
      >
        <span
          className="w-9 h-9 rounded-xl flex-shrink-0 flex items-center justify-center text-[10px] font-bold"
          style={{ background: bg, color: fg }}
        >
          {row.code?.slice(0, 3)}
        </span>
        <div className="flex-1 min-w-0">
          <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight truncate">{row.name}</p>
          <p className="text-[11px] text-slate-400 dark:text-slate-500 mt-0.5 font-mono tracking-wider">{row.code}</p>
        </div>
        <span className={`w-5 h-5 flex items-center justify-center ml-1 text-slate-400 transition-transform duration-200 flex-shrink-0 ${expanded ? 'rotate-90' : ''}`}>
          <ChevronRight className="w-4 h-4" />
        </span>
      </button>

      {/* Remark preview (always visible if exists) */}
      {row.remark && !expanded && (
        <div className="px-4 pb-3">
          <p className="text-[11px] text-slate-400 dark:text-slate-500 line-clamp-1">{row.remark}</p>
        </div>
      )}

      {/* Expanded */}
      {expanded && (
        <div className="border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] px-4 pt-3 pb-4 space-y-3">
          {row.remark && (
            <div className="rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-700/50 p-3">
              <p className="text-[10px] font-bold uppercase tracking-wide text-slate-400 dark:text-slate-500 mb-1.5 flex items-center gap-1">
                <AlignLeft className="w-3 h-3" /> Remark
              </p>
              <p className="text-[13px] text-slate-600 dark:text-slate-300">{row.remark}</p>
            </div>
          )}
          <div className="flex gap-2">
            <button
              onClick={() => onEdit(row)}
              className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
                bg-blue-600 text-white hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 transition-colors"
            >
              <Edit2 className="w-4 h-4" />
              Edit
            </button>
            <button
              onClick={() => onDelete(row)}
              className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold
                bg-rose-50 text-rose-700 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20 transition-colors"
            >
              <Trash2 className="w-4 h-4" />
              Delete
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function SupplierCategory() {
  const [categories,    setCategories]    = useState(INITIAL_CATEGORIES)
  const [search,        setSearch]        = useState('')
  const [toast,         setToast]         = useState(null)
  const [loading,       setLoading]       = useState(false)
  const [confirmDelete, setConfirmDelete] = useState(null)  // row to delete
  const [editMode,      setEditMode]      = useState(false)  // true when editing existing
  const [editId,        setEditId]        = useState(null)
  const [formOpen,      setFormOpen]      = useState(false)  // mobile form drawer

  // ── Form state ──────────────────────────────────────────────────────────────
  const [code,   setCode]   = useState('')
  const [name,   setName]   = useState('')
  const [remark, setRemark] = useState('')
  const [errors, setErrors] = useState({})

  const codeRef = useRef(null)

  const showToast = useCallback((msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3500)
  }, [])

  // ── Validate ────────────────────────────────────────────────────────────────
  const validate = useCallback(() => {
    const err = {}
    if (!code.trim())   err.code   = 'Code is required'
    if (!name.trim())   err.name   = 'Name is required'
    if (!remark.trim()) err.remark = 'Remark is required'
    setErrors(err)
    return Object.keys(err).length === 0
  }, [code, name, remark])

  // ── Submit (Add or Edit) ────────────────────────────────────────────────────
  const handleSubmit = useCallback(() => {
    if (!validate()) return
    setLoading(true)

    setTimeout(() => {
      if (editMode) {
        setCategories(prev =>
          prev.map(c =>
            c.supcat_id === editId
              ? { ...c, code: code.toUpperCase(), name: name.toUpperCase(), remark: remark.toUpperCase() }
              : c
          )
        )
        showToast('Category updated successfully.')
      } else {
        const dup = categories.find(c => c.code.toUpperCase() === code.toUpperCase())
        if (dup) {
          setErrors(e => ({ ...e, code: 'This code already exists' }))
          setLoading(false)
          return
        }
        setCategories(prev => [
          ...prev,
          { supcat_id: nextId++, code: code.toUpperCase(), name: name.toUpperCase(), remark: remark.toUpperCase() }
        ])
        showToast('Category added successfully.')
      }
      resetForm()
      setLoading(false)
      setFormOpen(false)
    }, 500)
  }, [validate, editMode, editId, code, name, remark, categories, showToast])

  // ── Edit ────────────────────────────────────────────────────────────────────
  const handleEdit = useCallback((row) => {
    setCode(row.code)
    setName(row.name)
    setRemark(row.remark)
    setEditMode(true)
    setEditId(row.supcat_id)
    setErrors({})
    setFormOpen(true)
    setTimeout(() => codeRef.current?.focus(), 100)
  }, [])

  // ── Delete ──────────────────────────────────────────────────────────────────
  const handleDeleteRequest = useCallback((row) => {
    setConfirmDelete(row)
  }, [])

  const handleDeleteConfirm = useCallback(() => {
    if (!confirmDelete) return
    setCategories(prev => prev.filter(c => c.supcat_id !== confirmDelete.supcat_id))
    showToast(`"${confirmDelete.name}" deleted.`, 'error')
    setConfirmDelete(null)
    if (editId === confirmDelete.supcat_id) resetForm()
  }, [confirmDelete, showToast, editId])

  // ── Reset form ──────────────────────────────────────────────────────────────
  const resetForm = () => {
    setCode(''); setName(''); setRemark('')
    setEditMode(false); setEditId(null); setErrors({})
  }

  // ── Search filter ────────────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    if (!search) return categories
    const q = search.toLowerCase()
    return categories.filter(c =>
      c.code.toLowerCase().includes(q) ||
      c.name.toLowerCase().includes(q) ||
      (c.remark || '').toLowerCase().includes(q)
    )
  }, [categories, search])

  const handleKeydown = (e) => {
    if (e.key === 'Enter') handleSubmit()
  }

  // ─────────────────────────────────────────────────────────────────────────────
  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ─────────────────────────────────────────────────────── */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
        <div>
          <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
            <Package className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
            Supplier Category
          </h1>
          <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
            Define and manage supplier categories with code, name &amp; remarks.
          </p>
        </div>
        {/* Mobile: floating Add button */}
        <button
          type="button"
          onClick={() => { resetForm(); setFormOpen(true) }}
          className="sm:hidden flex items-center gap-2 self-start px-4 py-2 rounded-xl text-[13px] font-semibold
            bg-blue-600 text-white hover:bg-blue-700 shadow-md shadow-blue-500/20 transition-all active:scale-95"
        >
          <Plus className="w-4 h-4" />
          Add Category
        </button>
      </div>

      {/* ── Summary Cards ──────────────────────────────────────────────────── */}
      <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
        <SummaryCard icon={Layers}   label="Total Categories" value={categories.length} color="blue"    />
        <SummaryCard icon={Tag}      label="Showing Records"  value={filtered.length}   color="emerald" />
        <SummaryCard icon={Hash}     label="Active Codes"     value={new Set(categories.map(c => c.code)).size} color="violet" />
      </div>

      {/* ── DESKTOP FORM CARD ──────────────────────────────────────────────── */}
      <div className="hidden sm:block rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          {editMode
            ? <Edit2 className="w-4 h-4 text-amber-600 dark:text-amber-400 flex-shrink-0" />
            : <Plus  className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
          }
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">
            {editMode ? 'Edit Supplier Category' : 'Define Supplier Category'}
          </span>
          {editMode && (
            <button
              onClick={resetForm}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
                bg-slate-100 text-slate-600 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-400 transition-colors"
            >
              <X className="w-3.5 h-3.5" />
              Cancel Edit
            </button>
          )}
        </div>

        <div className="p-5">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {/* Code */}
            <Field label="Code" error={errors.code} required hint="Short unique identifier (e.g. GEN, MED)">
              <div className="relative">
                <Hash className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <Input
                  ref={codeRef}
                  value={code}
                  onChange={e => { setCode(e.target.value.toUpperCase()); setErrors(p => ({ ...p, code: undefined })) }}
                  onKeyDown={handleKeydown}
                  placeholder="e.g. GEN"
                  error={errors.code}
                  className="pl-8 uppercase font-mono tracking-wider"
                  maxLength={10}
                />
              </div>
            </Field>

            {/* Name */}
            <Field label="Name" error={errors.name} required>
              <div className="relative">
                <Tag className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <Input
                  value={name}
                  onChange={e => { setName(e.target.value.toUpperCase()); setErrors(p => ({ ...p, name: undefined })) }}
                  onKeyDown={handleKeydown}
                  placeholder="e.g. GENERAL SUPPLIER"
                  error={errors.name}
                  className="pl-8 uppercase"
                  maxLength={60}
                />
              </div>
            </Field>

            {/* Remark */}
            <Field label="Remark" error={errors.remark} required>
              <div className="relative">
                <AlignLeft className="absolute left-2.5 top-3 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                <Textarea
                  value={remark}
                  onChange={e => { setRemark(e.target.value.toUpperCase()); setErrors(p => ({ ...p, remark: undefined })) }}
                  placeholder="Brief description…"
                  error={errors.remark}
                  className="pl-8 uppercase"
                  rows={2}
                  maxLength={200}
                />
              </div>
            </Field>
          </div>

          {/* Action Buttons */}
          <div className="flex gap-3 mt-4 justify-end">
            {editMode && (
              <button
                type="button"
                onClick={resetForm}
                className="flex items-center gap-2 px-5 py-2 rounded-xl text-[13px] font-semibold
                  bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors"
              >
                <X className="w-4 h-4" />
                Cancel
              </button>
            )}
            <button
              type="button"
              onClick={handleSubmit}
              disabled={loading}
              className="flex items-center gap-2 px-6 py-2 rounded-xl text-[13px] font-semibold text-white
                bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700
                transition-all active:scale-95 disabled:opacity-70"
            >
              {loading
                ? <Loader2 className="w-4 h-4 animate-spin" />
                : editMode ? <Save className="w-4 h-4" /> : <Plus className="w-4 h-4" />
              }
              {editMode ? 'Update Category' : 'Submit'}
            </button>
          </div>
        </div>
      </div>

      {/* ── MOBILE FORM DRAWER ─────────────────────────────────────────────── */}
      {formOpen && (
        <>
          <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm sm:hidden" onClick={() => { resetForm(); setFormOpen(false) }} />
          <div
            className="fixed inset-x-0 bottom-0 z-50 rounded-t-2xl bg-white dark:bg-[#1a1f35]
              border-t border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl sm:hidden"
            style={{ animation: 'drawerUp .25s ease', maxHeight: '92vh', overflowY: 'auto' }}
          >
            <style>{`@keyframes drawerUp{from{transform:translateY(100%)}to{transform:translateY(0)}}`}</style>
            <div className="flex justify-center pt-3 pb-1">
              <div className="w-10 h-1 rounded-full bg-slate-200 dark:bg-slate-700" />
            </div>
            <div className="flex items-center justify-between px-5 py-3 border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
              <div className="flex items-center gap-2">
                {editMode
                  ? <Edit2 className="w-4 h-4 text-amber-600 dark:text-amber-400" />
                  : <Plus  className="w-4 h-4 text-blue-600 dark:text-indigo-400"  />
                }
                <span className="text-[14px] font-bold text-slate-800 dark:text-slate-100">
                  {editMode ? 'Edit Category' : 'Add Category'}
                </span>
              </div>
              <button
                onClick={() => { resetForm(); setFormOpen(false) }}
                className="p-1.5 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="px-5 py-5 space-y-4">
              <Field label="Code" error={errors.code} required hint="Short unique identifier">
                <div className="relative">
                  <Hash className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                  <Input
                    value={code}
                    onChange={e => { setCode(e.target.value.toUpperCase()); setErrors(p => ({ ...p, code: undefined })) }}
                    placeholder="e.g. GEN"
                    error={errors.code}
                    className="pl-8 uppercase font-mono tracking-wider"
                    maxLength={10}
                  />
                </div>
              </Field>

              <Field label="Name" error={errors.name} required>
                <div className="relative">
                  <Tag className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                  <Input
                    value={name}
                    onChange={e => { setName(e.target.value.toUpperCase()); setErrors(p => ({ ...p, name: undefined })) }}
                    placeholder="e.g. GENERAL SUPPLIER"
                    error={errors.name}
                    className="pl-8 uppercase"
                    maxLength={60}
                  />
                </div>
              </Field>

              <Field label="Remark" error={errors.remark} required>
                <div className="relative">
                  <AlignLeft className="absolute left-2.5 top-3 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
                  <Textarea
                    value={remark}
                    onChange={e => { setRemark(e.target.value.toUpperCase()); setErrors(p => ({ ...p, remark: undefined })) }}
                    placeholder="Brief description…"
                    error={errors.remark}
                    className="pl-8 uppercase"
                    maxLength={200}
                  />
                </div>
              </Field>
            </div>

            <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)] flex gap-3">
              <button
                type="button"
                onClick={() => { resetForm(); setFormOpen(false) }}
                className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleSubmit}
                disabled={loading}
                className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white
                  bg-blue-600 hover:bg-blue-700 dark:bg-indigo-600 dark:hover:bg-indigo-700 disabled:opacity-70 transition-all"
              >
                {loading
                  ? <Loader2 className="w-4 h-4 animate-spin" />
                  : editMode ? <Save className="w-4 h-4" /> : <Plus className="w-4 h-4" />
                }
                {editMode ? 'Update' : 'Submit'}
              </button>
            </div>
          </div>
        </>
      )}

      {/* ── RECORD TABLE / LIST SECTION ────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Card Header */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0">
            <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
            <BookOpen className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Supplier Categories</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
              {filtered.length} record{filtered.length !== 1 ? 's' : ''}
            </span>
          </div>

          {/* Search + Reset row */}
          <div className="flex items-center gap-2 w-full sm:w-auto flex-shrink-0">
            <div className="relative flex-1 sm:w-56">
              <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
              <input
                value={search}
                onChange={e => setSearch(e.target.value)}
                placeholder="Search code, name, remark…"
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
            <button
              onClick={() => setSearch('')}
              title="Reset search"
              className="flex items-center justify-center px-3 py-1.5 rounded-lg text-[12px] font-semibold
                bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors"
            >
              <RefreshCw className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>

        {/* Hint bar */}
        <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
          <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
          <p className="text-[12px] text-blue-700 dark:text-blue-400">
            Click <strong>Edit</strong> to modify a category or <strong>Delete</strong> to remove it permanently.
          </p>
        </div>

        {/* ── DESKTOP TABLE ── */}
        <div className="hidden md:block overflow-x-auto">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-16 gap-3 text-slate-400 dark:text-slate-600">
              <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
                <SlidersHorizontal className="w-7 h-7 opacity-40" />
              </div>
              <div className="text-center">
                <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">
                  {search ? 'No records match your search.' : 'No categories defined yet.'}
                </p>
                {!search && (
                  <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
                    Use the form above to add a new supplier category.
                  </p>
                )}
              </div>
            </div>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                  {['S.No.', 'Code', 'Name', 'Remark', 'Action'].map((h, i) => (
                    <th
                      key={i}
                      className="px-4 py-2.5 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap text-left first:text-center first:w-12"
                    >
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.map((row, i) => (
                  <DesktopRow
                    key={row.supcat_id}
                    row={row}
                    idx={i + 1}
                    onEdit={handleEdit}
                    onDelete={handleDeleteRequest}
                  />
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* ── MOBILE CARDS ── */}
        <div className="md:hidden p-4 space-y-3">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-10 gap-3 text-slate-400 dark:text-slate-600">
              <div className="w-14 h-14 rounded-2xl bg-slate-100 dark:bg-slate-800 flex items-center justify-center">
                <SlidersHorizontal className="w-7 h-7 opacity-40" />
              </div>
              <div className="text-center">
                <p className="text-[14px] font-semibold text-slate-500 dark:text-slate-400">
                  {search ? 'No records match your search.' : 'No categories defined yet.'}
                </p>
                {!search && (
                  <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1">
                    Tap <strong>Add Category</strong> above to get started.
                  </p>
                )}
              </div>
            </div>
          ) : (
            <>
              <p className="text-[11px] text-blue-600 dark:text-blue-400 font-medium flex items-center gap-1.5 pb-1">
                <Info className="w-3.5 h-3.5 flex-shrink-0" />
                Tap a card to view details, edit or delete.
              </p>
              {filtered.map((row, i) => (
                <MobileCard
                  key={row.supcat_id}
                  row={row}
                  idx={i + 1}
                  onEdit={handleEdit}
                  onDelete={handleDeleteRequest}
                />
              ))}
            </>
          )}
        </div>

        {/* Table Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{categories.length}</span> records
          </p>
          {search && (
            <button
              onClick={() => setSearch('')}
              className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1"
            >
              <X className="w-3 h-3" /> Clear search
            </button>
          )}
        </div>
      </div>

      {/* ── Confirm Delete Modal ─────────────────────────────────────────────── */}
      <ConfirmModal
        open={!!confirmDelete}
        itemName={confirmDelete?.name}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setConfirmDelete(null)}
      />

      {/* ── Toast ────────────────────────────────────────────────────────────── */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
