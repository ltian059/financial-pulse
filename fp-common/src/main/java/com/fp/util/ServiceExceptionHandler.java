package com.fp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fp.exception.service.FollowServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientResponseException;

///
/// # Utility class to handle exceptions from the Communications between services using WebClient.
@Slf4j
public class ServiceExceptionHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static FollowServiceException handleFollowServiceWebClientException(WebClientResponseException e){
        try {
            JsonNode errorResponse = objectMapper.readTree(e.getResponseBodyAsString());
            String message = errorResponse.path("message").asText("Follow service error");

            String status = errorResponse.path("status").asText();
            HttpStatusCode httpStatus = determineHttpStatus(status, e.getStatusCode());

            return new FollowServiceException(httpStatus, message);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to parse follow service error response", ex);
            throw new FollowServiceException(
                    e.getStatusCode(),
                    "Follow service communication error",
                    e
            );
        }
    }
    /**
     * Handle exceptions from Content service (future use)
     */
    public static RuntimeException handleContentServiceWebClientException(WebClientResponseException e) {
        // Similar implementation for content service
        return new RuntimeException("Content service error: " + e.getMessage(), e);
    }




    private static HttpStatusCode determineHttpStatus(String status, HttpStatusCode originalStatus) {
        return switch (status){
            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "BAD_REQUEST" -> HttpStatus.BAD_REQUEST;
            case "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED;
            case "FORBIDDEN" -> HttpStatus.FORBIDDEN;
            case "INTERNAL_SERVER_ERROR" -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> originalStatus != null ? originalStatus : HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
