package bl.tech.realiza.gateways.repositories.users.profile;

import bl.tech.realiza.domains.user.security.ProfileRepo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepoRepository extends JpaRepository<ProfileRepo, String> {
    Boolean existsByName(String name);
}
