package com.fp.controller;


import com.fp.dto.common.PageResponseDTO;
import com.fp.dto.follow.request.*;
import com.fp.dto.follow.response.FollowResponseDTO;
import com.fp.service.FollowService;
import com.fp.sqs.impl.MessageFactory;
import com.fp.sqs.service.SqsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "API for Follow Management")
@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;


    @GetMapping("/count-follower")
    @Operation(summary = "get the number of followers for an account")
    public ResponseEntity<Long> getFollowerCountByAccountId(@RequestParam String accountId) {
        Long followerCount = followService.getFollowerCount(accountId);
        return ResponseEntity.ok(followerCount);
    }


    @PostMapping
    @Operation(summary = "Follow an account")
    public ResponseEntity<?> follow(@RequestBody FollowRequestDTO followRequestDTO){
        followService.follow(followRequestDTO);
        return ResponseEntity.ok("Followed successfully");
    }


    @DeleteMapping
    @Operation(summary = "Unfollow an account")
    public ResponseEntity<?> unfollow(@RequestBody UnfollowRequestDTO unfollowRequestDTO){
        followService.unfollow(unfollowRequestDTO);
        return ResponseEntity.ok("Unfollowed successfully");
    }


    @GetMapping("/followers")
    @Operation(summary = "Get a list of followers of an account with cursor pagination")
    public ResponseEntity<PageResponseDTO<FollowResponseDTO>> listFollower(@Valid ListFollowersRequestDTO listFollowersRequestDTO){
        return ResponseEntity.ok(followService.listFollowers(listFollowersRequestDTO));
    }


    @GetMapping("/following")
    @Operation(summary = "Get a list of accounts that a user is following")
    public ResponseEntity<PageResponseDTO<FollowResponseDTO>> listFollowing(@Valid ListFollowingsRequestDTO requestDTO){
        return ResponseEntity.ok(followService.listFollowings(requestDTO));
    }


}
