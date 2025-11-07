package com.example.demo;

import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class AsmsBackendApplication {

	private final AuthService authService;

	public static void main(String[] args) {
		SpringApplication.run(AsmsBackendApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initializeAdmin() {
		authService.initializeAdmin();
	}
}
