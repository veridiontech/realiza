package bl.tech.realiza.services.email;

import bl.tech.realiza.gateways.controllers.impl.services.EmailControllerImpl;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.services.EmailRequestDto;
import bl.tech.realiza.services.auth.TokenManagerService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
    private final TokenManagerService tokenManagerService;

    public void sendEmail(EmailRequestDto emailRequestDto) {
        String companyName = "";
        String idCompany = null;
        EmailRequestDto.Company company = emailRequestDto.getCompany();

        switch (emailRequestDto.getCompany()) {
            case CLIENT -> {
                var client = clientRepository.findById(emailRequestDto.getIdCompany())
                        .orElseThrow(() -> new RuntimeException("Client not found"));
                companyName = client.getCompanyName();
            }
            case SUPPLIER -> {
                var supplier = providerSupplierRepository.findById(emailRequestDto.getIdCompany())
                        .orElseThrow(() -> new RuntimeException("Supplier not found"));
                companyName = supplier.getCompanyName();
                idCompany = supplier.getClient().getIdClient();
            }
            case SUBCONTRACTOR -> {
                var subcontractor = providerSubcontractorRepository.findById(emailRequestDto.getIdCompany())
                        .orElseThrow(() -> new RuntimeException("Subcontractor not found"));
                companyName = subcontractor.getCompanyName();
                idCompany = subcontractor.getProviderSupplier().getIdProvider();
            }
        }

        try {
            // Generating a unique token
            String token = tokenManagerService.generateToken();
            String emailBody;

            // Reading and customizing the email template
            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-content.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("<span class=\"highlight\">Realiza Assessoria Empresarial Ltda</span>",
                                "<span class=\"highlight\">" + companyName + "</span>")
                        .replace("#TOKEN_PLACEHOLDER#", token)
                        .replace("#ID_PLACEHOLDER#",idCompany)
                        .replace("#COMPANY_PLACEHOLDER#",company.name());
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
