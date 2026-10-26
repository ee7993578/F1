const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'
export const SUPER_ADMIN_TOKEN_KEY = 'schoolErp.superAdminAccessToken'

export async function superAdminFetch(path, options = {}) {
  const token = localStorage.getItem(SUPER_ADMIN_TOKEN_KEY)
  const headers = { ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }), ...(options.headers || {}) }
  if (token) headers.Authorization = `Bearer ${token}`
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) {
    if ((response.status === 401 || response.status === 403) && !path.endsWith('/superadmin/login')) {
      localStorage.removeItem(SUPER_ADMIN_TOKEN_KEY)
      localStorage.removeItem('schoolErp.superAdminUser')
      if (window.location.pathname.startsWith('/superadmin')) window.location.replace('/superadmin')
      throw new Error('Super Admin session expired. Please sign in again.')
    }
    throw new Error(body.message || body.error || `Request failed (${response.status})`)
  }
  return body
}
