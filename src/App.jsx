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
function ERPApp() {
  const { menuData, menuMeta, activeModule, activeGroup, activePage, moduleData, currentGroup, currentPage, isDrawerOpen, isDashboard, setModule, setGroup, setPage, goHome, toggleDrawer, closeDrawer } = useERP()
  const { isDark, toggleTheme } = useTheme(); const { user, logout } = useAuth()
  const moduleLabel = isDashboard ? 'Dashboard' : moduleData?.label; const groupLabel = isDashboard ? null : currentGroup?.label; const pageLabel = isDashboard ? null : currentPage?.label
  return <div className="flex flex-col h-screen overflow-hidden bg-slate-100 dark:bg-[#0f1117] transition-colors duration-300">
    <Topbar isDrawerOpen={isDrawerOpen} toggleDrawer={toggleDrawer} goHome={goHome} isDark={isDark} toggleTheme={toggleTheme} user={user} menuMeta={menuMeta} onLogout={logout} />
    <MenuBar menuData={menuData} activeModule={activeModule} onModuleClick={setModule} />
    <div className="flex flex-1 overflow-hidden"><Sidebar moduleData={moduleData} activeGroup={activeGroup} activePage={activePage} onGroupClick={setGroup} onPageClick={setPage} />
      <div className="flex flex-col flex-1 overflow-hidden"><Breadcrumb moduleLabel={moduleLabel} groupLabel={groupLabel} pageLabel={pageLabel} onHome={goHome} />
        <main className="flex-1 overflow-y-auto overflow-x-hidden bg-slate-100 dark:bg-[#0f1117] p-4 md:p-5 transition-colors duration-300">{isDashboard ? <Dashboard onPageClick={setPage} user={user} menuMeta={menuMeta} /> : <PageRenderer moduleData={moduleData} currentGroup={currentGroup} currentPage={currentPage} onPageClick={setPage} />}</main>
      </div>
    </div>
    <MobileDrawer menuData={menuData} schoolName={user?.schoolName || menuMeta?.schoolName} logoPath={user?.logoPath} isOpen={isDrawerOpen} onClose={closeDrawer} activeModule={activeModule} activeGroup={activeGroup} activePage={activePage} onModuleClick={setModule} onPageClick={setPage} />
  </div>
}
export default function App() { const { user, loading } = useAuth(); if (loading) return <div className="min-h-screen flex items-center justify-center bg-slate-100 text-slate-500">Loading...</div>; return user ? <ERPApp /> : <Login /> }
