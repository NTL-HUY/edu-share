package com.nbh.edushare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EdushareApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdushareApplication.class, args);
	}

}
