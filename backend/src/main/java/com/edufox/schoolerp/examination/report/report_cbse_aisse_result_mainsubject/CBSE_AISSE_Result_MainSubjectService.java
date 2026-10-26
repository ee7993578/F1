package com.edufox.schoolerp.examination.report.report_cbse_aisse_result_mainsubject;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/CBSE_AISSE_Result_MainSubject.jsx. */
@Service("page_examination_report_report_cbse_aisse_result_mainsubject_cbse_aisse_result_mainsubjectservice_java")
public class CBSE_AISSE_Result_MainSubjectService extends PageArtifactService<CBSE_AISSE_Result_MainSubjectRepository> {
    public CBSE_AISSE_Result_MainSubjectService(CBSE_AISSE_Result_MainSubjectRepository repository) {
        super(repository);
    }
}