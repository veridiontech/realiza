package bl.tech.realiza.gateways.controllers.impl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailTestController {

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/test-smtp")
    public String testSmtp() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("jhonatan.sampaiof@gmail.com"); // Substitua pelo seu e-mail
            message.setSubject("SMTP Test");
            message.setText("This is a test email from Render.");
            mailSender.send(message);
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }
}