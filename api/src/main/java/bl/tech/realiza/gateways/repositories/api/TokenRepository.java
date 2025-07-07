package bl.tech.realiza.gateways.repositories.api;

import bl.tech.realiza.domains.services.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
}
