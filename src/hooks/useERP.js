import { useState, useCallback, useEffect } from 'react'
import { resolveMenu } from '../data/menuResolver'
import { apiFetch } from '../lib/api'
import { useAuth } from '../auth/AuthContext'

const DEFAULT_MODULE = 'dashboard'
const DEFAULT_GROUP = 'allDashboard'

const pageToken = (value) => String(value || '').toLowerCase().replace(/[^a-z0-9]/g, '')
const canonicalPageToken = (value) => {
  const token = pageToken(value)
  if (['vehiclemaster', 'vehiclemasteraspx'].includes(token)) return 'vehiclemaster'
  if (['drivervehicledetails', 'drivervehicledetailsaspx', 'driverdetail', 'driverdetails', 'drivervehiclemaster'].includes(token)) return 'drivervehiclemaster'
  if (['assigntransport', 'assigntransportaspx'].includes(token)) return 'assigntransport'
  return token
}
const pageMatches = (page, activePage) => {
  const active = canonicalPageToken(activePage)
  return [page?.id, page?.pageKey, page?.pageName, page?.url, page?.label].some((value) => canonicalPageToken(value) === active)
}

export function useERP() {
  const { user } = useAuth()
  const [menuData, setMenuData] = useState([])
  const [menuMeta, setMenuMeta] = useState({ schoolName: '', session: '' })
  const [menuLoading, setMenuLoading] = useState(true)
  const [menuError, setMenuError] = useState('')
  const [activeModule, setActiveModuleState] = useState(DEFAULT_MODULE)
  const [activeGroup, setActiveGroupState] = useState(DEFAULT_GROUP)
  const [activePage, setActivePageState] = useState(null)
  const [isDrawerOpen, setDrawerOpen] = useState(false)
  const [isDashboard, setIsDashboard] = useState(true)

  useEffect(() => {
    let mounted = true
    setMenuLoading(true)
    setMenuError('')
    apiFetch('/menu')
      .then((response) => {
        if (!mounted) return
        const resolved = resolveMenu(response)
        setMenuData(resolved)
        setMenuMeta({ schoolName: response.schoolName || '', session: response.session || '' })
        const portalUser = ['S', 'P'].includes(String(user?.typeStatus || '').toUpperCase())
        if (portalUser) {
          const studentModule = resolved.find(module => module.id === 'student') || resolved[0]
          const studentGroup = studentModule?.groups?.[0]
          const studentPage = studentGroup?.pages?.[0]
          if (studentModule) {
            setActiveModuleState(studentModule.id)
            setActiveGroupState(studentGroup?.id || '')
            setActivePageState(studentPage?.id || null)
            setIsDashboard(false)
          }
        } else if (!resolved.some(module => module.id === activeModule)) {
          setActiveModuleState(resolved[0]?.id || '')
          setActiveGroupState(resolved[0]?.groups?.[0]?.id || '')
          setActivePageState(null)
        }
      })
      .catch((cause) => {
        if (!mounted) return
        setMenuData([])
        setMenuError(cause.message || 'Unable to load the permitted menu.')
      })
      .finally(() => {
        if (mounted) setMenuLoading(false)
      })
    return () => { mounted = false }
  }, [user?.schoolId, user?.typeStatus])

  const moduleData = menuData.find((m) => m.id === activeModule) || menuData[0]
  const setModule = useCallback((moduleId) => {
    const mod = menuData.find((m) => m.id === moduleId)
    if (!mod) return
    setActiveModuleState(moduleId)
    setActiveGroupState(mod.groups[0]?.id || '')
    setActivePageState(null)
    setIsDashboard(false)
  }, [menuData])
  const setGroup = useCallback((groupId) => {
    setActiveGroupState(groupId)
    setActivePageState(null)
  }, [])
  const setPage = useCallback((moduleId, groupId, pageId) => {
    setActiveModuleState(moduleId)
    setActiveGroupState(groupId)
    setActivePageState(pageId)
    setIsDashboard(false)
    setDrawerOpen(false)
  }, [])
  const goHome = useCallback(() => {
    setIsDashboard(true)
    setActivePageState(null)
    setDrawerOpen(false)
  }, [])
  const toggleDrawer = useCallback(() => setDrawerOpen((p) => !p), [])
  const closeDrawer = useCallback(() => setDrawerOpen(false), [])
  const currentGroup = moduleData?.groups.find((g) => g.id === activeGroup)
    || moduleData?.groups.find((g) => g.pages?.some((p) => pageMatches(p, activePage)))
  const currentPage = currentGroup?.pages.find((p) => p.id === activePage || pageMatches(p, activePage)) || null

  return {
    menuData, menuMeta, menuLoading, menuError, activeModule, activeGroup, activePage, moduleData, currentGroup, currentPage,
    isDrawerOpen, isDashboard, setModule, setGroup, setPage, goHome, toggleDrawer, closeDrawer,
  }
}
