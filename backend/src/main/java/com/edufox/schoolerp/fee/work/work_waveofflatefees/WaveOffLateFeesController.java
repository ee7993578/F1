package com.edufox.schoolerp.fee.work.work_waveofflatefees;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/WaveOffLateFees.jsx. */
@RestController("page_fee_work_work_waveofflatefees_waveofflatefeescontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_waveofflatefees")
public class WaveOffLateFeesController extends PageArtifactController<WaveOffLateFeesService> {
    public WaveOffLateFeesController(WaveOffLateFeesService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}