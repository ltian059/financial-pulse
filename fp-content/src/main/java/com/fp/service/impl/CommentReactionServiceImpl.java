package com.fp.service.impl;

import com.fp.entity.CommentReaction;
import com.fp.repository.CommentReactionRepository;
import com.fp.repository.CommentRepository;
import com.fp.service.CommentReactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentReactionServiceImpl implements CommentReactionService {
    private final CommentReactionRepository commentReactionRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void saveCommentReaction(Long commentId, String accountId){
        commentReactionRepository.save(
                CommentReaction.builder()
                        .accountId(accountId)
                        .commentId(commentId)
                        .build()
        );
        commentRepository.incrementLikeCount(commentId);
    }
}
