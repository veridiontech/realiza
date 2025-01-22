package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.requests.services.ContactRequestDto;
import bl.tech.realiza.gateways.responses.services.ContactResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ContactControlller {
    ResponseEntity<ContactResponseDto> createContact(ContactRequestDto contactRequestDto);
    ResponseEntity<Optional<ContactResponseDto>> getOneContact(String id);
    ResponseEntity<Page<ContactResponseDto>> getAllContacts(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Optional<ContactResponseDto>> updateContact(ContactRequestDto contactRequestDto);
    ResponseEntity<Void> deleteContact(String id);
    ResponseEntity<Page<ContactResponseDto>> getAllContactByEnterprise(int page, int size, String sort, Sort.Direction direction, Provider.Company company, String idSearch);
}
