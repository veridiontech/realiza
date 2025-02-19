package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.gateways.controllers.interfaces.services.ItemManagementController;
import bl.tech.realiza.gateways.responses.services.ItemManagementResponseDto;
import bl.tech.realiza.usecases.impl.CrudItemManagementImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Activate or Delete item")
public class ItemManagementControllerImpl implements ItemManagementController {

    private final CrudItemManagementImpl crudItemManagementImpl;

    // apenas REALIZA_BASIC+ pode acessar
    @PatchMapping("/activate")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> activateItem(@RequestParam String id, @RequestParam CrudItemManagementImpl.ActivationItemType item) {

        String response = crudItemManagementImpl.activateItem(id,item);

        return ResponseEntity.ok(response);
    }

    // apenas REALIZA_PLUS+ pode acessar
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteItem(@RequestParam String id, @RequestParam CrudItemManagementImpl.DeleteItemType item) {

        crudItemManagementImpl.deleteItem(id,item);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/innactive-items")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ItemManagementResponseDto>> getInnactiveItems(@RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "10") int size,
                                                                                   @RequestParam(defaultValue = "idUpdateDataRequest") String sort,
                                                                                   @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));

        Page<ItemManagementResponseDto> items = crudItemManagementImpl.findAllAddSolicitations(pageable);

        return ResponseEntity.ok(items);
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
