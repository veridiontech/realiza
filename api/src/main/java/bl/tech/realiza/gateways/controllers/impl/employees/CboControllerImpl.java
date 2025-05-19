package bl.tech.realiza.gateways.controllers.impl.employees;

import bl.tech.realiza.gateways.controllers.interfaces.employees.CboController;
import bl.tech.realiza.gateways.requests.employees.CboRequestDto;
import bl.tech.realiza.gateways.responses.employees.CboResponseDto;
import bl.tech.realiza.usecases.impl.employees.CrudCboImpl;
import bl.tech.realiza.usecases.interfaces.employees.CrudCbo;
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
@RequestMapping("/cbo")
@Tag(name = "CBO")
public class CboControllerImpl implements CboController {
    private final CrudCbo crudCbo;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Cria uma função do cbo")
    @Override
    public ResponseEntity<CboResponseDto> save(@RequestBody @Valid CboRequestDto cboRequestDto) {
        return ResponseEntity.ok(crudCbo.save(cboRequestDto));
    }

    @PutMapping("/{cboId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Edita uma função do cbo via id")
    @Override
    public ResponseEntity<CboResponseDto> update(@PathVariable String cboId, @RequestBody @Valid CboRequestDto cboRequestDto) {
        return ResponseEntity.ok(crudCbo.update(cboId, cboRequestDto));
    }

    @GetMapping("/{cboId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Busca uma função do cbo via id")
    @Override
    public ResponseEntity<CboResponseDto> findOne(@PathVariable String cboId) {
        return ResponseEntity.ok(crudCbo.findOne(cboId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Busca todas as funções do cbo")
    @Override
    public ResponseEntity<List<CboResponseDto>> findAll() {
        return ResponseEntity.ok(crudCbo.findAll());
    }

    @DeleteMapping("/{cboId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Deleta uma função do cbo via id")
    @Override
    public ResponseEntity<Void> delete(@PathVariable String cboId) {
        crudCbo.delete(cboId);
        return ResponseEntity.noContent().build();
    }
}
