package bl.tech.realiza.usecases.impl.contracts.activity;

import bl.tech.realiza.domains.contract.activity.ActivityRepo;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepoRepository;
import bl.tech.realiza.gateways.requests.contracts.activity.ActivityRepoRequestDto;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityRepoResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.activity.CrudActivityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudActivityRepoImpl implements CrudActivityRepo {

    private final ActivityRepoRepository activityRepoRepository;

    @Override
    public ActivityRepoResponseDto save(ActivityRepoRequestDto activityRepoRequestDto) {
        ActivityRepo activityRepo = ActivityRepo.builder()
                .title(activityRepoRequestDto.getTitle())
                .risk(activityRepoRequestDto.getRisk())
                .build();

        ActivityRepo savedActivityRepo = activityRepoRepository.save(activityRepo);

        return ActivityRepoResponseDto.builder()
                .idActivity(savedActivityRepo.getIdActivity())
                .title(savedActivityRepo.getTitle())
                .risk(savedActivityRepo.getRisk())
                .build();
    }

    @Override
    public Optional<ActivityRepoResponseDto> findOne(String id) {
        ActivityRepo activityRepo = activityRepoRepository.findById(id).orElseThrow(() -> new NotFoundException("Activity not found"));

        return Optional.of(ActivityRepoResponseDto.builder()
                        .idActivity(activityRepo.getIdActivity())
                        .title(activityRepo.getTitle())
                        .risk(activityRepo.getRisk())
                        .build());
    }

    @Override
    public Page<ActivityRepoResponseDto> findAll(Pageable pageable) {
        Page<ActivityRepo> activities = activityRepoRepository.findAll(pageable);

        return activities.map(
                activityRepo -> ActivityRepoResponseDto.builder()
                        .idActivity(activityRepo.getIdActivity())
                        .title(activityRepo.getTitle())
                        .risk(activityRepo.getRisk())
                        .build()
        );
    }

    @Override
    public Optional<ActivityRepoResponseDto> update(String id, ActivityRepoRequestDto activityRepoRequestDto) {
        ActivityRepo activityRepo = activityRepoRepository.findById(id).orElseThrow(() -> new NotFoundException("Activity not found"));

        activityRepo.setTitle(activityRepoRequestDto.getTitle() != null ? activityRepoRequestDto.getTitle() : activityRepo.getTitle());
        activityRepo.setRisk(activityRepoRequestDto.getRisk() != null ? activityRepoRequestDto.getRisk() : activityRepo.getRisk());

        ActivityRepo savedActivityRepo = activityRepoRepository.save(activityRepo);

        return Optional.of(ActivityRepoResponseDto.builder()
                        .idActivity(savedActivityRepo.getIdActivity())
                        .title(savedActivityRepo.getTitle())
                        .risk(savedActivityRepo.getRisk())
                        .build());
    }

    @Override
    public void delete(String id) {
        activityRepoRepository.deleteById(id);
    }
}
