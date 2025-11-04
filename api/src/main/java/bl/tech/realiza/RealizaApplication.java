package bl.tech.realiza;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = "bl.tech.realiza")
@EnableRabbit
@EnableScheduling
public class RealizaApplication {

	public static void main(String[] args) {
		// O .env será carregado apenas se estiver em ambiente local (feito no DotenvConfig)
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		dotenv.entries().forEach(entry -> {
			if (System.getenv(entry.getKey()) == null) {
				System.setProperty(entry.getKey(), entry.getValue()); // Só define se ainda não existir
			}
		});

		System.setProperty("user.timezone", "America/Sao_Paulo");

		SpringApplication.run(RealizaApplication.class, args);
	}
}
