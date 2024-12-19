package bl.tech.realiza.gateways.controllers.impl.clients;

import bl.tech.realiza.gateways.controllers.interfaces.clients.ContactController;
import bl.tech.realiza.gateways.requests.clients.ContactRequestDto;
import bl.tech.realiza.gateways.responses.clients.ContactResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ContactControllerImpl implements ContactController {
    @Override
    public ResponseEntity<ContactResponseDto> createContact(ContactRequestDto contactRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ContactResponseDto>> getOneContact(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<ContactResponseDto>> getAllContacts(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ContactResponseDto>> updateContact(ContactRequestDto contactRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteContact(String id) {
        return null;
    }
}
