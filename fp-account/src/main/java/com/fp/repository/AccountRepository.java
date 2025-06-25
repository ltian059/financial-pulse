package com.fp.repository;

import com.fp.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AccountRepository extends DynamoDbRepository<Account>{
}
