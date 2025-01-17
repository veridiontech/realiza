package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.controllers.interfaces.services.AuthController;
import bl.tech.realiza.gateways.requests.services.EmailRequestDto;
import bl.tech.realiza.gateways.requests.services.LoginRequestDto;
import bl.tech.realiza.gateways.responses.services.LoginResponseDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.AuthService;
import bl.tech.realiza.services.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.TimeZone;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        String token = authService.login(email, password);

        return ResponseEntity.ok(LoginResponseDto.builder()
                .token(token)
                .build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/extract-token")
    public ResponseEntity<UserResponseDto> extract(@RequestBody String token) {
        // Extrai todas as claims do token
        Map<String, Object> claims = jwtService.extractAllClaims(token);

        // Constrói o objeto de resposta com os dados das claims
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .idUser((String) claims.get("idUser"))
                .cpf((String) claims.get("cpf"))
                .position((String) claims.get("position"))
                .role(User.Role.valueOf((String) claims.get("role")))
                .firstName((String) claims.get("firstName"))
                .surname((String) claims.get("surname"))
                .email((String) claims.get("email"))
                .telephone((String) claims.get("telephone"))
                .cellphone((String) claims.get("cellphone"))
                .build();

        return ResponseEntity.ok(userResponseDto);
    }



}
