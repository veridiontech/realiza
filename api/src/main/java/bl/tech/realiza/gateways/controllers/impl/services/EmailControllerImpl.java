package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.requests.services.EmailRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientAndUserClientResponseDto;
import bl.tech.realiza.services.auth.TokenManagerService;
import bl.tech.realiza.services.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class EmailControllerImpl {

    private final EmailSender emailSender;
    private final TokenManagerService tokenManagerService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/invite")
    public String sendEmail(@RequestBody EmailRequestDto email) {
        try {

            emailSender.sendEmail(email);

            return "Success!";
        } catch (Exception e) {
            return e.getMessage();
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
}
