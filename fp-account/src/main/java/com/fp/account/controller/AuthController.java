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
import org.springframework.web.bind.annotation.*;

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
        var refreshTokenVO = accountService.validateRefreshToken(refreshTokenDTO.getRefreshToken());
        return ResponseEntity.ok(refreshTokenVO);
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify account email")
    public ResponseEntity<?> verifyAccountEmail(@RequestParam String token){
        accountService.verifyAccountEmail(token);
        String htmlResponse = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Email Verified - Financial Pulse</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        min-height: 100vh;
                        margin: 0;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    }
                    .container {
                        background: white;
                        padding: 40px;
                        border-radius: 10px;
                        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
                        text-align: center;
                        max-width: 500px;
                    }
                    .success-icon {
                        font-size: 60px;
                        color: #28a745;
                        margin-bottom: 20px;
                    }
                    h1 {
                        color: #333;
                        margin-bottom: 20px;
                    }
                    p {
                        color: #666;
                        margin-bottom: 30px;
                        line-height: 1.6;
                    }
                    .btn {
                        display: inline-block;
                        padding: 12px 30px;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        text-decoration: none;
                        border-radius: 25px;
                        font-weight: bold;
                    }
                    .btn:hover {
                        opacity: 0.9;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="success-icon">✅</div>
                    <h1>Email Verified Successfully!</h1>
                    <p>
                        Congratulations! Your email address has been verified. 
                        Your Financial Pulse account is now fully activated and ready to use.
                    </p>
                    <a href="http://localhost:3000/login" class="btn">Continue to Login</a>
                </div>
            </body>
            </html>
            """;
        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(htmlResponse);
    }


}
