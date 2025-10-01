package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.ContractDocument;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractDocumentRepository extends JpaRepository<ContractDocument, String> {
    Page<ContractDocument> findAllByContract_IsActiveIsNot(Pageable pageable, ContractStatusEnum contractStatusEnum);
    List<ContractDocument> findAllByStatus(Document.Status status);
}
