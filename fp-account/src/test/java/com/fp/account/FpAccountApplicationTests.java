package com.fp.account;

import com.fp.common.properties.JwtProperties;
import com.fp.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
@Slf4j
class FpAccountApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void testJWTUtil(){
		var key = "idlHeCs1ALNXuA2gluO4lZj7cgSVBRmj";
		var prop = new JwtProperties();
		prop.setSecret(key);
		var jwtUtil = new JwtUtil(prop);
		String accessToken = jwtUtil.generateAccessToken(1L, "tianli0927@gmail.com", "litian");
		log.info(accessToken);

		String refreshToken = jwtUtil.generateRefreshToken(1L, "tianli0927@gmail.com");
		log.info(refreshToken);

	}

}
