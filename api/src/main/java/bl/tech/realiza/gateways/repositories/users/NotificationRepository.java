package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findAllByUser_IdUser(String idSearch, Pageable pageable);
    List<Notification> findAllByUser_IdUser(String idSearch);
}
