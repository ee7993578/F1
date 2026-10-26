import { useEffect, useMemo, useState } from 'react'
import { Check, ChevronDown, ChevronUp, Layers3, Loader2, Save, Search, School, SlidersHorizontal } from 'lucide-react'
import { superAdminFetch } from '../../lib/superAdminApi'

function normalizeGroupName(value) {
  const raw = String(value || '').trim()
  const normalized = raw.toLowerCase().replace(/[^a-z0-9]/g, '')
  if (['transaction', 'transactions', 'work'].includes(normalized)) return 'Work'
  if (['setting', 'settings', 'setup'].includes(normalized)) return 'Setup'
  if (['update', 'updaterecord'].includes(normalized)) return 'Update'
  if (['report', 'reports'].includes(normalized)) return 'Reports'
  return raw || 'Pages'
}

function Field({ label, children }) {
  return <label className="block"><span className="mb-1.5 block text-[11px] font-black uppercase tracking-wide text-slate-500">{label}</span>{children}</label>
}

export default function SuperAdminPageMapping() {
  const [schools, setSchools] = useState([])
  const [modules, setModules] = useState([])
  const [pages, setPages] = useState([])
  const [schoolId, setSchoolId] = useState('')
  const [moduleId, setModuleId] = useState('')
  const [search, setSearch] = useState('')
  const [openGroups, setOpenGroups] = useState({})
  const [loading, setLoading] = useState(true)
  const [moduleLoading, setModuleLoading] = useState(false)
  const [pageLoading, setPageLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    setLoading(true)
    superAdminFetch('/superadmin/page-mapping/bootstrap')
      .then(data => setSchools(data.schools || []))
      .catch(cause => setError(cause.message))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    setModules([]); setPages([]); setModuleId(''); setMessage(''); setError('')
    if (!schoolId) return
    setModuleLoading(true)
    superAdminFetch(`/superadmin/page-mapping/modules?schoolId=${encodeURIComponent(schoolId)}`)
      .then(setModules)
      .catch(cause => setError(cause.message))
      .finally(() => setModuleLoading(false))
  }, [schoolId])

  useEffect(() => {
    setPages([]); setMessage(''); setError('')
    if (!schoolId || !moduleId) return
    setPageLoading(true)
    superAdminFetch(`/superadmin/page-mapping/pages?schoolId=${encodeURIComponent(schoolId)}&moduleId=${encodeURIComponent(moduleId)}`)
      .then(rows => {
        setPages(rows || [])
        const groups = {}
        ;(rows || []).forEach(row => { groups[groupKey(row)] = true })
        setOpenGroups(groups)
      })
      .catch(cause => setError(cause.message))
      .finally(() => setPageLoading(false))
  }, [schoolId, moduleId])

  const filteredPages = useMemo(() => {
    const term = search.trim().toLowerCase()
    if (!term) return pages
    return pages.filter(row => `${row.pageName || ''} ${row.displayName || ''} ${row.parentName || ''}`.toLowerCase().includes(term))
  }, [pages, search])

  const groups = useMemo(() => {
    const grouped = new Map()
    filteredPages.forEach(row => {
      const key = groupKey(row)
      if (!grouped.has(key)) grouped.set(key, { key, label: normalizeGroupName(row.parentName || row.pageType), rows: [] })
      grouped.get(key).rows.push(row)
    })
    return [...grouped.values()]
  }, [filteredPages])

  const selectedCount = pages.filter(row => row.checked).length

  function updatePage(pageId, key, value) {
    setPages(current => current.map(row => Number(row.pageId) === Number(pageId) ? { ...row, [key]: value } : row))
  }

  function togglePage(pageId) {
    setPages(current => current.map(row => {
      if (Number(row.pageId) !== Number(pageId)) return row
      return { ...row, checked: !Boolean(row.checked), displayName: row.displayName || row.pageName }
    }))
  }

  function toggleGroup(group) {
    const shouldSelect = group.rows.some(row => !row.checked)
    const ids = new Set(group.rows.map(row => Number(row.pageId)))
    setPages(current => current.map(row => ids.has(Number(row.pageId)) ? { ...row, checked: shouldSelect, displayName: row.displayName || row.pageName } : row))
  }

  function toggleAll() {
    const shouldSelect = pages.some(row => !row.checked)
    setPages(current => current.map(row => ({ ...row, checked: shouldSelect, displayName: row.displayName || row.pageName })))
  }

  async function save() {
    if (!schoolId || !moduleId) return setError('Please select school and module')
    setSaving(true); setError(''); setMessage('')
    try {
      const result = await superAdminFetch('/superadmin/page-mapping', { method: 'POST', body: JSON.stringify({ schoolId, moduleId: Number(moduleId), pages: pages.map(row => ({ pageId: Number(row.pageId), displayName: row.displayName || row.pageName, priority: Number(row.displayPriority ?? row.priority ?? 0), checked: Boolean(row.checked) })) }) })
      setMessage(`${result.message || 'Page mapping saved'} (${result.selected ?? selectedCount} selected)`)
      const fresh = await superAdminFetch(`/superadmin/page-mapping/pages?schoolId=${encodeURIComponent(schoolId)}&moduleId=${encodeURIComponent(moduleId)}`)
      setPages(fresh || [])
    } catch (cause) { setError(cause.message) } finally { setSaving(false) }
  }

  return <div className="space-y-5 px-4 py-5 sm:px-6 sm:py-7">
    <section className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between"><div><h2 className="flex items-center gap-2 text-lg font-black"><Layers3 size={20} className="text-cyan-700" /> Assign Pages</h2><p className="mt-1 text-xs leading-5 text-slate-500">Select a school and module, then enable the pages that should appear in that school&apos;s new module/submodule menu.</p></div><div className="rounded-xl bg-cyan-50 px-3 py-2 text-xs font-bold text-cyan-800">{selectedCount} selected</div></div>
      <div className="mt-5 grid gap-4 md:grid-cols-2"><Field label="Select school"><div className="relative"><School size={16} className="pointer-events-none absolute left-3 top-3 text-slate-400" /><select value={schoolId} onChange={e => setSchoolId(e.target.value)} disabled={loading} className="w-full appearance-none rounded-xl border border-slate-200 bg-white py-2.5 pl-9 pr-3 text-sm outline-none focus:border-cyan-600 focus:ring-4 focus:ring-cyan-100"><option value="">{loading ? 'Loading schools...' : '--Select--'}</option>{schools.map(school => <option key={school.id} value={school.id}>{school.name} ({school.id})</option>)}</select></div></Field><Field label="Select module"><div className="relative"><Layers3 size={16} className="pointer-events-none absolute left-3 top-3 text-slate-400" /><select value={moduleId} onChange={e => setModuleId(e.target.value)} disabled={!schoolId || moduleLoading} className="w-full appearance-none rounded-xl border border-slate-200 bg-white py-2.5 pl-9 pr-3 text-sm outline-none focus:border-cyan-600 focus:ring-4 focus:ring-cyan-100"><option value="">{moduleLoading ? 'Loading modules...' : '--Select--'}</option>{modules.map(module => <option key={module.id} value={module.id}>{module.name}</option>)}</select></div></Field></div>
    </section>

    {error && <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm font-semibold text-rose-700">{error}</div>}
    {message && <div className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-semibold text-emerald-700">{message}</div>}

    {moduleId && <section className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6"><div className="flex flex-col gap-3 border-b border-slate-100 pb-4 lg:flex-row lg:items-center lg:justify-between"><div><h3 className="font-black">Pages and submodules</h3><p className="mt-1 text-xs text-slate-500">Enable/disable pages, change display name and page order exactly like the old mapping page.</p></div><div className="flex flex-col gap-2 sm:flex-row"><div className="relative"><Search size={15} className="absolute left-3 top-3 text-slate-400" /><input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search page" className="w-full rounded-xl border border-slate-200 py-2.5 pl-9 pr-3 text-sm outline-none focus:border-cyan-600 sm:w-64" /></div><button onClick={toggleAll} className="inline-flex items-center justify-center gap-1.5 rounded-xl border border-slate-200 px-3 py-2 text-xs font-bold text-slate-600 hover:bg-slate-50"><Check size={14} /> Select all</button></div></div>
      {pageLoading && <div className="flex items-center justify-center gap-2 py-12 text-sm text-slate-500"><Loader2 size={18} className="animate-spin text-cyan-700" /> Loading pages...</div>}
      {!pageLoading && !pages.length && <div className="py-12 text-center text-sm text-slate-500">No pages found for this school/module.</div>}
      {!pageLoading && pages.length > 0 && <div className="mt-4 space-y-3">{groups.map(group => { const allChecked = group.rows.length > 0 && group.rows.every(row => row.checked); const open = openGroups[group.key] !== false; return <div key={group.key} className="overflow-hidden rounded-2xl border border-slate-200"><div className="flex flex-wrap items-center justify-between gap-3 bg-slate-50 px-3 py-3 sm:px-4"><button onClick={() => setOpenGroups(current => ({ ...current, [group.key]: !open }))} className="flex min-w-0 items-center gap-2 text-left"><span className="grid h-8 w-8 shrink-0 place-items-center rounded-lg bg-white text-cyan-700 shadow-sm">{open ? <ChevronUp size={16} /> : <ChevronDown size={16} />}</span><span><strong className="block truncate text-sm">{group.label}</strong><small className="text-[11px] text-slate-500">{group.rows.length} page(s)</small></span></button><button onClick={() => toggleGroup(group)} className={`rounded-lg px-2.5 py-1.5 text-[11px] font-bold ${allChecked ? 'bg-cyan-100 text-cyan-800' : 'bg-white text-slate-600'}`}>{allChecked ? 'Unselect all' : 'Select all'}</button></div>{open && <div className="divide-y divide-slate-100">{group.rows.map(row => <div key={`${row.pageId}-${row.newPageId}`} className={`grid gap-3 px-3 py-3 transition sm:grid-cols-[auto_minmax(180px,1fr)_minmax(150px,1fr)_90px] sm:items-center sm:px-4 ${row.checked ? 'bg-cyan-50/40' : 'bg-white'}`}><label className="flex items-center gap-2"><input type="checkbox" checked={Boolean(row.checked)} onChange={() => togglePage(row.pageId)} className="h-4 w-4 accent-cyan-700" /><span className="text-xs font-bold text-slate-700 sm:hidden">Enable page</span></label><div className="min-w-0"><p className="truncate text-sm font-semibold text-slate-800">{row.pageName}</p><p className="truncate text-[11px] text-slate-400">{row.pageUrl || 'Page'} · ID {row.pageId}</p></div><input value={row.displayName ?? ''} onChange={e => updatePage(row.pageId, 'displayName', e.target.value)} aria-label={`Display name for ${row.pageName}`} className="w-full rounded-lg border border-slate-200 px-2.5 py-2 text-sm outline-none focus:border-cyan-600" /><input type="number" min="0" value={row.displayPriority ?? row.priority ?? 0} onChange={e => updatePage(row.pageId, 'displayPriority', e.target.value)} aria-label={`Priority for ${row.pageName}`} className="w-full rounded-lg border border-slate-200 px-2.5 py-2 text-sm outline-none focus:border-cyan-600" /></div>)}</div>}</div> })}</div>}
      <div className="mt-5 flex flex-col justify-between gap-3 border-t border-slate-100 pt-4 sm:flex-row sm:items-center"><p className="flex items-center gap-2 text-xs text-slate-500"><SlidersHorizontal size={14} /> Changes apply to the selected school and module only.</p><button onClick={save} disabled={saving || pageLoading || !pages.length} className="inline-flex items-center justify-center gap-2 rounded-xl bg-cyan-700 px-5 py-3 text-sm font-bold text-white hover:bg-cyan-800 disabled:cursor-not-allowed disabled:opacity-60">{saving ? <Loader2 size={16} className="animate-spin" /> : <Save size={16} />} {saving ? 'Saving...' : 'Save Mapping'}</button></div>
    </section>}
  </div>
}

function groupKey(row) {
  return String(row.parentId || row.subModuleId || row.parentName || row.pageType || 'direct')
}

