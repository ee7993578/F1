package com.edufox.schoolerp.attendance.update.update_deletestudentattendance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/Update/DeleteStudentAttendance.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_update_update_deletestudentattendance_deletestudentattendancerepository_java")
public class DeleteStudentAttendanceRepository extends PageArtifactRepository {
    public DeleteStudentAttendanceRepository() {
        super("Attendance", "update", "DeleteStudentAttendance", "Attendance/Update/DeleteStudentAttendance.jsx");
    }
}