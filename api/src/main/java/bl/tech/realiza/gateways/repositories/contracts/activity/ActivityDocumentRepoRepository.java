package bl.tech.realiza.gateways.repositories.contracts.activity;

import bl.tech.realiza.domains.contract.activity.ActivityDocumentsRepo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityDocumentRepoRepository extends JpaRepository<ActivityDocumentsRepo, String> {
}
