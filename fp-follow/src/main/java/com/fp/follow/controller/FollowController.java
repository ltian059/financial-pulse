package com.fp.follow.controller;


import com.fp.common.constant.Messages;
import com.fp.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Long> getFollowerCountByAccountId(@RequestParam Long accountId) {
        Long followerCount = followService.getFollowerCount(accountId);
        return ResponseEntity.ok(followerCount);
    }


    @PostMapping
    @Operation(summary = "Follow an account")
    public ResponseEntity<?> followAccount(
            @RequestParam Long followerId,
            @RequestParam Long followeeId
    ){
        followService.followAccount(followerId, followeeId);
        return ResponseEntity.ok(null);
    }
}
