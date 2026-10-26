package com.edufox.schoolerp.student.report.reports_entranceexamadmitcard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Reports/EntranceExamAdmitCard.jsx. */
@RestController("page_student_report_reports_entranceexamadmitcard_entranceexamadmitcardcontroller_java")
@RequestMapping("/api/page-artifacts/student/report/reports_entranceexamadmitcard")
public class EntranceExamAdmitCardController extends PageArtifactController<EntranceExamAdmitCardService> {
    public EntranceExamAdmitCardController(EntranceExamAdmitCardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}