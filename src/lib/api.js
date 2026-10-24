const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'
export const TOKEN_KEY = 'schoolErp.accessToken'

let activeRequests = 0
let loaderBranding = { schoolName: '', logoPath: '' }

function emitLoadingState() {
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new CustomEvent('school-erp:loading', {
      detail: { active: activeRequests > 0, count: activeRequests },
    }))
  }
}

export function getActiveRequests() {
  return activeRequests
}

export function getLoaderBranding() {
  return loaderBranding
}

export function setLoaderBranding(next = {}) {
  loaderBranding = {
    schoolName: next.schoolName || loaderBranding.schoolName || '',
    logoPath: next.logoPath || next.schoolLogo || loaderBranding.logoPath || '',
  }
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new CustomEvent('school-erp:branding', { detail: loaderBranding }))
  }
}

export async function apiFetch(path, options = {}) {
  activeRequests += 1
  emitLoadingState()
  try {
    const token = localStorage.getItem(TOKEN_KEY)
    const headers = { ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }), ...(options.headers || {}) }
    if (token) headers.Authorization = `Bearer ${token}`
    const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
    const body = await response.json().catch(() => ({}))
    if (!response.ok) {
      const message = body.message || body.error || body.detail || `Request failed (${response.status})`
      throw new Error(message)
    }
    return body
  } finally {
    activeRequests = Math.max(0, activeRequests - 1)
    emitLoadingState()
  }
}
