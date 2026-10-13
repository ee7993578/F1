import { useCallback, useEffect, useMemo, useState } from 'react'
import { useStudents } from '../hooks/useStudents'

export function useSessionsClasses() {
  const { data, loading, error, refresh } = useStudents()
  const sessions = useMemo(() => (data?.sessions || []).map(row => ({
    id: row.id,
    session: row.session ?? row.name ?? '',
    status: row.status ?? ''
  })).filter(row => row.session), [data])
  const classes = useMemo(() => (data?.classes || []).map(row => ({
    ...row,
    id: row.classId ?? row.id,
    classId: row.classId ?? row.id,
    className: row.className ?? row.class_name ?? ''
  })).filter(row => row.className), [data])
  const classIdOf = useCallback(name => classes.find(row => row.className === name)?.classId, [classes])
  const [session, setSession] = useState('')
  useEffect(() => {
    if (!session && sessions.length) setSession(sessions.find(row => /default|active/i.test(row.status))?.session || sessions[0].session)
  }, [sessions, session])
  return { sessions, classes, classIdOf, session, setSession, loading, error, refresh }
}

export function csvDownload(filename, rows) {
  const list = Array.isArray(rows) ? rows : []
  if (!list.length) return
  const keys = Object.keys(list[0])
  const csv = [keys.join(','), ...list.map(row => keys.map(key => JSON.stringify(row[key] ?? '')).join(','))].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const anchor = document.createElement('a')
  anchor.href = url
  anchor.download = filename
  anchor.click()
  URL.revokeObjectURL(url)
}
