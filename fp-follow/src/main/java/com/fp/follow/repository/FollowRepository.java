package com.fp.follow.repository;

import com.fp.follow.entity.Follow;
import com.fp.follow.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    long countFollowByFolloweeId(Long followeeId);
}
