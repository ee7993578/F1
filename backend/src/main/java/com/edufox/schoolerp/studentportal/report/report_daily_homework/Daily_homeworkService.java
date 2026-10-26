package com.edufox.schoolerp.studentportal.report.report_daily_homework;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for StundentPages/Report/Daily_homework.jsx. */
@Service("page_studentportal_report_report_daily_homework_daily_homeworkservice_java")
public class Daily_homeworkService extends PageArtifactService<Daily_homeworkRepository> {
    public Daily_homeworkService(Daily_homeworkRepository repository) {
        super(repository);
    }
}