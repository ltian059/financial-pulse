package com.fp.service.impl;

import com.fp.auth.service.JwtService;
import com.fp.dto.common.PageResponseDTO;
import com.fp.dto.content.ListPostsRequestDTO;
import com.fp.dto.content.PostRequestDTO;
import com.fp.dto.content.PostResponseDTO;
import com.fp.entity.Post;
import com.fp.exception.business.PostNotFoundException;
import com.fp.repository.PostRepository;
import com.fp.service.PostService;
import com.fp.specification.PostSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final JwtService jwtService;
    @Override
    public PostResponseDTO createPost(PostRequestDTO postRequestDTO) {
        // 1. Prepare the post entity from the request DTO
        Post post =  fromRequestDTO(postRequestDTO);
        // 2. Save the post entity using the repository.
        Post save = postRepository.save(post);
        //3. Prepare and return the response DTO.
        return buildResponseDTO(save);
    }

    private PostResponseDTO buildResponseDTO(Post save) {
        PostResponseDTO postResponseDTO = new PostResponseDTO();
        BeanUtils.copyProperties(save, postResponseDTO);
        // Convert the Status enum to String
        postResponseDTO.setStatus(save.getStatus().name());
        return postResponseDTO;
    }

    private Post fromRequestDTO(PostRequestDTO postRequestDTO) {
        Post post = new Post();
        BeanUtils.copyProperties(postRequestDTO, post);
        // Prepare the Status enum.
        post.setStatus(Post.Status.valueOf(postRequestDTO.getStatus()));
        return post;
    }

    @Override
    public PostResponseDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(HttpStatus.NOT_FOUND, "Post with ID " + postId + " not found"));
        return  buildResponseDTO(post);
    }


    /**
     * List posts with pagination and filtering
     * @param listPostsRequestDTO The request parameters for listing posts
     * @return A paginated response containing post view objects
     */
    @Override
    public PageResponseDTO<PostResponseDTO> listPosts(ListPostsRequestDTO listPostsRequestDTO) {
        // 1. Use Specification to build dynamic query based on query params.
        Specification<Post> specification = PostSpecification.fromListRequest(listPostsRequestDTO);
        PageRequest pageable = PageRequest.of(0, listPostsRequestDTO.getLimit());
        Page<Post> all = postRepository.findAll(specification, pageable);
        // 2. Build PageResponse
        return buildPageResponseDTO(all);
    }

    private PageResponseDTO<PostResponseDTO> buildPageResponseDTO(Page<Post> pages) {
        PageResponseDTO<PostResponseDTO> pageResponseDTO = new PageResponseDTO<>();
        // Map Post entities to PostResponseDTOs
        List<PostResponseDTO> responseDTOList = pages.getContent().stream().map(this::buildResponseDTO).toList();
        pageResponseDTO.setData(responseDTOList);
        // Set next cursor if there is a next page
        String nextCursor = null;
        if (pages.hasNext()) {
            PostResponseDTO lastPost = responseDTOList.get(responseDTOList.size() - 1);
            Instant effectiveAt = lastPost.getModifiedAt() != null ? lastPost.getModifiedAt() : lastPost.getCreatedAt();
            nextCursor = effectiveAt.toString() + "#" + lastPost.getId();
            pageResponseDTO.setNextCursor(nextCursor);
        }
        return PageResponseDTO.<PostResponseDTO>builder()
                .data(responseDTOList)
                .nextCursor(nextCursor)
                .hasMore(pages.hasNext())
                .limit(pages.getSize())
                .build();
    }

    /**
     * Get the specific account profile's posts.
     * The return content depends on the current user's status:
     * <ul>
     *     <li>if the <code>jwtContext.accountId == accountId</code>, then return all posts regardless of post status</li>
     *     <li>else then return all the posts with status <code>active</code></li>
     * </ul>
     * @param accountId The unique identifier of the account.
     * @return A paginated list of posts created by the specified account.
     */
    @Override
    public PageResponseDTO<PostResponseDTO> getAccountPosts(String accountId) {
        Optional<String> accountIdOpt = jwtService.getAccountIdFromAuthContext();
        ListPostsRequestDTO listRequestDTO;
        if(accountIdOpt.isEmpty() || !accountIdOpt.get().equals(accountId)){
            // Not logged in or viewing others' profile, only show active posts
            listRequestDTO = ListPostsRequestDTO.builder().accountId(accountId).status("ACTIVE").build();
        }else {
            // Viewing own profile, show all posts
            listRequestDTO = ListPostsRequestDTO.builder().accountId(accountId).build();
        }
        return listPosts(listRequestDTO);

    }

    @Override
    public void deletePost(Long postId) {

    }

    @Override
    public PostResponseDTO updatePost(Long postId, PostRequestDTO postRequestDTO) {
        return null;
    }

}
