package bl.tech.realiza.gateways.repositories.services.snapshots.clients;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.clients.ClientSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ClientSnapshotRepository extends JpaRepository<ClientSnapshot, String> {
    Optional<ClientSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idClient, Date from, SnapshotFrequencyEnum frequency);
    Page<ClientSnapshot> findAllById_SnapshotDateBeforeAndId_Frequency(Date from, SnapshotFrequencyEnum snapshotFrequencyEnum, Pageable pageable);
}
