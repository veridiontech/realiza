package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    Collection<User> findAllByIsActive(boolean b);
    Collection<User> findAllByDeleteRequest(boolean b);
}
