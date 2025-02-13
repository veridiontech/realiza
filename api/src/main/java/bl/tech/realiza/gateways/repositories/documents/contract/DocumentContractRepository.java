package bl.tech.realiza.gateways.repositories.documents.contract;

import bl.tech.realiza.domains.documents.contract.DocumentContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentContractRepository extends JpaRepository<DocumentContract, String> {
    Page<DocumentContract> findAllByContract_IdContract(String idSearch, Pageable pageable);
    List<DocumentContract> findAllByContract_IdContract(String idSearch);
}
