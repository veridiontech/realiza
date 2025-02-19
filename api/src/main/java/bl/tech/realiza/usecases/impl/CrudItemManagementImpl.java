package bl.tech.realiza.usecases.impl;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.documents.employee.DocumentEmployeeRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderRepository;
import bl.tech.realiza.gateways.repositories.services.ItemManagementRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.services.ItemManagementRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.gateways.responses.services.ItemManagementResponseDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrudItemManagementImpl implements CrudItemManagement {

    private final BranchRepository branchRepository;
    private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final RequirementRepository requirementRepository;
    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;
    private final DocumentEmployeeRepository documentEmployeeRepository;
    private final ItemManagementRepository itemManagementRepository;
    private final UserClientRepository userClientRepository;
    private final UserProviderSupplierRepository userProviderSupplierRepository;
    private final UserProviderSubcontractorRepository userProviderSubcontractorRepository;


    public String approveUserSolicitation(String id) {
        ItemManagement itemManagement = itemManagementRepository.findById(id).orElseThrow(() -> new NotFoundException("Solicitation not found"));
        String userId = itemManagement.getNewUser().getIdUser();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
        deleteUserSolicitation(id); // delete the solicitation
        return "User activated successfully";
    }

    public String denyUserSolicitation(String id) {
        ItemManagement itemManagement = itemManagementRepository.findById(id).orElseThrow(() -> new NotFoundException("Solicitation not found"));
        String userId = itemManagement.getNewUser().getIdUser();
        deleteUserSolicitation(id); // delete the solicitation
        userRepository.deleteById(userId);
        return "Solicitation denied successfully";
    }

    @Override
    public ItemManagementResponseDto saveUserSolicitation(ItemManagementRequestDto itemManagementRequestDto) {
        if (itemManagementRequestDto.getIdRequester() == null || itemManagementRequestDto.getIdRequester().isEmpty()) {
            throw new BadRequestException("Invalid requester");
        }
        if (itemManagementRequestDto.getIdNewUser() == null || itemManagementRequestDto.getIdNewUser().isEmpty()) {
            throw new BadRequestException("Invalid user");
        }
        User userRequester = userRepository.findById(itemManagementRequestDto.getIdRequester()).orElseThrow(() -> new NotFoundException("User not found"));
        String company = userRequester.getClass().getSimpleName();
        User newUser = userRepository.findById(itemManagementRequestDto.getIdNewUser()).orElseThrow(() -> new NotFoundException("New user not found"));

        ItemManagement itemManagement = ItemManagement.builder()
                .title(itemManagementRequestDto.getTitle())
                .requester(userRequester)
                .details(itemManagementRequestDto.getDetails())
                .newUser(newUser)
                .build();

        ItemManagement savedItemManagement = itemManagementRepository.save(itemManagement);

        UserResponseDto newUserResponse = UserResponseDto.builder()
                .idUser(savedItemManagement.getNewUser().getIdUser())
                .cpf(savedItemManagement.getNewUser().getCpf())
                .description(savedItemManagement.getNewUser().getDescription())
                .position(savedItemManagement.getNewUser().getPosition())
                .role(savedItemManagement.getNewUser().getRole())
                .firstName(savedItemManagement.getNewUser().getFirstName())
                .surname(savedItemManagement.getNewUser().getSurname())
                .email(savedItemManagement.getNewUser().getEmail())
                .telephone(savedItemManagement.getNewUser().getTelephone())
                .cellphone(savedItemManagement.getNewUser().getCellphone())
                .build();

        switch (company) {
            case "CLIENT" -> {
                UserClient userClient = userClientRepository.findById(savedItemManagement.getNewUser().getIdUser()).orElseThrow(() -> new NotFoundException("User not found"));
                newUserResponse.setBranchResponse(BranchResponseDto.builder()
                        .idBranch(userClient.getBranch().getIdBranch())
                        .name(userClient.getBranch().getName())
                        .build());
            }
            case "SUPPLIER" -> {
                UserProviderSupplier userProviderSupplier = userProviderSupplierRepository.findById(savedItemManagement.getNewUser().getIdUser()).orElseThrow(() -> new NotFoundException("User not found"));
                newUserResponse.setProviderResponseDto(ProviderResponseDto.builder()
                                .idProvider(userProviderSupplier.getProviderSupplier().getIdProvider())
                                .corporateName(userProviderSupplier.getProviderSupplier().getCorporateName())
                        .build());
            }
            case "SUBCONTRACTOR" -> {
                UserProviderSubcontractor userProviderSubcontractor = userProviderSubcontractorRepository.findById(savedItemManagement.getNewUser().getIdUser()).orElseThrow(() -> new NotFoundException("User not found"));
                newUserResponse.setProviderResponseDto(ProviderResponseDto.builder()
                                .idProvider(userProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                                .corporateName(userProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                        .build());
            }
        }

        ItemManagementResponseDto itemManagementResponseDto = ItemManagementResponseDto.builder()
                .title(savedItemManagement.getTitle())
                .requester(UserResponseDto.builder()
                        .idUser(savedItemManagement.getRequester().getIdUser())
                        .firstName(savedItemManagement.getRequester().getFirstName())
                        .surname(savedItemManagement.getRequester().getSurname())
                        .build())
                .details(savedItemManagement.getDetails())
                .newUser(newUserResponse)
                .build();

        return itemManagementResponseDto;
    }

    @Override
    public Page<ItemManagementResponseDto> findAllUserSolicitation(Pageable pageable) {
        Page<ItemManagement> inactiveItemsPage = itemManagementRepository.findAll(pageable);

        Page<ItemManagementResponseDto> itemManagementResponsePage = inactiveItemsPage.map(
                itemManagement -> ItemManagementResponseDto.builder()
                        .idSolicitation(itemManagement.getIdSolicitation())
                        .title(itemManagement.getTitle())
                        .creationDate(itemManagement.getCreationDate())
                        .requester(UserResponseDto.builder()
                                .idUser(itemManagement.getRequester().getIdUser())
                                .firstName(itemManagement.getRequester().getFirstName())
                                .surname(itemManagement.getRequester().getSurname())
                                .build())
                        .newUser(UserResponseDto.builder()
                                        .idUser(itemManagement.getNewUser().getIdUser())
                                        .build())
                        .build()
        );
        return itemManagementResponsePage;
    }

    @Override
    public void deleteUserSolicitation(String id) {
        itemManagementRepository.deleteById(id);
    }

    public List<Object> getDeleteItemRequest() {
        List<Object> deleteItemSolicitations = new ArrayList<>();

        deleteItemSolicitations.addAll(contractRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(clientRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(providerRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(userRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(activityRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(requirementRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(documentRepository.findAllByRequestIs(Document.Request.DELETE));
        deleteItemSolicitations.addAll(employeeRepository.findAllByDeleteRequest(true));

        return deleteItemSolicitations;
    }

    public void deleteItem(String id, DeleteItemType item) {
        switch (item) {
            case BRANCH ->  {
                branchRepository.deleteById(id);
            }
            case CLIENT -> {
                clientRepository.deleteById(id);
            }
            case CONTRACT -> {
                contractRepository.deleteById(id);
            }
            case PROVIDER -> {
                providerRepository.deleteById(id);
            }
            case USER -> {
                userRepository.deleteById(id);
            }
            case ACTIVITY -> {
                activityRepository.deleteById(id);
            }
            case REQUIREMENT -> {
                requirementRepository.deleteById(id);
            }
            case DOCUMENT -> {
                documentRepository.deleteById(id);
            }
            case EMPLOYEE -> {
                employeeRepository.deleteById(id);
            }
            default -> {
                throw new BadRequestException("Invalid entity");
            }
        }
    }

    public String approveNewDocumentEmployee(String idDocument) {
        DocumentEmployee documentEmployee = documentEmployeeRepository.findById(idDocument).orElseThrow(() -> new NotFoundException("Document not found"));

        documentEmployee.setRequest(Document.Request.NONE);

        return documentEmployee.getTitle() + " successfully approved";
    }

    public List<DocumentEmployee> getAddRequestDocumentEmployees() {
        List<DocumentEmployee> addRequestDocumentEmployees = documentEmployeeRepository.findAllByRequest(Document.Request.ADD);
        return addRequestDocumentEmployees;
    }

    public enum ActivationItemType {
        BRANCH,
        CLIENT,
        CONTRACT,
        PROVIDER,
        USER
    }

    public enum DeleteItemType {
        BRANCH,
        CLIENT,
        CONTRACT,
        PROVIDER,
        USER,
        ACTIVITY,
        REQUIREMENT,
        DOCUMENT,
        EMPLOYEE
    }
}
