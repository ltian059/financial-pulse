package com.fp.service;

import com.fp.dto.auth.request.CreateAccountRequestDTO;
import com.fp.dto.auth.request.LoginRequestDTO;
import com.fp.dto.auth.response.LoginResponseDTO;
import com.fp.dto.auth.response.RefreshTokenResponseDTO;

public interface AuthService {
    void createAccount(CreateAccountRequestDTO accountVO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    RefreshTokenResponseDTO validateRefreshToken(String refreshToken);

    String verifyAccountEmail(String verifyToken);

}
