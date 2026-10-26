package com.edufox.schoolerp.attendance.work.tapcard_rfidattendancedynamic;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/TapCard/RFIDAttendanceDynamic.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_work_tapcard_rfidattendancedynamic_rfidattendancedynamicrepository_java")
public class RFIDAttendanceDynamicRepository extends PageArtifactRepository {
    public RFIDAttendanceDynamicRepository() {
        super("Attendance", "work", "RFIDAttendanceDynamic", "Attendance/TapCard/RFIDAttendanceDynamic.jsx");
    }
}