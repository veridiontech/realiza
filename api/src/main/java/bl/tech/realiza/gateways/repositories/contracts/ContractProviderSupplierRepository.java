package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import static bl.tech.realiza.domains.contract.Contract.*;

public interface ContractProviderSupplierRepository extends JpaRepository<ContractProviderSupplier, String> {
    Page<ContractProviderSupplier> findAllByProviderSupplier_IdProvider(String idSearch, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranch(String idSearch, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndProviderSupplier_IdProvider(String idBranch, String idSupplier, Pageable pageable);
    Page<ContractProviderSupplier> findAllByIsActive(IsActive isActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByIsActiveIn(List<IsActive> isActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByProviderSupplier_IdProviderAndIsActive(String idSearch, IsActive isActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByProviderSupplier_IdProviderAndStatusIn(String idSearch, List<ContractStatusEnum> status, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndIsActive(String idSearch, IsActive isActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndIsActiveAndProviderSupplier_IsActive(String idBranch, IsActive contractStatus, Boolean providerIsActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndStatusInAndProviderSupplier_IsActive(String idBranch, List<ContractStatusEnum> status, Boolean providerIsActive, Pageable pageable);
    Page<ContractProviderSupplier> findAllByBranch_IdBranchAndProviderSupplier_IdProviderAndStatusIn(String idBranch, String idSupplier, List<ContractStatusEnum> status, Pageable pageable);
    ContractProviderSupplier findTopByProviderSupplier_IdProviderAndIsActiveOrderByCreationDateDesc(String idCompany, IsActive isActive);
    List<ContractProviderSupplier> findAllByBranch_IdBranchAndStatusAndSubcontractPermissionIsTrue(String idBranch, ContractStatusEnum status);
    Long countByBranch_IdBranchAndStatusAndFinishedIsFalse(String idBranch, ContractStatusEnum status);
    List<ContractProviderSupplier> findAllByResponsible_IdUser(String responsibleId);

    @Query("""
    SELECT COUNT(cps)
    FROM ContractProviderSupplier cps
    WHERE cps.branch.client.idClient = :idClient
        AND cps.status IN :status
""")
    Long countByClientIdAndIsActive(@Param("idClient") String clientId,
                                    @Param("status") List<ContractStatusEnum> status);
}
