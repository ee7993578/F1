package com.edufox.schoolerp.canteen.report.report_printinvoice;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Canteen/Report/PrintInvoice.jsx. */
@Repository("page_com_edufox_schoolerp_canteen_report_report_printinvoice_printinvoicerepository_java")
public class PrintInvoiceRepository extends PageArtifactRepository {
    public PrintInvoiceRepository() {
        super("Canteen", "report", "PrintInvoice", "Canteen/Report/PrintInvoice.jsx");
    }
}