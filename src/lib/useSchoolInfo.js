import { useEffect, useState } from 'react'
import { apiFetch } from './api'

export function useSchoolInfo() {
  const [schoolInfo, setSchoolInfo] = useState({ name: '', address: '', logo: '' })
  useEffect(() => {
    apiFetch('/menu')
      .then(menu => setSchoolInfo({
        name: menu.schoolName || '',
        address: menu.schoolAddress || '',
        logo: menu.schoolLogo || menu.logo || '',
      }))
      .catch(() => {})
  }, [])
  return schoolInfo
}
