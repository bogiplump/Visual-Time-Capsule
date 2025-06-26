package com.java.web.virtual.time.capsule;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VirtualTimeCapsuleApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.
			configure().directory("/app").load();
		System.setProperty("POSTGRES_DB", dotenv.get("POSTGRES_DB"));
		System.setProperty("POSTGRES_USER", dotenv.get("POSTGRES_USER"));
		System.setProperty("POSTGRES_PASSWORD", dotenv.get("POSTGRES_PASSWORD"));

		SpringApplication.run(VirtualTimeCapsuleApplication.class, args);
	}

}
