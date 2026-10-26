package com.edufox.schoolerp.examination.report.report_class_overall_marks;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Report/Class_overall_marks.jsx. */
@Service("page_examination_report_report_class_overall_marks_class_overall_marksservice_java")
public class Class_overall_marksService extends PageArtifactService<Class_overall_marksRepository> {
    public Class_overall_marksService(Class_overall_marksRepository repository) {
        super(repository);
    }
}