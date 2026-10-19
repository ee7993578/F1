package com.edufox.schoolerp.student;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class StudentMutationController {
    private final StudentMutationService service;

    @PutMapping("/{uid}")
    public StudentMutationDtos.MutationResponse update(
            @PathVariable String uid,
            @RequestBody StudentMutationDtos.UpdateRequest request,
            Authentication authentication
    ) {
        Claims claims = (Claims) authentication.getDetails();
        return service.update(String.valueOf(claims.get("schoolId")), uid, request);
    }

    @DeleteMapping("/{uid}")
    public StudentMutationDtos.MutationResponse delete(@PathVariable String uid, Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return service.delete(String.valueOf(claims.get("schoolId")), uid);
    }

    @PostMapping("/withdraw")
    public StudentMutationDtos.MutationResponse updateWithdrawnReason(
            @RequestBody StudentMutationDtos.WithdrawReasonRequest request,
            Authentication authentication) {
        Claims claims = (Claims) authentication.getDetails();
        return service.updateWithdrawnReason(String.valueOf(claims.get("schoolId")), request);
    }
}
