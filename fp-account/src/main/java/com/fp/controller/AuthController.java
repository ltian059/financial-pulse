package com.fp.controller;

import com.fp.annotation.RevokeJwt;
import com.fp.dto.auth.request.LoginRequestDTO;
import com.fp.dto.auth.request.CreateAccountRequestDTO;
import com.fp.dto.auth.request.RefreshTokenRequestDTO;
import com.fp.service.AuthService;
import com.fp.dto.auth.response.LoginResponseDTO;
import com.fp.dto.auth.response.RefreshTokenResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/create-account")
    @Operation(summary = "Create a new account")
    public ResponseEntity<String > createAccount(@RequestBody CreateAccountRequestDTO createAccountRequestDTO){
        authService.createAccount(createAccountRequestDTO);
        return ResponseEntity.ok("Account created successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Account login")
    @ApiResponse(description = "loginVO contains account information and JWT tokens")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);
        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    @ApiResponse(description = "Returns a new access token and optionally a new refresh token")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        var refreshTokenVO = authService.validateRefreshToken(refreshTokenRequestDTO.getRefreshToken());
        return ResponseEntity.ok(refreshTokenVO);
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify account email")
    public ResponseEntity<?> verifyAccountEmail(@RevokeJwt @RequestParam String token){
        var htmlResponse = authService.verifyAccountEmail(token);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(htmlResponse);
    }

}
