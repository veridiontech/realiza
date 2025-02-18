package bl.tech.realiza.services.email;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.controllers.impl.services.EmailControllerImpl;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.services.email.EmailInviteRequestDto;
import bl.tech.realiza.gateways.requests.services.email.EmailUpdateRequestDto;
import bl.tech.realiza.services.auth.TokenManagerService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
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
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ClientRepository clientRepository;
    private final TokenManagerService tokenManagerService;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    public void sendInviteEmail(EmailInviteRequestDto emailInviteRequestDto) {
        String companyName = "";
        String idCompany = "";
        Provider.Company company = emailInviteRequestDto.getCompany();
        switch (emailInviteRequestDto.getCompany()) {
            case CLIENT -> {
                companyName = "Realiza Assessoria Empresarial Ltda";
            }
            case SUPPLIER -> {
                var supplier = branchRepository.findById(emailInviteRequestDto.getIdCompany())
                        .orElseThrow(() -> new EntityNotFoundException("Client not found"));
                companyName = supplier.getClient().getCorporateName();
                idCompany = supplier.getIdBranch();
            }
            case SUBCONTRACTOR -> {
                var subcontractor = providerSupplierRepository.findById(emailInviteRequestDto.getIdCompany())
                        .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
                companyName = subcontractor.getCorporateName();
                idCompany = subcontractor.getIdProvider();
            }
        }

        try {
            // Generating a unique token
            String token = tokenManagerService.generateToken();
            String emailBody;

            // Reading and customizing the email template
            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-invite.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("<span class=\"highlight\">Realiza Assessoria Empresarial Ltda</span>",
                                "<span class=\"highlight\">" + companyName + "</span>")
                        .replace("#TOKEN_PLACEHOLDER#", token)
                        .replace("#ID_PLACEHOLDER#",idCompany)
                        .replace("#COMPANY_PLACEHOLDER#",company.name());
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate email", e);
            }
            // Creating and sending the email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(dotenv.get("GMAIL_EMAIL"));
            helper.setTo(emailInviteRequestDto.getEmail());
            helper.setSubject("Bem-vindo à " + companyName);
            helper.setText(emailBody, true); // Enable HTML format

            try {
                mailSender.send(message);
            } catch (MailException e) {
                throw new RuntimeException("Failed to send the email", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate email", e);
        }
    }

    public void sendUpdateEmail(EmailUpdateRequestDto emailUpdateRequestDto) {
        try {
            // Carrega o template básico do email
            String emailTemplate;
            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-update.html"))) {
                emailTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }

            // Constrói dinamicamente o HTML da lista de seções
            StringBuilder sectionsHtml = new StringBuilder();
            for (EmailUpdateRequestDto.Section section : emailUpdateRequestDto.getSections()) {
                sectionsHtml.append("<li><b>").append(section.getSectionTitle()).append("</b><ul>");
                for (String item : section.getItems()) {
                    sectionsHtml.append("<li>").append(item).append("</li>");
                }
                sectionsHtml.append("</ul></li>");
            }

            // Substitui os placeholders no template
            String emailBody = emailTemplate
                    .replace("#VERSION#", emailUpdateRequestDto.getVersion())
                    .replace("#TITLE#", emailUpdateRequestDto.getTitle())
                    .replace("#DESCRIPTION#", emailUpdateRequestDto.getDescription())
                    .replace("#SECTIONS#", sectionsHtml.toString());

            // Configura e envia o email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(dotenv.get("GMAIL_EMAIL"));
            helper.setTo("jhonatan.sampaiof@gmail.com");
            helper.setSubject("Atualização Realiza Sistema Versão " + emailUpdateRequestDto.getVersion());
            helper.setText(emailBody, true); // Habilita formato HTML

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate or send email", e);
        }
    }

    public void sendPasswordRecoveryEmail(String email) {
        User user = userRepository.findByEmail(email);

        try {
            // Generating a unique token
            String token = tokenManagerService.generateToken();
            String emailBody;

            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-password-recovery.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("#TOKEN_PLACEHOLDER#", token)
                        .replace("#ID_PLACEHOLDER#",user.getIdUser());
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate email", e);
            }

            // Creating and sending the email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(dotenv.get("GMAIL_EMAIL"));
            helper.setTo(user.getEmail());
            helper.setSubject("Recuperação de acesso a plataforma Realiza");
            helper.setText(emailBody, true); // Enable HTML format

            try {
                mailSender.send(message);
            } catch (MailException e) {
                throw new RuntimeException("Failed to send the email", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate email", e);
        }

    }
}
