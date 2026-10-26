import { Component, useState } from 'react'
import { useERP } from './hooks/useERP'
import { useTheme } from './hooks/useTheme'
import { useAuth } from './auth/AuthContext'
import Login from './components/auth/Login'
import Topbar from './components/layout/Topbar'
import MenuBar from './components/layout/MenuBar'
import Sidebar from './components/layout/Sidebar'
import MobileDrawer from './components/layout/MobileDrawer'
import Breadcrumb from './components/layout/Breadcrumb'
import Dashboard from './components/dashboard/Dashboard'
import PageRenderer from './components/pages/PageRenderer'
import SuperAdminLogin from './components/superadmin/SuperAdminLogin'
import SchoolSetup from './components/superadmin/SchoolSetup'
import { getSuperAdminPage, SuperAdminDashboard, SuperAdminModulePage, SuperAdminSchoolList, SuperAdminShell } from './components/superadmin/SuperAdminShell'
import SuperAdminPageMapping from './components/superadmin/SuperAdminPageMapping'
import { SUPER_ADMIN_TOKEN_KEY } from './lib/superAdminApi'

class PageErrorBoundary extends Component {
  state = { error: null }

  static getDerivedStateFromError(error) {
    return { error }
  }

  componentDidCatch(error, info) {
    console.error('Page render failed', error, info)
  }

  render() {
    if (!this.state.error) return this.props.children
    return <div className="m-4 rounded-xl border border-rose-300 bg-rose-50 p-5 text-rose-900 dark:border-rose-500/40 dark:bg-rose-950/30 dark:text-rose-100"><h2 className="font-semibold">Page render error</h2><p className="mt-2 text-sm">{this.state.error.message || String(this.state.error)}</p><pre className="mt-3 max-h-60 overflow-auto whitespace-pre-wrap text-xs">{this.state.error.stack}</pre></div>
  }
}
function ERPApp() {
  const { menuData, menuMeta, menuLoading, menuError, activeModule, activeGroup, activePage, moduleData, currentGroup, currentPage, isDrawerOpen, isDashboard, setModule, setGroup, setPage, goHome, toggleDrawer, closeDrawer } = useERP()
  const { isDark, toggleTheme } = useTheme(); const { user, logout } = useAuth()
  if (menuLoading) return <div className="flex min-h-screen items-center justify-center bg-slate-100 text-slate-600 dark:bg-[#0f1117] dark:text-slate-300"><div className="flex items-center gap-3 rounded-xl border border-slate-200 bg-white px-5 py-4 shadow-sm dark:border-slate-700 dark:bg-slate-900"><span className="h-5 w-5 animate-spin rounded-full border-2 border-indigo-200 border-t-indigo-600" />Loading permitted pages…</div></div>
  if (menuError || !menuData.length || !moduleData) return <div className="flex min-h-screen items-center justify-center bg-slate-100 p-5 dark:bg-[#0f1117]"><div className="max-w-md rounded-2xl border border-rose-200 bg-white p-6 text-center shadow-sm dark:border-rose-400/30 dark:bg-slate-900"><h2 className="font-semibold text-rose-700 dark:text-rose-300">Menu could not be loaded</h2><p className="mt-2 text-sm text-slate-600 dark:text-slate-300">{menuError || 'No permitted pages were returned for this account.'}</p><button onClick={() => window.location.reload()} className="mt-4 rounded-lg bg-indigo-600 px-4 py-2 text-sm font-semibold text-white">Retry</button></div></div>
  const moduleLabel = isDashboard ? 'Dashboard' : moduleData?.label; const groupLabel = isDashboard ? null : currentGroup?.label; const pageLabel = isDashboard ? null : currentPage?.label
  return <div className="flex flex-col h-screen overflow-hidden bg-slate-100 dark:bg-[#0f1117] transition-colors duration-300">
    <Topbar isDrawerOpen={isDrawerOpen} toggleDrawer={toggleDrawer} goHome={goHome} isDark={isDark} toggleTheme={toggleTheme} user={user} menuMeta={menuMeta} onLogout={logout} />
    <MenuBar menuData={menuData} activeModule={activeModule} onModuleClick={setModule} />
    <div className="flex flex-1 overflow-hidden"><Sidebar moduleData={moduleData} activeGroup={activeGroup} activePage={activePage} onGroupClick={setGroup} onPageClick={setPage} />
      <div className="flex flex-col flex-1 overflow-hidden"><Breadcrumb moduleLabel={moduleLabel} groupLabel={groupLabel} pageLabel={pageLabel} onHome={goHome} />
        <main className="flex-1 overflow-y-auto overflow-x-hidden bg-slate-100 dark:bg-[#0f1117] p-4 md:p-5 transition-colors duration-300">{isDashboard ? <Dashboard onPageClick={setPage} user={user} menuMeta={menuMeta} /> : <PageErrorBoundary><PageRenderer moduleData={moduleData} currentGroup={currentGroup} currentPage={currentPage} onPageClick={setPage} /></PageErrorBoundary>}</main>
      </div>
    </div>
    <MobileDrawer menuData={menuData} schoolName={user?.schoolName || menuMeta?.schoolName} logoPath={user?.logoPath} isOpen={isDrawerOpen} onClose={closeDrawer} activeModule={activeModule} activeGroup={activeGroup} activePage={activePage} onModuleClick={setModule} onPageClick={setPage} />
  </div>
}
function SuperAdminApp() {
  const [user, setUser] = useState(() => { try { return JSON.parse(localStorage.getItem('schoolErp.superAdminUser') || 'null') } catch { return null } })
  const [activeKey, setActiveKey] = useState(() => window.location.pathname.split('/')[2] || 'dashboard')
  const loggedIn = Boolean(localStorage.getItem(SUPER_ADMIN_TOKEN_KEY))
  if (!loggedIn || !user) return <SuperAdminLogin onSuccess={next => { localStorage.setItem('schoolErp.superAdminUser', JSON.stringify(next)); setUser(next); setActiveKey('dashboard'); window.history.replaceState({}, '', '/superadmin/dashboard') }} />
  function navigate(key) { const page = getSuperAdminPage(key); setActiveKey(page.key); window.history.replaceState({}, '', `/superadmin/${page.key}`) }
  function logout() { localStorage.removeItem(SUPER_ADMIN_TOKEN_KEY); localStorage.removeItem('schoolErp.superAdminUser'); setUser(null); setActiveKey('dashboard'); window.history.replaceState({}, '', '/superadmin') }
  const page = getSuperAdminPage(activeKey)
  return <SuperAdminShell user={user} activeKey={page.key} onNavigate={navigate} onLogout={logout}>
    {page.key === 'dashboard' && <SuperAdminDashboard onNavigate={navigate} />}
    {page.key === 'school-setup' && <SchoolSetup user={user} embedded />}
    {page.key === 'school-list' && <SuperAdminSchoolList />}
    {page.key === 'page-mapping' && <SuperAdminPageMapping />}
    {!['dashboard', 'school-setup', 'school-list', 'page-mapping'].includes(page.key) && <SuperAdminModulePage page={page} />}
  </SuperAdminShell>
}

export default function App() {
  const { user, loading } = useAuth()
  if (window.location.pathname.startsWith('/superadmin')) return <SuperAdminApp />
  if (loading) return <div className="min-h-screen flex items-center justify-center bg-slate-100 text-slate-500">Loading...</div>
  return user ? <ERPApp /> : <Login />
}
