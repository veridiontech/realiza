package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.*;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.providers.ProviderRepository;
import bl.tech.realiza.gateways.repositories.users.*;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.requests.users.NotificationRequestDto;
import bl.tech.realiza.gateways.responses.users.NotificationResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudNotificationImpl implements CrudNotification {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserManagerRepository userManagerRepository;
    private final ProviderRepository providerRepository;
    private final UserProviderSupplierRepository userProviderSupplierRepository;
    private final UserProviderSubcontractorRepository userProviderSubcontractorRepository;

    @Override
    public NotificationResponseDto save(NotificationRequestDto notificationRequestDto) {
        if (notificationRequestDto.getUser() == null || notificationRequestDto.getUser().isEmpty()) {
            throw new BadRequestException("Invalid user");
        }

        Optional<User> userOptional = userRepository.findById(notificationRequestDto.getUser());
        User user = userOptional.orElseThrow(() -> new NotFoundException("User not found"));

        Notification newNotification = Notification.builder()
                .title(notificationRequestDto.getTitle())
                .description(notificationRequestDto.getDescription())
                .isRead(notificationRequestDto.getIsRead())
                .user(user)
                .build();

        Notification savedNotification = notificationRepository.save(newNotification);

        return NotificationResponseDto.builder()
                .idNotification(savedNotification.getIdNotification())
                .title(savedNotification.getTitle())
                .description(savedNotification.getDescription())
                .isRead(savedNotification.getIsRead())
                .user(savedNotification.getUser().getIdUser())
                .build();
    }

    @Override
    public Optional<NotificationResponseDto> findOne(String id) {
        Optional<Notification> notificationOptional = notificationRepository.findById(id);

        Notification notification = notificationOptional.orElseThrow(() -> new NotFoundException("Notification not found"));

        NotificationResponseDto notificationResponse = NotificationResponseDto.builder()
                .idNotification(notification.getIdNotification())
                .title(notification.getTitle())
                .description(notification.getDescription())
                .isRead(notification.getIsRead())
                .user(notification.getUser().getIdUser())
                .build();

        return Optional.of(notificationResponse);
    }

    @Override
    public Page<NotificationResponseDto> findAll(Pageable pageable) {
        Page<Notification> notificationPage = notificationRepository.findAll(pageable);

        return notificationPage.map(
                notification -> NotificationResponseDto.builder()
                        .idNotification(notification.getIdNotification())
                        .title(notification.getTitle())
                        .description(notification.getDescription())
                        .isRead(notification.getIsRead())
                        .user(notification.getUser().getIdUser())
                        .build()
        );
    }

    @Override
    public Optional<NotificationResponseDto> update(String id, NotificationRequestDto notificationRequestDto) {
        Optional<Notification> notificationOptional = notificationRepository.findById(id);

        Notification notification = notificationOptional.orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.setTitle(notificationRequestDto.getTitle() != null ? notificationRequestDto.getTitle() : notification.getTitle());
        notification.setDescription(notificationRequestDto.getDescription() != null ? notificationRequestDto.getDescription() : notification.getDescription());
        notification.setIsRead(notificationRequestDto.getIsRead() != null ? notificationRequestDto.getIsRead() : notification.getIsRead());

        Notification savedNotification = notificationRepository.save(notification);

        NotificationResponseDto notificationResponse = NotificationResponseDto.builder()
                .idNotification(savedNotification.getIdNotification())
                .title(savedNotification.getTitle())
                .description(savedNotification.getDescription())
                .isRead(savedNotification.getIsRead())
                .user(savedNotification.getUser().getIdUser())
                .build();

        return Optional.of(notificationResponse);
    }

    @Override
    public void delete(String id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public Page<NotificationResponseDto> findAllByUser(String idSearch, Pageable pageable) {
        Page<Notification> notificationPage = notificationRepository.findAllByUser_IdUser(idSearch, pageable);

        return notificationPage.map(
                notification -> NotificationResponseDto.builder()
                        .idNotification(notification.getIdNotification())
                        .title(notification.getTitle())
                        .description(notification.getDescription())
                        .isRead(notification.getIsRead())
                        .user(notification.getUser().getIdUser())
                        .build()
        );
    }

    @Override
    public void saveUserNotificationForRealizaUsers(ItemManagement itemManagement) {
        List<Notification> notifications = new ArrayList<>();
        String title = null;
        String description = null;

        User user = itemManagement.getNewUser();

        switch (itemManagement.getSolicitationType()) {
            case CREATION -> {
                title = "Cadastro de novo usuário solicitado";
                description = "Solicitação de cadastro do usuário " + user.getFirstName() + " " + user.getSurname();
            }
            case INACTIVATION -> {
                title = "Inativação de usuário solicitada";
                description = "Solicitação de inativação do usuário " + user.getFirstName() + " " + user.getSurname();
            }
        }

        String finalTitle = title;
        String finalDescription = description;

        List<UserManager> users = userManagerRepository.findAll();

        users.forEach(
                userManager -> {
                    notifications.add(
                            Notification.builder()
                                    .user(userManager)
                                    .title(finalTitle)
                                    .description(finalDescription)
                                    .build()
                    );
                }
        );
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void saveProviderNotificationForRealizaUsers(ItemManagement itemManagement) {
        List<Notification> notifications = new ArrayList<>();
        String title = null;
        String description = null;

        Provider provider = itemManagement.getNewProvider();

        switch (itemManagement.getSolicitationType()) {
            case CREATION -> {
                title = "Cadastro de novo fornecedor solicitado";
                description = "Solicitação de cadastro do fornecedor " + provider.getCorporateName();
            }
            case INACTIVATION -> {
                title = "Inativação de fornecedor solicitada";
                description = "Solicitação de inativação do fornecedor " + provider.getCorporateName();
            }
        }

        String finalTitle = title;
        String finalDescription = description;

        List<UserManager> users = userManagerRepository.findAll();

        users.forEach(
                userManager -> {
                    notifications.add(
                            Notification.builder()
                                    .user(userManager)
                                    .title(finalTitle)
                                    .description(finalDescription)
                                    .build()
                    );
                }
        );
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void saveExpiredSupplierDocumentNotificationForSupplierUsers(DocumentProviderSupplier documentProviderSupplier) {
        int page = 0;
        int size = 50;
        boolean hasNext;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = documentProviderSupplier.getExpirationDate().format(formatter);

        String title = "Vencimento de documento";
        String description = documentProviderSupplier.getTitle() + " venceu dia " + formattedDate + " e deve ser renovado";

        Page<UserProviderSupplier> managers;
        List<Notification> notifications = new ArrayList<>(List.of());
        do {
            managers = userProviderSupplierRepository.findAllByProviderSupplier_IdProvider(documentProviderSupplier.getProviderSupplier().getIdProvider(), PageRequest.of(page, size));
            managers.forEach(
                    manager -> {
                        notifications.add(
                                Notification.builder()
                                        .user(manager)
                                        .title(title)
                                        .description(description)
                                        .build()
                        );
                    }
            );

            hasNext = managers.hasNext();
            page++;
        } while (hasNext);

        notificationRepository.saveAll(notifications);
    }

    @Override
    public void saveExpiredSubcontractDocumentNotificationForSubcontractorUsers(DocumentProviderSubcontractor documentProviderSubcontractor) {
        int page = 0;
        int size = 50;
        boolean hasNext;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = documentProviderSubcontractor.getExpirationDate().format(formatter);

        String title = "Vencimento de documento";
        String description = documentProviderSubcontractor.getTitle() + " venceu dia " + formattedDate + " e deve ser renovado";

        Page<UserProviderSubcontractor> managers;
        List<Notification> notifications = new ArrayList<>(List.of());
        do {
            managers = userProviderSubcontractorRepository.findAllByProviderSubcontractor_IdProvider(documentProviderSubcontractor.getProviderSubcontractor().getIdProvider(), PageRequest.of(page, size));
            managers.forEach(
                    manager -> {
                        notifications.add(
                                Notification.builder()
                                        .user(manager)
                                        .title(title)
                                        .description(description)
                                        .build()
                        );
                    }
            );

            hasNext = managers.hasNext();
            page++;
        } while (hasNext);

        notificationRepository.saveAll(notifications);
    }

    @Override
    public void saveExpiredEmployeeDocumentNotificationForManagerUsers(DocumentEmployee documentEmployee) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = documentEmployee.getExpirationDate().format(formatter);

        String title = "Vencimento de documento";
        String description = documentEmployee.getTitle() + " venceu dia " + formattedDate + " e deve ser renovado";

        String providerId = null;
        boolean isSupplier = false;

        if (documentEmployee.getEmployee().getSupplier() != null) {
            providerId = documentEmployee.getEmployee().getSupplier().getIdProvider();
            isSupplier = true;
        } else if (documentEmployee.getEmployee().getSubcontract() != null) {
            providerId = documentEmployee.getEmployee().getSubcontract().getIdProvider();
        }

        if (providerId == null) return;

        int page = 0;
        int size = 50;
        boolean hasNext;
        List<Notification> notifications = new ArrayList<>();

        do {
            Page<? extends User> managers = isSupplier
                    ? userProviderSupplierRepository.findAllByProviderSupplier_IdProvider(providerId, PageRequest.of(page, size))
                    : userProviderSubcontractorRepository.findAllByProviderSubcontractor_IdProvider(providerId, PageRequest.of(page, size));

            managers.forEach(manager -> {
                notifications.add(
                        Notification.builder()
                                .user(manager)
                                .title(title)
                                .description(description)
                                .build()
                );
            });

            hasNext = managers.hasNext();
            page++;
        } while (hasNext);

        notificationRepository.saveAll(notifications);
    }

    @Override
    public void markAllNotificationsAsRead(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Notification> notifications = notificationRepository.findAllByUser_IdUser(user.getIdUser());

        notifications.forEach(
                notification -> {
                    notification.setIsRead(true);
                }
        );

        notificationRepository.saveAll(notifications);
    }

    @Override
    public void markOneNotificationAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.setIsRead(true);

        notificationRepository.save(notification);
    }
}
