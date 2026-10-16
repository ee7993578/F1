import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { apiFetch } from '../../../../lib/api'

const card = 'rounded-xl border border-slate-200 bg-white dark:border-slate-700 dark:bg-[#1a1f35]'
const input = 'w-full rounded-md border border-slate-300 bg-white px-3 py-2 text-sm outline-none focus:border-emerald-500 dark:border-slate-700 dark:bg-[#111827] dark:text-slate-100'
const today = () => new Date().toISOString().slice(0, 10)
const displayDate = v => v ? new Date(`${v}T00:00:00`).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }).replace(/ /g, ' ') : ''
const money = v => Number(v || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })

function Field({ label, children }) { return <label className="block text-xs font-semibold text-slate-600 dark:text-slate-300"><span className="mb-1 block">{label}</span>{children}</label> }
function Notice({ children, error = false }) { return children ? <div className={`rounded-md px-3 py-2 text-sm ${error ? 'bg-rose-50 text-rose-700 dark:bg-rose-500/10 dark:text-rose-300' : 'bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-300'}`}>{children}</div> : null }
function downloadXls(title, columns, rows) {
  const esc = value => String(value ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
  const html = `<html><head><meta charset="utf-8"><style>table{border-collapse:collapse}th,td{border:1px solid #999;padding:5px}th{background:#e8e8e8}</style></head><body><h2>${esc(title)}</h2><table><thead><tr>${columns.map(c => `<th>${esc(c.label)}</th>`).join('')}</tr></thead><tbody>${rows.map(r => `<tr>${columns.map(c => `<td>${esc(c.value(r))}</td>`).join('')}</tr>`).join('')}</tbody></table></body></html>`
  const blob = new Blob([html], { type: 'application/vnd.ms-excel' })
  const a = document.createElement('a'); a.href = URL.createObjectURL(blob); a.download = `${title}.xls`; a.click(); URL.revokeObjectURL(a.href)
}
function printTable(title, columns, rows) {
  const w = window.open('', '_blank', 'width=1200,height=800'); if (!w) return
  const esc = value => String(value ?? '').replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
  w.document.write(`<html><head><title>${esc(title)}</title><style>body{font-family:Arial;padding:18px}h2{text-align:center}table{border-collapse:collapse;width:100%;font-size:11px}th,td{border:1px solid #999;padding:5px;text-align:left}th{background:#eee}</style></head><body><h2>${esc(title)}</h2><table><thead><tr>${columns.map(c => `<th>${esc(c.label)}</th>`).join('')}</tr></thead><tbody>${rows.map(r => `<tr>${columns.map(c => `<td>${esc(c.value(r))}</td>`).join('')}</tr>`).join('')}</tbody></table></body></html>`)
  w.document.close(); w.focus(); w.print()
}

const gatewayColumns = [
  ['Payment Date', 'paymentDate'], ['Settlement Date', 'settlementDate'], ['Order ID', 'orderId'], ['Tracking ID', 'trackingId'],
  ['Bank Reference No', 'bankReferenceNo'], ['Installments', 'installments'], ['Order Status', 'orderStatus'], ['Status Message', 'statusMessage'],
  ['Platform', 'platform'], ['Amount', 'amount'], ['Registration No', 'registrationNo'], ['Fee Book', 'feeBook'], ['Student Name', 'studentName'],
  ['Father Name', 'fatherName'], ['Mobile Number', 'mobileNumber'], ['Class', 'className'], ['Payment-User-Name', 'paymentUserName'],
].map(([label, key]) => ({ label, value: r => key === 'amount' ? money(r[key]) : r[key] }))

export function PaymentResponseReport() {
  const [platform, setPlatform] = useState('0'), [from, setFrom] = useState(today()), [to, setTo] = useState(today()), [admissionNo, setAdmissionNo] = useState('')
  const [rows, setRows] = useState([]), [loading, setLoading] = useState(false), [message, setMessage] = useState(''), [error, setError] = useState(''), [suggestions, setSuggestions] = useState([])
  const load = useCallback(async () => {
    setLoading(true); setError('')
    try {
      const q = new URLSearchParams({ platform, from, to, admissionNo })
      const data = await apiFetch(`/fees/legacy-reports/gateway?${q}`); setRows(data || []); setMessage(`${(data || []).length} record(s) loaded from database`)
    } catch (e) { setError(e.message || 'Gateway response report could not be loaded') } finally { setLoading(false) }
  }, [platform, from, to, admissionNo])
  useEffect(() => { load() }, [])
  const searchStudent = async value => { setAdmissionNo(value); if (value.length < 1) return setSuggestions([]); try { setSuggestions(await apiFetch(`/fees/legacy-reports/gateway/students?query=${encodeURIComponent(value)}`)) } catch { setSuggestions([]) } }
  return <div className="space-y-4">
    <div><h1 className="text-lg font-bold text-slate-800 dark:text-slate-100">GateWay Response Report</h1><p className="text-xs text-slate-500">Payment gateway transactions from PaymentResponse and the linked student records.</p></div>
    <div className={`${card} grid grid-cols-1 gap-3 p-4 md:grid-cols-5`}>
      <Field label="E-PlatForm"><select className={input} value={platform} onChange={e => setPlatform(e.target.value)}><option value="0">--Select All--</option><option value="App">Mobile App</option><option value="Erp">Website</option><option value="Admission">Online Admission</option></select></Field>
      <Field label="From Date"><input className={input} type="date" value={from} max={to || undefined} onChange={e => setFrom(e.target.value)} /></Field>
      <Field label="To Date"><input className={input} type="date" value={to} min={from || undefined} onChange={e => setTo(e.target.value)} /></Field>
      <Field label="Admission No."><div className="relative"><input className={input} value={admissionNo} placeholder="Admission No." autoComplete="off" onChange={e => searchStudent(e.target.value)} />{suggestions.length > 0 && <div className="absolute z-20 mt-1 max-h-56 w-full overflow-auto rounded-md border bg-white shadow dark:border-slate-700 dark:bg-slate-800">{suggestions.map(s => <button type="button" key={s.admissionNo} className="block w-full border-b px-3 py-2 text-left text-xs hover:bg-emerald-50 dark:border-slate-700 dark:hover:bg-slate-700" onClick={() => { setAdmissionNo(s.admissionNo); setSuggestions([]) }}><b>{s.admissionNo}</b> — {s.studentName} ({s.className})</button>)}</div>}</div></Field>
      <div className="flex items-end gap-2"><button type="button" onClick={load} disabled={loading} className="rounded-md bg-emerald-600 px-4 py-2 text-sm font-semibold text-white disabled:opacity-50">{loading ? 'Loading...' : 'Show'}</button><button type="button" onClick={() => downloadXls('PaymentResponseReport', gatewayColumns, rows)} disabled={!rows.length} className="rounded-md border border-emerald-600 px-4 py-2 text-sm font-semibold text-emerald-700 disabled:opacity-40 dark:text-emerald-300">Excel</button></div>
    </div>
    <Notice error={!!error}>{error || message}</Notice>
    <div className={`${card} overflow-x-auto`}><table className="w-full min-w-[1500px] text-xs"><thead className="bg-slate-50 text-left dark:bg-slate-800"><tr>{gatewayColumns.map(c => <th key={c.label} className="whitespace-nowrap p-2">{c.label}</th>)}</tr></thead><tbody>{rows.map((r, i) => <tr key={`${r.orderId || 'row'}-${i}`} className="border-t dark:border-slate-700">{gatewayColumns.map(c => <td key={c.label} className="whitespace-nowrap p-2">{c.value(r) || '—'}</td>)}</tr>)}{!rows.length && <tr><td colSpan={gatewayColumns.length} className="p-10 text-center text-slate-500">{loading ? 'Loading...' : 'No Record Found.'}</td></tr>}</tbody></table></div>
  </div>
}

const defaulterColumns = [
  ['Admission No', r => r.admNo], ['Name', r => r.name], ['Father Name', r => r.fatherName], ['Category', r => r.quota], ['Address', r => r.address], ['Class', r => r.className],
  ['Installment', r => r.installmentNo], ['Month', r => r.monthFullName], ['Charges', r => money(r.Charges)], ['Concession', r => money(r.Concession)], ['WaveOff', r => money(r.WaveOff)],
  ['Payable', r => money(r.payable)], ['Receive Amount', r => money(r.receiveAmount)], ['Previous Balance', r => money(r.previousBalance)], ['Balance', r => money(r.balance)], ['Phone', r => r.phone], ['Mobile', r => r.mobile],
]

export function FeeDefaulterReport({ onPageClick }) {
  const [boot, setBoot] = useState({ sessions: [], classes: [], feeTypes: [], installments: [], visibility: {} }), [session, setSession] = useState(''), [feeTypeId, setFeeTypeId] = useState(0), [reportType, setReportType] = useState(2), [installmentNo, setInstallmentNo] = useState(''), [classId, setClassId] = useState(0), [asOnDate, setAsOnDate] = useState(today()), [rows, setRows] = useState([]), [holds, setHolds] = useState([]), [loading, setLoading] = useState(false), [error, setError] = useState(''), [showHolds, setShowHolds] = useState(false)
  const loadBoot = useCallback(async () => { try { const x = await apiFetch('/fees/legacy-reports/defaulter/bootstrap'); setBoot(x); setSession(x.session || ''); setInstallmentNo(x.installments?.[0]?.value ?? '') } catch (e) { setError(e.message) } }, [])
  useEffect(() => { loadBoot() }, [loadBoot])
  useEffect(() => { if (!session) return; apiFetch(`/fees/legacy-reports/defaulter/installments?session=${encodeURIComponent(session)}&feeTypeId=${feeTypeId}`).then(x => setBoot(b => ({ ...b, installments: x }))).catch(e => setError(e.message)) }, [session, feeTypeId])
  const load = async () => { setLoading(true); setError(''); try { const q = new URLSearchParams({ session, installmentNo, feeTypeId, classId, reportType, asOnDate }); const x = await apiFetch(`/fees/legacy-reports/defaulter?${q}`); setRows(x || []) } catch (e) { setError(e.message || 'Defaulter report could not be loaded') } finally { setLoading(false) } }
  const openHolds = async () => { setShowHolds(true); try { setHolds(await apiFetch(`/fees/legacy-reports/defaulter/holds?feeTypeId=${feeTypeId}`)) } catch (e) { setError(e.message) } }
  const holdStudent = async row => { const uptoDate = window.prompt('Up to date (dd MMM yyyy)', displayDate(asOnDate)); if (!uptoDate) return; try { await apiFetch('/fees/legacy-reports/defaulter/holds', { method: 'POST', body: JSON.stringify({ studentId: row.studentId, feeTypeId, session, uptoDate }) }); await load() } catch (e) { setError(e.message) } }
  const columnsForExport = defaulterColumns.map(([label, value]) => ({ label, value }))
  const goDeposit = row => { sessionStorage.setItem('fee.deposit.prefill', JSON.stringify({ admissionNo: row.admNo, feeTypeId, installmentNo })); onPageClick?.('fee', 'fee_work', 'DepositFee') }
  return <div className="space-y-4">
    <div><h1 className="text-lg font-bold text-slate-800 dark:text-slate-100">Defaulter Report</h1><p className="text-xs text-slate-500">Same Regular/Transport/Hostel and Partial/Full/All workflow as the legacy report.</p></div>
    <div className={`${card} grid grid-cols-1 gap-3 p-4 md:grid-cols-3 lg:grid-cols-6`}>
      <Field label="Select Fee Type"><div className="flex gap-2 pt-2 text-xs"><label><input type="radio" checked={feeTypeId === 0} onChange={() => setFeeTypeId(0)} /> Regular</label>{boot.visibility?.transport && <label><input type="radio" checked={feeTypeId === 6} onChange={() => setFeeTypeId(6)} /> Transport</label>}{boot.visibility?.hostel && <label><input type="radio" checked={feeTypeId === 7} onChange={() => setFeeTypeId(7)} /> Hostel</label>}</div></Field>
      <Field label="Report Type"><div className="flex gap-2 pt-2 text-xs"><label><input type="radio" checked={reportType === 1} onChange={() => setReportType(1)} /> Partial</label><label><input type="radio" checked={reportType === 0} onChange={() => setReportType(0)} /> Full</label><label><input type="radio" checked={reportType === 2} onChange={() => setReportType(2)} /> All</label></div></Field>
      <Field label="Select Session"><select className={input} value={session} onChange={e => setSession(e.target.value)}>{boot.sessions.map(x => <option key={x.value} value={x.value}>{x.label}</option>)}</select></Field>
      <Field label="Select Installment"><select className={input} value={installmentNo} onChange={e => setInstallmentNo(e.target.value)}>{boot.installments.map(x => <option key={x.value} value={x.value}>{x.label}</option>)}</select></Field>
      <Field label="Select Class"><select className={input} value={classId} onChange={e => setClassId(e.target.value)}><option value="0">All Classes</option>{boot.classes.map(x => <option key={x.id} value={x.id}>{x.label}</option>)}</select></Field>
      <Field label="As On Date"><input className={input} type="date" value={asOnDate} onChange={e => setAsOnDate(e.target.value)} /></Field>
      <div className="flex flex-wrap items-end gap-2 md:col-span-3 lg:col-span-6"><button type="button" onClick={load} disabled={loading} className="rounded-md bg-emerald-600 px-4 py-2 text-sm font-semibold text-white disabled:opacity-50">{loading ? 'Loading...' : 'submit'}</button><button type="button" onClick={() => downloadXls('Defaulter Report', columnsForExport, rows)} disabled={!rows.length} className="rounded-md border border-emerald-600 px-4 py-2 text-sm font-semibold text-emerald-700 disabled:opacity-40 dark:text-emerald-300">Excel</button><button type="button" onClick={() => printTable('Defaulter Report', columnsForExport, rows)} disabled={!rows.length} className="rounded-md border px-4 py-2 text-sm font-semibold disabled:opacity-40">Print</button><button type="button" onClick={openHolds} className="rounded-md bg-emerald-600 px-4 py-2 text-sm font-semibold text-white">Hold Students</button></div>
    </div>
    <Notice error={!!error}>{error}</Notice>
    <div className={`${card} overflow-x-auto`}><table className="w-full min-w-[1800px] text-xs"><thead className="bg-slate-50 text-left dark:bg-slate-800"><tr><th className="p-2">S NO.</th>{defaulterColumns.map(([label]) => <th key={label} className="whitespace-nowrap p-2">{label}</th>)}<th className="p-2">Deposite</th><th className="p-2">Hold</th></tr></thead><tbody>{rows.map((r, i) => <tr key={`${r.admNo}-${i}`} className="border-t dark:border-slate-700"><td className="p-2">{i + 1}</td>{defaulterColumns.map(([label, value]) => <td key={label} className="whitespace-nowrap p-2">{value(r) || '—'}</td>)}<td className="p-2"><button type="button" className="rounded bg-emerald-600 px-2 py-1 text-white" onClick={() => goDeposit(r)}>Deposite</button></td><td className="p-2"><button type="button" className="rounded bg-emerald-600 px-2 py-1 text-white" onClick={() => holdStudent(r)}>Hold</button></td></tr>)}{!rows.length && <tr><td colSpan={defaulterColumns.length + 3} className="p-10 text-center text-slate-500">{loading ? 'Loading...' : 'No Record Found.'}</td></tr>}</tbody></table></div>
    {showHolds && <div className="fixed inset-0 z-30 flex items-start justify-center bg-black/40 p-5"><div className={`${card} max-h-[80vh] w-full max-w-5xl overflow-auto p-4`}><div className="mb-3 flex items-center justify-between"><h2 className="font-semibold">Hold students List from Fee Defaulters</h2><button type="button" onClick={() => setShowHolds(false)} className="rounded border px-3 py-1">Close</button></div><table className="w-full text-xs"><thead><tr className="border-b text-left"><th className="p-2">Up to date</th><th className="p-2">Admission No.</th><th className="p-2">Student Name</th><th className="p-2">Father Name</th><th className="p-2">Class</th><th className="p-2">Mobile</th><th className="p-2">Action</th></tr></thead><tbody>{holds.map(h => <tr key={h.id} className="border-b dark:border-slate-700"><td className="p-2">{h.uptodate}</td><td className="p-2">{h.admNo}</td><td className="p-2">{h.name}</td><td className="p-2">{h.fatherName}</td><td className="p-2">{h.className}</td><td className="p-2">{h.mobile}</td><td className="p-2"><button type="button" className="rounded bg-emerald-600 px-2 py-1 text-white" onClick={async () => { try { await apiFetch(`/fees/legacy-reports/defaulter/holds/${h.id}`, { method: 'PUT' }); setHolds(await apiFetch(`/fees/legacy-reports/defaulter/holds?feeTypeId=${feeTypeId}`)) } catch (e) { setError(e.message) } }}>UnHold</button></td></tr>)}</tbody></table></div></div>}
  </div>
}

