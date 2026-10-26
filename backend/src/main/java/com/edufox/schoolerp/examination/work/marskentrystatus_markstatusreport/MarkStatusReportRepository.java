package com.edufox.schoolerp.examination.work.marskentrystatus_markstatusreport;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/marskEntryStatus/MarkStatusReport.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_marskentrystatus_markstatusreport_markstatusreportrepository_java")
public class MarkStatusReportRepository extends PageArtifactRepository {
    public MarkStatusReportRepository() {
        super("ExamMaster", "work", "MarkStatusReport", "ExamMaster/marskEntryStatus/MarkStatusReport.jsx");
    }
}