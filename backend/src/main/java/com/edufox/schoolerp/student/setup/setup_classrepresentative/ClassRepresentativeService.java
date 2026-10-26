package com.edufox.schoolerp.student.setup.setup_classrepresentative;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for Student/Setup/ClassRepresentative.jsx. */
@Service("page_student_setup_setup_classrepresentative_classrepresentativeservice_java")
public class ClassRepresentativeService extends PageArtifactService<ClassRepresentativeRepository> {
    public ClassRepresentativeService(ClassRepresentativeRepository repository) {
        super(repository);
    }
}