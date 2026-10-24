import { useEffect, useMemo, useState } from 'react'
import { getActiveRequests, getLoaderBranding } from '../../lib/api'
import { useAuth } from '../../auth/AuthContext'

function initials(name) {
  const words = String(name || 'School ERP').trim().split(/\s+/).filter(Boolean)
  return (words.length ? words.slice(0, 3).map((word) => word[0]) : ['E', 'R', 'P']).join('').toUpperCase()
}

function cleanValue(value) {
  const text = String(value || '').trim()
  return text && text !== 'null' && text !== 'undefined' ? text : ''
}

export default function GlobalLoader() {
  const { user } = useAuth()
  const [visible, setVisible] = useState(() => getActiveRequests() > 0)
  const [branding, setBranding] = useState(() => getLoaderBranding())
  const [logoFailed, setLogoFailed] = useState(false)

  useEffect(() => {
    const onLoading = (event) => setVisible(Boolean(event.detail?.active))
    const onBranding = (event) => {
      setBranding(event.detail || {})
      setLogoFailed(false)
    }
    window.addEventListener('school-erp:loading', onLoading)
    window.addEventListener('school-erp:branding', onBranding)
    return () => {
      window.removeEventListener('school-erp:loading', onLoading)
      window.removeEventListener('school-erp:branding', onBranding)
    }
  }, [])

  useEffect(() => {
    if (user) {
      setBranding((current) => ({
        schoolName: cleanValue(user.schoolName) || current.schoolName,
        logoPath: cleanValue(user.logoPath) || current.logoPath,
      }))
    }
  }, [user])

  const schoolName = cleanValue(branding.schoolName) || cleanValue(user?.schoolName) || 'School ERP'
  const logoPath = cleanValue(branding.logoPath) || cleanValue(user?.logoPath)
  const schoolInitials = useMemo(() => initials(schoolName), [schoolName])

  if (!visible) return null

  return (
    <div className="global-loader fixed inset-0 z-[9999] flex items-center justify-center px-5" role="status" aria-live="polite" aria-label="Loading">
      <div className="global-loader-orb global-loader-orb-one" />
      <div className="global-loader-orb global-loader-orb-two" />
      <section className="global-loader-card relative w-full max-w-[360px] overflow-hidden rounded-[28px] border border-white/20 px-7 py-8 text-center shadow-2xl">
        <div className="global-loader-grid" />
        <div className="relative">
          <div className="global-loader-mark mx-auto h-[104px] w-[104px]">
            <div className="global-loader-ring global-loader-ring-one" />
            <div className="global-loader-ring global-loader-ring-two" />
            <div className="global-loader-logo relative flex h-[72px] w-[72px] items-center justify-center overflow-hidden rounded-[21px] bg-white text-[18px] font-extrabold tracking-wide text-indigo-900 shadow-xl">
              {logoPath && !logoFailed ? <img src={logoPath} alt={`${schoolName} logo`} className="h-full w-full object-contain p-2" onError={() => setLogoFailed(true)} /> : schoolInitials}
            </div>
          </div>
          <p className="mt-5 text-[11px] font-bold uppercase tracking-[0.28em] text-indigo-200/80">Please wait</p>
          <h2 className="mt-2 text-xl font-bold text-white">Preparing your workspace</h2>
          <p className="mt-2 truncate text-sm text-white/65" title={schoolName}>{schoolName}</p>
          <div className="global-loader-progress mx-auto mt-6 h-1.5 w-full overflow-hidden rounded-full bg-white/10"><span /></div>
          <div className="mt-4 flex items-center justify-center gap-1.5" aria-hidden="true"><i /><i /><i /></div>
        </div>
      </section>
    </div>
  )
}
