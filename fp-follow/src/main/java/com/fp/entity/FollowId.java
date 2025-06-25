package com.fp.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class FollowId implements Serializable {
    private String followerId;
    private String followeeId;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof FollowId that)) return false;

        return Objects.equals(this.followeeId, that.followeeId) &&
                Objects.equals(this.followerId, that.followerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followeeId);
    }
}
