package bl.tech.realiza.gateways.repositories.services.snapshots.providers;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface ProviderSubcontractorSnapshotRepository extends JpaRepository<ProviderSubcontractorSnapshot, String> {
    Optional<ProviderSubcontractorSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idProvider, Date from, SnapshotFrequencyEnum frequency);
}
