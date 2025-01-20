package bl.tech.realiza.gateways.repositories.documents.client;

import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.client.DocumentClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentClientRepository extends JpaRepository<DocumentClient, String> {
    Page<DocumentClient> findAllByClient_IdClient(String idSearch, Pageable pageable);
}
