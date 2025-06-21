package com.fp.account.controller;

import com.fp.account.service.AccountService;
import com.fp.common.constant.Messages;
import com.fp.common.vo.account.AccountVO;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "API for Account Management")
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;




    @GetMapping
    @Operation(summary = "Get account by ID")
    public ResponseEntity<?> getAccountById(@RequestParam Long id) {
        var accountOptional = accountService.getAccountById(id);
        if(accountOptional.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", Messages.Error.Account.notFoundById(id)));
        }else{
            AccountVO accountVO = new AccountVO();
            BeanUtils.copyProperties(accountOptional.get(), accountVO);
            return ResponseEntity.ok(accountVO);
        }
    }


    @GetMapping("/count-follower")
    @Operation(summary = "Get the number of followers for an account")
    public ResponseEntity<Long> getFollowerCountById(@RequestParam Long id){
        Long followerCount = accountService.getFollowerCountById(id);
        return ResponseEntity.ok(followerCount);
    }


    @PostMapping("/follow")
    @Operation(summary = "Follow another account")
    public ResponseEntity<String > followAccount(
            @RequestParam Long accountId,
            @RequestParam Long followeeId
    ){
        accountService.followAccount(accountId, followeeId);
        return ResponseEntity.ok(Messages.Success.Follow.FOLLOWED_SUCCESSFULLY);
    }
}
