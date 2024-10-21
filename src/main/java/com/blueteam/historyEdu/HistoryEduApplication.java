package com.blueteam.historyEdu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class HistoryEduApplication {

	public static void main(String[] args) {
		SpringApplication.run(HistoryEduApplication.class, args);
	}

}
