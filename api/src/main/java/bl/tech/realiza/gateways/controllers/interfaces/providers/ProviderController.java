package bl.tech.realiza.gateways.controllers.interfaces.providers;

import org.springframework.http.ResponseEntity;

public interface ProviderController {
    ResponseEntity<String> providerActivation(String providerId, Boolean activation);
}
