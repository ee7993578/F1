package com.edufox.schoolerp.fee.work.work_waveofffees;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/WaveOffFees.jsx. */
@RestController("page_fee_work_work_waveofffees_waveofffeescontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_waveofffees")
public class WaveOffFeesController extends PageArtifactController<WaveOffFeesService> {
    public WaveOffFeesController(WaveOffFeesService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}