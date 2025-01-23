package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.controllers.interfaces.services.ContactControlller;
import bl.tech.realiza.gateways.requests.services.ContactRequestDto;
import bl.tech.realiza.gateways.responses.services.ContactResponseDto;
import bl.tech.realiza.usecases.impl.CrudContactImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contact")
@Tag(name = "Contact")
public class ContactControllerImpl implements ContactControlller {

    private final CrudContactImpl crudContact;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ContactResponseDto> createContact(@RequestBody @Valid ContactRequestDto contactRequestDto) {
        ContactResponseDto contact = crudContact.save(contactRequestDto);

        return ResponseEntity.of(Optional.of(contact));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContactResponseDto>> getOneContact(@PathVariable String id) {
        Optional<ContactResponseDto> contact = crudContact.findOne(id);

        return ResponseEntity.of(Optional.of(contact));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContactResponseDto>> getAllContacts(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "5") int size,
                                                                   @RequestParam(defaultValue = "idContact") String sort,
                                                                   @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContactResponseDto> pageContact = crudContact.findAll(pageable);

        return ResponseEntity.ok(pageContact);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ContactResponseDto>> updateContact(@PathVariable String id, @RequestBody @Valid ContactRequestDto contactRequestDto) {
        Optional<ContactResponseDto> contact = crudContact.update(id, contactRequestDto);

        return ResponseEntity.of(Optional.of(contact));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {
        crudContact.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-enterprise")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContactResponseDto>> getAllContactByEnterprise(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "5") int size,
                                                                              @RequestParam(defaultValue = "idContact") String sort,
                                                                              @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                              @RequestParam Provider.Company company,
                                                                              @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ContactResponseDto> pageContact = crudContact.findAllByEnterprise(idSearch, company, pageable);

        return ResponseEntity.ok(pageContact);
    }
}
