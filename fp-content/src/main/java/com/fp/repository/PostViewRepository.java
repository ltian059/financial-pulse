package com.fp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fp.entity.PostView;

public interface PostViewRepository extends JpaRepository<PostView, Long> {

}
