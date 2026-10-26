package com.edufox.schoolerp.examination.work.work_definestudentremark;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Work/DefineStudentRemark.jsx. */
@RestController("page_examination_work_work_definestudentremark_definestudentremarkcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/work_definestudentremark")
public class DefineStudentRemarkController extends PageArtifactController<DefineStudentRemarkService> {
    public DefineStudentRemarkController(DefineStudentRemarkService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}