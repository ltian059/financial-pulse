package com.fp.follow.repository;

import com.fp.follow.entity.Follow;
import com.fp.follow.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    long countFollowByFolloweeId(Long followeeId);

    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
}
