package com.fp.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.constant.Messages;
import com.fp.dto.auth.AuthResponseDTO;
import com.fp.util.HttpUtil;
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
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = HttpUtil.getClientIpAddress(request);
        String authHeader = request.getHeader("Authorization");
        if(log.isDebugEnabled()){
            log.debug("🔴 Access denied: {} {} from {} - {}",
                    method, requestURI, remoteAddr, accessDeniedException.getMessage());
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        var authResponse = AuthResponseDTO.forbidden(
                requestURI,
                Messages.Error.Auth.accessDenied(accessDeniedException.getMessage())
        );

        objectMapper.writeValue(response.getWriter(), authResponse);
    }
}
