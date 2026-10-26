package com.edufox.schoolerp.examination.report.report_report_examresultdiscussion;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/Report_ExamResultDiscussion.jsx. */
@Service("page_examination_report_report_report_examresultdiscussion_report_examresultdiscussionservice_java")
public class Report_ExamResultDiscussionService extends PageArtifactService<Report_ExamResultDiscussionRepository> {
    public Report_ExamResultDiscussionService(Report_ExamResultDiscussionRepository repository) {
        super(repository);
    }
}