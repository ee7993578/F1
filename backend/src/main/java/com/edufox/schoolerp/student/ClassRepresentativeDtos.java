package com.edufox.schoolerp.student;

import java.util.List;

public final class ClassRepresentativeDtos {
    private ClassRepresentativeDtos() {}
    public record Assignment(Integer classId,Integer facultyId) {}
    public record SaveRequest(String session,List<Assignment> assignments) {}
}