package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.users.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProviderRepository extends JpaRepository<UserProvider, String> {
}
