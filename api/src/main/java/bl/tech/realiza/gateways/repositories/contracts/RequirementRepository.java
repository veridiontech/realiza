package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface RequirementRepository extends JpaRepository<Requirement, String> {
    Collection<Requirement> findAllByDeleteRequest(boolean b);
}
