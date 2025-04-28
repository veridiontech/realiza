package bl.tech.realiza.gateways.repositories.contracts.serviceType;

import bl.tech.realiza.domains.contract.serviceType.ServiceTypeBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ServiceTypeBranchRepository extends JpaRepository<ServiceTypeBranch, String> {
    List<ServiceTypeBranch> findAllByBranch_IdBranch(String idBranch);
}
