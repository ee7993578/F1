import { useEffect, useMemo, useState } from 'react'
import { Download, Loader2, Search, ChevronDown } from 'lucide-react'

export const card = 'rounded-xl border border-slate-200 bg-white shadow-sm dark:border-indigo-500/20 dark:bg-[#171a2b]'
export const input = 'w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-800 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 dark:border-indigo-500/25 dark:bg-[#1e2238] dark:text-slate-100 dark:focus:border-indigo-400'
export const button = 'inline-flex items-center justify-center gap-2 rounded-lg bg-blue-700 px-4 py-2 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-800 disabled:cursor-not-allowed disabled:opacity-50 dark:bg-indigo-600 dark:hover:bg-indigo-500'
export const secondary = 'inline-flex items-center justify-center gap-2 rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-50 disabled:opacity-50 dark:border-slate-700 dark:bg-slate-800 dark:text-slate-200 dark:hover:bg-slate-700'

export function text(value) { return value == null ? '' : String(value) }
export function value(row) { return text(row?.value ?? row?.id ?? row?.key ?? row?.session) }
export function label(row) { return text(row?.label ?? row?.name ?? row?.value ?? row?.session) }

export function Page({ title, description, children, error, message }) {
  return <div className="space-y-4">
    <div><h1 className="text-xl font-bold text-slate-800 dark:text-slate-100">{title}</h1>{description && <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">{description}</p>}</div>
    {error && <div className="rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700 dark:border-rose-900/50 dark:bg-rose-950/30 dark:text-rose-200">{error}</div>}
    {message && <div className="rounded-lg border border-emerald-200 bg-emerald-50 p-3 text-sm text-emerald-700 dark:border-emerald-900/50 dark:bg-emerald-950/30 dark:text-emerald-200">{message}</div>}
    {children}
  </div>
}

export function Busy({ text: caption = 'Loading...' }) {
  return <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-950/30 p-4 backdrop-blur-[2px]" role="status" aria-live="polite"><div className="flex items-center gap-3 rounded-2xl bg-white px-5 py-4 text-sm font-semibold text-slate-700 shadow-xl dark:bg-slate-900 dark:text-slate-100"><Loader2 className="h-5 w-5 animate-spin text-blue-600 dark:text-indigo-400" />{caption}</div></div>
}

export function Field({ title, children, className = '' }) { return <label className={`block min-w-0 ${className}`}><span className="mb-1.5 block text-xs font-semibold text-slate-600 dark:text-slate-300">{title}</span>{children}</label> }

export function Select({ value: selected, onChange, options = [], placeholder, disabled }) {
  return <div className="relative"><select className={`${input} appearance-none pr-9`} value={selected ?? ''} onChange={onChange} disabled={disabled}>{placeholder != null && <option value="">{placeholder}</option>}{options.map((option, index) => <option key={`${value(option)}-${index}`} value={value(option)}>{label(option)}</option>)}</select><ChevronDown className="pointer-events-none absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" /></div>
}

export function CheckList({ options = [], selected = [], onChange, selectAllLabel = 'Select All', searchPlaceholder = 'Search...' }) {
  const [search, setSearch] = useState('')
  const filtered = useMemo(() => options.filter(row => !search || Object.values(row).some(v => text(v).toLowerCase().includes(search.toLowerCase()))), [options, search])
  const all = options.length > 0 && options.every(row => selected.includes(value(row)))
  const toggle = id => onChange(selected.includes(id) ? selected.filter(v => v !== id) : [...selected, id])
  return <div className="rounded-lg border border-slate-200 dark:border-slate-700"><div className="flex flex-col gap-2 border-b border-slate-200 p-2 sm:flex-row sm:items-center sm:justify-between dark:border-slate-700"><label className="flex items-center gap-2 text-sm font-medium text-slate-700 dark:text-slate-200"><input type="checkbox" checked={all} onChange={e => onChange(e.target.checked ? options.map(value) : [])} />{selectAllLabel}</label><div className="relative sm:max-w-xs"><Search className="absolute left-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" /><input className={`${input} py-1.5 pl-8`} placeholder={searchPlaceholder} value={search} onChange={e => setSearch(e.target.value)} /></div></div><div className="grid max-h-56 grid-cols-1 gap-1 overflow-auto p-3 sm:grid-cols-2 lg:grid-cols-3">{filtered.map((row, index) => { const id = value(row); return <label key={`${id}-${index}`} className="flex min-w-0 items-start gap-2 rounded-md px-2 py-1.5 text-xs text-slate-700 hover:bg-slate-50 dark:text-slate-200 dark:hover:bg-slate-800"><input type="checkbox" checked={selected.includes(id)} onChange={() => toggle(id)} /><span className="truncate">{label(row)}</span></label> })}{filtered.length === 0 && <p className="col-span-full p-3 text-center text-xs text-slate-500">No records found.</p>}</div></div>
}

export function TemplateEditor({ templates = [], templateId, onTemplate, message, onMessage }) {
  const selected = templates.find(row => value(row) === templateId) || templates[0]
  const source = text(selected?.message ?? selected?.templateText)
  const parts = source.split('#VAL#')
  const placeholders = Math.max(0, parts.length - 1)
  const values = useMemo(() => {
    const slots = Array.from({ length: placeholders }, (_, i) => '')
    return slots
  }, [templateId, placeholders])
  const [slotValues, setSlotValues] = useState(values)
  useEffect(() => { setSlotValues(values) }, [templateId])
  const composed = parts.reduce((out, part, index) => out + part + (index < parts.length - 1 ? (slotValues[index] || '') : ''), '')
  useEffect(() => { if ((message === '' || message === undefined) && composed) onMessage(composed) }, [composed, message, onMessage])
  return <div className="space-y-3"><div className="space-y-2">{templates.length === 0 && <p className="text-sm text-slate-500">No SMS template configured for this school.</p>}{templates.map(row => <label key={value(row)} className="flex items-start gap-2 rounded-lg border border-slate-200 p-3 text-sm dark:border-slate-700"><input type="radio" name="sms-template" checked={value(row) === templateId} onChange={() => { onTemplate(value(row)); onMessage('') }} /><span className="min-w-0"><span className="block font-semibold text-slate-700 dark:text-slate-200">Template {value(row)}</span><span className="break-words text-xs text-slate-500 dark:text-slate-400">{text(row.message ?? row.templateText)}</span></span></label>)}</div>{selected && <div className="rounded-lg border border-slate-200 p-3 dark:border-slate-700"><p className="mb-2 text-xs font-semibold text-slate-600 dark:text-slate-300">Complete SMS text</p><div className="flex flex-wrap items-center gap-1 text-sm leading-8 text-slate-700 dark:text-slate-200">{parts.map((part, index) => <span key={index} className="contents"><span>{part}</span>{index < parts.length - 1 && <input className="inline-block w-32 rounded border border-blue-300 bg-blue-50 px-2 py-1 text-sm outline-none dark:border-indigo-500 dark:bg-indigo-950/30" value={slotValues[index] || ''} onChange={e => { const next = [...slotValues]; next[index] = e.target.value; setSlotValues(next); onMessage(parts.reduce((out, p, i) => out + p + (i < parts.length - 1 ? (next[i] || '') : ''), '')) }} placeholder="Enter value" />}</span>)}</div><textarea className={`${input} mt-3 min-h-20`} value={message ?? composed} onChange={e => onMessage(e.target.value)} /></div>}</div>
}

export function Table({ rows = [], columns = [], searchPlaceholder = 'Search...', onRowClick }) {
  const [query, setQuery] = useState('')
  const filtered = rows.filter(row => !query || columns.some(column => text(row[column.key]).toLowerCase().includes(query.toLowerCase())))
  return <div className={`${card} overflow-hidden`}><div className="flex flex-col gap-2 border-b border-slate-200 p-3 sm:flex-row sm:items-center sm:justify-between dark:border-slate-700"><div className="relative sm:max-w-xs"><Search className="absolute left-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" /><input className={`${input} pl-8`} placeholder={searchPlaceholder} value={query} onChange={e => setQuery(e.target.value)} /></div><span className="text-xs text-slate-500">{filtered.length} record(s)</span></div><div className="overflow-x-auto"><table className="min-w-full text-left text-sm"><thead className="bg-slate-50 text-xs uppercase text-slate-500 dark:bg-slate-900/60 dark:text-slate-300"><tr>{columns.map(column => <th className="whitespace-nowrap px-3 py-2" key={column.key}>{column.label}</th>)}</tr></thead><tbody>{filtered.map((row, index) => <tr className="border-t border-slate-100 dark:border-slate-700" key={row.id ?? row.registrationNo ?? row.admissionNo ?? index} onClick={() => onRowClick?.(row)}>{columns.map(column => <td className="max-w-[360px] whitespace-nowrap px-3 py-2 text-slate-700 dark:text-slate-200" key={column.key}>{column.render ? column.render(row) : text(row[column.key])}</td>)}</tr>)}{filtered.length === 0 && <tr><td colSpan={columns.length} className="p-8 text-center text-slate-500">No Record Found.</td></tr>}</tbody></table></div></div>
}

export function downloadExcel(filename, columns, rows) { const html = `<table><tr>${columns.map(c => `<th>${c.label}</th>`).join('')}</tr>${rows.map(row => `<tr>${columns.map(c => `<td>${text(row[c.key]).replaceAll('&','&amp;').replaceAll('<','&lt;')}</td>`).join('')}</tr>`).join('')}</table>`; const a = document.createElement('a'); a.href = URL.createObjectURL(new Blob([html], { type: 'application/vnd.ms-excel' })); a.download = filename; a.click(); URL.revokeObjectURL(a.href) }
