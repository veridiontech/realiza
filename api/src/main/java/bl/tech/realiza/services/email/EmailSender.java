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
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.services.auth.TokenManagerService;
import bl.tech.realiza.usecases.impl.providers.CrudProviderSupplierImpl;
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
    private final CrudProviderSupplierImpl crudProviderSupplierImpl;

    public void sendInviteEnterpriseEmail(EmailInviteRequestDto emailInviteRequestDto) {
        String companyName = "";
        String idCompany = "";
        String idBranch = "";
        Provider.Company company = emailInviteRequestDto.getCompany();
        switch (emailInviteRequestDto.getCompany()) {
            case CLIENT -> {
                companyName = "Realiza Assessoria Empresarial Ltda";
            }
            case SUPPLIER -> {
                ProviderResponseDto providerSupplier = crudProviderSupplierImpl.findOne(emailInviteRequestDto.getIdCompany())
                        .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
                companyName = providerSupplier.getCorporateName() != null ? providerSupplier.getCorporateName() : "Tech Solutions Ltda";
                idCompany = providerSupplier.getIdProvider();
                idBranch = providerSupplier.getBranches().get(0).getIdBranch();
            }
            case SUBCONTRACTOR -> {
                var subcontractor = providerSubcontractorRepository.findById(emailInviteRequestDto.getIdCompany())
                        .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
                companyName = subcontractor.getCorporateName();
                idCompany = subcontractor.getIdProvider();
                idBranch = subcontractor.getProviderSupplier().getBranches().get(0).getIdBranch();
            }
        }

        try {
            // Generating a unique token
            String token = tokenManagerService.generateToken();
            String emailBody;

            // Reading and customizing the email template
            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-enterprise-invite.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("Tech Solutions Ltda", companyName)
                        .replace("#TOKEN_PLACEHOLDER#", token)
                        .replace("#ID_PLACEHOLDER#",idCompany)
                        .replace("#ID_BRANCH#",idBranch);
                if (emailInviteRequestDto.getIdClient() != null) {
                    emailBody = emailBody.replace("#ID_CLIENT#", emailInviteRequestDto.getIdClient());
                } else {
                    emailBody = emailBody.replace("&idClient=#ID_CLIENT#", "");
                }
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

    public void sendInviteNewUserEmail(EmailInviteRequestDto emailInviteRequestDto) {
        String companyName = "";
        String idCompany = "";
        String idBranch = "";
        Provider.Company company = emailInviteRequestDto.getCompany();
        switch (emailInviteRequestDto.getCompany()) {
            case CLIENT -> {
                companyName = "Realiza Assessoria Empresarial Ltda";
            }
            case SUPPLIER -> {
                ProviderResponseDto providerSupplier = crudProviderSupplierImpl.findOne(emailInviteRequestDto.getIdCompany())
                        .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
                companyName = providerSupplier.getCorporateName() != null ? providerSupplier.getCorporateName() : "Tech Solutions Ltda";
                idCompany = providerSupplier.getIdProvider();
                idBranch = providerSupplier.getBranches().get(0).getIdBranch();
            }
            case SUBCONTRACTOR -> {
                var subcontractor = providerSubcontractorRepository.findById(emailInviteRequestDto.getIdCompany())
                        .orElseThrow(() -> new EntityNotFoundException("Supplier not found"));
                companyName = subcontractor.getCorporateName();
                idCompany = subcontractor.getIdProvider();
                idBranch = subcontractor.getProviderSupplier().getBranches().get(0).getIdBranch();
            }
        }

        try {
            // Generating a unique token
            String token = tokenManagerService.generateToken();
            String emailBody;

            // Reading and customizing the email template
            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-enterprise-invite.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("Tech Solutions Ltda", companyName)
                        .replace("#TOKEN_PLACEHOLDER#", token)
                        .replace("#ID_PLACEHOLDER#",idCompany)
                        .replace("#ID_BRANCH#",idBranch);
                if (emailInviteRequestDto.getIdClient() != null) {
                    emailBody = emailBody.replace("#ID_CLIENT#", emailInviteRequestDto.getIdClient());
                } else {
                    emailBody = emailBody.replace("&idClient=#ID_CLIENT#", "");
                }
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
