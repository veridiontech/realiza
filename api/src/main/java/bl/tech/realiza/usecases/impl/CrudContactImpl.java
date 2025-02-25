package bl.tech.realiza.usecases.impl;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Contact;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.services.ContactRepository;
import bl.tech.realiza.gateways.requests.services.ContactRequestDto;
import bl.tech.realiza.gateways.responses.services.ContactResponseDto;
import bl.tech.realiza.usecases.interfaces.CrudContact;
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
    private final BranchRepository branchRepository;

    @Override
    public ContactResponseDto save(ContactRequestDto contactRequestDto) {

        if (contactRequestDto.getClient() == null || contactRequestDto.getClient().isEmpty()) {
            throw new BadRequestException("Invalid Branch");
        }

        Branch branch = branchRepository.findById(contactRequestDto.getClient()).orElseThrow(() -> new NotFoundException("Branch not found"));

        Contact newContact = Contact.builder()
                .department(contactRequestDto.getDepartment())
                .email(contactRequestDto.getEmail())
                .country(contactRequestDto.getCountry())
                .telephone(contactRequestDto.getTelephone())
                .mainContact(contactRequestDto.getMainContact())
                .branch(branch)
                .build();

        Contact savedContact = contactRepository.save(newContact);

        ContactResponseDto contactResponse = ContactResponseDto.builder()
                .idContact(savedContact.getIdContact())
                .department(savedContact.getDepartment())
                .email(savedContact.getEmail())
                .country(savedContact.getCountry())
                .telephone(savedContact.getTelephone())
                .mainContact(savedContact.getMainContact())
                .branch(savedContact.getBranch().getIdBranch())
                .build();

        return contactResponse;
    }

    @Override
    public Optional<ContactResponseDto> findOne(String id) {
        Optional<Contact> contactOptional = contactRepository.findById(id);

        Contact contact = contactOptional.orElseThrow(() -> new NotFoundException("Contact not found"));

        ContactResponseDto contactResponse = ContactResponseDto.builder()
                .idContact(contact.getIdContact())
                .department(contact.getDepartment())
                .email(contact.getEmail())
                .country(contact.getCountry())
                .telephone(contact.getTelephone())
                .mainContact(contact.getMainContact())
                .branch(contact.getBranch().getIdBranch())
                .build();

        return Optional.of(contactResponse);
    }

    @Override
    public Page<ContactResponseDto> findAll(Pageable pageable) {
        Page<Contact> contactPage = contactRepository.findAll(pageable);

        Page<ContactResponseDto> contactResponseDtoPage = contactPage.map(
                contact -> ContactResponseDto.builder()
                        .idContact(contact.getIdContact())
                        .department(contact.getDepartment())
                        .email(contact.getEmail())
                        .country(contact.getCountry())
                        .telephone(contact.getTelephone())
                        .mainContact(contact.getMainContact())
                        .branch(contact.getBranch().getIdBranch())
                        .build()
        );

        return contactResponseDtoPage;
    }

    @Override
    public Optional<ContactResponseDto> update(String id, ContactRequestDto contactRequestDto) {
        Optional<Contact> contactOptional = contactRepository.findById(id);

        Contact contact = contactOptional.orElseThrow(() -> new NotFoundException("Contact not found"));

        contact.setDepartment(contactRequestDto.getDepartment() != null ? contactRequestDto.getDepartment() : contact.getDepartment());
        contact.setEmail(contactRequestDto.getEmail() != null ? contactRequestDto.getEmail() : contact.getEmail());
        contact.setCountry(contactRequestDto.getCountry() != null ? contactRequestDto.getCountry() : contact.getCountry());
        contact.setTelephone(contactRequestDto.getTelephone() != null ? contactRequestDto.getTelephone() : contact.getTelephone());
        contact.setMainContact(contactRequestDto.getMainContact() != null ? contactRequestDto.getMainContact() : contact.getMainContact());

        Contact savedContact = contactRepository.save(contact);

        ContactResponseDto contactResponse = ContactResponseDto.builder()
                .idContact(savedContact.getIdContact())
                .department(savedContact.getDepartment())
                .email(savedContact.getEmail())
                .country(savedContact.getCountry())
                .telephone(savedContact.getTelephone())
                .mainContact(savedContact.getMainContact())
                .branch(savedContact.getBranch().getIdBranch())
                .build();

        return Optional.of(contactResponse);
    }

    @Override
    public void delete(String id) {
        contactRepository.deleteById(id);
    }

    @Override
    public Page<ContactResponseDto> findAllByEnterprise(String idSearch, Provider.Company company, Pageable pageable) {
        Page<Contact> contactPage = contactRepository.findAllByBranch_IdBranch(idSearch, pageable);

        Page<ContactResponseDto> contactResponseDtoPage = contactPage.map(
                contact -> ContactResponseDto.builder()
                        .idContact(contact.getIdContact())
                        .department(contact.getDepartment())
                        .email(contact.getEmail())
                        .country(contact.getCountry())
                        .telephone(contact.getTelephone())
                        .mainContact(contact.getMainContact())
                        .branch(contact.getBranch().getIdBranch())
                        .build()
        );

        return contactResponseDtoPage;
    }
}
