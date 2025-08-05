package bl.tech.realiza.gateways.repositories.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.services.snapshots.documents.matrix.DocumentMatrixSubgroupSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentMatrixSubgroupSnapshotRepository extends JpaRepository<DocumentMatrixSubgroupSnapshot, String> {
}
