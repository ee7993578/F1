package com.edufox.schoolerp.student.update.update_updatestudentinfo;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Update/UpdateStudentInfo.jsx. */
@Service("page_student_update_update_updatestudentinfo_updatestudentinfoservice_java")
public class UpdateStudentInfoService extends PageArtifactService<UpdateStudentInfoRepository> {
    public UpdateStudentInfoService(UpdateStudentInfoRepository repository) {
        super(repository);
    }
}