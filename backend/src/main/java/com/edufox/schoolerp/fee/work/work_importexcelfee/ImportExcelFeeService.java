package com.edufox.schoolerp.fee.work.work_importexcelfee;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Fee/Work/ImportExcelFee.jsx. */
@Service("page_fee_work_work_importexcelfee_importexcelfeeservice_java")
public class ImportExcelFeeService extends PageArtifactService<ImportExcelFeeRepository> {
    public ImportExcelFeeService(ImportExcelFeeRepository repository) {
        super(repository);
    }
}