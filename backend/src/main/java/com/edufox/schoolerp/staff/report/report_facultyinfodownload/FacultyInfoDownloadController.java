package com.edufox.schoolerp.staff.report.report_facultyinfodownload;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Staff/Report/FacultyInfoDownload.jsx. */
@RestController("page_staff_report_report_facultyinfodownload_facultyinfodownloadcontroller_java")
@RequestMapping("/api/page-artifacts/staff/report/report_facultyinfodownload")
public class FacultyInfoDownloadController extends PageArtifactController<FacultyInfoDownloadService> {
    public FacultyInfoDownloadController(FacultyInfoDownloadService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}