import { MENU_DATA } from './menuData'

const key = (value) => String(value || '').toLowerCase().replace(/[^a-z0-9]/g, '')

const moduleAliases = {
  examination: 'examMaster',
  fees: 'fee',
  staffmanagement: 'staff',
  admission: 'student',
  webadmin: 'website',
  schooladmin: 'masterSetting',
  expense: 'canteen',
  tuckshop: 'canteen',
}

const moduleIdAliases = {
  1: 'examMaster', 4: 'fee', 5: 'transport', 10: 'library', 11: 'timetable',
  14: 'certificates', 16: 'communication', 19: 'attendance', 24: 'leave',
  28: 'canteen', 31: 'website', 32: 'masterSetting', 6: 'hostel',
}

// The legacy database contains several Fee page keys that do not match the
// React page IDs character-for-character. Keep these aliases at the menu
// boundary so the actual page workflow is selected instead of GenericPage.
const legacyPageAliases = Object.fromEntries([
  // The legacy Staff page contains the original spelling typo
  // `faculty-registartion.aspx`; map it to the React Faculty Registration page.
  ['faculty-registartion', 'FacultyRegistration'],
  ['facultyregistration', 'FacultyRegistration'],
  ['faculty-registartion.aspx', 'FacultyRegistration'],
  ['faculty_reregistration', 'FacultyReregistration'],
  ['faculty-infosheet', 'FacultyInfosheet'],
  ['facultyinfo_download', 'FacultyInfoDownload'],
  ['faculty_id_pwd', 'FacultyIdPassword'],
  ['JobApplication', 'JobApplicationReport'],
  ['Class_teacher', 'ClassTeacherReport'],
  ['Define_Class_Teacher', 'DefineClassTeacher'],
  ['jobVacancyFormConfiguration', 'RegistrationOpenConfig'],
  ['define_late_fees', 'DefineLateFee'],
  ['define_fee_concession', 'DefineConcession'],
  ['define_con_group', 'DefineConcessionGroup'],
  ['define_concession_value', 'DefineConcessionValue'],
  ['depositfee', 'DepositFee'],
  ['deposit-late-fees', 'DepositLateFeesFee'],
  ['Refundable_Fees', 'RefundableFees'],
  ['importexcel_database', 'ImportExcelFee'],
  ['stu_miscellaneous_fee', 'MiscellaneousFee'],
  ['importexcel_for_settlement', 'FeeSettlement'],
  ['getLateFee', 'LateFeeReportWork'],
  ['EmailReport', 'EmailReport'],
  ['smsreport_new', 'SmsReport'],
  ['sentfeesms', 'SendSMS'],
  ['WaveOffLateFees', 'WaveOffLateFees'],
  ['WaveOffFees', 'WaveOffFees'],
  ['AssignOptional', 'AssignOptional'],
  ['desig_permision', 'DefineStaffRole'],
  ['Roles_Permission', 'RolesPermission'],
  ['AssginConGroup', 'AssignConGroup'],
  ['Fee_notice', 'FeeNotice'],
  ['reprint_fee_slip', 'ReprintFeeSlip'],
  ['update_student_fee', 'UpdateStudentFee'],
  ['classwise_fee_details_report', 'ClasswiseFeeReport'],
  ['Concession_group_report', 'ConcessionGroupReport'],
  ['DefaulterReport', 'FeeDefaulterReport'],
  ['defaulter-report_renssance', 'FeeDefaulterReport'],
  ['BudgetReport', 'BudgetReport'],
  ['total_fee_colllection', 'DailyCollectionReport'],
  ['day_wise_feehead_deposit', 'DailyDepositReport'],
  ['Class_wise_fee_collection_report', 'ClasswiseFeeReport'],
  ['ConcessionReport', 'ConcessionReport'],
  ['DailyDepositReport', 'DailyDepositReport'],
  ['feeReconcialtionReport', 'FeeReconciliationReport'],
  ['PaymentResponseReport', 'PaymentResponseReport'],
  ['Installment_wise_defaulter', 'InstallmentWiseDefaulter'],
  ['headwis_defaulter_report(instwise)', 'HeadwiseDefaulter'],
  ['late_fee_report', 'LateFeeReport'],
  ['StudentFeeSummary', 'StudentFeeSummary'],
  ['driver_vehicle_details', 'DriverVehicleMaster'],
  ['driver_vehicle_details.aspx', 'DriverVehicleMaster'],
  ['drivervehiclemaster', 'DriverVehicleMaster'],
  ['driverdetails', 'DriverVehicleMaster'],
  ['vehicleMaster', 'VehicleMaster'],
  ['vehiclemaster.aspx', 'VehicleMaster'],
  ['assign_transport', 'AssignTransport'],
  ['assign_transport.aspx', 'AssignTransport'],
  // Exam Master pages are returned by the legacy menu with ASPX-era keys.
  // Normalize them here so React never falls through to the placeholder page.
  ['marks_status_subjectwise_(Graph)', 'MarksStatusGraph'],
  ['marks_status_subjectwise_graph', 'MarksStatusGraph'],
  ['define-marks_new', 'DefineMarksNew'],
  ['preprimary_marks', 'DefineMarksPrePrimary'],
  ['Preprimary_marks_mapping_rmps', 'PreprimaryMarksMapping'],
  ['define_stu_remark', 'DefineStudentRemark'],
  ['define_indicator_grade', 'DefineIndicatorGradePrimary'],
  ['Upload-marks', 'UploadMarks'],
  ['define_Learnerprofile_indicatorgrade', 'DefineLearnerProfileIndicator'],
  ['defineReportcardCategoryRemark', 'DefineReportcardCategoryRemark'],
  ['stu_health_status', 'StuHealthStatus'],
  ['define_max_min_primary', 'DefineMaxMinExam'],
  ['header', 'DefineHeader'],
  ['category', 'DefineCategory'],
  ['define-attrib', 'DefineAttrib'],
  ['indicator', 'Indicator'],
  ['indicator_category', 'IndicatorCategory'],
  ['DefineWeightage_new', 'DefineWeightage'],
  ['Define_Max_Min_new', 'DefineMaxMin'],
  ['Lock_and_Unlock_student', 'LockUnlockStudent'],
  ['CBSE_AISSE_Result_OVER_ALL', 'CBSE_AISSE_Result'],
  ['CBSE_AISSE_Result_MainSubject', 'CBSE_AISSE_Result_MainSubject'],
  ['CBSE_annual_report', 'CBSE_AnnualReport'],
  ['Class_overall_marks', 'Class_overall_marks'],
  ['CumulativeSheet', 'CumulativeSheet'],
  ['GetClassSubjects', 'GetClassSubjects'],
  ['MarksLogReport', 'MarksLogReport'],
  ['MarkStatusReport', 'MarkStatusReport'],
  ['overall_examination_report', 'OverallExaminationReport'],
  ['ReportCard', 'ReportCard'],
  ['reportcard_examwise', 'ReportCardExamwise'],
  ['studentwise_academic_performance', 'StudentAcademicPerformance'],
  ['subject_report', 'SubjectReport'],
  ['ToppersReport', 'ToppersReport'],
  ['marks_percentage_report_examwise', 'ClasswiseStudentPercentage'],
  ['ExamComparativeSheet', 'ExamComparativeSheet'],
  ['examwise_greensheet', 'ExamwiseGreensheet'],
  ['Report_Expected_Board_Average', 'ExpectedBoardAverageReport'],
  ['ExpectedSchoolAverageReport', 'ExpectedSchoolAverageReport'],
  ['failure_student_report', 'FailureStudentReport'],
  ['greenSheet_new', 'GreenSheetNew'],
  ['greensheet_primary', 'GreenSheetPrimary'],
  ['GreenSheet_SubjectWIse', 'GreenSheetSubjectWise'],
  ['marks_list_blank', 'MarksListBlank'],
  ['marks_list_junior', 'MarksListJunior'],
  ['Report_ExamResultDiscussion', 'Report_ExamResultDiscussion'],
  ['allstu_per', 'ClasswiseStudentPercentage'],
  ['deletemarks', 'DeleteMarks'],
  // Student pages are returned by the live menu with their ASPX-era keys.
  // Keep the database key at this boundary, then render the React page below.
  ['student-registration', 'studentRegistration'],
  ['adm_no_allotment', 'AdmNoAllotment'],
  ['adm_no_allotment_for_t_stu', 'TransferredStudentAdmNoAllotment'],
  ['new_class_allotment', 'newClassAllotment'],
  ['Icard', 'StudentICard'],
  ['bulkAdmissionOnlineStudentRegistration', 'BulkAdmissionOnlineRegistration'],
  ['Update_studentinfo', 'UpdateStudentInfo'],
  ['upload_studentimage', 'UploadStudentImage'],
  ['Update_stu_infosheet_report', 'UpdateStudentInfosheet'],
  ['change_adm_no', 'ChangeAdmNo'],
  ['student_withdrawn', 'StudentWithdrawn'],
  ['Update_studentinfo_withdrawn', 'UpdateStudentInfoWithdrawn'],
  ['promote_demote', 'PromoteDemote'],
  ['update_alumni_record', 'UpdateAlumniRecord'],
  ['download_student_images', 'DownloadStudentImages'],
  ['promote_demote_new', 'TransferStudentBranch'],
  ['AluminiForm', 'AlumniRegistration'],
  ['class_representative', 'ClassRepresentative'],
  ['examConfiguration', 'ExamConfiguration'],
  ['define_transport_type', 'DefineTransportType'],
  ['route_stoppage_mapping', 'RouteStoppageMapping'],
  ['student_transport_report', 'StudentTransportReport'],
  ['monthwise_transport_report', 'MonthwiseTransportReport'],
  ['feecollection_routewise', 'RouteWiseCollectionReport'],
  ['total_fee_colllection', 'RouteWiseTotalCollectionReport'],
  ['class_activity', 'ClassActivity'],
  ['faculty_free_lec', 'FacultyFreePeriod'],
  ['facultywisetimetable_report', 'CompleteTimeTableReport'],
  ['arrangement', 'ArrangementReport'],
].map(([legacy, reactId]) => [key(legacy), reactId]))

const field = (value, ...names) => {
  for (const name of names) {
    if (value && value[name] !== undefined && value[name] !== null) return value[name]
  }
  return ''
}

const pagePath = (page) => field(page, 'url', 'pageUrl', 'PageUrl', 'pageurl', 'path', 'href')
const pageName = (page) => field(page, 'pageKey', 'pageName', 'PageName', 'page_name', 'name')
const pageFileKey = (page) => key(pageName(page) || String(pagePath(page)).split(/[/?#]/).pop()?.replace(/\.aspx$/i, ''))

const findStaticModule = (module) => {
  const preferredId = moduleIdAliases[module.moduleId] || moduleAliases[key(module.label)]
  return MENU_DATA.find((item) => item.id === preferredId)
    || MENU_DATA.find((item) => key(item.label) === key(module.label))
}

const groupAlias = (value) => {
  const normalized = key(value)
  if (normalized === 'transaction' || normalized === 'transactions' || normalized === 'work') return 'work'
  if (normalized === 'setting' || normalized === 'settings' || normalized === 'setup') return 'setup'
  if (normalized === 'report' || normalized === 'reports') return 'report'
  if (normalized === 'update' || normalized === 'updates') return 'update'
  return normalized
}

// Exam Master still stores its submenu parents as database rows such as
// `Marks-Entry`, `Setting - Primary` and `Marks Edit/ Delete`.  The API
// normalizes some of those rows to "Work", which loses the actual parent
// identity before the React menu is built.  Keep the legacy parent IDs as the
// source of truth for this module so ASPX page keys land in the JSX group that
// has the corresponding workflow.
const examLegacyGroups = {
  '1': 'exam_report',
  '8558': 'exam_report', // Reports-Cumulative-Sheet
  '8555': 'exam_report', // MarkListSubSubject
  '8560': 'exam_report', // Reports
  '8565': 'exam_work',   // Marks-Entry / Transaction
  '8568': 'exam_setup',  // Setting - Primary
  '8569': 'exam_setup',  // Setting - Security
  '8592': 'exam_setup',  // Setting - Basic
  '8575': 'exam_update', // Marks Edit/ Delete
}

const findStaticGroup = (staticModule, group) => {
  if (!staticModule) return null
  const wanted = groupAlias(field(group, 'label', 'name', 'groupName'))
  return staticModule.groups.find((item) => groupAlias(item.label) === wanted)
    || staticModule.groups.find((item) => groupAlias(item.id) === wanted)
}

const allStaticPages = (staticModule, staticGroup) => [
  ...(staticGroup?.pages || []),
  ...(staticModule?.groups || []).flatMap((group) => group.pages || []),
  ...MENU_DATA.flatMap((module) => module.groups.flatMap((group) => group.pages || [])),
]

const findStaticPage = (staticModule, staticGroup, page) => {
  const candidates = allStaticPages(staticModule, staticGroup)
  const pageKey = pageFileKey(page)
  const labelKey = key(field(page, 'label', 'title', 'text', 'pageName', 'PageName'))
  const aliasId = legacyPageAliases[pageKey]
  return candidates.find((item) => aliasId && item.id === aliasId)
    || candidates.find((item) => key(item.id) === pageKey)
    || candidates.find((item) => key(item.label) === pageKey)
    || candidates.find((item) => labelKey && key(item.label) === labelKey)
}

const staticGroupForPage = (staticModule, staticPage) => {
  if (!staticModule || !staticPage) return null
  return staticModule.groups.find((group) => (group.pages || []).some((page) => page.id === staticPage.id))
}

const isReportPath = (page) => /(?:^|[\\/])reports?(?:[\\/]|$)/i.test(String(pagePath(page)))

const inferStaticGroup = (staticModule, sourceGroup, page, staticPage) => {
  if (staticModule?.id === 'examMaster') {
    const sourceId = String(field(sourceGroup, 'id', 'groupId')).trim()
    const groupId = examLegacyGroups[sourceId]
    if (groupId) return staticModule.groups.find((group) => group.id === groupId) || null
  }
  // Page identity/path wins over the old database group label. The legacy menu
  // sometimes puts Setup pages in a Transaction/Work row.
  return staticGroupForPage(staticModule, staticPage)
    || (isReportPath(page) && staticModule?.groups.find((group) => groupAlias(group.label) === 'report'))
    || findStaticGroup(staticModule, sourceGroup)
}

const pageIdentity = (page) => page.id || page.pageId || page.page_id || pageFileKey(page)

export function resolveMenu(serverMenu) {
  const modules = (serverMenu?.modules || []).map((module) => {
    const staticModule = findStaticModule(module)
    const buckets = new Map()

    for (const sourceGroup of module.groups || []) {
      for (const page of sourceGroup.pages || []) {
        const staticPage = findStaticPage(staticModule, findStaticGroup(staticModule, sourceGroup), page)
        const staticGroup = inferStaticGroup(staticModule, sourceGroup, page, staticPage)
        const groupId = staticGroup?.id || ('db-group-' + module.moduleId + '-' + (sourceGroup.id || sourceGroup.groupId || groupAlias(sourceGroup.label)))
        let bucket = buckets.get(groupId)
        if (!bucket) {
          bucket = {
            ...sourceGroup,
            id: groupId,
            label: staticGroup?.label || sourceGroup.label || 'Group',
            icon: sourceGroup.icon || staticGroup?.icon || 'Folder',
            pages: [],
          }
          buckets.set(groupId, bucket)
        }

        const resolvedPage = {
          ...page,
          id: staticPage?.id || legacyPageAliases[pageFileKey(page)] || page.id || ('db-page-' + pageIdentity(page)),
          label: page.label || staticPage?.label || 'Page',
        }
        if (!bucket.pages.some((item) => item.id === resolvedPage.id)) bucket.pages.push(resolvedPage)
      }
    }

    const groups = [...buckets.values()]
    groups.sort((a, b) => {
      const ai = staticModule?.groups.findIndex((group) => group.id === a.id) ?? -1
      const bi = staticModule?.groups.findIndex((group) => group.id === b.id) ?? -1
      if (ai < 0 && bi < 0) return 0
      if (ai < 0) return 1
      if (bi < 0) return -1
      return ai - bi
    })

    return {
      ...module,
      id: staticModule?.id || ('db-module-' + module.moduleId),
      label: module.label || staticModule?.label || 'Module',
      groups,
    }
  })
  return modules.length ? modules : MENU_DATA
}
