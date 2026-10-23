import { useEffect, useMemo, useState } from 'react'
import { apiFetch, TOKEN_KEY } from '../../../../lib/api'

const input = 'w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-800 outline-none focus:border-blue-500 dark:border-slate-600 dark:bg-slate-900 dark:text-slate-100'
const card = 'rounded-xl border border-slate-200 bg-white p-4 shadow-sm dark:border-slate-700 dark:bg-slate-800'
const primary = 'rounded-lg bg-emerald-600 px-4 py-2 text-sm font-semibold text-white hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-50'
const secondary = 'rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 dark:border-slate-600 dark:text-slate-200 dark:hover:bg-slate-700'

function Field({ label, children }) {
  return <label className="block space-y-1"><span className="text-xs font-semibold text-slate-600 dark:text-slate-300">{label}</span>{children}</label>
}

function FileLink({ path }) {
  const open = async () => {
    if (!path) return
    const token = localStorage.getItem(TOKEN_KEY)
    try {
      const response = await fetch(`${import.meta.env.VITE_API_BASE_URL || '/api'}/uploads/files?path=${encodeURIComponent(path)}`, { headers: token ? { Authorization: `Bearer ${token}` } : {} })
      if (!response.ok) throw new Error('File not found')
      const blob = await response.blob()
      window.open(URL.createObjectURL(blob), '_blank', 'noopener,noreferrer')
    } catch (error) {
      window.alert(error.message)
    }
  }
  return path ? <button type="button" className="text-blue-600 underline dark:text-blue-300" onClick={open}>View</button> : <span className="text-slate-400">—</span>
}

function BusyOverlay({ text }) {
  if (!text) return null
  return <div className="fixed inset-0 z-[100] flex items-center justify-center bg-slate-950/35 p-4 backdrop-blur-[2px]" role="status" aria-live="polite">
    <div className="flex min-w-[190px] items-center gap-3 rounded-2xl border border-white/20 bg-white/95 px-5 py-4 text-sm font-semibold text-slate-700 shadow-2xl dark:bg-slate-900/95 dark:text-slate-100">
      <span className="h-6 w-6 animate-spin rounded-full border-2 border-emerald-200 border-t-emerald-600 dark:border-emerald-900 dark:border-t-emerald-400" />
      <span>{text}</span>
    </div>
  </div>
}

function Message({ text, error }) {
  return text ? <div className={`rounded-lg px-3 py-2 text-sm ${error ? 'bg-red-50 text-red-700 dark:bg-red-500/10 dark:text-red-300' : 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-300'}`}>{text}</div> : null
}

function ExistingHomework({ rows, onDelete }) {
  return <div className={`${card} overflow-hidden p-0`}>
    <div className="border-b border-slate-200 p-4 dark:border-slate-700"><h2 className="font-semibold">◉&nbsp; Existing Homework</h2></div>
    <div className="overflow-x-auto">
      <table className="min-w-full text-sm">
        <thead className="bg-sky-700 text-left text-xs uppercase text-white"><tr><th className="px-3 py-2">S.No</th><th className="px-3 py-2">Subject</th><th className="px-3 py-2">Homework</th><th className="px-3 py-2">File</th><th className="px-3 py-2">Action</th></tr></thead>
        <tbody>{rows.map((row, index) => <tr className="border-t border-slate-100 dark:border-slate-700" key={row.homeworkId || `${row.subject}-${index}`}>
          <td className="whitespace-nowrap px-3 py-2">{index + 1}</td><td className="whitespace-nowrap px-3 py-2">{row.subject}</td><td className="px-3 py-2">{row.homeworkDesc || ''}</td><td className="whitespace-nowrap px-3 py-2"><FileLink path={row.filePath} /></td>
          <td className="whitespace-nowrap px-3 py-2">{row.isDeletable === undefined || Number(row.isDeletable) === 1 ? <button type="button" className="text-red-600 hover:underline dark:text-red-300" onClick={() => onDelete(row)}>Delete</button> : <span className="text-slate-400">—</span>}</td>
        </tr>)}{!rows.length && <tr><td colSpan="5" className="px-3 py-8 text-center text-slate-500">No records found.</td></tr>}</tbody>
      </table>
    </div>
  </div>
}

function HomeworkEditor({ rows, setRows, onSubmit, busy }) {
  return <div className={`${card} space-y-4`}>
    <h2 className="font-semibold">↥&nbsp; Upload / Update Homework</h2>
    <div className="overflow-x-auto">
      <table className="hidden min-w-full text-sm sm:table">
        <thead className="bg-emerald-700 text-left text-xs uppercase text-white"><tr><th className="px-3 py-2">S.No</th><th className="px-3 py-2">Subject</th><th className="px-3 py-2">Homework Description</th><th className="px-3 py-2">Upload File</th></tr></thead>
        <tbody>{rows.map((row, index) => <tr className="border-t border-slate-100 align-top dark:border-slate-700" key={row.subjectId || index}>
          <td className="px-3 py-3">{index + 1}</td><td className="px-3 py-3 font-semibold">{row.subjectName}</td><td className="px-3 py-3"><textarea className={input} rows="2" value={row.homeworkDesc || ''} onChange={e => setRows(old => old.map((item, i) => i === index ? { ...item, homeworkDesc: e.target.value } : item))} /></td><td className="px-3 py-3"><input className={input} type="file" onChange={e => setRows(old => old.map((item, i) => i === index ? { ...item, file: e.target.files?.[0] || null } : item))} />{row.filePath && <div className="mt-1 text-xs"><FileLink path={row.filePath} /></div>}</td>
        </tr>)}</tbody>
      </table>
    </div>
    <div className="space-y-3 sm:hidden">{rows.map((row, index) => <div className="rounded-lg border border-slate-200 p-3 dark:border-slate-700" key={row.subjectId || index}>
      <div className="mb-2 font-semibold">{index + 1}. {row.subjectName}</div><textarea className={input} rows="3" placeholder="Homework Description" value={row.homeworkDesc || ''} onChange={e => setRows(old => old.map((item, i) => i === index ? { ...item, homeworkDesc: e.target.value } : item))} /><input className={`${input} mt-2`} type="file" onChange={e => setRows(old => old.map((item, i) => i === index ? { ...item, file: e.target.files?.[0] || null } : item))} />{row.filePath && <div className="mt-1 text-xs"><FileLink path={row.filePath} /></div>}
    </div>)}</div>
    <button type="button" className={primary} disabled={busy || !rows.length} onClick={onSubmit}>{busy ? 'Submitting...' : 'Submit'}</button>
  </div>
}

export default function UploadDailyHomeworkExact() {
  const [boot, setBoot] = useState(null)
  const [session, setSession] = useState('')
  const [secId, setSecId] = useState('')
  const [date, setDate] = useState('')
  const [existing, setExisting] = useState([])
  const [rows, setRows] = useState([])
  const [message, setMessage] = useState('')
  const [error, setError] = useState(false)
  const [busy, setBusy] = useState(false)
  const [bootError, setBootError] = useState('')
  const current = useMemo(() => (boot?.classes || []).find(x => String(x.secId || x.id) === String(secId)), [boot, secId])

  useEffect(() => {
    let alive = true
    apiFetch('/uploads/bootstrap').then(data => { if (alive) { setBoot(data); setSession(data.session || data.sessions?.[0]?.value || '') } }).catch(e => { if (alive) setBootError(e.message) })
    return () => { alive = false }
  }, [])

  const clearResults = () => { setExisting([]); setRows([]) }
  const run = async (work, success = '') => {
    setBusy(true); setError(false); setMessage('')
    try { const result = await work(); if (success || result?.message) setMessage(success || result.message); return result } catch (e) { setError(true); setMessage(e.message); throw e } finally { setBusy(false) }
  }
  const show = () => run(async () => {
    if (!current) throw new Error('Please select a class.')
    if (!date) throw new Error('Please select a date.')
    const [view, subjects] = await Promise.all([
      apiFetch(`/uploads/daily-homework/view?session=${encodeURIComponent(session)}&secId=${current.secId}&date=${encodeURIComponent(date)}`),
      apiFetch(`/uploads/daily-homework/subjects?session=${encodeURIComponent(session)}&secId=${current.secId}&classId=${current.classId}&date=${encodeURIComponent(date)}`),
    ])
    setExisting(view || [])
    setRows((subjects || []).map(row => ({ ...row, originalDesc: row.homeworkDesc || '', file: null })))
    return { message: '' }
  })
  const submit = () => run(async () => {
    const changed = rows.filter(row => String(row.homeworkDesc || '').trim() !== String(row.originalDesc || '').trim() || row.file)
    const tooLarge = changed.filter(row => row.file && row.file.size > 2 * 1024 * 1024)
    const valid = changed.filter(row => !tooLarge.includes(row))
    if (!changed.length) throw new Error('Nothing to submit. Make a change first.')
    if (!valid.length) throw new Error('All selected files are larger than 2 MB.')
    const fd = new FormData(); fd.append('session', session); fd.append('secId', current.secId); fd.append('date', date)
    fd.append('rows', JSON.stringify(valid.map(row => ({ subjectId: row.subjectId, homeworkDesc: row.homeworkDesc || '' }))))
    valid.forEach(row => fd.append('files', row.file || new File([], 'empty.txt', { type: 'text/plain' })))
    const result = await apiFetch('/uploads/daily-homework', { method: 'POST', body: fd })
    await reload()
    return { message: tooLarge.length ? `${result.message || 'Homework uploaded successfully.'} ${tooLarge.length} file(s) skipped because the maximum size is 2 MB.` : result.message }
  })
  const reload = async () => {
    const [view, subjects] = await Promise.all([
      apiFetch(`/uploads/daily-homework/view?session=${encodeURIComponent(session)}&secId=${current.secId}&date=${encodeURIComponent(date)}`),
      apiFetch(`/uploads/daily-homework/subjects?session=${encodeURIComponent(session)}&secId=${current.secId}&classId=${current.classId}&date=${encodeURIComponent(date)}`),
    ])
    setExisting(view || [])
    setRows((subjects || []).map(row => ({ ...row, originalDesc: row.homeworkDesc || '', file: null })))
  }
  const deleteRow = row => { if (!window.confirm(`Delete homework for "${row.subject}"? This cannot be undone.`)) return; run(async () => { const result = await apiFetch(`/uploads/daily-homework/${row.homeworkId}`, { method: 'DELETE' }); await reload(); return result }) }

  if (bootError) return <div className="space-y-4 p-1 text-slate-800 dark:text-slate-100"><h1 className="text-xl font-bold">Upload Daily Homework</h1><Message text={bootError} error /></div>
  if (!boot) return <><div className="p-1 text-slate-800 dark:text-slate-100"><h1 className="text-xl font-bold">Upload Daily Homework</h1></div><BusyOverlay text="Loading classes..." /></>
  return <div className="relative space-y-4 p-1 text-slate-800 dark:text-slate-100">
    <div><h1 className="text-xl font-bold">Upload Daily Homework</h1><p className="text-xs text-slate-500 dark:text-slate-400">Upload Daily Homework</p></div><Message text={message} error={error} />
    <div className={`${card} grid gap-3 sm:grid-cols-2 lg:grid-cols-4`}><Field label="Select Class"><select className={input} value={secId} onChange={e => { setSecId(e.target.value); clearResults(); setMessage('') }}><option value="">-- Select Class --</option>{(boot.classes || []).map(x => <option key={x.secId || x.id} value={x.secId || x.id}>{x.label}</option>)}</select></Field><Field label="Issue Date"><input className={input} type="date" value={date} onChange={e => { setDate(e.target.value); clearResults(); setMessage('') }} /></Field><div className="flex items-end"><button type="button" className={primary} disabled={busy} onClick={() => show().catch(() => {})}>Show</button></div></div>
    {existing.length > 0 && <ExistingHomework rows={existing} onDelete={deleteRow} />}
    {rows.length > 0 && <HomeworkEditor rows={rows} setRows={setRows} onSubmit={submit} busy={busy} />}
    <BusyOverlay text={busy ? 'Please wait...' : ''} />
  </div>
}
