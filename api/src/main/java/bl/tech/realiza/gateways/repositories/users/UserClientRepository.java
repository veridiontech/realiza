package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserClientRepository extends JpaRepository<UserClient, String> {
    Page<UserClient> findAllByIsActiveIsTrue(Pageable pageable);
    Page<UserClient> findAllByBranch_IdBranchAndRoleAndIsActiveIsTrue(String idSearch, User.Role role, Pageable pageable);
}
