package bl.tech.realiza.gateways.repositories.documents.client;

import bl.tech.realiza.domains.documents.client.DocumentClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentClientRepository extends JpaRepository<DocumentClient, String> {
}
