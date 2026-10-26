import { useState } from 'react'
import { KeyRound, LockKeyhole, ShieldCheck, Loader2 } from 'lucide-react'
import { superAdminFetch, SUPER_ADMIN_TOKEN_KEY } from '../../lib/superAdminApi'

export default function SuperAdminLogin({ onSuccess }) {
  const [identifier, setIdentifier] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  async function submit(event) {
    event.preventDefault()
    setError('')
    setBusy(true)
    try {
      const result = await superAdminFetch('/superadmin/login', {
        method: 'POST',
        body: JSON.stringify({ identifier, password }),
      })
      localStorage.setItem(SUPER_ADMIN_TOKEN_KEY, result.accessToken)
      onSuccess(result.user)
    } catch (cause) {
      setError(cause.message || 'Unable to sign in')
    } finally {
      setBusy(false)
    }
  }

  return <main className="min-h-screen bg-[radial-gradient(circle_at_top_right,_#244a79,_#0c1b32_52%,_#07101d)] px-4 py-8 text-slate-100">
    <div className="mx-auto flex min-h-[calc(100vh-4rem)] w-full max-w-5xl items-center justify-center">
      <section className="grid w-full overflow-hidden rounded-3xl border border-white/10 bg-white/[0.06] shadow-2xl backdrop-blur-xl md:grid-cols-[1.1fr_0.9fr]">
        <div className="hidden p-10 md:block">
          <div className="mb-14 inline-flex items-center gap-3 rounded-2xl border border-white/10 bg-white/10 px-4 py-3"><span className="grid h-10 w-10 place-items-center rounded-xl bg-cyan-400 font-black text-slate-950">E</span><span className="font-semibold tracking-wide">Edufox Control Center</span></div>
          <p className="text-sm font-semibold uppercase tracking-[0.25em] text-cyan-300">Superadmin access</p>
          <h1 className="mt-4 max-w-md text-4xl font-black leading-tight">School setup, modules and roles in one place.</h1>
          <p className="mt-5 max-w-lg text-sm leading-6 text-slate-300">Create or update school configuration securely. The school ERP accounts remain isolated from this control center.</p>
          <div className="mt-10 grid grid-cols-3 gap-3 text-xs text-slate-300"><div className="rounded-2xl border border-white/10 bg-white/[0.05] p-4">Schools<br /><strong className="mt-2 block text-lg text-white">Setup</strong></div><div className="rounded-2xl border border-white/10 bg-white/[0.05] p-4">Modules<br /><strong className="mt-2 block text-lg text-white">Assign</strong></div><div className="rounded-2xl border border-white/10 bg-white/[0.05] p-4">Roles<br /><strong className="mt-2 block text-lg text-white">Control</strong></div></div>
        </div>
        <div className="bg-white p-7 text-slate-900 sm:p-10">
          <div className="mb-8 flex items-center gap-3 md:hidden"><span className="grid h-10 w-10 place-items-center rounded-xl bg-slate-900 font-black text-white">E</span><div><p className="font-bold">Edufox Control Center</p><p className="text-xs text-slate-500">Superadmin access</p></div></div>
          <div className="mb-7"><p className="text-xs font-bold uppercase tracking-[0.2em] text-cyan-700">Secure login</p><h2 className="mt-2 text-2xl font-black">Welcome back</h2><p className="mt-1 text-sm text-slate-500">Sign in to open School Setup.</p></div>
          <form onSubmit={submit} className="space-y-5">
            <label className="block"><span className="text-sm font-semibold text-slate-700">Superadmin mobile</span><div className="relative mt-2"><KeyRound className="absolute left-3 top-3.5 text-slate-400" size={17} /><input required inputMode="numeric" value={identifier} onChange={e => setIdentifier(e.target.value)} placeholder="Enter login ID" className="w-full rounded-xl border border-slate-200 bg-slate-50 py-3 pl-10 pr-3 outline-none transition focus:border-cyan-600 focus:bg-white focus:ring-4 focus:ring-cyan-100" /></div></label>
            <label className="block"><span className="text-sm font-semibold text-slate-700">Password</span><div className="relative mt-2"><LockKeyhole className="absolute left-3 top-3.5 text-slate-400" size={17} /><input required type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Enter password" className="w-full rounded-xl border border-slate-200 bg-slate-50 py-3 pl-10 pr-3 outline-none transition focus:border-cyan-600 focus:bg-white focus:ring-4 focus:ring-cyan-100" /></div></label>
            {error && <div className="rounded-xl border border-rose-200 bg-rose-50 px-3 py-2.5 text-sm text-rose-700">{error}</div>}
            <button disabled={busy} className="flex w-full items-center justify-center gap-2 rounded-xl bg-slate-950 py-3.5 font-bold text-white transition hover:bg-cyan-800 disabled:cursor-not-allowed disabled:opacity-60">{busy ? <><Loader2 size={17} className="animate-spin" />Signing in...</> : 'Open School Setup'}</button>
          </form>
          <p className="mt-7 flex items-center justify-center gap-1.5 text-xs text-slate-500"><ShieldCheck size={14} className="text-cyan-700" /> Server-validated superadmin access</p>
        </div>
      </section>
    </div>
  </main>
}
