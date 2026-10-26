package com.edufox.schoolerp.examination.report.report_class_overall_marks;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Report/Class_overall_marks.jsx. */
@RestController("page_examination_report_report_class_overall_marks_class_overall_markscontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/report_class_overall_marks")
public class Class_overall_marksController extends PageArtifactController<Class_overall_marksService> {
    public Class_overall_marksController(Class_overall_marksService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}