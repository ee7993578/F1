import { useEffect, useMemo, useState } from 'react'
import { apiFetch } from '../../../../lib/api'
import { useERP } from '../../../../hooks/useERP'

const control = 'w-full rounded border border-slate-300 bg-white px-3 py-2 text-sm outline-none focus:border-emerald-500 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-100'

function Message({ value, error = false }) {
  if (!value) return null
  return <div className={`mb-4 rounded border px-3 py-2 text-sm ${error ? 'border-red-200 bg-red-50 text-red-700' : 'border-green-200 bg-green-50 text-green-700'}`}>{value}</div>
}

function AssignedPages({ staff, onClose }) {
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  useEffect(() => {
    if (!staff) return
    setLoading(true)
    apiFetch(`/master-settings/staff-roles/${staff.f_id}/pages`).then(setRows).finally(() => setLoading(false))
  }, [staff])
  const groups = rows.reduce((out, row) => {
    const key = String(row.moduleId)
    if (!out[key]) out[key] = { name: row.moduleName, pages: [] }
    out[key].pages.push(row.pageName)
    return out
  }, {})
  if (!staff) return null
  return <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" onClick={onClose}>
    <div className="max-h-[85vh] w-full max-w-2xl overflow-auto rounded bg-white p-5 shadow-xl dark:bg-slate-900" onClick={e => e.stopPropagation()}>
      <div className="mb-4 flex items-center justify-between border-b pb-3 dark:border-slate-700"><div><h3 className="font-semibold text-slate-800 dark:text-slate-100">Assigned Links</h3><p className="text-xs text-slate-500">{staff.faculty}</p></div><button className="px-3 py-1 text-xl text-slate-500" onClick={onClose}>×</button></div>
      {loading && <p className="py-8 text-center text-sm text-slate-500">Loading...</p>}
      {!loading && !rows.length && <p className="py-8 text-center text-sm text-slate-500">No links assigned.</p>}
      {!loading && Object.values(groups).map(group => <div key={group.name} className="mb-3 rounded border dark:border-slate-700"><div className="border-b bg-slate-50 px-3 py-2 text-sm font-semibold dark:border-slate-700 dark:bg-slate-800 dark:text-slate-100">{group.name}</div><div className="grid gap-1 p-3 sm:grid-cols-2">{group.pages.map(page => <div key={page} className="text-sm text-slate-700 dark:text-slate-300">{page}</div>)}</div></div>)}
      <button className="mt-3 w-full rounded bg-slate-200 px-4 py-2 text-sm dark:bg-slate-700 dark:text-slate-100" onClick={onClose}>Close</button>
    </div>
  </div>
}

export default function DefineStaffRole() {
  const { data } = useMasterSettingsCompat()
  const { menuData, setPage } = useERP()
  const [staffId, setStaffId] = useState('')
  const [selected, setSelected] = useState([])
  const [rows, setRows] = useState([])
  const [modalStaff, setModalStaff] = useState(null)
  const [message, setMessage] = useState('')
  const [error, setError] = useState(false)
  const [saving, setSaving] = useState(false)
  const staff = data?.staff || []
  const designations = data?.designations || []

  const loadRows = () => apiFetch('/master-settings/staff-roles').then(result => setRows(result || []))
  useEffect(() => { loadRows().catch(() => setRows([])) }, [])
  const selectedSet = useMemo(() => new Set(selected.map(String)), [selected])

  const chooseStaff = async (value) => {
    setStaffId(value); setMessage(''); setError(false); setSelected([])
    if (!value) return
    try { setSelected((await apiFetch(`/master-settings/staff-roles/${value}`) || []).map(String)) }
    catch (e) { setError(true); setMessage(e.message) }
  }
  const submit = async () => {
    setMessage(''); setError(false)
    if (!staffId) { setError(true); setMessage('Please select staff'); return }
    setSaving(true)
    try { await apiFetch('/master-settings/staff-roles', { method: 'POST', body: JSON.stringify({ facultyId: Number(staffId), designationIds: selected.map(Number) }) }); await loadRows(); setMessage('Designation Assigned') }
    catch (e) { setError(true); setMessage(e.message) }
    finally { setSaving(false) }
  }
  const reset = () => { setStaffId(''); setSelected([]); setMessage(''); setError(false); loadRows().catch(() => {}) }
  const openPermissionPage = (facultyId) => {
    const master = menuData.find(m => m.id === 'masterSetting' || String(m.label || '').toLowerCase().includes('master'))
    const group = master?.groups?.find(g => String(g.label || '').toLowerCase() === 'work') || master?.groups?.[0]
    const page = group?.pages?.find(p => String(p.id || p.pageName || '').toLowerCase().replace(/[^a-z0-9]/g, '') === 'rolespermission') || group?.pages?.find(p => String(p.label || '').toLowerCase().includes('roles'))
    if (master && group && page) setPage(master.id, group.id, page.id)
    window.history.replaceState({}, '', `${window.location.pathname}?f_id=${facultyId}`)
  }

  return <div className="space-y-5 pb-8">
    <div><h1 className="text-xl font-semibold text-slate-800 dark:text-slate-100">define Staff Role</h1><p className="text-sm text-slate-500">Assign designation to staff.</p></div>
    <Message value={message} error={error} />
    <section className="rounded bg-white p-5 shadow-sm dark:bg-slate-900">
      <label className="mb-1 block text-sm font-semibold text-slate-700 dark:text-slate-200">Staff</label>
      <select className={`${control} max-w-xl`} value={staffId} onChange={e => chooseStaff(e.target.value)}><option value="">Select</option>{staff.map(item => <option key={item.facultyId} value={item.facultyId}>{item.name}</option>)}</select>
      <label className="mb-2 mt-5 block text-sm font-semibold text-slate-700 dark:text-slate-200">Designation</label>
      <div className="grid gap-x-5 gap-y-2 sm:grid-cols-2 lg:grid-cols-5">{designations.map(item => { const id = String(item.id); return <label key={id} className="flex items-center gap-2 text-sm text-slate-700 dark:text-slate-300"><input type="checkbox" checked={selectedSet.has(id)} onChange={e => setSelected(old => e.target.checked ? [...old, id] : old.filter(x => String(x) !== id))} />{item.name || item.displayName}</label> })}</div>
      <div className="mt-5 flex gap-2 border-t pt-4 dark:border-slate-700"><button className="rounded bg-emerald-600 px-5 py-2 text-sm font-semibold text-white disabled:opacity-50" disabled={saving} onClick={submit}>{saving ? 'Saving...' : 'submit'}</button><button className="rounded bg-slate-200 px-5 py-2 text-sm font-semibold text-slate-700 dark:bg-slate-700 dark:text-slate-100" onClick={reset}>Reset</button></div>
    </section>
    <section className="overflow-hidden rounded bg-white shadow-sm dark:bg-slate-900"><div className="overflow-x-auto"><table className="w-full text-left text-sm"><thead className="bg-slate-50 text-xs uppercase text-slate-500 dark:bg-slate-800 dark:text-slate-300"><tr><th className="px-4 py-3">Staff Name</th><th className="px-4 py-3">Department</th><th className="px-4 py-3">Designation</th><th className="px-4 py-3">role name</th><th className="px-4 py-3">Add More Links</th></tr></thead><tbody>{rows.map(row => <tr key={row.staffUid || row.f_id} className="border-t dark:border-slate-800"><td className="px-4 py-3">{row.faculty || row.name}</td><td className="px-4 py-3">{row.departmentName || row.departmentname}</td><td className="px-4 py-3">{row.designationName || row.DesignationName}</td><td className="px-4 py-3">{row.roleName || row.rolename}</td><td className="px-4 py-3"><button className="mr-2 rounded bg-slate-200 px-2 py-1 text-sm dark:bg-slate-700 dark:text-white" onClick={() => openPermissionPage(row.facultyId || row.f_id)}>+</button><button className="rounded bg-slate-200 px-2 py-1 text-sm dark:bg-slate-700 dark:text-white" onClick={() => setModalStaff({ ...row, f_id: row.facultyId || row.f_id, faculty: row.faculty || row.name })}>View Link</button></td></tr>)}</tbody></table></div></section>
    {modalStaff && <AssignedPages staff={modalStaff} onClose={() => setModalStaff(null)} />}
  </div>
}

function useMasterSettingsCompat() {
  const [state, setState] = useState({ data: null })
  useEffect(() => { apiFetch('/master-settings/bootstrap').then(data => setState({ data })).catch(() => {}) }, [])
  return state
}
