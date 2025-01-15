package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contract.Activity;
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
        Optional<Activity> activityOptional = activityRepository.findById(id);

        Activity activity = activityOptional.orElseThrow(() -> new RuntimeException("Activity not found"));

        ActivityResponseDto activityResponse = ActivityResponseDto.builder()
                .idActivity(activity.getIdActivity())
                .title(activity.getTitle())
                .build();

        return Optional.of(activityResponse);
    }

    @Override
    public Page<ActivityResponseDto> findAll(Pageable pageable) {
        Page<Activity> activityPage = activityRepository.findAll(pageable);

        Page<ActivityResponseDto> activities = activityPage.map(
                activity -> ActivityResponseDto.builder()
                        .idActivity(activity.getIdActivity())
                        .title(activity.getTitle())
                        .build()
        );

        return activities;
    }

    @Override
    public Optional<ActivityResponseDto> update(ActivityRequestDto activityRequestDto) {
        Optional<Activity> activityOptional = activityRepository.findById(activityRequestDto.getIdActivity());

        Activity activity = activityOptional.orElseThrow(() -> new RuntimeException("Activity not found"));

        activity.setTitle(activityRequestDto.getTitle() != null ? activityRequestDto.getTitle() : activity.getTitle());
        activity.setIsActive(activityRequestDto.getIsActive() != null ? activityRequestDto.getIsActive() : activity.getIsActive());

        Activity savedActivity = activityRepository.save(activity);

        ActivityResponseDto activityResponse = ActivityResponseDto.builder()
                .idActivity(savedActivity.getIdActivity())
                .title(savedActivity.getTitle())
                .build();

        return Optional.of(activityResponse);
    }

    @Override
    public void delete(String id) {
        activityRepository.deleteById(id);
    }
}
