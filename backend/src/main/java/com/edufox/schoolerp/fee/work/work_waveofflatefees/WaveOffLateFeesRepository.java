package com.edufox.schoolerp.fee.work.work_waveofflatefees;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/WaveOffLateFees.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_waveofflatefees_waveofflatefeesrepository_java")
public class WaveOffLateFeesRepository extends PageArtifactRepository {
    public WaveOffLateFeesRepository() {
        super("Fee", "work", "WaveOffLateFees", "Fee/Work/WaveOffLateFees.jsx");
    }
}