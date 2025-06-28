package com.fp.service.impl;

import com.fp.constant.PageConstant;
import com.fp.dto.common.PageResponseDTO;
import com.fp.dto.follow.request.*;
import com.fp.dto.follow.response.FollowResponseDTO;
import com.fp.exception.business.DuplicatedFollowException;
import com.fp.exception.business.FollowRelationshipNotFoundException;
import com.fp.exception.business.IllegalPageableCursorException;
import com.fp.exception.business.SelfFollowNotAllowedException;
import com.fp.entity.Follow;
import com.fp.repository.FollowRepository;
import com.fp.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;


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
        var paginationDTO = FollowPaginationRequestDTO.builder()
                .queryType(FollowPaginationRequestDTO.QueryType.FOLLOWERS)
                .order(listFollowersRequestDTO.getOrder())
                .cursor(listFollowersRequestDTO.getCursor())
                .accountId(listFollowersRequestDTO.getAccountId())
                .limit(listFollowersRequestDTO.getLimit())
                .build();
        return listFollow(paginationDTO);
    }

    @Override
    public PageResponseDTO<FollowResponseDTO> listFollowings(ListFollowingsRequestDTO listFollowingsRequestDTO) {
        var paginationDTO = FollowPaginationRequestDTO.builder()
                .queryType(FollowPaginationRequestDTO.QueryType.FOLLOWINGS)
                .order(listFollowingsRequestDTO.getOrder())
                .cursor(listFollowingsRequestDTO.getCursor())
                .accountId(listFollowingsRequestDTO.getAccountId())
                .limit(listFollowingsRequestDTO.getLimit())
                .build();
        return listFollow(paginationDTO);
    }


    private PageResponseDTO<FollowResponseDTO> listFollow(FollowPaginationRequestDTO followPaginationRequestDTO) {
        // Get the list of followers for the account

        var queryType = followPaginationRequestDTO.getQueryType();
        var isDesc = "desc".equalsIgnoreCase(followPaginationRequestDTO.getOrder());
        var pageable = PageRequest.of(0, followPaginationRequestDTO.getLimit());
        var accountId = followPaginationRequestDTO.getAccountId();
        var cursor = followPaginationRequestDTO.getCursor();
        List<Follow> follows;
        if(cursor == null || cursor.trim().isEmpty()){
            //return first page
            follows = switch (queryType) {
                case FOLLOWERS -> isDesc
                        ? followRepository.findFirstPageFollowersDesc(accountId, pageable)
                        : followRepository.findFirstPageFollowersAsc(accountId, pageable);
                case FOLLOWINGS -> isDesc
                        ? followRepository.findFirstPageFolloweesDesc(accountId, pageable)
                        : followRepository.findFirstPageFolloweesAsc(accountId, pageable);
            };
        }else{
            Pair<Instant, String> pair = parseCursor(cursor);
            Instant cursorTimestamp = pair.getLeft();
            String cursorId = pair.getRight();
            follows = switch(queryType){
                case FOLLOWERS -> isDesc
                        ? followRepository.findAllFollowersWithCursorDesc(accountId, cursorTimestamp, cursorId, pageable)
                        : followRepository.findAllFollowersWithCursorAsc(accountId, cursorTimestamp, cursorId, pageable);
                case FOLLOWINGS -> isDesc
                        ? followRepository.findAllFolloweesWithCursorDesc(accountId, cursorTimestamp, cursorId, pageable)
                        : followRepository.findAllFolloweesWithCursorAsc(accountId, cursorTimestamp, cursorId, pageable);
            };
        }
        // 3. Map the followers to the response DTO
        return buildPageResponse(follows, followPaginationRequestDTO.getLimit(), queryType);
    }

    private PageResponseDTO<FollowResponseDTO> buildPageResponse(List<Follow> follows, Integer limit, FollowPaginationRequestDTO.QueryType queryType) {
        //1. Map the followers to FollowerResponseDTO
        List<FollowResponseDTO> data = follows.stream()
                .map(follow -> {
                    var dto = new FollowResponseDTO();
                    BeanUtils.copyProperties(follow, dto);
                    return dto;
                })
                .toList();
        //2. Check if there are more followers
        boolean hasMore = follows.size() == limit;
        //3. If so, get the last follower to determine the next cursor
        String nextCursor = null;
        if(hasMore && !follows.isEmpty()){
            Follow lastOne = follows.get(follows.size() - 1);
            // Build the next cursor based on the last timestamp and follower or followee ID depending on the query type
            nextCursor = switch (queryType){
                case FOLLOWERS -> buildCursor(lastOne.getCreatedAt(), lastOne.getFollowerId());
                case FOLLOWINGS -> buildCursor(lastOne.getCreatedAt(), lastOne.getFolloweeId());
            };
        }
        //4. Seal the response
        return PageResponseDTO.<FollowResponseDTO>builder()
                .limit(limit)
                .data(data)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
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
