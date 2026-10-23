import { useCallback, useEffect, useState } from 'react'
import { apiFetch } from '../../../../lib/api'
import { Check, MessageSquare, Send, Users, X } from 'lucide-react'
import { Busy, button, card, CheckList, Field, input, Page, secondary, Select, TemplateEditor, value } from '../CommunicationShared'

const TYPE_OPTIONS = [
  { value: '0', label: '<--Select-->' },
  { value: '1', label: 'SMS Individual' },
  { value: '3', label: 'SMS Student' },
  { value: '4', label: 'SMS Faculty' },
]
const STUDENT_TYPES = [
  { value: '0', label: '<--Select All-->' },
  { value: '1', label: 'Old Student' },
  { value: '3', label: 'New Student' },
]

export default function SendSMS() {
  const [boot, setBoot] = useState(null)
  const [loading, setLoading] = useState(true)
  const [working, setWorking] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [type, setType] = useState('0')
  const [studentType, setStudentType] = useState('0')
  const [classIds, setClassIds] = useState([])
  const [studentIds, setStudentIds] = useState([])
  const [facultyIds, setFacultyIds] = useState([])
  const [students, setStudents] = useState([])
  const [faculty, setFaculty] = useState([])
  const [individualMobile, setIndividualMobile] = useState('')
  const [templateId, setTemplateId] = useState('')
  const [smsText, setSmsText] = useState('')
  const [confirm, setConfirm] = useState(false)
  const [result, setResult] = useState('')

  const load = useCallback(async () => {
    setLoading(true); setError('')
    try {
      const data = await apiFetch('/communication/bootstrap')
      setBoot(data)
      setTemplateId(value(data.templates?.[0]))
      setFaculty(data.faculty || [])
    } catch (e) { setError(e.message || 'Unable to load Communication options') } finally { setLoading(false) }
  }, [])
  useEffect(() => { load() }, [load])

  useEffect(() => {
    if (!boot || type !== '3' || classIds.length === 0) { setStudents([]); setStudentIds([]); return undefined }
    let cancelled = false
    apiFetch(`/communication/students?session=${encodeURIComponent(boot.session || '')}&classIds=${encodeURIComponent(classIds.join(','))}&studentType=${encodeURIComponent(studentType)}`)
      .then(rows => { if (!cancelled) { setStudents(rows || []); setStudentIds([]) } })
      .catch(e => { if (!cancelled) setError(e.message) })
    return () => { cancelled = true }
  }, [boot, type, classIds, studentType])

  const templates = boot?.templates || []
  const recipientCount = type === '1' ? (individualMobile ? 1 : 0) : type === '3' ? studentIds.length : type === '4' ? facultyIds.length : 0
  const smsCredits = recipientCount * Math.max(1, Math.ceil((smsText || '').length / 160))
  const validMobile = /^[6-9][0-9]{9}$/.test(individualMobile)

  function resetRecipients(nextType) { setType(nextType); setClassIds([]); setStudentIds([]); setFacultyIds([]); setStudents([]); setIndividualMobile(''); setError(''); setResult('') }
  function validate() {
    if (type === '0') return 'Please select type'
    if (!templateId || !smsText.trim()) return 'Please check your SMS text'
    if (type === '1' && !validMobile) return 'Please Enter Number.'
    if (type === '3' && studentIds.length === 0) return 'Please select student'
    if (type === '4' && facultyIds.length === 0) return 'Please select faculty'
    return ''
  }
  function preview() { const invalid = validate(); if (invalid) { setError(invalid); return } setError(''); setConfirm(true) }
  async function send() {
    const invalid = validate(); if (invalid) { setError(invalid); setConfirm(false); return }
    setWorking(true); setError(''); setResult('')
    try {
      const data = await apiFetch('/communication/sms/send', { method: 'POST', body: JSON.stringify({ session: boot?.session || '', type, studentIds, facultyIds, individualMobile, templateId, message: smsText }) })
      setResult(data.message || 'Message Sent'); setConfirm(false); setStudentIds([]); setFacultyIds([]); setIndividualMobile('')
    } catch (e) { setError(e.message || 'Message sending failed'); setConfirm(false) } finally { setWorking(false) }
  }

  if (loading) return <Page title="Send SMS" description="Legacy Communication / send_sms_new.aspx workflow."><div className={`${card} p-8 text-center text-sm text-slate-500`}>Loading Communication options...</div></Page>
  return <Page title="Send SMS" description="Send an SMS to an individual, selected students, or selected faculty using the school templates." error={error} message={result}>
    {working && <Busy text="Message Sending Please Wait..." />}
    <div className={`${card} p-4`}>
      <div className="mb-4 flex items-center gap-2 border-b border-slate-200 pb-3 dark:border-slate-700"><MessageSquare className="h-5 w-5 text-blue-600 dark:text-indigo-400" /><h2 className="font-bold text-slate-800 dark:text-slate-100">Send SMS</h2>{boot?.provider?.balanceUrl && <span className="ml-auto rounded border border-orange-300 px-2 py-1 text-xs text-orange-700 dark:text-orange-300">Provider balance configured</span>}</div>
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Field title="SMS Type"><Select value={type} onChange={e => resetRecipients(e.target.value)} options={TYPE_OPTIONS} /></Field>
        {type === '1' && <Field title="Enter No."><input className={input} value={individualMobile} maxLength={10} onChange={e => setIndividualMobile(e.target.value.replace(/\D/g, ''))} /></Field>}
        {type === '3' && <Field title="Student Type"><Select value={studentType} onChange={e => setStudentType(e.target.value)} options={STUDENT_TYPES} /></Field>}
      </div>
      {type === '3' && <div className="mt-4 space-y-4"><Field title="Select Class"><CheckList options={boot?.classes || []} selected={classIds} onChange={setClassIds} searchPlaceholder="Search class..." /></Field><Field title="Select Student"><CheckList options={students} selected={studentIds} onChange={setStudentIds} searchPlaceholder="Search student..." /></Field></div>}
      {type === '4' && <div className="mt-4"><Field title="Select Faculty"><CheckList options={faculty} selected={facultyIds} onChange={setFacultyIds} searchPlaceholder="Search faculty..." /></Field></div>}
    </div>
    <div className={`${card} p-4`}><div className="mb-3 flex items-center gap-2"><Users className="h-5 w-5 text-blue-600 dark:text-indigo-400" /><h2 className="font-bold text-slate-800 dark:text-slate-100">SMS Template</h2></div><TemplateEditor templates={templates} templateId={templateId} onTemplate={setTemplateId} message={smsText} onMessage={setSmsText} /></div>
    <div className={`${card} flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between`}><div className="text-sm text-slate-600 dark:text-slate-300"><span className="font-semibold">Total Recipient:</span> {recipientCount}<span className="ml-4 font-semibold">Total Credit:</span> {smsCredits}</div><button className={button} onClick={preview} disabled={recipientCount === 0}><Send className="h-4 w-4" />Send</button></div>
    {confirm && <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4"><div className="w-full max-w-md rounded-xl bg-white p-5 shadow-xl dark:bg-slate-900"><div className="flex items-start justify-between"><h3 className="text-lg font-bold text-slate-800 dark:text-slate-100">Confirm SMS</h3><button className="text-slate-400" onClick={() => setConfirm(false)}><X /></button></div><p className="mt-3 text-sm text-slate-600 dark:text-slate-300">Total Recipient: <b>{recipientCount}</b><br />Total Credit: <b>{smsCredits}</b></p><div className="mt-3 max-h-32 overflow-auto rounded bg-slate-50 p-3 text-xs text-slate-600 dark:bg-slate-800 dark:text-slate-300">{smsText}</div><div className="mt-5 flex justify-end gap-2"><button className={secondary} onClick={() => setConfirm(false)}>Cancel</button><button className={button} onClick={send}><Check className="h-4 w-4" />Send</button></div></div></div>}
  </Page>
}
