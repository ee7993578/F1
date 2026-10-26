package com.edufox.schoolerp.canteen.report.report_printinvoice;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Canteen/Report/PrintInvoice.jsx. */
@Service("page_canteen_report_report_printinvoice_printinvoiceservice_java")
public class PrintInvoiceService extends PageArtifactService<PrintInvoiceRepository> {
    public PrintInvoiceService(PrintInvoiceRepository repository) {
        super(repository);
    }
}