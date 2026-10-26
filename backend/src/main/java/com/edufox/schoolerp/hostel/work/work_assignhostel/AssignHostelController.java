package com.edufox.schoolerp.hostel.work.work_assignhostel;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Hostel/Work/AssignHostel.jsx. */
@RestController("page_hostel_work_work_assignhostel_assignhostelcontroller_java")
@RequestMapping("/api/page-artifacts/hostel/work/work_assignhostel")
public class AssignHostelController extends PageArtifactController<AssignHostelService> {
    public AssignHostelController(AssignHostelService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}