package com.edufox.schoolerp.attendance.work.work_studentattendance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/Work/StudentAttendance.jsx. */
@RestController("page_attendance_work_work_studentattendance_studentattendancecontroller_java")
@RequestMapping("/api/page-artifacts/attendance/work/work_studentattendance")
public class StudentAttendanceController extends PageArtifactController<StudentAttendanceService> {
    public StudentAttendanceController(StudentAttendanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}