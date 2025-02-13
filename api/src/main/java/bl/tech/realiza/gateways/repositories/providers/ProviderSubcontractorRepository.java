package bl.tech.realiza.gateways.repositories.providers;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProviderSubcontractorRepository extends JpaRepository<ProviderSubcontractor, String> {
    Page<ProviderSubcontractor> findAllByProviderSupplier_IdProvider(String idSearch, Pageable pageable);
    Optional<ProviderSubcontractor> findByCnpj(String cnpj);
    List<ProviderSubcontractor> findAllByProviderSupplier_IdProvider(String idSearch);
    Long countByProviderSupplier_IdProviderIn(List<String> idSearch);
    Long countByProviderSupplier_IdProvider(String idSearch);
    Page<ProviderSubcontractor> findAllByIsActiveIsTrue(Pageable pageable);
    Page<ProviderSubcontractor> findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);
}
