package com.fp.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.common.constant.Messages;
import com.fp.common.dto.auth.AuthResponseDTO;
import com.fp.common.util.UnauthorizedAuthClassifier;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;



/**
 *
 */
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    /**
     * Commences an authentication scheme.
     * <p>
     * <code>ExceptionTranslationFilter</code> will populate the <code>HttpSession</code>
     * attribute named
     * <code>AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY</code>
     * with the requested target URL before calling this method.
     * <p>
     * Implementations should modify the headers on the <code>ServletResponse</code> as
     * necessary to commence the authentication process.
     *
     * @param request       that resulted in an <code>AuthenticationException</code>
     * @param response      so that the user agent can begin authentication
     * @param authException that caused the invocation
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = request.getRemoteAddr();
        String authHeader = request.getHeader("Authorization");

        if(log.isDebugEnabled()){
            log.debug("🔴 Authentication failed: {} {} from {} - {}",
                    method, requestURI, remoteAddr, authException.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var authenticationErrorInfo = UnauthorizedAuthClassifier.classifyError(authException, authHeader);

        AuthResponseDTO authResponse = AuthResponseDTO.unauthorized(
                requestURI,
                Messages.Error.Auth.unauthorized(authException.getMessage()),
                authenticationErrorInfo
        );

        objectMapper.writeValue(response.getWriter(), authResponse);

    }
}
