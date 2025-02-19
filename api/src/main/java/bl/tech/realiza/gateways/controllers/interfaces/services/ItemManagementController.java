package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.gateways.responses.services.ItemManagementResponseDto;
import bl.tech.realiza.usecases.impl.CrudItemManagementImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

public interface ItemManagementController {
    ResponseEntity<String> activateItem(String id, CrudItemManagementImpl.ActivationItemType item);
    ResponseEntity<Void> deleteItem(String id, CrudItemManagementImpl.DeleteItemType item);
    ResponseEntity<Page<ItemManagementResponseDto>> getInnactiveItems(int page, int size, String sort, Sort.Direction direction);
    ResponseEntity<Collection<Object>> getDeleteRequests();
    ResponseEntity<String> approveDocumentEmployee(String id);
    ResponseEntity<Collection<DocumentEmployee>> getApproveRequests();
}
