package com.edufox.schoolerp.student.update.update_updatealumnirecord;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for Student/Update/UpdateAlumniRecord.jsx. */
@RestController("page_student_update_update_updatealumnirecord_updatealumnirecordcontroller_java")
@RequestMapping("/api/page-artifacts/student/update/update_updatealumnirecord")
public class UpdateAlumniRecordController extends PageArtifactController<UpdateAlumniRecordService> {
    public UpdateAlumniRecordController(UpdateAlumniRecordService service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}