package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.gateways.requests.enterprises.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.responses.services.EnterpriseAndUserResponseDto;
import org.springframework.http.ResponseEntity;

public interface EnterpriseController {
    ResponseEntity<EnterpriseAndUserResponseDto> createEnterpriseAndUser(EnterpriseAndUserRequestDto clientAndUserClientRequestDto);
}
