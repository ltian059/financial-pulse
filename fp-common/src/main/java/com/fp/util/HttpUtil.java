package com.fp.util;

import com.fp.constant.UrlConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

public class HttpUtil {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    public static boolean isLocalhostRequest(HttpServletRequest request) {
        String remoteAddr = getClientIpAddress(request);
        String serverName = request.getServerName();

        return "127.0.0.1".equals(remoteAddr) ||
                "0:0:0:0:0:0:0:1".equals(remoteAddr) ||
                "::1".equals(remoteAddr) ||
                "localhost".equals(serverName) ||
                "localhost".equals(remoteAddr);
    }

    public static boolean isPublicPath(String uri){
        return Arrays.stream(UrlConstant.PUBLIC_PATHS)
                .anyMatch(pattern -> antPathMatcher.match(pattern, uri));
    }

    public static boolean isVerificationTokenPath(String uri) {
        return Arrays.stream(UrlConstant.VERIFY_TOKEN_ONLY_PATHS)
                .anyMatch(pattern -> antPathMatcher.match(pattern, uri));
    }

    public static boolean isRefreshTokenPath(String uri) {
        return Arrays.stream(UrlConstant.REFRESH_TOKEN_ONLY_PATHS)
                .anyMatch(pattern -> antPathMatcher.match(pattern, uri));
    }
}
