package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.gateways.requests.users.NotificationRequestDto;
import bl.tech.realiza.gateways.responses.users.NotificationResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudNotificationImpl implements CrudNotification {
    @Override
    public NotificationResponseDto save(NotificationRequestDto notificationRequestDto) {
        return null;
    }

    @Override
    public Optional<NotificationResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<NotificationResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<NotificationResponseDto> update(String id, NotificationRequestDto notificationRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
