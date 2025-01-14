package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.requests.services.EmailRequestDto;
import bl.tech.realiza.services.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailSender emailSender;

    @RequestMapping("/invite")
    public String sendEmail(@RequestBody EmailRequestDto email) {
        try {

            emailSender.sendEmail(email);

            return "Success!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
