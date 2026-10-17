import { useEffect, useState } from 'react'
import { Calendar, ExternalLink, Loader2, Phone, AlertCircle } from 'lucide-react'
import { useStudents } from '../../../../hooks/useStudents'
import { apiFetch } from '../../../../lib/api'

export default function RegistrationRedirection() {
  const { data } = useStudents()
  const sessions = data?.sessions || []
  const [session, setSession] = useState('')
  const [phone, setPhone] = useState('')
  const [busy, setBusy] = useState(false)
  const [message, setMessage] = useState(null)

  useEffect(() => {
    const current = sessions.find(row => String(row.status).toLowerCase() === 'default')?.session || sessions[0]?.session || ''
    if (current && !session) setSession(current)
  }, [sessions, session])

  const submit = async event => {
    event.preventDefault()
    setMessage(null)
    if (!session) return setMessage({ error: 'Select session' })
    if (!phone.trim()) return setMessage({ error: 'Mobile number is required' })
    setBusy(true)
    try {
      const result = await apiFetch('/students/registration-redirection', {
        method: 'POST', body: JSON.stringify({ session, phone: phone.trim() })
      })
      window.open(result.url, '_blank', 'noopener,noreferrer')
      setMessage({ ok: result.message || 'Registration form opened' })
    } catch (error) {
      setMessage({ error: error.message || 'Unable to open registration form' })
    } finally { setBusy(false) }
  }

  return (
    <div className="page-animate space-y-4 pb-8">
      <div>
        <h1 className="font-display text-[20px] font-bold text-slate-800 dark:text-slate-100">Registration Redirection</h1>
        <p className="text-[13px] text-slate-500 dark:text-slate-400 mt-0.5">Open or create the online registration form for a parent.</p>
      </div>
      <form onSubmit={submit} className="max-w-2xl rounded-2xl border border-slate-200 dark:border-[rgba(99,102,241,0.2)] bg-white dark:bg-[#1a1f35] shadow-sm overflow-hidden">
        <div className="px-5 py-3.5 border-b border-slate-100 dark:border-[rgba(99,102,241,0.15)] bg-slate-50/70 dark:bg-white/[0.02] flex items-center gap-3">
          <span className="w-1 h-5 rounded-full bg-blue-500" /><ExternalLink className="w-4 h-4 text-blue-600 dark:text-blue-400" />
          <span className="text-[14px] font-bold text-slate-700 dark:text-slate-200">Online Student Registration</span>
        </div>
        <div className="p-5 grid grid-cols-1 sm:grid-cols-2 gap-4">
          <label className="flex flex-col gap-1.5 text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">
            <span className="flex items-center gap-1"><Calendar className="w-3.5 h-3.5" /> Session</span>
            <select value={session} onChange={e => setSession(e.target.value)} className="px-3 py-2 text-[13px] normal-case font-medium border rounded-lg outline-none bg-white dark:bg-[#1e2238] text-slate-800 dark:text-slate-200 border-slate-200 dark:border-[rgba(99,102,241,0.2)]">
              <option value="">-- Select Session --</option>{sessions.map(row => <option key={row.session} value={row.session}>{row.session}</option>)}
            </select>
          </label>
          <label className="flex flex-col gap-1.5 text-[11px] font-semibold text-slate-500 dark:text-slate-400 uppercase tracking-wide">
            <span className="flex items-center gap-1"><Phone className="w-3.5 h-3.5" /> Mobile Number</span>
            <input value={phone} onChange={e => setPhone(e.target.value)} inputMode="numeric" maxLength={15} placeholder="Enter parent mobile number" className="px-3 py-2 text-[13px] normal-case font-medium border rounded-lg outline-none bg-white dark:bg-[#1e2238] text-slate-800 dark:text-slate-200 border-slate-200 dark:border-[rgba(99,102,241,0.2)]" />
          </label>
        </div>
        {message && <div className={`mx-5 mb-4 flex items-center gap-2 px-3 py-2.5 rounded-lg text-[12px] ${message.error ? 'bg-rose-50 text-rose-700 dark:bg-rose-500/10 dark:text-rose-300' : 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-300'}`}><AlertCircle className="w-4 h-4" />{message.error || message.ok}</div>}
        <div className="px-5 py-4 border-t border-slate-100 dark:border-[rgba(99,102,241,0.15)] flex justify-end">
          <button type="submit" disabled={busy} className="inline-flex items-center gap-2 px-5 py-2 rounded-xl text-[13px] font-semibold text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-60">
            {busy ? <Loader2 className="w-4 h-4 animate-spin" /> : <ExternalLink className="w-4 h-4" />} Open Registration Form
          </button>
        </div>
      </form>
    </div>
  )
}
