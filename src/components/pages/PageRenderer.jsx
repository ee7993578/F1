import StudentRegistration from './Student/Work/StudentRegistration'
import AdmNoAllotment from './Student/Work/AdmNoAllotment'
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
import PromoteDemote from './Student/Update/PromoteDemote'
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
import DepositLateFees from './Fee/Work/DepositLateFees'
import FeeSettlement from './Fee/Work/FeeSettlement'
import ImportExcelFee from './Fee/Work/ImportExcelFee'
import LateFeeReportWork from './Fee/Work/LateFeeReportWork'
import MiscellaneousFee from './Fee/Work/MiscellaneousFee'
import RefundableFees from './Fee/Work/RefundableFees'
import WaveOffFees from './Fee/Work/WaveOffFees'
import WaveOffLateFees from './Fee/Work/WaveOffLateFees'


import AssignConGroup from './Fee/Update/AssignConGroup'
import AssignOptional from './Fee/Update/AssignOptional'
import FeeNotice from './Fee/Update/FeeNotice'
import ReconFee from './Fee/Update/ReconFee'
import ReprintFeeSlip from './Fee/Update/ReprintFeeSlip'
import UpdateStudentFee from './Fee/Update/UpdateStudentFee'


import FeeHeadMaster from './Fee/Setup/FeeHeadMaster'
import FeeHeadChargeMaster from './Fee/Setup/FeeHeadChargeMaster'
import DefineInstallment from './Fee/Setup/DefineInstallment'
import DefineLateFee from './Fee/Setup/DefineLateFee'
import DefineConcessionGroup from './Fee/Setup/DefineConcessionGroup'
import DefineConcession from './Fee/Setup/DefineConcession'
import DefineConcessionValue from './Fee/Setup/DefineConcessionValue'




import BudgetReport from './Fee/Report/BudgetReport'
import ClasswiseFeeReport from './Fee/Report/ClasswiseFeeReport'
import ConcessionGroupReport from './Fee/Report/ConcessionGroupReport'
import ConcessionReport from './Fee/Report/ConcessionReport'
import DailyCollectionReport from './Fee/Report/DailyCollectionReport'
import DailyDepositReport from './Fee/Report/DailyDepositReport'
import FeeDefaulterReport from './Fee/Report/FeeDefaulterReport'
import FeeReconciliationReport from './Fee/Report/FeeReconciliationReport'
import FeeSubReconciliationReport from './Fee/Report/FeeSubReconciliationReport'
import InstallmentWiseDefaulter from './Fee/Report/InstallmentWiseDefaulter'
import LateFeeReport from './Fee/Report/LateFeeReport'
import PaymentResponseReport from './Fee/Report/PaymentResponseReport'
import StudentFeeSummary from './Fee/Report/StudentFeeSummary'


//Transport work start from here

import AssignTransport from './Transport/Work/AssignTransport'
import DriverVehicleMaster from './Transport/Work/DriverVehicleMaster'
import VehicleMaster from './Transport/Work/VehicleMaster'


import AssignRoute from './Transport/Report/AssignRoute'
import MonthwiseTransportReport from './Transport/Report/MonthwiseTransportReport'
import StudentTransportReport from './Transport/Report/StudentTransportReport'
import TransportPrivateStudentsReport from './Transport/Report/TransportPrivateStudentsReport'

import DefineTransportType from './Transport/Setup/DefineTransportType'
import RouteMaster from './Transport/Setup/RouteMaster'
import RouteStoppageMapping from './Transport/Setup/RouteStoppageMapping'
import StoppageMaster from './Transport/Setup/StoppageMaster'

//Time Table start from here

import AltTimetable from './TimeTable/Work/AltTimetable'
import AssignTimeTable from './TimeTable/Work/AssignTimeTable'
import FacultySubjectMapping from './TimeTable/Work/FacultySubjectMapping'


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
import HolidayAttendance from './Attendance/Update/HolidayAttendance'


import AttendanceReportPercentage from './Attendance/Report/AttendanceReportPercentage'
import AttendanceStatusReport from './Attendance/Report/AttendanceStatusReport'
import TotalAttendanceReport from './Attendance/Report/TotalAttendanceReport'


import RFID_CampusStudentAttendance from './Attendance/TapCard/RFID_CampusStudentAttendance'
import RFID_TransportRouteAttendance from './Attendance/TapCard/RFID_TransportRouteAttendance'
import RFIDAttendance from './Attendance/TapCard/RFIDAttendance'
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


//website work start from here 
import UploadNotice from './WebSite/Work/UploadNotice'
import UploadNews from './WebSite/Work/UploadNews'
import UploadExamSchedule from './WebSite/Work/UploadExamSchedule'
import VideoGallery from './WebSite/Work/VideoGallery'
import UploadTC from './WebSite/Work/UploadTC'
import PhotoGallery from './WebSite/Work/PhotoGallery'
import LatestEvents from './WebSite/Work/LatestEvents'
import ShowUploadExamSchedule from './WebSite/Report/ShowUploadExamSchedule'


//ExamMaster start from here 

import DefineIndicatorGradePrimary from './ExamMaster/Work/DefineIndicatorGrade'
import DefineLearnerProfileIndicator from './ExamMaster/Work/DefineLearnerProfileIndicator'
import DefineMarksNew from './ExamMaster/Work/DefineMarks'
import DefineMarksPrePrimary from './ExamMaster/Work/DefineMarksPrePrimary'
import DefineReportcardCategoryRemark from './ExamMaster/Work/DefineReportcardCategoryRemark'
import DefineStudentRemark from './ExamMaster/Work/DefineStudentRemark'
import GreenSheet from './ExamMaster/Work/GreenSheet'
import StuHealthStatus from './ExamMaster/Work/StuHealthStatus'
import UploadMarks from './ExamMaster/Work/UploadMarks'


import Define_ClassSubject from './ExamMaster/Setup/Define_ClassSubject'
import DefineAttrib from './ExamMaster/Setup/DefineAttrib'
import DefineCategory from './ExamMaster/Setup/DefineCategory'
import DefineHeader from './ExamMaster/Setup/DefineHeader'
import DefineMaxMin from './ExamMaster/Setup/DefineMaxMin'
import DefineMaxMinExam from './ExamMaster/Setup/DefineMaxMinExam'
import DefinePrSubject from './ExamMaster/Setup/DefinePrSubject'
import DefineSubSubject from './ExamMaster/Setup/DefineSubSubject'
import DefineWeightage from './ExamMaster/Setup/DefineWeightage'
import Indicator from './ExamMaster/Setup/Indicator'
import IndicatorCategory from './ExamMaster/Setup/IndicatorCategory'
import LearnerProfileAttributeValue from './ExamMaster/Setup/LearnerProfileAttributeValue'
import LockUnlockStudent from './ExamMaster/Setup/LockUnlockStudent'
import SubjectMaster from './ExamMaster/Setup/SubjectMaster'


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

import DeleteMarks from './ExamMaster/Update/DeleteMarks'


//May be incorrect 
//Canteen work start from here
import AssignExpense from './Canteen/Work/AssignExpense'
import ExpenseGroupMaster from './Canteen/Work/ExpenseGroupMaster'
import ExpenseNameMaster from './Canteen/Work/ExpenseNameMaster'
import ProductMaster from './Canteen/Work/ProductMaster'
import ProductGroup from './Canteen/Work/Product_Group'
import UpdateCreditBalance from './Canteen/Work/UpdateCreditBalance'

import DebitCreditBalanceReport from './Canteen/Report/DebitCreditBalanceReport'
import PrintInvoice from './Canteen/Report/PrintInvoice'
import ProductBalanceReport from './Canteen/Report/ProductBalanceReport'
import RemainingProductReport from './Canteen/Report/RemainingProductReport'
import TuckshopAssignExpenseReport from './Canteen/Report/TuckshopAssignExpenseReport'
import TuckshopProductIssueDetail from './Canteen/Report/TuckshopProductIssueDetail'
import TuckshopReport from './Canteen/Report/TuckshopReport'

//Communication work start from here
import SendSMS from './Communication/Work/SendSMS'
import SendSmsGroup from './Communication/Work/SendSmsGroup'

import EmailReport from './Communication/Report/EmailReport'
import SmsReport from './Communication/Report/SmsReport'

//Hostel work start from here
import AssignHostel from './Hostel/Work/AssignHostel'
import HostelFees from './Hostel/Work/HostelFees'

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

/**
 * Maps specific page IDs to dedicated components.
 * Everything else falls through to GenericPage.
 * Future: add more dedicated pages here as you build them.
 */
export default function PageRenderer({ moduleData, currentGroup, currentPage }) {
  if (!moduleData || !currentGroup || !currentPage) {
    return (
      <div className="page-animate rounded-xl border border-amber-200 bg-amber-50 p-6 text-amber-900 dark:border-amber-500/30 dark:bg-amber-500/10 dark:text-amber-200">
        <h2 className="font-display text-lg font-semibold">Page mapping unavailable</h2>
        <p className="mt-1 text-sm">The selected legacy menu page is not mapped to a new frontend page yet. Please refresh the menu and try again.</p>
      </div>
    )
  }

  const pageId      = currentPage.id
  const pageLabel   = currentPage.label
  const groupLabel  = currentGroup.label
  const moduleLabel = moduleData.label

  // ── Dedicated pages ──────────────────────────────────────


  //Dashboard
  

  if (pageId === 'AdminDashboard') return <AdminDashboard />
  if (pageId === 'AdminDashboardAttendance') return <AdminDashboardAttendance />
  if (pageId === 'AdminDashboardFees') return <AdminDashboardFees />
  if (pageId === 'AdminDashBoardMarks') return <AdminDashBoardMarks />
  if (pageId === 'FacultyAttendanceDashboard') return <FacultyAttendanceDashboard />
  if (pageId === 'LibraryDashBoard') return <LibraryDashBoard />
  if (pageId === 'ManagementDashboard') return <ManagementDashboard />


  if (pageId === 'studentRegistration') return <StudentRegistration />
  if (pageId === 'AdmNoAllotment') return <AdmNoAllotment />
  if (pageId === 'newClassAllotment') return <NewClassAllotment />
   if (pageId === 'StudentICard') return <StudentICard />
   if (pageId === 'AdmitCard') return <AdmitCard />
   if (pageId === 'SearchStudent') return <SearchStudent />
   if (pageId === 'UpdateStudentInfo') return <UpdateStudentInfo />
   if (pageId === 'ChangeAdmNo') return <ChangeAdmNo />
   if (pageId === 'UploadStudentImage') return <UploadStudentImage />
   if (pageId === 'StudentWithdrawn') return <StudentWithdrawn />
   if (pageId === 'UpdateStudentInfosheet') return <UpdateStudentInfosheet />
   if (pageId === 'PromoteDemote') return <PromoteDemote />
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
    if (pageId === 'DepositFee') return <DepositFee />
    if (pageId === 'DepositLateFees') return <DepositLateFees />
    if (pageId === 'FeeSettlement') return <FeeSettlement />
    if (pageId === 'ImportExcelFee') return <ImportExcelFee />
    if (pageId === 'LateFeeReport') return <LateFeeReport />
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





      if (pageId === 'BudgetReport') return <BudgetReport />
      if (pageId === 'ClasswiseFeeReport') return <ClasswiseFeeReport />
      if (pageId === 'ConcessionGroupReport') return <ConcessionGroupReport />
      if (pageId === 'ConcessionReport') return <ConcessionReport />
      if (pageId === 'DailyCollectionReport') return <DailyCollectionReport />
      if (pageId === 'DailyDepositReport') return <DailyDepositReport />
      if (pageId === 'FeeDefaulterReport') return <FeeDefaulterReport />
      if (pageId === 'FeeReconciliationReport') return <FeeReconciliationReport />
      if (pageId === 'FeeSubReconciliationReport') return <FeeSubReconciliationReport />
      if (pageId === 'InstallmentWiseDefaulter') return <InstallmentWiseDefaulter />
      if (pageId === 'LateFeeReport') return <LateFeeReport />
      if (pageId === 'PaymentResponseReport') return <PaymentResponseReport />
      if (pageId === 'StudentFeeSummary') return <StudentFeeSummary />
     

//Transport work from here

if (pageId === 'AssignTransport') return <AssignTransport />
if (pageId === 'DriverVehicleMaster') return <DriverVehicleMaster />
if (pageId === 'VehicleMaster') return <VehicleMaster />

if (pageId === 'AssignRoute') return <AssignRoute />
if (pageId === 'MonthwiseTransportReport') return <MonthwiseTransportReport />
if (pageId === 'StudentTransportReport') return <StudentTransportReport />
if (pageId === 'TransportPrivateStudentsReport') return <TransportPrivateStudentsReport />


if (pageId === 'DefineTransportType') return <DefineTransportType />
if (pageId === 'RouteMaster') return <RouteMaster />
if (pageId === 'RouteStoppageMapping') return <RouteStoppageMapping />
if (pageId === 'StoppageMaster') return <StoppageMaster />


//Time Table work start from here
if (pageId === 'AltTimetable') return <AltTimetable />
if (pageId === 'AssignTimeTable') return <AssignTimeTable />
if (pageId === 'FacultySubjectMapping') return <FacultySubjectMapping />

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

//ExamMaster work start from here 

if (pageId === 'DefineIndicatorGradePrimary') return <DefineIndicatorGradePrimary />
if (pageId === 'DefineLearnerProfileIndicator') return <DefineLearnerProfileIndicator />
if (pageId === 'DefineMarksNew') return <DefineMarksNew />
if (pageId === 'DefineMarksPrePrimary') return <DefineMarksPrePrimary />
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


if (pageId === 'CumulativeSheet') return <CumulativeSheet />
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
//Canteen render lines
if (pageId === 'AssignExpense') return <AssignExpense />
if (pageId === 'ExpenseGroupMaster') return <ExpenseGroupMaster />
if (pageId === 'ExpenseNameMaster') return <ExpenseNameMaster />
if (pageId === 'ProductMaster') return <ProductMaster />
if (pageId === 'ProductGroup') return <ProductGroup />
if (pageId === 'UpdateCreditBalance') return <UpdateCreditBalance />

if (pageId === 'DebitCreditBalanceReport') return <DebitCreditBalanceReport />
if (pageId === 'PrintInvoice') return <PrintInvoice />
if (pageId === 'ProductBalanceReport') return <ProductBalanceReport />
if (pageId === 'RemainingProductReport') return <RemainingProductReport />
if (pageId === 'TuckshopAssignExpenseReport') return <TuckshopAssignExpenseReport />
if (pageId === 'TuckshopProductIssueDetail') return <TuckshopProductIssueDetail />
if (pageId === 'TuckshopReport') return <TuckshopReport />

//Communication render lines
if (pageId === 'SendSMS') return <SendSMS />
if (pageId === 'SendSmsGroup') return <SendSmsGroup />

if (pageId === 'EmailReport') return <EmailReport />
if (pageId === 'SmsReport') return <SmsReport />

//Hostel render lines
if (pageId === 'AssignHostel') return <AssignHostel />
if (pageId === 'HostelFees') return <HostelFees />

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
