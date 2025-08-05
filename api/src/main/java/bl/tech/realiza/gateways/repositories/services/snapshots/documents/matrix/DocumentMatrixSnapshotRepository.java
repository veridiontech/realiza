package bl.tech.realiza.gateways.repositories.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.services.snapshots.documents.matrix.DocumentMatrixSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentMatrixSnapshotRepository extends JpaRepository<DocumentMatrixSnapshot, String> {
}
