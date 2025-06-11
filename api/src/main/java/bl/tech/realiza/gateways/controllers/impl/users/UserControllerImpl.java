package bl.tech.realiza.gateways.controllers.impl.users;

import bl.tech.realiza.gateways.controllers.interfaces.users.UserController;
import bl.tech.realiza.usecases.interfaces.users.CrudUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "User")
public class UserControllerImpl implements UserController {
    private final CrudUser crudUser;

    @PostMapping("/{userId}/activation")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<String> userActivation(@PathVariable String userId, @RequestParam Boolean activation) {
        return ResponseEntity.ok(crudUser.userActivation(userId, activation));
    }
}
