import { apiFetch } from './api'

const text = value => value == null ? '' : String(value)

// Shape the registration-search row to the field names used by the two
// legacy online-registration reports.  The source remains the live
// student_reg row; this is only a view-model adapter for the JSX table.
export function normalizeOnlineRegistration(row = {}) {
  const rawStatus = text(row.approvalStatus || row.admissionStatus || row.AdmissionStatus || row.registrationStatus || row.status).trim()
  const lowerStatus = rawStatus.toLowerCase()
  const registrationStatus = lowerStatus.includes('reject') ? 'Reject'
    : lowerStatus.includes('confirm') || lowerStatus.includes('admit') ? 'Confirm'
    : lowerStatus.includes('accept') ? 'Accept'
    : rawStatus ? 'Accept' : 'Not Select'
  const paymentValue = row.paymentAmount ?? row.amount
  const paymentStatus = text(row.paymentStatus || row.PaymentStatus).trim()
    || (text(row.paymentUid || row.receiptNo || row.transactionNo || paymentValue).trim() ? 'Success' : 'Pending')
  const formStatus = text(row.formStatus || row.applicationStatus || row.registrationType).trim()
    || (row.onlineRegNo || row.registrationNo ? 'Completed' : 'Started')
  const date = row.registrationDate || row.registration_date || row.Registration_date || ''
  return {
    ...row,
    id: row.id ?? row.studentId ?? row.rowId ?? '',
    registrationNO: text(row.onlineRegNo || row.registrationNO || row.registrationNo || row.formNo),
    regPhone: text(row.registrationPhone || row.mobile || row.phone),
    class: text(row.className || row.class_name || row.class),
    Name: text(row.name || [row.firstName, row.middleName, row.lastName].filter(Boolean).join(' ')),
    dob: text(row.dob || row.DOB),
    studentEmail: text(row.studentEmail || row.email || row.registrationFatherEmail),
    gender: text(row.gender),
    FatherName: text(row.fatherName || row.father_name || row.registrationFatherName),
    fatherMobile: text(row.fatherMobile || row.phone),
    motherName: text(row.motherName || row.mother_name),
    motherPhone: text(row.motherMobile || row.motherPhone),
    residentialAddr: text(row.address || row.r_address),
    contactPerName: text(row.contactPerName),
    contactPhone: text(row.contactPhone),
    stream: text(row.stream),
    formStatus,
    applicationDate: text(date).slice(0, 10),
    paymentOrderId: text(row.paymentUid || row.orderId),
    paymentStatus,
    paymentAmount: text(paymentValue),
    paymentDate: text(row.paymentDate || row.transactionDate).slice(0, 10),
    settlementDate: text(row.settlementDate),
    registrationStatus,
    AdmissionStatus: registrationStatus,
    allotedadmno: text(row.admissionNo || row.stuAdmissionNo || row.registrationNo),
    studentType: text(row.studentType || row.student_type),
  }
}

const bootstrap = () => apiFetch('/students/bootstrap')

const legacySection = section => ({
  ...section,
  sec_id: section.sectionId ?? section.sec_id,
  section: section.sectionName ?? section.section ?? '',
  class_id: section.classId ?? section.class_id,
  class_name: section.className ?? section.class_name ?? '',
})

export async function studentApi(path, options = {}) {
  const request = { ...options }
  if (request.body && typeof request.body === 'object' && !(request.body instanceof FormData)) {
    request.body = JSON.stringify(request.body)
  }

  if (path === '/menu/session') {
    const menu = await apiFetch('/menu')
    return { session: menu.session || '' }
  }
  if (path === '/master/sessions') {
    const data = await bootstrap()
    return data.sessions || []
  }
  if (path === '/master/classes') {
    const data = await bootstrap()
    return (data.classes || []).map(row => ({ ...row, id: row.classId ?? row.id, class_name: row.className ?? row.class_name }))
  }
  if (path === '/master/houses') {
    const data = await bootstrap()
    return (data.houses || []).map(row => ({ ...row, id: row.houseId ?? row.id, house_name: row.houseName ?? row.house_name }))
  }
  if (path === '/master/sections') {
    const data = await bootstrap()
    return (data.sections || []).map(legacySection)
  }
  if (path.startsWith('/admission/sections?')) {
    const query = new URLSearchParams(path.split('?')[1] || '')
    const classId = query.get('classId')
    const data = await bootstrap()
    return (data.sections || []).filter(row => !classId || String(row.classId) === String(classId)).map(legacySection)
  }
  if (path === '/admission/terms') {
    return apiFetch('/students/terms')
  }
  if (path === '/admission/lookups') {
    const [data, lookups] = await Promise.all([bootstrap(), apiFetch('/students/registration-options')])
    return {
      sessions: data.sessions || [],
      classes: (data.classes || []).map(row => ({ ...row, id: row.classId ?? row.id, class_name: row.className ?? row.class_name })),
      banks: lookups.banks || [],
      studentTypes: lookups.types || [],
    }
  }
  if (path.startsWith('/admission/online-confirm')) {
    return apiFetch('/students/online-registration/confirm', request)
  }
  if (path.startsWith('/admission/online-status')) {
    return apiFetch('/students/online-registration/status', request)
  }
  if (path.startsWith('/admission/online-payment')) {
    return apiFetch('/students/online-registration/payment', request)
  }
  if (path === '/admission/withdraw') {
    return apiFetch('/students/withdraw', request)
  }
  const registrationMatch = path.match(/^\/admission\/registrations\/(\d+)(?:\/[^?]+)?(?:\?.*)?$/)
  if (registrationMatch) {
    const detail = await apiFetch('/students/online-registration/' + registrationMatch[1])
    return { fields: detail }
  }
  return apiFetch(path, request)
}
