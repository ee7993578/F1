import { useState } from 'react'
import { Search, Save, Trash2, UserPlus, RotateCcw } from 'lucide-react'
import { apiFetch } from '../../../../lib/api'
import { fetchStudentSearch } from '../../../../hooks/useStudents'

const inputClass = 'w-full px-3 py-2 text-[13px] border rounded-lg outline-none border-slate-200 bg-white text-slate-800 focus:border-blue-400 focus:ring-2 focus:ring-blue-100 dark:border-white/10 dark:bg-[#1e2238] dark:text-slate-200'

export default function UpdateAlumniRecord() {
  const [registrationNo, setRegistrationNo] = useState('')
  const [rows, setRows] = useState([])
  const [form, setForm] = useState({ admissionNo: '', name: '', fatherName: '', address: '', uid: '' })
  const [mode, setMode] = useState('view')
  const [message, setMessage] = useState('')
  const [saving, setSaving] = useState(false)

  const view = async () => {
    if (!registrationNo.trim()) { setMessage('Enter Registration No.'); return }
    setMessage('')
    try {
      const result = await fetchStudentSearch({ query: registrationNo.trim() })
      setRows(result); setMode('view')
      if (!result.length) setMessage('No Adm. No Exists')
    } catch (error) { setRows([]); setMessage(error.message || 'Request failed') }
  }

  const add = () => { setMode('add'); setMessage(''); setForm({ admissionNo: registrationNo.trim(), name: '', fatherName: '', address: '', uid: '' }) }
  const edit = row => { setMode('edit'); setForm({ admissionNo: row.registrationNo || '', name: row.name || '', fatherName: row.fatherName || '', address: row.address || '', uid: row.uid || row.studentId || '' }) }
  const change = (key, value) => setForm(current => ({ ...current, [key]: value }))

  const save = async () => {
    if (!form.uid) { setMessage('Select an existing student record before updating.'); return }
    if (!form.name.trim()) { setMessage('Student Name is required.'); return }
    setSaving(true); setMessage('')
    try {
      await apiFetch(`/students/${encodeURIComponent(form.uid)}`, { method: 'PUT', body: JSON.stringify({ registrationNo: form.admissionNo, firstName: form.name, fatherName: form.fatherName, address: form.address }) })
      setMessage('Record Updated Successfully'); await view()
    } catch (error) { setMessage(error.message || 'Record cannot be updated') } finally { setSaving(false) }
  }

  const remove = async row => {
    if (!row?.uid && !row?.studentId) return
    if (!window.confirm('Are you sure to delete this record?')) return
    try { await apiFetch(`/students/${encodeURIComponent(row.uid || row.studentId)}`, { method: 'DELETE' }); setMessage('Record Deleted Successfully'); await view() }
    catch (error) { setMessage(error.message || 'Something Went Wrong') }
  }

  return (
    <div className="space-y-4 pb-8">
      <div><h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100">Update Alumni Record</h1><p className="mt-0.5 text-[13px] text-slate-500 dark:text-slate-400">Search by registration number, view the bound record, update it or add an alumni record.</p></div>
      <div className="rounded-2xl border border-slate-200 bg-white p-4 dark:border-white/10 dark:bg-[#1a1f35]"><div className="flex flex-wrap items-end gap-3"><label className="w-full sm:w-72 text-[11px] font-semibold uppercase tracking-wide text-slate-500">Enter Registration No.<input className={`${inputClass} mt-1`} value={registrationNo} onChange={e => setRegistrationNo(e.target.value.replace(/['\"]+/g, ''))} /></label><button onClick={view} className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-[13px] font-semibold text-white"><Search className="h-4 w-4" />View Record</button><button onClick={add} className="inline-flex items-center gap-2 rounded-lg bg-emerald-600 px-4 py-2 text-[13px] font-semibold text-white"><UserPlus className="h-4 w-4" />Add Record</button><button onClick={() => { setRows([]); setMessage(''); setMode('view') }} className="rounded-lg bg-slate-100 px-3 py-2 text-slate-600 dark:bg-white/10 dark:text-slate-300"><RotateCcw className="h-4 w-4" /></button></div></div>
      {message && <p className="rounded-xl border border-amber-200 bg-amber-50 p-3 text-sm text-amber-700 dark:border-amber-500/20 dark:bg-amber-500/10 dark:text-amber-300">{message}</p>}
      {mode === 'view' && rows.length > 0 && <div className="overflow-x-auto rounded-2xl border border-slate-200 bg-white dark:border-white/10 dark:bg-[#1a1f35]"><table className="w-full min-w-[700px] text-left"><thead><tr className="border-b border-slate-100 dark:border-white/10">{['#','Adm. No.','Name','Father Name','Address','Action'].map(label => <th key={label} className="px-4 py-3 text-[10px] uppercase tracking-wider text-slate-400">{label}</th>)}</tr></thead><tbody>{rows.map((row, index) => <tr key={row.uid || row.studentId || index} className="border-b border-slate-50 dark:border-white/5"><td className="px-4 py-3 text-xs text-slate-400">{index + 1}</td><td className="px-4 py-3 text-sm font-semibold text-slate-600 dark:text-slate-300">{row.registrationNo || '—'}</td><td className="px-4 py-3 text-sm text-slate-700 dark:text-slate-200">{row.name || '—'}</td><td className="px-4 py-3 text-sm text-slate-500">{row.fatherName || '—'}</td><td className="px-4 py-3 text-sm text-slate-500">{row.address || '—'}</td><td className="px-4 py-3"><div className="flex gap-2"><button onClick={() => edit(row)} className="rounded-lg bg-blue-600 px-3 py-1.5 text-xs font-semibold text-white">Edit</button><button onClick={() => remove(row)} className="inline-flex items-center gap-1 rounded-lg bg-rose-600 px-3 py-1.5 text-xs font-semibold text-white"><Trash2 className="h-3.5 w-3.5" />Delete</button></div></td></tr>)}</tbody></table></div>}
      {(mode === 'add' || mode === 'edit') && <div className="rounded-2xl border border-slate-200 bg-white p-5 dark:border-white/10 dark:bg-[#1a1f35]"><h2 className="mb-4 text-[15px] font-bold text-slate-700 dark:text-slate-200">Student Detail</h2><div className="grid grid-cols-1 gap-3 sm:grid-cols-2"><label className="text-[11px] font-semibold uppercase tracking-wide text-slate-500">Admission No.<input className={`${inputClass} mt-1`} value={form.admissionNo} onChange={e => change('admissionNo', e.target.value)} /></label><label className="text-[11px] font-semibold uppercase tracking-wide text-slate-500">Student Name<input className={`${inputClass} mt-1`} value={form.name} onChange={e => change('name', e.target.value.toUpperCase())} /></label><label className="text-[11px] font-semibold uppercase tracking-wide text-slate-500">Father Name<input className={`${inputClass} mt-1`} value={form.fatherName} onChange={e => change('fatherName', e.target.value)} /></label><label className="text-[11px] font-semibold uppercase tracking-wide text-slate-500 sm:col-span-2">Address<textarea className={`${inputClass} mt-1 min-h-24`} value={form.address} onChange={e => change('address', e.target.value)} /></label></div><div className="mt-4 flex gap-2"><button onClick={save} disabled={saving} className="inline-flex items-center gap-2 rounded-lg bg-emerald-600 px-4 py-2 text-[13px] font-semibold text-white disabled:opacity-60"><Save className="h-4 w-4" />{saving ? 'Saving…' : mode === 'add' ? 'Student Registered' : 'Update Record'}</button><button onClick={() => setMode('view')} className="rounded-lg bg-slate-100 px-4 py-2 text-[13px] font-semibold text-slate-600 dark:bg-white/10 dark:text-slate-300">Cancel</button></div></div>}
    </div>
  )
}
