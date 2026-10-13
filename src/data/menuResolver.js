import { MENU_DATA } from './menuData'

const key = (value) => String(value || '').toLowerCase().replace(/[^a-z0-9]/g, '')
const moduleAliases = {
  examination: 'examMaster',
  fees: 'fee',
  staffmanagement: 'staff',
  admission: 'student',
  webadmin: 'website',
  schooladmin: 'masterSetting',
  expense: 'canteen',
  tuckshop: 'canteen',
}
const moduleIdAliases = {
  1: 'examMaster', 4: 'fee', 5: 'transport', 10: 'library', 11: 'timetable',
  14: 'certificates', 16: 'communication', 19: 'attendance', 24: 'leave',
  28: 'canteen', 31: 'website', 32: 'masterSetting', 6: 'hostel',
}

const findStaticModule = (module) => {
  const preferredId = moduleIdAliases[module.moduleId] || moduleAliases[key(module.label)]
  return MENU_DATA.find((item) => item.id === preferredId)
    || MENU_DATA.find((item) => key(item.label) === key(module.label))
}

const findStaticGroup = (staticModule, group) => {
  if (!staticModule) return null
  return staticModule.groups.find((item) => key(item.label) === key(group.label))
    || staticModule.groups.find((item) => key(item.label) === 'work' && key(group.label) === 'transaction')
    || staticModule.groups.find((item) => key(item.label) === 'setup' && key(group.label) === 'setting')
}

const pageFileKey = (page) => key(page.pageKey || page.url?.split(/[/?#]/).pop()?.replace(/\.aspx$/i, ''))
const findStaticPage = (staticModule, staticGroup, page) => {
  const candidates = [
    ...(staticGroup?.pages || []),
    ...(staticModule?.groups || []).flatMap((group) => group.pages || []),
    ...MENU_DATA.flatMap((module) => module.groups.flatMap((group) => group.pages || [])),
  ]
  const pageKey = pageFileKey(page)
  return candidates.find((item) => key(item.id) === pageKey)
    || candidates.find((item) => key(item.label) === key(page.label))
}

export function resolveMenu(serverMenu) {
  const modules = (serverMenu?.modules || []).map((module) => {
    const staticModule = findStaticModule(module)
    const groups = (module.groups || []).map((group) => {
      const staticGroup = findStaticGroup(staticModule, group)
      const pages = (group.pages || []).map((page) => {
        const staticPage = findStaticPage(staticModule, staticGroup, page)
        return {
          ...page,
          id: staticPage?.id || page.id || ('db-page-' + page.pageId),
          label: page.label || staticPage?.label || 'Page',
        }
      })
      return {
        ...group,
        id: staticGroup?.id || ('db-group-' + module.moduleId + '-' + group.id),
        icon: group.icon || staticGroup?.icon || 'Folder',
        pages,
      }
    })
    return {
      ...module,
      id: staticModule?.id || ('db-module-' + module.moduleId),
      label: module.label || staticModule?.label || 'Module',
      groups,
    }
  })
  return modules.length ? modules : MENU_DATA
}
