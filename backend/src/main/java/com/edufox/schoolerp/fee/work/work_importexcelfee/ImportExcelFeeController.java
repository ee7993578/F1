package com.edufox.schoolerp.fee.work.work_importexcelfee;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Fee/Work/ImportExcelFee.jsx. */
@RestController("page_fee_work_work_importexcelfee_importexcelfeecontroller_java")
@RequestMapping("/api/page-artifacts/fee/work/work_importexcelfee")
public class ImportExcelFeeController extends PageArtifactController<ImportExcelFeeService> {
    public ImportExcelFeeController(ImportExcelFeeService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}