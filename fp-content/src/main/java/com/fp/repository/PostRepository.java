package com.fp.repository;

import com.fp.entity.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    void deleteByAccountId(String accountId);

    List<Post> findByAccountId(String accountId);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    int incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.repostCount = p.repostCount + 1 WHERE p.id = :id")
    void incrementRepostCount(@Param("id") Long id);


    @Modifying
    @Query("UPDATE Post p SET p.quoteCount = p.quoteCount + 1 WHERE p.id = :id")
    void incrementQuoteCount(@Param("id") Long id);

}
