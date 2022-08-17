package com.example.redditclone.controller;

import com.example.redditclone.model.AuthenticationResponse;
import com.example.redditclone.model.LoginRequest;
import com.example.redditclone.model.RefreshTokenRequest;
import com.example.redditclone.model.RegisterRequest;
import com.example.redditclone.service.AuthService;
import com.example.redditclone.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/signUp")
    public ResponseEntity signUp(@RequestBody RegisterRequest registerRequest) {
        authService.signUp(registerRequest);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/verifyAccount")
    public ResponseEntity<String> verifyAccount(@RequestParam(required = true) String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return new ResponseEntity<>(authService.refreshToken(refreshTokenRequest), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken(), refreshTokenRequest.getUsername());
        return new ResponseEntity<>("Refresh Token deleted successfully.", HttpStatus.OK);
    }


}
