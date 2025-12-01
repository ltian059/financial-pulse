package com.fp.repository_tests;

import com.fp.entity.Post;
import com.fp.entity.PostView;
import com.fp.repository.PostRepository;
import com.fp.repository.PostViewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostViewTests {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostViewRepository postViewRepository;



    @Test
    void save_shouldPersistPostView() {
        // Test implementation goes here
        Post post = Post.builder()
                .content("Sample Post")
                .accountId("acc-1")
                .build();
        post = postRepository.saveAndFlush(post);
        PostView pv = PostView.builder()
                .postId(post.getId())
                .accountId("acc-2")
                .build();

        PostView postView = postViewRepository.saveAndFlush(pv);
        assertNotNull(postView);
        assertNotNull(postView.getPostId());
        assertNotNull(postView.getAccountId());

        assertEquals(post.getId(), postView.getPostId());
        assertNull(postView.getClientHash());
    }

    @Test
    public void save_shouldPersistClientHash(){
        //Test Client hash saving
        Post post = Post.builder()
                .content("Sample Post")
                .accountId("acc-1")
                .build();
        post = postRepository.saveAndFlush(post);
        PostView pv = PostView.builder()
                .postId(post.getId())
                .clientHash("client-hash-123")
                .build();
        PostView postView = postViewRepository.saveAndFlush(pv);
        assertNotNull(postView);
        assertNotNull(postView.getPostId());
        assertNotNull(postView.getClientHash());
        assertEquals("client-hash-123", postView.getClientHash());

        assertNull(postView.getAccountId());
    }

    //Test duplicate post view actions
    @Test
    public void save_duplicateAccountViewsShouldThrowException(){
        Post post1 = Post.builder()
                .content("Sample Post")
                .accountId("acc-1")
                .build();
        final Post saved = postRepository.saveAndFlush(post1);
        PostView postView = postViewRepository.saveAndFlush(PostView.builder()
                .postId(saved.getId())
                .accountId("acc-2")
                .build()
        );
        assertNotNull(postView);

        assertThrows(DataIntegrityViolationException.class, () -> {
            postViewRepository.saveAndFlush(
                    PostView.builder()
                            .postId(saved.getId())
                            .accountId("acc-2")
                            .build()
            );
        });
    }

    @Test
    public void save_duplicationClientHashViewsShouldThrowException(){
        Post post1 = Post.builder()
                .content("Sample Post")
                .accountId("acc-1")
                .build();
        final Post saved = postRepository.saveAndFlush(post1);
        //Client hash duplicate
        PostView postView2 = postViewRepository.saveAndFlush(
                PostView.builder()
                        .clientHash("client-hash-123")
                        .postId(saved.getId())
                        .build()
        );
        assertNotNull(postView2);
        assertThrows(DataIntegrityViolationException.class, () -> {
            postViewRepository.saveAndFlush(
                    PostView.builder()
                            .postId(saved.getId())
                            .clientHash("client-hash-123")
                            .build()
            );
        });

    }

}
