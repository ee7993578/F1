package com.edufox.schoolerp.examination.report.report_getclasssubjects;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/GetClassSubjects.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_getclasssubjects_getclasssubjectsrepository_java")
public class GetClassSubjectsRepository extends PageArtifactRepository {
    public GetClassSubjectsRepository() {
        super("ExamMaster", "report", "GetClassSubjects", "ExamMaster/Report/GetClassSubjects.jsx");
    }
}