package com.edufox.schoolerp.fee.work.work_waveofffees;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/WaveOffFees.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_waveofffees_waveofffeesrepository_java")
public class WaveOffFeesRepository extends PageArtifactRepository {
    public WaveOffFeesRepository() {
        super("Fee", "work", "WaveOffFees", "Fee/Work/WaveOffFees.jsx");
    }
}