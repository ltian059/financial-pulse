package com.fp.service;


import com.fp.dto.account.AccountVerifyRequestDTO;
import com.fp.dto.account.DeleteAccountRequestDTO;
import com.fp.dto.account.FollowAccountRequestDTO;
import com.fp.entity.Account;

public interface AccountService {
    // Define methods for account management

    Long getFollowerCountById(String accountId);

    void followAccount(FollowAccountRequestDTO followAccountRequestDTO);


    Account deleteAccountByEmail(DeleteAccountRequestDTO deleteAccountRequestDTO);


    void sendVerificationEmail(AccountVerifyRequestDTO verifyRequestDTO);

    void setVerificationStatus(String email, boolean status);

    Account getAccountByEmail(String email);

}
