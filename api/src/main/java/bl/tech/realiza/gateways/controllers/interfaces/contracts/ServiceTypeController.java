package bl.tech.realiza.gateways.controllers.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeRequestDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeBranchResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeClientResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeFullResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeRepoResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ServiceTypeController {
    ResponseEntity<ServiceTypeRepoResponseDto> createServiceTypeRepo(ServiceTypeRequestDto serviceTypeRequestDto);
    ResponseEntity<ServiceTypeBranchResponseDto> createServiceTypeBranch(String branchId, ServiceTypeRequestDto serviceTypeRequestDto);
    ResponseEntity<ServiceTypeClientResponseDto> createServiceTypeClient(String clientId, ServiceTypeRequestDto serviceTypeRequestDto);
    ResponseEntity<ServiceTypeFullResponseDto> getOneServiceType(String idServiceType);
    ResponseEntity<List<ServiceTypeFullResponseDto>> getAllServiceTypes();
    ResponseEntity<List<ServiceTypeFullResponseDto>> getAllServiceTypesByOwner(String idOwner, CrudServiceType.Owner owner);

    ResponseEntity<ServiceTypeRepoResponseDto> updateServiceTypeRepo(String idServiceType, ServiceTypeRequestDto serviceTypeRequestDto);
    ResponseEntity<ServiceTypeBranchResponseDto> updateServiceTypeBranch(String idServiceType, ServiceTypeRequestDto serviceTypeRequestDto);
    ResponseEntity<ServiceTypeClientResponseDto> updateServiceTypeClient(String idServiceType, ServiceTypeRequestDto serviceTypeRequestDto);

    ResponseEntity<Void> deleteServiceType(String idServiceType);
}
