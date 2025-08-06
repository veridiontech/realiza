package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractProviderSubcontractorRepository extends JpaRepository<ContractProviderSubcontractor, String> {
    Page<ContractProviderSubcontractor> findAllByProviderSubcontractor_IdProvider(String idSearch, Pageable pageable);
    Page<ContractProviderSubcontractor> findAllByProviderSupplier_IdProvider(String idSearch, Pageable pageable);
    Page<ContractProviderSubcontractor> findAllByIsActiveIsTrue(Pageable pageable);
    Page<ContractProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);
    Page<ContractProviderSubcontractor> findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);
    ContractProviderSubcontractor findTopByProviderSubcontractor_IdProviderOrderByCreationDateDesc(String idCompany);
    List<ContractProviderSubcontractor> findAllByContractProviderSupplier_IdContract(String contractId);

    Page<ContractProviderSubcontractor> findAllByIsActiveIsNot(Pageable pageable, ContractStatusEnum contractStatusEnum);
}
