package com.edufox.schoolerp.attendance.work.tapcard_rfid_transportrouteattendance;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Attendance/TapCard/RFID_TransportRouteAttendance.jsx. */
@RestController("page_attendance_work_tapcard_rfid_transportrouteattendance_rfid_transportrouteattendancecontroller_java")
@RequestMapping("/api/page-artifacts/attendance/work/tapcard_rfid_transportrouteattendance")
public class RFID_TransportRouteAttendanceController extends PageArtifactController<RFID_TransportRouteAttendanceService> {
    public RFID_TransportRouteAttendanceController(RFID_TransportRouteAttendanceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}