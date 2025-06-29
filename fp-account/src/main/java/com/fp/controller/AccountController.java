package com.fp.controller;

import com.fp.dto.account.request.AccountVerifyRequestDTO;
import com.fp.dto.account.request.DeleteAccountRequestDTO;
import com.fp.dto.account.response.AccountResponseDTO;
import com.fp.dto.follow.request.FollowRequestDTO;
import com.fp.dto.account.request.UpdateBirthdayRequestDTO;
import com.fp.entity.Account;
import com.fp.exception.business.JwtContextException;
import com.fp.service.AccountService;
import com.fp.constant.Messages;
import com.fp.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "API for Account Management")
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
public class AccountController {
    private final AccountService accountService;
    private final JwtService jwtService;

    @GetMapping("/by-email")
    @Operation(summary = "Get account by email")
    public ResponseEntity<AccountResponseDTO> getAccountByEmail(@RequestParam String email) {
        AccountResponseDTO accountByEmail = accountService.getAccountByEmail(email);

        return ResponseEntity.ok(accountByEmail);
    }


    @GetMapping("/count-follower")
    @Operation(summary = "Get the number of followers for an account")
    public ResponseEntity<Long> getFollowerCountById(@RequestParam String accountId) {
        Long followerCount = accountService.getFollowerCountById(accountId);
        return ResponseEntity.ok(followerCount);
    }

    @PostMapping("/follow")
    @Operation(summary = "Follow another account")
    //TODO Use AOP to validate JWT context
    public ResponseEntity<String > follow(@RequestBody FollowRequestDTO followRequestDTO){
        validateJwtContextWithRequest(followRequestDTO.getAccountId(), followRequestDTO.getEmail());

        accountService.follow(followRequestDTO);
        return ResponseEntity.ok(Messages.Success.Follow.FOLLOWED_SUCCESSFULLY);
    }



    @DeleteMapping
    @Operation(summary = "Delete account by email")
    //TODO Use AOP to validate JWT context
    //TODO Use AOP to revoke JWT tokens if successful
    public ResponseEntity<?> deleteAccountByEmail(@RequestBody DeleteAccountRequestDTO deleteAccountDTO) {
        validateJwtContextWithRequest(deleteAccountDTO.getAccountId(), deleteAccountDTO.getEmail());

        Account account = accountService.deleteAccountByEmail(deleteAccountDTO);
        return ResponseEntity.ok(Messages.Success.Account.DELETED + account.getEmail());
    }

    @PostMapping("/send-verification-email")
    @Operation(summary = "Send verification email to the currently logged in account")
    //TODO USE AOP to validate JWT context
    //TODO USE AOP to revoke JWT tokens if successful
    public ResponseEntity<?> sendVerificationEmail(@RequestBody AccountVerifyRequestDTO dto) {
        validateJwtContextWithRequest(dto.getAccountId(), dto.getEmail());
        accountService.sendVerificationEmail(dto);
        return ResponseEntity.ok(Messages.Success.Account.VERIFICATION_EMAIL_SENT);
    }

    @PutMapping("/set-verification-status")
    @Operation(summary = "Set account verification status ONLY FOR TESTING PURPOSES")
    public ResponseEntity<String> updateVerificationStatus(@RequestParam String email, @RequestParam boolean status) {
        accountService.updateVerificationStatus(email, status);
        return ResponseEntity.ok("Verification status updated successfully");
    }

    @PostMapping("logout")
    @Operation(summary = "Logout the currently logged in account")
    //TODO USE AOP to validate JWT context
    //TODO USE AOP to revoke JWT tokens if successful
    public ResponseEntity<?> logout() {
        accountService.logout();
        return ResponseEntity.ok("Logout successful");
    }


    //TODO Use AOP to validate JWT context
    //TODO update the date of birthday of an account
    @PutMapping("/update-birthday")
    @Operation(summary = "Set the birthday of the currently logged in account")
    public ResponseEntity<?> updateBirthday(@RequestBody UpdateBirthdayRequestDTO birthdayRequestDTO) {
        validateJwtContextWithRequest(birthdayRequestDTO.getAccountId(), birthdayRequestDTO.getEmail());
        accountService.updateBirthday(birthdayRequestDTO);
        return  ResponseEntity.ok("Birthday updated successfully");
    }

    /**
     * Validate the JWT context with the request parameters.
     * @param accountIdInRequest the account ID from the request
     * @param emailInRequest the email from the request
     */
    //TODO Use AOP to validate JWT context for POST, PUT, DELETE, and PATCH methods
    private void validateJwtContextWithRequest(String accountIdInRequest, String emailInRequest) {
        //Check if the accountId and email from JWT context is present
        Jwt jwt = jwtService.getJwtFromAuthContext();
        Optional<String> idOpt = jwtService.getAccountIdFromToken(jwt.getTokenValue());
        Optional<String> emailOpt = jwtService.getEmailFromToken(jwt.getTokenValue());
        if(idOpt.isEmpty() || emailOpt.isEmpty()){
            throw new JwtContextException(Messages.Error.Account.JWT_CONTEXT_ERROR);
        }
        //Check if the accountId from JWT matches account info from the method parameter
        String jwtAccountId = idOpt.get();
        String jwtEmail = emailOpt.get();
        if(!jwtAccountId.equals(accountIdInRequest) || !jwtEmail.equals(emailInRequest)){
            throw new JwtContextException(Messages.Error.Account.JWT_CONTEXT_ERROR);
        }
    }
}
