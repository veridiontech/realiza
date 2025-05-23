package bl.tech.realiza.gateways.controllers.interfaces.users;

import bl.tech.realiza.gateways.requests.users.NotificationRequestDto;
import bl.tech.realiza.gateways.responses.users.NotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface NotificationController {
    ResponseEntity<NotificationResponseDto> createNotification(NotificationRequestDto notificationRequestDto);
    ResponseEntity<Optional<NotificationResponseDto>> getOneNotification(String id);
    ResponseEntity<Page<NotificationResponseDto>> getAllNotifications(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<NotificationResponseDto>> updateNotification(String id, NotificationRequestDto notificationRequestDto);
    ResponseEntity<Void> deleteNotification(String id);
    ResponseEntity<Page<NotificationResponseDto>> getAllNotificationsByUser(int page, int size, String sort, Sort.Direction direction, String idSearch);
    ResponseEntity<Void> markOneNotificationAsRead(String notificationId);
    ResponseEntity<Void> markAllNotificationsFromUserAsRead(String userId);
}
