package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.PositionRequestDto;
import bl.tech.realiza.gateways.responses.employees.PositionResponseDto;

import java.util.List;

public interface CrudPosition {
    PositionResponseDto save(PositionRequestDto positionRequestDto);
    PositionResponseDto findOne(String id);
    PositionResponseDto update(String id, PositionRequestDto positionRequestDto);
    List<PositionResponseDto> findAll();
    void delete(String id);
}
