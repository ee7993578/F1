import { useState, useCallback, useEffect } from 'react'
import { MENU_DATA } from '../data/menuData'
import { resolveMenu } from '../data/menuResolver'
import { apiFetch } from '../lib/api'

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
  const [menuData, setMenuData] = useState(MENU_DATA)
  const [menuMeta, setMenuMeta] = useState({ schoolName: '', session: '' })
  const [activeModule, setActiveModuleState] = useState(DEFAULT_MODULE)
  const [activeGroup, setActiveGroupState] = useState(DEFAULT_GROUP)
  const [activePage, setActivePageState] = useState(null)
  const [isDrawerOpen, setDrawerOpen] = useState(false)
  const [isDashboard, setIsDashboard] = useState(true)

  useEffect(() => {
    let mounted = true
    apiFetch('/menu')
      .then((response) => {
        if (!mounted) return
        setMenuData(resolveMenu(response))
        setMenuMeta({ schoolName: response.schoolName || '', session: response.session || '' })
      })
      .catch(() => {})
    return () => { mounted = false }
  }, [])

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
    menuData, menuMeta, activeModule, activeGroup, activePage, moduleData, currentGroup, currentPage,
    isDrawerOpen, isDashboard, setModule, setGroup, setPage, goHome, toggleDrawer, closeDrawer,
  }
}
