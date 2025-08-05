package bl.tech.realiza.gateways.repositories.services.snapshots.clients;

import bl.tech.realiza.domains.services.snapshots.clients.ClientSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientSnapshotRepository extends JpaRepository<ClientSnapshot, String> {
}
