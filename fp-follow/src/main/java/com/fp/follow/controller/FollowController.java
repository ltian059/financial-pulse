package com.fp.follow.controller;


import com.fp.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "API for Follow Management")
@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @GetMapping("/countFollowers")
    @Operation(summary = "get the number of followers for an account")
    public Long getFollowerCountByAccountId(@RequestParam Long accountId) {
        return followService.getFollowerCount(accountId);
    }
}
