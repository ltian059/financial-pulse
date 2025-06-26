package com.fp.controller;

import com.fp.service.AccountService;
import com.fp.dto.auth.LoginDTO;
import com.fp.dto.auth.CreateAccountDTO;
import com.fp.dto.auth.RefreshTokenDTO;
import com.fp.service.AuthService;
import com.fp.vo.auth.LoginVO;
import com.fp.vo.auth.RefreshTokenVO;
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
    public ResponseEntity<String > createAccount(@RequestBody CreateAccountDTO createAccountDTO){
        authService.createAccount(createAccountDTO);
        return ResponseEntity.ok("Account created successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Account login")
    @ApiResponse(description = "loginVO contains account information and JWT tokens")
    public ResponseEntity<LoginVO> login(@RequestBody LoginDTO loginDTO){
        LoginVO loginVO = authService.login(loginDTO);
        return ResponseEntity.ok(loginVO);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    @ApiResponse(description = "Returns a new access token and optionally a new refresh token")
    public ResponseEntity<RefreshTokenVO> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO){
        var refreshTokenVO = authService.validateRefreshToken(refreshTokenDTO.getRefreshToken());
        return ResponseEntity.ok(refreshTokenVO);
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify account email")
    //TODO USE AOP to revoke the token after successful verification
    public ResponseEntity<?> verifyAccountEmail(@RequestParam String token){
        var htmlResponse = authService.verifyAccountEmail(token);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(htmlResponse);
    }

}
