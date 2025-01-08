package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequirementRepository extends JpaRepository<Requirement, String> {
}
