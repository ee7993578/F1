package com.edufox.schoolerp.attendance.work.tapcard_rfid_transportstudentattendance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/TapCard/RFID_TransportStudentAttendance.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_work_tapcard_rfid_transportstudentattendance_rfid_transportstudentattendancerepository_java")
public class RFID_TransportStudentAttendanceRepository extends PageArtifactRepository {
    public RFID_TransportStudentAttendanceRepository() {
        super("Attendance", "work", "RFID_TransportStudentAttendance", "Attendance/TapCard/RFID_TransportStudentAttendance.jsx");
    }
}