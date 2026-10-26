package com.edufox.schoolerp.attendance.work.tapcard_rfidattendance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/TapCard/RFIDAttendance.jsx. */
@Service("page_attendance_work_tapcard_rfidattendance_rfidattendanceservice_java")
public class RFIDAttendanceService extends PageArtifactService<RFIDAttendanceRepository> {
    public RFIDAttendanceService(RFIDAttendanceRepository repository) {
        super(repository);
    }
}