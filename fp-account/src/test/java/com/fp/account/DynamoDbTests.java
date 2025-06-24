package com.fp.account;

import com.fp.account.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class DynamoDbTests {

    @Autowired
    private AccountRepository accountRepository;
    
    @Test
    public void testAccountDynamo(){
        long count = accountRepository.count();
        log.info("Total accounts: {}", count);
    }

}
