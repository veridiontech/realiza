package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.gateways.requests.contracts.ActivityRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class CrudActivityImpl implements CrudActivity {
    @Override
    public ActivityResponseDto save(ActivityRequestDto activityRequestDto) {
        return null;
    }

    @Override
    public Optional<ActivityResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<ActivityResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ActivityResponseDto> update(ActivityRequestDto activityRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
