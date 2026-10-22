import { useEffect, useState } from 'react'
import { apiFetch } from '../../../../lib/api'
import { Button, Card, Field, Loading, Page, Select, Table, exportExcel, input, options } from '../StaffShared'
export default function ClassTeacherReport(){
 const [boot,setBoot]=useState({}),[session,setSession]=useState(''),[rows,setRows]=useState([]),[canView,setCanView]=useState(false),[loading,setLoading]=useState(true),[error,setError]=useState('')
 const load=async s=>{if(!s)return;setLoading(true);try{const r=await apiFetch(`/staff/reports/class-teachers-view?session=${encodeURIComponent(s)}`);setRows(r.rows||[]);setCanView(Boolean(r.canViewCredentials))}catch(e){setError(e.message)}finally{setLoading(false)}}
 useEffect(()=>{(async()=>{try{const b=await apiFetch('/staff/bootstrap?light=true');setBoot(b);const s=b.sessions?.find(x=>String(x.status).toLowerCase().includes('default')||String(x.status).toLowerCase().includes('active'))?.value??b.sessions?.[0]?.value??'';setSession(s);await load(s)}catch(e){setError(e.message)}finally{setLoading(false)}})()},[])
 const cols=[{key:'className',label:'Class'},{key:'sectionName',label:'Section'},{key:'facultyName',label:'Name'}];if(canView)cols.push({key:'userId',label:'Faculty ID'},{key:'password',label:'Faculty Password'});
 if(loading)return <Page title="Class Teacher Report"><Loading/></Page>
 return <Page title="Class Teacher Report" description="Class teacher assignments for the selected session." error={error}><Card className="p-4"><div className="flex flex-wrap items-end gap-3"><Field label="Session"><Select value={session} onChange={e=>{setSession(e.target.value);load(e.target.value)}} options={options(boot.sessions||[])}/></Field><Button kind="secondary" disabled={!rows.length} onClick={()=>exportExcel('class-teacher-report',rows,cols)}>Excel</Button></div></Card><Card className="overflow-hidden"><Table searchable searchPlaceholder="Search class teacher report..." columns={cols} rows={rows}/></Card></Page>
}
