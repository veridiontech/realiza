package bl.tech.realiza.services.email;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.controllers.impl.services.EmailController;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.services.EmailRequestDto;
import bl.tech.realiza.services.auth.TokenManager;
import io.github.cdimascio.dotenv.Dotenv;
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
    private final Dotenv dotenv;
    private final JavaMailSender mailSender;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ClientRepository clientRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final TokenManager tokenManager;

    public void sendEmail(EmailRequestDto emailRequestDto) {
        String companyName = "";
        String idCompany = "";

        switch (emailRequestDto.getCompany()) {
            case CLIENT -> {
                var client = clientRepository.findById(emailRequestDto.getIdCompany())
                        .orElseThrow(() -> new RuntimeException("Client not found"));
                companyName = client.getCompanyName();
                idCompany = client.getIdClient();
            }
            case SUPPLIER -> {
                var supplier = providerSupplierRepository.findById(emailRequestDto.getIdCompany())
                        .orElseThrow(() -> new RuntimeException("Supplier not found"));
                companyName = supplier.getCompanyName();
                idCompany = supplier.getIdProvider();
            }
            case SUBCONTRACTOR -> {
                var subcontractor = providerSubcontractorRepository.findById(emailRequestDto.getIdCompany())
                        .orElseThrow(() -> new RuntimeException("Subcontractor not found"));
                companyName = subcontractor.getCompanyName();
                idCompany = subcontractor.getIdProvider();
            }
        }

        try {
            // Generating a unique token
            String token = tokenManager.generateToken();
            String emailBody;

            // Reading and customizing the email template
            try (var inputStream = Objects.requireNonNull(
                    EmailController.class.getResourceAsStream("/templates/email-content.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("<span class=\"highlight\">Realiza Assessoria Empresarial Ltda</span>",
                                "<span class=\"highlight\">" + companyName + "</span>")
                        .replace("#TOKEN_PLACEHOLDER#", token)
                        .replace("#ID_PLACEHOLDER#",idCompany);
            }

            // Creating and sending the email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(dotenv.get("GMAIL_EMAIL"));
            helper.setTo(emailRequestDto.getEmail());
            helper.setSubject("Bem-vindo à " + companyName);
            helper.setText(emailBody, true); // Enable HTML format

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
