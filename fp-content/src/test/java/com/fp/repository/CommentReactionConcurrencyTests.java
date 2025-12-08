package com.fp.repository;

import com.fp.entity.Post;
import com.fp.service.CommentReactionService;
import com.fp.service.impl.CommentReactionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@DataJpaTest
@Slf4j
@Import({CommentReactionServiceImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentReactionConcurrencyTests {

    @Autowired
    private CommentReactionService commentReactionService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private Flyway flyway;
    @BeforeEach
    void setup(){
        // Any necessary setup before each test
        flyway.clean();
        flyway.migrate();
    }
    // Test: save multiple comment reactions concurrently should increment reaction count correctly

    /**
     *
     * <li>By default, @DataJpaTest runs each test method within a transaction that is rolled back after the test. </li>
     * <li>To accurately test concurrent behavior, we need to disable this transactional behavior for the test method </li>
     * <li>If not doing so, the concurrent threads may not see the committed state of the database, leading to inconsistent results.</li>
     * <li>the @Transactional(propagation = Propagation.NOT_SUPPORTED) annotation is used to disable transaction management for this test method, resulting in each operation being committed immediately.</li>
     * @throws InterruptedException
     */
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void concurrentReactions_shouldIncrementReactionCountCorrectly() throws InterruptedException {
        // Implementation would be similar to PostReactionConcurrencyTests
        // Create a post and a comment
        Post post = postRepository.saveAndFlush(
                Post.builder()
                        .accountId("acc-1")
                        .content("Test post for concurrency")
                        .build()
        );
        var comment = commentRepository.saveAndFlush(
                com.fp.entity.Comment.builder()
                        .postId(post.getId())
                        .accountId("acc-2")
                        .content("Test comment for concurrency")
                        .build()
        );
        long initialReactionCount = comment.getLikeCount();
        // Use ExecutorService and CountDownLatch to simulate concurrent reactions
        ExecutorService pool = Executors.newFixedThreadPool(10);
        int tasks = 500; // Number of concurrent reactions
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(tasks);
        for (int i = 0; i < tasks; i++) {
            final int index = i;
            pool.submit(() -> {
                try {
                    // Each thread uses a unique accountId to avoid duplicate reaction issues
                    start.await(); // wait for the starting signal
                    commentReactionService.saveCommentReaction(comment.getId(), "acc-" + index);
                } catch (Exception e) {
                    // Handle exceptions if necessary
                    log.error(e.getMessage());
                }finally {
                    done.countDown();
                }
            });
        }
        start.countDown(); // Signal all threads to start
        done.await();
        pool.shutdown();
        // Verify the final reaction count on the comment
        var updatedComment = commentRepository.findById(comment.getId()).orElseThrow();
        Assertions.assertEquals(initialReactionCount + tasks, updatedComment.getLikeCount());
    }
}
