package com.edufox.schoolerp.uploads.work.work_academicplanner;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Uploads/Work/AcademicPlanner.jsx. */
@RestController("page_uploads_work_work_academicplanner_academicplannercontroller_java")
@RequestMapping("/api/page-artifacts/uploads/work/work_academicplanner")
public class AcademicPlannerController extends PageArtifactController<AcademicPlannerService> {
    public AcademicPlannerController(AcademicPlannerService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}