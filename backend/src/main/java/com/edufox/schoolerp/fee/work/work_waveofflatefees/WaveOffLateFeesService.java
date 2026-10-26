package com.edufox.schoolerp.fee.work.work_waveofflatefees;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/WaveOffLateFees.jsx. */
@Service("page_fee_work_work_waveofflatefees_waveofflatefeesservice_java")
public class WaveOffLateFeesService extends PageArtifactService<WaveOffLateFeesRepository> {
    public WaveOffLateFeesService(WaveOffLateFeesRepository repository) {
        super(repository);
    }
}