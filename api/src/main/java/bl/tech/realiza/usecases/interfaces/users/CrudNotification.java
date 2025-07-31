package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.gateways.requests.users.NotificationRequestDto;
import bl.tech.realiza.gateways.responses.users.NotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CrudNotification {
    NotificationResponseDto save(NotificationRequestDto notificationRequestDto);
    Optional<NotificationResponseDto> findOne(String id);
    Page<NotificationResponseDto> findAll(Pageable pageable);
    Optional<NotificationResponseDto> update(String id, NotificationRequestDto notificationRequestDto);
    void delete(String id);
    Page<NotificationResponseDto> findAllByUser(String idSearch, Pageable pageable);
    @Transactional
    @Async
    void saveUserNotificationForRealizaUsers(ItemManagement itemManagement);
    @Transactional
    @Async
    void saveProviderNotificationForRealizaUsers(ItemManagement itemManagement);
    @Transactional
    @Async
    void saveExpiredSupplierDocumentNotificationForSupplierUsers(DocumentProviderSupplier documentProviderSupplier);
    @Transactional
    @Async
    void saveExpiredSubcontractDocumentNotificationForSubcontractorUsers(DocumentProviderSubcontractor documentProviderSubcontractor);
    @Transactional
    @Async
    void saveExpiredEmployeeDocumentNotificationForManagerUsers(DocumentEmployee documentEmployee);
    @Transactional
    @Async
    void markAllNotificationsAsRead(String userId);
    @Transactional
    @Async
    void markOneNotificationAsRead(String notificationId);
    @Transactional
    @Async
    void saveValidationNotificationForRealizaUsers(String idDocumentation);
    @Transactional
    @Async
    void saveDocumentNotificationForRealizaUsers(ItemManagement solicitation);
    @Transactional
    @Async
    void saveContractNotificationForRealizaUsers(ItemManagement solicitation);
}
