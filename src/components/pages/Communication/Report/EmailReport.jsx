import { useCallback, useEffect, useState } from 'react'
import { apiFetch } from '../../../../lib/api'
import { Eye, FileSpreadsheet, X } from 'lucide-react'
import { button, card, downloadExcel, Field, input, Page, secondary, Select, Table, value } from '../CommunicationShared'

const columns = [
  { key: 'entitytype', label: 'Entity Type' }, { key: 'created_date', label: 'Created Date' },
  { key: 'ReceipientEmailId', label: 'Receipient EmailId' }, { key: 'SenderEmailId', label: 'Sender EmailId' },
  { key: 'registration_no', label: 'Adm No' }, { key: 'EmailStatus', label: 'Status' },
  { key: 'EmailStatusDate', label: 'Status Date' }, { key: 'inst_no', label: 'Inst No' },
  { key: 'EmailSubject', label: 'Subject' },
]
const today = () => new Date().toISOString().slice(0, 10)

export default function EmailReport() {
  const [boot, setBoot] = useState(null); const [rows, setRows] = useState([]); const [loading, setLoading] = useState(true); const [working, setWorking] = useState(false); const [error, setError] = useState(''); const [content, setContent] = useState(null)
  const [session, setSession] = useState(''); const [entityType, setEntityType] = useState('0'); const [status, setStatus] = useState('0'); const [from, setFrom] = useState(today()); const [to, setTo] = useState(today())
  const loadRows = useCallback(async (filters = {}) => { setWorking(true); setError(''); try { const qs = new URLSearchParams({ session: filters.session ?? session, entityType: filters.entityType ?? entityType, status: filters.status ?? status, from: filters.from ?? from, to: filters.to ?? to }); setRows(await apiFetch(`/communication/reports/email?${qs}`) || []) } catch (e) { setError(e.message || 'Email report could not be loaded') } finally { setWorking(false) } }, [session, entityType, status, from, to])
  useEffect(() => { let alive = true; apiFetch('/communication/bootstrap').then(data => { if (!alive) return; setBoot(data); const s = data.session || ''; setSession(s); return loadRows({ session: s }) }).catch(e => { if (alive) setError(e.message || 'Unable to load Email Report') }).finally(() => { if (alive) setLoading(false) }); return () => { alive = false } }, [])
  if (loading) return <Page title="Email Report" description="Legacy Communication / EmailReport.aspx workflow."><div className={`${card} p-8 text-center text-sm text-slate-500`}>Loading Email Report...</div></Page>
  return <Page title="Email Report" description="Email status report with the same session, type, date, status and content flow as the legacy page." error={error}>
    {working && <div className="rounded-lg border border-blue-200 bg-blue-50 p-3 text-sm text-blue-700 dark:border-indigo-700/40 dark:bg-indigo-950/30 dark:text-indigo-200">Loading report...</div>}
    <div className={`${card} grid gap-4 p-4 md:grid-cols-2 lg:grid-cols-5`}><Field title="Select Session"><Select value={session} onChange={e => setSession(e.target.value)} options={boot?.sessions || []} /></Field><Field title="Select Type"><Select value={entityType} onChange={e => setEntityType(e.target.value)} options={[{ value: '0', label: '<-Select All->' }, ...(boot?.emailTypes || [])]} /></Field><Field title="Enter From Date"><input className={input} type="date" value={from} onChange={e => setFrom(e.target.value)} /></Field><Field title="Enter To Date"><input className={input} type="date" value={to} onChange={e => setTo(e.target.value)} /></Field><Field title="Select Status"><Select value={status} onChange={e => setStatus(e.target.value)} options={[{ value: '0', label: '<-Select All->' }, ...(boot?.emailStatuses || [])]} /></Field><div className="flex items-end gap-2 md:col-span-2 lg:col-span-5"><button className={button} onClick={() => loadRows()}>GO</button><button className={secondary} disabled={!rows.length} onClick={() => downloadExcel('EMAILDeliveryReport.xls', columns.concat([{ key: 'EmailContent', label: 'Content' }]), rows)}><FileSpreadsheet className="h-4 w-4" />EXPORT</button></div></div>
    <div className={`${card} overflow-hidden`}><div className="border-b border-slate-200 p-4 text-center dark:border-slate-700"><h2 className="font-bold text-slate-800 dark:text-slate-100">EMAIL STATUS REPORT</h2><p className="text-xs text-slate-500">SESSION: {session} &nbsp; DATE: {from} - {to}</p></div><Table rows={rows} columns={columns.concat([{ key: 'contentAction', label: 'Content', render: row => <button className="inline-flex items-center gap-1 text-blue-600 underline dark:text-indigo-300" onClick={() => setContent(row)}><Eye className="h-3.5 w-3.5" />Show Content</button> }])} searchPlaceholder="Search email report..." /></div>
    {content && <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 p-4"><div className="w-full max-w-2xl rounded-xl bg-white p-5 shadow-xl dark:bg-slate-900"><div className="flex items-start justify-between"><h3 className="font-bold text-slate-800 dark:text-slate-100">Email Content</h3><button className="text-slate-400" onClick={() => setContent(null)}><X /></button></div><div className="mt-4 max-h-[60vh] overflow-auto whitespace-pre-wrap rounded-lg bg-slate-50 p-4 text-sm text-slate-700 dark:bg-slate-800 dark:text-slate-200">{content.EmailContent || 'No content'}</div></div></div>}
  </Page>
}
