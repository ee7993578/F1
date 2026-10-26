import { useEffect, useMemo, useState } from 'react'
import {
  Activity,
  ArchiveRestore,
  BarChart3,
  BellRing,
  BookOpenCheck,
  Building2,
  ChevronDown,
  ClipboardList,
  DatabaseBackup,
  FileSpreadsheet,
  Gauge,
  Grid2X2,
  History,
  KeyRound,
  LayoutDashboard,
  LogOut,
  Menu,
  PanelLeftClose,
  PanelLeftOpen,
  ReceiptText,
  RefreshCw,
  School,
  Settings2,
  ShieldCheck,
  SlidersHorizontal,
  UserCog,
  UsersRound,
  X,
} from 'lucide-react'
import { superAdminFetch } from '../../lib/superAdminApi'
import SchoolSetup from './SchoolSetup'

const menu = [
  { section: 'Overview', items: [{ key: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, description: 'Super Admin overview and quick actions' }] },
  {
    section: 'School Management',
    items: [
      { key: 'school-setup', label: 'School Setup', icon: School, description: 'Create, update and configure a school' },
      { key: 'school-list', label: 'School List', icon: Building2, description: 'View and manage registered schools' },
      { key: 'subscription', label: 'Subscription / License', icon: ReceiptText, description: 'Manage school plans and license status' },
    ],
  },
  {
    section: 'Access Control',
    items: [
      { key: 'users', label: 'Users', icon: UsersRound, description: 'Manage school administrators and users' },
      { key: 'roles', label: 'Roles', icon: UserCog, description: 'Create and manage role templates' },
      { key: 'permissions', label: 'Permissions', icon: ShieldCheck, description: 'Configure role permissions' },
      { key: 'page-mapping', label: 'Page Mapping', icon: Grid2X2, description: 'Map modules and pages to schools' },
    ],
  },
  {
    section: 'Data Management',
    items: [
      { key: 'student-import', label: 'Student Import', icon: FileSpreadsheet, description: 'Import and validate student data' },
      { key: 'staff-import', label: 'Staff Import', icon: BookOpenCheck, description: 'Import and validate staff data' },
      { key: 'backup-restore', label: 'Backup & Restore', icon: DatabaseBackup, description: 'Manage school data backups' },
    ],
  },
  {
    section: 'System',
    items: [
      { key: 'global-settings', label: 'Global Settings', icon: SlidersHorizontal, description: 'Configure global ERP defaults' },
      { key: 'notifications', label: 'Notifications', icon: BellRing, description: 'Manage notification templates' },
      { key: 'audit-logs', label: 'Audit Logs', icon: History, description: 'Review Super Admin activity' },
      { key: 'reports', label: 'Reports', icon: BarChart3, description: 'View platform and school reports' },
      { key: 'system-health', label: 'System Health', icon: Activity, description: 'Monitor API and database health' },
    ],
  },
]

const allItems = menu.flatMap(group => group.items)
const toneClasses = {
  cyan: 'bg-cyan-50 text-cyan-700',
  indigo: 'bg-indigo-50 text-indigo-700',
  violet: 'bg-violet-50 text-violet-700',
  emerald: 'bg-emerald-50 text-emerald-700',
}

export function getSuperAdminPage(key) {
  return allItems.find(item => item.key === key) || allItems[0]
}

export function SuperAdminShell({ user, activeKey, onNavigate, onLogout, children }) {
  const [mobileOpen, setMobileOpen] = useState(false)
  const [collapsed, setCollapsed] = useState(false)
  const active = getSuperAdminPage(activeKey)

  function navigate(key) {
    onNavigate(key)
    setMobileOpen(false)
  }

  return <div className="min-h-screen bg-slate-100 text-slate-900">
    <header className="sticky top-0 z-40 border-b border-slate-200 bg-white/95 shadow-sm backdrop-blur">
      <div className="flex h-16 items-center justify-between gap-3 px-4 sm:px-6">
        <div className="flex min-w-0 items-center gap-3">
          <button onClick={() => setMobileOpen(true)} className="rounded-xl border border-slate-200 p-2 text-slate-600 lg:hidden" aria-label="Open menu"><Menu size={20} /></button>
          <div className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-slate-950 text-sm font-black text-white shadow-lg shadow-slate-950/15">E</div>
          <div className="min-w-0"><p className="truncate text-sm font-black tracking-tight sm:text-base">Edufox Super Admin</p><p className="hidden text-[11px] text-slate-500 sm:block">Central control center</p></div>
        </div>
        <div className="flex items-center gap-2 sm:gap-4">
          <div className="hidden text-right sm:block"><p className="text-xs font-bold">{user?.username || 'Super Admin'}</p><p className="text-[11px] text-emerald-600">Protected access</p></div>
          <button onClick={onLogout} className="inline-flex items-center gap-1.5 rounded-xl border border-slate-200 px-3 py-2 text-xs font-bold text-slate-600 transition hover:border-rose-200 hover:bg-rose-50 hover:text-rose-700"><LogOut size={14} /> <span className="hidden sm:inline">Logout</span></button>
        </div>
      </div>
    </header>

    <div className="flex min-h-[calc(100vh-4rem)]">
      {mobileOpen && <button aria-label="Close menu" onClick={() => setMobileOpen(false)} className="fixed inset-0 z-40 bg-slate-950/40 lg:hidden" />}
      <aside className={`fixed inset-y-0 left-0 z-50 mt-16 flex w-[285px] flex-col border-r border-slate-200 bg-white transition-transform duration-200 lg:sticky lg:top-16 lg:z-20 lg:mt-0 lg:h-[calc(100vh-4rem)] lg:translate-x-0 ${mobileOpen ? 'translate-x-0' : '-translate-x-full'} ${collapsed ? 'lg:w-[82px]' : ''}`}>
        <div className="flex items-center justify-between border-b border-slate-100 px-4 py-3">
          {!collapsed && <div><p className="text-[11px] font-black uppercase tracking-[0.18em] text-cyan-700">Navigation</p><p className="mt-0.5 text-xs text-slate-500">Platform administration</p></div>}
          <button onClick={() => { setCollapsed(value => !value); setMobileOpen(false) }} className="hidden rounded-lg p-2 text-slate-500 hover:bg-slate-100 lg:block" aria-label="Toggle sidebar">{collapsed ? <PanelLeftOpen size={18} /> : <PanelLeftClose size={18} />}</button>
          <button onClick={() => setMobileOpen(false)} className="rounded-lg p-2 text-slate-500 hover:bg-slate-100 lg:hidden" aria-label="Close menu"><X size={18} /></button>
        </div>
        <nav className="flex-1 overflow-y-auto px-3 py-3">
          {menu.map(group => <div key={group.section} className="mb-4">
            {!collapsed && <p className="mb-1 px-3 text-[10px] font-black uppercase tracking-[0.16em] text-slate-400">{group.section}</p>}
            {group.items.map(item => { const Icon = item.icon; const selected = item.key === activeKey; return <button key={item.key} title={collapsed ? item.label : undefined} onClick={() => navigate(item.key)} className={`group mb-1 flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-left text-sm transition ${selected ? 'bg-cyan-700 font-bold text-white shadow-md shadow-cyan-700/20' : 'font-semibold text-slate-600 hover:bg-slate-100 hover:text-slate-950'} ${collapsed ? 'justify-center' : ''}`}><Icon size={18} className={selected ? 'text-white' : 'text-slate-400 group-hover:text-cyan-700'} /><span className={collapsed ? 'sr-only' : 'min-w-0 truncate'}>{item.label}</span>{!collapsed && selected && <ChevronDown size={14} className="ml-auto rotate-[-90deg] opacity-70" />}</button> })}
          </div>)}
        </nav>
        {!collapsed && <div className="m-3 rounded-2xl border border-cyan-100 bg-cyan-50 p-3"><div className="flex items-center gap-2 text-cyan-800"><KeyRound size={15} /><strong className="text-xs">Secure session</strong></div><p className="mt-1 text-[11px] leading-5 text-cyan-900/70">Only Super Admin APIs are available in this control center.</p></div>}
      </aside>

      <main className="min-w-0 flex-1 overflow-x-hidden">
        <div className="border-b border-slate-200 bg-white px-4 py-4 sm:px-6"><div className="mx-auto flex max-w-7xl items-center justify-between gap-3"><div><p className="text-xs font-bold text-slate-400">Super Admin / {active?.label}</p><h1 className="mt-1 text-xl font-black tracking-tight sm:text-2xl">{active?.label}</h1><p className="mt-1 text-xs text-slate-500 sm:text-sm">{active?.description}</p></div><div className="hidden rounded-xl bg-slate-100 px-3 py-2 text-xs font-semibold text-slate-500 md:flex md:items-center md:gap-2"><Gauge size={15} className="text-cyan-700" /> Control center</div></div></div>
        <div className="mx-auto max-w-7xl">{children}</div>
      </main>
    </div>
  </div>
}

export function SuperAdminDashboard({ onNavigate }) {
  const [data, setData] = useState({ states: [], modules: [], roles: [], schools: [] })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  async function load() {
    setLoading(true); setError('')
    try { setData(await superAdminFetch('/superadmin/setup/bootstrap')) } catch (cause) { setError(cause.message) } finally { setLoading(false) }
  }
  useEffect(() => { load() }, [])

  const cards = useMemo(() => [
    { label: 'Registered schools', value: data.schools?.length || 0, icon: Building2, action: 'school-list', tone: 'cyan' },
    { label: 'Available modules', value: data.modules?.length || 0, icon: Grid2X2, action: 'school-setup', tone: 'indigo' },
    { label: 'Role templates', value: data.roles?.length || 0, icon: ShieldCheck, action: 'roles', tone: 'violet' },
    { label: 'States available', value: data.states?.length || 0, icon: SlidersHorizontal, action: 'global-settings', tone: 'emerald' },
  ], [data])

  return <div className="space-y-5 px-4 py-5 sm:px-6 sm:py-7">
    <div className="flex flex-col justify-between gap-3 md:flex-row md:items-end"><div><p className="text-xs font-black uppercase tracking-[0.2em] text-cyan-700">Platform overview</p><h2 className="mt-1 text-2xl font-black tracking-tight sm:text-3xl">Good to see you</h2><p className="mt-1 max-w-2xl text-sm text-slate-500">Manage schools, access, data and platform settings from one secure place.</p></div><button onClick={load} disabled={loading} className="inline-flex items-center justify-center gap-2 self-start rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs font-bold text-slate-600 hover:bg-slate-50 md:self-auto"><RefreshCw size={14} className={loading ? 'animate-spin' : ''} /> Refresh</button></div>
    {error && <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm font-semibold text-rose-700">{error}</div>}
    <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{cards.map(card => { const Icon = card.icon; return <button key={card.label} onClick={() => onNavigate(card.action)} className="group rounded-2xl border border-slate-200 bg-white p-4 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-cyan-200 hover:shadow-md sm:p-5"><div className="flex items-start justify-between"><span className={`grid h-10 w-10 place-items-center rounded-xl ${toneClasses[card.tone]}`}><Icon size={19} /></span><span className="text-[11px] font-bold text-slate-400 group-hover:text-cyan-700">Open →</span></div><p className="mt-5 text-2xl font-black">{loading ? '—' : card.value}</p><p className="mt-1 text-xs font-semibold text-slate-500">{card.label}</p></button> })}</div>
    <div className="grid gap-5 lg:grid-cols-[1.3fr_0.7fr]">
      <section className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6"><div className="flex items-center justify-between gap-3"><div><h3 className="font-black">Registered schools</h3><p className="mt-1 text-xs text-slate-500">Latest schools available in the control center.</p></div><button onClick={() => onNavigate('school-list')} className="text-xs font-bold text-cyan-700">View all</button></div><div className="mt-4 overflow-x-auto"><table className="w-full min-w-[520px] text-left text-sm"><thead><tr className="border-b border-slate-100 text-[11px] uppercase tracking-wide text-slate-400"><th className="px-2 py-2">School ID</th><th className="px-2 py-2">School name</th><th className="px-2 py-2">Status</th></tr></thead><tbody>{(data.schools || []).slice(0, 6).map(school => <tr key={school.schoolId} className="border-b border-slate-50"><td className="px-2 py-3 font-mono text-xs text-slate-500">{school.schoolId}</td><td className="px-2 py-3 font-semibold">{school.schoolName}</td><td className="px-2 py-3"><span className="rounded-full bg-emerald-50 px-2 py-1 text-[11px] font-bold text-emerald-700">{school.status || 'Active'}</span></td></tr>)}{!loading && !data.schools?.length && <tr><td colSpan="3" className="px-2 py-8 text-center text-sm text-slate-500">No schools found.</td></tr>}</tbody></table></div></section>
      <section className="rounded-2xl border border-slate-200 bg-slate-950 p-5 text-white shadow-sm sm:p-6"><div className="flex h-11 w-11 items-center justify-center rounded-xl bg-cyan-400 text-slate-950"><ShieldCheck size={21} /></div><h3 className="mt-5 text-lg font-black">Protected control center</h3><p className="mt-2 text-sm leading-6 text-slate-300">School ERP accounts remain isolated. Actions from this area use the Super Admin role and server-side authorization.</p><div className="mt-6 grid grid-cols-2 gap-2 text-xs"><div className="rounded-xl border border-white/10 bg-white/5 p-3"><strong className="block text-cyan-300">Direct API</strong><span className="mt-1 block text-slate-400">No legacy procedure calls</span></div><div className="rounded-xl border border-white/10 bg-white/5 p-3"><strong className="block text-cyan-300">Audit-ready</strong><span className="mt-1 block text-slate-400">Centralized access model</span></div></div></section>
    </div>
    <section className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6"><div className="flex items-center gap-2"><ClipboardList size={18} className="text-cyan-700" /><h3 className="font-black">Quick actions</h3></div><div className="mt-4 grid gap-3 sm:grid-cols-2 lg:grid-cols-4"><QuickAction label="Create or update school" icon={School} onClick={() => onNavigate('school-setup')} /><QuickAction label="Manage access control" icon={ShieldCheck} onClick={() => onNavigate('permissions')} /><QuickAction label="Import student data" icon={FileSpreadsheet} onClick={() => onNavigate('student-import')} /><QuickAction label="Check system health" icon={Activity} onClick={() => onNavigate('system-health')} /></div></section>
  </div>
}

function QuickAction({ label, icon: Icon, onClick }) { return <button onClick={onClick} className="flex items-center gap-3 rounded-xl border border-slate-200 p-3 text-left transition hover:border-cyan-200 hover:bg-cyan-50/40"><span className="grid h-9 w-9 place-items-center rounded-lg bg-cyan-50 text-cyan-700"><Icon size={17} /></span><span className="text-xs font-bold text-slate-700">{label}</span></button> }

export function SuperAdminSchoolList() {
  const [schools, setSchools] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  useEffect(() => { superAdminFetch('/superadmin/setup/bootstrap').then(data => setSchools(data.schools || [])).catch(cause => setError(cause.message)).finally(() => setLoading(false)) }, [])
  return <div className="space-y-5 px-4 py-5 sm:px-6 sm:py-7"><section className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:p-6"><div className="flex items-start justify-between gap-3"><div><h2 className="font-black">Registered schools</h2><p className="mt-1 text-xs text-slate-500">Schools currently available in the schoolmaster catalogue.</p></div><span className="rounded-full bg-cyan-50 px-3 py-1 text-xs font-bold text-cyan-700">{loading ? 'Loading...' : `${schools.length} schools`}</span></div>{error && <p className="mt-4 rounded-xl bg-rose-50 p-3 text-sm text-rose-700">{error}</p>}<div className="mt-5 overflow-x-auto"><table className="w-full min-w-[600px] text-left text-sm"><thead><tr className="border-b border-slate-100 text-[11px] uppercase tracking-wide text-slate-400"><th className="px-3 py-3">School ID</th><th className="px-3 py-3">School name</th><th className="px-3 py-3">Status</th><th className="px-3 py-3">Action</th></tr></thead><tbody>{schools.map(school => <tr key={school.schoolId} className="border-b border-slate-50"><td className="px-3 py-3 font-mono text-xs">{school.schoolId}</td><td className="px-3 py-3 font-semibold">{school.schoolName}</td><td className="px-3 py-3"><span className="rounded-full bg-emerald-50 px-2 py-1 text-[11px] font-bold text-emerald-700">{school.status || 'Active'}</span></td><td className="px-3 py-3 text-xs text-slate-400">Open School Setup to edit</td></tr>)}{!loading && !schools.length && <tr><td colSpan="4" className="px-3 py-10 text-center text-slate-500">No school found.</td></tr>}</tbody></table></div></section></div>
}

export function SuperAdminModulePage({ page }) {
  const Icon = page.icon
  const features = {
    subscription: ['School plan and license status', 'Activation and expiry tracking', 'Plan-wise school overview'],
    users: ['School administrator management', 'User activation and password reset', 'School-wise account isolation'],
    roles: ['Role templates', 'Designation mapping', 'Role activation and deactivation'],
    permissions: ['Module permissions', 'Page-level permissions', 'Role-wise access review'],
    'page-mapping': ['Module to page mapping', 'Default page provisioning', 'School-wise mapping review'],
    'student-import': ['Excel upload and validation', 'Duplicate and required-field checks', 'Import result summary'],
    'staff-import': ['Staff Excel upload', 'Validation before save', 'Import result summary'],
    'backup-restore': ['School-wise backup list', 'Restore controls', 'Backup activity history'],
    'global-settings': ['Global ERP defaults', 'Common master configuration', 'Safe configuration publishing'],
    notifications: ['SMS and email templates', 'Notification provider settings', 'Delivery history'],
    'audit-logs': ['Login and action history', 'User and school filters', 'Exportable audit records'],
    reports: ['Platform summary reports', 'School-wise usage reports', 'Excel export-ready views'],
    'system-health': ['API health', 'Database connectivity', 'Runtime and storage status'],
  }
  return <div className="space-y-5 px-4 py-5 sm:px-6 sm:py-7"><section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-7"><div className="flex flex-col gap-4 sm:flex-row sm:items-start"><span className="grid h-12 w-12 shrink-0 place-items-center rounded-2xl bg-cyan-50 text-cyan-700"><Icon size={23} /></span><div><h2 className="text-xl font-black">{page.label}</h2><p className="mt-1 max-w-2xl text-sm leading-6 text-slate-500">{page.description}. This section is available in the Super Admin navigation and is ready for its protected workflow.</p></div></div><div className="mt-7 grid gap-3 md:grid-cols-3">{(features[page.key] || ['Centralized management', 'Server-side authorization', 'School-wise data isolation']).map(feature => <div key={feature} className="rounded-xl border border-slate-200 bg-slate-50 p-4"><strong className="text-sm">{feature}</strong><p className="mt-1 text-xs text-slate-500">Configured as a dedicated Super Admin capability.</p></div>)}</div></section><section className="rounded-2xl border border-dashed border-slate-300 bg-white p-5 text-sm text-slate-600 shadow-sm"><div className="flex items-start gap-3"><Settings2 size={18} className="mt-0.5 shrink-0 text-cyan-700" /><div><strong>Workflow boundary</strong><p className="mt-1 leading-6">The menu and page shell are now separated from School Setup. Existing school configuration remains available through the School Setup page; this section can be wired to its own controller, service and repository without changing the navigation contract.</p></div></div></section></div>
}

