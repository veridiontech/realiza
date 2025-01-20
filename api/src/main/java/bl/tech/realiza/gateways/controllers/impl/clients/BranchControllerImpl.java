package bl.tech.realiza.gateways.controllers.impl.clients;

import bl.tech.realiza.gateways.controllers.interfaces.clients.BranchControlller;
import bl.tech.realiza.gateways.requests.clients.BranchRequestDto;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import bl.tech.realiza.usecases.impl.clients.CrudBranchImpl;
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

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/branch")
@Tag(name = "Branch")
public class BranchControllerImpl implements BranchControlller {

    private final CrudBranchImpl crudBranch;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<BranchResponseDto> createBranch(@RequestBody @Valid BranchRequestDto branchRequestDto) {
        BranchResponseDto branch = crudBranch.save(branchRequestDto);

        return ResponseEntity.of(Optional.of(branch));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<BranchResponseDto>> getOneBranch(@PathVariable String id) {
        Optional<BranchResponseDto> branch = crudBranch.findOne(id);

        return ResponseEntity.of(Optional.of(branch));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<BranchResponseDto>> getAllBranches(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "5") int size,
                                                                  @RequestParam(defaultValue = "idBranch") String sort,
                                                                  @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<BranchResponseDto> pageBranch = crudBranch.findAll(pageable);

        return ResponseEntity.ok(pageBranch);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<BranchResponseDto>> updateBranch(@RequestBody @Valid BranchRequestDto branchRequestDto) {
        Optional<BranchResponseDto> branch = crudBranch.update(branchRequestDto);

        return ResponseEntity.of(Optional.of(branch));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteBranch(@PathVariable String id) {
        crudBranch.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtered-client")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<BranchResponseDto>> getAllBranchesByClient(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "5") int size,
                                                                          @RequestParam(defaultValue = "idBranch") String sort,
                                                                          @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                          @RequestParam String idSearch) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<BranchResponseDto> pageBranch = crudBranch.findAllByClient(idSearch, pageable);

        return ResponseEntity.ok(pageBranch);
    }
}
