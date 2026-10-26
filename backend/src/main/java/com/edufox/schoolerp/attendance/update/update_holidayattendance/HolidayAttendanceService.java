package com.edufox.schoolerp.attendance.update.update_holidayattendance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/Update/HolidayAttendance.jsx. */
@Service("page_attendance_update_update_holidayattendance_holidayattendanceservice_java")
public class HolidayAttendanceService extends PageArtifactService<HolidayAttendanceRepository> {
    public HolidayAttendanceService(HolidayAttendanceRepository repository) {
        super(repository);
    }
}