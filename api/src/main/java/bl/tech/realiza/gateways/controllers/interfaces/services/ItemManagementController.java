package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.services.ItemManagementService;
import org.springframework.http.ResponseEntity;

public interface ItemManagementController {
    ResponseEntity<String> activateItem(String id, ItemManagementService.ActivationItemType item);
    ResponseEntity<Void> deleteItem(String id, ItemManagementService.DeleteItemType item);
}
