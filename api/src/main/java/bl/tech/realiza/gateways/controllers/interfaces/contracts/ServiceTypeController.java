package bl.tech.realiza.gateways.controllers.interfaces.contracts;

import bl.tech.realiza.gateways.requests.contracts.serviceType.*;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeBranchResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeClientResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeRepoResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ServiceTypeController {
    ResponseEntity<ServiceTypeRepoResponseDto> createServiceTypeRepo(ServiceTypeRepoDtoRequestDto serviceTypeRepoDtoRequestDto);
    ResponseEntity<ServiceTypeBranchResponseDto> createServiceTypeBranch(ServiceTypeBranchRequestDto serviceTypeBranchRequestDto);
    ResponseEntity<ServiceTypeClientResponseDto> createServiceTypeClient(ServiceTypeClientRequestDto serviceTypeClientRequestDto);
    ResponseEntity<ServiceTypeResponseDto> getOneServiceType(String idServiceType);
    ResponseEntity<List<ServiceTypeResponseDto>> getAllServiceTypes();
    ResponseEntity<List<ServiceTypeResponseDto>> getAllServiceTypesByOwner(String idOwner, CrudServiceType.Owner owner);
    ResponseEntity<ServiceTypeResponseDto> updateServiceType(String idServiceType, ServiceTypeBaseRequestDto serviceTypeBaseRequestDto);
    ResponseEntity<Void> deleteServiceType(String idServiceType);
}
