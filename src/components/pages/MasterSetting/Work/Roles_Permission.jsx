import { useEffect, useMemo, useState } from 'react'
import { apiFetch } from '../../../../lib/api'
import { useMasterSettings } from '../../../../hooks/useMasterSettings'

const input = 'rounded border border-slate-300 bg-white px-3 py-2 text-sm outline-none focus:border-emerald-500 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-100'

function Notice({ value, error }) {
  if (!value) return null
  return <div className={`mb-4 rounded border px-3 py-2 text-sm ${error ? 'border-red-200 bg-red-50 text-red-700' : 'border-green-200 bg-green-50 text-green-700'}`}>{value}</div>
}

function PageGroup({ group, selected, disabled, onToggle, onSelectAll }) {
  const active = group.pages.filter(page => selected.has(String(page.pageId))).length
  const all = group.pages.length > 0 && active === group.pages.length
  return <div className="mb-3 rounded border dark:border-slate-700">
    <div className="flex items-center justify-between border-b bg-slate-50 px-3 py-2 dark:border-slate-700 dark:bg-slate-800"><span className="text-sm font-semibold text-slate-700 dark:text-slate-200">{group.name}</span><label className="flex items-center gap-2 text-xs text-slate-600 dark:text-slate-300"><span>Select All:</span><input type="checkbox" checked={all} disabled={disabled} onChange={e => onSelectAll(group.pages, e.target.checked)} /></label></div>
    <div className="grid gap-x-5 gap-y-2 p-3 sm:grid-cols-2 lg:grid-cols-4">{group.pages.map(page => <label key={page.pageId} className={`flex items-center gap-2 text-sm ${disabled || page.inherited ? 'text-slate-400' : 'text-slate-700 dark:text-slate-300'}`} title={page.inherited ? 'Already assigned through staff designation' : ''}><input type="checkbox" checked={selected.has(String(page.pageId))} disabled={disabled || page.inherited} onChange={() => onToggle(page.pageId)} />{page.pageName}</label>)}</div>
  </div>
}

export default function RolesPermission() {
  const { data, loading } = useMasterSettings()
  const staffId = new URLSearchParams(window.location.search).get('f_id') || ''
  const staffMode = Boolean(staffId)
  const [roleId, setRoleId] = useState('')
  const [moduleId, setModuleId] = useState('')
  const [selectedClasses, setSelectedClasses] = useState([])
  const [selectedPages, setSelectedPages] = useState(new Set())
  const [pageRows, setPageRows] = useState([])
  const [moduleEnabled, setModuleEnabled] = useState(true)
  const [mobileMode, setMobileMode] = useState(false)
  const [mobilePages, setMobilePages] = useState([])
  const [mobileSelected, setMobileSelected] = useState(new Set())
  const [mobileType, setMobileType] = useState('Faculty')
  const [message, setMessage] = useState('')
  const [error, setError] = useState(false)
  const [saving, setSaving] = useState(false)

  const roles = data?.designations || []
  const staff = data?.staff || []
  const classes = data?.classes || []
  const modules = useMemo(() => (data?.modules || []).filter(module => module.mapped === true), [data?.modules])
  const pages = useMemo(() => {
    const source = data?.pages || []
    const names = new Map(source.map(page => [String(page.newPageId ?? page.pageId), page.displayName || page.pageName]))
    const seen = new Set()
    return source.filter(page => page.mapped === true).reduce((out, page) => {
      const pageId = page.newPageId ?? page.pageId
      const key = `${page.moduleId}:${pageId}`
      if (seen.has(key)) return out
      seen.add(key)
      out.push({ ...page, pageId, parentName: names.get(String(page.parentId || '')) || 'Pages' })
      return out
    }, [])
  }, [data?.pages])
  const currentStaff = staff.find(item => String(item.facultyId) === String(staffId))
  const currentModule = modules.find(item => String(item.moduleId) === String(moduleId))

  const modulePages = useMemo(() => {
    const permissions = new Map(pageRows.map(row => [String(row.pageId), row]))
    return pages.filter(page => String(page.moduleId) === String(moduleId)).map(page => ({
      pageId: page.pageId,
      pageName: page.displayName || page.pageName,
      parentId: page.parentId,
      parentName: page.parentName,
      inherited: permissions.get(String(page.pageId))?.inherited === true,
    }))
  }, [pages, moduleId, pageRows])
  const pageGroups = useMemo(() => groupPages(modulePages), [modulePages])

  const loadRole = async (id) => {
    setSelectedPages(new Set()); setSelectedClasses([]); setMessage(''); setError(false)
    if (!id) { setPageRows([]); return }
    try {
      const rows = await apiFetch(`/master-settings/permissions/${id}`)
      setPageRows(rows || [])
      setSelectedPages(new Set((rows || []).filter(row => row.assigned).map(row => String(row.pageId))))
      const classRow = (rows || []).find(row => row.assigned && row.classIds)
      setSelectedClasses(classRow?.classIds ? String(classRow.classIds).split(',').filter(Boolean) : [])
    } catch (e) { setError(true); setMessage(e.message) }
  }
  const loadStaffModule = async (id) => {
    setModuleId(id); setSelectedPages(new Set()); setPageRows([]); setMessage(''); setError(false)
    if (!id) return
    try {
      const rows = await apiFetch(`/master-settings/permissions/staff/${staffId}?moduleId=${id}`)
      setPageRows(rows || [])
      setSelectedPages(new Set((rows || []).filter(row => row.assigned || row.inherited).map(row => String(row.pageId))))
      setModuleEnabled((rows || []).some(row => row.assigned || row.inherited))
    } catch (e) { setError(true); setMessage(e.message) }
  }
  useEffect(() => { if (!staffMode) loadRole(roleId) }, [roleId, staffMode])
  useEffect(() => { if (staffMode && moduleId) loadStaffModule(moduleId) }, [staffMode])

  const isSelected = id => selectedPages.has(String(id))
  const togglePage = id => setSelectedPages(old => { const next = new Set(old); const key = String(id); next.has(key) ? next.delete(key) : next.add(key); return next })
  const toggleGroup = (items, value) => setSelectedPages(old => { const next = new Set(old); items.forEach(item => value ? next.add(String(item.pageId)) : next.delete(String(item.pageId))); return next })
  const toggleClass = id => setSelectedClasses(old => old.includes(String(id)) ? old.filter(item => item !== String(id)) : [...old, String(id)])
  const allClasses = classes.length > 0 && selectedClasses.length === classes.length
  const toggleAllClasses = () => setSelectedClasses(allClasses ? [] : classes.map(item => String(item.classId)))

  const roleRows = () => modules.flatMap(mod => pages.filter(page => String(page.moduleId) === String(mod.moduleId)).map(page => ({ pageId: Number(page.pageId), moduleId: Number(mod.moduleId), view: selectedPages.has(String(page.pageId)), add: selectedPages.has(String(page.pageId)), update: selectedPages.has(String(page.pageId)), delete: selectedPages.has(String(page.pageId)), classIds: selectedClasses.join(',') })))
  const saveRole = async () => {
    if (!roleId) { setError(true); setMessage('Please Select Role First'); return }
    setSaving(true); setError(false); setMessage('')
    try { await apiFetch('/master-settings/permissions', { method: 'PUT', body: JSON.stringify({ designationId: Number(roleId), permissions: roleRows() }) }); setMessage('Roles Assigned') }
    catch (e) { setError(true); setMessage(e.message) }
    finally { setSaving(false) }
  }
  const saveStaff = async () => {
    if (!moduleId) { setError(true); setMessage('Please Select Module First'); return }
    setSaving(true); setError(false); setMessage('')
    try { await apiFetch('/master-settings/permissions/staff', { method: 'PUT', body: JSON.stringify({ facultyId: Number(staffId), moduleId: Number(moduleId), pageIds: [...selectedPages].map(Number), classIds: '' }) }); await loadStaffModule(moduleId); setMessage('Roles Assigned') }
    catch (e) { setError(true); setMessage(e.message) }
    finally { setSaving(false) }
  }

  const loadMobile = async () => {
    if (!roleId) { setError(true); setMessage('Please Select Role First'); return }
    try { const rows = await apiFetch(`/master-settings/mobile-permissions/${roleId}`); setMobilePages(rows || []); setMobileType(rows?.[0]?.userType || 'Faculty'); setMobileSelected(new Set((rows || []).filter(row => row.assigned).map(row => String(row.pageId))) ) }
    catch (e) { setError(true); setMessage(e.message) }
  }
  const saveMobile = async () => {
    if (!roleId) { setError(true); setMessage('Please Select Role First'); return }
    setSaving(true); setError(false); setMessage('')
    try { await apiFetch('/master-settings/mobile-permissions', { method: 'PUT', body: JSON.stringify({ designationId: Number(roleId), pageIds: [...mobileSelected].map(Number), menuType: 'SideMenu' }) }); setMessage('Mobile App Pages Assigned') }
    catch (e) { setError(true); setMessage(e.message) }
    finally { setSaving(false) }
  }

  return <div className="space-y-5 pb-8">
    <div><h1 className="text-xl font-semibold text-slate-800 dark:text-slate-100">Assign Roles</h1></div>
    <Notice value={message} error={error} />
    <section className="rounded bg-white p-5 shadow-sm dark:bg-slate-900">
      <div className="mb-4 flex flex-wrap items-end gap-5">
        {!staffMode && <label className="w-full max-w-sm text-sm font-semibold text-slate-700 dark:text-slate-200">Role<select className={`${input} mt-1 block w-full`} value={roleId} onChange={e => setRoleId(e.target.value)}><option value="">Select</option>{roles.map(role => <option key={role.id} value={role.id}>{role.name || role.displayName}</option>)}</select></label>}
        {staffMode && <label className="w-full max-w-sm text-sm font-semibold text-slate-700 dark:text-slate-200">Staff<select className={`${input} mt-1 block w-full`} disabled value={staffId}><option value={staffId}>{currentStaff?.name || staffId}</option></select></label>}
        {staffMode && <label className="w-full max-w-sm text-sm font-semibold text-slate-700 dark:text-slate-200">Module<select className={`${input} mt-1 block w-full`} value={moduleId} onChange={e => loadStaffModule(e.target.value)}><option value="">Select</option>{modules.map(mod => <option key={mod.moduleId} value={mod.moduleId}>{mod.moduleName}</option>)}</select></label>}
        <fieldset className="text-sm font-semibold text-slate-700 dark:text-slate-200"><legend>Page Type</legend><label className="mr-4 mt-2 inline-flex items-center gap-2 font-normal"><input type="radio" checked={!mobileMode} onChange={() => setMobileMode(false)} /> ERP Pages</label><label className="inline-flex items-center gap-2 font-normal"><input type="radio" checked={mobileMode} onChange={() => { setMobileMode(true); loadMobile() }} /> Mobile App Pages</label></fieldset>
      </div>
      {!staffMode && !mobileMode && roleId && <div><div className="mb-2 flex items-center justify-between text-sm font-semibold text-slate-700 dark:text-slate-200"><span>Select Class</span><label className="flex items-center gap-2 font-normal"><input type="checkbox" checked={allClasses} onChange={toggleAllClasses} /> All</label></div><div className="mb-5 grid gap-2 sm:grid-cols-4 lg:grid-cols-9">{classes.map(item => <label key={item.classId} className="flex items-center gap-2 text-sm text-slate-700 dark:text-slate-300"><input type="checkbox" checked={selectedClasses.includes(String(item.classId))} onChange={() => toggleClass(item.classId)} />{item.className}</label>)}</div></div>}
    </section>
    {!mobileMode && ((staffMode && moduleId) || (!staffMode && roleId)) && <section className="rounded bg-white p-5 shadow-sm dark:bg-slate-900"><div className="mb-4 flex items-center justify-between"><h2 className="font-semibold text-slate-800 dark:text-slate-100">Pages {currentModule?.moduleName ? `- ${currentModule.moduleName}` : ''}</h2>{staffMode && <label className="flex items-center gap-2 text-sm text-slate-700 dark:text-slate-300"><input type="checkbox" checked={moduleEnabled} onChange={e => setModuleEnabled(e.target.checked)} /> Module</label>}</div>{loading && <p className="text-sm text-slate-500">Loading...</p>}{!loading && staffMode && <>{pageGroups.map(group => <PageGroup key={group.name} group={group} selected={selectedPages} disabled={!moduleEnabled} onToggle={togglePage} onSelectAll={toggleGroup} />)}<button className="mt-2 rounded bg-emerald-600 px-5 py-2 text-sm font-semibold text-white disabled:opacity-50" disabled={saving} onClick={saveStaff}>{saving ? 'Saving...' : 'Submit'}</button></>}{!loading && !staffMode && modules.map(mod => <RoleModule key={mod.moduleId} module={mod} pages={pages} selected={selectedPages} onToggle={togglePage} onSelectAll={toggleGroup} onSubmit={saveRole} saving={saving} />)}</section>}
    {mobileMode && !staffMode && <section className="rounded bg-white p-5 shadow-sm dark:bg-slate-900"><h2 className="mb-4 font-semibold text-slate-800 dark:text-slate-100">Assign Mobile App Pages <span className="text-xs text-slate-500">Type: {mobileType}</span></h2><div className="grid gap-2 sm:grid-cols-2 lg:grid-cols-4">{mobilePages.map(page => <label key={page.pageId} className="flex items-center gap-2 text-sm text-slate-700 dark:text-slate-300"><input type="checkbox" checked={mobileSelected.has(String(page.pageId))} onChange={() => setMobileSelected(old => { const next = new Set(old); next.has(String(page.pageId)) ? next.delete(String(page.pageId)) : next.add(String(page.pageId)); return next })} />{page.pageName}</label>)}</div><button className="mt-5 rounded bg-emerald-600 px-5 py-2 text-sm font-semibold text-white" disabled={saving} onClick={saveMobile}>SUBMIT</button></section>}
  </div>
}

function pageGroupsFor(module, pages, selected, onToggle, onSelectAll) {
  const list = pages.filter(page => String(page.moduleId) === String(module.moduleId)).map(page => ({ pageId: page.pageId, pageName: page.displayName || page.pageName, parentId: page.parentId, parentName: page.parentName }))
  return <>{groupPages(list).map(group => <PageGroup key={group.key} group={group} selected={selected} onToggle={onToggle} onSelectAll={onSelectAll} />)}</>
}

function groupPages(pages) {
  const groups = {}
  pages.forEach(page => {
    const key = String(page.parentId || 'root')
    if (!groups[key]) groups[key] = { key, name: page.parentName || 'Pages', pages: [] }
    groups[key].pages.push(page)
  })
  return Object.values(groups)
}

function RoleModule({ module, pages, selected, onToggle, onSelectAll, onSubmit, saving }) {
  const modulePages = pages.filter(page => String(page.moduleId) === String(module.moduleId))
  const enabled = modulePages.some(page => selected.has(String(page.pageId)))
  return <div className="mb-4 rounded border p-3 dark:border-slate-700"><div className="mb-3 flex items-center justify-between"><span className="font-semibold text-slate-800 dark:text-slate-100">{module.moduleName}</span><input type="checkbox" checked={enabled} onChange={e => onSelectAll(modulePages, e.target.checked)} /></div>{pageGroupsFor(module, pages, selected, onToggle, onSelectAll)}<button className="rounded bg-emerald-600 px-5 py-2 text-sm font-semibold text-white disabled:opacity-50" disabled={saving} onClick={onSubmit}>{saving ? 'Saving...' : 'Submit'}</button></div>
}
