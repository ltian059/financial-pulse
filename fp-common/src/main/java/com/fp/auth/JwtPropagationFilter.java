package com.fp.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * Use WebClient Filter to add JWT token to any outgoing request automatically.
 */
public class JwtPropagationFilter implements ExchangeFilterFunction {

    /**
     * Apply this filter to the given request and exchange function.
     * <p>The given {@link ExchangeFunction} represents the next entity in the
     * chain, to be invoked via {@link ExchangeFunction#exchange} in order to
     * proceed with the exchange, or not invoked to short-circuit the chain.
     * <p><strong>Note:</strong> When a filter handles the response after the
     * call to {@code ExchangeFunction.exchange(...)}, extra care must be taken
     * to always consume its content or otherwise propagate it downstream for
     * further handling, for example by the {@link org.springframework.web.reactive.function.client.WebClient}. Please see the
     * reference documentation for more details on this.
     *
     * @param request the current request
     * @param next    the next exchange function in the chain
     * @return the filtered response
     */
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        String authHeader = getAuthorizationHeader();
        if(authHeader != null){
            ClientRequest filteredReq = ClientRequest.from(request)
                    .header("Authorization", authHeader)
                    .build();

            return next.exchange(filteredReq);
        }
        return next.exchange(request);
    }

    public String getJwtToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken jwtAuth){
            Jwt jwt = jwtAuth.getToken();
            return jwt.getTokenValue();
        }
        return null;
    }

    public String getAuthorizationHeader(){
        String jwtToken = getJwtToken();
        return jwtToken != null ?
                "Bearer " + jwtToken
                : null;
    }

}
