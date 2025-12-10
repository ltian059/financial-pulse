package com.fp.service.impl;

import com.fp.auth.service.JwtService;
import com.fp.dto.common.PageResponseDTO;
import com.fp.dto.content.ListPostsRequestDTO;
import com.fp.dto.content.PostResponseDTO;
import com.fp.entity.Post;
import com.fp.repository.PostRepository;
import com.fp.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
@Import({PostServiceImpl.class})
public class PostServiceImplDataTest {
    @Autowired
    private PostService postService;
    @Autowired
    private Flyway flyway;
    @Autowired
    private PostRepository postRepository;

    @MockitoBean
    private JwtService jwtService;


    @BeforeEach
    void cleanDb() {
        // Code to clean the database before each test
        flyway.clean();
        flyway.migrate();
        generateTestPosts();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void listPosts_withEmptyParams_shouldReturnPageResponse(){
        // 2. Call listPosts with empty params
        ListPostsRequestDTO req = ListPostsRequestDTO.builder().build();
        PageResponseDTO<PostResponseDTO> pageResponse = postService.listPosts(req);

        // 3. Assert the response
        assertNotNull(pageResponse);
        assertEquals(20, pageResponse.getData().size()); // default limit is 20
        assertTrue(pageResponse.isHasMore());

    }

    @Test
    public void listPosts_withAccountId_shouldReturnCorrectPageResponse(){
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .accountId("acc-10")
                .build();

        PageResponseDTO<PostResponseDTO> pageResponse = postService.listPosts(req);
        assertNotNull(pageResponse);
        assertEquals(10, pageResponse.getData().size()); // Each account has 10 posts
        for(var dto : pageResponse.getData()){
            assertEquals("acc-10", dto.getAccountId());
        }
    }

    @Test
    public void listPosts_withAccountIdAndHiddenStatus_shouldReturnPage(){
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .accountId("acc-10")
                .status("HIDDEN")
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.listPosts(req);
        assertNotNull(pageResponseDTO);
        // Each account has 2 hidden posts (20% of 10)
        assertEquals(2, pageResponseDTO.getData().size());
        assertFalse(pageResponseDTO.isHasMore());
        for(var dto : pageResponseDTO.getData()){
            assertEquals("acc-10", dto.getAccountId());
            assertEquals("HIDDEN", dto.getStatus());
        }
    }

    @Test
    public void listPosts_withAccountIdAndActiveStatus_shouldReturnPage(){
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .accountId("acc-20")
                .status("ACTIVE")
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.listPosts(req);
        assertNotNull(pageResponseDTO);
        // Each account has 6 active posts (60% of 10)
        assertEquals(6, pageResponseDTO.getData().size());
        assertFalse(pageResponseDTO.isHasMore());
        for(var dto : pageResponseDTO.getData()){
            assertEquals("acc-20", dto.getAccountId());
            assertEquals("ACTIVE", dto.getStatus());
        }
    }

    @Test
    public void listPosts_withAccountIdAndDraftStatus_shouldReturnPage(){
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .accountId("acc-30")
                .status("DRAFT")
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.listPosts(req);
        assertNotNull(pageResponseDTO);
        // Each account has 1 draft post (10% of 10)
        assertEquals(1, pageResponseDTO.getData().size());
        assertFalse(pageResponseDTO.isHasMore());
        for(var dto : pageResponseDTO.getData()){
            assertEquals("acc-30", dto.getAccountId());
            assertEquals("DRAFT", dto.getStatus());
        }
    }
    @Test
    public void listPosts_withAccountIdAndDeletedStatus_shouldReturnPage(){
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .accountId("acc-40")
                .status("DELETED")
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.listPosts(req);
        assertNotNull(pageResponseDTO);
        // Each account has 1 deleted post (10% of 10)
        assertEquals(1, pageResponseDTO.getData().size());
        assertFalse(pageResponseDTO.isHasMore());
        for(var dto : pageResponseDTO.getData()){
            assertEquals("acc-40", dto.getAccountId());
            assertEquals("DELETED", dto.getStatus());
        }
    }

    /**
     * Test desc ordering of any status posts of an account by modifiedAt and createdAt
     */
    @Test
    public void listPosts_withAnyStatus_shouldReturnInDescOrder(){
        // Fetch all posts regardless of status of an account
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .accountId("acc-5")
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.listPosts(req);
        assertNotNull(pageResponseDTO);
        List<PostResponseDTO> data = pageResponseDTO.getData();
        for(int i = 0; i < data.size() - 1; i++){
            var post = data.get(i);
            var nextPost = data.get(i + 1);
            Instant curr = post.getModifiedAt() != null ? post.getModifiedAt() : post.getCreatedAt();
            Instant next = nextPost.getModifiedAt() != null ? nextPost.getModifiedAt() : nextPost.getCreatedAt();
            assert curr.isAfter(next) : "Posts are not in descending order";
            assertEquals("acc-5", post.getAccountId());
        }
    }

    /**
     * Test ordering of all active posts.
     */
    @Test
    public void listPosts_withActiveStatus_shouldReturnInDescOrder(){
        // Fetch all active posts of an account
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .status("ACTIVE")
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.listPosts(req);
        assertNotNull(pageResponseDTO);
        List<PostResponseDTO> data = pageResponseDTO.getData();
        for(int i = 0; i < data.size() - 1; i++){
            var post = data.get(i);
            var nextPost = data.get(i + 1);
            Instant curr = post.getModifiedAt() != null ? post.getModifiedAt() : post.getCreatedAt();
            Instant next = nextPost.getModifiedAt() != null ? nextPost.getModifiedAt() : nextPost.getCreatedAt();
            assert curr.isAfter(next) : "Active posts are not in descending order";
            assertEquals("ACTIVE", post.getStatus());
        }
    }

    /**
     * Test ordering of an account's active status
     */
    @Test
    public void listPosts_withAccountIdAndActiveStatus_shouldReturnInDescOrder(){
        // Fetch all active posts of an account
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .accountId("acc-15")
                .status("ACTIVE")
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.listPosts(req);
        assertNotNull(pageResponseDTO);
        List<PostResponseDTO> data = pageResponseDTO.getData();
        for(int i = 0; i < data.size() - 1; i++){
            var post = data.get(i);
            var nextPost = data.get(i + 1);
            Instant curr = post.getModifiedAt() != null ? post.getModifiedAt() : post.getCreatedAt();
            Instant next = nextPost.getModifiedAt() != null ? nextPost.getModifiedAt() : nextPost.getCreatedAt();
            assert curr.isAfter(next) : "Active posts of account are not in descending order";
            assertEquals("acc-15", post.getAccountId());
            assertEquals("ACTIVE", post.getStatus());
        }
    }

    /**
     * Test ordering of keyword search results
     */
    @Test
    public void listPosts_withKeyword_shouldReturnInDescOrder(){
        // Fetch all posts containing "investment"
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .keyword("investment")
                .status("ACTIVE")
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.listPosts(req);
        assertNotNull(pageResponseDTO);
        List<PostResponseDTO> data = pageResponseDTO.getData();
        for(int i = 0; i < data.size() - 1; i++){
            var post = data.get(i);
            var nextPost = data.get(i + 1);
            Instant curr = post.getModifiedAt() != null ? post.getModifiedAt() : post.getCreatedAt();
            Instant next = nextPost.getModifiedAt() != null ? nextPost.getModifiedAt() : nextPost.getCreatedAt();
            assert curr.isAfter(next) : "Keyword search posts are not in descending order";
            assertEquals("ACTIVE", post.getStatus());
            assertTrue(post.getContent().toLowerCase().contains("investment"));
        }
    }

    /**
     * Test cursor pagination
     */
    @Test
    public void listPosts_withCursorAndHasMore_shouldReturnNextPage(){
        // First page
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .status("ACTIVE")
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.listPosts(req);
        assertNotNull(pageResponseDTO);
        assertEquals(20, pageResponseDTO.getData().size());
        assertTrue(pageResponseDTO.isHasMore());

        // Second page using cursor from first page
        String cursor = pageResponseDTO.getNextCursor();
        ListPostsRequestDTO req2 = ListPostsRequestDTO.builder()
                .status("ACTIVE")
                .cursor(cursor)
                .build();

        PageResponseDTO<PostResponseDTO> pageResponseDTO2 = postService.listPosts(req2);
        assertNotNull(pageResponseDTO2);
        assertEquals(20, pageResponseDTO2.getData().size());
        // Ensure no overlap between pages
        Instant lastPostFirstPage = pageResponseDTO.getData().get(19).getModifiedAt() != null ?
                pageResponseDTO.getData().get(19).getModifiedAt() :
                pageResponseDTO.getData().get(19).getCreatedAt();
        Instant firstPostSecondPage = pageResponseDTO2.getData().get(0).getModifiedAt() != null ?
                pageResponseDTO2.getData().get(0).getModifiedAt() :
                pageResponseDTO2.getData().get(0).getCreatedAt();
        assertTrue(lastPostFirstPage.isAfter(firstPostSecondPage) || lastPostFirstPage.equals(firstPostSecondPage),
                "Pagination overlap detected between pages");
    }

    @Test
    public void listPosts_withCursor_shouldReturnNextPageNotHavingMore(){
        // First page
        ListPostsRequestDTO req = ListPostsRequestDTO.builder()
                .keyword("investment strategy") // 33 posts contain this keyword
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.listPosts(req);
        assertNotNull(pageResponseDTO);
        assertEquals(20, pageResponseDTO.getData().size());
        assertTrue(pageResponseDTO.isHasMore());

        // Second page using cursor from first page
        ListPostsRequestDTO req2 = ListPostsRequestDTO.builder()
                .keyword("investment strategy")
                .cursor(pageResponseDTO.getNextCursor())
                .build();
        PageResponseDTO<PostResponseDTO> pageResponseDTO2 = postService.listPosts(req2);
        assertNotNull(pageResponseDTO2);
        assertEquals(13, pageResponseDTO2.getData().size()); // remaining 13
        assertFalse(pageResponseDTO2.isHasMore());
        String cursor = pageResponseDTO2.getNextCursor();
        assertNull(cursor, "Cursor should be null when there are no more pages");
    }

    @Test
    public void getAccountPosts_withoutLoggedIn_shouldReturnAllActivePosts(){
        String accountId = "acc-25";
        when(jwtService.getAccountIdFromAuthContext()).thenReturn(java.util.Optional.empty());
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.getAccountPosts(accountId);
        assertNotNull(pageResponseDTO);
        verify(jwtService, times(1)).getAccountIdFromAuthContext();
        // Each account has 6 active posts (60% of 10)
        assertEquals(6, pageResponseDTO.getData().size());
        assertFalse(pageResponseDTO.isHasMore());
        for(var dto : pageResponseDTO.getData()){
            assertEquals("acc-25", dto.getAccountId());
            assertEquals("ACTIVE", dto.getStatus());
        }
    }
    @Test
    public void getAccountPosts_LoggedInAndViewOthers_shouldReturnAllActivePosts(){
        String accountId = "acc-30";
        when(jwtService.getAccountIdFromAuthContext()).thenReturn(java.util.Optional.of("acc-40"));
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.getAccountPosts(accountId);
        assertNotNull(pageResponseDTO);
        verify(jwtService, times(1)).getAccountIdFromAuthContext();
        // Each account has 6 active posts (60% of 10)
        assertEquals(6, pageResponseDTO.getData().size());
        assertFalse(pageResponseDTO.isHasMore());
        for(var dto : pageResponseDTO.getData()){
            assertEquals("acc-30", dto.getAccountId());
            assertEquals("ACTIVE", dto.getStatus());
        }
    }

    @Test
    public void getAccountPosts_loggedInAndViewSelf_shouldReturnAllPosts(){
        String accountId = "acc-12";
        when(jwtService.getAccountIdFromAuthContext()).thenReturn(java.util.Optional.of("acc-12"));
        PageResponseDTO<PostResponseDTO> pageResponseDTO = postService.getAccountPosts(accountId);
        assertNotNull(pageResponseDTO);
        verify(jwtService, times(1)).getAccountIdFromAuthContext();
        // Each account has 10 posts
        assertEquals(10, pageResponseDTO.getData().size());
        assertFalse(pageResponseDTO.isHasMore());
        for(var dto : pageResponseDTO.getData()){
            assertEquals("acc-12", dto.getAccountId());
        }
    }

    private void generateTestPosts() {
        // generate 500 posts.
        Instant now = Instant.now();
        List<Post> posts = new ArrayList<>();
        Random rand = new Random();
        for (int i = 1; i <= 500; i++) {
            Post post = new Post();
            post.setCreatedAt(now);
            now = now.plus(rand.nextInt(1, 61), ChronoUnit.MINUTES); // increment time by 1 to 60 minutes
            post.setContent(generateContent(i));
            post.setAccountId("acc-" + (i % 50)); // 50 different accounts
            // Hidden:
            posts.add(post);
        }
        // Select 20% of posts to be hidden
        int to = (posts.size() * 20) / 100;
        for (int i = 0; i < to; i++) {
            posts.get(i).setStatus(Post.Status.HIDDEN);
        }
        to += (posts.size() * 10) / 100;
        // Select next 10% of posts to be deleted
        for (int i = (posts.size() * 20) / 100; i < to; i++) {
            posts.get(i).setStatus(Post.Status.DELETED);
        }
        // select 10% of posts to be draft
        to += (posts.size() * 10) / 100;
        for (int i = (posts.size() * 30) / 100; i < to; i++) {
                posts.get(i).setStatus(Post.Status.DRAFT);
        }
        // The rest are active, then select 50% of them to be modified
        int s = (posts.size() * 40) / 100;
        int e = (posts.size() - s) * 50 / 100 + s;
        for (int i = s; i < e; i++) {
            // Modify 50% of active posts
            posts.get(i).setModifiedAt(posts.get(i).getCreatedAt().plus(rand.nextInt(1, 31), ChronoUnit.DAYS));
        }
        // Shuffle the posts list to randomize the order
        Collections.shuffle(posts);
        // Batch save all posts
        posts = postRepository.saveAllAndFlush(posts);
        log.info("Generated {} test posts", posts.size());
    }

    /**
     * Generate rich-text HTML content for a post.
     * The content includes varying topics/keywords so that keyword search tests
     * can verify LIKE-based filtering on the content field.
     */
    private String generateContent(int index) {
        String topic;
        if (index % 15 == 0) {
            topic = "investment strategy";
        } else if (index % 10 == 0) {
            topic = "personal finance management";
        } else if (index % 7 == 0) {
            topic = "stock market news";
        } else {
            topic = "saving money and budgeting";
        }

        return "<p>Post <strong>#"
                + index
                + "</strong> about <em>"
                + topic
                + "</em>.</p>"
                + "<p>This is <span style=\"color: blue;\">rich text</span> content with a "
                + "<a href=\"https://example.com/posts/"
                + index
                + "\">detailed article</a> and some <code>inline code</code>.</p>";
    }
}
