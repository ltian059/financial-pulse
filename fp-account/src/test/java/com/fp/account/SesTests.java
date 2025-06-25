package com.fp.account;

import com.fp.account.entity.Account;
import com.fp.account.properties.SesProperties;
import com.fp.account.service.SesService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class SesTests {
    @Autowired
    private SesProperties sesProperties;

    @Autowired
    private SesService sesService;

    @Test
    public void test(){
        sesService.sendVerificationEmail(Account.builder().accountId("s1234").email("tianli0927@gmail.com").name("Li Tian").build());
    }
}
