package com.fp.service;

import com.fp.dto.follow.request.FollowRequestDTO;
import com.fp.dto.follow.request.UnfollowRequestDTO;

import java.util.List;

public interface FollowService {

    Long getFollowerCount(String accountId);

    void follow(FollowRequestDTO followRequestDTO);

    void unfollow(UnfollowRequestDTO unfollowRequestDTO);

    List<String> listFollower(String accountId);

    List<String> listFollowing(String accountId);
}
