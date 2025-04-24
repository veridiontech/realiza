package bl.tech.realiza.gateways.repositories.contracts.activity;

import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityDocumentRepository extends JpaRepository<ActivityDocuments, String> {
    List<ActivityDocuments> findAllByActivity_IdActivity(String idActivity);
}
