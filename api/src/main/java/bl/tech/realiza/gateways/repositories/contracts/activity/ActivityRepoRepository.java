package bl.tech.realiza.gateways.repositories.contracts.activity;

import bl.tech.realiza.domains.contract.activity.ActivityRepo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepoRepository extends JpaRepository<ActivityRepo, String> {
    List<ActivityRepo> findAllByTitle(String name);
}
