package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.requests.enterprises.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.requests.clients.ClientRequestDto;
import bl.tech.realiza.gateways.responses.enterprises.EnterpriseAndUserResponseDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.interfaces.clients.CrudClient;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CrudClientImpl implements CrudClient {

    private final ClientRepository clientRepository;
    private final UserClientRepository userClientRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    private final FileRepository fileRepository;

    @Override
    public ClientResponseDto save(ClientRequestDto clientRequestDto, MultipartFile file) {
        FileDocument fileDocument = null;
        String fileDocumentId = null;
        FileDocument savedFileDocument= null;

        if (file != null && !file.isEmpty()) {
            try {
                fileDocument = FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .data(file.getBytes())
                        .build();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new NotFoundException("Could not build logo file");
            }

            try {
                savedFileDocument = fileRepository.save(fileDocument);
                fileDocumentId = savedFileDocument.getIdDocumentAsString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new NotFoundException("Could not save logo file");
            }
        }

        Client newClient = Client.builder()
                .cnpj(clientRequestDto.getCnpj())
                .tradeName(clientRequestDto.getTradeName())
                .corporateName(clientRequestDto.getCorporateName())
                .logo(fileDocumentId)
                .email(clientRequestDto.getEmail())
                .telephone(clientRequestDto.getTelephone())
                .cep(clientRequestDto.getCep())
                .state(clientRequestDto.getState())
                .city(clientRequestDto.getCity())
                .address(clientRequestDto.getAddress())
                .number(clientRequestDto.getNumber())
                .build();

        Client savedClient = clientRepository.save(newClient);

        ClientResponseDto clientResponse = ClientResponseDto.builder()
                .idClient(savedClient.getIdClient())
                .cnpj(savedClient.getCnpj())
                .tradeName(savedClient.getTradeName())
                .corporateName(savedClient.getCorporateName())
                .logoId(savedClient.getLogo())
                .email(savedClient.getEmail())
                .telephone(savedClient.getTelephone())
                .cep(savedClient.getCep())
                .state(savedClient.getState())
                .city(savedClient.getCity())
                .address(savedClient.getAddress())
                .number(savedClient.getNumber())
                .build();

        return clientResponse;
    }

    @Override
    public Optional<ClientResponseDto> findOne(String id) {
        FileDocument fileDocument = null;

        Optional<Client> clientOptional = clientRepository.findById(id);
        Client client = clientOptional.orElseThrow(() -> new NotFoundException("Client not found"));

        if (client.getLogo() != null) {
            Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(client.getLogo()));
            fileDocument = fileDocumentOptional.orElseThrow(() -> new NotFoundException("Logo not found"));
        }

        ClientResponseDto clientResponse = ClientResponseDto.builder()
                .idClient(client.getIdClient())
                .cnpj(client.getCnpj())
                .tradeName(client.getTradeName())
                .corporateName(client.getCorporateName())
                .logoData(fileDocument != null ? fileDocument.getData() : null)
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .cep(client.getCep())
                .state(client.getState())
                .city(client.getCity())
                .address(client.getAddress())
                .number(client.getNumber())
                .build();

        return Optional.of(clientResponse);
    }

    @Override
    public Page<ClientResponseDto> findAll(Pageable pageable) {
        Page<Client> clientPage = clientRepository.findAll(pageable);

        Page<ClientResponseDto> clientResponseDtoPage = clientPage.map(
                client -> {
                    FileDocument fileDocument = null;
                    if (client.getLogo() != null && !client.getLogo().isEmpty()) {
                        Optional<FileDocument> fileDocumentOptional = fileRepository.findById(new ObjectId(client.getLogo()));
                        fileDocument = fileDocumentOptional.orElse(null);
                    }

                    return ClientResponseDto.builder()
                            .idClient(client.getIdClient())
                            .cnpj(client.getCnpj())
                            .tradeName(client.getTradeName())
                            .corporateName(client.getCorporateName())
                            .email(client.getEmail())
                            .logoData(fileDocument != null ? fileDocument.getData() : null)
                            .telephone(client.getTelephone())
                            .cep(client.getCep())
                            .state(client.getState())
                            .city(client.getCity())
                            .address(client.getAddress())
                            .number(client.getNumber())
                            .build();
                }
        );

        return clientResponseDtoPage;
    }

    @Override
    public Optional<ClientResponseDto> update(String id, ClientRequestDto clientRequestDto) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        Client client = clientOptional.orElseThrow(() -> new NotFoundException("Client not found"));

        client.setCnpj(clientRequestDto.getCnpj() != null ? clientRequestDto.getCnpj() : client.getCnpj());
        client.setTradeName(clientRequestDto.getTradeName() != null ? clientRequestDto.getTradeName() : client.getTradeName());
        client.setCorporateName(clientRequestDto.getCorporateName() != null ? clientRequestDto.getCorporateName() : client.getCorporateName());
        client.setEmail(clientRequestDto.getEmail() != null ? clientRequestDto.getEmail() : client.getEmail());
        client.setTelephone(clientRequestDto.getTelephone() != null ? clientRequestDto.getTelephone() : client.getTelephone());
        client.setCep(clientRequestDto.getCep() != null ? clientRequestDto.getCep() : client.getCep());
        client.setState(client.getState() != null ? clientRequestDto.getState() : client.getState());
        client.setCity(client.getCity() != null ? clientRequestDto.getCity() : client.getCity());
        client.setAddress(client.getAddress() != null ? clientRequestDto.getAddress() : client.getAddress());
        client.setNumber(client.getNumber() != null ? clientRequestDto.getNumber() : client.getNumber());
        client.setIsActive(clientRequestDto.getIsActive() != null ? clientRequestDto.getIsActive() : client.getIsActive());

        Client savedClient = clientRepository.save(client);

        ClientResponseDto clientResponse = ClientResponseDto.builder()
                .idClient(savedClient.getIdClient())
                .cnpj(savedClient.getCnpj())
                .tradeName(savedClient.getTradeName())
                .corporateName(savedClient.getCorporateName())
                .email(savedClient.getEmail())
                .telephone(savedClient.getTelephone())
                .cep(savedClient.getCep())
                .state(savedClient.getState())
                .city(savedClient.getCity())
                .address(savedClient.getAddress())
                .number(savedClient.getNumber())
                .build();

        return Optional.of(clientResponse);
    }

    @Override
    public void delete(String id) {
        clientRepository.deleteById(id);
    }

    @Override
    public EnterpriseAndUserResponseDto saveBoth(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto) {
        Client newClient = Client.builder()
                .cnpj(enterpriseAndUserRequestDto.getCnpj())
                .tradeName(enterpriseAndUserRequestDto.getTradeName())
                .corporateName(enterpriseAndUserRequestDto.getCorporateName())
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

        EnterpriseAndUserResponseDto enterpriseAndUserResponseDto = EnterpriseAndUserResponseDto.builder()
                .idClient(savedClient.getIdClient())
                .cnpj(savedClient.getCnpj())
                .tradeName(savedClient.getTradeName())
                .corporateName(savedClient.getCorporateName())
                .email(savedClient.getEmail())
                .telephone(savedClient.getTelephone())
                .idUser(savedUserClient.getIdUser())
                .cpf(savedUserClient.getCpf())
                .position(savedUserClient.getPosition())
                .role(savedUserClient.getRole())
                .firstName(savedUserClient.getFirstName())
                .surname(savedUserClient.getSurname())
                .build();

        return enterpriseAndUserResponseDto;
    }

    @Override
    public String changeLogo(String id, MultipartFile file) throws IOException {
        Optional<Client> clientOptional = clientRepository.findById(id);
        Client client = clientOptional.orElseThrow(() -> new NotFoundException("Client not found"));

        if (file != null && !file.isEmpty()) {
            FileDocument fileDocument = FileDocument.builder()
                    .name(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .data(file.getBytes())
                    .build();

            if (client.getLogo() != null) {
                fileRepository.deleteById(new ObjectId(client.getLogo()));
            }
            FileDocument savedFileDocument = fileRepository.save(fileDocument);
            client.setLogo(savedFileDocument.getIdDocumentAsString());
        }

        clientRepository.save(client);

        return "Logo updated successfully";
    }
}
