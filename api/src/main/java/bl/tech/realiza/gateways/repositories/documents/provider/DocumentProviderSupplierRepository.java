package bl.tech.realiza.gateways.repositories.documents.provider;

import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentProviderSupplierRepository extends JpaRepository<DocumentProviderSupplier, String> {
    Page<DocumentProviderSupplier> findAllByProviderSupplier_IdProvider(String idSearch, Pageable pageable);
    List<DocumentProviderSupplier> findAllByProviderSupplier_IdProvider(String idSearch);
    List<DocumentProviderSupplier> findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupName(String idSearch, String groupName);
}
