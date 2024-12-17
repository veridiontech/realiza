package bl.tech.realiza.usecases.interfaces.clients;

import bl.tech.realiza.gateways.responses.clients.ContactResponseDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ContactCrud {
    Optional<ContactResponseDto> save(String id);
}
