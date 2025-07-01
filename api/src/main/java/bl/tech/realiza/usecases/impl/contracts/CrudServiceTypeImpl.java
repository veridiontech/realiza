package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeBranch;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeClient;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeRepo;
import bl.tech.realiza.domains.enums.AuditLogActions;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeBranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeRepoRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeRequestDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.*;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CrudServiceTypeImpl implements CrudServiceType {
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceTypeRepoRepository serviceTypeRepoRepository;
    private final ServiceTypeBranchRepository serviceTypeBranchRepository;
    private final BranchRepository branchRepository;
    private final ServiceTypeClientRepository serviceTypeClientRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;

    @Override
    public ServiceTypeRepoResponseDto saveServiceTypeRepo(ServiceTypeRequestDto serviceTypeRequestDto) {
        return toResponseRepo(
                serviceTypeRepoRepository.save(
                        ServiceTypeRepo.builder()
                            .title(serviceTypeRequestDto.getTitle())
                            .risk(serviceTypeRequestDto.getRisk())
                            .build()
        ));
    }

    @Override
    public ServiceTypeRepoResponseDto updateServiceTypeRepo(String idServiceType, ServiceTypeRequestDto serviceTypeRequestDto) {

        return toResponseRepo((ServiceTypeRepo) Hibernate.unproxy(updateServiceType(
                serviceTypeRepository.findById(idServiceType)
                        .orElseThrow(() -> new NotFoundException("Service type not found")),
                serviceTypeRequestDto)));
    }

    @Override
    public List<ServiceTypeFullResponseDto> getAllServiceType(String idOwner, Owner owner) {
        switch (owner) {
            case CLIENT -> {
                return serviceTypeClientRepository.findAllByClient_IdClient(idOwner).stream()
                        .sorted(Comparator.comparing(ServiceType::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                        .map(this::toResponse)
                        .toList();
            }
            case BRANCH -> {
                return serviceTypeBranchRepository.findAllByBranch_IdBranch(idOwner).stream()
                        .sorted(Comparator.comparing(ServiceType::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                        .map(this::toResponse)
                        .toList();
            }
            case REPO -> {
                return serviceTypeRepoRepository.findAll().stream()
                        .sorted(Comparator.comparing(ServiceType::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                        .map(this::toResponse)
                        .toList();
            }
            default -> {
                throw new BadRequestException("Incorrect owner");
            }
        }
    }

    @Override
    public void deleteServiceType(String idServiceType) {
        ServiceType serviceType = serviceTypeRepository.findById(idServiceType)
                .orElseThrow(() -> new NotFoundException("Service type not found"));
        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogServiceType(
                        serviceType,
                        userResponsible.getEmail() + " deletou tipo de serviço " + serviceType.getTitle(),
                        AuditLogActions.DELETE,
                        userResponsible);
            }
        }
        serviceTypeRepository.deleteById(idServiceType);
    }

    @Override
    public ServiceTypeFullResponseDto getServiceType(String idServiceType) {
        return toResponse(serviceTypeRepository.findById(idServiceType)
                .orElseThrow(() -> new NotFoundException("Service not found")));
    }

    @Override
    public List<ServiceTypeFullResponseDto> getAllServiceType() {
        return serviceTypeRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(ServiceType::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ServiceTypeBranchResponseDto saveServiceTypeBranch(String branchId, ServiceTypeRequestDto serviceTypeRequestDto) {
        ServiceTypeBranch serviceTypeBranch = serviceTypeBranchRepository.save(
                ServiceTypeBranch.builder()
                        .title(serviceTypeRequestDto.getTitle())
                        .risk(serviceTypeRequestDto.getRisk())
                        .branch(
                                branchRepository.findById(branchId)
                                        .orElseThrow(() -> new NotFoundException("Branch not found"))
                        )
                        .build()
        );

        ServiceType serviceType = (ServiceType) Hibernate.unproxy(serviceTypeBranch);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogServiceType(
                        serviceType,
                        userResponsible.getEmail() + " criou tipo de serviço " + serviceType.getTitle(),
                        AuditLogActions.CREATE,
                        userResponsible);
            }
        }

        return toResponseBranch(serviceTypeBranch);
    }

    @Override
    public ServiceTypeBranchResponseDto updateServiceTypeBranch(String idServiceType, ServiceTypeRequestDto serviceTypeRequestDto) {
        ServiceTypeBranch serviceTypeBranch = (ServiceTypeBranch) Hibernate.unproxy(updateServiceType(
                serviceTypeRepository.findById(idServiceType)
                        .orElseThrow(() -> new NotFoundException("Service type not found")),
                serviceTypeRequestDto));

        ServiceType serviceType = (ServiceType) Hibernate.unproxy(serviceTypeBranch);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogServiceType(
                        serviceType,
                        userResponsible.getEmail() + " atualizou tipo de serviço " + serviceType.getTitle(),
                        AuditLogActions.UPDATE,
                        userResponsible);
            }
        }
        return toResponseBranch(serviceTypeBranch);
    }

    @Override
    public ServiceTypeClientResponseDto saveServiceTypeClient(String clientId, ServiceTypeRequestDto serviceTypeRequestDto) {
        return toResponseClient(serviceTypeClientRepository.save(
                ServiceTypeClient.builder()
                        .title(serviceTypeRequestDto.getTitle())
                        .risk(serviceTypeRequestDto.getRisk())
                        .client(
                                clientRepository.findById(clientId)
                                        .orElseThrow(() -> new NotFoundException("Client not found"))
                        )
                        .build()
        ));
    }

    @Override
    public ServiceTypeClientResponseDto updateServiceTypeClient(String idServiceType, ServiceTypeRequestDto serviceTypeRequestDto) {
        return toResponseClient((ServiceTypeClient) Hibernate.unproxy(updateServiceType(
                serviceTypeRepository.findById(idServiceType)
                        .orElseThrow(() -> new NotFoundException("Service type not found")),
                serviceTypeRequestDto)));
    }

    @Override
    @Transactional
    public void transferFromRepoToClient(String idClient) {
        Client client = clientRepository.findById(idClient)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        List<ServiceTypeRepo> serviceTypeRepos = serviceTypeRepoRepository.findAll();

        List<ServiceTypeClient> serviceTypeClientList = serviceTypeRepos.stream().map(
                        serviceTypeRepo -> ServiceTypeClient.builder()
                                .title(serviceTypeRepo.getTitle())
                                .risk(serviceTypeRepo.getRisk())
                                .client(client)
                                .build())
                .collect(Collectors.toList());

        List<List<ServiceTypeClient>> batches = Lists.partition(serviceTypeClientList, 50);
        for (List<ServiceTypeClient> batch : batches) {
            serviceTypeClientRepository.saveAll(batch);
        }
    }

    @Override
    @Transactional
    public void transferFromClientToBranch(String idClient, String idBranch) {
        clientRepository.findById(idClient)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        Branch branch = branchRepository.findById(idBranch)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        List<ServiceTypeClient> serviceTypeClientList = serviceTypeClientRepository.findAllByClient_IdClient(idClient);

        List<ServiceTypeBranch> serviceTypeBranchList = serviceTypeClientList.stream().map(
                clientService -> ServiceTypeBranch.builder()
                        .title(clientService.getTitle())
                        .risk(clientService.getRisk())
                        .branch(branch)
                        .build()
        ).collect(Collectors.toList());

        // Salvar em lotes de 50
        List<List<ServiceTypeBranch>> batches = Lists.partition(serviceTypeBranchList, 50);
        for (List<ServiceTypeBranch> batch : batches) {
            serviceTypeBranchRepository.saveAll(batch);
        }
    }


    private ServiceTypeFullResponseDto toResponse(ServiceType serviceType) {
        return ServiceTypeFullResponseDto.builder()
                .idServiceType( serviceType.getIdServiceType())
                .title(serviceType.getTitle())
                .risk(serviceType.getRisk())
                .branchId(serviceType instanceof ServiceTypeBranch serviceTypeBranch ?
                    serviceTypeBranch.getBranch().getIdBranch()
                    : null)
                .clientId(serviceType instanceof ServiceTypeClient serviceTypeClient ?
                        serviceTypeClient.getClient().getIdClient()
                        : null)
                .build();
    }

    private ServiceTypeRepoResponseDto toResponseRepo(ServiceTypeRepo repo) {
        return ServiceTypeRepoResponseDto.builder()
                .idServiceType(repo.getIdServiceType())
                .title(repo.getTitle())
                .risk(repo.getRisk())
                .createdAt(repo.getCreationDate())
                .build();
    }

    private ServiceTypeBranchResponseDto toResponseBranch(ServiceTypeBranch branch) {
        return ServiceTypeBranchResponseDto.builder()
                .idServiceType(branch.getIdServiceType())
                .title(branch.getTitle())
                .risk(branch.getRisk())
                .branchId(branch.getBranch().getIdBranch())
                .createdAt(branch.getCreationDate())
                .build();
    }

    private ServiceTypeClientResponseDto toResponseClient(ServiceTypeClient client) {
        return ServiceTypeClientResponseDto.builder()
                .idServiceType(client.getIdServiceType())
                .title(client.getTitle())
                .risk(client.getRisk())
                .clientId(client.getClient().getIdClient())
                .createdAt(client.getCreationDate())
                .build();
    }

    private ServiceType updateServiceType(ServiceType serviceType, ServiceTypeRequestDto serviceTypeRequestDto) {
        serviceType.setTitle(serviceTypeRequestDto.getTitle() != null
                ? serviceTypeRequestDto.getTitle()
                : serviceType.getTitle());
        serviceType.setRisk(serviceTypeRequestDto.getRisk() != null
                ? serviceTypeRequestDto.getRisk()
                : serviceType.getRisk());

        return serviceTypeRepository.save(serviceType);
    }
}
