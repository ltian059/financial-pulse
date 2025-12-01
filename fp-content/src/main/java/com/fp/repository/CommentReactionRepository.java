package com.fp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fp.entity.CommentReaction;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long>{

}
