package bl.tech.realiza.gateways.repositories.documents.client;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentBranchRepository extends JpaRepository<DocumentBranch, String> {
    Page<DocumentBranch> findAllByBranch_IdBranchAndIsActiveIsTrue(String idSearch, Pageable pageable);
    List<DocumentBranch> findAllByBranch_IdBranchAndIsActiveIsTrue(String idSearch);
    List<DocumentBranch> findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(String idSearch, String groupName, Boolean isActive);
    List<DocumentBranch> findAllByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_IdDocumentGroup(String idSearch, String idDocumentGroup);
    Long countByBranch_IdBranchAndDocumentMatrix_SubGroup_Group_GroupName(String idSearch, String groupName);
    Long countByBranch_IdBranchAndDocumentationIsNotNullAndDocumentMatrix_SubGroup_Group_GroupName(String idSearch, String groupName);
    Long countByBranch_IdBranchAndStatusAndDocumentMatrix_SubGroup_Group_GroupName(String idSearch, Document.Status status, String groupName);

    List<DocumentBranch> findAllByBranch_IdBranch(String id);
}
