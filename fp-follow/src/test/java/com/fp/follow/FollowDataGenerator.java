package com.fp.follow;

import com.fp.entity.Follow;
import com.fp.repository.FollowRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
@Slf4j
public class FollowDataGenerator {
    @Autowired
    private FollowRepository followRepository;

    /**
     * Generate test data for Follow table
     * Creates realistic follow relationships for pagination testing
     */
    @Test
    public void generateTestData() {
        // Clear existing data
        followRepository.deleteAll();

        // Create test users
        List<String> users = generateTestUsers(30);

        // Generate follow relationships
        List<Follow> follows = new ArrayList<>();

        // Scenario 1: Popular users (many followers)
        follows.addAll(createPopularUsers(users));

        // Scenario 2: Social users (follow many people)
        follows.addAll(createSocialUsers(users));

        // Scenario 3: Mutual follows
        follows.addAll(createMutualFollows(users));

        // Scenario 4: Random follows for diversity
        follows.addAll(createRandomFollows(users));

        // Save all follows
        followRepository.saveAll(follows);

        log.info("Generated {} follow relationships", follows.size());
        printTestDataSummary(users);
    }

    /**
     * Generate UUIDs for test users
     */
    private List<String> generateTestUsers(int count) {
        List<String> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(UUID.randomUUID().toString());
        }
        return users;
    }

    /**
     * Create popular users that have many followers
     * Good for testing followers pagination
     */
    private List<Follow> createPopularUsers(List<String> users) {
        List<Follow> follows = new ArrayList<>();

        // Select 3 users to be "influencers"
        List<String> influencers = users.subList(0, 3);

        for (String influencer : influencers) {
            // Each influencer gets 50-80 followers
            int followerCount = 50 + new Random().nextInt(31);

            for (int i = 0; i < followerCount; i++) {
                String follower = users.get(3 + (i % (users.size() - 3)));

                // Ensure no self-follow
                if (!follower.equals(influencer)) {
                    follows.add(Follow.builder()
                            .followeeId(influencer)  // The popular user being followed
                            .followerId(follower)    // The follower
                            .createdAt(generateRandomTimestamp(i, followerCount))
                            .build());
                }
            }
        }

        return follows;
    }

    /**
     * Create social users that follow many people
     * Good for testing following pagination
     */
    private List<Follow> createSocialUsers(List<String> users) {
        List<Follow> follows = new ArrayList<>();

        // Select 3 users to be "social butterflies"
        List<String> socialUsers = users.subList(3, 6);

        for (String socialUser : socialUsers) {
            // Each social user follows 40-60 people
            int followingCount = 40 + new Random().nextInt(21);

            for (int i = 0; i < followingCount; i++) {
                String followee = users.get(6 + (i % (users.size() - 6)));

                // Ensure no self-follow
                if (!socialUser.equals(followee)) {
                    follows.add(Follow.builder()
                            .followerId(socialUser)   // The social user
                            .followeeId(followee)     // The person being followed
                            .createdAt(generateRandomTimestamp(i, followingCount))
                            .build());
                }
            }
        }

        return follows;
    }

    /**
     * Create mutual follow relationships
     */
    private List<Follow> createMutualFollows(List<String> users) {
        List<Follow> follows = new ArrayList<>();
        Random random = new Random();

        // Create 30 mutual follow pairs
        for (int i = 0; i < 30; i++) {
            String user1 = users.get(random.nextInt(users.size()));
            String user2 = users.get(random.nextInt(users.size()));

            if (!user1.equals(user2)) {
                Instant baseTime = generateRandomTimestamp(i, 30);

                // Mutual follows with slight time difference
                follows.add(Follow.builder()
                        .followerId(user1)
                        .followeeId(user2)
                        .createdAt(baseTime)
                        .build());

                follows.add(Follow.builder()
                        .followerId(user2)
                        .followeeId(user1)
                        .createdAt(baseTime.plusSeconds(random.nextInt(3600))) // Within 1 hour
                        .build());
            }
        }

        return follows;
    }

    /**
     * Create random follow relationships for diversity
     */
    private List<Follow> createRandomFollows(List<String> users) {
        List<Follow> follows = new ArrayList<>();
        Random random = new Random();

        // Generate 100 random follows
        for (int i = 0; i < 100; i++) {
            String follower = users.get(random.nextInt(users.size()));
            String followee = users.get(random.nextInt(users.size()));

            if (!follower.equals(followee)) {
                follows.add(Follow.builder()
                        .followerId(follower)
                        .followeeId(followee)
                        .createdAt(generateRandomTimestamp(i, 100))
                        .build());
            }
        }

        return follows;
    }

    /**
     * Generate realistic timestamps spread over the last 30 days
     */
    private Instant generateRandomTimestamp(int index, int total) {
        Random random = new Random();

        // Spread over last 30 days
        long daysAgo = 30;
        long secondsInDay = 24 * 60 * 60;

        // Base time (30 days ago)
        Instant baseTime = Instant.now().minus(Duration.ofDays(daysAgo));

        // Progressive time with some randomness
        long progressSeconds = (long) ((double) index / total * daysAgo * secondsInDay);
        long randomSeconds = random.nextInt(3600); // Up to 1 hour random variance

        return baseTime.plusSeconds(progressSeconds + randomSeconds);
    }

    /**
     * Print summary of generated test data
     */
    private void printTestDataSummary(List<String> users) {
        log.info("=== Test Data Summary ===");

        // Print influencers (users with most followers)
        List<String> influencers = users.subList(0, 3);
        for (int i = 0; i < influencers.size(); i++) {
            String influencer = influencers.get(i);
            long followerCount = followRepository.countFollowByFolloweeId(influencer);
            log.info("Influencer {}: {} (followers: {})", i + 1, influencer, followerCount);
        }

        // Print social users (users who follow many)
        List<String> socialUsers = users.subList(3, 6);
        for (int i = 0; i < socialUsers.size(); i++) {
            String socialUser = socialUsers.get(i);
            long followingCount = followRepository.countFollowByFollowerId(socialUser);
            log.info("Social User {}: {} (following: {})", i + 1, socialUser, followingCount);
        }

        long totalFollows = followRepository.count();
        log.info("Total follow relationships: {}", totalFollows);
    }

    // Additional repository method needed for testing
    // Add this to FollowRepository interface:
    /*
    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followerId = :followerId")
    long countFollowByFollowerId(@Param("followerId") String followerId);
    */
}
