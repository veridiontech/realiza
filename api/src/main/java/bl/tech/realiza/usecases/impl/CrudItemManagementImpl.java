package bl.tech.realiza.usecases.impl;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.domains.user.UserManager;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.providers.ProviderRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.services.ItemManagementRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserManagerRepository;
import bl.tech.realiza.gateways.requests.services.email.EmailInviteRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.ItemManagementProviderResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.ItemManagementUserResponseDto;
import bl.tech.realiza.services.email.EmailSender;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import lombok.Builder;
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

    @Override
    public ItemManagementUserResponseDto saveUserSolicitation(ItemManagementUserRequestDto itemManagementUserRequestDto) {

        UserClient requester = userClientRepository.findById(itemManagementUserRequestDto.getIdRequester())
                .orElseThrow(() -> new NotFoundException("Requester not found"));

        UserClient newUser = userClientRepository.findById(itemManagementUserRequestDto.getIdNewUser())
                .orElseThrow(() -> new NotFoundException("New user not found"));

        ItemManagement solicitation = itemManagementRepository.save(ItemManagement.builder()
                .title(itemManagementUserRequestDto.getTitle())
                .details(itemManagementUserRequestDto.getDetails())
                .requester(requester)
                .newUser(newUser)
                .build());

        return getItemManagementUserResponseDto(solicitation, requester, newUser);
    }

    @Override
    public ItemManagementProviderResponseDto saveProviderSolicitation(ItemManagementProviderRequestDto itemManagementProviderRequestDto) {

        UserManager requesterManager = userManagerRepository.findById(itemManagementProviderRequestDto.getIdRequester())
                .orElse(null);

        UserClient requesterClient = userClientRepository.findById(itemManagementProviderRequestDto.getIdRequester())
                .orElse(null);

        ProviderSupplier newProviderSupplier = providerSupplierRepository.findById(itemManagementProviderRequestDto.getIdNewProvider())
                .orElseThrow(() -> new NotFoundException("New Provider not found"));

        ItemManagement solicitation = itemManagementRepository.save(ItemManagement.builder()
                .title(itemManagementProviderRequestDto.getTitle())
                .details(itemManagementProviderRequestDto.getDetails())
                .requester(requesterManager != null ? requesterManager : requesterClient)
                .newProvider(newProviderSupplier)
                .build());

        return getItemManagementProviderResponseDto(solicitation, requesterClient, requesterManager);
    }

    @Override
    public Page<ItemManagementUserResponseDto> findAllUserSolicitation(Pageable pageable) {

        Page<ItemManagement> itemManagementPage = itemManagementRepository.findAllByNewUserIsNotNull(pageable);

        return itemManagementPage.map(
                itemManagement -> {
                    UserClient requester = userClientRepository.findById(itemManagement.getRequester().getIdUser())
                            .orElseThrow(() -> new NotFoundException("Requester not found"));

                    UserClient newUser = userClientRepository.findById(itemManagement.getNewUser().getIdUser())
                            .orElseThrow(() -> new NotFoundException("New user not found"));
                    return getItemManagementUserResponseDto(itemManagement, requester, newUser);
                }
        );
    }

    @Override
    public Page<ItemManagementProviderResponseDto> findAllProviderSolicitation(Pageable pageable) {

        Page<ItemManagement> itemManagementPage = itemManagementRepository.findAllByNewProviderIsNotNull(pageable);

        return itemManagementPage.map(
                itemManagement -> {
                    UserClient requesterClient = userClientRepository.findById(itemManagement.getRequester().getIdUser())
                            .orElse(null);

                    UserManager requesterManager = userManagerRepository.findById(itemManagement.getRequester().getIdUser())
                            .orElse(null);

                    return getItemManagementProviderResponseDto(itemManagement, requesterClient, requesterManager);
                }
        );
    }

    @Override
    public void deleteSolicitation(String id) {
        itemManagementRepository.deleteById(id);
    }

    @Override
    public String approveSolicitation(String id) {


        ItemManagement solicitation = itemManagementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Solicitation not found"));

        if (solicitation.getNewUser() != null) {
            UserClient userClient = userClientRepository.findById(solicitation.getNewUser().getIdUser())
                    .orElseThrow(() -> new NotFoundException("User not found"));

            userClient.setIsActive(true);

            userClientRepository.save(userClient);
        } else if (solicitation.getNewProvider() != null) {

            Provider provider = providerRepository.findById(solicitation.getNewProvider().getIdProvider())
                    .orElseThrow(() -> new NotFoundException("Provider not found"));

            provider.setIsActive(true);

            providerRepository.save(provider);

            if (provider.getEmail() != null) {
                emailSender.sendInviteEmail(EmailInviteRequestDto.builder()
                            .email(provider.getEmail())
                            .company(Provider.Company.SUPPLIER)
                            .idCompany(provider.getIdProvider())
                        .build());
            }
        }

        solicitation.setStatus(ItemManagement.Status.APPROVED);

        itemManagementRepository.save(solicitation);

        return "Solicitation approved";
    }

    @Override
    public String denySolicitation(String id) {

        ItemManagement solicitation = itemManagementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Solicitation not found"));

        solicitation.setStatus(ItemManagement.Status.DENIED);

        itemManagementRepository.save(solicitation);

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
        }

        return "Solicitation denied";
    }

    private ItemManagementUserResponseDto getItemManagementUserResponseDto(ItemManagement itemManagement, UserClient requester, UserClient newUser) {
        return ItemManagementUserResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .title(itemManagement.getTitle())
                .details(itemManagement.getDetails())
                .status(itemManagement.getStatus())
                .creationDate(itemManagement.getCreationDate())
                .requester(ItemManagementUserResponseDto.Requester.builder()
                        .idUser(itemManagement.getRequester().getIdUser())
                        .cpf(itemManagement.getRequester().getCpf())
                        .email(itemManagement.getRequester().getEmail())
                        .firstName(itemManagement.getRequester().getFirstName())
                        .surname(itemManagement.getRequester().getSurname())
                        .nameEnterprise(requester.getBranch().getName())
                        .build())
                .newUser(ItemManagementUserResponseDto.NewUser.builder()
                        .idUser(itemManagement.getNewUser().getIdUser())
                        .cpf(itemManagement.getNewUser().getCpf())
                        .email(itemManagement.getNewUser().getEmail())
                        .firstName(itemManagement.getNewUser().getFirstName())
                        .surname(itemManagement.getNewUser().getSurname())
                        .enterprise(newUser.getBranch().getName())
                        .build())
                .build();
    }

    private ItemManagementProviderResponseDto getItemManagementProviderResponseDto(ItemManagement itemManagement, UserClient requesterClient, UserManager requesterManager) {
        return ItemManagementProviderResponseDto.builder()
                .idSolicitation(itemManagement.getIdSolicitation())
                .title(itemManagement.getTitle())
                .details(itemManagement.getDetails())
                .status(itemManagement.getStatus())
                .creationDate(itemManagement.getCreationDate())
                .requester(ItemManagementProviderResponseDto.Requester.builder()
                        .idUser(itemManagement.getRequester().getIdUser())
                        .cpf(itemManagement.getRequester().getCpf())
                        .email(itemManagement.getRequester().getEmail())
                        .firstName(itemManagement.getRequester().getFirstName())
                        .surname(itemManagement.getRequester().getSurname())
                        .nameEnterprise(requesterManager != null ? "Realiza Assessoria" : requesterClient.getBranch().getName())
                        .build())
                .newProvider(ItemManagementProviderResponseDto.NewProvider.builder()
                        .cnpj(itemManagement.getNewProvider().getCnpj())
                        .telephone(itemManagement.getNewProvider().getTelephone())
                        .corporateName(itemManagement.getNewProvider().getCorporateName())
                        .build())
                .build();
    }
}
