package com.fp.service;

import com.fp.dto.auth.CreateAccountDTO;
import com.fp.dto.auth.LoginDTO;
import com.fp.vo.auth.LoginVO;
import com.fp.vo.auth.RefreshTokenVO;

public interface AuthService {
    void createAccount(CreateAccountDTO accountVO);

    LoginVO login(LoginDTO loginDTO);

    RefreshTokenVO validateRefreshToken(String refreshToken);

    String verifyAccountEmail(String verifyToken);

}
