package com.fp.repository;

import com.fp.entity.Post;
import com.fp.entity.PostReaction;
import com.fp.service.PostReactionService;
import com.fp.service.impl.PostReactionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.AssertionErrors;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@DataJpaTest
@Slf4j
@Import({PostReactionServiceImpl.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PostReactionConcurrencyTests {

    @Autowired
    private PostReactionService postReactionService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private Flyway flyway;

    @BeforeEach
    void cleanDb(){
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void concurrentLikes_shouldIncrementLikeCountCorrectly() throws InterruptedException {
        Post post = Post.builder()
                .accountId("acc-1")
                .content("Test post for concurrency")
                .build();
        Post savedPost = postRepository.save(post);
        long initialLikeCount = savedPost.getLikeCount();
        int tasks = 500; // 10 thousand likes
        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(tasks);

        for (int i = 0; i < tasks; i++) {
            final int index = i;
            pool.submit(() -> {
               try {
                   start.await(); // wait for the starting signal
                   // Each thread uses a unique accountId to avoid duplicate reaction issues
                   postReactionService.savePostReaction(savedPost.getId(), "acc-" + index, PostReaction.Type.LIKE);
               }catch (Exception e){
                   log.error(e.getMessage(),e);
               }finally {
                   done.countDown();
               }
            });
        }
        // Start all threads
        start.countDown();
        done.await(); // Wait for all to finish
        pool.shutdown();
        // Verify final like count
        Post updated  = postRepository.findById(savedPost.getId()).orElseThrow();
        Assertions.assertEquals(initialLikeCount + tasks, updated.getLikeCount());
    }
    @Test
    void concurrentLikes_shouldHaveRaceCondition() throws InterruptedException {
        Post post = Post.builder()
                .accountId("acc-1")
                .content("Test post for concurrency")
                .build();
        Post savedPost = postRepository.save(post);
        long initialLikeCount = savedPost.getLikeCount();
        int tasks = 200; // 200 likes
        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(tasks);
        for (int i = 0; i < tasks; i++) {
            final int index = i;
            pool.submit(() -> {
                try {
                    start.await(); // wait for the starting signal
                    // Each thread uses a unique accountId to avoid duplicate reaction issues
                    postReactionService.savePostReaction(savedPost.getId(), "acc-" + index, PostReaction.Type.LIKE);
                    Post post1 = postRepository.findById(savedPost.getId()).orElseThrow();
                    post1.setLikeCount(post1.getLikeCount() + 1);
                    postRepository.save(post1);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }finally {
                    done.countDown();
                }
            });
        }
        // Start all threads
        start.countDown();
        done.await(); // Wait for all to finish
        pool.shutdown();
        Long likeCount = postRepository.findById(savedPost.getId()).orElseThrow().getLikeCount();
        log.info("Final like count: {}", likeCount);
        AssertionErrors.assertNotEquals("should not equal",initialLikeCount + tasks, likeCount);
    }
}
