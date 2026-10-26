package com.edufox.schoolerp.hostel.work.work_hostelfees;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Hostel/Work/HostelFees.jsx. */
@RestController("page_hostel_work_work_hostelfees_hostelfeescontroller_java")
@RequestMapping("/api/page-artifacts/hostel/work/work_hostelfees")
public class HostelFeesController extends PageArtifactController<HostelFeesService> {
    public HostelFeesController(HostelFeesService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}