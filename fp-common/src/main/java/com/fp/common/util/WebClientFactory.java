package com.fp.common.util;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

public class WebClientFactory {
    public static WebClient create(String baseUrl){
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
