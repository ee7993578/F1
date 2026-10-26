package com.edufox.schoolerp.studentportal.report.report_daily_homework;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for StundentPages/Report/Daily_homework.jsx. */
@Repository("page_com_edufox_schoolerp_studentportal_report_report_daily_homework_daily_homeworkrepository_java")
public class Daily_homeworkRepository extends PageArtifactRepository {
    public Daily_homeworkRepository() {
        super("StundentPages", "report", "Daily_homework", "StundentPages/Report/Daily_homework.jsx");
    }
}