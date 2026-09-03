package com.mentorship.hanakoleh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HanakolehApplication {

	public static void main(String[] args) {
		SpringApplication.run(HanakolehApplication.class, args);
	}

}
