package bl.tech.realiza.gateways.repositories.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.documents.matrix.DocumentMatrixSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface DocumentMatrixSnapshotRepository extends JpaRepository<DocumentMatrixSnapshot, String> {
    Optional<DocumentMatrixSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idDocument, Date from, SnapshotFrequencyEnum frequency);
}
