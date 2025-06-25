package com.fp.account.controller;

import com.fp.account.service.AccountService;
import com.fp.common.dto.auth.AccountVerifyDTO;
import com.fp.common.dto.auth.LoginDTO;
import com.fp.common.dto.auth.CreateAccountDTO;
import com.fp.common.dto.auth.RefreshTokenDTO;
import com.fp.common.vo.auth.LoginVO;
import com.fp.common.vo.auth.RefreshTokenVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AccountService accountService;


    @PostMapping("/create-account")
    @Operation(summary = "Create a new account")
    public ResponseEntity<String > createAccount(@RequestBody CreateAccountDTO createAccountDTO){
        accountService.createAccount(createAccountDTO);
        return ResponseEntity.ok("Account created successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Account login")
    @ApiResponse(description = "loginVO contains account information and JWT tokens")
    public ResponseEntity<LoginVO> login(@RequestBody LoginDTO loginDTO){
        LoginVO loginVO = accountService.login(loginDTO);
        return ResponseEntity.ok(loginVO);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    @ApiResponse(description = "Returns a new access token and optionally a new refresh token")
    public ResponseEntity<RefreshTokenVO> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO){
        try {
            var refreshTokenVO = accountService.validateRefreshToken(refreshTokenDTO.getRefreshToken());
            return ResponseEntity.ok(refreshTokenVO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new RefreshTokenVO(null, null, "Invalid refresh token")
            );
        }
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify account email")
    public ResponseEntity<?> verifyAccountEmail(@RequestBody AccountVerifyDTO accountVerifyDTO){
        //TODO implement email verification logic
        return null;
    }


}
