package com.edufox.schoolerp.attendance.work.tapcard_rfidcampusattendancecount;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/TapCard/RFIDCampusAttendanceCount.jsx. */
@RestController("page_attendance_work_tapcard_rfidcampusattendancecount_rfidcampusattendancecountcontroller_java")
@RequestMapping("/api/page-artifacts/attendance/work/tapcard_rfidcampusattendancecount")
public class RFIDCampusAttendanceCountController extends PageArtifactController<RFIDCampusAttendanceCountService> {
    public RFIDCampusAttendanceCountController(RFIDCampusAttendanceCountService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}