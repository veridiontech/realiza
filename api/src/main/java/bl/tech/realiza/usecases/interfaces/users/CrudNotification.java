package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.requests.users.NotificationRequestDto;
import bl.tech.realiza.gateways.responses.users.NotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudNotification {
    NotificationResponseDto save(NotificationRequestDto notificationRequestDto);
    Optional<NotificationResponseDto> findOne(String id);
    Page<NotificationResponseDto> findAll(Pageable pageable);
    Optional<NotificationResponseDto> update(NotificationRequestDto notificationRequestDto);
    void delete(String id);
    Page<NotificationResponseDto> findAllByUser(String idSearch, Pageable pageable);
}
