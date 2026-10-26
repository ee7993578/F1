package com.edufox.schoolerp.examination.report.report_cbse_aisse_result_mainsubject;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Report/CBSE_AISSE_Result_MainSubject.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_report_cbse_aisse_result_mainsubject_cbse_aisse_result_mainsubjectrepository_java")
public class CBSE_AISSE_Result_MainSubjectRepository extends PageArtifactRepository {
    public CBSE_AISSE_Result_MainSubjectRepository() {
        super("ExamMaster", "report", "CBSE_AISSE_Result_MainSubject", "ExamMaster/Report/CBSE_AISSE_Result_MainSubject.jsx");
    }
}