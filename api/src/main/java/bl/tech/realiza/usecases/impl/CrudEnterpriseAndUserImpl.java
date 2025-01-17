package bl.tech.realiza.usecases.impl;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.clients.ClientAndUserClientRequestDto;
import bl.tech.realiza.gateways.requests.services.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientAndUserClientResponseDto;
import bl.tech.realiza.gateways.responses.services.EnterpriseAndUserResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.interfaces.CrudEnterpriseAndUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudEnterpriseAndUserImpl implements CrudEnterpriseAndUser {
    private final ClientRepository clientRepository;
    private final UserClientRepository userClientRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final UserProviderSupplierRepository userProviderSupplierRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final UserProviderSubcontractorRepository userProviderSubcontractorRepository;
    private final PasswordEncryptionService passwordEncryptionService;

    @Override
    public EnterpriseAndUserResponseDto saveBothClient(EnterpriseAndUserRequestDto enterpriseAndUserRequest) {
        Client newClient = Client.builder()
                .cnpj(enterpriseAndUserRequest.getCnpj())
                .tradeName(enterpriseAndUserRequest.getNameEnterprise())
                .companyName(enterpriseAndUserRequest.getSocialReason())
                .fantasyName(enterpriseAndUserRequest.getFantasyName())
                .email(enterpriseAndUserRequest.getEmail())
                .telephone(enterpriseAndUserRequest.getPhone())
                .build();

        Client savedClient = clientRepository.save(newClient);

        String encryptedPassword = passwordEncryptionService.encryptPassword(enterpriseAndUserRequest.getPassword());

        UserClient newUserClient = UserClient.builder()
                .cpf(enterpriseAndUserRequest.getCpf())
                .password(encryptedPassword)
                .position(enterpriseAndUserRequest.getPosition())
                .role(enterpriseAndUserRequest.getRole())
                .firstName(enterpriseAndUserRequest.getName())
                .surname(enterpriseAndUserRequest.getSurname())
                .email(enterpriseAndUserRequest.getEmail())
                .telephone(enterpriseAndUserRequest.getPhone())
                .client(savedClient)
                .build();

        UserClient savedUserClient = userClientRepository.save(newUserClient);

        EnterpriseAndUserResponseDto clientAndUserClientResponseDto = EnterpriseAndUserResponseDto.builder()
                .idEnterprise(savedClient.getIdClient())
                .cnpj(savedClient.getCnpj())
                .nameEnterprise(savedClient.getCompanyName())
                .fantasyName(savedClient.getFantasyName())
                .socialReason(savedClient.getTradeName())
                .email(savedClient.getEmail())
                .phone(savedClient.getTelephone())
                .idUser(savedUserClient.getIdUser())
                .cpf(savedUserClient.getCpf())
                .name(savedUserClient.getFirstName())
                .surname(savedUserClient.getSurname())
                .position(savedUserClient.getPosition())
                .role(savedUserClient.getRole())
                .build();

        return clientAndUserClientResponseDto;
    }

    @Override
    public EnterpriseAndUserResponseDto saveBothSupplier(EnterpriseAndUserRequestDto enterpriseAndUserRequest) {
        Optional<Client> clientOptional = clientRepository.findById(enterpriseAndUserRequest.getIdCompany());

        Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        ProviderSupplier newProviderSupplier = ProviderSupplier.builder()
                .cnpj(enterpriseAndUserRequest.getCnpj())
                .companyName(enterpriseAndUserRequest.getNameEnterprise())
                .tradeName(enterpriseAndUserRequest.getSocialReason())
                .fantasyName(enterpriseAndUserRequest.getFantasyName())
                .email(enterpriseAndUserRequest.getEmail())
                .client(client)
                .build();

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(newProviderSupplier);

        String encryptedPassword = passwordEncryptionService.encryptPassword(enterpriseAndUserRequest.getPassword());

        UserProviderSupplier newUserProviderSupplier = UserProviderSupplier.builder()
                .cpf(enterpriseAndUserRequest.getCpf())
                .password(encryptedPassword)
                .position(enterpriseAndUserRequest.getPosition())
                .role(enterpriseAndUserRequest.getRole())
                .firstName(enterpriseAndUserRequest.getName())
                .surname(enterpriseAndUserRequest.getSurname())
                .email(enterpriseAndUserRequest.getEmail())
                .telephone(enterpriseAndUserRequest.getPhone())
                .providerSupplier(savedProviderSupplier)
                .build();

        UserProviderSupplier savedUserProviderSupplier = userProviderSupplierRepository.save(newUserProviderSupplier);

        EnterpriseAndUserResponseDto clientAndUserClientResponseDto = EnterpriseAndUserResponseDto.builder()
                .idEnterprise(savedProviderSupplier.getIdProvider())
                .cnpj(savedProviderSupplier.getCnpj())
                .nameEnterprise(savedProviderSupplier.getCompanyName())
                .fantasyName(savedProviderSupplier.getFantasyName())
                .socialReason(savedProviderSupplier.getTradeName())
                .email(savedProviderSupplier.getEmail())
                .phone(savedUserProviderSupplier.getTelephone())
                .idUser(savedUserProviderSupplier.getIdUser())
                .cpf(savedUserProviderSupplier.getCpf())
                .name(savedUserProviderSupplier.getFirstName())
                .surname(savedUserProviderSupplier.getSurname())
                .position(savedUserProviderSupplier.getPosition())
                .role(savedUserProviderSupplier.getRole())
                .build();

        return clientAndUserClientResponseDto;
    }

    @Override
    public EnterpriseAndUserResponseDto saveBothSubcontractor(EnterpriseAndUserRequestDto enterpriseAndUserRequest) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(enterpriseAndUserRequest.getIdCompany());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider supplier not found"));

        ProviderSubcontractor newProviderSubcontractor = ProviderSubcontractor.builder()
                .cnpj(enterpriseAndUserRequest.getCnpj())
                .companyName(enterpriseAndUserRequest.getNameEnterprise())
                .tradeName(enterpriseAndUserRequest.getSocialReason())
                .fantasyName(enterpriseAndUserRequest.getFantasyName())
                .email(enterpriseAndUserRequest.getEmail())
                .providerSupplier(providerSupplier)
                .build();

        ProviderSubcontractor savedProviderSubcontractor = providerSubcontractorRepository.save(newProviderSubcontractor);

        String encryptedPassword = passwordEncryptionService.encryptPassword(enterpriseAndUserRequest.getPassword());

        UserProviderSubcontractor newUserProviderSubcontractor = UserProviderSubcontractor.builder()
                .cpf(enterpriseAndUserRequest.getCpf())
                .password(encryptedPassword)
                .position(enterpriseAndUserRequest.getPosition())
                .role(enterpriseAndUserRequest.getRole())
                .firstName(enterpriseAndUserRequest.getName())
                .surname(enterpriseAndUserRequest.getSurname())
                .email(enterpriseAndUserRequest.getEmail())
                .telephone(enterpriseAndUserRequest.getPhone())
                .providerSubcontractor(savedProviderSubcontractor)
                .build();

        UserProviderSubcontractor savedUserProviderSubcontractor = userProviderSubcontractorRepository.save(newUserProviderSubcontractor);

        EnterpriseAndUserResponseDto clientAndUserClientResponseDto = EnterpriseAndUserResponseDto.builder()
                .idEnterprise(savedProviderSubcontractor.getIdProvider())
                .cnpj(savedProviderSubcontractor.getCnpj())
                .nameEnterprise(savedProviderSubcontractor.getCompanyName())
                .fantasyName(savedProviderSubcontractor.getFantasyName())
                .socialReason(savedProviderSubcontractor.getTradeName())
                .email(savedProviderSubcontractor.getEmail())
                .phone(savedUserProviderSubcontractor.getTelephone())
                .idUser(savedUserProviderSubcontractor.getIdUser())
                .cpf(savedUserProviderSubcontractor.getCpf())
                .name(savedUserProviderSubcontractor.getFirstName())
                .surname(savedUserProviderSubcontractor.getSurname())
                .position(savedUserProviderSubcontractor.getPosition())
                .role(savedUserProviderSubcontractor.getRole())
                .build();

        return clientAndUserClientResponseDto;
    }
}
