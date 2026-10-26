package com.edufox.schoolerp.attendance.update.update_deleteattendance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/Update/DeleteAttendance.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_update_update_deleteattendance_deleteattendancerepository_java")
public class DeleteAttendanceRepository extends PageArtifactRepository {
    public DeleteAttendanceRepository() {
        super("Attendance", "update", "DeleteAttendance", "Attendance/Update/DeleteAttendance.jsx");
    }
}