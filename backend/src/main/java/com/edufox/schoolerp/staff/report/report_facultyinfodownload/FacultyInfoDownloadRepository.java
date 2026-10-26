package com.edufox.schoolerp.staff.report.report_facultyinfodownload;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Staff/Report/FacultyInfoDownload.jsx. */
@Repository("page_com_edufox_schoolerp_staff_report_report_facultyinfodownload_facultyinfodownloadrepository_java")
public class FacultyInfoDownloadRepository extends PageArtifactRepository {
    public FacultyInfoDownloadRepository() {
        super("Staff", "report", "FacultyInfoDownload", "Staff/Report/FacultyInfoDownload.jsx");
    }
}