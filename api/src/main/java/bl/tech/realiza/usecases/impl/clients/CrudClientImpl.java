package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.exceptions.UnprocessableEntityException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.services.FileRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.clients.branch.BranchCreateRequestDto;
import bl.tech.realiza.gateways.requests.clients.client.ClientRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import bl.tech.realiza.services.queue.setup.SetupMessage;
import bl.tech.realiza.services.GoogleCloudService;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.queue.setup.SetupQueueProducer;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.clients.CrudClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.*;

@Service
@RequiredArgsConstructor
public class CrudClientImpl implements CrudClient {

    private final ClientRepository clientRepository;
    private final FileRepository fileRepository;
    private final BranchRepository branchRepository;
    private final SetupQueueProducer setupQueueProducer;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final CrudBranchImpl crudBranchImpl;
    private final GoogleCloudService googleCloudService;

    @Override
    public ClientResponseDto save(ClientRequestDto clientRequestDto, Boolean profilesFromRepo) {

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

        setupQueueProducer.send(new SetupMessage("NEW_CLIENT",
                savedClient.getIdClient(),
                null,
                null,
                null,
                null,
                null,
                null));

        if (profilesFromRepo == null) {
            profilesFromRepo = false;
        }

        if (profilesFromRepo) {
            setupQueueProducer.send(new SetupMessage("NEW_CLIENT_PROFILES",
                    savedClient.getIdClient(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null));
        }

        crudBranchImpl.save(BranchCreateRequestDto.builder()
                .name(savedClient.getTradeName() != null
                        ? savedClient.getTradeName()
                        : "Base")
                .cnpj(savedClient.getCnpj())
                .cep(savedClient.getCep())
                .state(savedClient.getState())
                .city(savedClient.getCity())
                .email(savedClient.getEmail())
                .telephone(savedClient.getTelephone())
                .address(savedClient.getAddress())
                .number(savedClient.getNumber())
                .base(true)
                .client(savedClient.getIdClient())
                .build());

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        savedClient.getIdClient(),
                        CLIENT,
                        userResponsible.getFullName() + " criou cliente "
                                + savedClient.getCorporateName(),
                        null,
                        CREATE,
                        userResponsible.getIdUser());
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
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        String signedUrl = null;
        if (client.getLogo() != null) {
            if (client.getLogo().getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(client.getLogo().getUrl(), 15);
            }
        }

        return Optional.of(ClientResponseDto.builder()
                .idClient(client.getIdClient())
                .cnpj(client.getCnpj())
                .tradeName(client.getTradeName())
                .corporateName(client.getCorporateName())
                .logoSignedUrl(signedUrl)
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .cep(client.getCep())
                .state(client.getState())
                .city(client.getCity())
                .address(client.getAddress())
                .number(client.getNumber())
                .isUltragaz(client.getIsUltragaz())
                .build());
    }

    @Override
    public Page<ClientResponseDto> findAll(Pageable pageable) {
        Page<Client> clientPage = clientRepository.findAllByIsActiveIsTrue(pageable);

        return clientPage.map(
                client -> {
                    String signedUrl = null;
                    if (client.getLogo() != null) {
                        if (client.getLogo().getUrl() != null) {
                            signedUrl = googleCloudService.generateSignedUrl(client.getLogo().getUrl(), 15);
                        }
                    }

                    return ClientResponseDto.builder()
                            .idClient(client.getIdClient())
                            .cnpj(client.getCnpj())
                            .tradeName(client.getTradeName())
                            .corporateName(client.getCorporateName())
                            .email(client.getEmail())
                            .logoSignedUrl(signedUrl)
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
    }

    @Override
    public Optional<ClientResponseDto> update(String id, ClientRequestDto clientRequestDto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        client.setCnpj(clientRequestDto.getCnpj() != null
                ? clientRequestDto.getCnpj()
                : client.getCnpj());
        client.setTradeName(clientRequestDto.getTradeName() != null
                ? clientRequestDto.getTradeName()
                : client.getTradeName());
        client.setCorporateName(clientRequestDto.getCorporateName() != null
                ? clientRequestDto.getCorporateName()
                : client.getCorporateName());
        client.setEmail(clientRequestDto.getEmail() != null
                ? clientRequestDto.getEmail()
                : client.getEmail());
        client.setTelephone(clientRequestDto.getTelephone() != null
                ? clientRequestDto.getTelephone()
                : client.getTelephone());
        client.setCep(clientRequestDto.getCep() != null
                ? clientRequestDto.getCep()
                : client.getCep());
        client.setState(clientRequestDto.getState() != null
                ? clientRequestDto.getState()
                : client.getState());
        client.setCity(clientRequestDto.getCity() != null
                ? clientRequestDto.getCity()
                : client.getCity());
        client.setAddress(clientRequestDto.getAddress() != null
                ? clientRequestDto.getAddress()
                : client.getAddress());
        client.setNumber(clientRequestDto.getNumber() != null
                ? clientRequestDto.getNumber()
                : client.getNumber());

        Client savedClient = clientRepository.save(client);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        savedClient.getIdClient(),
                        CLIENT,
                        userResponsible.getFullName() + " atualizou cliente "
                                + savedClient.getCorporateName(),
                        null,
                        UPDATE,
                        userResponsible.getIdUser());
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
                auditLogServiceImpl.createAuditLog(
                        client.getIdClient(),
                        CLIENT,
                        userResponsible.getFullName() + " deletou cliente "
                                + client.getCorporateName(),
                        null,
                        UPDATE,
                        userResponsible.getIdUser());
            }
        }
        clientRepository.deleteById(id);
    }

    @Override
    public String changeLogo(String id, MultipartFile file) throws IOException {
        if (file != null) {
            if (file.getSize() > 1024 * 1024) { // 1 MB
                throw new BadRequestException("Arquivo muito grande.");
            }
        }
        FileDocument savedFileDocument = null;

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        if (file != null && !file.isEmpty()) {
            try {
                String gcsUrl = googleCloudService.uploadFile(file, "enterprise-logos");

                if (client.getLogo() != null) {
                    googleCloudService.deleteFile(client.getLogo().getUrl());
                }
                savedFileDocument = fileRepository.save(FileDocument.builder()
                        .name(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .url(gcsUrl)
                        .build());
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new EntityNotFoundException(e);
            }
            client.setLogo(savedFileDocument);
        }

        clientRepository.save(client);

        return "Logo updated successfully";
    }

    @Override
    public Optional<ClientResponseDto> findClientbyBranch(String idBranch) {
        Branch branch = branchRepository.findById(idBranch)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        Client client = branch.getClient();

        String signedUrl = null;
        if (client.getLogo() != null) {
            if (client.getLogo().getUrl() != null) {
                signedUrl = googleCloudService.generateSignedUrl(client.getLogo().getUrl(), 15);
            }
        }

        ClientResponseDto clientResponse = ClientResponseDto.builder()
                .idClient(client.getIdClient())
                .cnpj(client.getCnpj())
                .tradeName(client.getTradeName())
                .corporateName(client.getCorporateName())
                .logoSignedUrl(signedUrl)
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
