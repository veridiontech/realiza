package bl.tech.realiza.gateways.repositories.documents.client;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.gateways.responses.clients.controlPanel.ControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.document.DocumentControlPanelResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentSummarizedResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentBranchRepository extends JpaRepository<DocumentBranch, String> {
    Page<DocumentBranch> findAllByBranch_IdBranchAndIsActiveIsTrue(String idSearch, Pageable pageable);
    List<DocumentBranch> findAllByBranch_IdBranchAndIsActiveIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(String idSearch, String groupName, Boolean isActive);
    List<DocumentBranch> findAllByBranch_IdBranchAndDocumentMatrix_TypeAndIsActive(String idSearch, String typeName, Boolean isActive);
    List<DocumentBranch> findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_IdDocumentGroup(String idSearch, String idDocumentGroup);
    Long countByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupName(String idSearch, String groupName);
    Long countByBranch_IdBranchAndDocumentationIsNotNullAndDocumentMatrix_SubGroup_Group_GroupName(String idSearch, String groupName);
    Long countByBranch_IdBranchAndStatusAndDocumentMatrix_SubGroup_Group_GroupName(String idSearch, Document.Status status, String groupName);

    List<DocumentBranch> findAllByBranch_IdBranch(String id);

    @Query("""
    SELECT d.type
    FROM DocumentBranch d
    GROUP BY d.type
""")
    List<String> findAllTypes();

    @Query("""
    SELECT new bl.tech.realiza.gateways.responses.clients.controlPanel.document.DocumentControlPanelResponseDto(
        d.idDocumentation,
        d.title,
        d.expirationDateAmount,
        d.expirationDateUnit,
        d.type
    )
    FROM DocumentBranch d
    WHERE d.branch.idBranch = :branchId
        AND d.isActive = true
""")
    List<DocumentControlPanelResponseDto> findAllControlPanelDocumentResponseDtoByBranch_IdBranch(
            @Param("branchId") String id
    );

    @Query("""
    SELECT new bl.tech.realiza.gateways.responses.documents.DocumentSummarizedResponseDto(
        d.idDocumentation,
        d.title
    )
    FROM DocumentBranch d
    JOIN d.documentMatrix m
    WHERE d.branch.idBranch = :idBranch
      AND LOWER(m.type) = LOWER(:documentTypeName)
      AND d.isActive = :isSelected
    ORDER BY LOWER(d.title)
""")
    List<DocumentSummarizedResponseDto> findFilteredDocumentsSimplified(
            @Param("idBranch") String idBranch,
            @Param("documentTypeName") String documentTypeName,
            @Param("isSelected") Boolean isSelected
    );

}
