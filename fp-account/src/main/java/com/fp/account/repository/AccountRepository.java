package com.fp.account.repository;

import com.fp.account.entity.Account;
import com.fp.common.service.DynamoDbRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AccountRepository extends DynamoDbRepository<Account>{


}
