package com.edufox.schoolerp.student.setup.setup_housemaster;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Setup/HouseMaster.jsx. */
@RestController("page_student_setup_setup_housemaster_housemastercontroller_java")
@RequestMapping("/api/page-artifacts/student/setup/setup_housemaster")
public class HouseMasterController extends PageArtifactController<HouseMasterService> {
    public HouseMasterController(HouseMasterService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}