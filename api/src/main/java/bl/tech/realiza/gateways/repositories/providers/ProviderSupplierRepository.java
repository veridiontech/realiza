package bl.tech.realiza.gateways.repositories.providers;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderSupplierRepository extends JpaRepository<ProviderSupplier, String> {
    Page<ProviderSupplier> findAllByClient_IdClient(String idSearch, Pageable pageable);
}
