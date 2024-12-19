package bl.tech.realiza.gateways.controllers.impl.contracts;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.ActivityController;
import bl.tech.realiza.gateways.requests.contracts.ActivityRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ActivityResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ActivityControllerImpl implements ActivityController {
    @Override
    public ResponseEntity<ActivityResponseDto> createActivity(ActivityRequestDto activityRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ActivityResponseDto>> getOneActivity(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Page<ActivityResponseDto>> getAllActivities(int page, int size, String sort, Sort.Direction direction) {
        return null;
    }

    @Override
    public ResponseEntity<Optional<ActivityResponseDto>> updateActivity(ActivityRequestDto activityRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deleteActivity(String id) {
        return null;
    }
}
