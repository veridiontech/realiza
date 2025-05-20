package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeBranch;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeClient;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeRepo;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeBranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeRepoRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeRepository;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeBranchRequestDto;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeClientRequestDto;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeRepoDtoRequestDto;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeBaseRequestDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.*;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    @Override
    public ServiceTypeRepoResponseDto saveServiceTypeRepo(ServiceTypeRepoDtoRequestDto serviceTypeRepoDtoRequestDto) {
        return toResponse(
                serviceTypeRepoRepository.save(
                        ServiceTypeRepo.builder()
                            .title(serviceTypeRepoDtoRequestDto.getTitle())
                            .risk(serviceTypeRepoDtoRequestDto.getRisk())
                            .build()
        ));
    }

    @Override
    public List<ServiceTypeResponseDto> getAllServiceType(String idOwner, Owner owner) {
        switch (owner) {
            case CLIENT -> {
                return serviceTypeClientRepository.findAllByClient_IdClient(idOwner).stream()
                        .map(client -> (ServiceTypeResponseDto) toResponse(client))
                        .toList();
            }
            case BRANCH -> {
                return serviceTypeBranchRepository.findAllByBranch_IdBranch(idOwner).stream()
                        .map(branch -> (ServiceTypeResponseDto) toResponse(branch))
                        .toList();
            }
            case REPO -> {
                return serviceTypeRepoRepository.findAll().stream()
                        .map(repo -> (ServiceTypeResponseDto) toResponse(repo))
                        .toList();
            }
            default -> {
                throw new BadRequestException("Incorrect owner");
            }
        }
    }

    @Override
    public ServiceTypeResponseDto updateServiceType(String idServiceType, ServiceTypeBaseRequestDto serviceTypeBaseRequestDto) {
        ServiceType serviceType = serviceTypeRepository.findById(idServiceType)
                .orElseThrow(() -> new NotFoundException("Service type not found"));

        serviceType.setTitle(serviceTypeBaseRequestDto.getTitle());
        serviceType.setRisk(serviceTypeBaseRequestDto.getRisk());
        return toResponse(serviceTypeRepository.save(serviceType));
    }

    @Override
    public void deleteServiceType(String idServiceType) {
        serviceTypeRepository.deleteById(idServiceType);
    }

    @Override
    public ServiceTypeResponseDto getServiceType(String idServiceType) {
        return toResponse(serviceTypeRepository.findById(idServiceType)
                .orElseThrow(() -> new NotFoundException("Service not found")));
    }

    @Override
    public List<ServiceTypeResponseDto> getAllServiceType() {
        return serviceTypeRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    @Override
    public ServiceTypeBranchResponseDto saveServiceTypeBranch(ServiceTypeBranchRequestDto serviceTypeBranchRequestDto) {
        return toResponse(serviceTypeBranchRepository.save(
                ServiceTypeBranch.builder()
                        .title(serviceTypeBranchRequestDto.getTitle())
                        .risk(serviceTypeBranchRequestDto.getRisk())
                        .branch(branchRepository.findById(serviceTypeBranchRequestDto.getIdBranch())
                                .orElseThrow(() -> new NotFoundException("Branch not found")))
                        .build()
        ));
    }

    @Override
    public ServiceTypeClientResponseDto saveServiceTypeClient(ServiceTypeClientRequestDto serviceTypeClientRequestDto) {
        return toResponse(serviceTypeClientRepository.save(
                ServiceTypeClient.builder()
                        .title(serviceTypeClientRequestDto.getTitle())
                        .risk(serviceTypeClientRequestDto.getRisk())
                        .client(clientRepository.findById(serviceTypeClientRequestDto.getIdClient())
                                .orElseThrow(() -> new NotFoundException("Client not found")))
                        .build()
        ));
    }

    @Override
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

        List<List<ServiceTypeClient>> partitioned = Lists.partition(serviceTypeClientList, 50);
        partitioned.forEach(serviceTypeClientRepository::saveAll);

    }

    @Override
    public void transferFromClientToBranch(String idClient, String idBranch) {
        clientRepository.findById(idClient)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        Branch branch = branchRepository.findById(idBranch)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        List<ServiceTypeClient> serviceTypeClientList = serviceTypeClientRepository.findAllByClient_IdClient(idClient);

        List<ServiceTypeBranch> serviceTypeBranchList = serviceTypeClientList.stream().map(
                        serviceTypeClient -> ServiceTypeBranch.builder()
                                .title(serviceTypeClient.getTitle())
                                .risk(serviceTypeClient.getRisk())
                                .branch(branch)
                                .build())
                .collect(Collectors.toList());

        serviceTypeBranchRepository.saveAll(serviceTypeBranchList);
    }

    private ServiceTypeResponseDto toResponse(ServiceType serviceType) {
        if (serviceType instanceof ServiceTypeRepo repo) {
            return toResponse(repo);
        } else if (serviceType instanceof ServiceTypeBranch branch) {
            return toResponse(branch);
        } else if (serviceType instanceof ServiceTypeClient client) {
            return toResponse(client);
        } else {
            throw new IllegalArgumentException("Unknown ServiceType subclass: " + serviceType.getClass().getSimpleName());
        }
    }

    private ServiceTypeRepoResponseDto toResponse(ServiceTypeRepo repo) {
        return ServiceTypeRepoResponseDto.builder()
                .idServiceType(repo.getIdServiceType())
                .title(repo.getTitle())
                .risk(repo.getRisk())
                .creationDate(repo.getCreationDate())
                .build();
    }

    private ServiceTypeBranchResponseDto toResponse(ServiceTypeBranch branch) {
        return ServiceTypeBranchResponseDto.builder()
                .idServiceType(branch.getIdServiceType())
                .title(branch.getTitle())
                .risk(branch.getRisk())
                .idBranch(branch.getBranch().getIdBranch())
                .creationDate(branch.getCreationDate())
                .build();
    }

    private ServiceTypeClientResponseDto toResponse(ServiceTypeClient client) {
        return ServiceTypeClientResponseDto.builder()
                .idServiceType(client.getIdServiceType())
                .title(client.getTitle())
                .risk(client.getRisk())
                .idClient(client.getClient().getIdClient())
                .creationDate(client.getCreationDate())
                .build();
    }
}
