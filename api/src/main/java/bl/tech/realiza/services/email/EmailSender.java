package bl.tech.realiza.services.email;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.controllers.impl.services.EmailControllerImpl;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.services.email.EmailEnterpriseInviteRequestDto;
import bl.tech.realiza.gateways.requests.services.email.EmailNewUserRequestDto;
import bl.tech.realiza.gateways.requests.services.email.EmailRegistrationCompleteRequestDto;
import bl.tech.realiza.gateways.requests.services.email.EmailRegistrationDeniedRequestDto;
import bl.tech.realiza.services.auth.TokenManagerService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.internet.MimeMessage;
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
    private final TokenManagerService tokenManagerService;
    private final UserRepository userRepository;

    public void sendNewProviderEmail(EmailEnterpriseInviteRequestDto emailEnterpriseInviteRequestDto, String token) {
        String email = emailEnterpriseInviteRequestDto.getEmail();
        String companyName = emailEnterpriseInviteRequestDto.getCompanyName();
        String requesterName = emailEnterpriseInviteRequestDto.getRequesterName();
        String serviceName = emailEnterpriseInviteRequestDto.getServiceName();
        String startDate = emailEnterpriseInviteRequestDto.getStartDate().toString();
        String requesterBranchName = emailEnterpriseInviteRequestDto.getRequesterBranchName();
        String responsibleName = emailEnterpriseInviteRequestDto.getResponsibleName();
        String contractReference = emailEnterpriseInviteRequestDto.getContractReference();
        String idCompany = emailEnterpriseInviteRequestDto.getIdCompany();
        String idBranch = emailEnterpriseInviteRequestDto.getIdBranch();
        String idSupplier = emailEnterpriseInviteRequestDto.getIdSupplier();

        try {
            String emailBody;

            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-enterprise-invite.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("#NOME_ENTERPRISE#", companyName)
                        .replace("#TOKEN_PLACEHOLDER#", token)
                        .replace("#ID_PLACEHOLDER#",idCompany)
                        .replace("#NAME_REQUESTER#", requesterName)
                        .replace("#NOME_DO_SERVICO#", serviceName)
                        .replace("#DATA_DE_INICIO#", startDate)
                        .replace("#UNIDADE_SOLICITANTE#", requesterBranchName)
                        .replace("#NOME_DO_GESTOR#", responsibleName)
                        .replace("#REFERENCIA_DO_CONTRATO#", contractReference);
                if (idBranch != null) {
                    emailBody = emailBody.replace("#ID_BRANCH#",idBranch);
                } else {
                    emailBody = emailBody.replace("&idBranch=#ID_BRANCH#","");
                }
                if (idSupplier != null) {
                    emailBody = emailBody.replace("#ID_SUPPLIER#",idSupplier);
                } else {
                    emailBody = emailBody.replace("#&idSupplier=#ID_SUPPLIER#","");
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate email", e);
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(dotenv.get("GMAIL_EMAIL"));
            helper.setTo(email);
            helper.setSubject("Convite para Cadastro na Plataforma da Realiza Assessoria");
            helper.setText(emailBody, true);

            try {
                mailSender.send(message);
            } catch (MailException e) {
                throw new RuntimeException("Failed to send the email", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate email", e);
        }
    }

    public void sendNewUserEmail(EmailNewUserRequestDto emailNewUserRequestDto) {
        String email = emailNewUserRequestDto.getEmail();
        String password = emailNewUserRequestDto.getPassword();
        String nameUser = emailNewUserRequestDto.getNameUser();

        try {
            String emailBody;

            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-new-user.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("#NOME_USUARIO#", nameUser)
                        .replace("#EMAIL#", email)
                        .replace("#PASSWORD#", password);
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate email", e);
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(dotenv.get("GMAIL_EMAIL"));
            helper.setTo(email);
            helper.setSubject("Acesso Liberado à Plataforma da Realiza Assessoria");
            helper.setText(emailBody, true);

            try {
                mailSender.send(message);
            } catch (MailException e) {
                throw new RuntimeException("Failed to send the email", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate email", e);
        }
    }

    public void sendRegistrationCompleteEmail(EmailRegistrationCompleteRequestDto emailRegistrationCompleteRequestDto) {
        String email = emailRegistrationCompleteRequestDto.getEmail();
        String responsibleName = emailRegistrationCompleteRequestDto.getResponsibleName();
        String parentEnterpriseName = emailRegistrationCompleteRequestDto.getParentEnterpriseName();

        try {
            String emailBody;

            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-registration-complete.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("#RESPONSIBLE_NAME#", responsibleName)
                        .replace("#PARENT_ENTERPRISE_NAME#", parentEnterpriseName);
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate email", e);
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(dotenv.get("GMAIL_EMAIL"));
            helper.setTo(email);
            helper.setSubject("Bem-vindo à Plataforma de Gestão de Terceiros – Realiza Assessoria");
            helper.setText(emailBody, true);

            try {
                mailSender.send(message);
            } catch (MailException e) {
                throw new RuntimeException("Failed to send the email", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate email", e);
        }
    }

    public void sendNewProviderDeniedEmail(EmailRegistrationDeniedRequestDto emailRegistrationDeniedRequestDto) {
        String email = emailRegistrationDeniedRequestDto.getEmail();
        String responsibleName = emailRegistrationDeniedRequestDto.getResponsibleName();
        String enterpriseName = emailRegistrationDeniedRequestDto.getEnterpriseName();
        String reason = emailRegistrationDeniedRequestDto.getReason();

        try {
            String emailBody;

            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-enterprise-denied.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("#RESPONSIBLE_NAME#", responsibleName)
                        .replace("#ENTERPRISE_NAME#",enterpriseName);
//                        .replace("#REASON#",reason);
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate email", e);
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(dotenv.get("GMAIL_EMAIL"));
            helper.setTo(email);
            helper.setSubject("Inconsistência no Cadastro de Fornecedor – Plataforma Realiza Assessoria");
            helper.setText(emailBody, true);

            try {
                mailSender.send(message);
            } catch (MailException e) {
                throw new RuntimeException("Failed to send the email", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate email", e);
        }
    }

    public void sendPasswordRecoveryEmail(String email, String fourDigitCode) {
        User user = userRepository.findByEmailAndForgotPasswordCodeAndIsActiveIsTrue(email, fourDigitCode+email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        try {
            String emailBody;
            try (var inputStream = Objects.requireNonNull(
                    EmailControllerImpl.class.getResourceAsStream("/templates/email-password-recovery.html"))) {
                emailBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("#1#", String.valueOf(fourDigitCode.charAt(0)))
                        .replace("#2#", String.valueOf(fourDigitCode.charAt(1)))
                        .replace("#3#", String.valueOf(fourDigitCode.charAt(2)))
                        .replace("#4#", String.valueOf(fourDigitCode.charAt(3)));
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate email", e);
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(dotenv.get("GMAIL_EMAIL"));
            helper.setTo(user.getEmail());
            helper.setSubject("Recuperação de acesso a plataforma Realiza");
            helper.setText(emailBody, true);

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
