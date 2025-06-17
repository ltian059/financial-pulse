package com.fp.follow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;

@SpringBootTest
class FpFollowApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testInstant(){
        System.out.println(Instant.now());
        System.out.println(LocalDateTime.now());
    }

}
