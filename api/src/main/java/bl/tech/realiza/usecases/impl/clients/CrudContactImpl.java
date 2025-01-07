package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.clients.Contact;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.clients.ContactRepository;
import bl.tech.realiza.gateways.requests.clients.ContactRequestDto;
import bl.tech.realiza.gateways.responses.clients.ContactResponseDto;
import bl.tech.realiza.usecases.interfaces.clients.CrudContact;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudContactImpl implements CrudContact {

    private final ContactRepository contactRepository;
    private final ClientRepository clientRepository;

    @Override
    public ContactResponseDto save(ContactRequestDto contactRequestDto) {

        Optional<Client> clientOptional = clientRepository.findById(contactRequestDto.getClient());

        Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        Contact newContact = Contact.builder()
                .department(contactRequestDto.getDepartment())
                .email(contactRequestDto.getEmail())
                .country(contactRequestDto.getCountry())
                .telephone(contactRequestDto.getTelephone())
                .mainContact(contactRequestDto.getMainContact())
                .client(client)
                .build();

        Contact savedContact = contactRepository.save(newContact);

        ContactResponseDto contactResponse = ContactResponseDto.builder()
                .idContact(savedContact.getIdContact())
                .department(savedContact.getDepartment())
                .email(savedContact.getEmail())
                .country(savedContact.getCountry())
                .telephone(savedContact.getTelephone())
                .mainContact(savedContact.getMainContact())
                .client(savedContact.getClient().getIdClient())
                .build();

        return contactResponse;
    }

    @Override
    public Optional<ContactResponseDto> findOne(String id) {

        Optional<Contact> contactOptional = contactRepository.findById(id);

        Contact contact = contactOptional.orElseThrow(() -> new RuntimeException("Contact not found"));

        ContactResponseDto contactResponse = ContactResponseDto.builder()
                .idContact(contact.getIdContact())
                .department(contact.getDepartment())
                .email(contact.getEmail())
                .country(contact.getCountry())
                .telephone(contact.getTelephone())
                .mainContact(contact.getMainContact())
                .client(contact.getClient().getIdClient())
                .build();

        return Optional.of(contactResponse);
    }

    @Override
    public Page<ContactResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ContactResponseDto> update(ContactRequestDto contactRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
