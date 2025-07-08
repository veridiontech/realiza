package bl.tech.realiza.gateways.repositories.users;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    Page<Notification> findAllByUser_IdUser(String idSearch, Pageable pageable);
    List<Notification> findAllByUser_IdUser(String idSearch);

    @Query("SELECT n FROM Notification n " +
            "WHERE n.user.idUser = :userId " +
            "AND (n.isRead = false OR (n.isRead = true AND n.readAt > :timeThreshold))")
    Page<Notification> findByUserAndRecentReadMark(String userId, LocalDateTime timeThreshold, Pageable pageable);
}
