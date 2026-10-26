package com.edufox.schoolerp.examination.work.work_definereportcardcategoryremark;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for ExamMaster/Work/DefineReportcardCategoryRemark.jsx. */
@RestController("page_examination_work_work_definereportcardcategoryremark_definereportcardcategoryremarkcontroller_java")
@RequestMapping("/api/page-artifacts/exammaster/work/work_definereportcardcategoryremark")
public class DefineReportcardCategoryRemarkController extends PageArtifactController<DefineReportcardCategoryRemarkService> {
    public DefineReportcardCategoryRemarkController(DefineReportcardCategoryRemarkService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}