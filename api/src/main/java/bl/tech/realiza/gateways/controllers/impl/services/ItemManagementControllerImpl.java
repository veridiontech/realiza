package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.gateways.controllers.interfaces.services.ItemManagementController;
import bl.tech.realiza.gateways.requests.services.ItemManagementRequestDto;
import bl.tech.realiza.gateways.responses.services.ItemManagementResponseDto;
import bl.tech.realiza.usecases.impl.CrudItemManagementImpl;
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
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item-management")
@Tag(name = "Item Management")
public class ItemManagementControllerImpl implements ItemManagementController {

    private final CrudItemManagementImpl crudItemManagementImpl;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma solicitação para adicionar um usuário")
    @Override
    public ResponseEntity<ItemManagementResponseDto> createSolicitations(@RequestBody @Valid ItemManagementRequestDto itemManagementRequestDto) {
        ItemManagementResponseDto itemManagementResponseDto = crudItemManagementImpl.saveUserSolicitation(itemManagementRequestDto);

        return ResponseEntity.ok(itemManagementResponseDto);
    }

    @GetMapping("/new")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca todas as solicitações para adicionar usuários")
    @Override
    public ResponseEntity<Page<ItemManagementResponseDto>> getSolicitations(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(defaultValue = "idSolicitation") String sort,
                                                                            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ItemManagementResponseDto> itemManagementResponse = crudItemManagementImpl.findAllUserSolicitation(pageable);

        return ResponseEntity.ok(itemManagementResponse);
    }

    @DeleteMapping("/new/{idSolicitation}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Apaga uma solicitação para adicionar usuário")
    @Override
    public ResponseEntity<Void> deleteSolicitation(@PathVariable String idSolicitation) {
        crudItemManagementImpl.deleteUserSolicitation(idSolicitation);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/new/{idSolicitation}/approve")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Aprova uma solicitação para adicionar usuário")
    @Override
    public ResponseEntity<String> approveSolicitation(@PathVariable String idSolicitation) {
        String response = crudItemManagementImpl.approveUserSolicitation(idSolicitation);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/new/{idSolicitation}/deny")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Nega uma solicitação para adicionar usuário")
    @Override
    public ResponseEntity<String> denySolicitation(@PathVariable String idSolicitation) {
        String response = crudItemManagementImpl.denyUserSolicitation(idSolicitation);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/delete-requests")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Collection<Object>> getDeleteRequests() {
        Collection<Object> items = crudItemManagementImpl.getDeleteItemRequest();

        return ResponseEntity.of(Optional.ofNullable(items));
    }

    @PatchMapping("/approve-employee-document")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> approveDocumentEmployee(@RequestParam String idDocument) {
        String response = crudItemManagementImpl.approveNewDocumentEmployee(idDocument);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee-document-request")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Collection<DocumentEmployee>> getApproveRequests() {
        Collection<DocumentEmployee> documentEmployees = crudItemManagementImpl.getAddRequestDocumentEmployees();
        return ResponseEntity.ok(documentEmployees);
    }
}
