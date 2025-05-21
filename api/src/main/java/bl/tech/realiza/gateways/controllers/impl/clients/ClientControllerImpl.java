package bl.tech.realiza.gateways.controllers.impl.clients;

import bl.tech.realiza.gateways.controllers.interfaces.clients.ClientControlller;
import bl.tech.realiza.gateways.requests.clients.client.ClientRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import bl.tech.realiza.services.auth.TokenManagerService;
import bl.tech.realiza.usecases.impl.clients.CrudClientImpl;
import bl.tech.realiza.usecases.interfaces.clients.CrudClient;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/client")
@Tag(name = "Client")
public class ClientControllerImpl implements ClientControlller {

    private final CrudClient crudClient;
    private final TokenManagerService tokenManagerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ResponseEntity<ClientResponseDto> createClient(@RequestBody @Valid ClientRequestDto clientRequestDto) {
        ClientResponseDto client = crudClient.save(clientRequestDto);

        return ResponseEntity.of(Optional.of(client));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ClientResponseDto>> getOneClient(@PathVariable String id) {
        Optional<ClientResponseDto> client = crudClient.findOne(id);

        return ResponseEntity.of(Optional.of(client));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ClientResponseDto>> getAllClients(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size,
                                                                 @RequestParam(defaultValue = "tradeName") String sort,
                                                                 @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ClientResponseDto> pageClient = crudClient.findAll(pageable);

        return ResponseEntity.ok(pageClient);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ClientResponseDto>> updateClient(@PathVariable String id, @RequestBody @Valid ClientRequestDto clientRequestDto) {
        Optional<ClientResponseDto> client = crudClient.update(id, clientRequestDto);

        return ResponseEntity.of(Optional.of(client));
    }

    @PatchMapping("/change-logo/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> updateLogo(@PathVariable String id, @RequestPart(value = "file") MultipartFile file) {
        String userClient = null;
        try {
            userClient = crudClient.changeLogo(id, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(userClient);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteClient(@PathVariable String id) {
        crudClient.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/find-by-branch/{idBranch}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Optional<ClientResponseDto>> getClientByBranch(@PathVariable String idBranch) {
        Optional<ClientResponseDto> client = crudClient.findClientbyBranch(idBranch);

        return ResponseEntity.of(Optional.of(client));
    }
}
