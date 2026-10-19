package com.edufox.schoolerp.student;

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
