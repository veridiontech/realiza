package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.users.UserClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserClientRepository extends JpaRepository<UserClient, String> {
}
