package com.edufox.schoolerp.attendance.work.tapcard_rfid_campusstudentattendance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/TapCard/RFID_CampusStudentAttendance.jsx. */
@Service("page_attendance_work_tapcard_rfid_campusstudentattendance_rfid_campusstudentattendanceservice_java")
public class RFID_CampusStudentAttendanceService extends PageArtifactService<RFID_CampusStudentAttendanceRepository> {
    public RFID_CampusStudentAttendanceService(RFID_CampusStudentAttendanceRepository repository) {
        super(repository);
    }
}