import assert from 'node:assert/strict'
import { build } from 'esbuild'
import { studentSectionOptions } from '../src/lib/studentSections.js'
process.on('uncaughtException', error => { console.error(error.name + ': ' + error.message); process.exit(1) })

// Execute actual page components with a small hook harness. No browser or DB writes.
const session = '2025 - 2026'
const sections = [
  { classId: 10, className: 'V', sectionId: 102, sectionName: 'B' },
  { classId: 10, className: 'V', sectionId: 101, sectionName: 'A' },
]
const students = [{ uid: 'u1', id: 1, studentId: 1, sectionId: 101, className: 'V', sectionName: 'A', session,
  registrationNo: 'ADM1', feeBookNo: 'BOOK1', name: 'Test Student', firstName: 'Test', lastName: 'Student', status: 'Registered' }]
const bootstrap = { classes: [{ classId: 10, className: 'V' }], sections, sessions: [{ session, status: 'default' }, { session: '2024 - 2025' }], students: [] }
assert.deepEqual(studentSectionOptions(sections).map(x => [x.id, x.name]), [['101', 'V (A)'], ['102', 'V (B)']])

function walk(node, predicate) {
  if (Array.isArray(node)) return node.flatMap(child => walk(child, predicate))
  if (!node || typeof node !== 'object') return []
  return [...(predicate(node) ? [node] : []), ...walk(node.props?.children, predicate)]
}
function textOf(node) {
  if (Array.isArray(node)) return node.map(textOf).join('')
  if (node == null || typeof node === 'boolean') return ''
  return typeof node === 'object' ? textOf(node.props?.children) : String(node)
}
const tick = () => new Promise(resolve => setImmediate(resolve))
const calls = []
let apiOverride
globalThis.__studentTest = {
  useStudents: () => ({ data: bootstrap, loading: false, error: null }),
  fetchStudentSearch: async params => { calls.push({ kind: 'search', params }); return students },
  fetchStudentReport: async (type, params) => { calls.push({ kind: type, params }); return students },
  normalizeStudent: row => row,
  apiFetch: async (url, options) => {
    calls.push({ kind: 'api', url, options })
    if (apiOverride) return apiOverride(url, options)
    if (url.startsWith('/students/image-upload/sections')) return sections
    if (url.startsWith('/students/image-upload/students') || url.startsWith('/students/infosheet-lookup')) return students
    return {}
  },
}

async function mount(name, props = {}) {
  const result = await build({
    entryPoints: [`src/components/pages/Student/Update/${name}.jsx`], bundle: true, write: false,
    format: 'esm', platform: 'node', jsx: 'automatic', plugins: [{ name: 'page-test', setup(b) {
      b.onResolve({ filter: /^react(?:\/jsx-runtime)?$/ }, args => ({ path: args.path, namespace: 'mock' }))
      b.onResolve({ filter: /(?:hooks\/useStudents|lib\/api)$/ }, args => ({ path: args.path, namespace: 'mock' }))
      b.onLoad({ filter: /.*/, namespace: 'mock' }, ({ path }) => {
        if (path === 'react/jsx-runtime') return { contents: 'export const Fragment="fragment"; export const jsx=(type,props)=>({type,props}); export const jsxs=jsx;' }
        if (path === 'react') return { contents: `export const useState=(...a)=>globalThis.__hooks.useState(...a), useRef=(...a)=>globalThis.__hooks.useRef(...a), useEffect=(...a)=>globalThis.__hooks.useEffect(...a), useMemo=f=>f(), useCallback=f=>f, forwardRef=f=>f, createElement=(type,props,...children)=>({type,props:{...props,children}}); export default {createElement};` }
        const exports = path.includes('useStudents') ? ['useStudents', 'fetchStudentSearch', 'fetchStudentReport', 'normalizeStudent'] : ['apiFetch']
        return { contents: exports.map(n => `export const ${n}=(...a)=>globalThis.__studentTest.${n}(...a);`).join('\n') }
      })
    }}],
  })
  const { default: Page } = await import('data:text/javascript;base64,' + Buffer.from(result.outputFiles[0].text).toString('base64'))
  const state = [], effects = [], refs = []
  let index = 0, pending = [], tree
  globalThis.__hooks = {
    useState(initial) { const i = index++; if (!(i in state)) state[i] = typeof initial === 'function' ? initial() : initial; return [state[i], next => { state[i] = typeof next === 'function' ? next(state[i]) : next }] },
    useRef(initial) { const i = index++; return refs[i] ||= { current: initial } },
    useEffect(fn, deps) {
      const i = index++, previous = effects[i]
      if (!previous || deps.some((d, n) => !Object.is(d, previous.deps[n]))) pending.push(() => {
        previous?.cleanup?.()
        const cleanup = fn()
        assert.ok(cleanup === undefined || typeof cleanup === 'function', 'Effects must not return promises (destroy is not a function)')
        effects[i] = { deps, cleanup }
      })
    },
  }
  const render = async () => {
    for (let n = 0; n < 3; n++) { index = 0; tree = Page(props); const jobs = pending; pending = []; jobs.forEach(f => f()); await tick() }
  }
  await render()
  return {
    render, tree: () => tree,
    field(label) {
      const fields = walk(tree, n => n.props?.label === label)
      assert.ok(fields.length, `${name}: missing ${label}`)
      for (const field of fields) {
        const input = walk(field, n => typeof n.props?.onChange === 'function')[0]
        if (input) return input
      }
      throw Error(`Missing input: ${label}`)
    },
    async change(label, value) { this.field(label).props.onChange({ target: { value } }); await render() },
    async click(label) {
      const button = walk(tree, n => n.type === 'button' && textOf(n).trim() === label)[0]
      assert.ok(button, `${name}: missing button ${label}`)
      await button.props.onClick(); await render()
    },
    close() { effects.forEach(e => e?.cleanup?.()) },
  }
}

for (const [page, button, kind] of [
  ['ChangeAdmNo', 'Show Students', 'search'],
  ['StudentWithdrawn', 'Show', 'withdrawn-management'],
  ['UpdateStudentInfo', 'View Record', 'student-list'],
]) {
  calls.length = 0
  const view = await mount(page)
  assert.deepEqual(walk(view.field('Class'), n => n.type === 'option').filter(n => n.props.value).map(n => n.props.value), ['101', '102'])
  await view.change('Class', '101'); await view.click(button)
  const call = calls.findLast(c => c.kind === kind)
  assert.equal(call.params.sectionId, '101'); assert.equal(call.params.session, session); assert.equal(call.params.classId, undefined)
  if (page !== 'StudentWithdrawn') assert.equal(call.params.status, 'Registered')
  if (page !== 'ChangeAdmNo') {
    await view.change('Class', ''); await view.click(button)
    assert.equal(calls.findLast(c => c.kind === kind).params.sectionId, undefined)
  }
  view.close(); console.log(`PASS ${page}: section options, session and request binding`)
}

for (const page of ['UpdateStudentInfosheet', 'UpdateStudentInfoWithdrawn']) {
  calls.length = 0
  // The withdrawn page is a wrapper, so render its shared implementation with the prop.
  const withdrawn = page === 'UpdateStudentInfoWithdrawn'
  const view = await mount('UpdateStudentInfosheet', { withdrawnOnly: withdrawn })
  await view.change('Class', '101')
  assert.equal(calls.findLast(c => c.kind === 'search').params.sectionId, '101')
  assert.equal(calls.findLast(c => c.kind === 'search').params.session, session)
  assert.equal(calls.findLast(c => c.kind === 'search').params.status, withdrawn ? 'Withdrawn' : 'Registered')
  await view.change('Student', 'u1')
  assert.equal(view.field('Class').props.value, '101')
  assert.equal(view.field('Session').props.value, session)
  // Capture the save payload without writing to a database.
  await view.click('Update')
  const saved = JSON.parse(calls.findLast(c => c.options?.method === 'PUT').options.body)
  assert.equal(saved.sectionId, 101); assert.equal(saved.className, 'V'); assert.equal(saved.session, session)
  view.close()
  const resetView = await mount('UpdateStudentInfosheet', { withdrawnOnly: withdrawn })
  await resetView.change('Class', '101')
  await resetView.change('Student', 'u1')
  await resetView.change('Session', '2024 - 2025')
  assert.equal(resetView.field('Class').props.value, '')
  assert.equal(resetView.field('Student').props.value, '')
  await resetView.change('Fee Book No.', 'BOOK1')
  const lookup = new URL(calls.findLast(c => c.url?.includes('infosheet-lookup')).url, 'http://local')
  assert.equal(lookup.searchParams.get('session'), '2024 - 2025'); assert.equal(lookup.searchParams.get('feeBookNo'), 'BOOK1')
  assert.equal(lookup.searchParams.get('withdrawn'), String(withdrawn))
  resetView.close(); console.log(`PASS ${page}: student reload, section save payload, session reset, exact Fee Book lookup`)
}

calls.length = 0
const upload = await mount('UploadStudentImage')
assert.ok(calls.some(c => c.url?.startsWith('/students/image-upload/sections?')))
await upload.change('Class', '101'); await upload.click('View')
assert.ok(calls.some(c => c.url?.includes('sectionId=101') && c.url.includes('image-upload/students')))
await upload.change('Session', '2024 - 2025')
assert.equal(upload.field('Class').props.value, '')
assert.ok(calls.findLast(c => c.url?.includes('image-upload/sections')).url.includes('2024'))
upload.close()
console.log('PASS UploadStudentImage: staff/session options, section request, session reset')

const originalSearch = globalThis.__studentTest.fetchStudentSearch
let resolveOldSection
globalThis.__studentTest.fetchStudentSearch = params => params.sectionId === '101'
  ? new Promise(resolve => { resolveOldSection = resolve })
  : Promise.resolve([{ ...students[0], uid: 'u2', studentId: 2, sectionId: 102 }])
const race = await mount('UpdateStudentInfosheet')
await race.change('Class', '101')
await race.change('Class', '102')
resolveOldSection(students); await tick(); await race.render()
assert.deepEqual(walk(race.field('Student'), n => n.type === 'option').map(n => n.props.value), ['u2'])
race.close(); globalThis.__studentTest.fetchStudentSearch = originalSearch
console.log('PASS Infosheet: late response from previous section is ignored')

apiOverride = async () => { throw new Error('Class options unavailable') }
const unavailable = await mount('UploadStudentImage')
assert.ok(walk(unavailable.tree(), n => n.props?.role === 'alert').some(n => textOf(n).includes('Class options unavailable')))
assert.equal(walk(unavailable.tree(), n => n.type === 'button' && textOf(n).trim() === 'View')[0].props.disabled, true)
unavailable.close(); apiOverride = undefined
console.log('PASS UploadStudentImage: failed options show an error and cannot load all classes')
process.exit(0)
