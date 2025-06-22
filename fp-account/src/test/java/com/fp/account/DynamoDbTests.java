package com.fp.account;

import com.fp.common.entity.TokenBlacklist;
import com.fp.common.service.DynamoDbService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
public class DynamoDbTests {

    @Autowired
    private DynamoDbService dynamoDbService;

    @Test
    public void test1(){

    }

    @Test
    public void testCreateTable(){
        DynamoDbTable<TokenBlacklist> tableIfNotExists = dynamoDbService.createTableIfNotExists(TokenBlacklist.class);
    }

    @Test
    public void testBatchPut(){
        //generate 1000 items
        List<TokenBlacklist> tokenBlacklists = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tokenBlacklists.add(TokenBlacklist.builder()
                            .tokenId(i + "jwi")
                            .reason("test reason " + i)
                            .ttl(Instant.now().plus(Duration.ofMinutes(i)).getEpochSecond())
                            .revokedAt(Instant.now())
                    .build());
        }
        dynamoDbService.batchPutItems(TokenBlacklist.class, tokenBlacklists);
    }

    @Test
    public void testQueryByPartitionKey(){
        Stream<TokenBlacklist> tokenBlacklistStream = dynamoDbService.queryByPartitionKey("5jwi", TokenBlacklist.class);
        tokenBlacklistStream.forEach(tokenBlacklist -> {
            log.info(tokenBlacklist.toString());
        });
    }
    
    @Test
    public void testQueryConditional(){
        dynamoDbService.query(TokenBlacklist.class, QueryConditional.keyEqualTo(k -> k.partitionValue("5jwi")))
                .forEach(tokenBlacklist -> {
                    log.info(tokenBlacklist.toString());
                });
    }

    @Test
    public void testScanAll(){
        Stream<TokenBlacklist> tokenBlacklistStream = dynamoDbService.scanAllItems(TokenBlacklist.class);
        tokenBlacklistStream.forEach(tokenBlacklist -> {
            log.info(tokenBlacklist.toString());
        });
    }

}
