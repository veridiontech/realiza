package bl.tech.realiza.gateways.repositories.clients;

import bl.tech.realiza.domains.clients.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface BranchRepository extends JpaRepository<Branch, String> {
    Page<Branch> findAllByClient_IdClientAndIsActiveIsTrue(String client, Pageable pageable);
    Page<Branch> findAllByIsActiveIsTrue(Pageable pageable);
    Page<Branch> findAllByCenter_IdCenter(String idCenter, Pageable pageable);
    Branch findFirstByClient_IdClientOrderByCreationDateAsc(String idClient);

    Collection<Branch> findAllByIsActive(boolean b);
}
