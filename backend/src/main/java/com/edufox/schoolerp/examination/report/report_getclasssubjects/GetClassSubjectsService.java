package com.edufox.schoolerp.examination.report.report_getclasssubjects;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/GetClassSubjects.jsx. */
@Service("page_examination_report_report_getclasssubjects_getclasssubjectsservice_java")
public class GetClassSubjectsService extends PageArtifactService<GetClassSubjectsRepository> {
    public GetClassSubjectsService(GetClassSubjectsRepository repository) {
        super(repository);
    }
}