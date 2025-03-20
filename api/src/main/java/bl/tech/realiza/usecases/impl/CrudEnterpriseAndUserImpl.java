package bl.tech.realiza.usecases.impl;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.enterprises.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.responses.services.EnterpriseAndUserResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.impl.contracts.CrudActivityImpl;
import bl.tech.realiza.usecases.interfaces.CrudEnterpriseAndUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private final BranchRepository branchRepository;
    private final CrudActivityImpl crudActivity;

    @Override
    public EnterpriseAndUserResponseDto saveBothClient(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto) {

        if (enterpriseAndUserRequestDto.getPassword() == null || enterpriseAndUserRequestDto.getPassword().isEmpty()) {
            throw new BadRequestException("Invalid password");
        }

        Optional<Client> clientOptional = clientRepository.findByCnpj(enterpriseAndUserRequestDto.getCnpj());
        Client client = clientOptional.orElse(null);

        if (clientOptional.isPresent()) {
            client.setTradeName(enterpriseAndUserRequestDto.getTradeName() != null ? enterpriseAndUserRequestDto.getTradeName() : client.getTradeName());
            client.setCorporateName(enterpriseAndUserRequestDto.getCorporateName() != null ? enterpriseAndUserRequestDto.getCorporateName() : client.getCorporateName());
            client.setEmail(enterpriseAndUserRequestDto.getEmail() != null ? enterpriseAndUserRequestDto.getEmail() : client.getEmail());
        } else {
            client = Client.builder()
                    .cnpj(enterpriseAndUserRequestDto.getCnpj())
                    .tradeName(enterpriseAndUserRequestDto.getTradeName())
                    .corporateName(enterpriseAndUserRequestDto.getCorporateName())
                    .email(enterpriseAndUserRequestDto.getEmail())
                    .telephone(enterpriseAndUserRequestDto.getPhone())
                    .build();
        }

        Client savedClient = clientRepository.save(client);

        Branch newBranch = Branch.builder()
                .name(enterpriseAndUserRequestDto.getCorporateName() + " Matriz")
                .cnpj(enterpriseAndUserRequestDto.getCnpj())
                .email(enterpriseAndUserRequestDto.getEmail())
                .client(savedClient)
                .build();

        Branch savedBranch = branchRepository.save(newBranch);

        crudActivity.transferFromRepo(savedBranch.getIdBranch());

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
                .isActive(true)
                .branch(savedBranch)
                .build();

        UserClient savedUserClient = userClientRepository.save(newUserClient);

        EnterpriseAndUserResponseDto clientAndUserClientResponseDto = EnterpriseAndUserResponseDto.builder()
                .idEnterprise(savedClient.getIdClient())
                .cnpj(savedClient.getCnpj())
                .tradeName(savedClient.getTradeName())
                .corporateName(savedClient.getCorporateName())
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

        if (enterpriseAndUserRequestDto.getPassword() == null || enterpriseAndUserRequestDto.getPassword().isEmpty()) {
            throw new BadRequestException("Invalid password");
        }

        ProviderSupplier providerSupplier = providerSupplierRepository.findById(enterpriseAndUserRequestDto.getIdCompany())
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        providerSupplier.setTradeName(enterpriseAndUserRequestDto.getTradeName() != null ? enterpriseAndUserRequestDto.getTradeName() : providerSupplier.getTradeName());
        providerSupplier.setCep(enterpriseAndUserRequestDto.getCep() != null ? enterpriseAndUserRequestDto.getCep() : providerSupplier.getCep());
        providerSupplier.setState(enterpriseAndUserRequestDto.getState() != null ? enterpriseAndUserRequestDto.getState() : providerSupplier.getState());
        providerSupplier.setCity(enterpriseAndUserRequestDto.getCity() != null ? enterpriseAndUserRequestDto.getCity() : providerSupplier.getCity());
        providerSupplier.setAddress(enterpriseAndUserRequestDto.getAddress() != null ? enterpriseAndUserRequestDto.getAddress() : providerSupplier.getAddress());
        providerSupplier.setNumber(enterpriseAndUserRequestDto.getNumber() != null ? enterpriseAndUserRequestDto.getNumber() : providerSupplier.getNumber());

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(providerSupplier);

        String encryptedPassword = passwordEncryptionService.encryptPassword(enterpriseAndUserRequestDto.getPassword());

        UserProviderSupplier newUserProviderSupplier = UserProviderSupplier.builder()
                .cpf(enterpriseAndUserRequestDto.getCpf())
                .password(encryptedPassword)
                .position(enterpriseAndUserRequestDto.getPosition())
                .role(User.Role.ROLE_SUPPLIER_RESPONSIBLE)
                .firstName(enterpriseAndUserRequestDto.getName())
                .surname(enterpriseAndUserRequestDto.getSurname())
                .email(savedProviderSupplier.getEmail())
                .telephone(enterpriseAndUserRequestDto.getPhone())
                .isActive(true)
                .providerSupplier(savedProviderSupplier)
                .build();

        UserProviderSupplier savedUserProviderSupplier = userProviderSupplierRepository.save(newUserProviderSupplier);

        return EnterpriseAndUserResponseDto.builder()
                .idEnterprise(savedProviderSupplier.getIdProvider())
                .cnpj(savedProviderSupplier.getCnpj())
                .corporateName(savedProviderSupplier.getCorporateName())
                .tradeName(savedProviderSupplier.getTradeName())
                .email(savedProviderSupplier.getEmail())
                .phone(savedUserProviderSupplier.getTelephone())
                .idUser(savedUserProviderSupplier.getIdUser())
                .cpf(savedUserProviderSupplier.getCpf())
                .name(savedUserProviderSupplier.getFirstName())
                .surname(savedUserProviderSupplier.getSurname())
                .position(savedUserProviderSupplier.getPosition())
                .role(savedUserProviderSupplier.getRole())
                .build();
    }

    @Override
    public EnterpriseAndUserResponseDto saveBothSubcontractor(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto) {

        if (enterpriseAndUserRequestDto.getPassword() == null || enterpriseAndUserRequestDto.getPassword().isEmpty()) {
            throw new BadRequestException("Invalid password");
        }

        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findByCnpj(enterpriseAndUserRequestDto.getCnpj());
        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElse(null);

        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(enterpriseAndUserRequestDto.getIdCompany());
        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Provider supplier not found"));

        if (providerSubcontractorOptional.isPresent()) {
            providerSubcontractor.setTradeName(enterpriseAndUserRequestDto.getTradeName() != null ? enterpriseAndUserRequestDto.getTradeName() : providerSubcontractor.getTradeName());
            providerSubcontractor.setCorporateName(enterpriseAndUserRequestDto.getCorporateName() != null ? enterpriseAndUserRequestDto.getCorporateName() : providerSubcontractor.getCorporateName());
            providerSubcontractor.setEmail(enterpriseAndUserRequestDto.getEmail() != null ? enterpriseAndUserRequestDto.getEmail() : providerSubcontractor.getEmail());
            providerSubcontractor.setProviderSupplier(providerSupplier);
        } else {
            providerSubcontractor = ProviderSubcontractor.builder()
                    .cnpj(enterpriseAndUserRequestDto.getCnpj())
                    .tradeName(enterpriseAndUserRequestDto.getTradeName())
                    .corporateName(enterpriseAndUserRequestDto.getCorporateName())
                    .email(enterpriseAndUserRequestDto.getEmail())
                    .providerSupplier(providerSupplier)
                    .build();
        }

        ProviderSubcontractor savedProviderSubcontractor = providerSubcontractorRepository.save(providerSubcontractor);

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
                .isActive(true)
                .providerSubcontractor(savedProviderSubcontractor)
                .build();

        UserProviderSubcontractor savedUserProviderSubcontractor = userProviderSubcontractorRepository.save(newUserProviderSubcontractor);

        return EnterpriseAndUserResponseDto.builder()
                .idEnterprise(savedProviderSubcontractor.getIdProvider())
                .cnpj(savedProviderSubcontractor.getCnpj())
                .corporateName(savedProviderSubcontractor.getCorporateName())
                .tradeName(savedProviderSubcontractor.getTradeName())
                .email(savedProviderSubcontractor.getEmail())
                .phone(savedUserProviderSubcontractor.getTelephone())
                .idUser(savedUserProviderSubcontractor.getIdUser())
                .cpf(savedUserProviderSubcontractor.getCpf())
                .name(savedUserProviderSubcontractor.getFirstName())
                .surname(savedUserProviderSubcontractor.getSurname())
                .position(savedUserProviderSubcontractor.getPosition())
                .role(savedUserProviderSubcontractor.getRole())
                .build();
    }
}
