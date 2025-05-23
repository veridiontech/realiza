package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.ServiceTypeController;
import bl.tech.realiza.gateways.requests.contracts.serviceType.ServiceTypeRequestDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeBranchResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeClientResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeFullResponseDto;
import bl.tech.realiza.gateways.responses.contracts.serviceType.ServiceTypeRepoResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudServiceType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract/service-type")
@Tag(name = "Service Type")
public class ServiceTypeControllerImpl implements ServiceTypeController {
    private final CrudServiceType crudServiceTypeImpl;

    @PostMapping("/repository")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ServiceTypeRepoResponseDto> createServiceTypeRepo(@Valid @RequestBody ServiceTypeRequestDto serviceTypeRequestDto) {
        return ResponseEntity.ok(crudServiceTypeImpl.saveServiceTypeRepo(serviceTypeRequestDto));
    }

    @PostMapping("/branch/{branchId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ServiceTypeBranchResponseDto> createServiceTypeBranch(@PathVariable String branchId, @Valid @RequestBody ServiceTypeRequestDto serviceTypeRequestDto) {
        return ResponseEntity.ok(crudServiceTypeImpl.saveServiceTypeBranch(branchId, serviceTypeRequestDto));
    }

    @PostMapping("/client/{clientId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ServiceTypeClientResponseDto> createServiceTypeClient(@PathVariable String clientId, @Valid @RequestBody ServiceTypeRequestDto serviceTypeRequestDto) {
        return ResponseEntity.ok(crudServiceTypeImpl.saveServiceTypeClient(clientId, serviceTypeRequestDto));
    }

    @GetMapping("/{idServiceType}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<ServiceTypeFullResponseDto> getOneServiceType(@PathVariable String idServiceType) {
        return ResponseEntity.ok(crudServiceTypeImpl.getServiceType(idServiceType));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ServiceTypeFullResponseDto>> getAllServiceTypes() {
        return ResponseEntity.ok(crudServiceTypeImpl.getAllServiceType());
    }

    @GetMapping(params = {"idOwner", "owner"})
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ServiceTypeFullResponseDto>> getAllServiceTypesByOwner(@RequestParam String idOwner, @RequestParam CrudServiceType.Owner owner) {
        List<ServiceTypeFullResponseDto> serviceTypeResponseDtos = new ArrayList<>();
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

    @PutMapping("/repository/{idServiceType}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<ServiceTypeRepoResponseDto> updateServiceTypeRepo(@PathVariable String idServiceType, @Valid @RequestBody ServiceTypeRequestDto serviceTypeRequestDto) {
        return ResponseEntity.ok(crudServiceTypeImpl.updateServiceTypeRepo(idServiceType, serviceTypeRequestDto));
    }

    @PutMapping("/branch/{idServiceType}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<ServiceTypeBranchResponseDto> updateServiceTypeBranch(@PathVariable String idServiceType, @Valid @RequestBody ServiceTypeRequestDto serviceTypeRequestDto) {
        return ResponseEntity.ok(crudServiceTypeImpl.updateServiceTypeBranch(idServiceType, serviceTypeRequestDto));
    }

    @PutMapping("/client/{idServiceType}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<ServiceTypeClientResponseDto> updateServiceTypeClient(@PathVariable String idServiceType, @Valid @RequestBody ServiceTypeRequestDto serviceTypeRequestDto) {
        return ResponseEntity.ok(crudServiceTypeImpl.updateServiceTypeClient(idServiceType, serviceTypeRequestDto));
    }

    @DeleteMapping("/{idServiceType}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteServiceType(@PathVariable String idServiceType) {
        crudServiceTypeImpl.deleteServiceType(idServiceType);
        return ResponseEntity.noContent().build();
    }
}
