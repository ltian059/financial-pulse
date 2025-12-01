package com.fp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.fp.entity.Post;
import com.fp.repository.PostRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTests {
    @Autowired
    private PostRepository postRepository;

    // @Autowired
    // private Flyway flyway;

    // @BeforeEach
    // void initDb() { 
    //     flyway.migrate();
    // }

    /**
     * Verify default values and auto-generated fields are populated.
     */
    @Test
    void save_shouldFillDefaults() {
        var post = Post.builder()
                .content("<p>hi</p>")
                .accountId("acc-1")
                .build();

        Post save = postRepository.save(post);
        assertNotNull(save);
        assertNotNull(save.getId());
        assertNotNull(save.getCreatedAt());
        assertEquals(0L, save.getLikeCount());
        assertEquals(Post.Status.ACTIVE, save.getStatus());
    }
    
    @Test
    void update_shouldSetModifiedAt() {
        var post = Post.builder()
                .content("<p>hi</p>")
                .accountId("acc-1")
                .build();
        Post save = postRepository.save(post);
        save.setContent("<p>updated</p>");
        save.setModifiedAt(Instant.now());
        Post updated = postRepository.save(save);
        assertNotNull(updated.getModifiedAt());
    }

    /**
     * Basic round-trip: save then findById should return same payload.
     */
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

    /**
     * Required field validation should bubble up as a data integrity violation.
     */
    @Test
    void save_withoutContent_shouldFail() {
        var post = Post.builder()
                .accountId("acc-3")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> postRepository.saveAndFlush(post));
    }

    /**
     * When updating, the original columns that are not changed should remain the same.
     */
    @Test
    void update_shouldNotChangeUnmodifiedColumns() {
        var post = Post.builder()
                .content("<p>hi</p>")
                .accountId("acc-1")
                .likeCount(5L)
                .build();
        Post save = postRepository.save(post);
        Instant createdAt = save.getCreatedAt();
        Long likeCount = save.getLikeCount();
        Post.Status status = save.getStatus();

        save.setContent("<p>updated</p>");
        save.setModifiedAt(Instant.now());
        Post updated = postRepository.save(save);

        assertEquals(createdAt, updated.getCreatedAt());
        assertEquals(likeCount, updated.getLikeCount());
        assertEquals(status, updated.getStatus());
    }


}
