package com.fp.service.impl;

import com.fp.constant.PageConstant;
import com.fp.dto.common.PageResponseDTO;
import com.fp.dto.follow.request.*;
import com.fp.dto.follow.request.FollowPaginationRequestDTO.QueryType;
import com.fp.dto.follow.response.FollowResponseDTO;
import com.fp.dto.follow.response.FollowProjection;
import com.fp.exception.business.DuplicatedFollowException;
import com.fp.exception.business.FollowRelationshipNotFoundException;
import com.fp.exception.business.IllegalPageableCursorException;
import com.fp.exception.business.SelfFollowNotAllowedException;
import com.fp.entity.Follow;
import com.fp.repository.FollowRepository;
import com.fp.service.FollowService;
import com.fp.strategy.FollowQueryRequest;
import com.fp.strategy.FollowQueryStrategyContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;

    private final FollowQueryStrategyContext queryStrategyContext;

    /**
     * Get the number of followers for a specific account.
     * The current accountId is the followee ID.
     *
     * @param accountId
     * @return the number of followers for the account
     */
    @Override
    public Long getFollowerCount(String accountId) {
        //Get the number of people following this account
        //followeeId = accountId
        return followRepository.countFollowByFolloweeId(accountId);
    }

    @Override
    public void follow(FollowRequestDTO followRequestDTO) {
        var followeeId = followRequestDTO.getFolloweeId();
        var followerId = followRequestDTO.getAccountId();
        if (followerId.equals(followeeId)) {
            throw new SelfFollowNotAllowedException();
        }
        // Check if the follow relationship already exists
        Optional<Follow> optional = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        if (optional.isEmpty()) {
            //create a new follow relationship
            Follow follow = Follow.builder()
                    .followerId(followerId)
                    .followeeId(followeeId)
                    .createdAt(Instant.now())
                    .build();
            followRepository.save(follow);
        } else {
            throw new DuplicatedFollowException();
        }
    }

    @Override
    public void unfollow(UnfollowRequestDTO unfollowRequestDTO) {
        var followeeId = unfollowRequestDTO.getFolloweeId();
        var followerId = unfollowRequestDTO.getAccountId();
        // Check if the follow relationship exists
        Optional<Follow> optional = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        if (optional.isPresent()) {
            //delete the follow relationship
            followRepository.delete(optional.get());
        } else {
            throw new FollowRelationshipNotFoundException();
        }
    }

    @Override
    public PageResponseDTO<FollowResponseDTO> listFollowers(ListFollowersRequestDTO listFollowersRequestDTO) {
        log.debug("Listing followers of accountId: {}, request parameters:{}", listFollowersRequestDTO.getAccountId(), listFollowersRequestDTO);
        var paginationDTO = FollowPaginationRequestDTO.builder()
                .queryType(QueryType.FOLLOWERS)
                .order(listFollowersRequestDTO.getOrder())
                .cursor(listFollowersRequestDTO.getCursor())
                .accountId(listFollowersRequestDTO.getAccountId())
                .limit(listFollowersRequestDTO.getLimit())
                .build();
        return listFollow(paginationDTO);
    }

    @Override
    public PageResponseDTO<FollowResponseDTO> listFollowings(ListFollowingsRequestDTO listFollowingsRequestDTO) {
        log.debug("Listing followings of accountId: {}, request parameters:{}", listFollowingsRequestDTO.getAccountId(), listFollowingsRequestDTO);
        var paginationDTO = FollowPaginationRequestDTO.builder()
                .queryType(QueryType.FOLLOWINGS)
                .order(listFollowingsRequestDTO.getOrder())
                .cursor(listFollowingsRequestDTO.getCursor())
                .accountId(listFollowingsRequestDTO.getAccountId())
                .limit(listFollowingsRequestDTO.getLimit())
                .build();
        return listFollow(paginationDTO);
    }


    /**
     * Refactored listFollow method using Strategy Pattern
     */
    private PageResponseDTO<FollowResponseDTO> listFollow(FollowPaginationRequestDTO followPaginationRequestDTO) {
        //1. Build the query request for the strategy pattern
        FollowQueryRequest followQueryRequest = buildQueryRequest(followPaginationRequestDTO);
        //2. Use the strategy context to execute the query based on the request
        List<FollowProjection> follows = queryStrategyContext.executeFollowQuery(followQueryRequest);
        //3. Build the page response from the follows
        return buildPageResponse(follows, followPaginationRequestDTO.getLimit(), followPaginationRequestDTO.getQueryType());
    }

    /**
     * Build query request for strategy pattern.
     */
    private FollowQueryRequest buildQueryRequest(FollowPaginationRequestDTO followPaginationRequestDTO) {
        boolean isFirstPage = followPaginationRequestDTO.getCursor() == null || followPaginationRequestDTO.getCursor().isBlank();
        var builder = FollowQueryRequest.builder()
                .accountId(followPaginationRequestDTO.getAccountId())
                .queryType(followPaginationRequestDTO.getQueryType())
                .isDescending("desc".equalsIgnoreCase(followPaginationRequestDTO.getOrder()))
                .isFirstPage(isFirstPage)
                .pageable(PageRequest.of(0, followPaginationRequestDTO.getLimit()));
        if (!isFirstPage) {
            Pair<Instant, String> pair = parseCursor(followPaginationRequestDTO.getCursor());
            builder.cursorId(pair.getRight())
                    .cursorTimestamp(pair.getLeft());
        }

        return builder.build();

    }

    private PageResponseDTO<FollowResponseDTO> buildPageResponse(List<FollowProjection> follows, Integer limit, QueryType queryType) {
        //1. Map the follows to FollowResponseDTO
        List<FollowResponseDTO> data = mapToFollowResponseDTOS(follows);
        //2. Check if there are more followers
        boolean hasMore = follows.size() == limit;
        //3. Get next cursor
        String nextCursor = buildNextCursor(follows, queryType, hasMore);
        //4. Seal the response
        return PageResponseDTO.<FollowResponseDTO>builder()
                .limit(limit)
                .data(data)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    private String buildNextCursor(List<FollowProjection> follows, QueryType queryType, boolean hasMore) {
        String nextCursor = null;
        if(hasMore && !follows.isEmpty()){
            FollowProjection lastOne = follows.get(follows.size() - 1);
            // Build the next cursor based on the last timestamp and follower or followee ID depending on the query type
            nextCursor = buildCursor(lastOne.getCreatedAt(), lastOne.getAccountId());
        }
        return nextCursor;
    }

    private List<FollowResponseDTO> mapToFollowResponseDTOS(List<FollowProjection> follows) {
        //1. Map the followers to FollowerResponseDTO
        return follows.stream()
                .map(follow -> {
                    var dto = new FollowResponseDTO();
                    BeanUtils.copyProperties(follow, dto);
                    return dto;
                })
                .toList();
    }

    private static String buildCursor(Instant timestamp, String id) {
        return timestamp.toString() + PageConstant.CURSOR_SEPARATOR + id;
    }
    private static Pair<Instant, String> parseCursor(String cursor) {
        String[] split = cursor.split(PageConstant.CURSOR_SEPARATOR);
        if (split.length != 2) {
            throw new IllegalPageableCursorException("Invalid cursor format for list follower page. Expected format: 'timestamp:followerId'");
        }
        return Pair.of(Instant.parse(split[0]), split[1]);
    }

}
