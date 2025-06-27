package com.fp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String content; // rich text HTML


    //TODO ADD INDEXES FOR SEARCHING
    @Column(nullable = false)
    private Instant createdAt;

    private Instant modifiedAt;

    private String imageLinks;

    @Column(nullable = false)
    private Long likes;

    @Column(nullable = false)
    private Long views;

    @Column(columnDefinition = "text")
    private String labels;

    @Column(nullable = false)
    private String accountId;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
