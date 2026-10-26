package com.edufox.schoolerp.attendance.update.update_deletestudentattendance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/Update/DeleteStudentAttendance.jsx. */
@RestController("page_attendance_update_update_deletestudentattendance_deletestudentattendancecontroller_java")
@RequestMapping("/api/page-artifacts/attendance/update/update_deletestudentattendance")
public class DeleteStudentAttendanceController extends PageArtifactController<DeleteStudentAttendanceService> {
    public DeleteStudentAttendanceController(DeleteStudentAttendanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}