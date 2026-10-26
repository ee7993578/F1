package com.edufox.schoolerp.attendance.work.tapcard_rfid_campusstudentattendance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/TapCard/RFID_CampusStudentAttendance.jsx. */
@RestController("page_attendance_work_tapcard_rfid_campusstudentattendance_rfid_campusstudentattendancecontroller_java")
@RequestMapping("/api/page-artifacts/attendance/work/tapcard_rfid_campusstudentattendance")
public class RFID_CampusStudentAttendanceController extends PageArtifactController<RFID_CampusStudentAttendanceService> {
    public RFID_CampusStudentAttendanceController(RFID_CampusStudentAttendanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}