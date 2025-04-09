package bl.tech.realiza.gateways.repositories.ultragaz;

import bl.tech.realiza.domains.ultragaz.Center;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CenterRepository extends JpaRepository<Center, String> {
    Page<Center> findAllByMarket_IdMarket(String idMarket, Pageable pageable);
}
