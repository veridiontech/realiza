package bl.tech.realiza.services.email;

import bl.tech.realiza.gateways.controllers.impl.services.EmailController;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.services.EmailRequestDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ClientRepository clientRepository;
    private final ProviderSupplierRepository providerSupplierRepository;

    public void sendEmail(EmailRequestDto emailRequestDto) {
        switch (emailRequestDto.getCompany()) {
            case CLIENT -> {
                clientRepository.findById(emailRequestDto.getIdCompany())
                        .orElseThrow(() -> new RuntimeException("Client not found"));
            }
            case SUPPLIER -> {
                providerSupplierRepository.findById(emailRequestDto.getIdCompany())
                        .orElseThrow(() -> new RuntimeException("Supplier not found"));
            }
            case SUBCONTRACTOR -> {
                providerSubcontractorRepository.findById(emailRequestDto.getIdCompany())
                        .orElseThrow(() -> new RuntimeException("Subcontractor not found"));
            }
        }

        try {
            // Creating a MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Setting sender, recipient, and subject
            helper.setFrom("jhonatan.sampaiof@gmail.com");
            helper.setTo(emailRequestDto.getEmail());
            helper.setSubject("Java email with attachment | From GC");

            // Adding the HTML email body from a file
            try (var inputStream = Objects.requireNonNull(
                    EmailController.class.getResourceAsStream("/templates/email-content.html"))) {
                helper.setText(
                        new String(inputStream.readAllBytes(), StandardCharsets.UTF_8),
                        true // Enables HTML format
                );
            }

            // Adding an inline image attachment
            helper.addInline(
                    "fe2da35f82b3200096d22e32b6a7c011.jpg",
                    new File("C:/Users/Rogerio/Pictures/fe2da35f82b3200096d22e32b6a7c011.jpg")
            );

            // Sending the email
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
