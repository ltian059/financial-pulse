package com.fp.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.fp.entity.Comment;
import com.fp.entity.CommentReaction;
import com.fp.entity.Post;
import com.fp.entity.PostReaction;
import com.fp.entity.PostReaction.Type;
import com.fp.entity.PostView;

import jakarta.persistence.EntityManager;

/**
 * Focused tests for persistence behaviors such as cascades and constraints.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryCascadeTests {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentReactionRepository commentReactionRepository;
    @Autowired
    private PostReactionRepository postReactionRepository;
    @Autowired
    private PostViewRepository postViewRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private Flyway flyway;

    @BeforeEach
    void clearDatabase() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void save_and_findById_shouldReturnSameContent() {
        var post = Post.builder()
                .content("<p>persist me</p>")
                .accountId("acc-2")
                .build();
        Post saved = postRepository.save(post);

        Post found = postRepository.findById(saved.getId()).orElseThrow();
        assertEquals(saved.getContent(), found.getContent());
        assertEquals(saved.getAccountId(), found.getAccountId());
    }

    @Test
    void save_withoutContent_shouldFail() {
        var post = Post.builder()
                .accountId("acc-3")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> postRepository.saveAndFlush(post));
    }

    @Test
    void deletePost_shouldCascadeDependents() {
        Post post = postRepository.save(
                Post.builder().content("<p>cascade</p>").accountId("acc-4").build()
        );

        Comment comment = commentRepository.save(
                Comment.builder()
                        .content("c1")
                        .postId(post.getId())
                        .accountId("acc-c1")
                        .build()
        );

        commentReactionRepository.save(
                CommentReaction.builder()
                        .commentId(comment.getId())
                        .accountId("acc-r1")
                        .build()
        );

        postReactionRepository.save(
                PostReaction.builder()
                        .postId(post.getId())
                        .accountId("acc-rp")
                        .type(Type.LIKE)
                        .build()
        );

        postViewRepository.save(
                PostView.builder()
                        .postId(post.getId())
                        .accountId("acc-v1")
                        .clientHash("client-1")
                        .build()
        );

        postRepository.delete(post);
        postRepository.flush();

        assertEquals(0, commentRepository.count());
        assertEquals(0, commentReactionRepository.count());
        assertEquals(0, postReactionRepository.count());
        assertEquals(0, postViewRepository.count());
    }

    @Test
    void deleteParentComment_shouldNullOutChildParentId() {
        Post post = postRepository.save(
                Post.builder().content("<p>cascade comment</p>").accountId("acc-5").build()
        );

        Comment parent = commentRepository.save(
                Comment.builder()
                        .content("parent")
                        .postId(post.getId())
                        .accountId("acc-parent")
                        .build()
        );

        Comment child = commentRepository.save(
                Comment.builder()
                        .content("child")
                        .postId(post.getId())
                        .accountId("acc-child")
                        .parentCommentId(parent.getId())
                        .build()
        );
        assertNotNull(child.getParentCommentId());
        commentRepository.delete(parent);
        commentRepository.flush();
        entityManager.clear(); // ensure reload from DB

        Comment reloadedChild = commentRepository.findById(child.getId()).orElseThrow();
        assertNotNull(reloadedChild);
        assertNull(reloadedChild.getParentCommentId());
    }
}
