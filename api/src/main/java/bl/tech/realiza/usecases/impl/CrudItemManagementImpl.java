package bl.tech.realiza.usecases.impl;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
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
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserResponseDto;
import bl.tech.realiza.services.auth.TokenManagerService;
import bl.tech.realiza.services.email.EmailSender;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Override
    public ItemManagementUserResponseDto saveUserSolicitation(ItemManagementUserRequestDto itemManagementUserRequestDto) {

        User requester = userClientRepository.findById(itemManagementUserRequestDto.getIdRequester())
                .orElseThrow(() -> new NotFoundException("Requester not found"));

        User newUser = userClientRepository.findById(itemManagementUserRequestDto.getIdNewUser())
                .orElseThrow(() -> new NotFoundException("New user not found"));

        ItemManagement solicitation = itemManagementRepository.save(ItemManagement.builder()
                .solicitationType(itemManagementUserRequestDto.getSolicitationType())
                .requester(requester)
                .newUser(newUser)
                .build());

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
                .orElseThrow(() -> new NotFoundException("Requester not found"));

        Provider newProviderSupplier = providerSupplierRepository.findById(itemManagementProviderRequestDto.getIdNewProvider())
                .orElseThrow(() -> new NotFoundException("New Provider not found"));

        ItemManagement solicitation = itemManagementRepository.save(ItemManagement.builder()
                .solicitationType(itemManagementProviderRequestDto.getSolicitationType())
                .requester(requester)
                .newProvider(newProviderSupplier)
                .build());

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
        ItemManagement itemManagement = itemManagementRepository.findById(idSolicitation)
                .orElseThrow(() -> new NotFoundException("Solicitation not found"));

        return toItemManagementProviderDetailsResponseDto(itemManagement);
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
                            contractProviderSupplierRepository.findTopByProviderSupplier_IdProviderOrderByCreationDateDesc(provider.getIdProvider()).getIdContract())
                    .orElseThrow(() -> new NotFoundException("Contract not found"));

            String token = tokenManagerService.generateToken();
            solicitation.setInvitationToken(token);
            itemManagementRepository.save(solicitation);

            if (provider.getEmail() != null) {
                emailSender.sendNewProviderEmail(EmailEnterpriseInviteRequestDto.builder()
                        .email(provider.getEmail())
                        .companyName(provider.getCorporateName())
                        .requesterName(contract instanceof ContractProviderSupplier ? ((ContractProviderSupplier) contract).getBranch().getName() : ((ContractProviderSubcontractor) contract).getContractProviderSupplier().getProviderSupplier().getTradeName())
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
                            contractProviderSupplierRepository.findTopByProviderSupplier_IdProviderOrderByCreationDateDesc(provider.getIdProvider()).getIdContract())
                    .orElseThrow(() -> new NotFoundException("Contract not found"));

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

    private ItemManagementUserResponseDto toItemManagementUserResponseDto(ItemManagement itemManagement, User requester, User newUser) {
        if (!(newUser instanceof UserClient userClient)) {
            throw new IllegalArgumentException("Not a valid user");
        }

        return ItemManagementUserResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .userFullName(newUser.getFirstName() + " " + newUser.getSurname())
                .solicitationType(itemManagement.getSolicitationType())
                .clientTradeName(userClient.getBranch().getClient().getTradeName())
                .clientCnpj(userClient.getBranch().getClient().getCnpj())
                .requesterFullName(requester.getFirstName() + " " + requester.getSurname())
                .requesterEmail(requester.getEmail())
                .status(itemManagement.getStatus())
                .creationDate(itemManagement.getCreationDate())
                .build();
    }

    private ItemManagementProviderResponseDto toItemManagementProviderResponseDto(ItemManagement itemManagement, User requester, Provider newProvider) {
        if (!(newProvider instanceof ProviderSupplier providerSupplier)) {
            throw new IllegalArgumentException("Not a valid provider");
        }

        return ItemManagementProviderResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .enterpriseName(providerSupplier.getTradeName())
                .solicitationType(itemManagement.getSolicitationType())
                .clientName(providerSupplier.getBranches().get(0).getClient().getTradeName())
                .clientCnpj(providerSupplier.getBranches().get(0).getClient().getCnpj())
                .requesterName(requester.getFirstName() + " " + requester.getSurname())
                .requesterEmail(requester.getEmail())
                .status(itemManagement.getStatus())
                .creationDate(itemManagement.getCreationDate())
                .build();
    }

    private ItemManagementUserDetailsResponseDto toItemManagementUserDetailsResponseDto(ItemManagement itemManagement) {
        if (!(itemManagement.getNewUser() instanceof UserClient userClient)) {
            throw new IllegalArgumentException("Not a valid user");
        }

        return ItemManagementUserDetailsResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .solicitationType(itemManagement.getSolicitationType())
                .creationDate(itemManagement.getCreationDate())
                .client(ItemManagementUserDetailsResponseDto.Client.builder()
                        .cnpj(userClient.getBranch().getClient().getCnpj())
                        .tradeName(userClient.getBranch().getClient().getTradeName())
                        .build())
                .newUser(ItemManagementUserDetailsResponseDto.NewUser.builder()
                        .fullName(itemManagement.getNewUser().getFirstName() + " " + itemManagement.getNewUser().getSurname())
                        .cpf(itemManagement.getNewUser().getCpf())
                        .email(itemManagement.getNewUser().getEmail())
                        .build())
                .requester(ItemManagementUserDetailsResponseDto.Requester.builder()
                        .fullName(itemManagement.getRequester().getFirstName() + " " + itemManagement.getRequester().getSurname())
                        .email(itemManagement.getRequester().getEmail())
                        .build())
                .build();
    }

    private ItemManagementProviderDetailsResponseDto toItemManagementProviderDetailsResponseDto(ItemManagement itemManagement) {
        if (!(itemManagement.getNewProvider() instanceof ProviderSupplier providerSupplier)) {
            throw new IllegalArgumentException("Not a valid provider");
        }

        return ItemManagementProviderDetailsResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .solicitationType(itemManagement.getSolicitationType())
                .creationDate(itemManagement.getCreationDate())
                .client(ItemManagementProviderDetailsResponseDto.Client.builder()
                        .cnpj(providerSupplier.getBranches().get(0).getClient().getCnpj())
                        .tradeName(providerSupplier.getBranches().get(0).getClient().getTradeName())
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

}
