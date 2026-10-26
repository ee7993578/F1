package com.edufox.schoolerp.attendance.update.update_deletestudentattendance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/Update/DeleteStudentAttendance.jsx. */
@Service("page_attendance_update_update_deletestudentattendance_deletestudentattendanceservice_java")
public class DeleteStudentAttendanceService extends PageArtifactService<DeleteStudentAttendanceRepository> {
    public DeleteStudentAttendanceService(DeleteStudentAttendanceRepository repository) {
        super(repository);
    }
}