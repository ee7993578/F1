package com.edufox.schoolerp.attendance.work.tapcard_rfid_transportrouteattendance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/TapCard/RFID_TransportRouteAttendance.jsx. */
@Service("page_attendance_work_tapcard_rfid_transportrouteattendance_rfid_transportrouteattendanceservice_java")
public class RFID_TransportRouteAttendanceService extends PageArtifactService<RFID_TransportRouteAttendanceRepository> {
    public RFID_TransportRouteAttendanceService(RFID_TransportRouteAttendanceRepository repository) {
        super(repository);
    }
}