import { useCallback, useEffect, useMemo, useState } from 'react'
import { AlertCircle, BadgeIndianRupee, Check, Eye, Loader2, RefreshCw, Save, X } from 'lucide-react'
import { apiFetch } from '../../../../lib/api'

const money = value => Number(value || 0).toLocaleString('en-IN')

function Toast({ message, type = 'success', onClose }) {
  return <div className={`fixed bottom-6 left-1/2 z-50 flex -translate-x-1/2 items-center gap-2 rounded-xl px-4 py-3 text-sm font-semibold text-white shadow-xl ${type === 'error' ? 'bg-rose-600' : 'bg-emerald-600'}`}>
    {type === 'error' ? <AlertCircle className="h-4 w-4" /> : <Check className="h-4 w-4" />}<span>{message}</span><button type="button" onClick={onClose}><X className="h-4 w-4 opacity-75" /></button>
  </div>
}

function normalize(row) {
  return { ...row, feeHeadId: row.feeHeadId ?? row.fee_head_id, feeTypeId: row.feeTypeId ?? row.fee_type_id, feeHead: row.feeHead || row.fee_head || '', feeType: row.feeType || row.fee_type || '', charges: Number(row.charges ?? row.fee_charges ?? 0), id: Number(row.id || 0) }
}

function ChargeRow({ row, onChange, onUpdate, busy }) {
  return <tr className="border-b border-slate-100 dark:border-white/10">
    <td className="px-3 py-2 text-center text-xs text-slate-400">{row.feeHeadId}</td><td className="px-3 py-2 text-sm font-semibold text-slate-700 dark:text-slate-200">{row.feeHead}</td><td className="px-3 py-2 text-center text-xs text-slate-500 dark:text-slate-400">{row.feeType}</td>
    <td className="px-3 py-2"><input type="number" min="0" value={row.charges} onChange={e => onChange(row.feeHeadId, e.target.value)} className="w-32 rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-right text-sm outline-none focus:border-blue-400 dark:border-white/15 dark:bg-[#1e2238] dark:text-slate-100" /></td>
    <td className="px-3 py-2 text-center"><button type="button" disabled={busy} onClick={() => onUpdate(row)} className="rounded-lg bg-blue-600 px-3 py-1.5 text-xs font-semibold text-white disabled:opacity-50">{busy ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : row.id ? 'Update' : 'Save'}</button></td>
  </tr>
}

function Chart({ rows, installments }) {
  const chargeByHead = useMemo(() => new Map(rows.map(row => [String(row.feeHeadId), Number(row.charges || 0)])), [rows])
  const groups = useMemo(() => {
    const grouped = new Map()
    for (const item of installments) {
      const key = String(item.installmentNo)
      if (!grouped.has(key)) grouped.set(key, { installmentNo: item.installmentNo, dueDate: item.dueDate, items: [] })
      const group = grouped.get(key); const feeHeadId = item.feeHeadId == null ? '' : String(item.feeHeadId)
      if (!group.items.some(x => x.feeHeadId === feeHeadId && x.monthId === item.monthId)) group.items.push({ feeHeadId, feeHead: item.feeHead || item.description || `Fee Type ${item.feeTypeId}`, monthId: item.monthId, amount: chargeByHead.get(feeHeadId) || 0 })
    }
    return [...grouped.values()]
  }, [installments, chargeByHead])
  if (!groups.length) return <p className="p-5 text-sm text-slate-500">No installment chart found for this session.</p>
  return <div className="space-y-3 p-4">{groups.map(group => <div key={group.installmentNo} className="overflow-hidden rounded-xl border border-slate-200 dark:border-white/10"><div className="flex items-center justify-between bg-slate-50 px-4 py-2 text-sm font-bold dark:bg-white/[.03] dark:text-slate-200"><span>Installment {group.installmentNo}</span><span className="text-xs font-normal text-slate-500">{group.dueDate ? new Date(group.dueDate).toLocaleDateString('en-IN') : ''}</span></div><div className="overflow-x-auto"><table className="w-full"><thead><tr className="text-left text-[11px] uppercase text-slate-400"><th className="px-4 py-2">Fee Head</th><th className="px-4 py-2">Month</th><th className="px-4 py-2 text-right">Charge</th></tr></thead><tbody>{group.items.map((item, index) => <tr key={`${item.feeHeadId}-${item.monthId}-${index}`} className="border-t border-slate-100 text-sm dark:border-white/10 dark:text-slate-300"><td className="px-4 py-2">{item.feeHead}</td><td className="px-4 py-2">{item.monthId}</td><td className="px-4 py-2 text-right font-semibold">₹{money(item.amount)}</td></tr>)}</tbody></table></div></div>)}</div>
}

export default function FeeHeadChargeMaster() {
  const [meta, setMeta] = useState({ session: '', sessions: [], classes: [], schoolType: 1 })
  const [session, setSession] = useState(''); const [schoolType, setSchoolType] = useState(1); const [classId, setClassId] = useState('')
  const [rows, setRows] = useState([]); const [installments, setInstallments] = useState([]); const [tab, setTab] = useState('charges')
  const [loading, setLoading] = useState(false); const [busyId, setBusyId] = useState(null); const [toast, setToast] = useState(null)
  const showToast = useCallback((message, type = 'success') => { setToast({ message, type }); window.setTimeout(() => setToast(null), 3500) }, [])

  useEffect(() => { apiFetch('/fees/setup/bootstrap').then(data => { setMeta(data); setSession(data.session || data.sessions?.[0]?.value || ''); setSchoolType(Number(data.schoolType || 1)) }).catch(error => showToast(error.message || 'Fee setup could not be loaded', 'error')) }, [showToast])
  const loadCharges = useCallback(() => {
    if (!session || !classId) { showToast('Select session and class first.', 'error'); return }
    setLoading(true); apiFetch('/fees/charges?' + new URLSearchParams({ session, classId, schoolType })).then(data => setRows((data || []).map(normalize))).then(() => showToast('Fee head charges loaded.')).catch(error => showToast(error.message || 'Fee head charges could not be loaded.', 'error')).finally(() => setLoading(false))
  }, [session, classId, schoolType, showToast])
  const loadChart = useCallback(() => {
    if (!session) return
    Promise.all([apiFetch('/fees/installments?session=' + encodeURIComponent(session)), classId ? apiFetch('/fees/charges?' + new URLSearchParams({ session, classId, schoolType })) : Promise.resolve([])]).then(([inst, charges]) => { setInstallments(inst || []); if (classId) setRows((charges || []).map(normalize)) }).catch(error => showToast(error.message || 'Fee chart could not be loaded.', 'error'))
  }, [session, classId, schoolType, showToast])
  useEffect(() => { if (tab === 'chart') loadChart() }, [tab, loadChart])
  const updateValue = (id, value) => setRows(previous => previous.map(row => row.feeHeadId === id ? { ...row, charges: value } : row))
  const saveRow = useCallback(row => {
    setBusyId(row.feeHeadId); const payload = { session, feeHeadId: row.feeHeadId, feeTypeId: row.feeTypeId, charges: Number(row.charges || 0), classId: Number(classId), schoolType: Number(schoolType), status: 1 }
    apiFetch(row.id ? `/fees/charges/${row.id}` : '/fees/charges', { method: row.id ? 'PUT' : 'POST', body: JSON.stringify(payload) }).then(() => { showToast(`${row.feeHead} charge saved.`); loadCharges() }).catch(error => showToast(error.message || 'Charge save failed.', 'error')).finally(() => setBusyId(null))
  }, [session, classId, schoolType, loadCharges, showToast])
  const saveAll = () => Promise.all(rows.map(row => { const payload = { session, feeHeadId: row.feeHeadId, feeTypeId: row.feeTypeId, charges: Number(row.charges || 0), classId: Number(classId), schoolType: Number(schoolType), status: 1 }; return apiFetch(row.id ? `/fees/charges/${row.id}` : '/fees/charges', { method: row.id ? 'PUT' : 'POST', body: JSON.stringify(payload) }) })).then(() => { showToast('All charges saved.'); loadCharges() }).catch(error => showToast(error.message || 'Charges could not be saved.', 'error'))
  const schoolTypes = meta.schoolType === 1 ? [{ value: 1, label: 'Day School' }] : meta.schoolType === 2 ? [{ value: 2, label: 'Day Boarding' }] : [{ value: 1, label: 'Day School' }, { value: 2, label: 'Day Boarding' }]
  return <div className="space-y-4 pb-10"><div><h1 className="flex items-center gap-2 text-xl font-bold text-slate-800 dark:text-slate-100"><BadgeIndianRupee className="h-5 w-5 text-blue-600" />Fee Head Charge Master</h1><p className="mt-1 text-sm text-slate-500">Session, school type and class wise fee charges.</p></div>
    <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm dark:border-white/10 dark:bg-[#1a1f35]"><div className="grid gap-3 md:grid-cols-4">
      <label className="text-xs font-bold uppercase text-slate-500">Session<select value={session} onChange={e => { setSession(e.target.value); setClassId(''); setRows([]) }} className="mt-1 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm dark:border-white/15 dark:bg-[#1e2238] dark:text-slate-100">{meta.sessions.map(item => <option key={item.value} value={item.value}>{item.label || item.value}</option>)}</select></label>
      <label className="text-xs font-bold uppercase text-slate-500">School Type<select value={schoolType} onChange={e => { setSchoolType(Number(e.target.value)); setRows([]) }} className="mt-1 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm dark:border-white/15 dark:bg-[#1e2238] dark:text-slate-100">{schoolTypes.map(item => <option key={item.value} value={item.value}>{item.label}</option>)}</select></label>
      <label className="text-xs font-bold uppercase text-slate-500">Class<select value={classId} onChange={e => { setClassId(e.target.value); setRows([]) }} className="mt-1 w-full rounded-lg border border-slate-200 px-3 py-2 text-sm dark:border-white/15 dark:bg-[#1e2238] dark:text-slate-100"><option value="">Select class</option>{meta.classes.map(item => <option key={item.id} value={item.id}>{item.label || item.name}</option>)}</select></label>
      <div className="flex items-end gap-2"><button type="button" onClick={loadCharges} disabled={loading} className="flex flex-1 items-center justify-center gap-2 rounded-lg bg-blue-600 px-3 py-2 text-sm font-semibold text-white disabled:opacity-50">{loading ? <Loader2 className="h-4 w-4 animate-spin" /> : <Eye className="h-4 w-4" />}Load</button><button type="button" onClick={() => { setRows([]); setClassId('') }} className="rounded-lg bg-slate-100 p-2 text-slate-600 dark:bg-slate-800 dark:text-slate-300"><RefreshCw className="h-4 w-4" /></button></div>
    </div></div>
    <div className="flex gap-1 rounded-xl bg-slate-100 p-1 dark:bg-slate-800"><button type="button" onClick={() => setTab('charges')} className={`flex-1 rounded-lg px-3 py-2 text-sm font-semibold ${tab === 'charges' ? 'bg-white text-blue-600 shadow-sm dark:bg-[#1e2238]' : 'text-slate-500'}`}>Fee Charges</button><button type="button" onClick={() => setTab('chart')} className={`flex-1 rounded-lg px-3 py-2 text-sm font-semibold ${tab === 'chart' ? 'bg-white text-blue-600 shadow-sm dark:bg-[#1e2238]' : 'text-slate-500'}`}>Fee Chart</button></div>
    {tab === 'charges' && <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm dark:border-white/10 dark:bg-[#1a1f35]">{rows.length ? <><div className="flex items-center justify-between border-b border-slate-100 px-4 py-3 dark:border-white/10"><p className="text-sm font-bold text-slate-700 dark:text-slate-200">Fee heads for selected class</p><button type="button" onClick={saveAll} className="flex items-center gap-2 rounded-lg bg-emerald-600 px-3 py-2 text-xs font-semibold text-white"><Save className="h-3.5 w-3.5" />Save All</button></div><div className="overflow-x-auto"><table className="w-full"><thead><tr className="bg-slate-50 text-left text-[11px] uppercase text-slate-500 dark:bg-white/[.03] dark:text-slate-400"><th className="px-3 py-2">ID</th><th className="px-3 py-2">Fee Head</th><th className="px-3 py-2">Fee Type</th><th className="px-3 py-2">Charges</th><th className="px-3 py-2">Action</th></tr></thead><tbody>{rows.map(row => <ChargeRow key={row.feeHeadId} row={row} onChange={updateValue} onUpdate={saveRow} busy={busyId === row.feeHeadId} />)}</tbody></table></div></> : <p className="p-6 text-center text-sm text-slate-500">Select session, school type and class, then click Load.</p>}</div>}
    {tab === 'chart' && <div className="rounded-2xl border border-slate-200 bg-white shadow-sm dark:border-white/10 dark:bg-[#1a1f35]"><Chart rows={rows} installments={installments} /></div>}{toast && <Toast {...toast} onClose={() => setToast(null)} />}</div>
}
