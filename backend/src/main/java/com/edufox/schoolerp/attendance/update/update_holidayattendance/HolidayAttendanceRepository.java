package com.edufox.schoolerp.attendance.update.update_holidayattendance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/Update/HolidayAttendance.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_update_update_holidayattendance_holidayattendancerepository_java")
public class HolidayAttendanceRepository extends PageArtifactRepository {
    public HolidayAttendanceRepository() {
        super("Attendance", "update", "HolidayAttendance", "Attendance/Update/HolidayAttendance.jsx");
    }
}