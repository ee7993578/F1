package com.edufox.schoolerp.student.update.update_updatestudentinfo;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Update/UpdateStudentInfo.jsx. */
@RestController("page_student_update_update_updatestudentinfo_updatestudentinfocontroller_java")
@RequestMapping("/api/page-artifacts/student/update/update_updatestudentinfo")
public class UpdateStudentInfoController extends PageArtifactController<UpdateStudentInfoService> {
    public UpdateStudentInfoController(UpdateStudentInfoService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}