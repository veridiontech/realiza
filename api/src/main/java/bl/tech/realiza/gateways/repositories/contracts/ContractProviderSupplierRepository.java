package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import static bl.tech.realiza.domains.contract.Contract.*;

public interface ContractProviderSupplierRepository extends JpaRepository<ContractProviderSupplier, String> {
    Page<ContractProviderSupplier> findAllByProviderSupplier_IdProvider(String idSearch, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranch(String idSearch, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndProviderSupplier_IdProvider(String idBranch, String idSupplier, Pageable pageable);
    Page<ContractProviderSupplier> findAllByIsActive(IsActive isActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByIsActiveIn(List<IsActive> isActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByProviderSupplier_IdProviderAndIsActive(String idSearch, IsActive isActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByProviderSupplier_IdProviderAndIsActiveIn(String idSearch, List<IsActive> isActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndIsActive(String idSearch, IsActive isActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndIsActiveAndProviderSupplier_IsActive(String idBranch, IsActive contractStatus, Boolean providerIsActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndIsActiveInAndProviderSupplier_IsActive(String idBranch, List<IsActive> contractStatus, Boolean providerIsActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndProviderSupplier_IdProviderAndIsActiveIn(String idBranch, String idSupplier, List<IsActive> isActive, Pageable pageable);
    ContractProviderSupplier findTopByProviderSupplier_IdProviderAndIsActiveOrderByCreationDateDesc(String idCompany, IsActive isActive);
    List<ContractProviderSupplier> findAllByBranch_IdBranchAndIsActiveAndSubcontractPermissionIsTrue(String idBranch, IsActive isActive);
    Long countByBranch_IdBranchAndIsActiveAndFinishedIsFalse(String idBranch, IsActive isActive);
}
