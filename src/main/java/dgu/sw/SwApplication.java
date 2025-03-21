package dgu.sw;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootApplication
public class SwApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwApplication.class, args);
	}

//	@Bean
//	CommandLineRunner testMailSender(JavaMailSender mailSender) {
//		return args -> System.out.println("✅ JavaMailSender is injected: " + mailSender);
//	}
}