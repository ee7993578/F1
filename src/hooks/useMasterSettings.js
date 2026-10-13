import { useCallback, useEffect, useState } from 'react'
import { apiFetch } from '../lib/api'

export function useMasterSettings() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const refresh = useCallback(async () => {
    setLoading(true)
    try {
      const result = await apiFetch('/master-settings/bootstrap')
      setData(result)
      setError(null)
      return result
    } catch (err) {
      setError(err)
      throw err
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    refresh().catch(() => {})
  }, [refresh])

  return { data, loading, error, refresh }
}
