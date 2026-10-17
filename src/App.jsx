import { Component } from 'react'
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
  const { menuData, menuMeta, activeModule, activeGroup, activePage, moduleData, currentGroup, currentPage, isDrawerOpen, isDashboard, setModule, setGroup, setPage, goHome, toggleDrawer, closeDrawer } = useERP()
  const { isDark, toggleTheme } = useTheme(); const { user, logout } = useAuth()
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
export default function App() { const { user, loading } = useAuth(); if (loading) return <div className="min-h-screen flex items-center justify-center bg-slate-100 text-slate-500">Loading...</div>; return user ? <ERPApp /> : <Login /> }
