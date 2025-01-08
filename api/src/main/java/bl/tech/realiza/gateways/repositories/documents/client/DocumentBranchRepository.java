package bl.tech.realiza.gateways.repositories.documents.client;

import bl.tech.realiza.domains.documents.client.DocumentBranch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentBranchRepository extends JpaRepository<DocumentBranch, String> {
}
