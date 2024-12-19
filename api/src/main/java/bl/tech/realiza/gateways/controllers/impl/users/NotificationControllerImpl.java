package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.NotificationController;
import bl.tech.realiza.gateways.requests.users.NotificationRequestDto;
import bl.tech.realiza.gateways.responses.users.NotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class NotificationControllerImpl implements NotificationController {
    @Override
    public ResponseEntity<NotificationResponseDto> createNotification(NotificationRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<NotificationResponseDto>> getOneNotification(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<NotificationResponseDto>> getAllNotifications(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<NotificationResponseDto>> updateNotification(NotificationRequestDto branchRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteNotification(String id) {
        return null;
    }
}
