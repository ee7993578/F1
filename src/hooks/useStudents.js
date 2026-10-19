import { useCallback, useEffect, useState } from 'react'
import { apiFetch } from '../lib/api'

const asText = value => value == null ? '' : String(value)

export function normalizeStudent(row = {}) {
  const firstName = asText(row.firstName)
  const middleName = asText(row.middleName)
  const lastName = asText(row.lastName)
  const fullName = [firstName, middleName, lastName].filter(Boolean).join(' ')
  const status = asText(row.status)
  const studentId = row.studentId ?? row.id ?? row.uid ?? ''
  const registrationNo = row.registrationNo ?? row.regNo ?? row.registration_no ?? row.admissionNo ?? ''
  const className = asText(row.className ?? row.class_name ?? row.class)
  const sectionName = asText(row.sectionName ?? row.section_name ?? row.section)
  const fatherName = asText(row.fatherName ?? row.father_name)
  const name = fullName || asText(row.name ?? row.studentName ?? row.student_name)
  const statusValue = status || asText(row.StudentStatus ?? row.admissionStatus ?? row.AdmissionStatus)
  return {
    ...row,
    studentId,
    id: studentId,
    uid: row.uid ?? studentId,
    registrationNo,
    registration_no: registrationNo,
    admissionNo: registrationNo,
    admission_no: registrationNo,
    form_no: asText(row.form_no ?? row.formNo ?? registrationNo),
    registrationNO: registrationNo,
    Registration_NO: registrationNo,
    name,
    studentName: name,
    student_name: name,
    Name: name,
    Student_Name: name,
    firstName,
    middleName,
    lastName,
    fatherName,
    father_name: fatherName,
    FatherName: fatherName,
    father: fatherName,
    mobile: asText(row.mobile || row.phone),
    phone: asText(row.phone || row.mobile),
    rollNo: asText(row.rollNo ?? row.roll_no),
    feeBookNo: asText(row.feeBookNo ?? row.fee_book_no),
    motherName: asText(row.motherName ?? row.mother_name),
    mother_name: asText(row.motherName ?? row.mother_name),
    address: asText(row.address ?? row.r_address),
    bloodGroup: asText(row.bloodGroup ?? row.bloodgroup),
    quota: asText(row.quota),
    rte: asText(row.rte),
    status: statusValue,
    StudentStatus: statusValue,
    admissionStatus: statusValue,
    className,
    class_name: className,
    class: className,
    classNameText: className,
    sectionName,
    section_name: sectionName,
    section: sectionName,
    withdrawnDate: row.withdrawnDate ?? row.withdrawn_date ?? '',
    withdrawn_date: row.withdrawnDate ?? row.withdrawn_date ?? '',
    withdrawnReason: asText(row.withdrawnReason ?? row.withdrawn_reason),
    withdrawn_reason: asText(row.withdrawnReason ?? row.withdrawn_reason),
    tcNo: asText(row.tcNo ?? row.tc_no),
    tc_no: asText(row.tcNo ?? row.tc_no),
    registrationDate: row.registrationDate ?? row.registration_date ?? row.Registration_date ?? '',
    Registration_date: row.registrationDate ?? row.registration_date ?? row.Registration_date ?? '',
    registration_date: row.registrationDate ?? row.registration_date ?? row.Registration_date ?? '',
    DOB: row.dob ?? row.DOB ?? '',
    formStatus: asText(row.formStatus ?? row.status),
    paymentStatus: asText(row.paymentStatus ?? (row.receiptNo ? 'Success' : '')),
    paymentAmount: row.paymentAmount ?? row.registrationFee ?? row.regFee ?? '',
    reg_fee: Number(row.reg_fee ?? row.regFee ?? row.registrationFee ?? 0) || 0,
    studentType: asText(row.studentType ?? row.student_type),
    AdmissionStatus: asText(row.admissionStatus ?? row.status),
    session: asText(row.session),
    dobText: row.dob ? new Date(row.dob).toLocaleDateString('en-IN') : '',
    studentImage: row.studentImage || null,
    studentimage: row.studentImage || null,
    active: statusValue.toLowerCase() === 'registered',
  }
}

function normalizeBootstrap(value) {
  return {
    ...value,
    classes: (value?.classes || []).map(item => ({
      ...item,
      classId: item.classId ?? item.id,
      className: item.className ?? item.class_name ?? '',
    })),
    sections: value?.sections || [],
    sessions: value?.sessions || [],
    students: (value?.students || []).map(normalizeStudent),
  }
}

export function useStudents() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const refresh = useCallback(async () => {
    setLoading(true)
    try {
      const next = normalizeBootstrap(await apiFetch('/students/bootstrap'))
      setData(next)
      setError(null)
      return next
    } catch (err) {
      setError(err)
      throw err
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { refresh().catch(() => {}) }, [refresh])
  return { data, loading, error, refresh }
}

function paramsOf(params = {}) {
  const query = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') query.set(key, value)
  })
  return query.toString()
}

export async function fetchStudentSearch(params = {}) {
  const query = paramsOf(params)
  const rows = await apiFetch('/students/search' + (query ? '?' + query : ''))
  return Array.isArray(rows) ? rows.map(normalizeStudent) : []
}

export async function fetchStudentReport(type, params = {}) {
  const query = paramsOf(params)
  const rows = await apiFetch('/students/reports/' + encodeURIComponent(type) + (query ? '?' + query : ''))
  return Array.isArray(rows) ? rows.map(normalizeStudent) : []
}

export async function fetchStudentDetails(studentId) {
  return normalizeStudent(await apiFetch('/students/' + encodeURIComponent(studentId)))
}
