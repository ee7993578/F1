package com.edufox.schoolerp.student.update.update_updatestudentinfosheet;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Update/UpdateStudentInfosheet.jsx. */
@RestController("page_student_update_update_updatestudentinfosheet_updatestudentinfosheetcontroller_java")
@RequestMapping("/api/page-artifacts/student/update/update_updatestudentinfosheet")
public class UpdateStudentInfosheetController extends PageArtifactController<UpdateStudentInfosheetService> {
    public UpdateStudentInfosheetController(UpdateStudentInfosheetService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}