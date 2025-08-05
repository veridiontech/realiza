package bl.tech.realiza.gateways.repositories.services.snapshots.employees;

import bl.tech.realiza.domains.services.snapshots.employees.EmployeeSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeSnapshotRepository extends JpaRepository<EmployeeSnapshot, String> {
}
