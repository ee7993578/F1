package com.edufox.schoolerp.student.update.update_promotedemote;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Update/PromoteDemote.jsx. */
@RestController("page_student_update_update_promotedemote_promotedemotecontroller_java")
@RequestMapping("/api/page-artifacts/student/update/update_promotedemote")
public class PromoteDemoteController extends PageArtifactController<PromoteDemoteService> {
    public PromoteDemoteController(PromoteDemoteService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}