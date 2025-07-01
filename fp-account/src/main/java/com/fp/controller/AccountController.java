package com.fp.controller;

import com.fp.annotation.RevokeJwt;
import com.fp.dto.account.request.*;
import com.fp.dto.account.response.AccountResponseDTO;
import com.fp.dto.follow.request.FollowRequestDTO;
import com.fp.entity.Account;
import com.fp.service.AccountService;
import com.fp.constant.Messages;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "API for Account Management")
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/by-email")
    @Operation(summary = "Get account by email")
    public ResponseEntity<AccountResponseDTO> getAccountByEmail(@RequestParam String email) {
        AccountResponseDTO accountByEmail = accountService.getAccountByEmail(email);
        return ResponseEntity.ok(accountByEmail);
    }


    @GetMapping("/count-follower")
    @Operation(summary = "Get the number of followers for an account")
    @RevokeJwt(value = RevokeJwt.RevokeTokenAfter.ANY)
    public ResponseEntity<Long> getFollowerCountById(@RequestParam String accountId) {
        Long followerCount = accountService.getFollowerCountById(accountId);
        return ResponseEntity.ok(followerCount);
    }

    @PostMapping("/follow")
    @Operation(summary = "Follow another account")
    public ResponseEntity<String > follow(@RequestBody FollowRequestDTO followRequestDTO){
        accountService.follow(followRequestDTO);
        return ResponseEntity.ok(Messages.Success.Follow.FOLLOWED_SUCCESSFULLY);
    }



    @DeleteMapping
    @Operation(summary = "Delete account by email")
    @RevokeJwt(reason = "Account deleted")
    public ResponseEntity<?> deleteAccountByEmail(@RequestBody DeleteAccountRequestDTO deleteAccountDTO) {
        Account account = accountService.deleteAccountByEmail(deleteAccountDTO);
        return ResponseEntity.ok(Messages.Success.Account.DELETED + account.getEmail());
    }

    @PostMapping("/send-verification-email")
    @Operation(summary = "Send verification email to the currently logged in account")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody AccountVerifyRequestDTO dto) {
        accountService.sendVerificationEmail(dto);
        return ResponseEntity.ok(Messages.Success.Account.VERIFICATION_EMAIL_SENT);
    }

    @PatchMapping("/set-verification-status")
    @Operation(summary = "Set account verification status ONLY FOR TESTING PURPOSES")
    public ResponseEntity<String> updateVerificationStatus(@RequestBody AccountUpdateStatusRequestDTO requestDTO) {
        accountService.updateVerificationStatus(requestDTO.getEmail(),  requestDTO.getVerified());
        return ResponseEntity.ok("Verification status updated successfully");
    }

    @PostMapping("logout")
    @Operation(summary = "Logout the currently logged in account")
    @RevokeJwt(reason = "User logged out", value = RevokeJwt.RevokeTokenAfter.SUCCESS)
    public ResponseEntity<?> logout(@RequestBody AccountLogoutRequestDTO accountLogoutDTO) {
        accountService.logout();
        return ResponseEntity.ok("Logout successful");
    }


    @PutMapping("/update-birthday")
    @Operation(summary = "Set the birthday of the currently logged in account")
    public ResponseEntity<?> updateBirthday(@RequestBody UpdateBirthdayRequestDTO birthdayRequestDTO) {
        accountService.updateBirthday(birthdayRequestDTO);
        return  ResponseEntity.ok("Birthday updated successfully");
    }
}
