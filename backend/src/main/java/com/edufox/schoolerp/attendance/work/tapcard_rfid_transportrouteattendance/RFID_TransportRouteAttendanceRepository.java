package com.edufox.schoolerp.attendance.work.tapcard_rfid_transportrouteattendance;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Attendance/TapCard/RFID_TransportRouteAttendance.jsx. */
@Repository("page_com_edufox_schoolerp_attendance_work_tapcard_rfid_transportrouteattendance_rfid_transportrouteattendancerepository_java")
public class RFID_TransportRouteAttendanceRepository extends PageArtifactRepository {
    public RFID_TransportRouteAttendanceRepository() {
        super("Attendance", "work", "RFID_TransportRouteAttendance", "Attendance/TapCard/RFID_TransportRouteAttendance.jsx");
    }
}