package com.fp.service;

import com.fp.entity.Account;

///
/// Email service interface for account-related emails.
public interface SesService {

    void sendVerificationEmail(Account account);
}
