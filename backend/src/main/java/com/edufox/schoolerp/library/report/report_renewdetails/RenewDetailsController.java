package com.edufox.schoolerp.library.report.report_renewdetails;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Library/Report/RenewDetails.jsx. */
@RestController("page_library_report_report_renewdetails_renewdetailscontroller_java")
@RequestMapping("/api/page-artifacts/library/report/report_renewdetails")
public class RenewDetailsController extends PageArtifactController<RenewDetailsService> {
    public RenewDetailsController(RenewDetailsService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}