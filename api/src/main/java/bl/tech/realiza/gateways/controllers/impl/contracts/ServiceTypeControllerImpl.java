package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.ServiceTypeController;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeBaseRequestDto;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeBranchRequestDto;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeClientRequestDto;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeRepoDtoRequestDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeBranchResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeClientResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeRepoResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeResponseDto;
import bl.tech.realiza.usecases.impl.contracts.CrudServiceTypeImpl;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract/service-type")
@Tag(name = "Service Type")
public class ServiceTypeControllerImpl implements ServiceTypeController {
    private final CrudServiceTypeImpl crudServiceTypeImpl;

    @PostMapping("/repository")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ServiceTypeRepoResponseDto> createServiceTypeRepo(@Valid @RequestBody ServiceTypeRepoDtoRequestDto serviceTypeRepoDtoRequestDto) {
        return ResponseEntity.ok(crudServiceTypeImpl.saveServiceTypeRepo(serviceTypeRepoDtoRequestDto));
    }

    @PostMapping("/branch")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ServiceTypeBranchResponseDto> createServiceTypeBranch(@Valid @RequestBody ServiceTypeBranchRequestDto serviceTypeBranchRequestDto) {
        return ResponseEntity.ok(crudServiceTypeImpl.saveServiceTypeBranch(serviceTypeBranchRequestDto));
    }

    @PostMapping("/client")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ServiceTypeClientResponseDto> createServiceTypeClient(@Valid @RequestBody ServiceTypeClientRequestDto serviceTypeClientRequestDto) {
        return ResponseEntity.ok(crudServiceTypeImpl.saveServiceTypeClient(serviceTypeClientRequestDto));
    }

    @GetMapping("/{idServiceType}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<ServiceTypeResponseDto> getOneServiceType(@PathVariable String idServiceType) {
        return ResponseEntity.ok(crudServiceTypeImpl.getServiceType(idServiceType));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ServiceTypeResponseDto>> getAllServiceTypes() {
        return ResponseEntity.ok(crudServiceTypeImpl.getAllServiceType());
    }

    @GetMapping(params = {"idOwner", "owner"})
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ServiceTypeResponseDto>> getAllServiceTypesByOwner(@RequestParam String idOwner, @RequestParam CrudServiceType.Owner owner) {
        List<ServiceTypeResponseDto> serviceTypeResponseDtos = List.of();
        switch (owner) {
            case CLIENT -> {
                serviceTypeResponseDtos = crudServiceTypeImpl.getAllServiceType(idOwner, CrudServiceType.Owner.CLIENT);
            }
            case BRANCH -> {
                serviceTypeResponseDtos = crudServiceTypeImpl.getAllServiceType(idOwner, CrudServiceType.Owner.BRANCH);
            }
            case REPO -> {
                serviceTypeResponseDtos = crudServiceTypeImpl.getAllServiceType(idOwner, CrudServiceType.Owner.REPO);
            }
        }
        return ResponseEntity.ok(serviceTypeResponseDtos);
    }

    @PutMapping("/{idServiceType}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<ServiceTypeResponseDto> updateServiceType(@PathVariable String idServiceType, @Valid @RequestBody ServiceTypeBaseRequestDto serviceTypeBaseRequestDto) {
        return ResponseEntity.ok(crudServiceTypeImpl.updateServiceType(idServiceType, serviceTypeBaseRequestDto));
    }

    @DeleteMapping("/{idServiceType}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteServiceType(@PathVariable String idServiceType) {
        return ResponseEntity.noContent().build();
    }
}
