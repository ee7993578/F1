import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { apiFetch, TOKEN_KEY } from '../lib/api'
const AuthContext = createContext(null)
export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY))
  const [user, setUser] = useState(null); const [loading, setLoading] = useState(Boolean(token))
  useEffect(() => { if (!token) { setLoading(false); return }
    apiFetch('/auth/me').then(({ user: current }) => setUser(current))
      .catch(() => { localStorage.removeItem(TOKEN_KEY); setToken(null); setUser(null) }).finally(() => setLoading(false))
  }, [token])
  const value = useMemo(() => ({
    user, loading,
    async login(payload) { const result = await apiFetch('/auth/login', { method: 'POST', body: JSON.stringify(payload) }); localStorage.setItem(TOKEN_KEY, result.accessToken); setToken(result.accessToken); setUser(result.user); return result },
    logout() { localStorage.removeItem(TOKEN_KEY); setToken(null); setUser(null) },
  }), [user, loading])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
export function useAuth() { return useContext(AuthContext) }