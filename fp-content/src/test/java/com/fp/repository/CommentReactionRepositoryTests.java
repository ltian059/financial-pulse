package com.fp.repository;

import com.fp.entity.Comment;
import com.fp.entity.CommentReaction;
import com.fp.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentReactionRepositoryTests {
    @Autowired
    private CommentReactionRepository commentReactionRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    // Test insertions
    @Test
    public void save_shouldPersist(){
        Post post = postRepository.saveAndFlush(
                Post.builder()
                        .content("Test Content")
                        .accountId("acc-1")
                        .build()
        );
        Comment comment = commentRepository.saveAndFlush(
                Comment.builder()
                        .content("Test Comment")
                        .accountId("acc-1")
                        .postId(post.getId())
                        .build()
        );
        CommentReaction commentReaction = commentReactionRepository.saveAndFlush(
                CommentReaction.builder()
                        .commentId(comment.getId())
                        .accountId("acc-2")
                        .build()
        );
        assertNotNull(commentReaction);
        assertEquals(commentReaction.getCommentId(), comment.getId());
    }

    // Test duplication prevention
    @Test
    public void save_duplicateShouldFail(){
        Post post = postRepository.saveAndFlush(
                Post.builder()
                        .content("Test Content")
                        .accountId("acc-1")
                        .build()
        );
        Comment comment = commentRepository.saveAndFlush(
                Comment.builder()
                        .content("Test Comment")
                        .accountId("acc-1")
                        .postId(post.getId())
                        .build()
        );
        commentReactionRepository.saveAndFlush(
                CommentReaction.builder()
                        .commentId(comment.getId())
                        .accountId("acc-2")
                        .build()
        );
        assertThrows(DataIntegrityViolationException.class, () -> {
            commentReactionRepository.saveAndFlush(
                    CommentReaction.builder()
                            .commentId(comment.getId())
                            .accountId("acc-2")
                            .build()
            );
        });
    }

    // Test deletion
    @Test
    public void delete_shouldRemove(){
        Post post = postRepository.saveAndFlush(
                Post.builder()
                        .content("Test Content")
                        .accountId("acc-1")
                        .build()
        );
        Comment comment = commentRepository.saveAndFlush(
                Comment.builder()
                        .content("Test Comment")
                        .accountId("acc-1")
                        .postId(post.getId())
                        .build()
        );
        CommentReaction commentReaction = commentReactionRepository.saveAndFlush(
                CommentReaction.builder()
                        .commentId(comment.getId())
                        .accountId("acc-2")
                        .build()
        );
        commentReactionRepository.delete(commentReaction);
        assertFalse(commentReactionRepository.findById(commentReaction.getId()).isPresent());
    }
}
