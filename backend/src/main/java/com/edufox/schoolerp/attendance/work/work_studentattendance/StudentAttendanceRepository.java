package com.edufox.schoolerp.attendance.work.work_studentattendance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/Work/StudentAttendance.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_work_work_studentattendance_studentattendancerepository_java")
public class StudentAttendanceRepository extends PageArtifactRepository {
    public StudentAttendanceRepository() {
        super("Attendance", "work", "StudentAttendance", "Attendance/Work/StudentAttendance.jsx");
    }
}