package com.fp.follow;

import com.fp.entity.Follow;
import com.fp.repository.FollowRepository;
import com.fp.service.FollowService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;

@SpringBootTest
@Slf4j
public class RDSTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private FollowRepository followRepository;

    @Test
    public void testInsert(){
//        followService.followAccount("follower1", "followee1");

        Follow follower1 = Follow.builder().followerId("follower1").followeeId("followee1").createdAt(Instant.now()).build();
        followRepository.save(follower1);
    }

    @Test
    public void testDelete(){
        followRepository.delete(Follow.builder().followerId("follower1").followeeId("333").build());
    }
}
