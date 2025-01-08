package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.gateways.requests.clients.ContactRequestDto;
import bl.tech.realiza.gateways.responses.clients.ContactResponseDto;
import bl.tech.realiza.usecases.interfaces.clients.CrudContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class CrudContactImpl implements CrudContact {
    @Override
    public ContactResponseDto save(ContactRequestDto contactRequestDto) {
        return null;
    }

    @Override
    public Optional<ContactResponseDto> findOne(String id) {
        return Optional.empty();
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
