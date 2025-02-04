package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.user.Notification;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.users.NotificationRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.users.NotificationRequestDto;
import bl.tech.realiza.gateways.responses.users.NotificationResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudNotificationImpl implements CrudNotification {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

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

        NotificationResponseDto notificationResponse = NotificationResponseDto.builder()
                .idNotification(savedNotification.getIdNotification())
                .title(savedNotification.getTitle())
                .description(savedNotification.getDescription())
                .isRead(savedNotification.getIsRead())
                .user(savedNotification.getUser().getIdUser())
                .build();

        return notificationResponse;
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

        Page<NotificationResponseDto> notificationResponseDtoPage = notificationPage.map(
                notification -> NotificationResponseDto.builder()
                        .idNotification(notification.getIdNotification())
                        .title(notification.getTitle())
                        .description(notification.getDescription())
                        .isRead(notification.getIsRead())
                        .user(notification.getUser().getIdUser())
                        .build()
        );

        return notificationResponseDtoPage;
    }

    @Override
    public Optional<NotificationResponseDto> update(String id, NotificationRequestDto notificationRequestDto) {
        Optional<Notification> notificationOptional = notificationRepository.findById(id);

        Notification notification = notificationOptional.orElseThrow(() -> new NotFoundException("Notification not found"));

        notification.setTitle(notificationRequestDto.getTitle() != null ? notificationRequestDto.getTitle() : notification.getTitle());
        notification.setDescription(notificationRequestDto.getDescription() != null ? notificationRequestDto.getDescription() : notification.getDescription());
        notification.setIsRead(notificationRequestDto.getIsRead() != null ? notificationRequestDto.getIsRead() : notification.getIsRead());
        notification.setIsActive(notificationRequestDto.getIsActive() != null ? notificationRequestDto.getIsActive() : notification.getIsActive());

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

        Page<NotificationResponseDto> notificationResponseDtoPage = notificationPage.map(
                notification -> NotificationResponseDto.builder()
                        .idNotification(notification.getIdNotification())
                        .title(notification.getTitle())
                        .description(notification.getDescription())
                        .isRead(notification.getIsRead())
                        .user(notification.getUser().getIdUser())
                        .build()
        );

        return notificationResponseDtoPage;
    }
}
