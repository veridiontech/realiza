package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.controllers.interfaces.services.AuthController;
import bl.tech.realiza.gateways.requests.services.ExtractTokenRequestDto;
import bl.tech.realiza.gateways.requests.services.LoginRequestDto;
import bl.tech.realiza.gateways.responses.services.LoginResponseDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.AuthService;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.auth.PasswordRecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final PasswordRecoveryService passwordRecoveryService;

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

        UserResponseDto userResponseDto = jwtService.extractAllClaims(extractTokenRequestDto.getToken());

        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/change-my-password/{idUser}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> changePassword(@PathVariable String idUser, @RequestParam String newPassword) {

        String response = passwordRecoveryService.changePassword(idUser, newPassword);

        return ResponseEntity.ok(response);
    }

}
