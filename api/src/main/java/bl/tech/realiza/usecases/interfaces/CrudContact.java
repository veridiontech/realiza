package bl.tech.realiza.usecases.interfaces;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.requests.services.ContactRequestDto;
import bl.tech.realiza.gateways.responses.services.ContactResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudContact {
    ContactResponseDto save(ContactRequestDto contactRequestDto);
    Optional<ContactResponseDto> findOne(String id);
    Page<ContactResponseDto> findAll(Pageable pageable);
    Optional<ContactResponseDto> update(ContactRequestDto contactRequestDto);
    void delete(String id);
    Page<ContactResponseDto> findAllByEnterprise(String idSearch, Provider.Company company, Pageable pageable);
}
