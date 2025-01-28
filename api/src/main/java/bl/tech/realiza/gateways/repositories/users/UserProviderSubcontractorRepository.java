package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.Notification;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProviderSubcontractorRepository extends JpaRepository<UserProviderSubcontractor, String> {
    Page<UserProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndRole(String idSearch, User.Role role, Pageable pageable);
}
