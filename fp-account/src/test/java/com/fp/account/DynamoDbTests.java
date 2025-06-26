package com.fp.account;

import com.fp.entity.Account;
import com.fp.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class DynamoDbTests {

    @Autowired
    private AccountRepository accountRepository;
    
    @Test
    public void testAccountDynamo(){
        Optional<Account> account = accountRepository.findByAccountId("96ba8168-ccee-4138-8c87-e2874b55b1e9");
        if (account.isPresent()) {
            log.info("Account found: {}", account.get());
        } else {
            log.info("Account not found");
        }
    }

}
