package bl.tech.realiza.usecases.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeRequestDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeBranchResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeClientResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeFullResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeRepoResponseDto;

import java.util.List;

public interface CrudServiceType {
    // any
    void deleteServiceType(String idServiceType, Boolean replicate);
    ServiceTypeFullResponseDto getServiceType(String idServiceType);
    List<ServiceTypeFullResponseDto> getAllServiceType();
    List<ServiceTypeFullResponseDto> getAllServiceType(String idOwner, Owner owner);

    // Repositório
    ServiceTypeRepoResponseDto saveServiceTypeRepo(ServiceTypeRequestDto serviceTypeRequestDto);
    ServiceTypeRepoResponseDto updateServiceTypeRepo(String idServiceType, ServiceTypeRequestDto serviceTypeRequestDto);

    // Branch
    ServiceTypeBranchResponseDto saveServiceTypeBranch(String branchId, ServiceTypeRequestDto serviceTypeRequestDto, Boolean replicate);
    ServiceTypeBranchResponseDto updateServiceTypeBranch(String idServiceType, ServiceTypeRequestDto serviceTypeRequestDto, Boolean replicate);

    // Client
    ServiceTypeClientResponseDto saveServiceTypeClient(String clientId, ServiceTypeRequestDto serviceTypeRequestDto);
    ServiceTypeClientResponseDto updateServiceTypeClient(String idServiceType, ServiceTypeRequestDto serviceTypeRequestDto);

    void transferFromRepoToClient(String idClient);

    void transferFromClientToBranch(String idClient, String idBranch);

    public enum Owner {
        REPO,
        BRANCH,
        CLIENT
    }
}
