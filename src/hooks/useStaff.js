import { apiFetch } from '../lib/api'

export async function fetchStaffBootstrap(session = '') {
  const query = session ? `?session=${encodeURIComponent(session)}` : ''
  return apiFetch('/staff/bootstrap' + query)
}

export async function fetchStaff(params = {}) {
  const q = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => { if (v !== undefined && v !== null && v !== '') q.set(k, v) })
  return apiFetch('/staff' + (q.toString() ? `?${q}` : ''))
}
