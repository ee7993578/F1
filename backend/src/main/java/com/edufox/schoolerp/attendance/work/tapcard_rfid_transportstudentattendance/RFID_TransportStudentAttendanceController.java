package com.edufox.schoolerp.attendance.work.tapcard_rfid_transportstudentattendance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/TapCard/RFID_TransportStudentAttendance.jsx. */
@RestController("page_attendance_work_tapcard_rfid_transportstudentattendance_rfid_transportstudentattendancecontroller_java")
@RequestMapping("/api/page-artifacts/attendance/work/tapcard_rfid_transportstudentattendance")
public class RFID_TransportStudentAttendanceController extends PageArtifactController<RFID_TransportStudentAttendanceService> {
    public RFID_TransportStudentAttendanceController(RFID_TransportStudentAttendanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}