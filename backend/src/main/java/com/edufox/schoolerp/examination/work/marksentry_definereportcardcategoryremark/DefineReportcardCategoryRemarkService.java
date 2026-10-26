package com.edufox.schoolerp.examination.work.marksentry_definereportcardcategoryremark;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/marksEntry/DefineReportcardCategoryRemark.jsx. */
@Service("page_examination_work_marksentry_definereportcardcategoryremark_definereportcardcategoryremarkservice_java")
public class DefineReportcardCategoryRemarkService extends PageArtifactService<DefineReportcardCategoryRemarkRepository> {
    public DefineReportcardCategoryRemarkService(DefineReportcardCategoryRemarkRepository repository) {
        super(repository);
    }
}