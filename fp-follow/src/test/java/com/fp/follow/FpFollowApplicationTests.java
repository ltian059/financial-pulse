package com.fp.follow;

import com.fp.entity.Follow;
import com.fp.service.FollowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;

@SpringBootTest
class FpFollowApplicationTests {

    @Autowired
    private FollowService followService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testInstant(){
        System.out.println(Instant.now());
        System.out.println(LocalDateTime.now());
    }


    @Test
    public void testPageQueries(){

    }
}
