import { useCallback, useEffect, useState } from 'react'
import { apiFetch } from '../../../../lib/api'
import { Check, MessageSquare, Send, Users, X } from 'lucide-react'
import { Busy, button, card, CheckList, Field, Page, secondary, Select, TemplateEditor, value } from '../CommunicationShared'

export default function SendSmsGroup() {
  const [boot, setBoot] = useState(null)
  const [loading, setLoading] = useState(true)
  const [working, setWorking] = useState(false)
  const [error, setError] = useState('')
  const [result, setResult] = useState('')
  const [session, setSession] = useState('')
  const [feeType, setFeeType] = useState('-1')
  const [defaulterType, setDefaulterType] = useState('2')
  const [installment, setInstallment] = useState('0')
  const [classId, setClassId] = useState('0')
  const [students, setStudents] = useState([])
  const [studentIds, setStudentIds] = useState([])
  const [templateId, setTemplateId] = useState('')
  const [smsText, setSmsText] = useState('')
  const [installments, setInstallments] = useState([])
  const [confirm, setConfirm] = useState(false)

  const load = useCallback(async () => {
    setLoading(true); setError('')
    try { const data = await apiFetch('/communication/bootstrap'); setBoot(data); setSession(data.session || ''); setTemplateId(value(data.feeTemplates?.[0])) } catch (e) { setError(e.message || 'Unable to load Communication options') } finally { setLoading(false) }
  }, [])
  useEffect(() => { load() }, [load])
  useEffect(() => {
    if (!boot || feeType === '-1') { setInstallments([]); setInstallment('0'); setStudents([]); setStudentIds([]); return undefined }
    let cancelled = false
    const qs = new URLSearchParams({ session, feeType: feeType === '0' ? '0' : feeType })
    apiFetch(`/fees/legacy-reports/defaulter/installments?${qs}`).then(rows => { if (!cancelled) setInstallments(rows || []) }).catch(e => { if (!cancelled) setError(e.message) })
    return () => { cancelled = true }
  }, [boot, session, feeType])
  useEffect(() => {
    if (!boot || feeType === '-1' || installment === '0') { setStudents([]); setStudentIds([]); return undefined }
    let cancelled = false
    const qs = new URLSearchParams({ session, feeType, installment, defaulterType, classId })
    apiFetch(`/communication/fee-students?${qs}`).then(rows => { if (!cancelled) { setStudents(rows || []); setStudentIds([]) } }).catch(e => { if (!cancelled) setError(e.message) })
    return () => { cancelled = true }
  }, [boot, session, feeType, installment, defaulterType, classId])

  const templates = boot?.feeTemplates || []
  const credit = studentIds.length * Math.max(1, Math.ceil((smsText || '').length / 160))
  function validate() { if (feeType === '-1') return 'Please select fee type'; if (installment === '0') return 'Please select installment'; if (!templateId || !smsText.trim()) return 'Please check your SMS text'; if (!studentIds.length) return 'please select atleast one student to send sms'; return '' }
  function preview() { const invalid = validate(); if (invalid) { setError(invalid); return } setError(''); setConfirm(true) }
  async function send() {
    const invalid = validate(); if (invalid) { setError(invalid); setConfirm(false); return }
    setWorking(true); setError(''); setResult('')
    try { const data = await apiFetch('/communication/fee-sms/send', { method: 'POST', body: JSON.stringify({ session, feeType, installment, defaulterType, studentIds, templateId, message: smsText }) }); setResult(data.message || 'Message Sent'); setConfirm(false); setStudentIds([]) } catch (e) { setError(e.message || 'Message sending failed'); setConfirm(false) } finally { setWorking(false) }
  }
  if (loading) return <Page title="Send SMS Group" description="Legacy Communication / sendsms.aspx workflow."><div className={`${card} p-8 text-center text-sm text-slate-500`}>Loading Communication options...</div></Page>
  return <Page title="Send SMS Group" description="Fee-defaulter SMS group workflow from the legacy sendsms.aspx page." error={error} message={result}>
    {working && <Busy text="Message Sending Please Wait..." />}
    <div className={`${card} p-4`}><div className="mb-4 flex items-center gap-2 border-b border-slate-200 pb-3 dark:border-slate-700"><MessageSquare className="h-5 w-5 text-blue-600 dark:text-indigo-400" /><h2 className="font-bold text-slate-800 dark:text-slate-100">Send SMS</h2></div><div className="grid gap-4 md:grid-cols-2 lg:grid-cols-5"><Field title="Session"><Select value={session} onChange={e => { setSession(e.target.value); setStudents([]); setStudentIds([]) }} options={boot?.sessions || []} /></Field><Field title="Fee Type"><Select value={feeType} onChange={e => { setFeeType(e.target.value); setInstallment('0'); setStudents([]); setStudentIds([]) }} options={boot?.feeTypes || []} /></Field><Field title="Defaulter Type"><div className="flex flex-wrap gap-3 pt-2 text-sm text-slate-700 dark:text-slate-200">{(boot?.defaulterTypes || []).map(row => <label key={value(row)} className="flex items-center gap-1"><input type="radio" checked={defaulterType === value(row)} onChange={() => setDefaulterType(value(row))} />{row.label}</label>)}</div></Field><Field title="Installment"><Select value={installment} onChange={e => setInstallment(e.target.value)} options={[{ value: '0', label: '<--Select-->' }, ...installments]} /></Field><Field title="Class"><Select value={classId} onChange={e => setClassId(e.target.value)} options={[{ value: '0', label: 'All Classes' }, ...(boot?.classes || [])]} /></Field></div></div>
    <div className={`${card} p-4`}><Field title="Select Student"><CheckList options={students} selected={studentIds} onChange={setStudentIds} searchPlaceholder="Search student..." /></Field></div>
    <div className={`${card} p-4`}><div className="mb-3 flex items-center gap-2"><Users className="h-5 w-5 text-blue-600 dark:text-indigo-400" /><h2 className="font-bold text-slate-800 dark:text-slate-100">SMS Template</h2></div><TemplateEditor templates={templates} templateId={templateId} onTemplate={setTemplateId} message={smsText} onMessage={setSmsText} /></div>
    <div className={`${card} flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between`}><div className="text-sm text-slate-600 dark:text-slate-300"><span className="font-semibold">Total Recipient:</span> {studentIds.length}<span className="ml-4 font-semibold">Total Credit:</span> {credit}</div><button className={button} onClick={preview} disabled={!studentIds.length}><Send className="h-4 w-4" />Send</button></div>
    {confirm && <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4"><div className="w-full max-w-md rounded-xl bg-white p-5 shadow-xl dark:bg-slate-900"><div className="flex items-start justify-between"><h3 className="text-lg font-bold text-slate-800 dark:text-slate-100">Confirm SMS</h3><button className="text-slate-400" onClick={() => setConfirm(false)}><X /></button></div><p className="mt-3 text-sm text-slate-600 dark:text-slate-300">Total Recipient: <b>{studentIds.length}</b><br />Total Credit: <b>{credit}</b></p><div className="mt-3 max-h-32 overflow-auto rounded bg-slate-50 p-3 text-xs text-slate-600 dark:bg-slate-800 dark:text-slate-300">{smsText}</div><div className="mt-5 flex justify-end gap-2"><button className={secondary} onClick={() => setConfirm(false)}>Cancel</button><button className={button} onClick={send}><Check className="h-4 w-4" />Send</button></div></div></div>}
  </Page>
}
