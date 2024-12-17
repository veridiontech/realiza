package bl.tech.realiza.gateways.repositories.documents.client;

import bl.tech.realiza.domains.documents.client.DocumentsClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentsClientRepository extends JpaRepository<DocumentsClient, String> {
}
