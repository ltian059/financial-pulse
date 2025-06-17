package com.fp.follow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class FollowId implements Serializable {
    private Long followerId;
    private Long followeeId;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof FollowId)) return false;
        FollowId that = (FollowId) obj;

        return Objects.equals(this.followeeId, that.followeeId) &&
                Objects.equals(this.followerId, that.followerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followeeId);
    }
}
