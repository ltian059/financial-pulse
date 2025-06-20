package com.fp.account;

import com.fp.common.properties.JwtProperties;
import com.fp.common.util.JwtUtil2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Base64;

@SpringBootTest
@Slf4j
class FpAccountApplicationTests {

	@Autowired
	private JwtUtil2 jwtUtil;
	@Autowired
	private JwtProperties jwtProperties;
	@Autowired
	private JwtDecoder jwtDecoder;
	@Test
	void contextLoads() {
	}

	@Test
	public void testJWTUtil(){


	}

	@Test
	public void testJwtVerfProcess(){
		String token = jwtUtil.generateAccessToken(1L, "test@exmaple.com", "test");
		log.info("Generated JWT Token: {}", token);

		//手动解析jwt结构
		String[] parts = token.split("\\.");
		assert parts.length == 3 : "Invalid JWT structure";

		//解码header和payload
		var header = new String(Base64.getUrlDecoder().decode(parts[0]));
		var payload = new String(Base64.getUrlDecoder().decode(parts[1]));
		log.info("JWT Header: {}", header);
		log.info("JWT Payload: {}", payload);

		//使用jwtDecoder验证
        try {
            Jwt jwt = jwtDecoder.decode(token);
            log.info("jwt验证成功");
        	log.info("jwt subject :{}", jwt.getSubject());
			log.info("jwt claims :{}", jwt.getClaims());

        } catch (JwtException e) {
			log.error("JWT verification failed: {}", e.getMessage());

        }

	}
	@Test
	public void testTokenValidAfterRestart(){
		String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aWFubGkwOTI3QGdtYWlsLmNvbSIsImF1ZCI6ImZpbmFuY2lhbC1wdWxzZS1hcGkiLCJhY2NvdW50SWQiOjYsIm5hbWUiOiJ0aWFubGkiLCJleHAiOjE3NTA0NDE3MDUsInR5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3NTA0NDE3MDR9.vBloW49uBbZ5bFLOZZ-YAfx0pHCC1BNucyeKFju6TSs";

		try{
			Jwt jwt = jwtDecoder.decode(token);
			log.info("✅ 重启后token仍然有效");
			log.info("过期时间: {}", jwt.getExpiresAt());
			log.info("当前时间: {}", Instant.now());
			// 验证token是否真的还没过期
			if (jwt.getExpiresAt().isAfter(Instant.now())) {
				log.info("✅ Token确实未过期");
			} else {
				log.warn("⚠️ Token已过期但验证通过 - 这是个问题!");
			}
		}catch (JwtException e){
			log.info("❌ Token验证失败: {}", e.getMessage());
		}

	}

}
