package bl.tech.realiza.gateways.controllers.impl.providers;

import bl.tech.realiza.gateways.controllers.interfaces.providers.ProviderController;
import bl.tech.realiza.usecases.interfaces.providers.CrudProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/provider")
@Tag(name = "Provider")
public class ProviderControllerImpl implements ProviderController {
    private final CrudProvider crudProvider;

    @PostMapping("/{providerId}/activation")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<String> providerActivation(@PathVariable String providerId, @RequestParam Boolean activation) {
        return ResponseEntity.ok(crudProvider.providerActivation(providerId, activation));
    }
}
