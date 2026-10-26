package com.edufox.schoolerp.student.work.student_admission;
import com.edufox.schoolerp.student.setup.alumni.*;
import com.edufox.schoolerp.student.setup.class_representative.*;
import com.edufox.schoolerp.student.setup.exam_configuration.*;
import com.edufox.schoolerp.student.setup.house_master.*;
import com.edufox.schoolerp.student.setup.registration_open.*;
import com.edufox.schoolerp.student.shared.student_core.*;
import com.edufox.schoolerp.student.update.student_image.*;
import com.edufox.schoolerp.student.update.student_mutation.*;
import com.edufox.schoolerp.student.update.student_update.*;
import com.edufox.schoolerp.student.work.bulk_admission.*;
import com.edufox.schoolerp.student.work.class_allotment.*;
import com.edufox.schoolerp.student.work.online_registration.*;
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

public final class StudentAdmissionDtos {
    private StudentAdmissionDtos() {}
    public record AdmissionRequest(
        String admissionNo, String feeBookNo, String className, Integer sectionId,
        String session, String admissionDate, String rollNo, String status,
        String concession, String stream, String optionalSubject
    ) {}
    public record AdmissionResponse(String uid, Integer studentId, String admissionNo, String message) {}
}
