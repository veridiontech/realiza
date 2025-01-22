package bl.tech.realiza.gateways.repositories.documents.provider;

import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentProviderSubcontractorRepository extends JpaRepository<DocumentProviderSubcontractor, String> {
    Page<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProvider(String idSearch, Pageable pageable);
}
