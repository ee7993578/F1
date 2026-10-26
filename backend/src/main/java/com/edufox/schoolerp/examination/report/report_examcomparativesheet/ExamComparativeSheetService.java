package com.edufox.schoolerp.examination.report.report_examcomparativesheet;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/ExamComparativeSheet.jsx. */
@Service("page_examination_report_report_examcomparativesheet_examcomparativesheetservice_java")
public class ExamComparativeSheetService extends PageArtifactService<ExamComparativeSheetRepository> {
    public ExamComparativeSheetService(ExamComparativeSheetRepository repository) {
        super(repository);
    }
}