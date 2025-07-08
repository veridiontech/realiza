package bl.tech.realiza.gateways.controllers.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.PositionRequestDto;
import bl.tech.realiza.gateways.responses.employees.PositionResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PositionController {
    ResponseEntity<PositionResponseDto> save(PositionRequestDto positionRequestDto);
    ResponseEntity<PositionResponseDto> update(String positionId, PositionRequestDto positionRequestDto);
    ResponseEntity<PositionResponseDto> findOne(String positionId);
    ResponseEntity<List<PositionResponseDto>> findAll();
    ResponseEntity<Void> delete(String positionId);
}
