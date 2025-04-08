package com.interviewmate.be;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@EnableScheduling
@SpringBootApplication
public class BeApplication {

	public static void main(String[] args) {

		// .env 파일이 존재할 때만 로딩
		File envFile = new File(".env");
		if (envFile.exists()) {
			Dotenv dotenv = Dotenv.configure().load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		}

		SpringApplication.run(BeApplication.class, args);

	}

}
