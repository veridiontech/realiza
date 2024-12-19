package bl.tech.realiza.gateways.controllers.interfaces.clients;

import bl.tech.realiza.gateways.requests.clients.ContactRequestDto;
import bl.tech.realiza.gateways.responses.clients.ContactResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ContactController {
    ResponseEntity<ContactResponseDto> createContact(ContactRequestDto contactRequestDto);
    ResponseEntity<Optional<ContactResponseDto>> getOneContact(String id);
    ResponseEntity<Page<ContactResponseDto>> getAllContacts(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ContactResponseDto>> updateContact(ContactRequestDto contactRequestDto);
    ResponseEntity<Void> deleteContact(String id);
}
