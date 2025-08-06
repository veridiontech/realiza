package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Collection<User> findAllByIsActive(Boolean b);
    Collection<User> findAllByDeleteRequest(Boolean b);
    User findByEmailAndIsActive(String email, Boolean isActive);
    User findByForgotPasswordCode(String forgotPasswordCode);
    Optional<User> findByEmailAndForgotPasswordCodeAndIsActiveIsTrue(String userEmail, String forgotPasswordCode);
    List<User> findAllByForgotPasswordCodeIsNotNull();

    Page<User> findAllByContractsIsEmpty(Boolean isContractEmpty, Pageable pageable);
}
