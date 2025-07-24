package bl.tech.realiza.usecases.impl;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.ItemManagementRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserManagerRepository;
import bl.tech.realiza.gateways.requests.services.email.EmailEnterpriseInviteRequestDto;
import bl.tech.realiza.gateways.requests.services.email.EmailRegistrationDeniedRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.document.ItemManagementDocumentDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.document.ItemManagementDocumentResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserResponseDto;
import bl.tech.realiza.services.auth.TokenManagerService;
import bl.tech.realiza.services.email.EmailSender;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import bl.tech.realiza.usecases.interfaces.users.CrudNotification;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static bl.tech.realiza.domains.contract.Contract.IsActive.*;

@Service
@RequiredArgsConstructor
public class CrudItemManagementImpl implements CrudItemManagement {
    private final UserClientRepository userClientRepository;
    private final ItemManagementRepository itemManagementRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ProviderRepository providerRepository;
    private final EmailSender emailSender;
    private final UserManagerRepository userManagerRepository;
    private final ContractProviderSupplierRepository contractProviderSupplierRepository;
    private final ContractRepository contractRepository;
    private final TokenManagerService tokenManagerService;
    private final CrudNotification crudNotification;

    @Override
    public ItemManagementUserResponseDto saveUserSolicitation(ItemManagementUserRequestDto itemManagementUserRequestDto) {

        User requester = userClientRepository.findById(itemManagementUserRequestDto.getIdRequester())
                .orElse(null);

        if (requester == null) {
            requester = userManagerRepository.findById(itemManagementUserRequestDto.getIdRequester())
                    .orElseThrow(() -> new NotFoundException("Requester not found"));
        }

        User newUser = userClientRepository.findById(itemManagementUserRequestDto.getIdNewUser())
                .orElseThrow(() -> new NotFoundException("New user not found"));

        ItemManagement solicitation = itemManagementRepository.save(ItemManagement.builder()
                .solicitationType(itemManagementUserRequestDto.getSolicitationType())
                .requester(requester)
                .newUser(newUser)
                .build());

        crudNotification.saveUserNotificationForRealizaUsers(solicitation);

        return toItemManagementUserResponseDto(solicitation, requester, newUser);
    }

    @Override
    public Page<ItemManagementUserResponseDto> findAllUserSolicitation(Pageable pageable) {
        Page<ItemManagement> itemManagementPage = itemManagementRepository.findAllByNewUserIsNotNull(pageable);

        return itemManagementPage.map(
                itemManagement ->
                        toItemManagementUserResponseDto(
                                itemManagement,
                                itemManagement.getRequester(),
                                itemManagement.getNewUser()));
    }

    @Override
    public ItemManagementUserDetailsResponseDto findUserSolicitationDetails(String idSolicitation) {
        ItemManagement itemManagement = itemManagementRepository.findById(idSolicitation)
                .orElseThrow(() -> new NotFoundException("Solicitation not found"));

        return toItemManagementUserDetailsResponseDto(itemManagement);
    }
    // já finalizado, verificar as rotas e depois as chamadas
    @Override
    public ItemManagementProviderResponseDto saveProviderSolicitation(ItemManagementProviderRequestDto itemManagementProviderRequestDto) {

        User requester = userClientRepository.findById(itemManagementProviderRequestDto.getIdRequester())
                .orElse(null);
        if (requester == null) {
            requester = userManagerRepository.findById(itemManagementProviderRequestDto.getIdRequester())
                    .orElseThrow(() -> new NotFoundException("Requester not found"));
        }

        Provider newProviderSupplier = providerSupplierRepository.findById(itemManagementProviderRequestDto.getIdNewProvider())
                .orElseThrow(() -> new NotFoundException("New Provider not found"));

        ItemManagement solicitation = itemManagementRepository.save(ItemManagement.builder()
                .solicitationType(itemManagementProviderRequestDto.getSolicitationType())
                .requester(requester)
                .newProvider(newProviderSupplier)
                .build());

        crudNotification.saveProviderNotificationForRealizaUsers(solicitation);

        return toItemManagementProviderResponseDto(solicitation, requester, newProviderSupplier);
    }

    @Override
    public Page<ItemManagementProviderResponseDto> findAllProviderSolicitation(Pageable pageable) {

        Page<ItemManagement> itemManagementPage = itemManagementRepository.findAllByNewProviderIsNotNull(pageable);

        return itemManagementPage.map(
                itemManagement ->
                        toItemManagementProviderResponseDto(
                                itemManagement,
                                itemManagement.getRequester(),
                                itemManagement.getNewProvider())
        );
    }

    @Override
    public ItemManagementProviderDetailsResponseDto findProviderSolicitationDetails(String idSolicitation) {
        return toItemManagementProviderDetailsResponseDto(itemManagementRepository.findById(idSolicitation)
                .orElseThrow(() -> new NotFoundException("Solicitation not found")));
    }

    @Override
    public String approveSolicitation(String idSolicitation) {
        ItemManagement solicitation = itemManagementRepository.findById(idSolicitation)
                .orElseThrow(() -> new NotFoundException("Solicitation not found"));

        if (solicitation.getNewUser() != null) {
            UserClient userClient = userClientRepository.findById(solicitation.getNewUser().getIdUser())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            userClient.setIsActive(true);

            userClientRepository.save(userClient);

            // enviar e-mail convite pedindo para finalizar cadastro
        } else if (solicitation.getNewProvider() != null) {

            Provider provider = providerRepository.findById(solicitation.getNewProvider().getIdProvider())
                    .orElseThrow(() -> new NotFoundException("Provider not found"));

            provider.setIsActive(true);

            providerRepository.save(provider);


            Contract contract = contractRepository.findById(
                            contractProviderSupplierRepository.findTopByProviderSupplier_IdProviderAndIsActiveOrderByCreationDateDesc(provider.getIdProvider(), PENDENTE).getIdContract())
                    .orElseThrow(() -> new NotFoundException("Contract not found"));

            contract.setIsActive(ATIVADO);
            contractRepository.save(contract);

            String token = tokenManagerService.generateToken();
            solicitation.setInvitationToken(token);
            itemManagementRepository.save(solicitation);

            if (provider.getEmail() != null) {
                emailSender.sendNewProviderEmail(EmailEnterpriseInviteRequestDto.builder()
                        .email(provider.getEmail())
                        .companyName(provider.getCorporateName())
                        .requesterName(contract instanceof ContractProviderSupplier ? ((ContractProviderSupplier) contract).getBranch().getName() : ((ContractProviderSubcontractor) contract).getContractProviderSupplier().getProviderSupplier().getCorporateName())
                        .serviceName(contract.getServiceName())
                        .startDate(contract.getDateStart())
                        .requesterBranchName(contract instanceof ContractProviderSupplier ? ((ContractProviderSupplier) contract).getBranch().getName() : ((ContractProviderSubcontractor) contract).getContractProviderSupplier().getBranch().getName())
                        .responsibleName(contract.getResponsible().getFirstName() + " " + contract.getResponsible().getSurname())
                        .contractReference(contract.getContractReference())
                        .idCompany(provider.getIdProvider())
                        .idBranch(contract instanceof ContractProviderSupplier ? ((ContractProviderSupplier) contract).getBranch().getIdBranch() : null)
                        .idSupplier(contract instanceof ContractProviderSubcontractor ? ((ContractProviderSubcontractor) contract).getContractProviderSupplier().getProviderSupplier().getIdProvider() : null)
                        .build(), token);
            }
        }

        solicitation.setStatus(ItemManagement.Status.APPROVED);

        itemManagementRepository.save(solicitation);

        return "Solicitation approved";
    }

    @Override
    public String denySolicitation(String idSolicitation) {

        ItemManagement solicitation = itemManagementRepository.findById(idSolicitation)
                .orElseThrow(() -> new NotFoundException("Solicitation not found"));

        if (solicitation.getNewUser() != null) {
            UserClient userClient = userClientRepository.findById(solicitation.getNewUser().getIdUser())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            userClient.setDenied(true);

            userClientRepository.save(userClient);
        } else if (solicitation.getNewProvider() != null) {
            Provider provider = providerRepository.findById(solicitation.getNewProvider().getIdProvider())
                    .orElseThrow(() -> new NotFoundException("Provider not found"));

            provider.setDenied(true);

            providerRepository.save(provider);

            Contract contract = contractRepository.findById(
                            contractProviderSupplierRepository.findTopByProviderSupplier_IdProviderAndIsActiveOrderByCreationDateDesc(provider.getIdProvider(), PENDENTE).getIdContract())
                    .orElseThrow(() -> new NotFoundException("Contract not found"));

            contract.setIsActive(NEGADO);
            contractRepository.save(contract);

            String token = tokenManagerService.generateToken();
            solicitation.setInvitationToken(token);

            if (provider.getEmail() != null) {
                emailSender.sendNewProviderDeniedEmail(EmailRegistrationDeniedRequestDto.builder()
                        .email(provider.getEmail())
                        .responsibleName(contract.getResponsible().getFirstName() + " " + contract.getResponsible().getSurname())
                        .enterpriseName(provider.getCorporateName())
                        // adicionar motivos ao sistema
                        .reason(null)
                        .build());
            }
        }

        solicitation.setStatus(ItemManagement.Status.DENIED);

        itemManagementRepository.save(solicitation);

        if (solicitation.getInvitationToken() != null) {
            tokenManagerService.revokeToken(solicitation.getInvitationToken());
            solicitation.setInvitationToken(null); // limpa do banco também
        }


        return "Solicitation denied";
    }

    @Override
    public void deleteSolicitation(String idSolicitation) {
        itemManagementRepository.deleteById(idSolicitation);
    }

    @Override
    public Page<ItemManagementDocumentResponseDto> findAllDocumentSolicitation(Pageable pageable) {
        Page<ItemManagement> itemManagementPage = itemManagementRepository.findAllByContractDocumentIsNotNull(pageable);
        return itemManagementPage.map(this::toItemManagementDocumentResponseDto);
    }

    @Override
    public ItemManagementDocumentDetailsResponseDto findDocumentSolicitationDetails(String idSolicitation) {
        return toItemManagementDocumentDetailsResponseDto(itemManagementRepository.findById(idSolicitation)
                .orElseThrow(() -> new NotFoundException("Solicitation not found")));
    }

    private ItemManagementUserResponseDto toItemManagementUserResponseDto(ItemManagement itemManagement, User requester, User newUser) {
        User actualUser = (User) Hibernate.unproxy(newUser);


        if (!(actualUser instanceof UserClient userClient)) {
            throw new IllegalArgumentException("Not a valid user");
        } else {
            return ItemManagementUserResponseDto.builder()
                    .idSolicitation(itemManagement.getIdSolicitation())
                    .userFullName(userClient.getFirstName() + " " + userClient.getSurname())
                    .solicitationType(itemManagement.getSolicitationType())
                    .clientTradeName(userClient.getBranch() != null
                            ? (userClient.getBranch().getClient() != null
                                ? userClient.getBranch().getClient().getCorporateName()
                                : null)
                            : null)
                    .clientCnpj(userClient.getBranch().getClient().getCnpj())
                    .requesterFullName(requester.getFirstName() + " " + requester.getSurname())
                    .requesterEmail(requester.getEmail())
                    .status(itemManagement.getStatus())
                    .branchName(userClient.getBranch() != null
                            ? userClient.getBranch().getName()
                            : null)
                    .creationDate(itemManagement.getCreationDate())
                    .build();
        }
    }

    private ItemManagementProviderResponseDto toItemManagementProviderResponseDto(ItemManagement itemManagement, User requester, Provider newProvider) {
        Provider actualProvider = (Provider) Hibernate.unproxy(newProvider);

        if (!(actualProvider instanceof ProviderSupplier providerSupplier)) {
            throw new IllegalArgumentException("Not a valid provider");
        } else {
            return ItemManagementProviderResponseDto.builder()
                    .idSolicitation(itemManagement.getIdSolicitation())
                    .enterpriseName(providerSupplier.getCorporateName())
                    .solicitationType(itemManagement.getSolicitationType())
                    .clientName(!providerSupplier.getBranches().isEmpty()
                            ? providerSupplier.getBranches().get(0).getClient().getCorporateName()
                            : null)
                    .clientCnpj(!providerSupplier.getBranches().isEmpty()
                            ? providerSupplier.getBranches().get(0).getClient().getCnpj()
                            : null)
                    .requesterName(requester.getFirstName() + " " + requester.getSurname())
                    .requesterEmail(requester.getEmail())
                    .status(itemManagement.getStatus())
                    .branchName((providerSupplier.getBranches() != null && !providerSupplier.getBranches().isEmpty())
                            ? (providerSupplier.getBranches().get(0) != null
                                ? providerSupplier.getBranches().get(0).getName()
                                : null)
                            : null)
                    .creationDate(itemManagement.getCreationDate())
                    .build();
        }
    }

    private ItemManagementDocumentResponseDto toItemManagementDocumentResponseDto(ItemManagement itemManagement) {
        Document document = itemManagement.getContractDocument().getDocument();
        Contract contract = itemManagement.getContractDocument().getContract();
        User requester = itemManagement.getRequester();
        Client client = null;
        Branch branch = null;
        String ownerName = null;
        String enterpriseName = null;
        String clientName = null;
        String clientCnpj = null;
        String branchName = null;

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            branch = contractProviderSupplier.getBranch();
            branchName = branch.getName();
            client = branch.getClient();
            clientName = client.getCorporateName();
            clientCnpj = client.getCnpj();
        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            branch = contractProviderSubcontractor.getContractProviderSupplier().getBranch();
            branchName = branch.getName();
            client = branch.getClient();
            clientName = client.getCorporateName();
            clientCnpj = client.getCnpj();
        }
        if (document instanceof DocumentEmployee documentEmployee) {
            Employee employee = documentEmployee.getEmployee();
            ownerName = documentEmployee.getEmployee().getFullName();
            if (employee.getSupplier() != null) {
               enterpriseName = employee.getSupplier().getCorporateName();
            } else if(employee.getSubcontract() != null) {
               enterpriseName = employee.getSubcontract().getCorporateName();
            }
        } else if (document instanceof DocumentProviderSupplier documentProviderSupplier) {
            Provider provider = documentProviderSupplier.getProviderSupplier();
            ownerName = provider.getCorporateName();
            enterpriseName = provider.getCorporateName();
        } else if (document instanceof DocumentProviderSubcontractor documentProviderSubcontractor) {
            Provider provider = documentProviderSubcontractor.getProviderSubcontractor();
            ownerName = provider.getCorporateName();
            enterpriseName = provider.getCorporateName();
        }

        return ItemManagementDocumentResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .title(document.getTitle())
                .ownerName(ownerName)
                .enterpriseName(enterpriseName)
                .solicitationType(itemManagement.getSolicitationType())
                .clientName(clientName)
                .clientCnpj(clientCnpj)
                .branchName(branchName)
                .requesterName(requester.getFullName())
                .requesterEmail(requester.getEmail())
                .status(itemManagement.getStatus())
                .creationDate(itemManagement.getCreationDate())
                .build();
    }

    private ItemManagementUserDetailsResponseDto toItemManagementUserDetailsResponseDto(ItemManagement itemManagement) {
        User actualUser = (User) Hibernate.unproxy(itemManagement.getNewUser());

        if (!(actualUser instanceof UserClient userClient)) {
            throw new IllegalArgumentException("Not a valid user");
        }

        return ItemManagementUserDetailsResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .solicitationType(itemManagement.getSolicitationType())
                .creationDate(itemManagement.getCreationDate())
                .client(ItemManagementUserDetailsResponseDto.Client.builder()
                        .cnpj(userClient.getBranch().getClient().getCnpj())
                        .tradeName(userClient.getBranch().getClient().getCorporateName())
                        .build())
                .newUser(ItemManagementUserDetailsResponseDto.NewUser.builder()
                        .fullName(userClient.getFirstName() + " " + userClient.getSurname())
                        .cpf(userClient.getCpf())
                        .email(userClient.getEmail())
                        .build())
                .requester(ItemManagementUserDetailsResponseDto.Requester.builder()
                        .fullName(itemManagement.getRequester().getFirstName() + " " + itemManagement.getRequester().getSurname())
                        .email(itemManagement.getRequester().getEmail())
                        .build())
                .build();
    }

    private ItemManagementProviderDetailsResponseDto toItemManagementProviderDetailsResponseDto(ItemManagement itemManagement) {
        Provider actualProvider = (Provider) Hibernate.unproxy(itemManagement.getNewProvider());

        if (!(actualProvider instanceof ProviderSupplier providerSupplier)) {
            throw new IllegalArgumentException("Not a valid provider");
        }

        return ItemManagementProviderDetailsResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .solicitationType(itemManagement.getSolicitationType())
                .creationDate(itemManagement.getCreationDate())
                .client(ItemManagementProviderDetailsResponseDto.Client.builder()
                        .cnpj(!providerSupplier.getBranches().isEmpty()
                                ? providerSupplier.getBranches().get(0).getClient().getCnpj()
                                : null)
                        .tradeName(!providerSupplier.getBranches().isEmpty()
                                ? providerSupplier.getBranches().get(0).getClient().getCorporateName()
                                : null)
                        .build())
                .newProvider(ItemManagementProviderDetailsResponseDto.NewProvider.builder()
                        .cnpj(providerSupplier.getCnpj())
                        .corporateName(providerSupplier.getCorporateName())
                        .build())
                .requester(ItemManagementProviderDetailsResponseDto.Requester.builder()
                        .fullName(itemManagement.getRequester().getFirstName() + " " + itemManagement.getRequester().getSurname())
                        .email(itemManagement.getRequester().getEmail())
                        .build())
                .build();
    }

    private ItemManagementDocumentDetailsResponseDto toItemManagementDocumentDetailsResponseDto(ItemManagement itemManagement) {
        Document document = itemManagement.getContractDocument().getDocument();
        Contract contract = itemManagement.getContractDocument().getContract();
        Client client = null;
        Branch branch = null;
        String clientName = null;
        String clientCnpj = null;
        String enterpriseName = null;
        String enterpriseCnpj = null;
        String ownerName = null;

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            branch = contractProviderSupplier.getBranch();
            client = branch.getClient();
            clientName = client.getCorporateName();
            clientCnpj = client.getCnpj();
        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            branch = contractProviderSubcontractor.getContractProviderSupplier().getBranch();
            client = branch.getClient();
            clientName = client.getCorporateName();
            clientCnpj = client.getCnpj();
        }
        if (document instanceof DocumentEmployee documentEmployee) {
            Employee employee = documentEmployee.getEmployee();
            ownerName = documentEmployee.getEmployee().getFullName();
            if (employee.getSupplier() != null) {
                enterpriseName = employee.getSupplier().getCorporateName();
            } else if(employee.getSubcontract() != null) {
                enterpriseName = employee.getSubcontract().getCorporateName();
            }
        } else if (document instanceof DocumentProviderSupplier documentProviderSupplier) {
            Provider provider = documentProviderSupplier.getProviderSupplier();
            ownerName = provider.getCorporateName();
            enterpriseName = provider.getCorporateName();
        } else if (document instanceof DocumentProviderSubcontractor documentProviderSubcontractor) {
            Provider provider = documentProviderSubcontractor.getProviderSubcontractor();
            ownerName = provider.getCorporateName();
            enterpriseName = provider.getCorporateName();
        }

        return ItemManagementDocumentDetailsResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .solicitationType(itemManagement.getSolicitationType())
                .creationDate(itemManagement.getCreationDate())
                .client(ItemManagementDocumentDetailsResponseDto.Client.builder()
                        .corporateName(clientName)
                        .cnpj(clientCnpj)
                        .build())
                .enterprise(ItemManagementDocumentDetailsResponseDto.Enterprise.builder()
                        .corporateName(enterpriseName)
                        .build())
                .requester(ItemManagementDocumentDetailsResponseDto.Requester.builder()
                        .fullName(itemManagement.getRequester().getFullName())
                        .email(itemManagement.getRequester().getEmail())
                        .build())
                .document(ItemManagementDocumentDetailsResponseDto.Document.builder()
                        .title(itemManagement.getContractDocument().getDocument().getTitle())
                        .ownerName(ownerName)
                        .build())
                .build();
    }
}
