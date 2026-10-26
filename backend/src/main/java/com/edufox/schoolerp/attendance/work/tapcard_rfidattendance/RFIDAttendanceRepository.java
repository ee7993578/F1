package com.edufox.schoolerp.attendance.work.tapcard_rfidattendance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/TapCard/RFIDAttendance.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_work_tapcard_rfidattendance_rfidattendancerepository_java")
public class RFIDAttendanceRepository extends PageArtifactRepository {
    public RFIDAttendanceRepository() {
        super("Attendance", "work", "RFIDAttendance", "Attendance/TapCard/RFIDAttendance.jsx");
    }
}