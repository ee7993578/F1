package com.edufox.schoolerp.student;

public final class ExamConfigurationDtos {
    private ExamConfigurationDtos() {}
    public record ExamRequest(Integer id,String classId,String examDate,String examReportingTime,String examEntryGateNO,
      String examStartTime,String examEndTime,String examSession,String studentType) {}
    public record ExamResponse(Integer id,String message) {}
}