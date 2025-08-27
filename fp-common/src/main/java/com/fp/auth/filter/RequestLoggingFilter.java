package com.fp.auth.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest && log.isDebugEnabled()) {
            logRequestDetails(httpRequest);
        }

        chain.doFilter(request, response);
    }

    private void logRequestDetails(HttpServletRequest request) {
        log.debug("======================== REQUEST DETAILS ========================");
        log.debug("Method: {}", request.getMethod());
        log.debug("RequestURI: {}", request.getRequestURI());
        log.debug("ContextPath: {}", request.getContextPath());
        log.debug("ServletPath: {}", request.getServletPath());
        log.debug("PathInfo: {}", request.getPathInfo());
        log.debug("QueryString: {}", request.getQueryString());
        log.debug("RemoteAddr: {}", request.getRemoteAddr());
        log.debug("RequestURL: {}", request.getRequestURL());
        log.debug("Scheme: {}", request.getScheme());

        log.debug("------------------------ HEADERS ------------------------");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.debug("{}: {}", headerName, headerValue);
        }

        // API Gateway检测
        String xAmazonTraceId = request.getHeader("X-Amzn-Trace-Id");
        String userAgent = request.getHeader("User-Agent");
        if (xAmazonTraceId != null || (userAgent != null && userAgent.contains("AmazonAPIGateway"))) {
            log.debug("==================== API GATEWAY DETECTED ====================");
            log.debug("X-Amzn-Trace-Id: {}", xAmazonTraceId);
            log.debug("X-Forwarded-For: {}", request.getHeader("X-Forwarded-For"));
            log.debug("===========================================================");
        }

        log.debug("======================== END REQUEST ========================");
    }
}