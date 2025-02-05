package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ActivityRepository extends JpaRepository<Activity, String > {
    Collection<Activity> findAllByDeleteRequest(boolean b);
}
