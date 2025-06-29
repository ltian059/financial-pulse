package com.fp.account;

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

    @Mock
    private Jwt mockJwt;

    @BeforeEach
    void setUpAccessMockJwt(){
        // 配置 mock 对象的行为
        when(mockJwt.getClaimAsString("type")).thenReturn(JwtType.ACCESS.getType());
        when(mockJwt.getSubject()).thenReturn("test@example.com");
    }

    @Test
    void shouldValidateAccessTokenForApiEndpoints() {


        JwtValidationResult result = validationContext.validateJwtType(mockJwt, "/api/account/profile", JwtValidationRequest.ValidationLevel.TYPE_ONLY);
        assertTrue(result.isValid());
    }

    @Test
    void shouldRejectAccessTokenForRefreshEndpoints() {
        JwtValidationResult result = validationContext.validateJwtType(mockJwt, "/api/auth/refresh", JwtValidationRequest.ValidationLevel.TYPE_ONLY);
        assertFalse(result.isValid());
        assertEquals(HttpStatus.FORBIDDEN, result.getCode());
    }
}