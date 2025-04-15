package bl.tech.realiza.gateways.repositories.documents.provider;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentProviderSubcontractorRepository extends JpaRepository<DocumentProviderSubcontractor, String> {
    Page<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);
    List<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(String idSearch);
    List<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(String idSearch, String groupName, Boolean isActive);
    Long countByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(String enterpriseId, String groupName, Boolean isActive);
    Long countByProviderSubcontractor_IdProviderAndDocumentationIsNotNullAndDocumentMatrix_SubGroup_Group_GroupName(String enterpriseId, String groupName);
    Long countByProviderSubcontractor_IdProviderAndStatusAndDocumentMatrix_SubGroup_Group_GroupName(String enterpriseId, Document.Status status, String groupName);
}
