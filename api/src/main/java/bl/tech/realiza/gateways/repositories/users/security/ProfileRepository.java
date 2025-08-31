package bl.tech.realiza.gateways.repositories.users.security;

import bl.tech.realiza.domains.user.security.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    List<Profile> findAllByClient_IdClient(String clientId);
}
