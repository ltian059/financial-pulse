package com.fp.util;

import org.springframework.web.reactive.function.client.WebClient;

public class WebClientFactory {
    public static WebClient create(String baseUrl){
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
