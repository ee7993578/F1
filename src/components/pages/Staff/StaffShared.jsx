import { useEffect, useState } from 'react'
import { apiFetch } from '../../../lib/api'

export const input = 'w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-800 outline-none transition placeholder:text-slate-400 focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:bg-slate-50 dark:border-[rgba(99,102,241,0.25)] dark:bg-[#1e2238] dark:text-slate-200 dark:placeholder:text-slate-500 dark:focus:border-indigo-400 dark:focus:ring-indigo-500/20 dark:disabled:bg-slate-800'
export const button = 'inline-flex items-center justify-center rounded-lg px-3 py-2 text-sm font-semibold transition disabled:cursor-not-allowed disabled:opacity-50'
export const primary = `${button} bg-blue-600 text-white hover:bg-blue-700`
export const secondary = `${button} border border-slate-200 bg-white text-slate-700 hover:bg-slate-50 dark:border-[rgba(99,102,241,0.25)] dark:bg-[#1e2238] dark:text-slate-300 dark:hover:bg-slate-700`
export const danger = `${button} bg-rose-600 text-white hover:bg-rose-700 dark:bg-rose-600 dark:hover:bg-rose-500`

export function Page({ title, description, children, message, error, actions }) {
  return <main className="staff-page min-h-full space-y-4 p-3 text-slate-800 dark:text-slate-200 sm:p-5">
    <div className="flex flex-wrap items-start justify-between gap-3">
      <div><h1 className="text-xl font-bold text-slate-800 dark:text-slate-100">{title}</h1>{description && <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">{description}</p>}</div>
      {actions}
    </div>
    {message && <div className="rounded-lg border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm text-emerald-700 dark:border-emerald-500/30 dark:bg-emerald-950/40 dark:text-emerald-300">{message}</div>}
    {error && <div className="rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700 dark:border-rose-500/30 dark:bg-rose-950/40 dark:text-rose-300">{error}</div>}
    {children}
  </main>
}

export function Card({ children, className = '' }) { return <section className={`rounded-xl border border-slate-200 bg-white shadow-sm dark:border-[rgba(99,102,241,0.2)] dark:bg-[#1a1f35] ${className}`}>{children}</section> }
export function Field({ label, required, children, className = '' }) { return <label className={`block space-y-1 ${className}`}><span className="text-xs font-semibold uppercase tracking-wide text-slate-500 dark:text-slate-400">{label}{required && <b className="ml-1 text-rose-500">*</b>}</span>{children}</label> }
export function Select({ value, onChange, options = [], placeholder = '-- Select --', disabled = false }) { return <select className={input} value={value ?? ''} onChange={onChange} disabled={disabled}><option value="">{placeholder}</option>{options.map((o) => <option key={String(o.value ?? o.id)} value={o.value ?? o.id}>{o.label ?? o.name ?? o.text ?? o.value ?? o.id}</option>)}</select> }
export function Button({ children, kind = 'primary', className = '', ...props }) { return <button className={`${kind === 'primary' ? primary : kind === 'danger' ? danger : secondary} ${className}`} {...props}>{children}</button> }
export function Loading({ text = 'Loading...' }) { return <div className="p-8 text-center text-sm text-slate-500 dark:text-slate-400">{text}</div> }
export function Empty({ text = 'No record found.' }) { return <div className="p-8 text-center text-sm text-slate-500 dark:text-slate-400">{text}</div> }

export function useStaffBootstrap(session = '') {
  const [boot, setBoot] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const load = async () => { setLoading(true); setError(''); try { setBoot(await apiFetch(`/staff/bootstrap?light=true${session ? `&session=${encodeURIComponent(session)}` : ''}`)) } catch (e) { setError(e.message) } finally { setLoading(false) } }
  useEffect(() => { load() }, [session])
  return { boot, loading, error, reload: load }
}

export const option = (x) => ({ value: x?.value ?? x?.id ?? '', label: x?.label ?? x?.name ?? x?.text ?? x?.value ?? x?.id ?? '' })
export const options = (xs = []) => xs.map(option)
export const text = (value) => value == null ? '' : String(value)
export const dateOnly = (value) => { if (!value) return ''; const s = String(value); const m = s.match(/(\d{4})[-/](\d{1,2})[-/](\d{1,2})/); if (m) return `${m[1]}-${m[2].padStart(2, '0')}-${m[3].padStart(2, '0')}`; const d = new Date(value); return Number.isNaN(d.getTime()) ? '' : d.toISOString().slice(0, 10) }
export const displayDate = (value) => { const s = dateOnly(value); if (!s) return ''; const [y, m, d] = s.split('-'); return `${d}-${m}-${y}` }

export function exportExcel(filename, rows, columns) {
  const head = columns.map(c => `<th>${String(c.label ?? c.key).replaceAll('<','&lt;')}</th>`).join('')
  const body = rows.map(row => `<tr>${columns.map(c => `<td>${String(row[c.key] ?? '').replaceAll('&','&amp;').replaceAll('<','&lt;')}</td>`).join('')}</tr>`).join('')
  const blob = new Blob([`<html><meta charset="utf-8"><table><thead><tr>${head}</tr></thead><tbody>${body}</tbody></table></html>`], { type: 'application/vnd.ms-excel' })
  const url = URL.createObjectURL(blob); const a = document.createElement('a'); a.href = url; a.download = filename.endsWith('.xls') ? filename : `${filename}.xls`; a.click(); URL.revokeObjectURL(url)
}

export function Table({ columns, rows, actions, mobile = true, searchable = false, searchPlaceholder = 'Search report...' }) {
  const [query, setQuery] = useState('')
  if (!rows?.length) return <Empty />
  const visibleRows = searchable && query.trim()
    ? rows.filter(row => Object.values(row ?? {}).join(' ').toLowerCase().includes(query.trim().toLowerCase()))
    : rows
  return <>
    {searchable && <div className="border-b border-slate-100 p-3 dark:border-[rgba(99,102,241,0.1)]"><input className={`${input} max-w-md`} value={query} onChange={e=>setQuery(e.target.value)} placeholder={searchPlaceholder} aria-label={searchPlaceholder}/><p className="mt-1 text-xs text-slate-400 dark:text-slate-500">Showing {visibleRows.length} of {rows.length} records</p></div>}
    {!visibleRows.length ? <Empty text="No matching record found." /> : <>
      <div className="hidden overflow-auto md:block"><table className="w-full text-left text-sm"><thead className="bg-slate-50 text-xs uppercase text-slate-500 dark:bg-white/[0.03] dark:text-slate-400"><tr>{columns.map(c => <th key={c.key} className="whitespace-nowrap px-3 py-2">{c.label}</th>)}{actions && <th className="px-3 py-2">Action</th>}</tr></thead><tbody>{visibleRows.map((r, i) => <tr key={r.id ?? r.f_id ?? r.RowID ?? i} className="border-t border-slate-100 align-top hover:bg-slate-50 dark:border-[rgba(99,102,241,0.1)] dark:hover:bg-white/[0.02]">{columns.map(c => <td key={c.key} className="whitespace-nowrap px-3 py-2 text-slate-700 dark:text-slate-300">{c.render ? c.render(r, i) : text(r[c.key])}</td>)}{actions && <td className="px-3 py-2">{actions(r, i)}</td>}</tr>)}</tbody></table></div>
      {mobile && <div className="space-y-2 p-2 md:hidden">{visibleRows.map((r, i) => <div key={r.id ?? r.f_id ?? r.RowID ?? i} className="rounded-lg border border-slate-200 p-3 dark:border-[rgba(99,102,241,0.15)]">{columns.map(c => <div key={c.key} className="flex justify-between gap-3 border-b border-slate-100 py-1.5 last:border-0 dark:border-[rgba(99,102,241,0.08)]"><span className="text-xs font-semibold text-slate-500 dark:text-slate-400">{c.label}</span><span className="max-w-[62%] text-right text-sm text-slate-800 dark:text-slate-200">{c.render ? c.render(r, i) : text(r[c.key])}</span></div>)}{actions && <div className="mt-2">{actions(r, i)}</div>}</div>)}</div>}
    </>}
  </>
}
