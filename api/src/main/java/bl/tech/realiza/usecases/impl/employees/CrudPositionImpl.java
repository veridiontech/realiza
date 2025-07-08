package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.domains.employees.Position;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.employees.PositionRepository;
import bl.tech.realiza.gateways.requests.employees.PositionRequestDto;
import bl.tech.realiza.gateways.responses.employees.PositionResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrudPositionImpl implements CrudPosition {
    private final PositionRepository positionRepository;

    @Override
    public PositionResponseDto save(PositionRequestDto positionRequestDto) {
        Position position = Position.builder()
                .title(positionRequestDto.getTitle())
                .build();

        return toResponse(positionRepository.save(position));
    }

    @Override
    public PositionResponseDto findOne(String id) {
        return toResponse(positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Position not found")));
    }

    @Override
    public PositionResponseDto update(String id, PositionRequestDto positionRequestDto) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Position not found"));

        position.setTitle(positionRequestDto.getTitle() != null ? positionRequestDto.getTitle() : position.getTitle());

        return toResponse(positionRepository.save(position));
    }

    @Override
    public List<PositionResponseDto> findAll() {
        return toResponse(positionRepository.findAll());
    }

    @Override
    public void delete(String id) {
        positionRepository.deleteById(id);
    }

    private PositionResponseDto toResponse(Position position) {
        return PositionResponseDto.builder()
                .id(position.getId())
                .title(position.getTitle())
                .build();
    }

    private List<PositionResponseDto> toResponse(List<Position> positionList) {
        return positionList.stream()
                .sorted(Comparator.comparing(Position::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::toResponse)
                .toList();
    }
}
