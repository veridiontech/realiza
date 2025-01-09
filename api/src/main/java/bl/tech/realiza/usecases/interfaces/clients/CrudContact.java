package bl.tech.realiza.usecases.interfaces.clients;

import bl.tech.realiza.gateways.requests.clients.ContactRequestDto;
import bl.tech.realiza.gateways.responses.clients.ContactResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudContact {
    ContactResponseDto save(ContactRequestDto contactRequestDto);
    Optional<ContactResponseDto> findOne(String id);
    Page<ContactResponseDto> findAll(Pageable pageable);
    Optional<ContactResponseDto> update(ContactRequestDto contactRequestDto);
    void delete(String id);
}
