package bl.tech.realiza.gateways.repositories.clients;

import bl.tech.realiza.domains.clients.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByCnpj(String cnpj);
    Collection<Client> findAllByIsActive(boolean b);
    Collection<Client> findAllByDeleteRequest(boolean b);
    Page<Client> findAllByIsActiveIsTrue(Pageable pageable);
}
