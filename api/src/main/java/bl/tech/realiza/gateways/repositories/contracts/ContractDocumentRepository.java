package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.ContractDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractDocumentRepository extends JpaRepository<ContractDocument, String> {
}
