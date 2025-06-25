package com.fp.account.service;

import com.fp.account.entity.Account;

///
/// Email service interface for account-related emails.
public interface SesService {

    void sendVerificationEmail(Account account);
}
