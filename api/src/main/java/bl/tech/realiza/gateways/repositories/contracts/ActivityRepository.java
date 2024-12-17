package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contracts.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, String> {
}
