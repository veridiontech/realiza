package bl.tech.realiza.repositories;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

    Page<Document> findAllByStatusAndLastCheckAfter(Document.Status status, LocalDateTime lastCheck, Pageable pageable);

    Page<Document> findAllByStatus(Document.Status status, Pageable pageable);

    @Query("SELECT d FROM Document d JOIN d.contractDocuments cd JOIN cd.contract c WHERE d.status = :status AND c.status NOT IN :contractStatuses")
    Page<Document> findAllByStatusAndNotInContractStatuses(@Param("status") Document.Status status, @Param("contractStatuses") List<ContractStatusEnum> contractStatuses, Pageable pageable);

    Optional<Document> findTopByDocumentMatrixAndVersionDateBeforeOrderByVersionDateDesc(DocumentMatrix matrix, LocalDateTime versionDate);
}
