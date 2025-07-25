package com.fp.enumeration.api;

import org.springframework.http.HttpMethod;

public enum FollowServiceAPI {
    // Define the API endpoints for follow management
    /**
     * Endpoint to follow an account
     * @apiNote Implementation is in FollowController#getFollowerCountByAccountId(Long)
     */
    GET_FOLLOWER_COUNT_BY_ACCOUNT_ID("/api/follow/count-follower", HttpMethod.GET),

    /**
     * @Description: Endpoint to follow an account
     * @apiNote Implementation is in FollowController#followAccount(Long, Long)
     */
    FOLLOW_ACCOUNT("/api/follow", HttpMethod.POST);

    private final String path;
    private final HttpMethod method;

    FollowServiceAPI(String path, HttpMethod method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }
}
