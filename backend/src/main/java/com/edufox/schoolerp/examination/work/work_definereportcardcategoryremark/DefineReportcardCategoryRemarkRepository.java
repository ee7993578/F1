package com.edufox.schoolerp.examination.work.work_definereportcardcategoryremark;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for ExamMaster/Work/DefineReportcardCategoryRemark.jsx. */
@Repository("page_com_edufox_schoolerp_examination_work_work_definereportcardcategoryremark_definereportcardcategoryremarkrepository_java")
public class DefineReportcardCategoryRemarkRepository extends PageArtifactRepository {
    public DefineReportcardCategoryRemarkRepository() {
        super("ExamMaster", "work", "DefineReportcardCategoryRemark", "ExamMaster/Work/DefineReportcardCategoryRemark.jsx");
    }
}