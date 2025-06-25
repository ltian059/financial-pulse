package com.fp.util;

import jakarta.servlet.http.HttpServletRequest;

public class HttpUtil {
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
}
