package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractProviderSupplierRepository extends JpaRepository<ContractProviderSupplier, String> {
    Page<ContractProviderSupplier> findAllByProviderSupplier_IdProvider(String idSearch, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranch(String idSearch, Pageable pageable);
}
