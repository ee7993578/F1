package com.edufox.schoolerp.attendance.work.work_studentattendance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/Work/StudentAttendance.jsx. */
@Service("page_attendance_work_work_studentattendance_studentattendanceservice_java")
public class StudentAttendanceService extends PageArtifactService<StudentAttendanceRepository> {
    public StudentAttendanceService(StudentAttendanceRepository repository) {
        super(repository);
    }
}