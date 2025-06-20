package com.fp.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.common.constant.JwtClaimsConstant;
import com.fp.common.constant.Messages;
import com.fp.common.constant.UrlConstant;
import com.fp.common.dto.auth.AuthResponseDTO;
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
public class JwtTokenTypeValidationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        if(isPublicPath(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        var context = SecurityContextHolder.getContext();

        var authentication = context.getAuthentication();

        if(authentication instanceof JwtAuthenticationToken jwtAuth){
            Jwt jwt = jwtAuth.getToken();
            String type = jwt.getClaimAsString(JwtClaimsConstant.TYPE);
            //Even though the token is valid, the type doesn't match the uri it is trying to access.
            if("refresh".equals(type) && !isRefreshTokenPath(requestURI)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                var authResp = AuthResponseDTO.forbidden(requestURI, Messages.Error.Auth.INVALID_TOKEN_TYPE);
                objectMapper.writeValue(response.getWriter(), authResp);
                return;
            }
        }


        filterChain.doFilter(request, response);

    }
    private boolean isPublicPath(String uri){
        return Arrays.stream(UrlConstant.PUBLIC_PATHS)
                .anyMatch(uri::startsWith);
    }
    private boolean isRefreshTokenPath(String uri) {
        return Arrays.stream(UrlConstant.REFRESH_TOKEN_ONLY_PATHS)
                .anyMatch(uri::startsWith);
    }
}
