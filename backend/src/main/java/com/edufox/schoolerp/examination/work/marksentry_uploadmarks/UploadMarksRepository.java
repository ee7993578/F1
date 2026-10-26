package com.edufox.schoolerp.examination.work.marksentry_uploadmarks;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/marksEntry/UploadMarks.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_marksentry_uploadmarks_uploadmarksrepository_java")
public class UploadMarksRepository extends PageArtifactRepository {
    public UploadMarksRepository() {
        super("ExamMaster", "work", "UploadMarks", "ExamMaster/marksEntry/UploadMarks.jsx");
    }
}