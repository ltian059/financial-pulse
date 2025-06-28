package com.fp.controller;


import com.fp.dto.follow.request.FollowRequestDTO;
import com.fp.dto.follow.request.UnfollowRequestDTO;
import com.fp.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(null);
    }


    //TODO TEST THIS ENDPOINT
    @DeleteMapping
    @Operation(summary = "Unfollow an account")
    public ResponseEntity<?> unfollow(@RequestBody UnfollowRequestDTO unfollowRequestDTO){
        followService.unfollow(unfollowRequestDTO);
        return ResponseEntity.ok("Unfollowed successfully");
    }


    //TODO TEST THIS ENDPOINT
    @GetMapping("/followers")
    @Operation(summary = "Get a list of followers of an account")
    public ResponseEntity<List<String>> listFollower(@RequestParam String accountId){
        return ResponseEntity.ok(followService.listFollower(accountId));
    }

    //TODO TEST THIS ENDPOINT
    @GetMapping("/following")
    @Operation(summary = "Get a list of accounts that a user is following")
    public ResponseEntity<List<String>> getFollowing(@RequestParam String accountId){
        return ResponseEntity.ok(followService.listFollowing(accountId));
    }

    //TODO get a list of accounts that a use is following with pagination


    //TODO get a list of followers of an account with pagination

}
