package com.edufox.schoolerp.attendance.update.update_deleteattendance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/Update/DeleteAttendance.jsx. */
@Service("page_attendance_update_update_deleteattendance_deleteattendanceservice_java")
public class DeleteAttendanceService extends PageArtifactService<DeleteAttendanceRepository> {
    public DeleteAttendanceService(DeleteAttendanceRepository repository) {
        super(repository);
    }
}