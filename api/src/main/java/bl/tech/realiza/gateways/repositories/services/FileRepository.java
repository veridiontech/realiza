package bl.tech.realiza.gateways.repositories.services;

import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.services.FileDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileDocument, String> {
    Page<FileDocument> findAllByCanBeOverwritten(Boolean canBeOverwritten, Pageable pageable);

    @Query(
    value = """
    SELECT DISTINCT f
    FROM FileDocument f
    JOIN f.document d
    JOIN d.contractDocuments cd
    WHERE (cd.contract.status IN :status)
        AND (cd.contract.endDate IS NOT NULL)
        AND (cd.contract.endDate <= :endDate)
        AND (f.deleted = false)
        AND (f.creationDate <= :endDate)
""",
    countQuery = """
    SELECT COUNT(DISTINCT f)
    FROM FileDocument f
    JOIN f.document d
    JOIN d.contractDocuments cd
    WHERE (cd.contract.status IN :status)
        AND (cd.contract.endDate IS NOT NULL)
        AND (cd.contract.endDate <= :endDate)
        AND (f.deleted = false)
        AND (f.creationDate <= :endDate)
""")
    Page<FileDocument> findAllUploadedBeforeThanAndNotDeletedAndContractStatuses(@Param("endDate") LocalDateTime contractEndDate,
                                                                                 @Param("statuses") List<ContractStatusEnum> statuses,
                                                                                 Pageable pageable);
}
