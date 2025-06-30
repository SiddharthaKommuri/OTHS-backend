package com.cts.SupportTicket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SupportTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportTicketApplication.class, args);
	}

}
