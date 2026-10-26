package com.edufox.schoolerp.student.update.student_update;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

import java.util.List;

public final class StudentUpdateDtos {
    private StudentUpdateDtos() {}

    public record MoveRequest(
        String operation,
        String sourceSession,
        String targetSession,
        Integer sourceSectionId,
        Integer targetSectionId,
        List<String> studentUids,
        Boolean sameRegistrationNo,
        String sourceSchoolId,
        String targetSchoolId
    ) {}

    public record MoveResponse(int moved, String message) {}
}
