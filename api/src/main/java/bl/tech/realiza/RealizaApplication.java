package bl.tech.realiza;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@SpringBootApplication
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

		// TODO criar rotas que usem as permissões para filtrar ✔️
		/* incluir permissões no token ✔️
		* documentos do colaborador por permissão ✔️
		* contratos e documentos por permissão da nova página de contratos ✔️
		* contratos por permissão 👁️
		* filiais por permissão 👁️
		* pode finalizar ou suspender contrato 👁️
		* */

		// TODO pensar na tratativa de inativar usuário ✔️
		// caso tenha contratos com ele como responsável, ao clicar em desabilitar,
		// exibir modal com cada contrato na esquerda e um dropdown dos responsáveis na direita
		// exceto o que esta sendo inativado
		// rota exibir contratos de um responsável ✔️
		// rota atualizar somente responsável contrato ✔️

		// TODO criar rota da Itaminas
	}
}
