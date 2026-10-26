package com.edufox.schoolerp.attendance.work.tapcard_rfid_transportstudentattendance;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Attendance/TapCard/RFID_TransportStudentAttendance.jsx. */
@Service("page_attendance_work_tapcard_rfid_transportstudentattendance_rfid_transportstudentattendanceservice_java")
public class RFID_TransportStudentAttendanceService extends PageArtifactService<RFID_TransportStudentAttendanceRepository> {
    public RFID_TransportStudentAttendanceService(RFID_TransportStudentAttendanceRepository repository) {
        super(repository);
    }
}