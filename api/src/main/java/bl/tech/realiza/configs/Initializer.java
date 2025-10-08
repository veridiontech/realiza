package bl.tech.realiza.configs;

import bl.tech.realiza.domains.user.UserManager;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.users.UserCreateRequestDto;
import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.usecases.impl.users.CrudUserManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import static bl.tech.realiza.domains.user.User.Role.ROLE_REALIZA_PLUS;

@Slf4j
@RequiredArgsConstructor
@Component
public class Initializer implements ApplicationRunner {
    private final CrudUserManagerImpl crudUserManagerImpl;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing...");
        if (!userRepository.existsByEmail("realiza@assessoria.com")) {
            log.info("Creating user Realiza...");
            crudUserManagerImpl.save(UserManagerRequestDto.builder()
                            .cpf("342.419.420-40")
                            .description("Conta administradora do sistema")
                            .password("senha123")
                            .position("Gerente de Contas")
                            .role(ROLE_REALIZA_PLUS)
                            .firstName("Realiza")
                            .surname("Assessoria")
                            .email("realiza@assessoria.com")
                            .telephone("1133344455")
                    .cellphone("11999999999")
                    .build(), null);
        } else {
            log.info("User Realiza already exists.");
        }
        log.info("App Initialized.");
    }
}
