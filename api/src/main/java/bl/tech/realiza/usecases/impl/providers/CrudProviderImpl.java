package bl.tech.realiza.usecases.impl.providers;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.providers.ProviderRepository;
import bl.tech.realiza.usecases.interfaces.providers.CrudProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrudProviderImpl implements CrudProvider {
    private final ProviderRepository providerRepository;

    @Override
    public String providerActivation(String providerId, Boolean activation) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() ->  new NotFoundException("Provider not found"));

        if (activation) {
            provider.setIsActive(true);
            providerRepository.save(provider);
            return "Provider activated";
        } else {
            provider.setIsActive(false);
            providerRepository.save(provider);
            return "Provider inactivated";
        }
    }
}
