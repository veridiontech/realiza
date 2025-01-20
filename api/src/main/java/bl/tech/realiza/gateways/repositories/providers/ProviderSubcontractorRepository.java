package bl.tech.realiza.gateways.repositories.providers;

import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderSubcontractorRepository extends JpaRepository<ProviderSubcontractor, String> {
    Page<ProviderSubcontractor> findAllByProviderSupplier_IdProvider(String idSearch, Pageable pageable);
}
