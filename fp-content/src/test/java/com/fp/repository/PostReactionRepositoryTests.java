package com.fp.repository;

import com.fp.entity.Post;
import com.fp.entity.PostReaction;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class PostReactionRepositoryTests {

    @Autowired
    private PostReactionRepository postReactionRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private Flyway flyway;

    @BeforeEach
    void clearDatabase() {
        flyway.clean();
        flyway.migrate();
    }
    /**
     * 1. Test Insertion
     */
    @Test
    void save_shouldPersistPostReaction() {
        var post = Post.builder()
                .content("Sample Post</p>")
                .accountId("acc-1")
                .build();
        post = postRepository.saveAndFlush(post);

        var postReaction = PostReaction.builder()
                .postId(post.getId())
                .accountId("acc-3")
                .type(com.fp.entity.PostReaction.Type.LIKE)
                .build();
        PostReaction saved = postReactionRepository.save(postReaction);
        assertNotNull(saved);
        assertNotNull(saved.getPostId());
        assertEquals("acc-3", saved.getAccountId());
        assertEquals(post.getId(), saved.getPostId());
        assertEquals(com.fp.entity.PostReaction.Type.LIKE, saved.getType());
    }

    //1.1 Test data integrity violation on missing fields
    @Test
    void save_missingFields_shouldThrowException() {
        var postReaction = PostReaction.builder()
                .accountId("acc-1")
                .type(com.fp.entity.PostReaction.Type.LIKE)
                .build();
        var ex = assertThrows(DataIntegrityViolationException.class, () -> postReactionRepository.saveAndFlush(postReaction));
        Throwable root = ex.getMostSpecificCause();
        log.info("root is {},", root.getMessage());
    }

    //1.2 Test insertion of different reaction types
    @Test
    void save_differentReactionTypes_shouldPersist() {
        Post post = postRepository.saveAndFlush(
                Post.builder()
                        .content("Sample Post</p>")
                        .accountId("acc-1")
                        .build()
        );
        for (PostReaction.Type type : PostReaction.Type.values()) {
            PostReaction pr = PostReaction.builder()
                    .postId(post.getId())
                    .accountId("acc-1")
                    .type(type)
                    .build();

            //retrieve
            PostReaction saved = postReactionRepository.saveAndFlush(pr);
            assertNotNull(saved);
            assertEquals(type, saved.getType());
            assertEquals("acc-1", saved.getAccountId());
            assertEquals(post.getId(), saved.getPostId());
        }
    }
    //1.3 Test Duplicate Insertion should violate unique constraint
    @Test
    void save_duplicateInsertion_shouldThrowException(){
        Post post = postRepository.saveAndFlush(
                Post.builder()
                        .content("Sample Post</p>")
                        .accountId("acc-1")
                        .build()
        );
        PostReaction pr = PostReaction.builder()
                .postId(post.getId())
                .accountId("acc-2")
                .type(PostReaction.Type.LIKE)
                .build();

        // First insertion
        PostReaction saved = postReactionRepository.saveAndFlush(pr);
        assertNotNull(saved);

        // Duplicate insertion with same composite key
        // Need to recreate the object to avoid JPA caching effects
        PostReaction pr2 = PostReaction.builder()
                .postId(post.getId())
                .accountId("acc-2")
                .type(PostReaction.Type.LIKE)
                .build();
        var ex = assertThrows(DataIntegrityViolationException.class, () -> postReactionRepository.saveAndFlush(pr2));
        Throwable root = ex.getCause();
        log.info("root is {},", root.getMessage());
    }

    //1.4 Test insertion should trigger increment of post's reaction count
    //Updating post's like count should be an atomic operation in real application with service layer
    @Test
    void save_shouldIncrementPostLikeCount() {
        Post post = postRepository.saveAndFlush(
                Post.builder()
                        .content("Sample Post</p>")
                        .accountId("acc-1")
                        .build()
        );
        PostReaction pr = PostReaction.builder()
                .postId(post.getId())
                .accountId("acc-2")
                .type(PostReaction.Type.LIKE)
                .build();
        long initialLikeCount = post.getLikeCount();
        // Insertion
        PostReaction saved = postReactionRepository.saveAndFlush(pr);
        assertNotNull(saved);
        //update post like count atomically(This operation won't update the first layer cache in EntityManager)
        postRepository.incrementLikeCount(post.getId());
        entityManager.clear(); // Clear the persistence context to avoid caching issues, so that we fetch fresh data
        // Retrieve updated post
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(initialLikeCount + 1, updatedPost.getLikeCount());
    }
    //3. Test deletion

    //4. Test Unique Constraints
}
