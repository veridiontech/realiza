package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.controllers.interfaces.services.EnterpriseController;
import bl.tech.realiza.gateways.requests.clients.ClientAndUserClientRequestDto;
import bl.tech.realiza.gateways.requests.services.EmailRequestDto;
import bl.tech.realiza.gateways.requests.services.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientAndUserClientResponseDto;
import bl.tech.realiza.gateways.responses.services.EnterpriseAndUserResponseDto;
import bl.tech.realiza.usecases.impl.CrudEnterpriseAndUserImpl;
import bl.tech.realiza.usecases.impl.clients.CrudClientImpl;
import bl.tech.realiza.usecases.impl.providers.CrudProviderSubcontractorImpl;
import bl.tech.realiza.usecases.impl.providers.CrudProviderSupplierImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sign-enterprise")
@Tag(name = "Enterprise and User")
public class EnterpriseControllerImpl implements EnterpriseController {

    private final CrudEnterpriseAndUserImpl crudEnterpriseAndUser;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<EnterpriseAndUserResponseDto> createEnterpriseAndUser(@RequestBody @Valid EnterpriseAndUserRequestDto clientAndUserClientRequestDto) {
        EnterpriseAndUserResponseDto enterpriseAndUser = null;
        if (clientAndUserClientRequestDto.getCompany() == EmailRequestDto.Company.CLIENT) {
            enterpriseAndUser = crudEnterpriseAndUser.saveBothClient(clientAndUserClientRequestDto);
        } else if (clientAndUserClientRequestDto.getCompany() == EmailRequestDto.Company.SUPPLIER) {
            enterpriseAndUser = crudEnterpriseAndUser.saveBothClient(clientAndUserClientRequestDto);
        } else if (clientAndUserClientRequestDto.getCompany() == EmailRequestDto.Company.SUBCONTRACTOR) {
            enterpriseAndUser = crudEnterpriseAndUser.saveBothClient(clientAndUserClientRequestDto);
        }
        assert enterpriseAndUser != null;
        return ResponseEntity.of(Optional.of(enterpriseAndUser));
    }
}
