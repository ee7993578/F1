package com.edufox.schoolerp.examination.setup.settingprimary_indicator;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/SettingPrimary/Indicator.jsx. */
@RestController("page_examination_setup_settingprimary_indicator_indicatorcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/setup/settingprimary_indicator")
public class IndicatorController extends PageArtifactController<IndicatorService> {
    public IndicatorController(IndicatorService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}