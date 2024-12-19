package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.gateways.requests.users.UserSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSubcontractorResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudUserSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudUserSubcontractorImpl implements CrudUserSubcontractor {
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
    public Optional<UserSubcontractorResponseDto> update(String id, UserSubcontractorRequestDto userSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
