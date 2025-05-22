package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.requests.users.NotificationRequestDto;
import bl.tech.realiza.gateways.responses.users.NotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;

public interface CrudNotification {
    NotificationResponseDto save(NotificationRequestDto notificationRequestDto);
    Optional<NotificationResponseDto> findOne(String id);
    Page<NotificationResponseDto> findAll(Pageable pageable);
    Optional<NotificationResponseDto> update(String id, NotificationRequestDto notificationRequestDto);
    void delete(String id);
    Page<NotificationResponseDto> findAllByUser(String idSearch, Pageable pageable);
    @Async
    void saveUserNotificationForManagerUsers(ItemManagement itemManagement);
    @Async
    void saveProviderNotificationForManagerUsers(ItemManagement itemManagement);
}
