package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.controllers.interfaces.services.EnterpriseController;
import bl.tech.realiza.gateways.requests.enterprises.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.responses.services.EnterpriseAndUserResponseDto;
import bl.tech.realiza.usecases.impl.CrudEnterpriseAndUserImpl;
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
        switch (clientAndUserClientRequestDto.getCompany()) {
            case CLIENT -> enterpriseAndUser = crudEnterpriseAndUser.saveBothClient(clientAndUserClientRequestDto);
            case SUPPLIER -> enterpriseAndUser = crudEnterpriseAndUser.saveBothSupplier(clientAndUserClientRequestDto);
            case SUBCONTRACTOR -> enterpriseAndUser = crudEnterpriseAndUser.saveBothSubcontractor(clientAndUserClientRequestDto);
        }
        assert enterpriseAndUser != null;
        return ResponseEntity.of(Optional.of(enterpriseAndUser));
    }
}
