package com.edufox.schoolerp.auth;
public record AuthUser(String schoolId,String schoolName,String campusId,String username,String staffUid,String imagePath,String logoPath,String colorScheme,String typeStatus,String dashboard,String designation,String identifierType,boolean otpRequired){
 public AuthDtos.User toDto(){return new AuthDtos.User(schoolId,schoolName,campusId,username,staffUid,imagePath,logoPath,colorScheme,typeStatus,dashboard,designation);}
}