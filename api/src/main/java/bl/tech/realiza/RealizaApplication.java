package bl.tech.realiza;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RealizaApplication {

	public static void main(String[] args) {
		// O .env será carregado apenas se estiver em ambiente local (feito no DotenvConfig)
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		dotenv.entries().forEach(entry -> {
			if (System.getenv(entry.getKey()) == null) {
				System.setProperty(entry.getKey(), entry.getValue()); // Só define se ainda não existir
			}
		});

		SpringApplication.run(RealizaApplication.class, args);
	}
}
