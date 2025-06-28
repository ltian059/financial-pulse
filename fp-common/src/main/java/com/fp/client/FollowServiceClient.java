package com.fp.client;

import com.fp.dto.follow.request.FollowRequestDTO;
import com.fp.enumeration.api.FollowServiceAPI;
import com.fp.util.ServiceExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


///
/// # Client for the Follow Service
///
@Slf4j
@Component
@ConditionalOnProperty(name = "services.follow.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class FollowServiceClient {
    private final WebClient followWebClient;


    //TODO SQS handle asynchronously the get follower count request; SQS or Kafka can be used for this purpose
    public Long getFollowerCountById(String accountId) {
        try {
            return followWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(FollowServiceAPI.GET_FOLLOWER_COUNT_BY_ACCOUNT_ID.getPath())
                            .queryParam("accountId", accountId)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(Long.class)
                    .block(); // Blocking call to get the follower count
        } catch (WebClientResponseException e) {
            throw ServiceExceptionHandler.handleFollowServiceWebClientException(e);
        }
    }


    public void follow(FollowRequestDTO followRequestDTO) {
        try {
            //TODO Asynchronously handle the follow request; SQS or Kafka can be used for this purpose

            //TODO SQS handle dead letter queue for failed follow requests
            followWebClient.method(FollowServiceAPI.FOLLOW_ACCOUNT.getMethod())
                    .uri(uriBuilder -> uriBuilder
                            .path(FollowServiceAPI.FOLLOW_ACCOUNT.getPath())
                            .build()
                    )
                    .bodyValue(followRequestDTO)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw ServiceExceptionHandler.handleFollowServiceWebClientException(e);
        }
    }

}
