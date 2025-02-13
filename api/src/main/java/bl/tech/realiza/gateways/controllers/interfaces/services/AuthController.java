package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.gateways.requests.services.ExtractTokenRequestDto;
import bl.tech.realiza.gateways.requests.services.LoginRequestDto;
import bl.tech.realiza.gateways.responses.services.LoginResponseDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthController {
    ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto);
    ResponseEntity<UserResponseDto> extract(ExtractTokenRequestDto extractTokenRequestDto);
}
