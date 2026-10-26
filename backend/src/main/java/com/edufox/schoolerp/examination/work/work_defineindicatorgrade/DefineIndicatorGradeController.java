package com.edufox.schoolerp.examination.work.work_defineindicatorgrade;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Work/DefineIndicatorGrade.jsx. */
@RestController("page_examination_work_work_defineindicatorgrade_defineindicatorgradecontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/work_defineindicatorgrade")
public class DefineIndicatorGradeController extends PageArtifactController<DefineIndicatorGradeService> {
    public DefineIndicatorGradeController(DefineIndicatorGradeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}