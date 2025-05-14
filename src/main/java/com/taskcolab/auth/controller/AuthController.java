package com.taskcolab.auth.controller;



import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskcolab.auth.dto.LoginRequest;
import com.taskcolab.auth.dto.RefreshToken;
import com.taskcolab.auth.service.AuthService;
import com.taskcolab.user.dto.UserDTO;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test")
    public String test() {
        return "Public endpoint is working!";
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        authService.register(userDTO);
        return ResponseEntity.ok("Utilisateur enregistré avec succès");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest login){

        Map<String, String> tokens = authService.login(login.getEmail(), login.getPassword());
        return ResponseEntity.ok(tokens);

    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody RefreshToken refreshToken){
        Map<String, String> token = authService.refreshToken(refreshToken.getRefreshToken());
        return ResponseEntity.ok(token);
    }


}
