package com.edufox.schoolerp.studentportal.report.report_examschedule;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/ExamSchedule.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_examschedule_examschedulerepository_java")
public class ExamScheduleRepository extends PageArtifactRepository {
    public ExamScheduleRepository() {
        super("StundentPages", "report", "ExamSchedule", "StundentPages/Report/ExamSchedule.jsx");
    }
}