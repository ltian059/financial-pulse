package com.fp.account.service;


import com.fp.account.entity.Account;
import com.fp.common.dto.auth.LoginDTO;
import com.fp.common.dto.auth.CreateAccountDTO;
import com.fp.common.vo.auth.LoginVO;
import com.fp.common.vo.auth.RefreshTokenVO;

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
