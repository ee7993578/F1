package com.edufox.schoolerp.examination.report.cbse_result_cbse_aisse_result;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/CBSE Result/CBSE_AISSE_Result.jsx. */
@Service("page_examination_report_cbse_result_cbse_aisse_result_cbse_aisse_resultservice_java")
public class CBSE_AISSE_ResultService extends PageArtifactService<CBSE_AISSE_ResultRepository> {
    public CBSE_AISSE_ResultService(CBSE_AISSE_ResultRepository repository) {
        super(repository);
    }
}