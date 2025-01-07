package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.users.Notification;
import bl.tech.realiza.domains.users.User;
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

        Optional<User> userOptional = userRepository.findById(notificationRequestDto.getUser());

        User user = userOptional.orElseThrow(() -> new RuntimeException("User not found"));

        Notification newNotification = Notification.builder()
                .title(notificationRequestDto.getTitle())
                .description(notificationRequestDto.getDescription())
                .read(notificationRequestDto.getRead())
                .user(user)
                .build();

        Notification savedNotification = notificationRepository.save(newNotification);

        NotificationResponseDto notificationResponse = NotificationResponseDto.builder()
                .idNotification(savedNotification.getIdNotification())
                .title(savedNotification.getTitle())
                .description(savedNotification.getDescription())
                .read(savedNotification.getRead())
                .user(savedNotification.getUser().getIdUser())
                .build();

        return notificationResponse;
    }

    @Override
    public Optional<NotificationResponseDto> findOne(String id) {

        Optional<Notification> notificationOptional = notificationRepository.findById(id);

        Notification notification = notificationOptional.orElseThrow(() -> new RuntimeException("Notification not found"));

        NotificationResponseDto notificationResponse = NotificationResponseDto.builder()
                .idNotification(notification.getIdNotification())
                .title(notification.getTitle())
                .description(notification.getDescription())
                .read(notification.getRead())
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
                        .read(notification.getRead())
                        .user(notification.getUser().getIdUser())
                        .build()
        );

        return notificationResponseDtoPage;
    }

    @Override
    public Optional<NotificationResponseDto> update(NotificationRequestDto notificationRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        notificationRepository.deleteById(id);
    }
}
