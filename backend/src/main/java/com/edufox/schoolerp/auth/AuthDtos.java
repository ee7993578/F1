package com.edufox.schoolerp.auth;
import jakarta.validation.constraints.*;
public final class AuthDtos{private AuthDtos(){}
 public record LoginRequest(@NotBlank String identifier,@NotBlank String password,@NotBlank @Pattern(regexp="(?i)faculty|student|parent") String role){}
 public record User(String schoolId,String schoolName,String campusId,String username,String staffUid,String imagePath,String logoPath,String colorScheme,String typeStatus,String dashboard,String designation){}
 public record LoginResponse(String accessToken,String tokenType,long expiresIn,User user,boolean requiresOtp,String message){}
 public record MeResponse(User user){} public record ErrorResponse(String code,String message){}
}