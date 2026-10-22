import { useEffect, useRef, useState } from 'react'
import { apiFetch } from '../../../../lib/api'

const control = 'w-full min-w-0 rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-800 dark:border-slate-600 dark:bg-slate-800 dark:text-slate-100 disabled:opacity-60'
const panel = 'min-w-0 rounded-xl border border-slate-200 bg-white p-4 dark:border-slate-700 dark:bg-[#1a1f35]'
const button = 'rounded-lg bg-indigo-600 px-4 py-2 text-sm font-semibold text-white hover:bg-indigo-700 disabled:opacity-50'

function Dropdown({ label, value, onChange, options, disabled }) {
  return <label className="flex min-w-0 flex-col gap-2 text-sm font-medium text-slate-700 dark:text-slate-200">{label}
    <select className={control} value={value} disabled={disabled} onChange={e => onChange(e.target.value)}>
      <option value="">-- Select --</option>{options.map(o => <option key={o.id} value={o.id}>{o.label}</option>)}
    </select>
  </label>
}

export default function CreateVirtualClass() {
  const [options, setOptions] = useState(null)
  const [faculty, setFaculty] = useState(''), [subject, setSubject] = useState(''), [name, setName] = useState('')
  const [section, setSection] = useState(''), [available, setAvailable] = useState([]), [checked, setChecked] = useState([])
  const [members, setMembers] = useState([]), [classes, setClasses] = useState([]), [editId, setEditId] = useState(0)
  const [busy, setBusy] = useState(false), [studentLoading, setStudentLoading] = useState(false), [message, setMessage] = useState(null)
  const [revision, setRevision] = useState(0)
  const request = useRef(0)
  const fail = e => setMessage({ error: true, text: e.message })

  useEffect(() => {
    let active = true
    apiFetch('/timetable/virtual-class/options').then(data => {
      if (!active) return
      setOptions(data); setFaculty(data.facultyId ? String(data.facultyId) : '')
    }).catch(e => { if (active) fail(e) })
    return () => { active = false }
  }, [])

  useEffect(() => {
    let active = true
    setClasses([])
    if (faculty && options) apiFetch(`/timetable/virtual-class?${new URLSearchParams({ session: options.session, facultyId: faculty })}`)
      .then(data => { if (active) setClasses(data.rows || []) }).catch(e => { if (active) fail(e) })
    return () => { active = false }
  }, [faculty, options, revision])

  useEffect(() => {
    let active = true
    setAvailable([]); setChecked([]); setStudentLoading(false)
    if (section && subject && faculty) {
      setStudentLoading(true)
      apiFetch(`/timetable/virtual-class/students?${new URLSearchParams({ sectionId: section, subjectId: subject })}`)
        .then(data => { if (active) setAvailable(data) }).catch(e => { if (active) fail(e) })
        .finally(() => { if (active) setStudentLoading(false) })
    }
    return () => { active = false }
  }, [section, subject, faculty, revision])

  const changeFaculty = value => {
    request.current++; setFaculty(value); setEditId(0); setMembers([]); setAvailable([]); setChecked([]); setSection(''); setMessage(null)
  }
  const changeSubject = value => { setSubject(value); setChecked([]); setAvailable([]); setMessage(null) }
  const addStudents = () => {
    const additions = available.filter(s => checked.includes(s.id) && !members.some(m => m.id === s.id))
    setMembers(current => [...current, ...additions.map(s => ({ ...s, selected: true, locked: false }))])
    setChecked([]); setMessage(null)
  }
  const edit = async row => {
    const token = ++request.current
    setBusy(true); setMessage(null)
    try {
      const data = await apiFetch(`/timetable/virtual-class?${new URLSearchParams({ session: options.session, facultyId: faculty, vClassId: row.vClassId, subjectId: row.subjectId })}`)
      if (token !== request.current) return
      setEditId(row.vClassId); setName(row.vClassName); setSubject(String(row.subjectId)); setSection('')
      setMembers(data.students.map(s => ({ ...s, locked: Number(s.locked) === 1, selected: true })))
    } catch (e) { if (token === request.current) fail(e) }
    finally { if (token === request.current) setBusy(false) }
  }
  const save = async () => {
    setMessage(null)
    if (!faculty || !subject || !name.trim()) return fail(new Error('Please select faculty, subject and enter the virtual class name.'))
    if (!editId && (!section || !members.length)) return fail(new Error('Select a class and move students to Selected Student.'))
    setBusy(true)
    try {
      // Creation includes all moved rows; edit uses the right-hand checkboxes to remove members.
      await apiFetch('/timetable/virtual-class', { method: 'POST', body: JSON.stringify({
        session: options.session, facultyId: Number(faculty), subjectId: Number(subject), sectionId: Number(section),
        vClassId: editId, vClassName: name, studentIds: members.filter(s => !editId || s.selected || s.locked).map(s => s.id),
      }) })
      setMessage({ error: false, text: `Virtual Class: ${name} ${editId ? 'Recreated' : 'Created'} Successfully!` })
      setEditId(0); setName(''); setSubject(''); setSection(''); setMembers([]); setAvailable([]); setChecked([])
      if (options.canSelectFaculty) setFaculty('')
      setRevision(v => v + 1)
    } catch (e) { fail(e) } finally { setBusy(false) }
  }
  const candidates = available.filter(s => !members.some(m => m.id === s.id))

  return <div className="space-y-4 text-slate-800 dark:text-slate-100">
    {message && <div role={message.error ? 'alert' : 'status'} className={`rounded-lg border p-3 text-sm ${message.error ? 'border-rose-300 bg-rose-50 text-rose-800 dark:border-rose-800 dark:bg-rose-950 dark:text-rose-200' : 'border-emerald-300 bg-emerald-50 text-emerald-800 dark:border-emerald-800 dark:bg-emerald-950 dark:text-emerald-200'}`}>{message.text}</div>}
    {!options ? <div className={panel}>{message?.error ? 'Unable to load virtual class options.' : 'Loading virtual class...'}</div> : <>
      <section className={panel}>
        <h2 className="mb-4 text-base font-semibold">Create Virtual Class</h2>
        <div className="grid gap-4 md:grid-cols-3">
          <Dropdown label="Faculty" value={faculty} onChange={changeFaculty} options={options.faculties} disabled={!options.canSelectFaculty || busy}/>
          <Dropdown label="Select Subject" value={subject} onChange={changeSubject} options={options.subjects} disabled={busy}/>
          <label className="flex min-w-0 flex-col gap-2 text-sm font-medium">Enter Virtual Class Name<input className={control} value={name} maxLength={50} disabled={busy} onChange={e => setName(e.target.value)}/></label>
        </div>
      </section>
      {faculty && <section className={panel}>
        <div className="grid items-start gap-4 lg:grid-cols-[1fr_auto_1fr_1fr]">
          <div className="min-w-0 space-y-3">
            <Dropdown label="Select Class" value={section} onChange={setSection} options={options.sections} disabled={busy}/>
            <div aria-label="Available students" className="max-h-96 min-h-24 space-y-2 overflow-y-auto rounded-lg border border-slate-200 p-3 dark:border-slate-700">
              {studentLoading ? <p className="text-sm">Loading students...</p> : candidates.map(s => <label key={s.id} className="flex items-start gap-2 text-sm">
                <input type="checkbox" disabled={busy} checked={checked.includes(s.id)} onChange={e => setChecked(current => e.target.checked ? [...current, s.id] : current.filter(id => id !== s.id))} className="mt-1 shrink-0"/>
                <span className="break-words">{s.name} [{s.admissionNo}]</span>
              </label>)}
              {!studentLoading && !candidates.length && <p className="text-sm text-slate-500 dark:text-slate-400">{section && subject ? 'No eligible students.' : 'Select a subject and class to load students.'}</p>}
            </div>
          </div>
          <button className={`${button} lg:mt-16`} onClick={addStudents} disabled={busy || studentLoading || !checked.length} aria-label="Add selected students">Add →</button>
          <div className="min-w-0 space-y-3">
            <h3 className="text-sm font-semibold">Selected Student ({members.length})</h3>
            <div aria-label="Selected students" className="max-h-96 min-h-24 space-y-2 overflow-y-auto rounded-lg border border-slate-200 p-3 dark:border-slate-700">
              {members.map(s => <label key={s.id} className="flex items-start gap-2 text-sm">
                <input type="checkbox" checked={s.selected} disabled={busy || s.locked} onChange={e => setMembers(current => current.map(m => m.id === s.id ? { ...m, selected: e.target.checked } : m))} className="mt-1 shrink-0"/>
                <span className="break-words">{s.name} [{s.sectionLabel}]{s.locked && <small className="block text-amber-700 dark:text-amber-300">Marks defined — cannot remove</small>}</span>
              </label>)}
              {!members.length && <p className="text-sm text-slate-500 dark:text-slate-400">No students selected.</p>}
            </div>
          </div>
          <div className="min-w-0 space-y-3">
            <h3 className="text-sm font-semibold">Already Created Virtual Classes</h3>
            <div className="max-h-96 space-y-2 overflow-y-auto">
              {classes.map(row => <article key={`${row.vClassId}-${row.subjectId}`} className={`rounded-lg border p-3 ${editId === row.vClassId ? 'border-indigo-400 bg-indigo-50 dark:bg-indigo-950' : 'border-slate-200 dark:border-slate-700'}`}>
                <p className="break-words text-sm font-semibold">{row.vClassName}</p><p className="mb-2 break-words text-sm text-slate-500 dark:text-slate-400">{row.subjectName}</p>
                <button className={button} disabled={busy} onClick={() => edit(row)}>Edit</button>
              </article>)}
              {!classes.length && <p className="text-sm text-slate-500 dark:text-slate-400">No virtual classes for this faculty.</p>}
            </div>
          </div>
        </div>
        <button className={`${button} mt-5`} disabled={busy || studentLoading} onClick={save}>{busy ? 'Please wait...' : editId ? 'Update Virtual Class' : 'Create Virtual Class'}</button>
      </section>}
    </>}
  </div>
}

