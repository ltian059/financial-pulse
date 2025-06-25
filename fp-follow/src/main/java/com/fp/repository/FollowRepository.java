package com.fp.repository;

import com.fp.entity.Follow;
import com.fp.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    long countFollowByFolloweeId(String followeeId);

    Optional<Follow> findByFollowerIdAndFolloweeId(String followerId, String followeeId);
}
