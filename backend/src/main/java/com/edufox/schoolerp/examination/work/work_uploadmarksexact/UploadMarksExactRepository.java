package com.edufox.schoolerp.examination.work.work_uploadmarksexact;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/UploadMarksExact.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_uploadmarksexact_uploadmarksexactrepository_java")
public class UploadMarksExactRepository extends PageArtifactRepository {
    public UploadMarksExactRepository() {
        super("ExamMaster", "work", "UploadMarksExact", "ExamMaster/Work/UploadMarksExact.jsx");
    }
}