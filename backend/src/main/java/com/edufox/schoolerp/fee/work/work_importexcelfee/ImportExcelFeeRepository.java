package com.edufox.schoolerp.fee.work.work_importexcelfee;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Fee/Work/ImportExcelFee.jsx. */
@Repository("page_com_edufox_schoolerp_fee_work_work_importexcelfee_importexcelfeerepository_java")
public class ImportExcelFeeRepository extends PageArtifactRepository {
    public ImportExcelFeeRepository() {
        super("Fee", "work", "ImportExcelFee", "Fee/Work/ImportExcelFee.jsx");
    }
}