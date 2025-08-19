package bl.tech.realiza.usecases.interfaces.contracts.activity;

import bl.tech.realiza.gateways.requests.contracts.activity.ActivityRepoRequestDto;
import bl.tech.realiza.gateways.responses.contracts.activity.ActivityRepoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudActivityRepo {
    ActivityRepoResponseDto save(ActivityRepoRequestDto activityRepoRequestDto);
    Optional<ActivityRepoResponseDto> findOne(String id);
    Page<ActivityRepoResponseDto> findAll(Pageable pageable);
    Optional<ActivityRepoResponseDto> update(String id, ActivityRepoRequestDto activityRepoRequestDto);
    void delete(String id);
    Boolean findExistsByName(String name);
}
