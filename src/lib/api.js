const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'
export const TOKEN_KEY = 'schoolErp.accessToken'
export async function apiFetch(path, options = {}) {
  const token = localStorage.getItem(TOKEN_KEY)
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) }
  if (token) headers.Authorization = `Bearer ${token}`
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) {
    const message = body.message || body.error || body.detail || `Request failed (${response.status})`
    throw new Error(message)
  }
  return body
}
