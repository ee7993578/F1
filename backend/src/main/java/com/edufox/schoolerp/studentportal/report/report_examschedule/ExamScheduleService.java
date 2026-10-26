package com.edufox.schoolerp.studentportal.report.report_examschedule;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/ExamSchedule.jsx. */
@Service("page_studentportal_report_report_examschedule_examscheduleservice_java")
public class ExamScheduleService extends PageArtifactService<ExamScheduleRepository> {
    public ExamScheduleService(ExamScheduleRepository repository) {
        super(repository);
    }
}