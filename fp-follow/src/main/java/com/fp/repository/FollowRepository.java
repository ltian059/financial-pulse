package com.fp.repository;

import com.fp.entity.Follow;
import com.fp.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    long countFollowByFolloweeId(String followeeId);

    Optional<Follow> findByFollowerIdAndFolloweeId(String followerId, String followeeId);

    @Query("SELECT f.followerId FROM Follow f WHERE f.followeeId = :accountId")
    List<String> findFollowerIdsByFolloweeId(String accountId);

    @Query("SELECT f.followeeId FROM Follow f WHERE f.followerId = :accountId")
    List<String> findFolloweeIdsByFollowerId(String accountId);
}
