package tms.crimsoncompass.TMS_OAuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients // Enables Feign client scanning
public class TmsOAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(TmsOAuthApplication.class, args);
	}

}
