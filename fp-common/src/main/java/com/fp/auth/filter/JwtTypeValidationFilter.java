package com.fp.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.auth.strategy.core.JwtValidationContext;
import com.fp.auth.strategy.core.JwtValidationResult;
import com.fp.constant.Messages;
import com.fp.constant.UrlConstant;
import com.fp.dto.auth.response.AuthResponseDTO;
import com.fp.util.UnauthorizedAuthClassifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.fp.util.HttpUtil.isPublicPath;

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
    private final JwtValidationContext jwtValidationContext;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if(isPublicPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken jwtAuth){
            Jwt jwt = jwtAuth.getToken();
            JwtValidationResult jwtValidationResult = jwtValidationContext.validateJwt(jwt, requestURI);
            if (!jwtValidationResult.isValid()){
                handleInvalidTokenTypeError(response, requestURI);
            }
        }

        filterChain.doFilter(request, response);

    }

    private void handleInvalidTokenTypeError(HttpServletResponse response, String requestURI) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var authResp = AuthResponseDTO.unauthorized(requestURI, Messages.Error.Auth.INVALID_TOKEN_TYPE,
                UnauthorizedAuthClassifier.createErrorInfo(UnauthorizedAuthClassifier.ErrorType.INVALID_TOKEN_TYPE, true));

        objectMapper.writeValue(response.getWriter(), authResp);
    }


}
