package com.example.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmsApplication.class, args);
	}

}

// package com.example.sms;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Import;
// import org.springframework.scheduling.annotation.EnableScheduling;

// import com.example.sms.api.config.RestTemplateConfig;

// @SpringBootApplication
// @EnableScheduling
// @Import(RestTemplateConfig.class)
// public class SmsApplication {
//     public static void main(String[] args) {
//         SpringApplication.run(SmsApplication.class, args);
//     }
// }