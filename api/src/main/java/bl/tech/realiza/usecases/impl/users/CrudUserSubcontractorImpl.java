package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.gateways.repositories.users.UserSubcontractorRepository;
import bl.tech.realiza.gateways.requests.users.UserSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSubcontractorResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudUserSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserSubcontractorImpl implements CrudUserSubcontractor {

    private final UserSubcontractorRepository userSubcontractorRepository;

    @Override
    public UserSubcontractorResponseDto save(UserSubcontractorRequestDto userSubcontractorRequestDto) {
        return null;
    }

    @Override
    public Optional<UserSubcontractorResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<UserSubcontractorResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<UserSubcontractorResponseDto> update(UserSubcontractorRequestDto userSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
