package com.edufox.schoolerp.attendance.work.tapcard_rfidcampusattendancecount;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/TapCard/RFIDCampusAttendanceCount.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_work_tapcard_rfidcampusattendancecount_rfidcampusattendancecountrepository_java")
public class RFIDCampusAttendanceCountRepository extends PageArtifactRepository {
    public RFIDCampusAttendanceCountRepository() {
        super("Attendance", "work", "RFIDCampusAttendanceCount", "Attendance/TapCard/RFIDCampusAttendanceCount.jsx");
    }
}