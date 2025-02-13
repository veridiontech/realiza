package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractProviderSubcontractorRepository extends JpaRepository<ContractProviderSubcontractor, String> {
    Page<ContractProviderSubcontractor> findAllByProviderSubcontractor_IdProvider(String idSearch, Pageable pageable);
    Page<ContractProviderSubcontractor> findAllByProviderSupplier_IdProvider(String idSearch, Pageable pageable);
    Page<ContractProviderSubcontractor> findAllByIsActiveIsTrue(Pageable pageable);
    Page<ContractProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);
    Page<ContractProviderSubcontractor> findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);
}
