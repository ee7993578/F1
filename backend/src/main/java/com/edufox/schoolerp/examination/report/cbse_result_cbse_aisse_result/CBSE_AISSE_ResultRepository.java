package com.edufox.schoolerp.examination.report.cbse_result_cbse_aisse_result;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/CBSE Result/CBSE_AISSE_Result.jsx. */
@Repository("page_com_edufox_schoolerp_examination_report_cbse_result_cbse_aisse_result_cbse_aisse_resultrepository_java")
public class CBSE_AISSE_ResultRepository extends PageArtifactRepository {
    public CBSE_AISSE_ResultRepository() {
        super("ExamMaster", "report", "CBSE_AISSE_Result", "ExamMaster/CBSE Result/CBSE_AISSE_Result.jsx");
    }
}