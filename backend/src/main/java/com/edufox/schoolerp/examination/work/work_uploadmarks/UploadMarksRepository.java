package com.edufox.schoolerp.examination.work.work_uploadmarks;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/UploadMarks.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_uploadmarks_uploadmarksrepository_java")
public class UploadMarksRepository extends PageArtifactRepository {
    public UploadMarksRepository() {
        super("ExamMaster", "work", "UploadMarks", "ExamMaster/Work/UploadMarks.jsx");
    }
}