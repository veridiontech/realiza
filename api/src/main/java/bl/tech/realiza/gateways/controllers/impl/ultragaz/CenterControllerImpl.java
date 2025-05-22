package bl.tech.realiza.gateways.controllers.impl.ultragaz;

import bl.tech.realiza.gateways.controllers.interfaces.ultragaz.CenterController;
import bl.tech.realiza.gateways.requests.ultragaz.CenterRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.CenterResponseDto;
import bl.tech.realiza.usecases.interfaces.ultragaz.CrudCenter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ultragaz/center")
@Tag(name = "Center / Núcleo / 2º nível")
public class CenterControllerImpl implements CenterController {
    
    private final CrudCenter crudCenter;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<CenterResponseDto> createCenter(@RequestBody @Valid CenterRequestDto centerRequestDto) {
        return ResponseEntity.ok(crudCenter.save(centerRequestDto));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<CenterResponseDto> getOneCenter(@PathVariable String id) {
        return ResponseEntity.ok(crudCenter.findOne(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<CenterResponseDto>> getAllCenters(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size,
                                                                 @RequestParam(defaultValue = "name") String sort,
                                                                 @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));
        return ResponseEntity.ok(crudCenter.findAll(pageable));
    }

    @GetMapping("/find-by-market")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<CenterResponseDto>> getAllCentersByMarket(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "200") int size,
                                                                         @RequestParam(defaultValue = "name") String sort,
                                                                         @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                         @RequestParam String idMarket) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));
        return ResponseEntity.ok(crudCenter.findAllByMarket(idMarket, pageable));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<CenterResponseDto> updateCenter(@PathVariable String id, @RequestBody @Valid CenterRequestDto centerRequestDto) {
        return ResponseEntity.ok(crudCenter.update(id, centerRequestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteCenter(@PathVariable String id) {
        crudCenter.delete(id);
        return ResponseEntity.ok().build();
    }
}
