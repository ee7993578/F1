package com.edufox.schoolerp.student.setup.setup_classrepresentative;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for Student/Setup/ClassRepresentative.jsx. */
@Repository("page_com_edufox_schoolerp_student_setup_setup_classrepresentative_classrepresentativerepository_java")
public class ClassRepresentativeRepository extends PageArtifactRepository {
    public ClassRepresentativeRepository() {
        super("Student", "setup", "ClassRepresentative", "Student/Setup/ClassRepresentative.jsx");
    }
}