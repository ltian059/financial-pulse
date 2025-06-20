package com.fp.account.service;


import com.fp.account.entity.Account;
import com.fp.common.dto.auth.LoginDTO;
import com.fp.common.dto.auth.CreateAccountDTO;
import com.fp.common.vo.auth.LoginVO;
import com.fp.common.vo.auth.RefreshTokenVO;

import java.util.Optional;

public interface AccountService {
    // Define methods for account management

    void createAccount(CreateAccountDTO accountVO);

    Optional<Account> getAccountByEmail(String email);

    void updateVerificationStatus(Long id, boolean verified);

    Optional<Account> getAccountById(Long id);

    Long getFollowerCountById(Long id);

    void followAccount(Long accountId, Long followeeId);

    LoginVO login(LoginDTO loginDTO);

    RefreshTokenVO validateRefreshToken(String refreshToken);
}
