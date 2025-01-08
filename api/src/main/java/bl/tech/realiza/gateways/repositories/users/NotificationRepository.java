package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.user.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {
}
