package bl.tech.realiza.services.email;

import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.services.EmailRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
                clientRepository.findById(emailRequestDto.getIdCompany()).orElseThrow(() -> new RuntimeException("Client not found"));
            }
            case SUPPLIER -> {
                providerSupplierRepository.findById(emailRequestDto.getIdCompany()).orElseThrow(() -> new RuntimeException("Supplier not found"));
            }
            case SUBCONTRACTOR -> {
                providerSubcontractorRepository.findById(emailRequestDto.getIdCompany()).orElseThrow(() -> new RuntimeException("Subcontractor not found"));
            }
        }


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("jhonatan.sampaiof@gmail.com");
        message.setTo(emailRequestDto.getEmail());
        message.setSubject("Bem vindo à Realiza!");
        message.setText("Olá, tudo bem?\n" +
                "\n" +
                "A Realiza Assessoria Empresarial Ltda está implementando um novo sistema para gestão de seus terceiros.\n" +
                "Seja bem vindo! \uD83D\uDE04\n" +
                "Agora ficou muito mais fácil buscar um terceiro, solicitar o seu serviço e acompanhar todos os seus requisitos de documentações.\n" +
                "Vamos começar pelo cadastro? Nos vemos lá dentro.\n" +
                "Acessar Wehandle\n");

        mailSender.send(message);
    }
}
