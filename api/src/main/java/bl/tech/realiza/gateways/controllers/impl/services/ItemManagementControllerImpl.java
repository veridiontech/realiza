package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.controllers.interfaces.services.ItemManagementController;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementUserRequestDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.document.ItemManagementDocumentDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.document.ItemManagementDocumentResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.provider.ItemManagementProviderResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserDetailsResponseDto;
import bl.tech.realiza.gateways.responses.services.itemManagement.user.ItemManagementUserResponseDto;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item-management")
@Tag(name = "Item Management")
public class ItemManagementControllerImpl implements ItemManagementController {

    private final CrudItemManagement crudItemManagement;

    @PostMapping("/new-user")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma solicitação para adicionar um usuário")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<ItemManagementUserResponseDto> createUserSolicitation(@RequestBody @Valid ItemManagementUserRequestDto itemManagementUserRequestDto) {
        ItemManagementUserResponseDto itemManagementUserResponseDto = crudItemManagement.saveUserSolicitation(itemManagementUserRequestDto);

        return ResponseEntity.ok(itemManagementUserResponseDto);
    }

    @PostMapping("/new-provider")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma solicitação para adicionar uma empresa")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<ItemManagementProviderResponseDto> createProviderSolicitation(@RequestBody @Valid ItemManagementProviderRequestDto itemManagementProviderRequestDto) {
        ItemManagementProviderResponseDto itemManagementProviderResponseDto = crudItemManagement.saveProviderSolicitation(itemManagementProviderRequestDto);

        return ResponseEntity.ok(itemManagementProviderResponseDto);
    }

    @GetMapping("/new-user")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca todas as solicitações para adicionar usuários")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<Page<ItemManagementUserResponseDto>> getUserSolicitations(@RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                @RequestParam(defaultValue = "creationDate") String sort,
                                                                                @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ItemManagementUserResponseDto> itemManagementResponse = crudItemManagement.findAllUserSolicitation(pageable);

        return ResponseEntity.ok(itemManagementResponse);
    }

    @GetMapping("/new-user/details/{idSolicitation}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca detalhes da solicitação de adicionar usuário")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<ItemManagementUserDetailsResponseDto> getUserSolicitationDetails(@PathVariable String idSolicitation) {
        return ResponseEntity.ok(crudItemManagement.findUserSolicitationDetails(idSolicitation));
    }

    @GetMapping("/new-provider")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca todas as solicitações para adicionar cnpj")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<Page<ItemManagementProviderResponseDto>> getProviderSolicitations(@RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size,
                                                                                    @RequestParam(defaultValue = "creationDate") String sort,
                                                                                    @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        return ResponseEntity.ok(crudItemManagement.findAllProviderSolicitation(pageable));
    }

    @GetMapping("/new-provider/details/{idSolicitation}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca detalhes da solicitação de adicionar usuário")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<ItemManagementProviderDetailsResponseDto> getProviderSolicitationDetails(@PathVariable String idSolicitation) {
        return ResponseEntity.ok(crudItemManagement.findProviderSolicitationDetails(idSolicitation));
    }

    @GetMapping("/document")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca todas as solicitações para isentar documento")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<Page<ItemManagementDocumentResponseDto>> getDocumentSolicitations(@RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size,
                                                                                            @RequestParam(defaultValue = "creationDate") String sort,
                                                                                            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        return ResponseEntity.ok(crudItemManagement.findAllDocumentSolicitation(pageable));
    }

    @GetMapping("/document/details/{idSolicitation}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca detalhes da solicitação de isentar documento")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<ItemManagementDocumentDetailsResponseDto> getDocumentSolicitationDetails(@PathVariable String idSolicitation) {
        return ResponseEntity.ok(crudItemManagement.findDocumentSolicitationDetails(idSolicitation));
    }

    @PatchMapping("/{idSolicitation}/approve")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Aprova uma solicitação")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<String> approveSolicitation(@PathVariable String idSolicitation) {
        return ResponseEntity.ok(crudItemManagement.approveSolicitation(idSolicitation));
    }

    @PatchMapping("/{idSolicitation}/deny")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Nega uma solicitação")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<String> denySolicitation(@PathVariable String idSolicitation) {
        return ResponseEntity.ok(crudItemManagement.denySolicitation(idSolicitation));
    }

    @DeleteMapping("/{idSolicitation}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Deleta uma solicitação")
    @PreAuthorize("hasRole('ROLE_REALIZA_BASIC')")
    @Override
    public ResponseEntity<Void> deleteSolicitation(@PathVariable String idSolicitation) {
        crudItemManagement.deleteSolicitation(idSolicitation);
        return ResponseEntity.noContent().build();
    }
}