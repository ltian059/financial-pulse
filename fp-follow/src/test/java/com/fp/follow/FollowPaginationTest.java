package com.fp.follow;

import com.fp.dto.common.PageResponseDTO;
import com.fp.dto.follow.request.FollowPaginationRequestDTO;
import com.fp.dto.follow.request.ListFollowersRequestDTO;
import com.fp.dto.follow.request.ListFollowingsRequestDTO;
import com.fp.dto.follow.response.FollowResponseDTO;
import com.fp.entity.Follow;
import com.fp.repository.FollowRepository;
import com.fp.service.FollowService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class FollowPaginationTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private FollowRepository followRepository;

    /**
     * Test followers pagination (DESC order)
     */
    @Test
    public void testFollowersPaginationDesc() {
        // Get an influencer ID (user with many followers)
        String influencerId = getInfluencerUserId();

        // Test first page
        ListFollowersRequestDTO firstPageRequest = ListFollowersRequestDTO.builder()
                .limit(10)
                .accountId(influencerId)
                .order("desc")
                .build();


        PageResponseDTO<FollowResponseDTO> firstPage = followService
                .listFollowers(firstPageRequest);

        assertThat(firstPage.getData()).hasSize(10);
        assertThat(firstPage.isHasMore()).isTrue();
        assertThat(firstPage.getNextCursor()).isNotNull();

        log.info("First page cursor: {}", firstPage.getNextCursor());

        // Test second page
        ListFollowersRequestDTO secondPageRequest = ListFollowersRequestDTO.builder()
                .limit(10)
                .accountId(influencerId)
                .cursor(firstPage.getNextCursor())
                .order("desc")
                .build();

        PageResponseDTO<FollowResponseDTO> secondPage = followService.listFollowers(secondPageRequest);

        assertThat(secondPage.getData()).hasSize(10);

        // Verify no overlap between pages
        Set<String> firstPageFollowerIds = firstPage.getData().stream()
                .map(FollowResponseDTO::getFollowerId)
                .collect(Collectors.toSet());

        Set<String> secondPageFollowerIds = secondPage.getData().stream()
                .map(FollowResponseDTO::getFollowerId)
                .collect(Collectors.toSet());

        assertThat(Collections.disjoint(firstPageFollowerIds, secondPageFollowerIds)).isTrue();

        // Verify chronological order (DESC)
        List<Instant> firstPageTimes = firstPage.getData().stream()
                .map(FollowResponseDTO::getCreatedAt)
                .collect(Collectors.toList());

        assertThat(firstPageTimes).isSortedAccordingTo(Comparator.reverseOrder());
    }

    /**
     * Test following pagination (ASC order)
     */
    @Test
    public void testFollowingPaginationAsc() {
        // Get a social user ID (user who follows many)
        String socialUserId = getSocialUserId();

        ListFollowingsRequestDTO request = ListFollowingsRequestDTO.builder()
                .limit(15)
                .accountId(socialUserId)
                .order("asc")
                .build();

        PageResponseDTO<FollowResponseDTO> page = followService.listFollowings(request);

        assertThat(page.getData()).hasSizeLessThanOrEqualTo(15);

        // Verify chronological order (ASC)
        List<Instant> times = page.getData().stream()
                .map(FollowResponseDTO::getCreatedAt)
                .collect(Collectors.toList());

        assertThat(times).isSortedAccordingTo(Instant::compareTo);
    }

    /**
     * Test pagination with same timestamps
     */
    @Test
    public void testPaginationWithSameTimestamps() {
        // Create test data with identical timestamps
        Instant sameTime = Instant.now();
        String followeeId = UUID.randomUUID().toString();
        log.info("Testing pagination with same timestamps for followee: {}", followeeId);
        log.info("Using timestamp: {}", sameTime);
        List<Follow> sameTimeFollows = List.of(
                Follow.builder().followeeId(followeeId).followerId("follower_a").createdAt(sameTime).build(),
                Follow.builder().followeeId(followeeId).followerId("follower_b").createdAt(sameTime).build(),
                Follow.builder().followeeId(followeeId).followerId("follower_c").createdAt(sameTime).build(),
                Follow.builder().followeeId(followeeId).followerId("follower_d").createdAt(sameTime).build()
        );

        followRepository.saveAll(sameTimeFollows);

        // Test pagination
        ListFollowersRequestDTO request = ListFollowersRequestDTO.builder()
                .limit(2)
                .accountId(followeeId)
                .order("desc")
                .build();

        PageResponseDTO<FollowResponseDTO> page1 = followService.listFollowers(request);
        assertThat(page1.getData()).hasSize(2);

        // Get second page
        request.setCursor(page1.getNextCursor());
        PageResponseDTO<FollowResponseDTO> page2 = followService.listFollowers(request);
        assertThat(page2.getData()).hasSize(2);

        // Verify no duplicates
        Set<String> allFollowerIds = new HashSet<>();
        page1.getData().forEach(f -> allFollowerIds.add(f.getFollowerId()));
        page2.getData().forEach(f -> allFollowerIds.add(f.getFollowerId()));

        assertThat(allFollowerIds).hasSize(4); // No duplicates
    }

    // Helper methods to get test user IDs
    private String getInfluencerUserId() {
        return followRepository.findAll().stream()
                .collect(Collectors.groupingBy(Follow::getFolloweeId, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new RuntimeException("No influencer found"));
    }

    private String getSocialUserId() {
        return followRepository.findAll().stream()
                .collect(Collectors.groupingBy(Follow::getFollowerId, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new RuntimeException("No social user found"));
    }


    @Test
    public void testInstantParseProblem(){
        // 模拟数据库中的微秒精度时间
        Instant originalTime = Instant.parse("2025-06-28T15:57:08.735826Z");
        System.out.println("原始时间: " + originalTime);
        // 输出: 2025-06-28T15:57:08.735826Z

        // 当前的cursor生成方式（有问题）
        long epochMilli = originalTime.toEpochMilli();
        System.out.println("转为毫秒: " + epochMilli);
        // 输出: 1719593828735 (丢失了826微秒)

        // 从cursor恢复时间
        Instant restoredTime = Instant.ofEpochMilli(epochMilli);
        System.out.println("恢复时间: " + restoredTime);
        // 输出: 2025-06-28T15:57:08.735Z (微秒信息丢失！)

        // 问题：原始时间和恢复时间不相等
        System.out.println("时间相等? " + originalTime.equals(restoredTime));
        // 输出: false

        // 这导致cursor查询条件不准确
        // f.createdAt < restoredTime 可能会遗漏数据
        // f.createdAt = restoredTime 永远不会匹配
    }
}
