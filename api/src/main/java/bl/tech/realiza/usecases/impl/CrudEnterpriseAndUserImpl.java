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
import bl.tech.realiza.gateways.requests.enterprises.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.responses.services.EnterpriseAndUserResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.interfaces.CrudEnterpriseAndUser;
import jakarta.persistence.EntityNotFoundException;
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
    public EnterpriseAndUserResponseDto saveBothClient(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto) {
        Client newClient = Client.builder()
                .cnpj(enterpriseAndUserRequestDto.getCnpj())
                .tradeName(enterpriseAndUserRequestDto.getNameEnterprise())
                .companyName(enterpriseAndUserRequestDto.getSocialReason())
                .fantasyName(enterpriseAndUserRequestDto.getFantasyName())
                .email(enterpriseAndUserRequestDto.getEmail())
                .telephone(enterpriseAndUserRequestDto.getPhone())
                .build();

        Client savedClient = clientRepository.save(newClient);

        String encryptedPassword = passwordEncryptionService.encryptPassword(enterpriseAndUserRequestDto.getPassword());

        UserClient newUserClient = UserClient.builder()
                .cpf(enterpriseAndUserRequestDto.getCpf())
                .password(encryptedPassword)
                .position(enterpriseAndUserRequestDto.getPosition())
                .role(enterpriseAndUserRequestDto.getRole())
                .firstName(enterpriseAndUserRequestDto.getName())
                .surname(enterpriseAndUserRequestDto.getSurname())
                .email(enterpriseAndUserRequestDto.getEmail())
                .telephone(enterpriseAndUserRequestDto.getPhone())
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
    public EnterpriseAndUserResponseDto saveBothSupplier(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto) {
        Optional<Client> clientOptional = clientRepository.findById(enterpriseAndUserRequestDto.getIdCompany());

        Client client = clientOptional.orElseThrow(() -> new EntityNotFoundException("Client not found"));

        ProviderSupplier newProviderSupplier = ProviderSupplier.builder()
                .cnpj(enterpriseAndUserRequestDto.getCnpj())
                .companyName(enterpriseAndUserRequestDto.getNameEnterprise())
                .tradeName(enterpriseAndUserRequestDto.getSocialReason())
                .fantasyName(enterpriseAndUserRequestDto.getFantasyName())
                .email(enterpriseAndUserRequestDto.getEmail())
                .client(client)
                .build();

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(newProviderSupplier);

        String encryptedPassword = passwordEncryptionService.encryptPassword(enterpriseAndUserRequestDto.getPassword());

        UserProviderSupplier newUserProviderSupplier = UserProviderSupplier.builder()
                .cpf(enterpriseAndUserRequestDto.getCpf())
                .password(encryptedPassword)
                .position(enterpriseAndUserRequestDto.getPosition())
                .role(enterpriseAndUserRequestDto.getRole())
                .firstName(enterpriseAndUserRequestDto.getName())
                .surname(enterpriseAndUserRequestDto.getSurname())
                .email(enterpriseAndUserRequestDto.getEmail())
                .telephone(enterpriseAndUserRequestDto.getPhone())
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
    public EnterpriseAndUserResponseDto saveBothSubcontractor(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(enterpriseAndUserRequestDto.getIdCompany());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Provider supplier not found"));

        ProviderSubcontractor newProviderSubcontractor = ProviderSubcontractor.builder()
                .cnpj(enterpriseAndUserRequestDto.getCnpj())
                .companyName(enterpriseAndUserRequestDto.getNameEnterprise())
                .tradeName(enterpriseAndUserRequestDto.getSocialReason())
                .fantasyName(enterpriseAndUserRequestDto.getFantasyName())
                .email(enterpriseAndUserRequestDto.getEmail())
                .providerSupplier(providerSupplier)
                .build();

        ProviderSubcontractor savedProviderSubcontractor = providerSubcontractorRepository.save(newProviderSubcontractor);

        String encryptedPassword = passwordEncryptionService.encryptPassword(enterpriseAndUserRequestDto.getPassword());

        UserProviderSubcontractor newUserProviderSubcontractor = UserProviderSubcontractor.builder()
                .cpf(enterpriseAndUserRequestDto.getCpf())
                .password(encryptedPassword)
                .position(enterpriseAndUserRequestDto.getPosition())
                .role(enterpriseAndUserRequestDto.getRole())
                .firstName(enterpriseAndUserRequestDto.getName())
                .surname(enterpriseAndUserRequestDto.getSurname())
                .email(enterpriseAndUserRequestDto.getEmail())
                .telephone(enterpriseAndUserRequestDto.getPhone())
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
