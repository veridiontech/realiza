package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.UserManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserManagerRepository extends JpaRepository<UserManager, String> {
}
