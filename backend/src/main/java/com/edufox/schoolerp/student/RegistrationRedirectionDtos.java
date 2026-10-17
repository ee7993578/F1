package com.edufox.schoolerp.student;

public final class RegistrationRedirectionDtos {
    private RegistrationRedirectionDtos() {}
    public record Request(String session, String phone) {}
    public record Response(String url, String formNo, String message) {}
}
