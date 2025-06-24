package com.fp.account.repository;

import com.fp.account.entity.Account;
import com.fp.common.properties.DynamoDbProperties;
import com.fp.common.service.DynamoDbRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
@Slf4j
public class AccountRepository extends DynamoDbRepository<Account>{
}
