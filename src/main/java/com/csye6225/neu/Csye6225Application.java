package com.csye6225.neu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.csye6225.neu"})
@EnableJpaAuditing
@EnableScheduling
public class Csye6225Application {

	public static void main(String[] args) {
		SpringApplication.run(Csye6225Application.class, args);
	}


}
