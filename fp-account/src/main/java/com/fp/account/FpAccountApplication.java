package com.fp.account;

import com.fp.common.config.ExternalServiceAutoConfiguration;
import com.fp.common.properties.ExternalServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class FpAccountApplication {

	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext context = SpringApplication.run(FpAccountApplication.class, args);
		} catch (Exception e) {
			log.error("Application start failed" + e.getMessage());
			e.printStackTrace();
		}
    }

}
