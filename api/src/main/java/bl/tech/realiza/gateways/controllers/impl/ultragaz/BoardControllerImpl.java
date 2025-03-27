package bl.tech.realiza.gateways.controllers.impl.ultragaz;

import bl.tech.realiza.gateways.controllers.interfaces.ultragaz.BoardController;
import bl.tech.realiza.gateways.requests.ultragaz.BoardRequestDto;
import bl.tech.realiza.gateways.responses.ultragaz.BoardResponseDto;
import bl.tech.realiza.usecases.interfaces.ultragaz.CrudBoard;
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
@RequestMapping("/ultragaz/board")
@Tag(name = "Board / Diretoria / 1º nível")
public class BoardControllerImpl implements BoardController {

    private final CrudBoard crudBoard;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody @Valid BoardRequestDto boardRequestDto) {
        return ResponseEntity.ok(crudBoard.save(boardRequestDto));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<BoardResponseDto> getOneBoard(@PathVariable String id) {
        return ResponseEntity.ok(crudBoard.findOne(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<BoardResponseDto>> getAllBoards(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "5") int size,
                                                               @RequestParam(defaultValue = "idBoard") String sort,
                                                               @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));
        return ResponseEntity.ok(crudBoard.findAll(pageable));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable String id,  @RequestBody @Valid BoardRequestDto boardRequestDto) {
        return ResponseEntity.ok(crudBoard.update(id, boardRequestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteBoard(@PathVariable String id) {
        crudBoard.delete(id);
        return ResponseEntity.ok().build();
    }
}
