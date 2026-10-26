package com.edufox.schoolerp.student.work.work_admitcard;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Work/AdmitCard.jsx. */
@RestController("page_student_work_work_admitcard_admitcardcontroller_java")
@RequestMapping("/api/page-artifacts/student/work/work_admitcard")
public class AdmitCardController extends PageArtifactController<AdmitCardService> {
    public AdmitCardController(AdmitCardService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}