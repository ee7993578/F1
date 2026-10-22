import { useEffect, useState } from 'react'
import { apiFetch } from '../../../../lib/api'
import { Button, Card, Empty, Field, Loading, Page, Table, dateOnly, displayDate, input } from '../StaffShared'

const detailFields=[['facultyId','Faculty ID'],['firstName','First Name'],['middleName','Middle Name'],['lastName','Last Name'],['dob','Date of Birth'],['gender','Gender'],['bloodGroup','Blood Group'],['graduation','Graduation'],['education','Post Graduation'],['experience','Experience'],['department','Department'],['designation','Designation'],['doj','Joining Date'],['district','District'],['city','City'],['mobile','Mobile'],['email','Email'],['aadhar','Aadhar'],['pan','PAN'],['accountNo','Account No.'],['ifsc','IFSC']]
export default function FacultyReregistration(){
  const [rows,setRows]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(''),[message,setMessage]=useState(''),[selected,setSelected]=useState([]),[detail,setDetail]=useState(null),[search,setSearch]=useState(''),[saving,setSaving]=useState(false)
  const load=async()=>{setLoading(true);setError('');try{setRows(await apiFetch('/staff?withdrawn=true'))}catch(e){setError(e.message)}finally{setLoading(false)}};useEffect(()=>{load()},[])
  const filtered=rows.filter(r=>`${r.name||''} ${r.email||''} ${r.mobile||''}`.toLowerCase().includes(search.toLowerCase()))
  const openDetail=async r=>{try{setDetail(await apiFetch(`/staff/${r.id??r.f_id}`))}catch(e){setError(e.message)}}
  const toggle=id=>setSelected(s=>s.includes(id)?s.filter(x=>String(x)!==String(id)):[...s,id])
  const reRegister=async()=>{if(!selected.length)return setError('Select at least one faculty.');setSaving(true);try{for(const id of selected)await apiFetch(`/staff/${id}/reregister?session=${encodeURIComponent('')}`,{method:'POST'});setMessage('Selected faculty re-registered successfully');setSelected([]);await load()}catch(e){setError(e.message)}finally{setSaving(false)}}
  const cols=[{key:'check',label:'',render:r=><input type="checkbox" checked={selected.includes(r.id??r.f_id)} onChange={()=>toggle(r.id??r.f_id)}/>},{key:'name',label:'Faculty Name'},{key:'email',label:'Email'},{key:'mobile',label:'Mobile'},{key:'dob',label:'DOB',render:r=>displayDate(r.dob)},{key:'remark',label:'Reason'}]
  if(loading)return <Page title="Faculty Re-Registration"><Loading/></Page>
  return <Page title="Faculty Re-Registration" description="Withdrawn faculty for the current session, matching the legacy re-registration grid." message={message} error={error} actions={<div className="flex gap-2"><input className={`${input} max-w-56`} placeholder="Search..." value={search} onChange={e=>setSearch(e.target.value)}/><Button onClick={reRegister} disabled={saving}>{saving?'Saving...':'Re Register'}</Button></div>}>
    <Card className="overflow-hidden"><Table columns={cols} rows={filtered} actions={r=><Button kind="secondary" onClick={()=>openDetail(r)}>Details</Button>}/></Card>
    {!filtered.length&&<Card><Empty text="No withdrawn faculty found."/></Card>}
    {detail&&<div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 p-3" onClick={()=>setDetail(null)}><div className="max-h-[92vh] w-full max-w-4xl overflow-auto rounded-xl bg-white p-4 dark:border dark:border-[rgba(99,102,241,0.2)] dark:bg-[#1a1f35]" onClick={e=>e.stopPropagation()}><div className="mb-3 flex items-center justify-between"><h2 className="font-bold text-slate-800 dark:text-slate-100">Faculty Details</h2><button onClick={()=>setDetail(null)} className="text-xl text-slate-500 dark:text-slate-400">×</button></div><div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">{detailFields.map(([key,label])=><Field key={key} label={label}><input className={input} readOnly value={key==='dob'||key==='doj'?dateOnly(detail[key]):detail[key]??''}/></Field>)}</div><div className="mt-4 text-right"><Button kind="secondary" onClick={()=>setDetail(null)}>Cancel</Button></div></div></div>}
  </Page>
}
