package com.fp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
@Slf4j
public class FpAccountApplication {

	public static void main(String[] args) {
		try {
            SpringApplication.run(FpAccountApplication.class, args);
			log.info("Fp-Account Application started at {} successfully!", LocalDateTime.now());
        } catch (Exception e) {
			e.printStackTrace();
		}
    }

}
