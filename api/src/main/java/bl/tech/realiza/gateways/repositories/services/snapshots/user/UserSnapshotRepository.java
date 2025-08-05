package bl.tech.realiza.gateways.repositories.services.snapshots.user;

import bl.tech.realiza.domains.services.snapshots.user.UserSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSnapshotRepository extends JpaRepository<UserSnapshot, String> {
}
