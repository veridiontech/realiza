package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contracts.Activity;
import bl.tech.realiza.gateways.repositories.contracts.ActivityRepository;
import bl.tech.realiza.gateways.requests.contracts.ActivityRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudActivityImpl implements CrudActivity {

    private final ActivityRepository activityRepository;

    @Override
    public ActivityResponseDto save(ActivityRequestDto activityRequestDto) {

        Activity activity = Activity.builder()
                .title(activityRequestDto.getTitle())
                .build();

        Activity savedActivity = activityRepository.save(activity);

        ActivityResponseDto activityResponse = ActivityResponseDto.builder()
                .idActivity(savedActivity.getIdActivity())
                .title(savedActivity.getTitle())
                .build();

        return activityResponse;
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
