package com.fp.account;

import com.fp.common.entity.TokenBlacklist;
import com.fp.common.service.DynamoDbService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class DynamoDbTests {

    @Autowired
    private DynamoDbService dynamoDbService;

    @Test
    public void test1(){
        DynamoDbTable<TokenBlacklist> table = dynamoDbService.getTable(TokenBlacklist.class);
        TokenBlacklist tokenBlacklist = new TokenBlacklist("1ss", Instant.now(), Instant.now().minus(Duration.ofHours(2)), "test");
        table.putItem(tokenBlacklist);
    }

}
