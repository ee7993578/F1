package com.edufox.schoolerp.master.work.work_logreportdatewise;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for MasterSetting/Work/LogReportDateWise.jsx. */
@Repository("page_com_edufox_schoolerp_master_work_work_logreportdatewise_logreportdatewiserepository_java")
public class LogReportDateWiseRepository extends PageArtifactRepository {
    public LogReportDateWiseRepository() {
        super("MasterSetting", "work", "LogReportDateWise", "MasterSetting/Work/LogReportDateWise.jsx");
    }
}