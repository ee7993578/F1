package com.edufox.schoolerp.examination.report.report_report_examresultdiscussion;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/Report_ExamResultDiscussion.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_report_examresultdiscussion_report_examresultdiscussionrepository_java")
public class Report_ExamResultDiscussionRepository extends PageArtifactRepository {
    public Report_ExamResultDiscussionRepository() {
        super("ExamMaster", "report", "Report_ExamResultDiscussion", "ExamMaster/Report/Report_ExamResultDiscussion.jsx");
    }
}