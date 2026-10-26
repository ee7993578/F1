package com.edufox.schoolerp.examination.work.work_definereportcardcategoryremark;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for ExamMaster/Work/DefineReportcardCategoryRemark.jsx. */
@Service("page_examination_work_work_definereportcardcategoryremark_definereportcardcategoryremarkservice_java")
public class DefineReportcardCategoryRemarkService extends PageArtifactService<DefineReportcardCategoryRemarkRepository> {
    public DefineReportcardCategoryRemarkService(DefineReportcardCategoryRemarkRepository repository) {
        super(repository);
    }
}