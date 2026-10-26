package com.edufox.schoolerp.examination.report.report_getclasssubjects;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/GetClassSubjects.jsx. */
@RestController("page_examination_report_report_getclasssubjects_getclasssubjectscontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_getclasssubjects")
public class GetClassSubjectsController extends PageArtifactController<GetClassSubjectsService> {
    public GetClassSubjectsController(GetClassSubjectsService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}