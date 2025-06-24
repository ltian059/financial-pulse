package com.fp.account.controller;

import com.fp.account.entity.Account;
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
import java.util.Optional;

@Tag(name = "API for Account Management")
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/by-email")
    @Operation(summary = "Get account by email")
    public ResponseEntity<?> getAccountByEmail(@RequestParam String email) {
        Account accountByEmail = accountService.getAccountByEmail(email);
        return new ResponseEntity<>(accountByEmail, HttpStatus.OK);
    }
    
    @GetMapping
    @Operation(summary = "Get account by ID")
    public ResponseEntity<?> getAccountById(@RequestParam String accountId) {
        var account = accountService.getAccountById(accountId);
        AccountVO accountVO = new AccountVO();
        BeanUtils.copyProperties(account, accountVO);
        return ResponseEntity.ok(accountVO);

    }


    @GetMapping("/count-follower")
    @Operation(summary = "Get the number of followers for an account")
    public ResponseEntity<Long> getFollowerCountById(@RequestParam String accountId) {
        Long followerCount = accountService.getFollowerCountById(accountId);
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

    @PutMapping("/update-verification")
    @Operation(summary = "Update account verification status")
    public ResponseEntity<?> updateVerificationStatus(@RequestParam boolean verified) {
        accountService.updateVerificationStatus(verified);
        return ResponseEntity.ok("Verification status updated successfully");
    }

    @DeleteMapping
    @Operation(summary = "Delete account by email")
    public ResponseEntity<?> deleteAccountByEmail() {
        Account account = accountService.deleteAccountByEmail();
        return ResponseEntity.ok("Account deleted successfully: " + account.getEmail());
    }
}
