package com.edufox.schoolerp.canteen.report.report_printinvoice;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Canteen/Report/PrintInvoice.jsx. */
@RestController("page_canteen_report_report_printinvoice_printinvoicecontroller_java")
@RequestMapping("/api/page-artifacts/canteen/report/report_printinvoice")
public class PrintInvoiceController extends PageArtifactController<PrintInvoiceService> {
    public PrintInvoiceController(PrintInvoiceService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}