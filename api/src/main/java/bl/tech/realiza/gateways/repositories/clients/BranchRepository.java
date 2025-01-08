package bl.tech.realiza.gateways.repositories.clients;

import bl.tech.realiza.domains.clients.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, String> {
    Optional<Branch> findById(String id);
    Branch save(Branch branch);
    void deleteById(String id);
}
