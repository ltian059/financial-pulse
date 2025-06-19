package com.fp.common.auth;

import com.fp.common.constant.JwtClaimsConstant;
import com.fp.common.constant.UrlConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.fp.common.util.HttpUtil.getClientIpAddress;

@Slf4j
public class AuthenticationLoggingFilter extends OncePerRequestFilter {
    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = getClientIpAddress(request);

        if(isAllowedUrl(requestURI)){
            log.info("🟢 Public endpoint accessed: {} {} from {}", method, requestURI, remoteAddr);
            filterChain.doFilter(request, response);
            return;
        }

        //Not an allowed URL, proceed with authentication logging
        filterChain.doFilter(request, response);

        var auth  = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated()){
            if(auth instanceof JwtAuthenticationToken jwtAuth){
                Jwt jwt = jwtAuth.getToken();
                String email = jwt.getSubject();
                String name = jwt.getClaimAsString(JwtClaimsConstant.NAME);
                Long accountId = Long.valueOf(jwt.getClaimAsString(JwtClaimsConstant.ACCOUNT_ID));

                log.info("🟢 JWT authentication successful: {} {} from {} - User: {}(ID: {}, Email: {})",
                        method, requestURI, remoteAddr, name, accountId, email);
            }else{
                log.info("🟢 Authentication successful: {} {} from {} - Principal: {}",
                        method, requestURI, remoteAddr, auth.getName());
            }
        }

    }

    private boolean isAllowedUrl(String requestURI){
        return Arrays.stream(UrlConstant.ALLOWED_REQUEST_URLS)
                .anyMatch(requestURI::startsWith);
    }
}
