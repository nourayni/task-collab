package com.taskcolab.taskcolab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.taskcolab")
@EnableJpaRepositories(basePackages = "com.taskcolab.*.repository")
@EntityScan(basePackages = "com.taskcolab.*.entity")
public class TaskcolabApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskcolabApplication.class, args);
	}

}
