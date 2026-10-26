package com.edufox.schoolerp.attendance.work.tapcard_rfidcampusattendancecount;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/TapCard/RFIDCampusAttendanceCount.jsx. */
@Service("page_attendance_work_tapcard_rfidcampusattendancecount_rfidcampusattendancecountservice_java")
public class RFIDCampusAttendanceCountService extends PageArtifactService<RFIDCampusAttendanceCountRepository> {
    public RFIDCampusAttendanceCountService(RFIDCampusAttendanceCountRepository repository) {
        super(repository);
    }
}