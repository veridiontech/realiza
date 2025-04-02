package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.requests.services.email.EmailInviteRequestDto;
import bl.tech.realiza.services.auth.TokenManagerService;
import bl.tech.realiza.services.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EmailControllerImpl {

    private final EmailSender emailSender;
    private final TokenManagerService tokenManagerService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/invite")
    public ResponseEntity<String> sendEmailInvite(@RequestBody EmailInviteRequestDto email) {
        try {
            emailSender.sendInviteEnterpriseEmail(email);
            return ResponseEntity.ok("Success!");
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam String token) {
        boolean isValid = tokenManagerService.validateToken(token);
        if (isValid){
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.");
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/password-recovery")
    public ResponseEntity<String> sendEmailPasswordRecovery(@RequestParam String email) {
        try {
            emailSender.sendPasswordRecoveryEmail(email);
            return ResponseEntity.ok("Password recovery email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
