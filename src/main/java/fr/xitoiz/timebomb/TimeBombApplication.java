package fr.xitoiz.timebomb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TimeBombApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimeBombApplication.class, args);
	}

}
