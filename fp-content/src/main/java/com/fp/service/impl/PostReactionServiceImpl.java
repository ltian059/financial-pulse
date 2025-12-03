package com.fp.service.impl;

import com.fp.entity.PostReaction;
import com.fp.repository.PostReactionRepository;
import com.fp.repository.PostRepository;
import com.fp.service.PostReactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostReactionServiceImpl implements PostReactionService {
    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;


    @Transactional
    public void savePostReaction(Long postId, String accountId, PostReaction.Type type) {
        switch (type) {
            case LIKE -> saveLikeReaction(postId, accountId);
            case REPOST -> saveRepostReaction(postId, accountId);
            case QUOTE -> saveQuoteReaction(postId, accountId);
        }
    }

    private void saveQuoteReaction(Long postId, String accountId) {
        postReactionRepository.save(PostReaction.builder()
                .type(PostReaction.Type.QUOTE)
                .accountId(accountId)
                .postId(postId)
                .build());
        postRepository.incrementQuoteCount(postId);
    }

    private void saveRepostReaction(Long postId, String accountId) {
        postReactionRepository.save(PostReaction.builder()
                .type(PostReaction.Type.REPOST)
                .accountId(accountId)
                .postId(postId)
                .build());
        postRepository.incrementRepostCount(postId);
    }

    /**
     * Save a like reaction for a post by a specific account.
     * Should also increment the like count of the post.
     * @param postId
     * @param accountId
     */
    private void saveLikeReaction(Long postId, String accountId) {
        PostReaction postReaction = PostReaction.builder()
                .type(PostReaction.Type.LIKE)
                .accountId(accountId)
                .postId(postId)
                .build();
        postReactionRepository.save(postReaction);
        int row = postRepository.incrementLikeCount(postId);
        if(row == 0){
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }
    }

}
