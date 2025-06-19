package com.fp.common.util;

import com.fp.common.auth.JwtTokenPropagationFilter;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

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
                .filter(new JwtTokenPropagationFilter())
                .build();
    }
}
