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

		// TODO implementar exibição de unicidade de documentos ✔️
		// exibir documentos da matriz de documentos nas configurações do sistema
		// exibir campo de unicidade como check ✔️
		// incluir unicidade no update ✔️

		// TODO desenvolver botão de replicar alterações em outras filiais ✔️
		/* create, update, delete atividades ✔️
		* create, update, delete service types ✔️
		* allocate and deallocate documents ✔️
		* */

		// TODO desenvolver botão de replicar alterações em nova filial ✔️
		/* criar prop para identificar base e apenas replicar a partir de lá ✔️
		 */

		// TODO pensar na lógica de documentos bloqueáveis ✔️

		// TODO testar rota de histórico de um único documento ✔️

		// TODO adicionar prop de hasDoc para os docs da página nova de contrato ✔️

		// TODO refinar validação de IA ✔️

		// TODO adicionar lógica de recuperaçao de senha com código de 4 dígitos ✔️

		// TODO aumentar eficiência do search de documentos por atividade ✔️

		// TODO criar rota para checar se colaborador está bloqueado ✔️

		// TODO criar rota da Itaminas

		// TODO clear 4 digit codes 24h+ ✔️
	}
}
