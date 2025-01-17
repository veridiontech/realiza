package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.gateways.requests.services.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.responses.services.EnterpriseAndUserResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface EnterpriseController {
    ResponseEntity<EnterpriseAndUserResponseDto> createEnterpriseAndUser(EnterpriseAndUserRequestDto clientAndUserClientRequestDto);
}
