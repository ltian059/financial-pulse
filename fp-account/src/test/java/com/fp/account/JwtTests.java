package com.fp.account;

import com.fp.service.JwtService;
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

    @Test
    public void testRevokeJwt(){
        String testToken = jwtService.generateVerifyToken("12s", "li@sas.com");
        log.info("testToken: {}", testToken);
        Jwt jwt = jwtService.validateToken(testToken);
    }
    @Test
    public void testIsRevokedJwt(){
        String revokedToken = """
                eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsaUBzYXMuY29tIiwiYXVkIjoiZmluYW5jaWFsLXB1bHNlLXZlcmlmeSIsImFjY291bnRJZCI6IjEycyIsImlzcyI6ImZpbmFuY2lhbC1wdWxzZS1pc3N1ZXIiLCJleHAiOjE3NTA4OTQxNTYsInR5cGUiOiJWRVJJRlkiLCJpYXQiOjE3NTA4OTA1NTYsImp0aSI6ImMyMTA0ZDE3LWFlNDEtNDhjMy1iNTExLTg5NjBlZmEwMjI1ZCJ9.38a4XHcOnVWYi4TybnMxrgd7gDZQ9HNyPf7VgfIvCho
                """;
        jwtService.validateToken(revokedToken);
    }
}
