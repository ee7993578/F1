package com.edufox.schoolerp.fee.report.report_studentfeesummary;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Report/StudentFeeSummary.jsx. */
@Service("page_fee_report_report_studentfeesummary_studentfeesummaryservice_java")
public class StudentFeeSummaryService extends PageArtifactService<StudentFeeSummaryRepository> {
    public StudentFeeSummaryService(StudentFeeSummaryRepository repository) {
        super(repository);
    }
}