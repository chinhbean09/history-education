package com.blueteam.historyEdu.controllers;

import com.blueteam.historyEdu.components.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor

public class UserController {
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/generate-secret-key")
    public ResponseEntity<?> generateSecretKey() {
        return ResponseEntity.ok(jwtTokenUtils.generateSecretKey());
    }

}
