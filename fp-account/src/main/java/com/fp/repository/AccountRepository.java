package com.fp.repository;

import com.fp.dynamodb.repository.DynamoDbRepository;
import com.fp.entity.Account;
import com.fp.exception.business.AccountNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
@Slf4j
public class AccountRepository extends DynamoDbRepository<Account> {

    public Account findByEmail(String email) {
        Key key = Key.builder().partitionValue(email).build();
        Optional<Account> byKey = findByKey(key);
        if (byKey.isEmpty()){
            throw new AccountNotFoundException("Account not found for email: " + email);
        } else {
            return byKey.get();
        }
    }

    public Account deleteAccount(String accountId, String email) {
        //Delete Account by ID and email
        Key key = Key.builder()
                .partitionValue(email)
                .build();
        //First, get the account by email.
        Optional<Account> accountByEmail = findByKey(key);
        if (accountByEmail.isEmpty()) {
            throw new AccountNotFoundException("Account not found for email: " + email);
        }
        //Check if the account ID matches
        Account account = accountByEmail.get();
        if(!account.getAccountId().equals(accountId)){
            throw new AccountNotFoundException("Account ID does not match for email: " + email);
        }
        //Delete the account
        Optional<Account> deleteByKey = deleteByKey(key);
        if (deleteByKey.isEmpty()) {
            throw new AccountNotFoundException("Account not found for email: " + email);
        }
        log.info("Account deleted successfully for email: {}", email);
        return deleteByKey.get();
    }


    public Optional<Account> findByAccountId(String accountId) {
        var queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(accountId).build());
        Stream<Page<Account>> pageStream = queryIndex("account-id-index", queryConditional);
        //Get the first result
        return pageStream
                .flatMap(page -> page.items().stream())
                .findFirst();
    }
}
