package com.fp.common.auth;

import com.fp.common.constant.JwtPropertiesConstant;
import com.fp.common.properties.JwtProperties;
import com.fp.common.util.JwtPropagationUtil;
import com.fp.common.util.JwtUtil;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * Use WebClient Filter to add JWT token to any outgoing request automatically.
 */
public class JwtTokenPropagationFilter implements ExchangeFilterFunction {

    /**
     * Apply this filter to the given request and exchange function.
     * <p>The given {@link ExchangeFunction} represents the next entity in the
     * chain, to be invoked via {@link ExchangeFunction#exchange} in order to
     * proceed with the exchange, or not invoked to short-circuit the chain.
     * <p><strong>Note:</strong> When a filter handles the response after the
     * call to {@code ExchangeFunction.exchange(...)}, extra care must be taken
     * to always consume its content or otherwise propagate it downstream for
     * further handling, for example by the {@link WebClient}. Please see the
     * reference documentation for more details on this.
     *
     * @param request the current request
     * @param next    the next exchange function in the chain
     * @return the filtered response
     */
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        String authHeader = JwtPropagationUtil.getAuthorizationHeader();
        if(authHeader != null){
            ClientRequest filteredReq = ClientRequest.from(request)
                    .header(JwtPropertiesConstant.JWT_ACCESS_TOKEN_HEADER_NAME, authHeader)
                    .build();

            return next.exchange(filteredReq);
        }
        return next.exchange(request);
    }

}
