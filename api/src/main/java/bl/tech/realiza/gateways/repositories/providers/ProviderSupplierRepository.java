package bl.tech.realiza.gateways.repositories.providers;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderSupplierRepository extends JpaRepository<ProviderSupplier, String> {
    Page<ProviderSupplier> findAllByBranches_IdBranch(String idSearch, Pageable pageable);
    Optional<ProviderSupplier> findByCnpj(String cnpj);
}
