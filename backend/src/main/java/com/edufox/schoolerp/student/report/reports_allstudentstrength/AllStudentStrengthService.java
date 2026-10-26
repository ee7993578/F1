package com.edufox.schoolerp.student.report.reports_allstudentstrength;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Reports/AllStudentStrength.jsx. */
@Service("page_student_report_reports_allstudentstrength_allstudentstrengthservice_java")
public class AllStudentStrengthService extends PageArtifactService<AllStudentStrengthRepository> {
    public AllStudentStrengthService(AllStudentStrengthRepository repository) {
        super(repository);
    }
}