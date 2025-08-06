package bl.tech.realiza.gateways.repositories.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.documents.matrix.DocumentMatrixGroupSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface DocumentMatrixGroupSnapshotRepository extends JpaRepository<DocumentMatrixGroupSnapshot, String> {
    Optional<DocumentMatrixGroupSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idDocumentGroup, Date from, SnapshotFrequencyEnum frequency);
}
