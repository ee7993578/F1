package com.edufox.schoolerp.student;

import java.util.List;

public final class ClassAllotmentDtos {
    private ClassAllotmentDtos() {}

    public record ClassAllotmentRequest(Integer sectionId, String session, List<String> studentUids) {}
    public record ClassAllotmentResponse(int updated, String message) {}
}
