import GenericPage from './GenericPage'
import StudentPortalExact from './StundentPages/StudentPortalExact'

// The legacy menu returns ASPX file names in different fields depending on
// the school/menu record. Resolve those names at render time as a second line
// of defence, so a page can never fall through merely because its database
// id was not normalized by menuResolver.
const legacyTransportPageIds = {
  vehiclemaster: 'VehicleMaster',
  vehiclemasteraspx: 'VehicleMaster',
  drivervehicledetails: 'DriverVehicleMaster',
  drivervehicledetailsaspx: 'DriverVehicleMaster',
  driverdetail: 'DriverVehicleMaster',
  driverdetails: 'DriverVehicleMaster',
  drivervehiclemaster: 'DriverVehicleMaster',
  assigntransport: 'AssignTransport',
  assigntransportaspx: 'AssignTransport',
}

const legacyStudentPageIds = {
  admnoallotmentfortstu: 'TransferredStudentAdmNoAllotment',
  updatestudentinfowithdrawn: 'UpdateStudentInfoWithdrawn',
  updatealumnirecord: 'UpdateAlumniRecord',
  downloadstudentimages: 'DownloadStudentImages',
  promotedemotenew: 'TransferStudentBranch',
  updatestudentinfo: 'UpdateStudentInfo',
  uploadstudentimage: 'UploadStudentImage',
  updatestuinfosheetreport: 'UpdateStudentInfosheet',
  changeadmno: 'ChangeAdmNo',
  studentwithdrawn: 'StudentWithdrawn',
  promotedemote: 'PromoteDemote',
}

const legacyAttendancePageIds = {
  attreportpercentage: 'AttendanceReportPercentage',
  attreportpercentageaspx: 'AttendanceReportPercentage',
  attendancereportpercentage: 'AttendanceReportPercentage',
  attendancereportin: 'AttendanceReportPercentage',
  attendancereportinpercentage: 'AttendanceReportPercentage',
  attendancestatus: 'AttendanceStatusReport',
  attendancestatusaspx: 'AttendanceStatusReport',
  checkattstatus: 'AttendanceStatusReport',
  checkattstatusaspx: 'AttendanceStatusReport',
  totalattendancereport: 'TotalAttendanceReport',
  totalattendancereportaspx: 'TotalAttendanceReport',
  classwisetotalattreport: 'TotalAttendanceReport',
  classwisetotalattreportaspx: 'TotalAttendanceReport',
}

const legacyTimetablePageIds = {
  alttimetable: 'AltTimetable',
  assigntimetable: 'AssignTimeTable',
  classactivity: 'ClassActivity',
  createvirtualclass: 'CreateVirtualClass',
  defineholidays: 'DefineHolidays',
  facultysubjectmapping: 'FacultySubjectMapping',
  lecturetimings: 'LectureTimings',
  activityreport: 'ActivityReport',
  arrangement: 'ArrangementReport',
  facultyfreelec: 'FacultyFreePeriod',
  facultywisetimetablereport: 'CompleteTimeTableReport',
  schooltimetablereport: 'SchoolTimetableReport',
  timetablereport: 'TimetableReport',
  virtualclassreport: 'VirtualClassReport',
}

const legacyLeavePageIds = {
  leavetype: 'LeaveType',
  wingincharge: 'WingIncharge',
  leaveallocation: 'LeaveAllocation',
  leaveapplyform: 'LeaveApplyForm',
  leaveform: 'LeaveForm',
  leavestaffapproval: 'LeaveStaffApproval',
  facultytotalleave: 'FacultyTotalLeave',
  leaveapproval: 'LeaveApproval',
  leaveapprovalbyprincipal: 'LeaveApprovalByPrincipal',
  leavereport: 'LeaveReport',
  indentform: 'IndentForm',
  indentformlist: 'IndentFormList',
  indentformapproval: 'IndentFormApproval',
  indentformapprovalprincipal: 'IndentFormApprovalPrincipal',
}

const legacyHostelPageIds = {
  assignhostel: 'AssignHostel',
  assignhostelaspx: 'AssignHostel',
  hostelfees: 'HostelFees',
  hostelfeesaspx: 'HostelFees',
  hostelassignedreport: 'HostelAssignedReport',
  hostelassignedreportaspx: 'HostelAssignedReport',
}

const legacyStudentPortalPageIds = {
  student: 'StudentPortalDashboard',
  studentaspx: 'StudentPortalDashboard',
  showsyllabus: 'StudentPortalSyllabus',
  showcircular: 'StudentPortalCirculars',
  holidayhomework: 'StudentPortalHolidayHomework',
  dailyhomework: 'StudentPortalDailyHomework',
  showassignment: 'StudentPortalAssignments',
  initiatestudentpayment: 'StudentPortalFees',
  attendancereport: 'StudentPortalAttendance',
  timetable: 'StudentPortalTimetable',
  examschedule: 'StudentPortalExamSchedule',
  acedmicperformance: 'StudentPortalAcademicPerformance',
}

const resolvedPageId = (page) => {
  const values = [page?.id, page?.pageId, page?.pageKey, page?.pageName, page?.url, page?.label]
    .filter(Boolean)
    .map((value) => String(value).toLowerCase().replace(/[^a-z0-9]/g, ''))
  for (const value of values) {
    if (legacyTransportPageIds[value]) return legacyTransportPageIds[value]
    if (legacyStudentPageIds[value]) return legacyStudentPageIds[value]
    if (legacyAttendancePageIds[value]) return legacyAttendancePageIds[value]
    if (legacyTimetablePageIds[value]) return legacyTimetablePageIds[value]
    if (legacyLeavePageIds[value]) return legacyLeavePageIds[value]
    if (legacyHostelPageIds[value]) return legacyHostelPageIds[value]
    if (legacyStudentPortalPageIds[value]) return legacyStudentPortalPageIds[value]
    const file = value.split('aspx')[0]
    if (legacyTransportPageIds[file]) return legacyTransportPageIds[file]
    if (legacyStudentPageIds[file]) return legacyStudentPageIds[file]
    if (legacyAttendancePageIds[file]) return legacyAttendancePageIds[file]
    if (legacyTimetablePageIds[file]) return legacyTimetablePageIds[file]
    if (legacyLeavePageIds[file]) return legacyLeavePageIds[file]
    if (legacyHostelPageIds[file]) return legacyHostelPageIds[file]
    if (legacyStudentPortalPageIds[file]) return legacyStudentPortalPageIds[file]
  }
  return page?.id
}
import StudentRegistration from './Student/Work/StudentRegistration'
import AdmNoAllotment from './Student/Work/AdmNoAllotment'
import TransferredStudentAdmNoAllotment from './Student/Work/TransferredStudentAdmNoAllotment'
import NewClassAllotment from './Student/Work/newClassAllotment'
import StudentICard from './Student/Work/StudentICard'
import AdmitCard from './Student/Work/AdmitCard'
import SearchStudent from './Student/Work/SearchStudent'
import RegistrationRedirection from './Student/Work/RegistrationRedirection'
import BulkAdmissionOnlineRegistration from './Student/Work/BulkAdmissionOnlineRegistration'
import UpdateStudentInfo from './Student/Update/UpdateStudentInfo'
import ChangeAdmNo from './Student/Update/ChangeAdmNo'
import UploadStudentImage from './Student/Update/UploadStudentImage'
import StudentWithdrawn from './Student/Update/StudentWithdrawn'
import UpdateStudentInfosheet from './Student/Update/UpdateStudentInfosheet'
import UpdateStudentInfoWithdrawn from './Student/Update/UpdateStudentInfoWithdrawn'
import PromoteDemote from './Student/Update/PromoteDemote'
import UpdateAlumniRecord from './Student/Update/UpdateAlumniRecord'
import DownloadStudentImages from './Student/Update/DownloadStudentImages'
import TransferStudentBranch from './Student/Update/TransferStudentBranch'
import StudentIDPassword from './Student/Reports/StudentIDPassword'
import StrengthReport from './Student/Reports/StrengthReport'
import ClasswiseStrengthReport from './Student/Reports/ClasswiseStrengthReport'
import AllStudentStrength from './Student/Reports/AllStudentStrength'
import HouseReport from './Student/Reports/HouseReport'
import StudentTypeReport from './Student/Reports/StudentTypeReport'
import BirthdayReport from './Student/Reports/BirthdayReport'
import SiblingReport from './Student/Reports/SiblingReport'
import StudentInfoSheet from './Student/Reports/StudentInfoSheet'
import StudentList from './Student/Reports/StudentList'
import OnlineRegistrationReportStatus from './Student/Reports/OnlineRegistrationReportStatus'
import OnlineRegistrationReport from './Student/Reports/OnlineRegistrationReport'
import UDISEPromotionReport from './Student/Reports/UDISEPromotionReport'
import AdmittedStudentReport from './Student/Reports/AdmittedStudentReport'
import EntranceExamAdmitCard from './Student/Reports/EntranceExamAdmitCard'
import RegistrationReport from './Student/Reports/RegistrationReport'
import StudentWithdrawnReport from './Student/Reports/StudentWithdrawnReport'
import DownloadRegistrationForm from './Student/Reports/DownloadRegistrationForm'
import NewStudentReport from './Student/Reports/NewStudentReport'
import StudentRegistrationDetails from './Student/Reports/StudentRegistrationDetails'
import StudentRecord from './Student/Reports/StudentRecord'
import AlumniRegistration from './Student/Setup/AlumniRegistration'
import HouseMaster from './Student/Setup/HouseMaster'
import ClassRepresentative from './Student/Setup/ClassRepresentative'
import RegistrationOpen from './Student/Setup/RegistrationOpen'
import ExamConfiguration from './Student/Setup/ExamConfiguration'
//Dashboard

import AdminDashboard from './Dashboard/AllDashboard/AdminDashboard'
import AdminDashboardAttendance from './Dashboard/AllDashboard/AdminDashboardAttendance'
import AdminDashboardFees from './Dashboard/AllDashboard/AdminDashboardFees'
import AdminDashBoardMarks from './Dashboard/AllDashboard/AdminDashBoardMarks'
import FacultyAttendanceDashboard  from './Dashboard/AllDashboard/FacultyAttendanceDashboard'
import LibraryDashBoard from './Dashboard/AllDashboard/LibraryDashBoard'
import ManagementDashboard from './Dashboard/AllDashboard/ManagementDashboard'


//staff work start from here
import FacultyRegistration from './Staff/Work/FacultyRegistration'
import FacultyReregistration from './Staff/Work/FacultyReregistration'

import FacultyInfosheet from './Staff/Update/FacultyInfosheet'

import ClassTeacherReport from './Staff/Report/ClassTeacherReport'
import FacultyInfoDownload from './Staff/Report/FacultyInfoDownload'
import JobApplicationReport from './Staff/Report/JobApplicationReport'
import FacultyIdPassword from './Staff/Report/FacultyIdPassword'

import DefineClassTeacher from './Staff/Setup/DefineClassTeacher'
import RegistrationOpenConfig from './Staff/Setup/RegistrationOpenConfig'




// Fees work start here
import DepositFee from './Fee/Work/DepositFee'
import FeeBooklet from './Fee/Work/FeeBooklet'
import { DepositLateFees, FeeSettlement, ImportExcelFee, LateFeeReportWork, MiscellaneousFee, RefundableFees, WaveOffFees, WaveOffLateFees } from './Fee/Work/FeeWorkDynamic'


import { AssignConGroup, AssignOptional, FeeNotice, ReconFee, ReprintFeeSlip, UpdateStudentFee } from './Fee/Update/FeeUpdateDynamic'


import FeeHeadMaster from './Fee/Setup/FeeHeadMaster'
import FeeHeadChargeMaster from './Fee/Setup/FeeHeadChargeMaster'
import DefineInstallment from './Fee/Setup/DefineInstallment'
import DefineLateFee from './Fee/Setup/DefineLateFee'
import DefineConcessionGroup from './Fee/Setup/DefineConcessionGroup'
import DefineConcession from './Fee/Setup/DefineConcession'
import DefineConcessionValue from './Fee/Setup/DefineConcessionValue'




import { ClasswiseFeeReport, ConcessionGroupReport, ConcessionReport, DailyCollectionReport, FeeReconciliationReport, FeeSubReconciliationReport, InstallmentWiseDefaulter, LateFeeReport, StudentFeeSummary } from './Fee/Report/FeeReportDynamic'
import { FeeDefaulterReport, PaymentResponseReport } from './Fee/Report/LegacyFeeReports'
import { BudgetReportExact, DailyDepositReportExact } from './Fee/Report/FeeExactReports'
import { HeadwiseDefaulterExact, InstallmentWiseDefaulterExact, LateFeeReportExact, StudentFeeSummaryExact } from './Fee/Report/LegacyFeeDetailReports'


//Transport work start from here

import AssignTransport from './Transport/Work/AssignTransport'
import DriverVehicleMaster from './Transport/Work/DriverVehicleMaster'
import VehicleMaster from './Transport/Work/VehicleMaster'


import AssignRoute from './Transport/Report/AssignRoute'
import MonthwiseTransportReport from './Transport/Report/MonthwiseTransportReport'
import StudentTransportReport from './Transport/Report/StudentTransportReport'
import TransportPrivateStudentsReport from './Transport/Report/TransportPrivateStudentsReport'
import TransportReportModule from './Transport/Report/TransportReportModule'

import DefineTransportType from './Transport/Setup/DefineTransportType'
import RouteMaster from './Transport/Setup/RouteMaster'
import RouteStoppageMapping from './Transport/Setup/RouteStoppageMapping'
import StoppageMaster from './Transport/Setup/StoppageMaster'

//Time Table start from here

import AltTimetable from './TimeTable/Work/AltTimetable'
import AssignTimeTable from './TimeTable/Work/AssignTimeTable'
import FacultySubjectMapping from './TimeTable/Work/FacultySubjectMapping'
import ClassActivity from './TimeTable/Work/ClassActivity'


import ActivityReport from './TimeTable/Report/ActivityReport'
import ArrangementReport from './TimeTable/Report/ArrangementReport'
import CompleteTimeTableReport from './TimeTable/Report/CompleteTimeTableReport'
import FacultyFreePeriod from './TimeTable/Report/FacultyFreePeriod'
import SchoolTimetableReport from './TimeTable/Report/SchoolTimetableReport'
import TimetableReport from './TimeTable/Report/TimetableReport'
import VirtualClassReport from './TimeTable/Report/VirtualClassReport'


import CreateVirtualClass from './TimeTable/Setup/CreateVirtualClass'
import DefineHolidays from './TimeTable/Setup/DefineHolidays'
import LectureTimings from './TimeTable/Setup/LectureTimings'
import TimetableConfiguration from './TimeTable/Setup/TimetableConfiguration'


//Attendance work start from here 
import StudentAttendance from './Attendance/Work/StudentAttendance'

import DeleteAttendance from './Attendance/Update/DeleteAttendance'
import DeleteStudentAttendance from './Attendance/Update/DeleteStudentAttendance'
import HolidayAttendance from './Attendance/Update/HolidayAttendance'


import AttendanceReportPercentage from './Attendance/Report/AttendanceReportPercentage'
import AttendanceStatusReport from './Attendance/Report/AttendanceStatusReport'
import TotalAttendanceReport from './Attendance/Report/TotalAttendanceReport'


import RFID_CampusStudentAttendance from './Attendance/TapCard/RFID_CampusStudentAttendance'
import RFID_TransportRouteAttendance from './Attendance/TapCard/RFID_TransportRouteAttendance'
import RFIDAttendance from './Attendance/TapCard/RFIDAttendanceDynamic'
import RFID_TransportStudentAttendance from './Attendance/TapCard/RFID_TransportStudentAttendance'
import RFIDCampusAttendanceCount from './Attendance/TapCard/RFIDCampusAttendanceCount'




//Certificate work start from here

import BonafiedCertificate from './Certificates/Work/BonafiedCertificate'
import CharacterCertificate from './Certificates/Work/CharacterCertificate'
import FeeCertificate from './Certificates/Work/FeeCertificate'
import IncomeCertificate from './Certificates/Work/IncomeCertificate'
import TransferCertificate from './Certificates/Work/TransferCertificate'

import BonafideCertificateReport from './Certificates/Report/BonafideCertificateReport'
import CharacterCertificateReport from './Certificates/Report/CharacterCertificateReport'
import TC_Report from './Certificates/Report/TC_Report'
import TCReport from './Certificates/Report/TCReport'

//Library work start from here 
import Issue_book from './Library/Work/Issue_book'
import ItemEntryMaster from './Library/Work/ItemEntryMaster'
import PublisherEntry from './Library/Work/PublisherEntry'
import SupplierEntry from './Library/Work/SupplierEntry'


import BookList from './Library/Report/BookList'
import BookReturnReport from './Library/Report/BookReturnReport'
import BooksIssueReturnDetail from './Library/Report/BooksIssueReturnDetail'
import DefaulterReport from './Library/Report/DefaulterReport'
import RenewDetails from './Library/Report/RenewDetails'

import SupplierCategory from './Library/Setup/SupplierCategory'
import PublisherCategory from './Library/Setup/PublisherCategory'
import ItemCategory from './Library/Setup/ItemCategory'
import ItemFrequencyMaster from './Library/Setup/ItemFrequencyMaster'
import ItemLanguage from './Library/Setup/ItemLanguage'
import ItemRulesMaster from './Library/Setup/ItemRulesMaster'
import ItemTypeMaster from './Library/Setup/ItemTypeMaster'
import LibraryModule from './Library/LibraryModule'


//website work start from here 
import UploadNotice from './WebSite/Work/UploadNotice'
import UploadNews from './WebSite/Work/UploadNews'
import UploadExamSchedule from './WebSite/Work/UploadExamSchedule'
import VideoGallery from './WebSite/Work/VideoGallery'
import UploadTC from './WebSite/Work/UploadTC'
import PhotoGallery from './WebSite/Work/PhotoGallery'
import LatestEvents from './WebSite/Work/LatestEvents'
import ShowUploadExamSchedule from './WebSite/Report/ShowUploadExamSchedule'

// Uploads module
import UploadAcademicPlanner from './Uploads/Work/AcademicPlanner'
import UploadAssignment from './Uploads/Work/Assignment'
import UploadDailyHomework from './Uploads/Work/UploadDailyHomework'
import UploadHolidayHomework from './Uploads/Work/UploadHolidayHomework'
import UploadSyllabus from './Uploads/Work/UploadSyllabus'
import AcademicPlannerReport from './Uploads/Report/AcademicPlannerReport'
import AssignmentReport from './Uploads/Report/AssignmentReport'
import DailyHomeworkReport from './Uploads/Report/DailyHomeworkReport'
import MobileAppCoverageReport from './Uploads/Report/MobileAppCoverageReport'
import ShowDailyHomework from './Uploads/Report/ShowDailyHomework'
import ShowSyllabus from './Uploads/Report/ShowSyllabus'
import SchoolCalendar from './Uploads/Setup/SchoolCalendar'
import DefineActivity from './Uploads/Setup/DefineActivity'


//ExamMaster start from here 

import DefineIndicatorGradePrimary from './ExamMaster/Work/DefineIndicatorGrade'
import DefineLearnerProfileIndicator from './ExamMaster/Work/DefineLearnerProfileIndicator'
import DefineMarksNew from './ExamMaster/Work/DefineMarks'
import DefineMarksPrePrimary from './ExamMaster/Work/DefineMarksPrePrimary'
import PreprimaryMarksMapping from './ExamMaster/Work/PreprimaryMarksMapping'
import DefineReportcardCategoryRemark from './ExamMaster/Work/DefineReportcardCategoryRemark'
import DefineStudentRemark from './ExamMaster/Work/DefineStudentRemark'
import GreenSheet from './ExamMaster/Work/GreenSheet'
import StuHealthStatus from './ExamMaster/Work/StuHealthStatus'
import UploadMarks from './ExamMaster/Work/UploadMarks'


import { Define_ClassSubject, DefineAttrib, DefineCategory, DefineHeader, DefineMaxMin, DefineMaxMinExam, DefinePrSubject, DefineSubSubject, DefineWeightage, Indicator, IndicatorCategory, LearnerProfileAttributeValue, LockUnlockStudent, SubjectMaster, StudentChartLock, LockExam } from './ExamMaster/Setup/ExamSetupExact'


import CumulativeSheet from './ExamMaster/Report/CumulativeSheet'
import GetClassSubjects from './ExamMaster/Report/GetClassSubjects'
import MarksLogReport from './ExamMaster/Report/MarksLogReport'
import MarksStatusGraph from './ExamMaster/Report/MarksStatusGraph'
import MarkStatusReport from './ExamMaster/Report/MarkStatusReport'
import OverallExaminationReport from './ExamMaster/Report/OverallExaminationReport'
import ReportCard from './ExamMaster/Report/ReportCard'
import ReportCardExamwise from './ExamMaster/Report/ReportCardExamwise'
import StudentAcademicPerformance from './ExamMaster/Report/StudentAcademicPerformance'
import SubjectReport from './ExamMaster/Report/SubjectReport'
import ToppersReport from './ExamMaster/Report/ToppersReport'
import CBSE_AISSE_Result from './ExamMaster/Report/CBSE_AISSE_Result'
import CBSE_AISSE_Result_MainSubject from './ExamMaster/Report/CBSE_AISSE_Result_MainSubject'
import CBSE_AnnualReport from './ExamMaster/Report/CBSE_AnnualReport'
import Class_overall_marks from './ExamMaster/Report/Class_overall_marks'
import ClasswiseStudentPercentage from './ExamMaster/Report/ClasswiseStudentPercentage'
import ExamComparativeSheet from './ExamMaster/Report/ExamComparativeSheet'
import ExamwiseGreensheet from './ExamMaster/Report/ExamwiseGreensheet'
import ExpectedBoardAverageReport from './ExamMaster/Report/ExpectedBoardAverageReport'
import ExpectedSchoolAverageReport from './ExamMaster/Report/ExpectedSchoolAverageReport'
import FailureStudentReport from './ExamMaster/Report/FailureStudentReport'
import GreenSheetNew from './ExamMaster/Report/GreenSheetNew'
import GreenSheetPrimary from './ExamMaster/Report/GreenSheetPrimary'
import GreenSheetSubjectWise from './ExamMaster/Report/GreenSheetSubjectWise'
import MarksListBlank from './ExamMaster/Report/MarksListBlank'
import MarksListJunior from './ExamMaster/Report/MarksListJunior'
import Report_ExamResultDiscussion from './ExamMaster/Report/Report_ExamResultDiscussion'
import ExamReportExact from './ExamMaster/Report/ExamReportExact'

import DeleteMarks from './ExamMaster/Update/DeleteMarks'


//May be incorrect 
// Canteen work and reports
import CanteenModulePage from './Canteen/CanteenModulePage'

//Communication work start from here
import SendSMS from './Communication/Work/SendSMS'
import SendSmsGroup from './Communication/Work/SendSmsGroup'

import EmailReport from './Communication/Report/EmailReport'
import SmsReport from './Communication/Report/SmsReport'

//Hostel work start from here
import HostelModulePage from './Hostel/HostelModulePage'

//MasterSetting work start from here
import ChangeSectionName from './MasterSetting/Work/Change_Section_Name'
import ClassSectionMaster from './MasterSetting/Work/ClassSectionMaster'
import DefineStaffRole from './MasterSetting/Work/DefineStaffRole'
import LogReportDateWise from './MasterSetting/Work/LogReportDateWise'
import ModuleMapping from './MasterSetting/Work/ModuleMapping'
import RoleList from './MasterSetting/Work/RoleList'
import RolesPermission from './MasterSetting/Work/Roles_Permission'
import SessionMaster from './MasterSetting/Work/SessionMaster'
import SessionTerm from './MasterSetting/Work/Session_term'
import SwitchSession from './MasterSetting/Work/SwitchSession'
import ViewProfile from './MasterSetting/Work/ViewProfile'

//Leave work start from here
import FacultyLeaveList from './Leave/Work/FacultyLeaveList'
import FacultyTotalLeave from './Leave/Work/FacultyTotalLeave'
import IndentForm from './Leave/Work/IndentForm'
import IndentFormApproval from './Leave/Work/IndentFormApproval'
import IndentFormApprovalPrincipal from './Leave/Work/IndentFormApprovalPrincipal'
import IndentFormList from './Leave/Work/IndentFormList'
import LeaveAllocation from './Leave/Work/LeaveAllocation'
import LeaveApplyForm from './Leave/Work/LeaveApplyForm'
import LeaveApproval from './Leave/Work/LeaveApproval'
import LeaveApprovalByPrincipal from './Leave/Work/LeaveApprovalByPrincipal'
import LeaveForm from './Leave/Work/LeaveForm'
import LeaveStaffApproval from './Leave/Work/LeaveStaffApproval'
import LeaveModulePage from './Leave/LeaveModulePage'

/**
 * Maps specific page IDs to dedicated components.
 * Everything else falls through to GenericPage.
 * Future: add more dedicated pages here as you build them.
 */
export default function PageRenderer({ moduleData, currentGroup, currentPage, onPageClick }) {
  if (!moduleData || !currentGroup || !currentPage) {
    return (
      <div className="page-animate rounded-xl border border-amber-200 bg-amber-50 p-6 text-amber-900 dark:border-amber-500/30 dark:bg-amber-500/10 dark:text-amber-200">
        <h2 className="font-display text-lg font-semibold">Page mapping unavailable</h2>
        <p className="mt-1 text-sm">The selected legacy menu page is not mapped to a new frontend page yet. Please refresh the menu and try again.</p>
      </div>
    )
  }

  const pageId      = resolvedPageId(currentPage)
  const pageLabel   = currentPage.label
  const groupLabel  = currentGroup.label
  const moduleLabel = moduleData.label

  // ── Dedicated pages ──────────────────────────────────────


  //Dashboard
  
  if (pageId === 'StudentPortalDashboard') return <StudentPortalExact mode="dashboard" />
  if (pageId === 'StudentPortalSyllabus') return <StudentPortalExact mode="syllabus" />
  if (pageId === 'StudentPortalCirculars') return <StudentPortalExact mode="circulars" />
  if (pageId === 'StudentPortalHolidayHomework') return <StudentPortalExact mode="holidayHomework" />
  if (pageId === 'StudentPortalDailyHomework') return <StudentPortalExact mode="dailyHomework" />
  if (pageId === 'StudentPortalAssignments') return <StudentPortalExact mode="assignments" />
  if (pageId === 'StudentPortalFees') return <StudentPortalExact mode="fees" />
  if (pageId === 'StudentPortalAttendance') return <StudentPortalExact mode="attendance" />
  if (pageId === 'StudentPortalTimetable') return <StudentPortalExact mode="timetable" />
  if (pageId === 'StudentPortalExamSchedule') return <StudentPortalExact mode="examSchedule" />
  if (pageId === 'StudentPortalAcademicPerformance') return <StudentPortalExact mode="academicPerformance" />


  if (pageId === 'AdminDashboard') return <AdminDashboard />
  if (pageId === 'AdminDashboardAttendance') return <AdminDashboardAttendance />
  if (pageId === 'AdminDashboardFees') return <AdminDashboardFees />
  if (pageId === 'AdminDashBoardMarks') return <AdminDashBoardMarks />
  if (pageId === 'FacultyAttendanceDashboard') return <FacultyAttendanceDashboard />
  if (pageId === 'LibraryDashBoard') return <LibraryDashBoard />
  if (pageId === 'ManagementDashboard') return <ManagementDashboard />


  if (pageId === 'studentRegistration') return <StudentRegistration />
  if (pageId === 'AdmNoAllotment') return <AdmNoAllotment />
  if (pageId === 'TransferredStudentAdmNoAllotment') return <TransferredStudentAdmNoAllotment />
  if (pageId === 'newClassAllotment') return <NewClassAllotment />
   if (pageId === 'StudentICard') return <StudentICard />
   if (pageId === 'AdmitCard') return <AdmitCard />
   if (pageId === 'SearchStudent') return <SearchStudent />
   if (pageId === 'UpdateStudentInfo') return <UpdateStudentInfo />
   if (pageId === 'ChangeAdmNo') return <ChangeAdmNo />
   if (pageId === 'UploadStudentImage') return <UploadStudentImage />
   if (pageId === 'StudentWithdrawn') return <StudentWithdrawn />
   if (pageId === 'UpdateStudentInfosheet') return <UpdateStudentInfosheet />
   if (pageId === 'UpdateStudentInfoWithdrawn') return <UpdateStudentInfoWithdrawn />
   if (pageId === 'PromoteDemote') return <PromoteDemote />
   if (pageId === 'UpdateAlumniRecord') return <UpdateAlumniRecord />
   if (pageId === 'DownloadStudentImages') return <DownloadStudentImages />
   if (pageId === 'TransferStudentBranch') return <TransferStudentBranch />
   if (pageId === 'StudentIDPassword') return <StudentIDPassword />
   if (pageId === 'StrengthReport') return <StrengthReport />
   if (pageId === 'ClasswiseStrengthReport') return <ClasswiseStrengthReport />
   if (pageId === 'AllStudentStrength') return <AllStudentStrength />
   if (pageId === 'HouseReport') return <HouseReport />
   if (pageId === 'StudentTypeReport') return <StudentTypeReport />
   if (pageId === 'BirthdayReport') return <BirthdayReport />
   if (pageId === 'SiblingReport') return <SiblingReport />
   if (pageId === 'StudentInfoSheet') return <StudentInfoSheet />
   if (pageId === 'RegistrationRedirection') return <RegistrationRedirection />
   if (pageId === 'BulkAdmissionOnlineRegistration') return <BulkAdmissionOnlineRegistration />
   if (pageId === 'StudentList') return <StudentList />
   if (pageId === 'OnlineRegistrationReportStatus') return <OnlineRegistrationReportStatus />
   if (pageId === 'OnlineRegistrationReport') return <OnlineRegistrationReport />
   if (pageId === 'UDISEPromotionReport') return <UDISEPromotionReport />
   if (pageId === 'AdmittedStudentReport') return <AdmittedStudentReport />
   if (pageId === 'EntranceExamAdmitCard') return <EntranceExamAdmitCard />
   if (pageId === 'RegistrationReport') return <RegistrationReport />
   if (pageId === 'StudentWithdrawnReport') return <StudentWithdrawnReport />
   if (pageId === 'DownloadRegistrationForm') return <DownloadRegistrationForm />
   if (pageId === 'NewStudentReport') return <NewStudentReport />
   if (pageId === 'StudentRegistrationDetails') return <StudentRegistrationDetails />
   if (pageId === 'StudentRecord') return <StudentRecord />
   if (pageId === 'AlumniRegistration') return <AlumniRegistration />
   if (pageId === 'HouseMaster') return <HouseMaster /> 
   if (pageId === 'ClassRepresentative') return <ClassRepresentative /> 
   if (pageId === 'RegistrationOpen') return <RegistrationOpen /> 
   if (pageId === 'ExamConfiguration') return <ExamConfiguration /> 


  //staff work start from here
    if (pageId === 'FacultyRegistration') return <FacultyRegistration />    
    if (pageId === 'FacultyReregistration') return <FacultyReregistration />  

    if (pageId === 'FacultyInfosheet') return <FacultyInfosheet />  

    if (pageId === 'FacultyInfoDownload') return <FacultyInfoDownload />
    if (pageId === 'FacultyIdPassword') return <FacultyIdPassword />  
    if (pageId === 'JobApplicationReport') return <JobApplicationReport />  
    if (pageId === 'ClassTeacherReport') return <ClassTeacherReport /> 
    
    if (pageId === 'DefineClassTeacher') return <DefineClassTeacher />
    if (pageId === 'RegistrationOpenConfig') return <RegistrationOpenConfig />

  //Fee work start here
    if (pageId === 'DepositFee') return <DepositFee onPageClick={onPageClick} />
    if (pageId === 'fee_booklet' || pageId === 'FeeBooklet') return <FeeBooklet />
    if (pageId === 'DepositLateFees' || pageId === 'DepositLateFeesFee') return <DepositLateFees />
    if (pageId === 'FeeSettlement') return <FeeSettlement />
    if (pageId === 'ImportExcelFee') return <ImportExcelFee />
    if (pageId === 'LateFeeReportWork') return <LateFeeReportWork />
    if (pageId === 'MiscellaneousFee') return <MiscellaneousFee />
    if (pageId === 'RefundableFees') return <RefundableFees />
    if (pageId === 'WaveOffFees') return <WaveOffFees />
    if (pageId === 'WaveOffLateFees') return <WaveOffLateFees />

    if (pageId === 'AssignConGroup') return <AssignConGroup />
    if (pageId === 'AssignOptional') return <AssignOptional />
    if (pageId === 'FeeNotice') return <FeeNotice />
    if (pageId === 'ReconFee') return <ReconFee />
    if (pageId === 'ReprintFeeSlip') return <ReprintFeeSlip />
    if (pageId === 'UpdateStudentFee') return <UpdateStudentFee />


      if (pageId === 'FeeHeadMaster') return <FeeHeadMaster />
      if (pageId === 'FeeHeadChargeMaster') return <FeeHeadChargeMaster />
      if (pageId === 'DefineInstallment') return <DefineInstallment />
      if (pageId === 'DefineLateFee') return <DefineLateFee />
      if (pageId === 'DefineConcession') return <DefineConcession />
      if (pageId === 'DefineConcessionGroup') return <DefineConcessionGroup />
      if (pageId === 'DefineConcessionValue') return <DefineConcessionValue />





      if (pageId === 'BudgetReport') return <BudgetReportExact />
      if (pageId === 'ClasswiseFeeReport') return <ClasswiseFeeReport />
      if (pageId === 'ConcessionGroupReport') return <ConcessionGroupReport />
      if (pageId === 'ConcessionReport') return <ConcessionReport />
      if (pageId === 'DailyCollectionReport') return <DailyCollectionReport />
      if (pageId === 'DailyDepositReport') return <DailyDepositReportExact />
      if (pageId === 'FeeDefaulterReport' || (moduleLabel === 'Fee' && (pageId === 'DefaulterReport' || pageId === 'defaulter-report_renssance'))) return <FeeDefaulterReport onPageClick={onPageClick} />
      if (pageId === 'FeeReconciliationReport') return <FeeReconciliationReport />
      if (pageId === 'FeeSubReconciliationReport') return <FeeSubReconciliationReport />
      if (pageId === 'InstallmentWiseDefaulter' || pageId === 'Installment_wise_defaulter') return <InstallmentWiseDefaulterExact />
      if (pageId === 'HeadwiseDefaulter' || pageId === 'headwis_defaulter_report(instwise)' || pageId === 'HeadwiseDefaulterReport') return <HeadwiseDefaulterExact />
      if (pageId === 'LateFeeReport' || pageId === 'late_fee_report') return <LateFeeReportExact />
      if (pageId === 'PaymentResponseReport') return <PaymentResponseReport />
      if (pageId === 'StudentFeeSummary' || pageId === 'StudentFeeSummaryReport') return <StudentFeeSummaryExact />
     

//Transport work from here

if (pageId === 'AssignTransport') return <AssignTransport />
if (['DriverVehicleMaster', 'DriverDetail', 'DriverVehicleDetails', 'driver_vehicle_details'].includes(pageId)) return <DriverVehicleMaster />
if (['VehicleMaster', 'vehicleMaster', 'vehicle_master'].includes(pageId)) return <VehicleMaster />

if (pageId === 'AssignRoute') return <AssignRoute />
if (pageId === 'MonthwiseTransportReport') return <MonthwiseTransportReport />
if (pageId === 'StudentTransportReport') return <StudentTransportReport />
if (pageId === 'TransportPrivateStudentsReport') return <TransportPrivateStudentsReport />
if (pageId === 'RouteWiseCollectionReport' || pageId === 'feecollection_routewise') return <TransportReportModule mode="routeCollection" />
if (pageId === 'RouteWiseTotalCollectionReport') return <TransportReportModule mode="totalCollection" />


if (pageId === 'DefineTransportType') return <DefineTransportType />
if (pageId === 'RouteMaster') return <RouteMaster />
if (pageId === 'RouteStoppageMapping') return <RouteStoppageMapping />
if (pageId === 'StoppageMaster') return <StoppageMaster />


//Time Table work start from here
if (pageId === 'AltTimetable') return <AltTimetable />
if (pageId === 'AssignTimeTable') return <AssignTimeTable />
if (pageId === 'FacultySubjectMapping') return <FacultySubjectMapping />
if (pageId === 'ClassActivity') return <ClassActivity />

if (pageId === 'ActivityReport') return <ActivityReport />
if (pageId === 'ArrangementReport') return <ArrangementReport />
if (pageId === 'CompleteTimeTableReport') return <CompleteTimeTableReport />
if (pageId === 'FacultyFreePeriod') return <FacultyFreePeriod />
if (pageId === 'SchoolTimetableReport') return <SchoolTimetableReport />
if (pageId === 'TimetableReport') return <TimetableReport />
if (pageId === 'VirtualClassReport') return <VirtualClassReport />

if (pageId === 'CreateVirtualClass') return <CreateVirtualClass />
if (pageId === 'DefineHolidays') return <DefineHolidays />
if (pageId === 'LectureTimings') return <LectureTimings />
if (pageId === 'TimetableConfiguration') return <TimetableConfiguration />


//Attendance work start from here
if (pageId === 'StudentAttendance') return <StudentAttendance />

if (pageId === 'DeleteAttendance') return <DeleteAttendance />
if (pageId === 'DeleteStudentAttendance') return <DeleteStudentAttendance />
if (pageId === 'HolidayAttendance') return <HolidayAttendance />

if (pageId === 'AttendanceReportPercentage') return <AttendanceReportPercentage />
if (pageId === 'AttendanceStatusReport') return <AttendanceStatusReport />
if (pageId === 'TotalAttendanceReport') return <TotalAttendanceReport />

if (pageId === 'RFID_CampusStudentAttendance') return <RFID_CampusStudentAttendance />
if (pageId === 'RFID_TransportRouteAttendance') return <RFID_TransportRouteAttendance />
if (pageId === 'RFIDAttendance') return <RFIDAttendance />
if (pageId === 'RFID_TransportStudentAttendance') return <RFID_TransportStudentAttendance />
if (pageId === 'RFIDCampusAttendanceCount') return <RFIDCampusAttendanceCount />

//certificate work start from here
if (pageId === 'BonafiedCertificate') return <BonafiedCertificate />
if (pageId === 'CharacterCertificate') return <CharacterCertificate />
if (pageId === 'FeeCertificate') return <FeeCertificate />
if (pageId === 'IncomeCertificate') return <IncomeCertificate />
if (pageId === 'TransferCertificate') return <TransferCertificate />

if (pageId === 'BonafideCertificateReport') return <BonafideCertificateReport />
if (pageId === 'CharacterCertificateReport') return <CharacterCertificateReport />
if (pageId === 'TC_Report') return <TC_Report />
if (pageId === 'TCReport') return <TCReport />

//Library work start from here 
if (['Issue_book','ItemEntryMaster','PublisherEntry','SupplierEntry','BookList','BookReturnReport','BooksIssueReturnDetail','DefaulterReport','RenewDetails','ReturnDateReport','IssueReturnDetails','LibraryCategoryReport','SlowMovingStock','StockInHand','SupplierCategory','PublisherCategory','ItemCategory','ItemFrequencyMaster','ItemLanguage','ItemRulesMaster','ItemTypeMaster','MemberCategory'].includes(pageId)) return <LibraryModule pageId={pageId} />
if (pageId === 'Issue_book') return <Issue_book />
if (pageId === 'ItemEntryMaster') return <ItemEntryMaster />
if (pageId === 'PublisherEntry') return <PublisherEntry />
if (pageId === 'SupplierEntry') return <SupplierEntry />


if (pageId === 'BookList') return <BookList />
if (pageId === 'BookReturnReport') return <BookReturnReport />
if (pageId === 'BooksIssueReturnDetail') return <BooksIssueReturnDetail />
if (pageId === 'DefaulterReport') return <DefaulterReport />
if (pageId === 'RenewDetails') return <RenewDetails />

if (pageId === 'SupplierCategory') return <SupplierCategory />
if (pageId === 'PublisherCategory') return <PublisherCategory />
if (pageId === 'ItemCategory') return <ItemCategory />
if (pageId === 'ItemFrequencyMaster') return <ItemFrequencyMaster />
if (pageId === 'ItemLanguage') return <ItemLanguage />
if (pageId === 'ItemRulesMaster') return <ItemRulesMaster />
if (pageId === 'ItemTypeMaster') return <ItemTypeMaster />


//website work from here
if (pageId === 'UploadNotice') return <UploadNotice />
if (pageId === 'UploadNews') return <UploadNews />
if (pageId === 'UploadExamSchedule') return <UploadExamSchedule />
if (pageId === 'VideoGallery') return <VideoGallery />
if (pageId === 'UploadTC') return <UploadTC />
if (pageId === 'PhotoGallery') return <PhotoGallery />
if (pageId === 'LatestEvents') return <LatestEvents />

if (pageId === 'ShowUploadExamSchedule') return <ShowUploadExamSchedule />

// Uploads module
if (pageId === 'UploadAcademicPlanner') return <UploadAcademicPlanner />
if (pageId === 'UploadAssignment') return <UploadAssignment />
if (pageId === 'UploadDailyHomework') return <UploadDailyHomework />
if (pageId === 'UploadHolidayHomework') return <UploadHolidayHomework />
if (pageId === 'UploadSyllabus') return <UploadSyllabus />
if (pageId === 'AcademicPlannerReport') return <AcademicPlannerReport />
if (pageId === 'AssignmentReport') return <AssignmentReport />
if (pageId === 'DailyHomeworkReport') return <DailyHomeworkReport />
if (pageId === 'MobileAppCoverageReport') return <MobileAppCoverageReport />
if (pageId === 'ShowDailyHomework') return <ShowDailyHomework />
if (pageId === 'ShowSyllabus') return <ShowSyllabus />
if (pageId === 'SchoolCalendar') return <SchoolCalendar />
if (pageId === 'DefineActivity') return <DefineActivity />

//ExamMaster work start from here 

if (pageId === 'DefineIndicatorGradePrimary') return <DefineIndicatorGradePrimary />
if (pageId === 'DefineLearnerProfileIndicator') return <DefineLearnerProfileIndicator />
if (pageId === 'DefineMarksNew') return <DefineMarksNew />
if (pageId === 'DefineMarksPrePrimary') return <DefineMarksPrePrimary />
if (pageId === 'PreprimaryMarksMapping') return <PreprimaryMarksMapping />
if (pageId === 'DefineReportcardCategoryRemark') return <DefineReportcardCategoryRemark />
if (pageId === 'DefineStudentRemark') return <DefineStudentRemark />
if (pageId === 'GreenSheet') return <GreenSheet />
if (pageId === 'StuHealthStatus') return <StuHealthStatus />
if (pageId === 'UploadMarks') return <UploadMarks />

if (pageId === 'Define_ClassSubject') return <Define_ClassSubject />
if (pageId === 'DefineAttrib') return <DefineAttrib />
if (pageId === 'DefineCategory') return <DefineCategory />
if (pageId === 'DefineHeader') return <DefineHeader />
if (pageId === 'DefineMaxMin') return <DefineMaxMin />
if (pageId === 'DefineMaxMinExam') return <DefineMaxMinExam />
if (pageId === 'DefinePrSubject') return <DefinePrSubject />
if (pageId === 'DefineSubSubject') return <DefineSubSubject />
if (pageId === 'DefineWeightage') return <DefineWeightage />
if (pageId === 'Indicator') return <Indicator />
if (pageId === 'IndicatorCategory') return <IndicatorCategory />
if (pageId === 'LearnerProfileAttributeValue') return <LearnerProfileAttributeValue />
if (pageId === 'LockUnlockStudent') return <LockUnlockStudent />
if (pageId === 'SubjectMaster') return <SubjectMaster />
if (pageId === 'StudentChartLock') return <StudentChartLock />
if (pageId === 'LockExam') return <LockExam />


if (['CumulativeSheet','GetClassSubjects','MarksLogReport','MarksStatusGraph','MarkStatusReport','OverallExaminationReport','ReportCard','ReportCardExamwise','StudentAcademicPerformance','SubjectReport','ToppersReport','CBSE_AISSE_Result','CBSE_AISSE_Result_MainSubject','CBSE_AnnualReport','Class_overall_marks','ClasswiseStudentPercentage','ExamComparativeSheet','ExamwiseGreensheet','ExpectedBoardAverageReport','ExpectedSchoolAverageReport','FailureStudentReport','GreenSheetNew','GreenSheetPrimary','GreenSheetSubjectWise','MarksListBlank','MarksListJunior','Report_ExamResultDiscussion'].includes(pageId)) return <ExamReportExact pageId={pageId} />
if (pageId === 'GetClassSubjects') return <GetClassSubjects />
if (pageId === 'MarksLogReport') return <MarksLogReport />
if (pageId === 'MarksStatusGraph') return <MarksStatusGraph />
if (pageId === 'MarkStatusReport') return <MarkStatusReport />
if (pageId === 'OverallExaminationReport') return <OverallExaminationReport />
if (pageId === 'ReportCard') return <ReportCard />
if (pageId === 'ReportCardExamwise') return <ReportCardExamwise />
if (pageId === 'StudentAcademicPerformance') return <StudentAcademicPerformance />
if (pageId === 'SubjectReport') return <SubjectReport />
if (pageId === 'ToppersReport') return <ToppersReport />
if (pageId === 'CBSE_AISSE_Result') return <CBSE_AISSE_Result />
if (pageId === 'CBSE_AISSE_Result_MainSubject') return <CBSE_AISSE_Result_MainSubject />
if (pageId === 'CBSE_AnnualReport') return <CBSE_AnnualReport />
if (pageId === 'Class_overall_marks') return <Class_overall_marks />
if (pageId === 'ClasswiseStudentPercentage') return <ClasswiseStudentPercentage />
if (pageId === 'ExamComparativeSheet') return <ExamComparativeSheet />
if (pageId === 'ExamwiseGreensheet') return <ExamwiseGreensheet />
if (pageId === 'ExpectedBoardAverageReport') return <ExpectedBoardAverageReport />
if (pageId === 'ExpectedSchoolAverageReport') return <ExpectedSchoolAverageReport />
if (pageId === 'FailureStudentReport') return <FailureStudentReport /> 
if (pageId === 'GreenSheetNew') return <GreenSheetNew />
if (pageId === 'GreenSheetPrimary') return <GreenSheetPrimary />
if (pageId === 'GreenSheetSubjectWise') return <GreenSheetSubjectWise />
if (pageId === 'MarksListBlank') return <MarksListBlank />
if (pageId === 'MarksListJunior') return <MarksListJunior />
if (pageId === 'Report_ExamResultDiscussion') return <Report_ExamResultDiscussion />

if (pageId === 'DeleteMarks') return <DeleteMarks />

//May be inorrect 
// Canteen (legacy Tuckshop) pages.  All of them use the same dynamic
// repository-backed implementation; the old ASPX page id only selects the
// workflow variant.
if (['AssignExpense', 'assign_expense'].includes(pageId)) return <CanteenModulePage mode="assign" />
if (['ExpenseGroupMaster', 'Expense_group'].includes(pageId)) return <CanteenModulePage mode="groups" />
if (['ExpenseNameMaster', 'Expense_name'].includes(pageId)) return <CanteenModulePage mode="names" />
if (['ProductMaster', 'product_master'].includes(pageId)) return <CanteenModulePage mode="products" />
if (['ProductGroup', 'Product_Group'].includes(pageId)) return <CanteenModulePage mode="productGroups" />
if (['UpdateCreditBalance', 'update_credit_balance'].includes(pageId)) return <CanteenModulePage mode="credits" />

if (['DebitCreditBalanceReport', 'debitcredit_balancereport'].includes(pageId)) return <CanteenModulePage mode="debitCredit" />
if (['PrintInvoice', 'print_invoice'].includes(pageId)) return <CanteenModulePage mode="invoice" />
if (['ProductBalanceReport', 'Product_balance_report'].includes(pageId)) return <CanteenModulePage mode="productBalance" />
if (['RemainingProductReport', 'remainingproductreport'].includes(pageId)) return <CanteenModulePage mode="remaining" />
if (['TuckshopAssignExpenseReport', 'tuckshop_assign_expense_reports'].includes(pageId)) return <CanteenModulePage mode="assignReport" />
if (['TuckshopProductIssueDetail', 'tuckshop_product_issue_detail'].includes(pageId)) return <CanteenModulePage mode="issues" />
if (['TuckshopReport', 'tuckshop_report'].includes(pageId)) return <CanteenModulePage mode="productReport" />

//Communication render lines
if (pageId === 'SendSMS') return <SendSMS />
if (pageId === 'SendSmsGroup') return <SendSmsGroup />

if (pageId === 'EmailReport') return <EmailReport />
if (pageId === 'SmsReport') return <SmsReport />

//Hostel render lines
if (pageId === 'AssignHostel') return <HostelModulePage mode="assign" />
if (pageId === 'HostelFees') return <HostelModulePage mode="fees" />
if (pageId === 'HostelAssignedReport') return <HostelModulePage mode="report" />

//MasterSetting render lines
if (pageId === 'ChangeSectionName') return <ChangeSectionName />
if (pageId === 'ClassSectionMaster') return <ClassSectionMaster />
if (pageId === 'DefineStaffRole') return <DefineStaffRole />
if (pageId === 'LogReportDateWise') return <LogReportDateWise />
if (pageId === 'ModuleMapping') return <ModuleMapping />
if (pageId === 'RoleList') return <RoleList />
if (pageId === 'RolesPermission') return <RolesPermission />
if (pageId === 'SessionMaster') return <SessionMaster />
if (pageId === 'SessionTerm') return <SessionTerm />
if (pageId === 'SwitchSession') return <SwitchSession />
if (pageId === 'ViewProfile') return <ViewProfile />

//Leave render lines
if (pageId === 'LeaveType') return <LeaveModulePage mode="type" />
if (pageId === 'FacultyTotalLeave') return <LeaveModulePage mode="allocation" />
if (pageId === 'LeaveForm') return <LeaveModulePage mode="apply" />
if (pageId === 'LeaveApproval') return <LeaveModulePage mode="headApproval" />
if (pageId === 'LeaveApprovalByPrincipal') return <LeaveModulePage mode="principalApproval" />
if (pageId === 'FacultyLeaveList') return <FacultyLeaveList />
if (pageId === 'FacultyTotalLeave') return <FacultyTotalLeave />
if (pageId === 'IndentForm') return <IndentForm />
if (pageId === 'IndentFormApproval') return <IndentFormApproval />
if (pageId === 'IndentFormApprovalPrincipal') return <IndentFormApprovalPrincipal />
if (pageId === 'IndentFormList') return <IndentFormList />
if (pageId === 'LeaveAllocation') return <LeaveAllocation />
if (pageId === 'LeaveApplyForm') return <LeaveApplyForm />
if (pageId === 'LeaveApproval') return <LeaveApproval />
if (pageId === 'LeaveApprovalByPrincipal') return <LeaveApprovalByPrincipal />
if (pageId === 'LeaveForm') return <LeaveForm />
if (pageId === 'LeaveStaffApproval') return <LeaveStaffApproval />

  return (
    <GenericPage
      key={pageId}             // re-mount on page change
      moduleLabel={moduleLabel}
      groupLabel={groupLabel}
      pageLabel={pageLabel}
    />
  )
}
