package com.fp.account.controller;

import com.fp.account.entity.Account;
import com.fp.account.service.AccountService;
import com.fp.common.dto.account.AccountLoginDTO;
import com.fp.common.dto.account.CreateAccountDTO;
import com.fp.common.util.JwtUtil;
import com.fp.common.vo.account.AccountLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create-account")
    @Operation(summary = "Create a new account")
    public ResponseEntity<String > createA1ccount(@RequestBody CreateAccountDTO createAccountDTO){
        accountService.createAccount(createAccountDTO);
        return ResponseEntity.ok("Account created successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Account login")
    @ApiResponse(description = "loginVO contains account information and JWT tokens")
    public ResponseEntity<AccountLoginVO> login(@RequestBody AccountLoginDTO accountLoginDTO){
        Account account = accountService.verifyLogin(accountLoginDTO);
        var id = account.getId();
        var email = account.getEmail();
        var name = account.getName();
        //After login successfully, generate JWT token
        String accessToken = jwtUtil.generateAccessToken(id, email, name);
        String refreshToken = jwtUtil.generateRefreshToken(id, email);
        var loginVO = new AccountLoginVO();
        BeanUtils.copyProperties(account, loginVO);
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        log.info("Account login successful: {}", loginVO);
        return ResponseEntity.ok(loginVO);
    }


}
