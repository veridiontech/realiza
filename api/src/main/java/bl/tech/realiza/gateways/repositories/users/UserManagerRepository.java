package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.UserManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserManagerRepository extends JpaRepository<UserManager, String> {
    Page<UserManager> findAllByIsActiveIsTrue(Pageable pageable);
}
