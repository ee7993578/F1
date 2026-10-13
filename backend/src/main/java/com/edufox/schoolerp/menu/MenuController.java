package com.edufox.schoolerp.menu;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService service;
    @GetMapping
    public MenuDtos.MenuResponse menu(Authentication authentication) {
        if (!(authentication.getDetails() instanceof Claims claims)) throw new IllegalStateException("Authenticated claims are missing");
        return service.getMenu(claims.get("schoolId", String.class), claims.get("staffUid", String.class), claims.get("role", String.class));
    }
}