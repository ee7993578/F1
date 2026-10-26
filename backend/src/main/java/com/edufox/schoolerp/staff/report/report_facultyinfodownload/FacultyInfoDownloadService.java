package com.edufox.schoolerp.staff.report.report_facultyinfodownload;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Staff/Report/FacultyInfoDownload.jsx. */
@Service("page_staff_report_report_facultyinfodownload_facultyinfodownloadservice_java")
public class FacultyInfoDownloadService extends PageArtifactService<FacultyInfoDownloadRepository> {
    public FacultyInfoDownloadService(FacultyInfoDownloadRepository repository) {
        super(repository);
    }
}