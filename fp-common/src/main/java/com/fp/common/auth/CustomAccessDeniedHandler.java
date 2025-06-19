package com.fp.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.common.dto.auth.AuthResponseDTO;
import com.fp.common.util.HttpUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    /**
     * Handles an access denied failure.
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     * @throws IOException      in the event of an IOException
     * @throws ServletException in the event of a ServletException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = HttpUtil.getClientIpAddress(request);

        log.warn("🔴 Access denied: {} {} from {} - {}",
                method, requestURI, remoteAddr, accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var authResponse = AuthResponseDTO.forbidden(
                requestURI,
                "Access Denied: " + accessDeniedException.getMessage()
        );
        String jsonResponse = objectMapper.writeValueAsString(authResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
