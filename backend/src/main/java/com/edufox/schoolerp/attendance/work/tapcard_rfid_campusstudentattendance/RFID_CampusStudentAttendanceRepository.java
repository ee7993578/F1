package com.edufox.schoolerp.attendance.work.tapcard_rfid_campusstudentattendance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/TapCard/RFID_CampusStudentAttendance.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_work_tapcard_rfid_campusstudentattendance_rfid_campusstudentattendancerepository_java")
public class RFID_CampusStudentAttendanceRepository extends PageArtifactRepository {
    public RFID_CampusStudentAttendanceRepository() {
        super("Attendance", "work", "RFID_CampusStudentAttendance", "Attendance/TapCard/RFID_CampusStudentAttendance.jsx");
    }
}