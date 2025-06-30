package com.fp.account;

import com.fp.auth.service.JwtService;
import com.fp.auth.strategy.JwtValidationContext;
import com.fp.auth.strategy.JwtValidationRequest;
import com.fp.auth.strategy.JwtValidationResult;
import com.fp.enumeration.jwt.JwtType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * ACCESS令牌验证策略测试
 */
@Slf4j
@SpringBootTest
class AccessTokenValidationStrategyTest {


    @Autowired
    private JwtValidationContext validationContext;

    @Autowired
    private JwtService jwtService;
    @Mock
    private Jwt mockJwt;

    @BeforeEach
    void setUpAccessMockJwt(){
        // 配置 mock 对象的行为
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aWFubGkwOTI3QGdtYWlsLmNvbSIsImF1ZCI6ImZpbmFuY2lhbC1wdWxzZS1hcGkiLCJhY2NvdW50SWQiOiI0ODYwN2UzNC00YjljLTRmMWMtOGRjMy0wMTY0ZTE3MjlmYTciLCJpc3MiOiJmaW5hbmNpYWwtcHVsc2UtaXNzdWVyIiwibmFtZSI6IkxpIFRpYW4iLCJleHAiOjE3NTEzMzgxMDQsInR5cGUiOiJBQ0NFU1MiLCJpYXQiOjE3NTEyNTE3MDQsImp0aSI6ImU0MjhkNWUxLWE5Y2YtNDFlOC05Yzc3LWE0ZmZiNjk0NDM5NiJ9.5eijkNqWy2caWW1od8o0q6aWkiqBEwoYmJA-2qKwBqY";
        mockJwt = jwtService.decode(token);
//        when(mockJwt.getClaimAsString("type")).thenReturn(JwtType.ACCESS.getType());
//        when(mockJwt.getSubject()).thenReturn("test@example.com");
//        when(mockJwt.getId()).thenReturn("40ce9f09-2707-4f53-8287-ab78ec6289cf");
    }

    @Test
    void shouldValidateAccessTokenForApiEndpoints() {

        JwtValidationRequest validationRequest = JwtValidationRequest.builder()
                .jwt(mockJwt)
                .requestURI("/api/account/profile")
                .build();

        JwtValidationResult result = validationContext.executeValidationStrategy(validationRequest);
        assertTrue(result.isValid());
    }

    @Test
    void shouldRejectAccessTokenForRefreshEndpoints() {
        JwtValidationRequest validationRequest = JwtValidationRequest.builder()
                .jwt(mockJwt)
                .requestURI("/api/auth/refresh")
                .build();
        //Should be first validated by RevokedJwtStrategy
        JwtValidationResult result = validationContext.executeValidationStrategy(validationRequest);
        assertFalse(result.isValid());
        assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }
}