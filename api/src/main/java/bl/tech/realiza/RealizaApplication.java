package bl.tech.realiza;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RealizaApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
				.directory("./")      // Busca o .env no diretório atual
				.ignoreIfMissing()    // Ignora se o .env não for encontrado
				.load();

		// Adiciona as variáveis do .env às variáveis de ambiente do sistema
		dotenv.entries().forEach(entry -> {
			// Se a variável do sistema já existir, não sobrescreve
			System.setProperty(entry.getKey(),
					System.getenv(entry.getKey()) != null
							? System.getenv(entry.getKey())
							: entry.getValue());
		});


		SpringApplication.run(RealizaApplication.class, args);
	}



}
