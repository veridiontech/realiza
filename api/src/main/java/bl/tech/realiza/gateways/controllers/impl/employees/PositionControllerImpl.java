package bl.tech.realiza.gateways.controllers.impl.employees;

import bl.tech.realiza.gateways.controllers.interfaces.employees.PositionController;
import bl.tech.realiza.gateways.requests.employees.PositionRequestDto;
import bl.tech.realiza.gateways.responses.employees.PositionResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudPosition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/position")
@Tag(name = "Position")
public class PositionControllerImpl implements PositionController {
    private final CrudPosition crudPosition;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Cria um cargo")
    @Override
    public ResponseEntity<PositionResponseDto> save(@RequestBody @Valid PositionRequestDto positionRequestDto) {
        return ResponseEntity.ok(crudPosition.save(positionRequestDto));
    }

    @PutMapping("/{positionId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Edita um cargo via id")
    @Override
    public ResponseEntity<PositionResponseDto> update(@PathVariable String positionId, @RequestBody @Valid PositionRequestDto positionRequestDto) {
        return ResponseEntity.ok(crudPosition.update(positionId, positionRequestDto));
    }

    @GetMapping("/{positionId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca um cargo via id")
    @Override
    public ResponseEntity<PositionResponseDto> findOne(@PathVariable String positionId) {
        return ResponseEntity.ok(crudPosition.findOne(positionId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Busca todos os cargos")
    @Override
    public ResponseEntity<List<PositionResponseDto>> findAll() {
        return ResponseEntity.ok(crudPosition.findAll());
    }

    @DeleteMapping("/{positionId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Deleta um cargo via id")
    @Override
    public ResponseEntity<Void> delete(@PathVariable String positionId) {
        crudPosition.delete(positionId);
        return ResponseEntity.noContent().build();
    }
}
