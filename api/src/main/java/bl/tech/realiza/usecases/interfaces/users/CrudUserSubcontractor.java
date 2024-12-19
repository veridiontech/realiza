package bl.tech.realiza.usecases.interfaces.users;

import bl.tech.realiza.gateways.requests.users.UserSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSubcontractorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudUserSubcontractor {
    UserSubcontractorResponseDto save(UserSubcontractorRequestDto userSubcontractorRequestDto);
    Optional<UserSubcontractorResponseDto> findOne(String id);
    Page<UserSubcontractorResponseDto> findAll(Pageable pageable);
    Optional<UserSubcontractorResponseDto> update(UserSubcontractorRequestDto userSubcontractorRequestDto);
    void delete(String id);
}
