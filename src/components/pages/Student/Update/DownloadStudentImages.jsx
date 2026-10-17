import { useEffect, useMemo, useState } from 'react'
import { Download, Eye, Image as ImageIcon, RotateCcw, Search } from 'lucide-react'
import { fetchStudentSearch, useStudents } from '../../../../hooks/useStudents'

const inputClass = 'w-full px-3 py-2 text-[13px] border rounded-lg outline-none border-slate-200 bg-white text-slate-800 focus:border-blue-400 focus:ring-2 focus:ring-blue-100 dark:border-white/10 dark:bg-[#1e2238] dark:text-slate-200'

function imageUrl(value) {
  if (!value) return ''
  if (/^(https?:|data:|blob:)/i.test(value)) return value
  return value.startsWith('/') ? value : `/${value.replace(/^~\//, '')}`
}

export default function DownloadStudentImages() {
  const { data, loading: optionsLoading, error: optionsError } = useStudents()
  const [session, setSession] = useState('')
  const [sectionId, setSectionId] = useState('0')
  const [rows, setRows] = useState([])
  const [query, setQuery] = useState('')
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')

  const sessions = data?.sessions || []
  // Legacy ddlclass binds TB_Class.Sec_id, displaying class_name(section).
  // Exclude orphan sections, matching the old class_id IN (class_name) query.
  const classes = useMemo(() => {
    const classIds = new Set((data?.classes || []).map(item => String(item.classId)))
    return (data?.sections || [])
      .filter(item => classIds.has(String(item.classId)))
      .map(item => ({ ...item, label: `${item.className}(${item.sectionName})` }))
      .sort((a, b) => Number(a.classId) - Number(b.classId) || a.label.localeCompare(b.label))
  }, [data])

  useEffect(() => {
    const current = sessions.find(item => String(item.status).toLowerCase() === 'default')?.session || sessions[0]?.session || ''
    if (!session && current) setSession(current)
  }, [sessions, session])

  const load = async () => {
    if (!session) { setMessage('Select a session first.'); return }
    setLoading(true); setMessage('')
    try {
      const result = await fetchStudentSearch({ session, sectionId: sectionId === '0' ? undefined : sectionId })
      setRows(result)
      if (!result.length) setMessage('No students found.')
    } catch (error) {
      setRows([]); setMessage(error.message || 'Request failed')
    } finally { setLoading(false) }
  }

  const clear = () => { setRows([]); setQuery(''); setSectionId('0'); setMessage('') }
  const filtered = useMemo(() => rows.filter(row => {
    const text = `${row.name || ''} ${row.registrationNo || ''}`.toLowerCase()
    return !query || text.includes(query.toLowerCase())
  }), [rows, query])

  const download = (row) => {
    const src = imageUrl(row.studentImage)
    if (!src) return
    const a = document.createElement('a'); a.href = src; a.download = `${row.registrationNo || row.studentId}.jpg`; a.target = '_blank'; a.rel = 'noreferrer'; a.click()
  }

  return (
    <div className="space-y-4 pb-8">
      <div>
        <h1 className="text-[20px] font-bold text-slate-800 dark:text-slate-100">Download Student Images</h1>
        <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">Select session and class, view the bound students, then download an available image.</p>
      </div>
      <div className="rounded-2xl border border-slate-200 bg-white dark:border-white/10 dark:bg-[#1a1f35] p-4">
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
          <select aria-label="Select Session" disabled={optionsLoading || !!optionsError} className={inputClass} value={session} onChange={e => setSession(e.target.value)}>
            <option value="">Select Session</option>{sessions.map(item => <option key={item.session}>{item.session}</option>)}
          </select>
          <select aria-label="Select Class" disabled={optionsLoading || !!optionsError} className={inputClass} value={sectionId} onChange={e => setSectionId(e.target.value)}>
            <option value="0">{'<Select All>'}</option>{classes.map(item => <option key={item.sectionId} value={item.sectionId}>{item.label}</option>)}
          </select>
          <div className="flex gap-2">
            <button onClick={load} disabled={loading || optionsLoading || !!optionsError} className="flex-1 inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-3 py-2 text-[13px] font-semibold text-white disabled:opacity-60"><Eye className="w-4 h-4" />{loading ? 'Loading…' : 'View'}</button>
            <button onClick={clear} className="rounded-lg bg-slate-100 px-3 py-2 text-slate-600 dark:bg-white/10 dark:text-slate-300" title="Reset"><RotateCcw className="w-4 h-4" /></button>
          </div>
        </div>
      </div>
      {optionsError && <p role="alert" className="text-sm text-rose-600">{optionsError.message || 'Unable to load sessions and classes.'}</p>}
      {(rows.length > 0 || message) && <div className="rounded-2xl border border-slate-200 bg-white dark:border-white/10 dark:bg-[#1a1f35] overflow-hidden">
        <div className="flex flex-wrap items-center gap-3 border-b border-slate-100 p-4 dark:border-white/10">
          <span className="font-semibold text-slate-700 dark:text-slate-200">Student Images ({filtered.length})</span>
          <div className="relative ml-auto w-full sm:w-64"><Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" /><input className={`${inputClass} pl-9`} value={query} onChange={e => setQuery(e.target.value)} placeholder="Search name or admission no." /></div>
        </div>
        {message && <p className="p-5 text-sm text-amber-600">{message}</p>}
        {filtered.length > 0 && <div className="overflow-x-auto"><table className="w-full text-left"><thead><tr className="border-b border-slate-100 dark:border-white/10">{['#','Student','Admission No.','Class','Image','Action'].map(label => <th key={label} className="px-4 py-3 text-[10px] uppercase tracking-wider text-slate-400">{label}</th>)}</tr></thead><tbody>{filtered.map((row, index) => { const src = imageUrl(row.studentImage); return <tr key={row.studentId || row.uid || index} className="border-b border-slate-50 dark:border-white/5"><td className="px-4 py-3 text-xs text-slate-400">{index + 1}</td><td className="px-4 py-3 text-sm font-semibold text-slate-700 dark:text-slate-200">{row.name || '—'}</td><td className="px-4 py-3 text-sm text-slate-500">{row.registrationNo || '—'}</td><td className="px-4 py-3 text-sm text-slate-500">{row.className || '—'}</td><td className="px-4 py-3">{src ? <img src={src} alt={row.name || ''} className="h-12 w-12 rounded-lg object-cover" /> : <ImageIcon className="h-7 w-7 text-slate-300" />}</td><td className="px-4 py-3"><button onClick={() => download(row)} disabled={!src} className="inline-flex items-center gap-1.5 rounded-lg bg-emerald-600 px-3 py-1.5 text-xs font-semibold text-white disabled:cursor-not-allowed disabled:opacity-40"><Download className="h-3.5 w-3.5" />Download</button></td></tr> })}</tbody></table></div>}
      </div>}
    </div>
  )
}
