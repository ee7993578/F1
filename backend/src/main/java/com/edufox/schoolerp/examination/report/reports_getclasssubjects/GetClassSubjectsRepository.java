package com.edufox.schoolerp.examination.report.reports_getclasssubjects;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Reports/GetClassSubjects.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_reports_getclasssubjects_getclasssubjectsrepository_java")
public class GetClassSubjectsRepository extends PageArtifactRepository {
    public GetClassSubjectsRepository() {
        super("ExamMaster", "report", "GetClassSubjects", "ExamMaster/Reports/GetClassSubjects.jsx");
    }
}