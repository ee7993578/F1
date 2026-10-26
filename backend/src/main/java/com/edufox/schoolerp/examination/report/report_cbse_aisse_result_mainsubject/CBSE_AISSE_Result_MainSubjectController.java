package com.edufox.schoolerp.examination.report.report_cbse_aisse_result_mainsubject;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/CBSE_AISSE_Result_MainSubject.jsx. */
@RestController("page_examination_report_report_cbse_aisse_result_mainsubject_cbse_aisse_result_mainsubjectcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_cbse_aisse_result_mainsubject")
public class CBSE_AISSE_Result_MainSubjectController extends PageArtifactController<CBSE_AISSE_Result_MainSubjectService> {
    public CBSE_AISSE_Result_MainSubjectController(CBSE_AISSE_Result_MainSubjectService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}