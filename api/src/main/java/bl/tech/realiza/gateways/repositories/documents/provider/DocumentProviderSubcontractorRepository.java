package bl.tech.realiza.gateways.repositories.documents.provider;

import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentProviderSubcontractorRepository extends JpaRepository<DocumentProviderSubcontractor, String> {
    Page<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProvider(String idSearch, Pageable pageable);
    List<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProvider(String idSearch);
    List<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupName(String idSearch, String groupName);
}
