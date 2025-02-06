package bl.tech.realiza.gateways.repositories.providers;

import bl.tech.realiza.domains.providers.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ProviderRepository extends JpaRepository<Provider, String> {
    Collection<Provider> findAllByIsActive(boolean b);
    Collection<Provider> findAllByDeleteRequest(boolean b);
}
