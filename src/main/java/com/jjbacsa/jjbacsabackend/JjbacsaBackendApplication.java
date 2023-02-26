package com.jjbacsa.jjbacsabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class JjbacsaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JjbacsaBackendApplication.class, args);
	}

}
