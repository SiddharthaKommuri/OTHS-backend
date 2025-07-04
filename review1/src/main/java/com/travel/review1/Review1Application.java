package com.travel.review1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Review1Application {

	public static void main(String[] args) {
		SpringApplication.run(Review1Application.class, args);
	}

}
