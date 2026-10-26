package com.edufox.schoolerp.fee.update.update_feenotice;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Update/FeeNotice.jsx. */
@Service("page_fee_update_update_feenotice_feenoticeservice_java")
public class FeeNoticeService extends PageArtifactService<FeeNoticeRepository> {
    public FeeNoticeService(FeeNoticeRepository repository) {
        super(repository);
    }
}