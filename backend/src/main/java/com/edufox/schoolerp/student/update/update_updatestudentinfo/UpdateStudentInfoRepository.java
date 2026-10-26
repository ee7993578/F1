package com.edufox.schoolerp.student.update.update_updatestudentinfo;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Update/UpdateStudentInfo.jsx. */
@Repository("page_com_edufox_schoolerp_student_update_update_updatestudentinfo_updatestudentinforepository_java")
public class UpdateStudentInfoRepository extends PageArtifactRepository {
    public UpdateStudentInfoRepository() {
        super("Student", "update", "UpdateStudentInfo", "Student/Update/UpdateStudentInfo.jsx");
    }
}