package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogClient;
import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.exceptions.UnprocessableEntityException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.clients.client.ClientRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import bl.tech.realiza.gateways.responses.queue.SetupMessage;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.queue.SetupAsyncQueueProducer;
import bl.tech.realiza.services.setup.SetupService;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
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
    private final FileRepository fileRepository;
    private final BranchRepository branchRepository;
    private final SetupService setupService;
    private final SetupAsyncQueueProducer setupQueueProducer;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;

    @Override
    public ClientResponseDto save(ClientRequestDto clientRequestDto) {

        Client client = clientRepository.findByCnpj(clientRequestDto.getCnpj())
                .orElse(null);

        if (client != null) {
            throw new UnprocessableEntityException("CNPJ already exists!");
        }

        Client newClient = Client.builder()
                .cnpj(clientRequestDto.getCnpj())
                .tradeName(clientRequestDto.getTradeName())
                .corporateName(clientRequestDto.getCorporateName())
                .email(clientRequestDto.getEmail())
                .telephone(clientRequestDto.getTelephone())
                .cep(clientRequestDto.getCep())
                .state(clientRequestDto.getState())
                .city(clientRequestDto.getCity())
                .address(clientRequestDto.getAddress())
                .number(clientRequestDto.getNumber())
                .isUltragaz(clientRequestDto.getIsUltragaz())
                .build();

        Client savedClient = clientRepository.save(newClient);

        setupQueueProducer.sendSetup(new SetupMessage("NEW_CLIENT", savedClient.getIdClient(), null, null, null, null, null));

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogClient(
                        savedClient,
                        userResponsible.getEmail() + " created client " + savedClient.getCorporateName(),
                        AuditLogClient.AuditLogClientActions.CREATE,
                        userResponsible);
            }
        }

        return ClientResponseDto.builder()
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
                .isUltragaz(savedClient.getIsUltragaz())
                .build();
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
                .isUltragaz(client.getIsUltragaz())
                .build();

        return Optional.of(clientResponse);
    }

    @Override
    public Page<ClientResponseDto> findAll(Pageable pageable) {
        Page<Client> clientPage = clientRepository.findAllByIsActiveIsTrue(pageable);

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
                            .isUltragaz(client.getIsUltragaz())
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

        Client savedClient = clientRepository.save(client);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogClient(
                        savedClient,
                        userResponsible.getEmail() + " updated client " + savedClient.getCorporateName(),
                        AuditLogClient.AuditLogClientActions.UPDATE,
                        userResponsible);
            }
        }

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
                .isUltragaz(savedClient.getIsUltragaz())
                .build();

        return Optional.of(clientResponse);
    }

    @Override
    public void delete(String id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogClient(
                        client,
                        userResponsible.getEmail() + " deleted client " + client.getCorporateName(),
                        AuditLogClient.AuditLogClientActions.UPDATE,
                        userResponsible);
            }
        }
        clientRepository.deleteById(id);
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

    @Override
    public Optional<ClientResponseDto> findClientbyBranch(String idBranch) {
        Branch branch = branchRepository.findById(idBranch).orElseThrow(() -> new NotFoundException("Branch not found"));

        FileDocument fileDocument = null;

        Client client = clientRepository.findById(branch.getClient().getIdClient()).orElseThrow(() -> new NotFoundException("Client not found"));

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
                .isUltragaz(client.getIsUltragaz())
                .build();

        return Optional.of(clientResponse);
    }
}
