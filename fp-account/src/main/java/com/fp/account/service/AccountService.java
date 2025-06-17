package com.fp.account.service;


import com.fp.account.entity.Account;
import com.fp.dto.account.AccountDTO;
import com.fp.vo.account.CreateAccountVO;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    // Define methods for account management

    void createAccount(CreateAccountVO accountVO);

    Optional<Account> getAccountByEmail(String email);

    void updateVerificationStatus(Long id, boolean verified);

    Optional<Account> getAccountById(Long id);

    Long getFollowerCountById(Long id);
}
