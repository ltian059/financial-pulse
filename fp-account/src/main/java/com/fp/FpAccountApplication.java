package com.fp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@Slf4j
public class FpAccountApplication {

	public static void main(String[] args) {
		try {
            SpringApplication.run(FpAccountApplication.class, args);
			log.info("Application Under Test deployment branch Started Successfully");
        } catch (Exception e) {
			e.printStackTrace();
		}
    }

}
