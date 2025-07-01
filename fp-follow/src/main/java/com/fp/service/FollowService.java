package com.fp.service;

import com.fp.dto.common.PageResponseDTO;
import com.fp.dto.follow.request.*;
import com.fp.dto.follow.response.FollowResponseDTO;

public interface FollowService {

    Long getFollowerCount(String accountId);

    void follow(FollowRequestDTO followRequestDTO);

    void unfollow(UnfollowRequestDTO unfollowRequestDTO);

    PageResponseDTO<FollowResponseDTO> listFollowers(ListFollowersRequestDTO listFollowersRequestDTO);

    PageResponseDTO<FollowResponseDTO> listFollowings(ListFollowingsRequestDTO listFollowingsRequestDTO);


}
