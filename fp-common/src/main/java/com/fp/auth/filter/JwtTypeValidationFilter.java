package com.fp.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.auth.strategy.JwtValidationContext;
import com.fp.auth.strategy.JwtValidationRequest;
import com.fp.auth.strategy.JwtValidationResult;
import com.fp.constant.Messages;
import com.fp.dto.auth.response.AuthResponseDTO;
import com.fp.util.UnauthorizedAuthClassifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

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
@Slf4j
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
            // Strategy to validate JWT type based on request URI
            JwtValidationResult jwtValidationResult = jwtValidationContext.executeValidationStrategy(
                    JwtValidationRequest.builder()
                            .jwt(jwt)
                            .requestURI(requestURI)
                            .build()
            );
            if (!jwtValidationResult.isValid()){
                handleInvalidTokenError(response, jwtValidationResult,requestURI);
                return;
            }
        }

        filterChain.doFilter(request, response);

    }

    private void handleInvalidTokenError(HttpServletResponse response, JwtValidationResult validationResult, String requestURI) throws IOException {
        if (response.isCommitted()) {
            log.warn("Cannot write error response - response already committed for URI: {}", requestURI);
            return;
        }
        try {
            response.setStatus(validationResult.getStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            var resp = AuthResponseDTO.builder()
                    .status(validationResult.getStatus())
                    .requestPath(requestURI)
                    .timestamp(Instant.now())
                    .statusCode(validationResult.getStatus().value())
                    .message(validationResult.getMessage())
                    .build();
            // Use getOutputStream() instead of getWriter() to avoid conflicts
            String valueAsString = objectMapper.writeValueAsString(resp);
            response.getOutputStream().write(valueAsString.getBytes("UTF-8"));
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("Error writing JWT validation error response for URI: {}", requestURI, e);
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                String fallbackResponse = "{\"error\":\"Authentication failed\"}";
                response.getOutputStream().write(fallbackResponse.getBytes("UTF-8"));
                response.getOutputStream().flush();
            }
        }

    }


}
