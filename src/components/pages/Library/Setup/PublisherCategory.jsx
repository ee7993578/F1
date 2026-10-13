/**
 * PublisherCategory.jsx
 * Folder: src/pages/Library/PublisherCategory.jsx
 *
 * Converts legacy ASPX "Publisher Category" master page to React + Tailwind.
 *
 * Fields: Code, Name, Remark
 * Features:
 *  - Add / Edit form with validation
 *  - List of categories (desktop table / mobile cards)
 *  - Edit (loads into form) + Delete (with confirm) actions
 *  - Search box for quick filtering
 *  - Toast notifications, loading + empty states
 */

import { useState, useMemo, useCallback } from 'react'
import {
  Library, Tag, FileText, Plus, RefreshCw, Search,
  Pencil, Trash2, X, Check, AlertCircle, Loader2,
  Info, ChevronRight, ListChecks, Save
} from 'lucide-react'

// ─── STATIC / DUMMY DATA ────────────────────────────────────────────────────
const INITIAL_CATEGORIES = [
  { id: 1, code: 'NCERT', name: 'NCERT Publications', remark: 'National Council of Educational Research and Training' },
  { id: 2, code: 'OUP',   name: 'Oxford University Press', remark: 'Reference & language books' },
  { id: 3, code: 'SCHND', name: 'S. Chand Publishing', remark: 'School textbooks vendor' },
  { id: 4, code: 'ARH',   name: 'Arihant Publications', remark: 'Competitive exam guides' },
  { id: 5, code: 'PEN',   name: 'Penguin Random House', remark: 'Fiction & general reading' },
]

// ─── PRIMITIVE COMPONENTS ───────────────────────────────────────────────────

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

function TextInput({ value, onChange, error, placeholder, multiline, uppercase = true }) {
  const base = `w-full px-3 py-2 text-[13px] rounded-lg border outline-none transition-all
    bg-white text-slate-800 placeholder-slate-300
    focus:border-blue-400 focus:ring-2 focus:ring-blue-100
    dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder-slate-600
    dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20
    ${uppercase ? 'uppercase placeholder:normal-case' : ''}
    ${error
      ? 'border-rose-400 ring-2 ring-rose-100'
      : 'border-slate-200 dark:border-[rgba(99,102,241,0.25)]'
    }`

  if (multiline) {
    return (
      <textarea
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        rows={1}
        className={`${base} resize-none`}
      />
    )
  }

  return (
    <input
      type="text"
      value={value}
      onChange={onChange}
      placeholder={placeholder}
      className={base}
    />
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

// ─── CONFIRM DELETE MODAL ────────────────────────────────────────────────────
function ConfirmDeleteModal({ open, category, onCancel, onConfirm, loading }) {
  if (!open) return null
  return (
    <>
      <div className="fixed inset-0 bg-black/40 z-40 backdrop-blur-sm" onClick={onCancel} />
      <div className="fixed inset-0 z-50 flex items-center justify-center px-4">
        <div
          className="w-full max-w-sm rounded-2xl bg-white dark:bg-[#1a1f35] border border-slate-200 dark:border-[rgba(99,102,241,0.2)] shadow-2xl p-5"
          style={{ animation: 'popIn .2s ease' }}
        >
          <style>{`@keyframes popIn{from{opacity:0;transform:scale(.96)}to{opacity:1;transform:scale(1)}}`}</style>
          <div className="flex items-center gap-3 mb-3">
            <span className="w-10 h-10 rounded-xl bg-rose-50 dark:bg-rose-500/10 text-rose-600 dark:text-rose-400 flex items-center justify-center flex-shrink-0">
              <Trash2 className="w-5 h-5" />
            </span>
            <div>
              <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100">Delete Category</p>
              <p className="text-[12px] text-slate-400 dark:text-slate-500">This action cannot be undone.</p>
            </div>
          </div>
          <p className="text-[13px] text-slate-600 dark:text-slate-300 mb-5">
            Are you sure you want to delete{' '}
            <span className="font-bold text-slate-800 dark:text-slate-100">{category?.name}</span>{' '}
            (<span className="font-semibold">{category?.code}</span>)?
          </p>
          <div className="flex gap-3">
            <button onClick={onCancel}
              className="flex-1 py-2.5 rounded-xl text-[13px] font-semibold bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 transition-colors">
              Cancel
            </button>
            <button onClick={onConfirm} disabled={loading}
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

// ─── DESKTOP TABLE ROW ────────────────────────────────────────────────────────
function DesktopRow({ row, idx, onEdit, onDelete, isEditing }) {
  return (
    <tr className={`border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] transition-colors
      ${isEditing
        ? 'bg-blue-50/70 dark:bg-indigo-500/[0.08]'
        : 'hover:bg-slate-50/60 dark:hover:bg-white/[0.02]'}`}>
      <td className="px-4 py-3 text-center text-[12px] text-slate-400 dark:text-slate-500 tabular-nums w-12">{idx}</td>
      <td className="px-4 py-3">
        <span className="inline-flex items-center justify-center px-2.5 py-1 rounded-lg text-[12px] font-bold bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400 tracking-wide">
          {row.code}
        </span>
      </td>
      <td className="px-4 py-3 text-[13px] font-semibold text-slate-700 dark:text-slate-200">{row.name}</td>
      <td className="px-4 py-3 text-[12px] text-slate-500 dark:text-slate-400">{row.remark || '—'}</td>
      <td className="px-4 py-3">
        <div className="flex items-center justify-center gap-2">
          <button onClick={() => onEdit(row)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
              bg-amber-50 text-amber-700 hover:bg-amber-100 dark:bg-amber-500/10 dark:text-amber-400 dark:hover:bg-amber-500/20 transition-colors">
            <Pencil className="w-3.5 h-3.5" /> Edit
          </button>
          <button onClick={() => onDelete(row)}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[12px] font-semibold
              bg-rose-50 text-rose-700 hover:bg-rose-100 dark:bg-rose-500/10 dark:text-rose-400 dark:hover:bg-rose-500/20 transition-colors">
            <Trash2 className="w-3.5 h-3.5" /> Delete
          </button>
        </div>
      </td>
    </tr>
  )
}

// ─── MOBILE CARD ──────────────────────────────────────────────────────────────
function MobileCard({ row, idx, onEdit, onDelete, isEditing }) {
  return (
    <div className={`rounded-xl border overflow-hidden shadow-sm transition-colors
      ${isEditing
        ? 'border-blue-300 dark:border-indigo-500/40 bg-blue-50/60 dark:bg-indigo-500/[0.08]'
        : 'border-slate-200 dark:border-[rgba(99,102,241,0.15)] bg-white dark:bg-[#1a1f35]'}`}>
      <div className="flex items-start gap-3 px-4 py-3.5">
        <span className="w-9 h-9 rounded-xl flex-shrink-0 flex items-center justify-center text-[10px] font-bold bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400">
          {idx}
        </span>
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 flex-wrap">
            <p className="text-[14px] font-bold text-slate-800 dark:text-slate-100 leading-tight">{row.name}</p>
            <span className="inline-flex items-center px-2 py-0.5 rounded-md text-[11px] font-bold bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-400 tracking-wide">
              {row.code}
            </span>
          </div>
          <p className="text-[12px] text-slate-400 dark:text-slate-500 mt-1 leading-snug">
            {row.remark || 'No remark'}
          </p>
        </div>
      </div>
      <div className="flex border-t border-slate-100 dark:border-[rgba(99,102,241,0.1)]">
        <button onClick={() => onEdit(row)}
          className="flex-1 flex items-center justify-center gap-1.5 py-2.5 text-[12px] font-semibold
            text-amber-700 dark:text-amber-400 hover:bg-amber-50 dark:hover:bg-amber-500/10 transition-colors">
          <Pencil className="w-3.5 h-3.5" /> Edit
        </button>
        <div className="w-px bg-slate-100 dark:bg-[rgba(99,102,241,0.1)]" />
        <button onClick={() => onDelete(row)}
          className="flex-1 flex items-center justify-center gap-1.5 py-2.5 text-[12px] font-semibold
            text-rose-700 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-500/10 transition-colors">
          <Trash2 className="w-3.5 h-3.5" /> Delete
        </button>
      </div>
    </div>
  )
}

// ─── MAIN COMPONENT ───────────────────────────────────────────────────────────
export default function PublisherCategory() {
  const [categories, setCategories] = useState(INITIAL_CATEGORIES)
  const [form, setForm]             = useState({ code: '', name: '', remark: '' })
  const [errors, setErrors]         = useState({})
  const [editingId, setEditingId]   = useState(null)
  const [search, setSearch]         = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [toast, setToast]           = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleting, setDeleting]     = useState(false)

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3000)
  }

  // ── Validation ─────────────────────────────────────────────────────────
  const validate = () => {
    const err = {}
    if (!form.code.trim()) err.code = 'Code is required'
    if (!form.name.trim()) err.name = 'Name is required'
    return err
  }

  // ── Submit (Add / Update) ─────────────────────────────────────────────
  const handleSubmit = useCallback(() => {
    const err = validate()
    if (Object.keys(err).length) { setErrors(err); return }
    setErrors({})
    setSubmitting(true)

    setTimeout(() => {
      const payload = {
        code: form.code.trim().toUpperCase(),
        name: form.name.trim().toUpperCase(),
        remark: form.remark.trim().toUpperCase(),
      }

      if (editingId) {
        // TODO: API call -> PUT /api/library/publisher-category/{editingId}
        setCategories(prev => prev.map(c => c.id === editingId ? { ...c, ...payload } : c))
        showToast(`Category "${payload.name}" updated successfully.`)
      } else {
        // TODO: API call -> POST /api/library/publisher-category
        const newId = Math.max(0, ...categories.map(c => c.id)) + 1
        setCategories(prev => [...prev, { id: newId, ...payload }])
        showToast(`Category "${payload.name}" added successfully.`)
      }

      setForm({ code: '', name: '', remark: '' })
      setEditingId(null)
      setSubmitting(false)
    }, 500)
  }, [form, editingId, categories])

  // ── Reset ──────────────────────────────────────────────────────────────
  const handleReset = () => {
    setForm({ code: '', name: '', remark: '' })
    setErrors({})
    setEditingId(null)
  }

  // ── Edit ───────────────────────────────────────────────────────────────
  const handleEdit = (row) => {
    setForm({ code: row.code, name: row.name, remark: row.remark })
    setEditingId(row.id)
    setErrors({})
    // Scroll form into view on mobile
    document.getElementById('pubcat-form')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }

  // ── Delete ─────────────────────────────────────────────────────────────
  const handleDeleteConfirm = () => {
    setDeleting(true)
    setTimeout(() => {
      // TODO: API call -> DELETE /api/library/publisher-category/{deleteTarget.id}
      setCategories(prev => prev.filter(c => c.id !== deleteTarget.id))
      if (editingId === deleteTarget.id) handleReset()
      showToast(`Category "${deleteTarget.name}" deleted.`)
      setDeleting(false)
      setDeleteTarget(null)
    }, 500)
  }

  // ── Search filter ──────────────────────────────────────────────────────
  const filtered = useMemo(() => {
    if (!search) return categories
    const q = search.toLowerCase()
    return categories.filter(c =>
      c.code.toLowerCase().includes(q) ||
      c.name.toLowerCase().includes(q) ||
      (c.remark || '').toLowerCase().includes(q)
    )
  }, [categories, search])

  return (
    <div className="space-y-4 pb-10">

      {/* ── Page Title ─────────────────────────────────────────────────── */}
      <div>
        <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100 flex items-center gap-2">
          <Library className="w-5 h-5 text-blue-600 dark:text-indigo-400" />
          Publisher Category
        </h1>
        <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">
          Manage publisher categories used across the library module.
        </p>
      </div>

      {/* ── Form Card ──────────────────────────────────────────────────── */}
      <div id="pubcat-form" className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="flex items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
          {editingId ? <Pencil className="w-4 h-4 text-amber-600 dark:text-amber-400 flex-shrink-0" /> : <Plus className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />}
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200 flex-1">
            {editingId ? 'Edit Category' : 'Add New Category'}
          </span>
          {editingId && (
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 dark:bg-amber-500/15 dark:text-amber-400">
              Editing
            </span>
          )}
        </div>

        <div className="p-5">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 items-end">
            <Field label="Code" error={errors.code} required>
              <TextInput
                value={form.code}
                onChange={e => { setForm(p => ({ ...p, code: e.target.value })); setErrors(p => ({ ...p, code: undefined })) }}
                placeholder="e.g. NCERT"
              />
            </Field>

            <Field label="Name" error={errors.name} required>
              <TextInput
                value={form.name}
                onChange={e => { setForm(p => ({ ...p, name: e.target.value })); setErrors(p => ({ ...p, name: undefined })) }}
                placeholder="e.g. NCERT Publications"
              />
            </Field>

            <div className="sm:col-span-2 lg:col-span-1">
              <Field label="Remark">
                <TextInput
                  value={form.remark}
                  onChange={e => setForm(p => ({ ...p, remark: e.target.value }))}
                  placeholder="Optional remark"
                  multiline
                  uppercase={false}
                />
              </Field>
            </div>

            <div className="flex gap-2 sm:col-span-2 lg:col-span-1">
              <button type="button" onClick={handleSubmit} disabled={submitting}
                className="flex-1 flex items-center justify-center gap-2 py-2.5 rounded-xl text-[13px] font-semibold text-white
                  bg-blue-600 hover:bg-blue-700 shadow-md shadow-blue-500/20 dark:bg-indigo-600 dark:hover:bg-indigo-700
                  transition-all active:scale-95 disabled:opacity-70">
                {submitting
                  ? <Loader2 className="w-4 h-4 animate-spin" />
                  : editingId ? <Save className="w-4 h-4" /> : <Plus className="w-4 h-4" />}
                {editingId ? 'Update' : 'Submit'}
              </button>
              <button type="button" onClick={handleReset}
                className="flex items-center justify-center px-3 py-2.5 rounded-xl text-[13px] font-semibold
                  bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-300 dark:hover:bg-slate-700 transition-colors">
                <RefreshCw className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* ── List Card ──────────────────────────────────────────────────── */}
      <div className="rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">

        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02]">
          <div className="flex items-center gap-3 flex-1 min-w-0 flex-wrap">
            <span className="w-1 h-5 rounded-full bg-blue-500 flex-shrink-0" />
            <ListChecks className="w-4 h-4 text-blue-600 dark:text-blue-400 flex-shrink-0" />
            <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Categories</span>
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400 flex-shrink-0">
              {filtered.length} record{filtered.length !== 1 ? 's' : ''}
            </span>
          </div>

          {/* Search */}
          <div className="relative w-full sm:w-60 flex-shrink-0">
            <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400 pointer-events-none" />
            <input
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Search code, name, remark…"
              className="w-full pl-8 pr-7 py-2 text-[12px] rounded-lg border outline-none transition-all
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

        {/* Info hint - desktop only */}
        <div className="hidden sm:flex items-center gap-2 px-5 py-2 border-b border-slate-100 dark:border-[rgba(99,102,241,0.07)] bg-blue-50/20 dark:bg-blue-500/[0.03]">
          <Info className="w-3.5 h-3.5 text-blue-500 flex-shrink-0" />
          <p className="text-[12px] text-blue-700 dark:text-blue-400">
            Click Edit to load a category into the form above, or Delete to remove it permanently.
          </p>
        </div>

        {/* ── DESKTOP TABLE ── */}
        <div className="hidden md:block overflow-x-auto">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-32 gap-2 text-slate-400 dark:text-slate-600">
              <Search className="w-6 h-6 opacity-40" />
              <span className="text-[13px]">
                {categories.length === 0 ? 'No categories added yet.' : 'No records match your search.'}
              </span>
            </div>
          ) : (
            <table className="w-full">
              <thead>
                <tr className="border-b border-slate-100 dark:border-[rgba(99,102,241,0.1)] bg-slate-50/50 dark:bg-white/[0.02]">
                  {['S.No.', 'Code', 'Name', 'Remark', 'Action'].map((h, i) => (
                    <th key={i} className={`px-4 py-2.5 text-[11px] font-bold uppercase tracking-wide text-slate-500 dark:text-slate-400 whitespace-nowrap
                      ${i === 0 || i === 4 ? 'text-center' : 'text-left'} ${i === 0 ? 'w-12' : ''}`}>
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
                    onEdit={handleEdit}
                    onDelete={setDeleteTarget}
                    isEditing={editingId === row.id}
                  />
                ))}
              </tbody>
            </table>
          )}
        </div>

        {/* ── MOBILE CARDS ── */}
        <div className="md:hidden p-4 space-y-3">
          {filtered.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-32 gap-2 text-slate-400 dark:text-slate-600">
              <Search className="w-6 h-6 opacity-40" />
              <span className="text-[13px] text-center">
                {categories.length === 0 ? 'No categories added yet.' : 'No records match your search.'}
              </span>
            </div>
          ) : (
            filtered.map((row, i) => (
              <MobileCard
                key={row.id}
                row={row}
                idx={i + 1}
                onEdit={handleEdit}
                onDelete={setDeleteTarget}
                isEditing={editingId === row.id}
              />
            ))
          )}
        </div>

        {/* Footer */}
        <div className="flex items-center justify-between gap-3 px-5 py-3.5 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/50 dark:bg-white/[0.015]">
          <p className="text-[12px] text-slate-400 dark:text-slate-500">
            Showing <span className="font-semibold text-slate-700 dark:text-slate-300">{filtered.length}</span> of{' '}
            <span className="font-semibold text-slate-700 dark:text-slate-300">{categories.length}</span> categories
          </p>
          {search && (
            <button onClick={() => setSearch('')}
              className="text-[12px] text-blue-600 dark:text-blue-400 hover:underline flex items-center gap-1">
              <X className="w-3 h-3" /> Clear search
            </button>
          )}
        </div>
      </div>

      {/* Confirm Delete */}
      <ConfirmDeleteModal
        open={!!deleteTarget}
        category={deleteTarget}
        onCancel={() => setDeleteTarget(null)}
        onConfirm={handleDeleteConfirm}
        loading={deleting}
      />

      {/* Toast */}
      {toast && <Toast message={toast.msg} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  )
}
