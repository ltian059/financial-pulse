package com.fp.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.constant.JwtClaimsKey;
import com.fp.constant.Messages;
import com.fp.constant.UrlConstant;
import com.fp.dto.auth.response.AuthResponseDTO;
import com.fp.enumeration.jwt.JwtType;
import com.fp.util.UnauthorizedAuthClassifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/// Filter to validate JWT token type for different endpoints.
///
/// e.g `Access token` is only used for API access.
/// Refresh token is only used for refreshing access tokens.
/// This filter stops requests with invalid token types from proceeding further.
///
/// Effective after BearerTokenAuthenticationFilter
///
@RequiredArgsConstructor
public class JwtTypeValidationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if(isPublicPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        var context = SecurityContextHolder.getContext();

        var authentication = context.getAuthentication();

        if(authentication instanceof JwtAuthenticationToken jwtAuth){
            Jwt jwt = jwtAuth.getToken();
            String typeString = jwt.getClaimAsString(JwtClaimsKey.TYPE);
            JwtType type = JwtType.fromString(typeString);
            //Even though the token is valid, the type doesn't match the uri it is trying to access.
            if(JwtType.REFRESH.equals(type) && !isRefreshTokenPath(requestURI)) {
                handleInvalidTokenTypeError(response, requestURI);
                return;
            }

            if(JwtType.VERIFY.equals(type) && !isVerifyTokenPath(requestURI)){
                handleInvalidTokenTypeError(response, requestURI);
                return;
            }
        }

        filterChain.doFilter(request, response);

    }

    private void handleInvalidTokenTypeError(HttpServletResponse response, String requestURI) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var authResp = AuthResponseDTO.unauthorized(requestURI, Messages.Error.Auth.INVALID_TOKEN_TYPE,
                UnauthorizedAuthClassifier.createErrorInfo(UnauthorizedAuthClassifier.ErrorType.INVALID_TOKEN_TYPE, true));

        objectMapper.writeValue(response.getWriter(), authResp);
    }

    private boolean isPublicPath(String uri){
        return Arrays.stream(UrlConstant.PUBLIC_PATHS)
                .anyMatch(uri::startsWith);
    }
    private boolean isRefreshTokenPath(String uri) {
        return Arrays.stream(UrlConstant.REFRESH_TOKEN_ONLY_PATHS)
                .anyMatch(uri::startsWith);
    }
    private boolean isVerifyTokenPath(String uri) {
        return Arrays.stream(UrlConstant.VERIFY_TOKEN_ONLY_PATHS)
                .anyMatch(uri::startsWith);
    }

}
