package bit.salesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@EnableScheduling
public class SalesServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(SalesServiceApplication.class, args);
	}

}
