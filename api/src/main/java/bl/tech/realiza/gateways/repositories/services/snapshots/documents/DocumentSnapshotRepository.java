package bl.tech.realiza.gateways.repositories.services.snapshots.documents;

import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentSnapshotRepository extends JpaRepository<DocumentSnapshot, String> {
}
