package com.edufox.schoolerp.examination.report.cbse_result_cbse_aisse_result;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/CBSE Result/CBSE_AISSE_Result.jsx. */
@RestController("page_examination_report_cbse_result_cbse_aisse_result_cbse_aisse_resultcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/cbse_result_cbse_aisse_result")
public class CBSE_AISSE_ResultController extends PageArtifactController<CBSE_AISSE_ResultService> {
    public CBSE_AISSE_ResultController(CBSE_AISSE_ResultService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}