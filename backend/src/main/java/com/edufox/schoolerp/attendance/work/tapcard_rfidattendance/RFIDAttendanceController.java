package com.edufox.schoolerp.attendance.work.tapcard_rfidattendance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/TapCard/RFIDAttendance.jsx. */
@RestController("page_attendance_work_tapcard_rfidattendance_rfidattendancecontroller_java")
@RequestMapping("/api/page-artifacts/attendance/work/tapcard_rfidattendance")
public class RFIDAttendanceController extends PageArtifactController<RFIDAttendanceService> {
    public RFIDAttendanceController(RFIDAttendanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}