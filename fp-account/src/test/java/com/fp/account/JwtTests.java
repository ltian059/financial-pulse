package com.fp.account;

import com.fp.common.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;

@SpringBootTest
@Slf4j
public class JwtTests {

    @Autowired
    private JwtService jwtService;

    @Test
    public void testJwtTypeGeneration(){
        String accessToken = jwtService.generateVerifyToken("12s", "testverf");

        Jwt jwt = jwtService.validateToken(accessToken);
        log.info("jwt: {}", jwt);
    }
}
