package com.fp.account;

import com.fp.auth.strategy.AccessTokenValidationStrategy;
import com.fp.auth.strategy.core.JwtValidationResult;
import com.fp.enumeration.jwt.JwtType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ACCESS令牌验证策略测试
 */
class AccessTokenValidationStrategyTest {

    private AccessTokenValidationStrategy strategy;

    @Mock
    private Jwt mockJwt;

    @BeforeEach
    void setUp() {
        strategy = new AccessTokenValidationStrategy();
    }

    @Test
    void shouldSupportAccessTokenType() {
        assertTrue(strategy.supportsJwtType(JwtType.ACCESS));
        assertFalse(strategy.supportsJwtType(JwtType.REFRESH));
        assertFalse(strategy.supportsJwtType(JwtType.VERIFICATION));
    }

    @Test
    void shouldValidateAccessTokenForApiEndpoints() {
        JwtValidationResult result = strategy.validateJwt(mockJwt, "/api/account/profile");
        assertTrue(result.isValid());
    }

    @Test
    void shouldRejectAccessTokenForRefreshEndpoints() {
        JwtValidationResult result = strategy.validateJwt(mockJwt, "/api/auth/refresh");
        assertFalse(result.isValid());
        assertEquals(HttpStatus.FORBIDDEN, result.getCode());
    }
}