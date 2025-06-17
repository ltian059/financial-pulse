package com.fp.account.controller;

import com.fp.account.service.AccountService;
import com.fp.dto.account.AccountDTO;
import com.fp.vo.account.CreateAccountVO;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "API for Account Management")
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;


    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<String > createAccount(@RequestBody CreateAccountVO accountVO){
        accountService.createAccount(accountVO);
        return ResponseEntity.ok("Account created successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(account -> {
                    AccountDTO accountDTO = new AccountDTO();
                    BeanUtils.copyProperties(account, accountDTO);
                    return ResponseEntity.ok(accountDTO);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
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
        return ResponseEntity.ok("Followed successfully");
    }



}
