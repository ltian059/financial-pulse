package com.fp.account;

import com.fp.common.properties.JwtProperties;
import com.fp.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
@Slf4j
class FpAccountApplicationTests {

	@Autowired
	private JwtUtil jwtUtil;
	@Test
	void contextLoads() {
	}

	@Test
	public void testJWTUtil(){
		var token = jwtUtil.generateAccessToken(1L, "admin", "litian");
		log.info(token);
	}

}
