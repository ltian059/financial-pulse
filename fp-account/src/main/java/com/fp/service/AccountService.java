package com.fp.service;


import com.fp.dto.account.request.AccountVerifyRequestDTO;
import com.fp.dto.account.request.DeleteAccountRequestDTO;
import com.fp.dto.follow.request.FollowRequestDTO;
import com.fp.dto.account.request.UpdateBirthdayRequestDTO;
import com.fp.entity.Account;

public interface AccountService {
    // Define methods for account management

    Long getFollowerCountById(String accountId);

    void follow(FollowRequestDTO followRequestDTO);


    Account deleteAccountByEmail(DeleteAccountRequestDTO deleteAccountRequestDTO);


    void sendVerificationEmail(AccountVerifyRequestDTO verifyRequestDTO);

    void updateVerificationStatus(String email, boolean status);

    Account getAccountByEmail(String email);

    void logout();

    void updateBirthday(UpdateBirthdayRequestDTO birthdayRequestDTO);
}
