package com.fp.account.service;


import com.fp.account.entity.Account;
import com.fp.common.dto.account.AccountLoginDTO;
import com.fp.common.dto.account.CreateAccountDTO;
import com.fp.common.vo.account.AccountLoginVO;
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

    AccountLoginVO login(AccountLoginDTO accountLoginDTO);

    RefreshTokenVO validateRefreshToken(String refreshToken);
}
