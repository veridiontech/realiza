package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
