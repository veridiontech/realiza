package bl.tech.realiza.usecases.interfaces.contracts;

import bl.tech.realiza.domains.contract.serviceType.ServiceTypeRepo;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeBranchRequestDto;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeClientRequestDto;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeRepoDtoRequestDto;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeBaseRequestDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeBranchResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeClientResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeRepoResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeResponseDto;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface CrudServiceType {
    // any
    void deleteServiceType(String idServiceType);
    ServiceTypeResponseDto updateServiceType(String idServiceType, ServiceTypeBaseRequestDto serviceTypeBaseRequestDto);
    ServiceTypeResponseDto getServiceType(String idServiceType);
    List<ServiceTypeResponseDto> getAllServiceType();

    // Repositório
    ServiceTypeRepoResponseDto saveServiceTypeRepo(ServiceTypeRepoDtoRequestDto serviceTypeRepoDtoRequestDto);
    List<ServiceTypeResponseDto> getAllServiceType(String idOwner, Owner owner);

    // Branch
    ServiceTypeBranchResponseDto saveServiceTypeBranch(ServiceTypeBranchRequestDto serviceTypeBranchRequestDto);

    // Client
    ServiceTypeClientResponseDto saveServiceTypeClient(ServiceTypeClientRequestDto serviceTypeClientRequestDto);

    @Async
    void transferFromRepoToClient(String idClient);

    @Async
    void transferFromClientToBranch(String idClient, String idBranch);

    public enum Owner {
        REPO,
        BRANCH,
        CLIENT
    }
}
