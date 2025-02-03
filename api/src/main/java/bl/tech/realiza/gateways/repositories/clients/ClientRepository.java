package bl.tech.realiza.gateways.repositories.clients;

import bl.tech.realiza.domains.clients.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByCnpj(String cnpj);
}
