package bl.tech.realiza.gateways.repositories.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.services.snapshots.documents.matrix.DocumentMatrixGroupSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentMatrixGroupSnapshotRepository extends JpaRepository<DocumentMatrixGroupSnapshot, String> {
}
