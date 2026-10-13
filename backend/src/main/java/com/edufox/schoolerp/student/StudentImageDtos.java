package com.edufox.schoolerp.student;

public final class StudentImageDtos {
    private StudentImageDtos() {}
    public record ImageRequest(String imageData, String type) {}
    public record ImageResponse(String uid, String message) {}
}