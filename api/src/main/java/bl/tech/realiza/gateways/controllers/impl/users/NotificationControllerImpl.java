package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.NotificationController;
import bl.tech.realiza.gateways.requests.users.NotificationRequestDto;
import bl.tech.realiza.gateways.responses.users.NotificationResponseDto;
import bl.tech.realiza.usecases.impl.users.CrudNotificationImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/notification")
public class NotificationControllerImpl implements NotificationController {

    private final CrudNotificationImpl crudNotification;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<NotificationResponseDto> createNotification(@RequestBody @Valid NotificationRequestDto notificationRequestDto) {
        NotificationResponseDto notification = crudNotification.save(notificationRequestDto);

        return ResponseEntity.of(Optional.of(notification));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<NotificationResponseDto>> getOneNotification(@PathVariable String id) {
        Optional<NotificationResponseDto> notification = crudNotification.findOne(id);

        return ResponseEntity.of(Optional.of(notification));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<NotificationResponseDto>> getAllNotifications(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "5") int size,
                                                                             @RequestParam(defaultValue = "id") String sort,
                                                                             @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<NotificationResponseDto> pageNotification = crudNotification.findAll(pageable);

        return ResponseEntity.ok(pageNotification);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<NotificationResponseDto>> updateNotification(@RequestBody @Valid NotificationRequestDto notificationRequestDto) {
        Optional<NotificationResponseDto> notification = crudNotification.update(notificationRequestDto);

        return ResponseEntity.of(Optional.of(notification));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        crudNotification.delete(id);

        return ResponseEntity.noContent().build();
    }
}
