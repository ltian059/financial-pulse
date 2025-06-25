package com.fp.util;

import com.fp.auth.JwtPropagationFilter;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientFactory {

    public static WebClient create(String baseUrl, boolean enableJwtPropagation) {
        if(enableJwtPropagation) {
            return createWithJwtPropagation(baseUrl);
        }
        return create(baseUrl);
    }

    public static WebClient create(String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    private static WebClient createWithJwtPropagation(String baseUrl){
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(new JwtPropagationFilter())
                .build();
    }
}
