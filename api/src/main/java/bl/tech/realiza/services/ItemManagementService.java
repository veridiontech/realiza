package bl.tech.realiza.services;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.controllers.interfaces.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemManagementService {

    private final BranchRepository branchRepository;
    private final ClientRepository clientRepository;
    private final ContractRepository contractRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final RequirementRepository requirementRepository;
    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;


    public String activateItem(String id, ActivationItemType item) {
        switch (item) {
            case BRANCH ->  {
                Branch branch = branchRepository.findById(id).orElseThrow(() -> new NotFoundException("Branch not found"));
                branch.setIsActive(true);
                branchRepository.save(branch);
                return "Branch activated successfully";
            }
            case CLIENT -> {
                Client client = clientRepository.findById(id).orElseThrow(() -> new NotFoundException("Client not found"));
                client.setIsActive(true);
                clientRepository.save(client);
                return "Client activated successfully";
            }
            case CONTRACT -> {
                Contract contract = contractRepository.findById(id).orElseThrow(() -> new NotFoundException("Contract not found"));
                contract.setIsActive(true);
                contractRepository.save(contract);
                return "Contract activated successfully";
            }
            case PROVIDER -> {
                Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotFoundException("Provider not found"));
                provider.setIsActive(true);
                providerRepository.save(provider);
                return "Provider activated successfully";
            }
            case USER -> {
                User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
                user.setIsActive(true);
                userRepository.save(user);
                return "User activated successfully";
            }
            default -> {
                throw new BadRequestException("Invalid entity");
            }
        }
    }

    public List<Object> getInactiveItems() {
        List<Object> inactiveItems = new ArrayList<>();

        inactiveItems.addAll(branchRepository.findAllByIsActive(false));
        inactiveItems.addAll(clientRepository.findAllByIsActive(false));
        inactiveItems.addAll(contractRepository.findAllByIsActive(false));
        inactiveItems.addAll(providerRepository.findAllByIsActive(false));
        inactiveItems.addAll(userRepository.findAllByIsActive(false));

        return inactiveItems;
    }

    public List<Object> getDeleteItemRequest() {
        List<Object> deleteItemSolicitations = new ArrayList<>();

        deleteItemSolicitations.addAll(contractRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(clientRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(providerRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(userRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(activityRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(requirementRepository.findAllByDeleteRequest(true));
        deleteItemSolicitations.addAll(documentRepository.findAllByDeleteRequest(true));
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
