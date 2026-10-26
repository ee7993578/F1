package com.edufox.schoolerp.fee.work.work_waveofffees;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/WaveOffFees.jsx. */
@Service("page_fee_work_work_waveofffees_waveofffeesservice_java")
public class WaveOffFeesService extends PageArtifactService<WaveOffFeesRepository> {
    public WaveOffFeesService(WaveOffFeesRepository repository) {
        super(repository);
    }
}