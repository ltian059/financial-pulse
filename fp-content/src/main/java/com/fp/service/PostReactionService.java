package com.fp.service;

public interface PostReactionService {

    public void savePostReaction(Long postId, String accountId, com.fp.entity.PostReaction.Type type);
}
