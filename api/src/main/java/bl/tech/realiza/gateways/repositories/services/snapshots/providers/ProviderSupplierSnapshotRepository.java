package bl.tech.realiza.gateways.repositories.services.snapshots.providers;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface ProviderSupplierSnapshotRepository extends JpaRepository<ProviderSupplierSnapshot, String> {
    Optional<ProviderSupplierSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idProvider, Date from, SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(ps)
    FROM ProviderSupplierSnapshot ps
    JOIN ps.contractsSupplier cs
    WHERE cs.branch.client.id = :clientId
""")
    Long countByClientId(@Param("clientId") String clientId);
}
