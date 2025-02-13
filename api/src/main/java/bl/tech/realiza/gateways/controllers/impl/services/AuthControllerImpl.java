package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.controllers.interfaces.services.AuthController;
import bl.tech.realiza.gateways.requests.services.ExtractTokenRequestDto;
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

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        String token = authService.login(email, password);

        return ResponseEntity.ok(LoginResponseDto.builder()
                .token(token)
                .build());
    }

    @PostMapping("/extract-token")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<UserResponseDto> extract(@RequestBody ExtractTokenRequestDto extractTokenRequestDto) {
        // Extrai todas as claims do token
        Map<String, Object> claims = jwtService.extractAllClaims(extractTokenRequestDto.getToken());

        // Constrói o objeto de resposta com os dados das claims
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .idUser((String) claims.getOrDefault("idUser", ""))
                .cpf((String) claims.getOrDefault("cpf", ""))
                .position((String) claims.getOrDefault("position", ""))
                .role(User.Role.valueOf((String) claims.getOrDefault("role", "")))
                .firstName((String) claims.getOrDefault("firstName", ""))
                .surname((String) claims.getOrDefault("surname", ""))
                .email((String) claims.getOrDefault("email", ""))
                .telephone((String) claims.getOrDefault("telephone", ""))
                .cellphone((String) claims.getOrDefault("cellphone", ""))
                .build();

        return ResponseEntity.ok(userResponseDto);
    }



}
