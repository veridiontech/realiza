package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractProviderSupplierRepository extends JpaRepository<ContractProviderSupplier, String> {
    Page<ContractProviderSupplier> findAllByProviderSupplier_IdProvider(String idSearch, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranch(String idSearch, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndProviderSupplier_IdProvider(String idBranch, String idSupplier, Pageable pageable);
    Page<ContractProviderSupplier> findAllByIsActiveIsTrue(Pageable pageable);
    Page<ContractProviderSupplier> findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndIsActiveIsTrue(String idSearch, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndProviderSupplier_IdProviderAndIsActiveIsTrue(String idBranch, String idSupplier, Pageable pageable);
    ContractProviderSupplier findTopByProviderSupplier_IdProviderOrderByCreationDateDesc(String idCompany);
    List<ContractProviderSupplier> findAllByBranch_IdBranchAndSubcontractPermissionIsTrue(String idBranch);
    Long countByBranch_IdBranchAndFinishedIsFalse(String idBranch);
}
