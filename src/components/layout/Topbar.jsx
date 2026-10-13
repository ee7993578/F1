import { LayoutDashboard, Settings, Menu, X, Sun, Moon, LogOut } from 'lucide-react'
import { cn } from '../../lib/utils'

export default function Topbar({ isDrawerOpen, toggleDrawer, goHome, isDark, toggleTheme, user, menuMeta, onLogout }) {
  const displayName = user?.username || 'User'
  const initials = displayName.split(' ').filter(Boolean).slice(0, 2).map((x) => x[0]).join('').toUpperCase() || 'U'
  const schoolName = user?.schoolName || menuMeta?.schoolName || 'APS INTERNATIONAL SCHOOL'
  const session = menuMeta?.session || '2026-2027'

  return <header className="relative flex-shrink-0 h-[58px] bg-gradient-to-r from-primary-950 via-primary-900 to-[#1e2d6b] dark:from-[#0d1117] dark:via-[#151929] dark:to-[#0d1117] flex items-center justify-between pl-0 pr-2 sm:pr-3 sticky top-0 z-[200] topbar-shimmer" style={{ boxShadow: isDark ? '0 2px 20px rgba(0,0,0,0.6)' : '0 2px 12px rgba(30,58,138,0.3)' }}>
    <div className="flex items-center h-full min-w-0 flex-1 overflow-hidden">
      <div className="flex items-center gap-2 px-3 sm:px-4 h-full min-w-0 max-w-full bg-white/[0.08] border-r border-white/[0.12]">
        <div className="w-9 h-9 flex-shrink-0 bg-white rounded-lg flex items-center justify-center font-bold text-[13px] text-primary-900 cursor-pointer" onClick={goHome}>APS</div>
        <span className="font-bold text-[14.5px] text-white whitespace-nowrap overflow-hidden text-ellipsis min-w-0 max-w-[calc(100vw-155px)] sm:max-w-[42vw]">{schoolName}</span>
      </div>
    </div>

    <div className="flex items-center gap-1 sm:gap-2 flex-shrink-0">
      <span className="hidden md:block text-[12px] text-white/75 px-2.5 py-1 bg-white/10 rounded-full font-medium border border-white/[0.08]">SESSION: {session}</span>
      <div className="flex items-center gap-2 px-2 py-1 bg-white/10 rounded-full border border-white/[0.08] flex-shrink-0">
        <div className="w-[30px] h-[30px] rounded-full bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center text-[12px] font-bold text-white">{initials}</div>
        <div className="leading-tight hidden sm:block"><div className="text-[12.5px] font-semibold text-white">{displayName}</div><div className="text-[11px] text-white/65">{user?.typeStatus || 'User'}</div></div>
      </div>
      <button onClick={goHome} className="hidden md:flex flex-shrink-0 w-[34px] h-[34px] rounded-lg items-center justify-center bg-white/10 text-white hover:bg-white/20" title="Dashboard"><LayoutDashboard size={17} /></button>
      <button className="hidden md:flex flex-shrink-0 w-[34px] h-[34px] rounded-lg items-center justify-center bg-white/10 text-white hover:bg-white/20" title="Settings"><Settings size={17} /></button>
      <button onClick={onLogout} className="hidden sm:flex flex-shrink-0 w-[34px] h-[34px] rounded-lg items-center justify-center bg-white/10 text-white hover:bg-red-500/80" title="Logout"><LogOut size={17} /></button>
      <button onClick={toggleTheme} className="theme-toggle flex-shrink-0 flex items-center" style={{ background: isDark ? 'linear-gradient(135deg,#1e1b4b,#312e81)' : 'linear-gradient(135deg,#bfdbfe,#93c5fd)' }} title="Toggle theme" aria-label="Toggle theme"><div className={cn('theme-toggle-thumb', isDark ? 'theme-toggle-dark' : 'theme-toggle-light')}>{isDark ? <Moon size={12} className="text-indigo-100" /> : <Sun size={12} className="text-amber-900" />}</div></button>
      <button onClick={toggleDrawer} className="md:hidden flex-shrink-0 flex w-[38px] h-[38px] rounded-lg items-center justify-center bg-white/10 text-white" aria-label="Toggle menu">{isDrawerOpen ? <X size={20} /> : <Menu size={20} />}</button>
    </div>
  </header>
}
