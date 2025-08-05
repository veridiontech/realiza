package bl.tech.realiza.gateways.repositories.services.snapshots.documents.employee;

import bl.tech.realiza.domains.services.snapshots.documents.employee.DocumentEmployeeSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentEmployeeSnapshotRepository extends JpaRepository<DocumentEmployeeSnapshot, String> {
}
