package com.fp.repository;

import com.fp.entity.Follow;
import com.fp.entity.FollowId;
import com.fp.dto.follow.response.FollowProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    long countFollowByFolloweeId(String followeeId);

    Optional<Follow> findByFollowerIdAndFolloweeId(String followerId, String followeeId);



    /**
     * Find followers of an account with pagination support.
     *
     * Cursor-based pagination for followers of an account.
     * @param accountId the ID of the account whose followers are being queried
     * @param cursorTimestamp the timestamp of the last fetched follower, used for pagination
     * @param cursorFollowerId the ID of the last-fetched follower, used for pagination
     * @return a list of Follow entities representing the followers of the specified account
     */
    @Query(
            """
            SELECT f.followerId as accountId, f.createdAt as createdAt FROM Follow f
            WHERE f.followeeId = :accountId
            AND (f.createdAt < :cursorTimestamp OR (f.createdAt = :cursorTimestamp AND f.followerId < :cursorFollowerId))
            ORDER BY f.createdAt DESC, f.followerId DESC
            """
    )
    List<FollowProjection> findAllFollowersWithCursorDesc(
            @Param("accountId") String accountId,
            @Param("cursorTimestamp") Instant cursorTimestamp,
            @Param("cursorFollowerId") String cursorFollowerId,
            Pageable pageable
    );

    @Query(
            """
            SELECT f.followerId as accountId, f.createdAt as createdAt FROM Follow f
                    WHERE f.followeeId = :accountId
                    AND (f.createdAt > :cursorTimestamp OR (f.createdAt = :cursorTimestamp AND f.followerId > :cursorFollowerId))
                    ORDER BY f.createdAt ASC, f.followerId ASC
            """
    )
    List<FollowProjection> findAllFollowersWithCursorAsc(
            @Param("accountId") String accountId,
            @Param("cursorTimestamp") Instant cursorTimestamp,
            @Param("cursorFollowerId") String cursorFollowerId,
            Pageable pageable
    );

    ///
    /// This method retrieves the first page of followers for a given account ID using descending order.
    ///
    @Query("""
            SELECT f.followerId as accountId, f.createdAt as createdAt FROM Follow f
            WHERE f.followeeId = :accountId
            ORDER BY f.createdAt DESC, f.followerId DESC
    """)
    List<FollowProjection> findFirstPageFollowersDesc(
            @Param("accountId") String accountId,
            Pageable pageable
    );

    @Query("""
            SELECT f.followerId as accountId, f.createdAt as createdAt FROM Follow f
            WHERE f.followeeId = :accountId
            ORDER BY f.createdAt ASC, f.followerId ASC
    """)
    List<FollowProjection> findFirstPageFollowersAsc(
            @Param("accountId") String accountId,
            Pageable pageable
    );


    ///
    ///
    /// # Find Followings(followees) of an account
    ///
    @Query("""
            SELECT f.followeeId as accountId, f.createdAt as createdAt FROM Follow f
            WHERE f.followerId = :accountId
            ORDER BY f.createdAt DESC, f.followeeId DESC
    """)
    List<FollowProjection> findFirstPageFolloweesDesc(@Param("accountId") String accountId, Pageable pageable);

    @Query("""
            SELECT f.followeeId as accountId, f.createdAt as createdAt FROM Follow f
            WHERE f.followerId = :accountId
            ORDER BY f.createdAt ASC, f.followeeId ASC
    """)
    List<FollowProjection> findFirstPageFolloweesAsc(@Param("accountId") String accountId, Pageable pageable);

    @Query(
            """
            SELECT f.followeeId as accountId, f.createdAt as createdAt FROM Follow f
            WHERE f.followerId = :accountId
            AND (f.createdAt < :cursorTimestamp OR (f.createdAt = :cursorTimestamp AND f.followeeId < :cursorFolloweeId))
            ORDER BY f.createdAt DESC, f.followeeId DESC
            """
    )
    List<FollowProjection> findAllFolloweesWithCursorDesc(
            @Param("accountId") String accountId,
            @Param("cursorTimestamp") Instant cursorTimestamp,
            @Param("cursorFolloweeId") String cursorFolloweeId,
            Pageable pageable
    );

    @Query(
            """
            SELECT f.followeeId as accountId, f.createdAt as createdAt FROM Follow f
            WHERE f.followerId = :accountId
            AND (f.createdAt < :cursorTimestamp OR (f.createdAt = :cursorTimestamp AND f.followeeId < :cursorFolloweeId))
            ORDER BY f.createdAt ASC, f.followeeId ASC
            """
    )
    List<FollowProjection> findAllFolloweesWithCursorAsc(
            @Param("accountId") String accountId,
            @Param("cursorTimestamp") Instant cursorTimestamp,
            @Param("cursorFolloweeId") String cursorFolloweeId,
            Pageable pageable
    );

    long countFollowByFollowerId(String followerId);
}
