package bl.tech.realiza.gateways.repositories.services.snapshots.contract;

import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSupplierSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ContractProviderSupplierSnapshotRepository extends JpaRepository<ContractProviderSupplierSnapshot, String> {
    Optional<ContractProviderSupplierSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idContract, Date from, SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(cps)
    FROM ContractProviderSupplierSnapshot cps
    WHERE cps.branch.client.id = :idClient
        AND cps.status IN :status
""")
    Long countByClientIdAndStatusIn(@Param("idClient") String clientId,
                                    @Param("status") List<ContractStatusEnum> status);
}
