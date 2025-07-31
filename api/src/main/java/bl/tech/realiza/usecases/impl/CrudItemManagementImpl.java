package bl.tech.realiza.usecases.impl;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractDocument;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.ContractDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.ItemManagementRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserManagerRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.services.email.EmailEnterpriseInviteRequestDto;
import bl.tech.realiza.gateways.requests.services.email.EmailRegistrationDeniedRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementContractRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementDocumentRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.contract.ItemManagementContractDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.contract.ItemManagementContractResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.document.ItemManagementDocumentDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.document.ItemManagementDocumentResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserResponseDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.auth.TokenManagerService;
import bl.tech.realiza.services.email.EmailSender;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.users.CrudNotification;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;

import static bl.tech.realiza.domains.contract.Contract.IsActive.*;
import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.CONTRACT;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.DOCUMENT;

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
    private final DocumentRepository documentRepository;
    private final ContractDocumentRepository contractDocumentRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;

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

        Provider newProvider = providerSupplierRepository.findById(itemManagementProviderRequestDto.getIdNewProvider())
                .orElse(null);

        if (newProvider == null) {
            newProvider = providerSubcontractorRepository.findById(itemManagementProviderRequestDto.getIdNewProvider())
                    .orElseThrow(() -> new NotFoundException("Provider not found"));
        }


        ItemManagement solicitation = itemManagementRepository.save(ItemManagement.builder()
                .solicitationType(itemManagementProviderRequestDto.getSolicitationType())
                .requester(requester)
                .newProvider(newProvider)
                .build());

        crudNotification.saveProviderNotificationForRealizaUsers(solicitation);

        return toItemManagementProviderResponseDto(solicitation, requester, newProvider);
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
                            contractProviderSupplierRepository.findTopByProviderSupplier_IdProviderAndStatusOrderByCreationDateDesc(provider.getIdProvider(), ContractStatusEnum.PENDING).getIdContract())
                    .orElseThrow(() -> new NotFoundException("Contract not found"));

            contract.setIsActive(ATIVADO);
            contract.setStatus(ContractStatusEnum.ACTIVE);
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
        } else if (solicitation.getContractDocument() != null) {
            ContractDocument contractDoc = solicitation.getContractDocument();
            Document document = documentRepository.findById(contractDoc.getDocument().getIdDocumentation())
                    .orElseThrow(() -> new NotFoundException("Document not found"));

            Contract contract = contractRepository.findById(contractDoc.getContract().getIdContract())
                    .orElseThrow(() -> new NotFoundException("Contract not found"));

            if (document.getContractDocuments().stream().anyMatch(contractDocument -> contractDocument.getContract().equals(contract))) {
                document.getContractDocuments().removeIf(contractDocument -> contractDocument.getContract().equals(contract));

                if (document.getContractDocuments().isEmpty()) {
                    documentRepository.delete(document);
                } else {
                    documentRepository.save(document);
                }
                contractRepository.save(contract);
            }

            if (JwtService.getAuthenticatedUserId() != null) {
                User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                        .orElse(null);
                String owner = "";
                if (userResponsible != null) {
                    if (document instanceof DocumentEmployee documentEmployee) {
                        owner = documentEmployee.getEmployee() != null
                                ? documentEmployee.getEmployee().getFullName()
                                : "Not Identified";
                    } else if (document instanceof DocumentProviderSupplier documentProviderSupplier) {
                        owner = documentProviderSupplier.getProviderSupplier() != null
                                ? (documentProviderSupplier.getProviderSupplier().getCorporateName() != null
                                ? documentProviderSupplier.getProviderSupplier().getCorporateName()
                                : (documentProviderSupplier.getProviderSupplier().getTradeName() != null
                                ? documentProviderSupplier.getProviderSupplier().getTradeName()
                                : "Not Identified"))
                                : "Not Identified";
                    } else if (document instanceof DocumentProviderSubcontractor documentProviderSubcontractor) {
                        owner = documentProviderSubcontractor.getProviderSubcontractor() != null
                                ? (documentProviderSubcontractor.getProviderSubcontractor().getCorporateName() != null
                                ? documentProviderSubcontractor.getProviderSubcontractor().getCorporateName()
                                : (documentProviderSubcontractor.getProviderSubcontractor().getTradeName() != null
                                ? documentProviderSubcontractor.getProviderSubcontractor().getTradeName()
                                : "Not Identified"))
                                : "Not Identified";
                    }
                    auditLogService.createAuditLog(
                            document.getIdDocumentation(),
                            DOCUMENT,
                            userResponsible.getFullName() + " isentou documento "
                                    + document.getTitle() + " de " + owner,
                            null,
                            EXEMPT,
                            userResponsible.getIdUser());
                }
            }

            return "Document " + document.getTitle() + " exempted from contract " + contract.getContractReference();
        } else if (solicitation.getContract() != null) {
            String contractId = solicitation.getContract().getIdContract();
            switch (solicitation.getSolicitationType()) {
                case FINISH -> {
                    UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

                    if ((requester.getAdmin() != null ? requester.getAdmin() : false)
                            || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                            || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)
                            || requester.getManager()) {

                        Contract contract = contractRepository.findById(contractId)
                                .orElseThrow(() -> new NotFoundException("Contract not found"));
                        if (requester.getContractAccess().contains(contract.getIdContract())
                                || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                                || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)) {
                            contract.setFinished(true);
                            contract.setEndDate(Date.valueOf(LocalDate.now()));

                            contract = contractRepository.save(contract);

                            if (JwtService.getAuthenticatedUserId() != null) {
                                User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                                        .orElse(null);
                                if (userResponsible != null) {
                                    auditLogService.createAuditLog(
                                            contract.getIdContract(),
                                            CONTRACT,
                                            userResponsible.getFullName() + " finalizou contrato "
                                                    + contract.getContractReference(),
                                            null,
                                            FINISH,
                                            userResponsible.getIdUser());
                                }
                            }
                            return "Contract finished successfully";
                        }
                    }
                    throw new IllegalArgumentException("User don't have permission to finish a contract");
                }
                case SUSPEND -> {
                    UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

                    if ((requester.getAdmin() != null ? requester.getAdmin() : false)
                            || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                            || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)
                            || requester.getManager()) {
                        Contract contract = contractRepository.findById(contractId)
                                .orElseThrow(() -> new NotFoundException("Contract not found"));
                        if (requester.getContractAccess().contains(contract.getIdContract())
                                || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                                || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)) {
                            contract.setIsActive(SUSPENSO);
                            contract.setStatus(ContractStatusEnum.SUSPENDED);
                            contract.setEndDate(Date.valueOf(LocalDate.now()));

                            contract = contractRepository.save(contract);

                            if (JwtService.getAuthenticatedUserId() != null) {
                                User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                                        .orElse(null);
                                if (userResponsible != null) {
                                    auditLogService.createAuditLog(
                                            contract.getIdContract(),
                                            CONTRACT,
                                            userResponsible.getFullName() + " suspendeu contrato "
                                                    + contract.getContractReference(),
                                            null,
                                            UPDATE,
                                            userResponsible.getIdUser());
                                }
                            }

                            return "Contract suspended successfully";
                        }
                    }
                    throw new IllegalArgumentException("User don't have permission to suspend a contract");
                }
                case REACTIVATION -> {
                    UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());

                    if ((requester.getAdmin() != null ? requester.getAdmin() : false)
                            || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                            || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)
                            || requester.getManager()) {
                        Contract contract = contractRepository.findById(contractId)
                                .orElseThrow(() -> new NotFoundException("Contract not found"));
                        if (requester.getContractAccess().contains(contract.getIdContract())
                                || requester.getRole().equals(User.Role.ROLE_REALIZA_BASIC)
                                || requester.getRole().equals(User.Role.ROLE_REALIZA_PLUS)) {
                            contract.setIsActive(ATIVADO);
                            contract.setStatus(ContractStatusEnum.ACTIVE);
                            contract.setEndDate(null);

                            contract = contractRepository.save(contract);

                            if (JwtService.getAuthenticatedUserId() != null) {
                                User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                                        .orElse(null);
                                if (userResponsible != null) {
                                    auditLogService.createAuditLog(
                                            contract.getIdContract(),
                                            CONTRACT,
                                            userResponsible.getFullName() + " reativou contrato "
                                                    + contract.getContractReference(),
                                            null,
                                            UPDATE,
                                            userResponsible.getIdUser());
                                }
                            }

                            return "Contract reactivated successfully";
                        }
                    }
                    throw new IllegalArgumentException("User don't have permission to suspend a contract");
                }
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

            Contract contract = null;
            if (provider instanceof ProviderSupplier providerSupplier) {
                contract = providerSupplier.getContractsSupplier().stream()
                        .max(Comparator.comparing(Contract::getCreationDate))
                        .orElseThrow(() -> new NotFoundException("Contract not found"));
            } else if (provider instanceof ProviderSubcontractor providerSubcontractor) {
                contract = providerSubcontractor.getContractsSubcontractor().stream()
                        .max(Comparator.comparing(Contract::getCreationDate))
                        .orElseThrow(() -> new NotFoundException("Contract not found"));
            }

            assert contract != null;
            contract.setIsActive(NEGADO);
            contractRepository.save(contract);

            String token = tokenManagerService.generateToken();
            solicitation.setInvitationToken(token);

            if (provider.getEmail() != null) {
                emailSender.sendNewProviderDeniedEmail(EmailRegistrationDeniedRequestDto.builder()
                        .email(provider.getEmail())
                        .responsibleName(contract.getResponsible().getFullName())
                        .enterpriseName(provider.getCorporateName())
                        // adicionar motivos ao sistema
                        .reason(null)
                        .build());
            }
        } else if (solicitation.getContractDocument() != null) {
            ContractDocument contractDocument = solicitation.getContractDocument();
            contractDocument.setStatus(Document.Status.PENDENTE);
            contractDocumentRepository.save(contractDocument);
        } else if (solicitation.getContract() != null) {
            Contract contract = solicitation.getContract();
            switch (solicitation.getSolicitationType()) {
                case FINISH -> {
                    if(contract.getStatus() == ContractStatusEnum.SUSPENDED) {
                        contract.setStatus(ContractStatusEnum.SUSPENDED);
                    } else {
                        contract.setStatus(ContractStatusEnum.ACTIVE);
                    }
                }
                case SUSPEND -> contract.setStatus(ContractStatusEnum.ACTIVE);
                case REACTIVATION -> contract.setStatus(ContractStatusEnum.SUSPENDED);
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
    public ItemManagementDocumentResponseDto saveDocumentSolicitation(ItemManagementDocumentRequestDto requestDto) {
        User requester = userClientRepository.findById(requestDto.getIdRequester())
                .orElse(null);

        if (requester == null) {
            requester = userManagerRepository.findById(requestDto.getIdRequester())
                    .orElseThrow(() -> new NotFoundException("Requester not found"));
        }

        Document document = documentRepository.findById(requestDto.getDocumentId())
                .orElseThrow(() -> new NotFoundException("Document not found"));

        Contract contract = contractRepository.findById(requestDto.getContractId())
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        ContractDocument contractDocument = document.getContractDocuments().stream()
                .findFirst()
                .filter(contractDoc -> contractDoc.getContract().getIdContract().equals(contract.getIdContract()))
                .orElseThrow(() -> new IllegalArgumentException("Contract and Document dont have a link"));

        ItemManagement solicitation = itemManagementRepository.save(ItemManagement.builder()
                .solicitationType(requestDto.getSolicitationType())
                .requester(requester)
                .description(requestDto.getDescription())
                .contractDocument(contractDocument)
                .build());

        crudNotification.saveDocumentNotificationForRealizaUsers(solicitation);

        return toItemManagementDocumentResponseDto(solicitation);
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

    @Override
    public ItemManagementContractResponseDto saveContractSolicitation(ItemManagementContractRequestDto requestDto) {
        User requester = userClientRepository.findById(requestDto.getIdRequester())
                .orElse(null);

        if (requester == null) {
            requester = userManagerRepository.findById(requestDto.getIdRequester())
                    .orElseThrow(() -> new NotFoundException("Requester not found"));
        }

        Contract contract = contractRepository.findById(requestDto.getContractId())
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        ItemManagement solicitation = itemManagementRepository.save(ItemManagement.builder()
                .solicitationType(requestDto.getSolicitationType())
                .requester(requester)
                .contract(contract)
                .build());

        crudNotification.saveContractNotificationForRealizaUsers(solicitation);

        return toItemManagementContractResponseDto(solicitation);
    }

    @Override
    public Page<ItemManagementContractResponseDto> findAllContractSolicitation(Pageable pageable) {
        Page<ItemManagement> itemManagementPage = itemManagementRepository.findAllByContractIsNotNull(pageable);
        return itemManagementPage.map(this::toItemManagementContractResponseDto);
    }

    @Override
    public ItemManagementContractDetailsResponseDto findContractSolicitationDetails(String idSolicitation) {
        return toItemManagementContractDetailsResponseDto(itemManagementRepository.findById(idSolicitation)
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
        String enterpriseName = null;
        String clientName = null;
        String clientCnpj = null;
        String branchName = null;

        if (newProvider instanceof ProviderSupplier providerSupplier) {
            enterpriseName = providerSupplier.getCorporateName();
            clientName = !providerSupplier.getBranches().isEmpty()
                    ? providerSupplier.getBranches().get(0).getClient().getCorporateName()
                    : null;
            clientCnpj = !providerSupplier.getBranches().isEmpty()
                    ? providerSupplier.getBranches().get(0).getClient().getCnpj()
                    : null;
            branchName = !providerSupplier.getBranches().isEmpty()
                    ? providerSupplier.getBranches().get(0) != null
                    ? providerSupplier.getBranches().get(0).getName()
                    : null
                    : null;
        } else if (newProvider instanceof ProviderSubcontractor providerSubcontractor) {
            enterpriseName = providerSubcontractor.getCorporateName();
            clientName = !providerSubcontractor.getProviderSupplier().getBranches().isEmpty()
                    ? providerSubcontractor.getProviderSupplier().getBranches().get(0).getClient().getCorporateName()
                    : null;
            clientCnpj = !providerSubcontractor.getProviderSupplier().getBranches().isEmpty()
                    ? providerSubcontractor.getProviderSupplier().getBranches().get(0).getClient().getCnpj()
                    : null;
            branchName = !providerSubcontractor.getProviderSupplier().getBranches().isEmpty()
                    ? providerSubcontractor.getProviderSupplier().getBranches().get(0) != null
                    ? providerSubcontractor.getProviderSupplier().getBranches().get(0).getName()
                    : null
                    : null;
        }

        return ItemManagementProviderResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .enterpriseName(enterpriseName)
                .solicitationType(itemManagement.getSolicitationType())
                .clientName(clientName)
                .clientCnpj(clientCnpj)
                .requesterName(requester.getFullName())
                .requesterEmail(requester.getEmail())
                .status(itemManagement.getStatus())
                .branchName(branchName)
                .creationDate(itemManagement.getCreationDate())
                .build();
    }

    private ItemManagementDocumentResponseDto toItemManagementDocumentResponseDto(ItemManagement itemManagement) {
        Document document = itemManagement.getContractDocument().getDocument();
        Contract contract = itemManagement.getContractDocument().getContract();
        String description = itemManagement.getDescription();
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
                .description(itemManagement.getDescription())
                .clientName(clientName)
                .clientCnpj(clientCnpj)
                .branchName(branchName)
                .requesterName(requester.getFullName())
                .requesterEmail(requester.getEmail())
                .status(itemManagement.getStatus())
                .creationDate(itemManagement.getCreationDate())
                .build();
    }

    private ItemManagementContractResponseDto toItemManagementContractResponseDto(ItemManagement itemManagement) {
        Contract contract = itemManagement.getContract();
        User requester = itemManagement.getRequester();
        Client client = null;
        Branch branch = null;
        String enterpriseName = null;
        String contractReference = null;
        String clientName = null;
        String clientCnpj = null;
        String branchName = null;

        contractReference = contract.getContractReference();

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            enterpriseName = contractProviderSupplier.getProviderSupplier().getCorporateName();
            branch = contractProviderSupplier.getBranch();
            branchName = branch.getName();
            client = branch.getClient();
            clientName = client.getCorporateName();
            clientCnpj = client.getCnpj();
        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            enterpriseName = contractProviderSubcontractor.getProviderSubcontractor().getCorporateName();
            branch = contractProviderSubcontractor.getContractProviderSupplier().getBranch();
            branchName = branch.getName();
            client = branch.getClient();
            clientName = client.getCorporateName();
            clientCnpj = client.getCnpj();
        }

        return ItemManagementContractResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .contractReference(contractReference)
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
        String clientCnpj = null;
        String clientTradeName = null;
        String providerCnpj = null;
        String providerCorporateName = null;

        if (itemManagement.getNewProvider() instanceof ProviderSupplier providerSupplier) {
            clientCnpj = !providerSupplier.getBranches().isEmpty()
                    ? providerSupplier.getBranches().get(0).getClient().getCnpj()
                    : null;
            clientTradeName = !providerSupplier.getBranches().isEmpty()
                    ? providerSupplier.getBranches().get(0).getClient().getCorporateName()
                    : null;
            providerCnpj = providerSupplier.getCnpj();
            providerCorporateName = providerSupplier.getCorporateName();
        } else if (itemManagement.getNewProvider() instanceof ProviderSubcontractor providerSubcontractor) {
            clientCnpj = !providerSubcontractor.getProviderSupplier().getBranches().isEmpty()
                    ? providerSubcontractor.getProviderSupplier().getBranches().get(0).getClient().getCnpj()
                    : null;
            clientTradeName = !providerSubcontractor.getProviderSupplier().getBranches().isEmpty()
                    ? providerSubcontractor.getProviderSupplier().getBranches().get(0).getClient().getCorporateName()
                    : null;
            providerCnpj = providerSubcontractor.getProviderSupplier().getCnpj();
            providerCorporateName = providerSubcontractor.getProviderSupplier().getCorporateName();
        }

        return ItemManagementProviderDetailsResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .solicitationType(itemManagement.getSolicitationType())
                .creationDate(itemManagement.getCreationDate())
                .client(ItemManagementProviderDetailsResponseDto.Client.builder()
                        .cnpj(clientCnpj)
                        .tradeName(clientTradeName)
                        .build())
                .newProvider(ItemManagementProviderDetailsResponseDto.NewProvider.builder()
                        .cnpj(providerCnpj)
                        .corporateName(providerCorporateName)
                        .build())
                .requester(ItemManagementProviderDetailsResponseDto.Requester.builder()
                        .fullName(itemManagement.getRequester().getFullName())
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

    private ItemManagementContractDetailsResponseDto toItemManagementContractDetailsResponseDto(ItemManagement itemManagement) {
        Document document = itemManagement.getContractDocument().getDocument();
        Contract contract = itemManagement.getContractDocument().getContract();
        Client client = null;
        Branch branch = null;
        String clientName = null;
        String clientCnpj = null;
        String enterpriseName = null;
        String contractReference = null;

        contractReference = contract.getContractReference();
        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            enterpriseName = contractProviderSupplier.getProviderSupplier().getCorporateName();
            branch = contractProviderSupplier.getBranch();
            client = branch.getClient();
            clientName = client.getCorporateName();
            clientCnpj = client.getCnpj();
        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            enterpriseName = contractProviderSubcontractor.getProviderSubcontractor().getCorporateName();
            branch = contractProviderSubcontractor.getContractProviderSupplier().getBranch();
            client = branch.getClient();
            clientName = client.getCorporateName();
            clientCnpj = client.getCnpj();
        }

        return ItemManagementContractDetailsResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .solicitationType(itemManagement.getSolicitationType())
                .creationDate(itemManagement.getCreationDate())
                .client(ItemManagementContractDetailsResponseDto.Client.builder()
                        .corporateName(clientName)
                        .cnpj(clientCnpj)
                        .build())
                .enterprise(ItemManagementContractDetailsResponseDto.Enterprise.builder()
                        .corporateName(enterpriseName)
                        .build())
                .requester(ItemManagementContractDetailsResponseDto.Requester.builder()
                        .fullName(itemManagement.getRequester().getFullName())
                        .email(itemManagement.getRequester().getEmail())
                        .build())
                .contract(ItemManagementContractDetailsResponseDto.Contract.builder()
                        .contractReference(contractReference)
                        .build())
                .build();
    }
}
