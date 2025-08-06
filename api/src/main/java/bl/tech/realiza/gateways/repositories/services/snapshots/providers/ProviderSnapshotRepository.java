package bl.tech.realiza.gateways.repositories.services.snapshots.providers;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ProviderSnapshotRepository extends JpaRepository<ProviderSnapshot, String> {
    Page<ProviderSnapshot> findAllById_SnapshotDateBeforeAndId_Frequency(Date from, SnapshotFrequencyEnum snapshotFrequencyEnum, Pageable pageable);
}
