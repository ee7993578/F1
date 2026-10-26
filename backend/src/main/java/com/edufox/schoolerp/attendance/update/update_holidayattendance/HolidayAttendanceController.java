package com.edufox.schoolerp.attendance.update.update_holidayattendance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/Update/HolidayAttendance.jsx. */
@RestController("page_attendance_update_update_holidayattendance_holidayattendancecontroller_java")
@RequestMapping("/api/page-artifacts/attendance/update/update_holidayattendance")
public class HolidayAttendanceController extends PageArtifactController<HolidayAttendanceService> {
    public HolidayAttendanceController(HolidayAttendanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}