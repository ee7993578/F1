package com.edufox.schoolerp.attendance.update.update_deleteattendance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/Update/DeleteAttendance.jsx. */
@RestController("page_attendance_update_update_deleteattendance_deleteattendancecontroller_java")
@RequestMapping("/api/page-artifacts/attendance/update/update_deleteattendance")
public class DeleteAttendanceController extends PageArtifactController<DeleteAttendanceService> {
    public DeleteAttendanceController(DeleteAttendanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}