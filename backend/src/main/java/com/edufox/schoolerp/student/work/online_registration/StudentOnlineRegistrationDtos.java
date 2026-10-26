package com.edufox.schoolerp.student.work.online_registration;
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
import com.edufox.schoolerp.student.work.registration_redirection.*;
import com.edufox.schoolerp.student.work.student_admission.*;
import com.edufox.schoolerp.student.work.student_registration.*;
import com.edufox.schoolerp.student.work.student_work.*;

import java.util.Map;

public final class StudentOnlineRegistrationDtos {
    private StudentOnlineRegistrationDtos() { }

    public record ConfirmRequest(String onlineRegNo, String registrationNo, String secId,
                                 String admissionDate, String quota) { }

    public record StatusRequest(String registrationNo, String status) { }

    public record PaymentRequest(String onlineRegNo, String orderId, String receiptNo,
                                 String paymentDate, String paymentAmount, String paymentMode) { }

    public record Result(String message, Map<String, Object> row) { }
}
