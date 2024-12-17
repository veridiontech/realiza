package bl.tech.realiza.gateways.repositories.documents.client;

import bl.tech.realiza.domains.documents.client.DocumentsBranch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentsBranchRepository extends JpaRepository<DocumentsBranch, String> {
}
