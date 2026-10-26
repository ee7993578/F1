package com.edufox.schoolerp.examination.work.work_greensheet;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/GreenSheet.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_greensheet_greensheetrepository_java")
public class GreenSheetRepository extends PageArtifactRepository {
    public GreenSheetRepository() {
        super("ExamMaster", "work", "GreenSheet", "ExamMaster/Work/GreenSheet.jsx");
    }
}