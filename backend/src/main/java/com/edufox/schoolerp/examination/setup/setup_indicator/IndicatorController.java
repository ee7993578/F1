package com.edufox.schoolerp.examination.setup.setup_indicator;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Setup/Indicator.jsx. */
@RestController("page_examination_setup_setup_indicator_indicatorcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/setup_indicator")
public class IndicatorController extends PageArtifactController<IndicatorService> {
    public IndicatorController(IndicatorService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}