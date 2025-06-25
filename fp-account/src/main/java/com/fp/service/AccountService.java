package com.fp.service;


import com.fp.entity.Account;
import com.fp.dto.auth.LoginDTO;
import com.fp.dto.auth.CreateAccountDTO;
import com.fp.vo.auth.LoginVO;
import com.fp.vo.auth.RefreshTokenVO;

public interface AccountService {
    // Define methods for account management

    void createAccount(CreateAccountDTO accountVO);

    Account getAccountByEmail(String email);

    Account getAccountById(String id);

    Long getFollowerCountById(String accountId);

    void followAccount(Long accountId, Long followeeId);

    LoginVO login(LoginDTO loginDTO);

    RefreshTokenVO validateRefreshToken(String refreshToken);

    Account deleteAccountByEmail();

    void verifyAccountEmail(String verifyToken);
}
