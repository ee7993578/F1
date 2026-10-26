package com.edufox.schoolerp.examination.report.reportanalytics_classwisestudentpercentage;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/ReportAnalytics/ClasswiseStudentPercentage.jsx. */
@RestController("page_examination_report_reportanalytics_classwisestudentpercentage_classwisestudentpercentagecontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/report/reportanalytics_classwisestudentpercentage")
public class ClasswiseStudentPercentageController extends PageArtifactController<ClasswiseStudentPercentageService> {
    public ClasswiseStudentPercentageController(ClasswiseStudentPercentageService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}