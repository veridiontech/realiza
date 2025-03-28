package bl.tech.realiza.gateways.repositories.ultragaz;

import bl.tech.realiza.domains.ultragaz.Market;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepository extends JpaRepository<Market, String> {
}
