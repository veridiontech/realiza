package bl.tech.realiza.gateways.repositories.contracts.activity;

import bl.tech.realiza.domains.contract.activity.ActivityRepo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepoRepository extends JpaRepository<ActivityRepo, String> {
}
