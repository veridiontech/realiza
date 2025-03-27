package bl.tech.realiza.gateways.controllers.impl.ultragaz;

import bl.tech.realiza.gateways.controllers.interfaces.ultragaz.MarketController;
import bl.tech.realiza.gateways.requests.ultragaz.MarketRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.MarketResponseDto;
import bl.tech.realiza.usecases.interfaces.ultragaz.CrudMarket;
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
@RequestMapping("/ultragaz/market")
@Tag(name = "Market / Mercado / 3º nível")
public class MarketControllerImpl implements MarketController {

    private final CrudMarket crudMarket;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<MarketResponseDto> createMarket(@RequestBody @Valid MarketRequestDto marketRequestDto) {
        return ResponseEntity.ok(crudMarket.save(marketRequestDto));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<MarketResponseDto> getOneMarket(@PathVariable String id) {
        return ResponseEntity.ok(crudMarket.findOne(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<MarketResponseDto>> getAllMarkets(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size,
                                                                 @RequestParam(defaultValue = "idMarket") String sort,
                                                                 @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));
        return ResponseEntity.ok(crudMarket.findAll(pageable));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<MarketResponseDto> updateMarket(@PathVariable String id, @RequestBody @Valid MarketRequestDto marketRequestDto) {
        return ResponseEntity.ok(crudMarket.update(id, marketRequestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteMarket(@PathVariable String id) {
        crudMarket.delete(id);
        return ResponseEntity.ok().build();
    }
}
