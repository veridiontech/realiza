package bl.tech.realiza.gateways.repositories.providers;

import bl.tech.realiza.domains.providers.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ProviderRepository extends JpaRepository<Provider, String>, JpaSpecificationExecutor<Provider> {
    Collection<Provider> findAllByIsActive(boolean b);
    Collection<Provider> findAllByDeleteRequest(boolean b);
    @Query("""
    SELECT DISTINCT p.idProvider
        FROM Provider p
        WHERE p.isActive = true
""")
    List<String> findAllActiveIds();
}
