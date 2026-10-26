package com.edufox.schoolerp.attendance.work.tapcard_rfidattendancedynamic;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/TapCard/RFIDAttendanceDynamic.jsx. */
@RestController("page_attendance_work_tapcard_rfidattendancedynamic_rfidattendancedynamiccontroller_java")
@RequestMapping("/api/page-artifacts/attendance/work/tapcard_rfidattendancedynamic")
public class RFIDAttendanceDynamicController extends PageArtifactController<RFIDAttendanceDynamicService> {
    public RFIDAttendanceDynamicController(RFIDAttendanceDynamicService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}