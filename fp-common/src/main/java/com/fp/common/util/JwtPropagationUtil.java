package com.fp.common.util;

import com.fp.common.constant.JwtPropertiesConstant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class JwtPropagationUtil {
    public static String getJwtToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken jwtAuth){
            Jwt jwt = jwtAuth.getToken();
            return jwt.getTokenValue();
        }
        return null;
    }

    public static String getAuthorizationHeader(){
        String jwtToken = getJwtToken();
        return jwtToken != null ?
                JwtPropertiesConstant.JWT_PREFIX + jwtToken
                : null;
    }
}
