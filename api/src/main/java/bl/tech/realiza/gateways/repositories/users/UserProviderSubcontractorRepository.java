package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.Notification;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProviderSubcontractorRepository extends JpaRepository<UserProviderSubcontractor, String> {
    Page<UserProviderSubcontractor> findAllByProviderSubcontractor_IdProvider(String idSearch, Pageable pageable);
}
