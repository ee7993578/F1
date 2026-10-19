import { useEffect, useMemo, useState } from 'react'
import { Check, Eye, RotateCcw, Save } from 'lucide-react'
import { apiFetch } from '../../../../lib/api'
import { useStudents } from '../../../../hooks/useStudents'

const inputClass = 'w-full px-2.5 py-1.5 text-[12px] border rounded-md outline-none border-slate-200 bg-white text-slate-800 focus:border-blue-400 focus:ring-2 focus:ring-blue-100 dark:border-white/10 dark:bg-[#1e2238] dark:text-slate-200'

export default function TransferredStudentAdmNoAllotment() {
  const { data } = useStudents()
  const [session, setSession] = useState('')
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [message, setMessage] = useState('')
  const sessions = data?.sessions || []

  useEffect(() => {
    const current = sessions.find(item => String(item.status).toLowerCase() === 'default')?.session || sessions[0]?.session || ''
    if (!session && current) setSession(current)
  }, [sessions, session])

  const load = async () => {
    if (!session) return
    setLoading(true); setMessage('')
    try {
      const result = await apiFetch('/students/transferred-admission?session=' + encodeURIComponent(session))
      const candidates = (result || []).map(row => ({
        ...row,
        admNo: '',
        feeBookNo: row.feeBookNo || '',
        sectionId: row.sectionId || '',
        admissionDate: new Date().toISOString().slice(0, 10),
      }))
      setRows(candidates); if (!candidates.length) setMessage('No transferred students found for this session.')
    } catch (error) { setRows([]); setMessage(error.message || 'Request failed') } finally { setLoading(false) }
  }

  const update = (uid, field, value) => setRows(current => current.map(row => row.uid === uid ? { ...row, [field]: value } : row))
  const selected = useMemo(() => rows.filter(row => row.admNo && row.sectionId), [rows])

  const allot = async () => {
    if (!selected.length) { setMessage('Enter Admission No. and select Section for at least one student.'); return }
    setSaving(true); setMessage('')
    try {
      await Promise.all(selected.map(row => apiFetch(`/students/${encodeURIComponent(row.uid)}/admit`, {
        method: 'POST',
        body: JSON.stringify({
          admissionNo: row.admNo, feeBookNo: row.feeBookNo, className: row.className,
          sectionId: Number(row.sectionId), session, admissionDate: row.admissionDate,
          status: 'Registered', stream: row.stream || '', optionalSubject: row.optionalSubject || '',
        }),
      })))
      setMessage('Admission no. allotted successfully.'); await load()
    } catch (error) { setMessage(error.message || 'Admission allotment failed') } finally { setSaving(false) }
  }

  const clear = () => { setRows([]); setMessage('') }

  return (
    <div className="space-y-4 pb-8">
      <div>
        <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100">Allot Adm No for Transferred Students</h1>
        <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">Bind transferred students, enter Fee Book No. and admission details, then allot admission numbers.</p>
      </div>
      <div className="rounded-2xl border border-slate-200 bg-white p-4 dark:border-white/10 dark:bg-[#1a1f35]">
        <div className="flex flex-wrap items-end gap-3">
          <label className="w-56 text-[11px] font-semibold uppercase tracking-wide text-slate-500">Select Session<select className={`${inputClass} mt-1`} value={session} onChange={e => setSession(e.target.value)}><option value="">Select Session</option>{sessions.map(item => <option key={item.session}>{item.session}</option>)}</select></label>
          <button onClick={load} disabled={loading} className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-[13px] font-semibold text-white disabled:opacity-60"><Eye className="h-4 w-4" />{loading ? 'Loading…' : 'View'}</button>
          <button onClick={clear} className="rounded-lg bg-slate-100 px-3 py-2 text-slate-600 dark:bg-white/10 dark:text-slate-300" title="Reset"><RotateCcw className="h-4 w-4" /></button>
        </div>
      </div>
      {(rows.length > 0 || message) && <div className="overflow-x-auto rounded-2xl border border-slate-200 bg-white dark:border-white/10 dark:bg-[#1a1f35]">
        {message && <p className="border-b border-slate-100 p-3 text-sm text-amber-600 dark:border-white/10">{message}</p>}
        <table className="w-full min-w-[980px] text-left"><thead><tr className="border-b border-slate-100 dark:border-white/10">{['#','Form No.','Student Name','Admission No.','Fee Book No.','Admission Date','Class','Section','Status'].map(label => <th key={label} className="px-3 py-3 text-[10px] uppercase tracking-wider text-slate-400">{label}</th>)}</tr></thead><tbody>{rows.map((row, index) => { const sections = data?.sections?.filter(item => String(item.classId) === String(row.classId)) || []; return <tr key={row.uid || index} className="border-b border-slate-50 dark:border-white/5"><td className="px-3 py-3 text-xs text-slate-400">{index + 1}</td><td className="px-3 py-3 text-sm font-semibold text-slate-600 dark:text-slate-300">{row.formNo || row.registrationNo || '—'}</td><td className="px-3 py-3 text-sm text-slate-700 dark:text-slate-200">{row.name || '—'}<div className="text-[11px] text-slate-400">{row.fatherName || ''}</div></td><td className="px-3 py-3"><input className={inputClass} value={row.admNo} onChange={e => update(row.uid, 'admNo', e.target.value.replace(/\D/g, ''))} /></td><td className="px-3 py-3"><input className={inputClass} value={row.feeBookNo} onChange={e => update(row.uid, 'feeBookNo', e.target.value.replace(/\D/g, ''))} /></td><td className="px-3 py-3"><input type="date" className={inputClass} value={row.admissionDate} onChange={e => update(row.uid, 'admissionDate', e.target.value)} /></td><td className="px-3 py-3 text-sm text-slate-500">{row.className || '—'}</td><td className="px-3 py-3"><select className={inputClass} value={row.sectionId} onChange={e => update(row.uid, 'sectionId', e.target.value)}><option value="">Select Section</option>{sections.map(item => <option key={item.sectionId} value={item.sectionId}>{item.sectionName}</option>)}</select></td><td className="px-3 py-3 text-xs text-slate-500">{row.status || 'Transferred'}{row.admNo && row.sectionId && <Check className="ml-1 inline h-3.5 w-3.5 text-emerald-500" />}</td></tr> })}</tbody></table>
        {rows.length > 0 && <div className="flex justify-end border-t border-slate-100 p-3 dark:border-white/10"><button onClick={allot} disabled={saving} className="inline-flex items-center gap-2 rounded-lg bg-emerald-600 px-4 py-2 text-[13px] font-semibold text-white disabled:opacity-60"><Save className="h-4 w-4" />{saving ? 'Saving…' : 'Admitted'}</button></div>}
      </div>}
    </div>
  )
}
