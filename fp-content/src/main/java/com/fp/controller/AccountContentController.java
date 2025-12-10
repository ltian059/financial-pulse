package com.fp.controller;

import com.fp.dto.common.PageResponseDTO;
import com.fp.dto.content.PostResponseDTO;
import com.fp.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/content/accounts")
@Tag(name = "API for Account Content Management")
@RequiredArgsConstructor
public class AccountContentController {
    private final PostService postService;

    /**
     * Get the specific account profile's posts.
     * The return content depends on the current user's status:
     * <ul>
     *     <li>if the <code>jwtContext.accountId == accountId</code>, then return all posts regardless of post status</li>
     *     <li>else then return all the posts with status <code>active</code></li>
     * </ul>
     *
     * @param accountId The unique identifier of the account.
     * @return A paginated list of posts created by the specified account.
     */
    @GetMapping("/{accountId}/posts")
    public ResponseEntity<PageResponseDTO<PostResponseDTO>> getAccountPosts(@PathVariable String accountId) {
        PageResponseDTO<PostResponseDTO> resp = postService.getAccountPosts(accountId);
        return ResponseEntity.ok(resp);
    }
}
