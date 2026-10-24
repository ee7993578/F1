/**
 * MENU_DATA — Static config. Future: swap with API call GET /api/menu
 * Shape: TopModule → groups[] → pages[]
 */

export const MENU_DATA = [
  {
    id: 'dashboard', label: 'Dashboard',
    groups: [
      {
        id: 'allDashboard', label: 'All Dashboard', icon: 'Home',
        pages: [
          { id: 'AdminDashboard',      label: 'Admin Dashboard' },
          { id: 'AdminDashboardAttendance',      label: 'Admin Dashboard Attendance' },
          { id: 'AdminDashboardFees',      label: 'Admin Dashboard Fees' },
          { id: 'AdminDashBoardMarks',      label: 'Admin DashBoard Marks' },
          { id: 'FacultyAttendanceDashboard',      label: 'Faculty Attendance Dashboard' },
          { id: 'LibraryDashBoard',      label: 'Library DashBoard' },
          { id: 'ManagementDashboard',      label: 'Management Dashboard' },
        ],
      },
      
    ],
  },
  
  
  {
    id: 'transport', label: 'Transport',
    groups: [
      {
        id: 'transport_work', label: 'Work', icon: 'MapPin',
        pages: [
              { id: 'AssignTransport',   label: 'Assign Transport' },
          { id: 'DriverVehicleMaster',   label: 'Driver Vehicle Master' },
          { id: 'VehicleMaster',   label: 'Vehicle Master' },
         
        ],
      },
      {
        id: 'transport', label: 'Report', icon: 'UserCheck',
        pages: [
        { id: 'AssignRoute',   label: 'Assign Route' },
        { id: 'MonthwiseTransportReport',   label: 'Monthwise Transport Report' },
        { id: 'StudentTransportReport',   label: 'Student Transport Report' },
        { id: 'TransportPrivateStudentsReport',   label: 'Transport Private Students Report' },
        ],
      },
      {
        id: 'transport_setup', label: 'Setup', icon: 'UserCheck',
        pages: [
          { id: 'DefineTransportType',   label: 'Define Transport Type' },
          { id: 'RouteMaster',   label: 'Route Master' },
          { id: 'RouteStoppageMapping',   label: 'Route Stoppage Mapping' },
          { id: 'StoppageMaster',   label: 'Stoppage Master' },
        ],
      },
    ],
  },

  {
    id: 'student', label: 'Student',
    groups: [
      {
        id: 'stu_work', label: 'Work', icon: 'FilePlus',
        pages: [
          { id: 'studentRegistration', label: 'Student Registration' },
          { id: 'AdmNoAllotment', label: 'Registration No' },
          { id: 'newClassAllotment', label: 'Allotment Section' },
          { id: 'StudentICard', label: 'Student ICard' },
          { id: 'AdmitCard', label: 'Admit card' },
          { id: 'SearchStudent', label: 'Search Student' },
            { id: 'RegistrationRedirection',    label: 'Online Registration Form' },
          { id: 'BulkAdmissionOnlineRegistration',    label: 'Online Confirm Admission' },
        ],
      },
      {
        id: 'stu_update', label: 'Updates', icon: 'CheckSquare',
        pages: [
          { id: 'UpdateStudentInfo',    label: 'Update Student Info' },
          { id: 'ChangeAdmNo',    label: 'Change Admission No' },
          { id: 'UploadStudentImage',    label: 'Update Student Image' },
          { id: 'StudentWithdrawn',    label: 'Student Withdrawn' },
          { id: 'UpdateStudentInfosheet',    label: 'Update A Student' },
          { id: 'PromoteDemote',    label: 'Promote Demote' },
        ],
      },
      {
        id: 'stu_report', label: 'Reports', icon: 'FilePlus',
        pages: [
          { id: 'StudentIDPassword',    label: 'Student ID Password' },
          { id: 'StrengthReport',    label: 'Strength Report' },
          { id: 'ClasswiseStrengthReport',    label: 'Classwise Strength Report' },
          { id: 'AllStudentStrength',    label: 'All Student Strength' },
          { id: 'HouseReport',    label: 'House Report' },
          { id: 'StudentTypeReport',    label: 'Student Type Report' },
          { id: 'BirthdayReport',    label: 'Birthday Report' },
          { id: 'SiblingReport',    label: 'Sibling Report' },
          { id: 'StudentInfoSheet',    label: 'Student Info Sheet' },
          { id: 'StudentList',    label: 'Student List' },
          { id: 'OnlineRegistrationReportStatus',    label: 'Online Registration Report Status' },
          { id: 'OnlineRegistrationReport',    label: 'Online Registration Report' },
          { id: 'UDISEPromotionReport',    label: 'UDISE Promotion Report' },
          { id: 'AdmittedStudentReport',    label: 'Admitted Student Report' },
          { id: 'EntranceExamAdmitCard',    label: 'Entrance Exam Admit Card' },
          { id: 'RegistrationReport',    label: 'Registration Report' },
          { id: 'StudentWithdrawnReport',    label: 'Student Withdrawn Report' },
          { id: 'DownloadRegistrationForm',    label: 'Download Registration Form' },
          { id: 'NewStudentReport',    label: 'New Student Report' },
          { id: 'StudentRegistrationDetails',    label: 'Registration Fee Report' },
          { id: 'StudentRecord',    label: 'Student Record' },
          { id: 'AlumniRegistration',    label: 'Alumni Registration' },
          { id: 'ClassRepresentative',    label: 'Class Representative' },

        ],
      },
      {
        id: 'stu_setup', label: 'Setup', icon: 'CheckSquare',
        pages: [
          { id: 'AlumniRegistration',    label: 'Alumni Registration ' },
          { id: 'HouseMaster',    label: 'House Master ' },
          { id: 'ClassRepresentative',    label: 'Class Representative ' },
          { id: 'RegistrationOpen',    label: 'Registration Open ' },
          { id: 'ExamConfiguration',    label: 'Exam Configuration ' },
        ],
      },
    ],
  },
 {
    id: 'staff', label: 'Staff',
    groups: [
      {
        id: 'staff_work', label: 'Work', icon: 'FilePlus',
        pages: [
          { id: 'FacultyRegistration', label: 'Faculty Registration' },
          { id: 'FacultyReregistration', label: 'Faculty Reregistration' },   
        ],
      },
      {
        id: 'staff_update', label: 'Updates', icon: 'CheckSquare',
        pages: [
          { id: 'FacultyInfosheet',    label: 'Update Faculty Info' },
        ],
      },
      {
        id: 'staff_report', label: 'Reports', icon: 'FilePlus',
        pages: [
          { id: 'FacultyInfoDownload',    label: 'Faculty Info Download' },
          { id: 'FacultyIdPassword',    label: 'Faculty ID Password' },
          { id: 'JobApplicationReport',    label: 'Job Application Report' },
          { id: 'ClassTeacherReport',    label: 'Class Teacher Report' },

          
        ],
      },
      {
        id: 'staff_setup', label: 'Setup', icon: 'CheckSquare',
        pages: [
          { id: 'DefineClassTeacher',    label: 'Define Class Teacher ' },
          { id: 'RegistrationOpenConfig',    label: 'Registration Open Config ' },
        ],
      },
    ],
  },

  {
    id: 'fee', label: 'Fee',
    groups: [
      {
        id: 'fee_work', label: 'Work', icon: 'FilePlus',
        pages: [
          { id: 'DepositFee', label: 'Deposit Fee' },
          { id: 'DepositLateFeesFee', label: 'Deposit LateFees' },
          { id: 'FeeSettlement', label: 'Fee Settlement' },
          { id: 'ImportExcelFee', label: 'Import Excel Fee' },
          { id: 'LateFeeReportWork', label: 'Late Fee Report' },
          { id: 'MiscellaneousFee', label: 'Miscellaneous Fee' },
          { id: 'RefundableFees', label: 'Refundable Fees' },
          { id: 'WaveOffFees', label: 'Wave Off Fees' },
          { id: 'WaveOffLateFees', label: 'Wave Off Late Fees' },
         
        ],
      },
      {
        id: 'fee_update', label: 'Updates', icon: 'CheckSquare',
        pages: [
           { id: 'AssignConGroup', label: 'Assign Concession Group' },
           { id: 'AssignOptional', label: 'Assign Optional Subjects' },
           { id: 'FeeNotice', label: 'Generate Fee Notice' },
           { id: 'ReconFee', label: 'Reconcile Fees' },
           { id: 'ReprintFeeSlip', label: 'Reprint Fee Slip' },
           { id: 'UpdateStudentFee', label: 'Update Student Fee' },
        ],
      },
      {
        id: 'fee_report', label: 'Reports', icon: 'FilePlus',
        pages: [
        { id: 'BudgetReport', label: 'Budget Report' },
        { id: 'ClasswiseFeeReport', label: 'Classwise FeeReport' },
        { id: 'ConcessionGroupReport', label: 'Concession Group Report' },
        { id: 'ConcessionReport', label: 'Concession Report' },
        { id: 'DailyCollectionReport', label: 'Daily Collection Report' },
        { id: 'DailyDepositReport', label: 'Daily Deposit Report' },
        { id: 'FeeDefaulterReport', label: 'Fee Defaulter Report' },
        { id: 'FeeReconciliationReport', label: 'Fee Reconciliation Report' },
        { id: 'FeeSubReconciliationReport', label: 'Fee SubReconciliation Report' },
        { id: 'InstallmentWiseDefaulter', label: 'Installment Wise Defaulter' },
        { id: 'LateFeeReport', label: 'Late Fee Report' },
        { id: 'PaymentResponseReport', label: 'Payment Response Report' },
        ],
      },
      {
        id: 'fee_setup', label: 'Setup', icon: 'CheckSquare',
        pages: [
            { id: 'FeeHeadMaster', label: 'Fee Head Master' },
            { id: 'FeeHeadChargeMaster', label: 'Fee Head Charge Master' },
            { id: 'DefineInstallment', label: 'Define Installment' },
            { id: 'DefineLateFee', label: 'Define LateFee' },
            { id: 'DefineConcession', label: 'Define Concession' },
            { id: 'DefineConcessionGroup', label: 'Define Concession Group' },
            { id: 'DefineConcessionValue', label: 'Define Concession Value' },
        ],
      },
    ],
  },



  {
    id: 'timetable', label: 'Time Table',
    groups: [
      {
        id: 'timetable_work', label: 'Work', icon: 'Calendar',
        pages: [
          { id: 'AltTimetable',    label: 'Alt Timetable' },
          { id: 'AssignTimeTable',    label: 'Assign TimeTable' },
          { id: 'FacultySubjectMapping',    label: 'Faculty Subject Mapping' },
          { id: 'ClassActivity',    label: 'Define Class Activity' },
        
        ],
      },
      {
        id: 'timetable_setup', label: 'Setup', icon: 'CheckSquare',
        pages: [
          { id: 'CreateVirtualClass',  label: 'Create Virtual Class' },
          { id: 'DefineHolidays',  label: 'Define Holidays' },
          { id: 'LectureTimings',  label: 'Lecture Timings' },
          
        ],
      },
      {
        id: 'timetable_update', label: 'Update', icon: 'CheckSquare',
        pages: [
         
          
        ],
      },
      {
        id: 'timetable_report', label: 'Report', icon: 'CheckSquare',
        pages: [
          { id: 'ActivityReport',  label: 'Activity Report' },
          { id: 'ArrangementReport',  label: 'Arrangement Report' },
          { id: 'CompleteTimeTableReport',  label: 'Complete TimeTable Report' },
          { id: 'FacultyFreePeriod',  label: 'Faculty Free Period' },
          { id: 'SchoolTimetableReport',  label: 'School Timetable Report' },
          { id: 'TimetableReport',  label: 'Timetable Report' },
          { id: 'VirtualClassReport',  label: 'Virtual Class Report' },
          
        ],
      },
    ],
  },
  {
    id: 'attendance', label: 'Attendance',
    groups: [
      {
        id: 'attedance_work', label: 'Work', icon: 'Package',
        pages: [
          { id: 'StudentAttendance',   label: 'Student Attendance' },
       
        ],
      },
      {
        id: 'attedance_update', label: 'Update', icon: 'Package',
        pages: [
          { id: 'DeleteAttendance',   label: 'DeleteAttendance' },
          { id: 'DeleteStudentAttendance', label: 'Delete Student Attendance' },
          { id: 'HolidayAttendance',   label: 'HolidayAttendance' },
       
        ],
      },
      {
        id: 'attedance_tapcard', label: 'Tap card', icon: 'Package',
        pages: [
          { id: 'RFID_CampusStudentAttendance',   label: 'RFID Campus Student Attendance' },
          { id: 'RFID_TransportRouteAttendance',   label: 'RFID Transport Route Attendance' },
          { id: 'RFIDAttendance',   label: 'RFID Attendance' },
          { id: 'RFID_TransportStudentAttendance',   label: 'RFID Transport Student Attendance' },
          { id: 'RFIDCampusAttendanceCount',   label: 'RFID Campus Attendance Count' },
       
        ],
      },
      {
        id: 'attedance_report', label: 'Report', icon: 'Package',
        pages: [
         { id: 'AttendanceReportPercentage',   label: 'Attendance Report Percentage' },
         { id: 'AttendanceStatusReport',   label: 'Attendance Status Report' },
         { id: 'TotalAttendanceReport',   label: 'Total Attendance' },
       
        ],
      },
      {
        id: 'attedance_setup', label: 'Setup', icon: 'Package',
        pages: [
      
       
        ],
      }
     
    ],
  },

  {
  id: 'library',
  label: 'Library',
  groups: [
    {
      id: 'library_work',
      label: 'Work',
      icon: 'FilePlus',
      pages: [
        { id: 'Issue_book', label: 'Issue Book' },
        { id: 'ItemEntryMaster', label: 'Item Entry Master' },
        { id: 'PublisherEntry', label: 'Publisher Entry' },
        { id: 'SupplierEntry', label: 'Supplier Entry' },
      ],
    },
    {
  id: 'library_report',
  label: 'Reports',
  icon: 'FileText',
  pages: [
    { id: 'BookList', label: 'Book List' },
    { id: 'BookReturnReport', label: 'Book Return Report' },
    { id: 'BooksIssueReturnDetail', label: 'Books Issue Return Detail' },
    { id: 'DefaulterReport', label: 'Defaulter Report' },
    { id: 'RenewDetails', label: 'Renew Details' },
    { id: 'ReturnDateReport', label: 'Student Report Return Date' },
    { id: 'IssueReturnDetails', label: 'Issue Return Details' },
    { id: 'LibraryCategoryReport', label: 'Library Book Category' },
    { id: 'SlowMovingStock', label: 'Slow Moving Stock' },
    { id: 'StockInHand', label: 'Stock In Hand' },
  ],
},{
  id: 'library_setup',
  label: 'Setup',
  icon: 'CheckSquare',
  pages: [
    { id: 'SupplierCategory', label: 'Supplier Category' },
    { id: 'PublisherCategory', label: 'Publisher Category' },
    { id: 'ItemCategory', label: 'Item Category' },
    { id: 'ItemFrequencyMaster', label: 'Item Frequency Master' },
    { id: 'ItemLanguage', label: 'Item Language' },
    { id: 'ItemRulesMaster', label: 'Item Rules Master' },
    { id: 'ItemTypeMaster', label: 'Item Type Master' },
    { id: 'MemberCategory', label: 'Member Category' },
  ],
}
  ],
},{
  id: 'website',
  label: 'Website',
  groups: [
    {
      id: 'website_work',
      label: 'Work',
      icon: 'FilePlus',
      pages: [
        { id: 'UploadNotice', label: 'Upload Notice' },
        { id: 'UploadNews', label: 'Upload News' },
        { id: 'UploadExamSchedule', label: 'Upload Exam Schedule' },
        { id: 'VideoGallery', label: 'Video Gallery' },
        { id: 'UploadTC', label: 'Upload TC' },
        { id: 'PhotoGallery', label: 'Photo Gallery' },
        { id: 'LatestEvents', label: 'Latest Events' },
      ],
    },
    {
      id: 'website_report',
      label: 'Reports',
      icon: 'FileText',
      pages: [
        { id: 'ShowUploadExamSchedule', label: 'Show Upload Exam Schedule' },
      ],
    },
  ],
},

  {
    id: 'uploads',
    label: 'Uploads',
    groups: [
      {
        id: 'uploads_work',
        label: 'Work',
        icon: 'FilePlus',
        pages: [
          { id: 'UploadAcademicPlanner', label: 'Upload Academic Planner' },
          { id: 'UploadHolidayHomework', label: 'Upload Holiday Homework' },
          { id: 'UploadSyllabus', label: 'Upload Syllabus' },
          { id: 'UploadDailyHomework', label: 'Upload Daily Homework' },
          { id: 'UploadAssignment', label: 'Upload Assignment' },
        ],
      },
      {
        id: 'uploads_report',
        label: 'Reports',
        icon: 'FileText',
        pages: [
          { id: 'ShowSyllabus', label: 'Show Syllabus' },
          { id: 'AcademicPlannerReport', label: 'Academic Planner Report' },
          { id: 'MobileAppCoverageReport', label: 'MobileApp Coverage Report' },
          { id: 'ShowDailyHomework', label: 'Daily Home Work' },
          { id: 'AssignmentReport', label: 'Assignment Report' },
          { id: 'DailyHomeworkReport', label: 'Class Wise Daily Homework' },
        ],
      },
      {
        id: 'uploads_setup',
        label: 'Setup',
        icon: 'CheckSquare',
        pages: [
          { id: 'SchoolCalendar', label: 'Calender' },
          { id: 'DefineActivity', label: 'Activity' },
        ],
      },
    ],
  },

  //May be incorrect
{
  id: 'examMaster', label: 'Exam Master',
  groups: [
    {
      id: 'exam_work', label: 'Work', icon: 'FilePlus',
      pages: [
        { id: 'DefineMarksNew', label: 'Define Marks' },
        { id: 'DefineMarksPrePrimary', label: 'Define Marks Pre Primary' },
        { id: 'UploadMarks', label: 'Upload Marks' },
        { id: 'GreenSheet', label: 'Green Sheet' },
        { id: 'StuHealthStatus', label: 'Student Health Status' },
        { id: 'DefineStudentRemark', label: 'Define Student Remark' },
        { id: 'DefineReportcardCategoryRemark', label: 'Define Reportcard Category Remark' },
        { id: 'DefineLearnerProfileIndicator', label: 'Define Learner Profile Indicator' },
        { id: 'DefineIndicatorGradePrimary', label: 'Define Indicator Grade' },
      ],
    },
    {
      id: 'exam_setup', label: 'Setup', icon: 'CheckSquare',
      pages: [
        { id: 'Define_ClassSubject', label: 'Define Class Subject' },
        { id: 'SubjectMaster', label: 'Subject Master' },
        { id: 'DefinePrSubject', label: 'Define Primary Subject' },
        { id: 'DefineSubSubject', label: 'Define Sub Subject' },
        { id: 'DefineMaxMin', label: 'Define Max Min' },
        { id: 'DefineMaxMinExam', label: 'Define Max Min (Exam)' },
        { id: 'DefineWeightage', label: 'Define Weightage' },
        { id: 'DefineAttrib', label: 'Define Attribute' },
        { id: 'DefineCategory', label: 'Define Category' },
        { id: 'DefineHeader', label: 'Define Header' },
        { id: 'Indicator', label: 'Indicator' },
        { id: 'IndicatorCategory', label: 'Indicator Category' },
        { id: 'LearnerProfileAttributeValue', label: 'Learner Profile Attribute Value' },
        { id: 'LockUnlockStudent', label: 'Lock Unlock Student' },
        { id: 'StudentChartLock', label: 'Student Chart Lock' },
        { id: 'LockExam', label: 'Lock Exam' },
      ],
    },
    {
      id: 'exam_report', label: 'Reports', icon: 'FileText',
      pages: [
        { id: 'CumulativeSheet', label: 'Cumulative Sheet' },
        { id: 'GetClassSubjects', label: 'Get Class Subjects' },
        { id: 'MarksLogReport', label: 'Marks Log Report' },
        { id: 'MarksStatusGraph', label: 'Marks Status Graph' },
        { id: 'MarkStatusReport', label: 'Mark Status Report' },
        { id: 'OverallExaminationReport', label: 'Overall Examination Report' },
        { id: 'ReportCard', label: 'Report Card' },
        { id: 'ReportCardExamwise', label: 'Report Card Examwise' },
        { id: 'StudentAcademicPerformance', label: 'Student Academic Performance' },
        { id: 'SubjectReport', label: 'Subject Report' },
        { id: 'ToppersReport', label: 'Toppers Report' },
        { id: 'CBSE_AISSE_Result', label: 'CBSE AISSE Result' },
        { id: 'CBSE_AISSE_Result_MainSubject', label: 'CBSE AISSE Result (Main Subject)' },
        { id: 'CBSE_AnnualReport', label: 'CBSE Annual Report' },
        { id: 'Class_overall_marks', label: 'Class Overall Marks' },
        { id: 'ClasswiseStudentPercentage', label: 'Classwise Student Percentage' },
        { id: 'ExamComparativeSheet', label: 'Exam Comparative Sheet' },
        { id: 'ExamwiseGreensheet', label: 'Examwise Greensheet' },
        { id: 'ExpectedBoardAverageReport', label: 'Expected Board Average Report' },
        { id: 'ExpectedSchoolAverageReport', label: 'Expected School Average Report' },
        { id: 'FailureStudentReport', label: 'Failure Student Report' },
        { id: 'GreenSheetNew', label: 'Green Sheet New' },
        { id: 'GreenSheetPrimary', label: 'Green Sheet Primary' },
        { id: 'GreenSheetSubjectWise', label: 'Green Sheet Subject Wise' },
        { id: 'MarksListBlank', label: 'Marks List Blank' },
        { id: 'MarksListJunior', label: 'Marks List Junior' },
        { id: 'Report_ExamResultDiscussion', label: 'Exam Result Discussion Report' },
      ],
    },
    {
      id: 'exam_update', label: 'Update', icon: 'CheckSquare',
      pages: [
        { id: 'DeleteMarks', label: 'Delete Marks' },
      ],
    },
  ],
},
{
  id: 'certificates', label: 'Certificates',
  groups: [
    {
      id: 'cert_work', label: 'Work', icon: 'FilePlus',
      pages: [
        { id: 'BonafiedCertificate', label: 'Bonafied Certificate' },
        { id: 'CharacterCertificate', label: 'Character Certificate' },
        { id: 'FeeCertificate', label: 'Fee Certificate' },
        { id: 'IncomeCertificate', label: 'Income Certificate' },
        { id: 'TransferCertificate', label: 'Transfer Certificate' },
      ],
    },
    {
      id: 'cert_report', label: 'Reports', icon: 'FileText',
      pages: [
        { id: 'BonafideCertificateReport', label: 'Bonafide Certificate Report' },
        { id: 'CharacterCertificateReport', label: 'Character Certificate Report' },
        { id: 'TC_Report', label: 'TC Report' },
        { id: 'TCReport', label: 'TC Report (New)' },
      ],
    },
  ],
},
{
  id: 'canteen', label: 'Canteen',
  groups: [
    {
      id: 'canteen_work', label: 'Work', icon: 'FilePlus',
      pages: [
        { id: 'AssignExpense', label: 'Assign Expense' },
        { id: 'ExpenseGroupMaster', label: 'Expense Group Master' },
        { id: 'ExpenseNameMaster', label: 'Expense Name Master' },
        { id: 'ProductMaster', label: 'Product Master' },
        { id: 'ProductGroup', label: 'Product Group' },
        { id: 'UpdateCreditBalance', label: 'Update Credit Balance' },
      ],
    },
    {
      id: 'canteen_report', label: 'Reports', icon: 'FileText',
      pages: [
        { id: 'DebitCreditBalanceReport', label: 'Debit Credit Balance Report' },
        { id: 'PrintInvoice', label: 'Print Invoice' },
        { id: 'ProductBalanceReport', label: 'Product Balance Report' },
        { id: 'RemainingProductReport', label: 'Remaining Product Report' },
        { id: 'TuckshopAssignExpenseReport', label: 'Tuckshop Assign Expense Report' },
        { id: 'TuckshopProductIssueDetail', label: 'Tuckshop Product Issue Detail' },
        { id: 'TuckshopReport', label: 'Tuckshop Report' },
      ],
    },
  ],
},
{
  id: 'communication', label: 'Communication',
  groups: [
    {
      id: 'comm_work', label: 'Work', icon: 'FilePlus',
      pages: [
        { id: 'SendSMS', label: 'Send SMS' },
        { id: 'SendSmsGroup', label: 'Send SMS Group' },
      ],
    },
    {
      id: 'comm_report', label: 'Reports', icon: 'FileText',
      pages: [
        { id: 'EmailReport', label: 'Email Report' },
        { id: 'SmsReport', label: 'SMS Report' },
      ],
    },
  ],
},
{
  id: 'hostel', label: 'Hostel',
  groups: [
    {
      id: 'hostel_work', label: 'Work', icon: 'FilePlus',
      pages: [
        { id: 'AssignHostel', label: 'Assign Hostel' },
        { id: 'HostelFees', label: 'Hostel Fees' },
      ],
    },
    {
      id: 'hostel_report', label: 'Reports', icon: 'FileText',
      pages: [
        { id: 'HostelAssignedReport', label: 'Assigned Hostel Report' },
      ],
    },
  ],
},
{
  id: 'masterSetting', label: 'Master Setting',
  groups: [
    {
      id: 'master_work', label: 'Work', icon: 'CheckSquare',
      pages: [
        { id: 'ChangeSectionName', label: 'Change Section Name' },
        { id: 'ClassSectionMaster', label: 'Class Section Master' },
        { id: 'DefineStaffRole', label: 'Define Staff Role' },
        { id: 'LogReportDateWise', label: 'Log Report Date Wise' },
        { id: 'ModuleMapping', label: 'Module Mapping' },
        { id: 'RoleList', label: 'Role List' },
        { id: 'RolesPermission', label: 'Roles Permission' },
        { id: 'SessionMaster', label: 'Session Master' },
        { id: 'SessionTerm', label: 'Session Term' },
        { id: 'SwitchSession', label: 'Switch Session' },
        { id: 'ViewProfile', label: 'View Profile' },
      ],
    },
  ],
},
{
  id: 'leave', label: 'Leave',
  groups: [
    {
      id: 'leave_work', label: 'Work', icon: 'FilePlus',
      pages: [
        { id: 'FacultyLeaveList', label: 'Faculty Leave List' },
        { id: 'FacultyTotalLeave', label: 'Faculty Total Leave' },
        { id: 'IndentForm', label: 'Indent Form' },
        { id: 'IndentFormApproval', label: 'Indent Form Approval' },
        { id: 'IndentFormApprovalPrincipal', label: 'Indent Form Approval (Principal)' },
        { id: 'IndentFormList', label: 'Indent Form List' },
        { id: 'LeaveAllocation', label: 'Leave Allocation' },
        { id: 'LeaveApplyForm', label: 'Leave Apply Form' },
        { id: 'LeaveApproval', label: 'Leave Approval' },
        { id: 'LeaveApprovalByPrincipal', label: 'Leave Approval By Principal' },
        { id: 'LeaveForm', label: 'Leave Form' },
        { id: 'LeaveStaffApproval', label: 'Leave Staff Approval' },
      ],
    },
  ],
}

]
