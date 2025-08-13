package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractProviderSubcontractorRepository extends JpaRepository<ContractProviderSubcontractor, String> {
    Page<ContractProviderSubcontractor> findAllByIsActiveIsTrue(Pageable pageable);
    Page<ContractProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);
    Page<ContractProviderSubcontractor> findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);
    List<ContractProviderSubcontractor> findAllByContractProviderSupplier_IdContract(String contractId);
    Page<ContractProviderSubcontractor> findAllByIsActiveIsNot(Pageable pageable, ContractStatusEnum contractStatusEnum);
    List<ContractProviderSubcontractor> findAllByContractProviderSupplier_Branch_Client_IdClientAndStatusIsNot(String idClient, ContractStatusEnum contractStatus);
}
