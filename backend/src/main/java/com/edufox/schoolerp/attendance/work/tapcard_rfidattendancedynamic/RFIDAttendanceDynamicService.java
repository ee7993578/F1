package com.edufox.schoolerp.attendance.work.tapcard_rfidattendancedynamic;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/TapCard/RFIDAttendanceDynamic.jsx. */
@Service("page_attendance_work_tapcard_rfidattendancedynamic_rfidattendancedynamicservice_java")
public class RFIDAttendanceDynamicService extends PageArtifactService<RFIDAttendanceDynamicRepository> {
    public RFIDAttendanceDynamicService(RFIDAttendanceDynamicRepository repository) {
        super(repository);
    }
}