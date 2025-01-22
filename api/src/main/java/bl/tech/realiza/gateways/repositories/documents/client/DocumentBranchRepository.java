package bl.tech.realiza.gateways.repositories.documents.client;

import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentBranchRepository extends JpaRepository<DocumentBranch, String> {
    Page<DocumentBranch> findAllByBranch_IdBranch(String idSearch, Pageable pageable);
}
