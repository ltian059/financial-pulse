package com.fp.account;

import com.fp.entity.Account;
import com.fp.properties.SesProperties;
import com.fp.service.SesService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class SesTests {

    @Autowired
    private SesService sesService;

    @Test
    public void test(){
        sesService.sendVerificationEmail(Account.builder().accountId("s1234").email("tianli0927@gmail.com").name("Li Tian").build());
    }
}
