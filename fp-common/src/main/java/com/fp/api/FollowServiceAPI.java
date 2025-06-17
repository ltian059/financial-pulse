package com.fp.api;

import org.springframework.http.HttpMethod;

public enum FollowServiceAPI {
    // Define the API endpoints for follow management
    GET_FOLLOWER_COUNT_BY_ACCOUNT_ID("/api/follow/countFollowers", HttpMethod.GET);

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
