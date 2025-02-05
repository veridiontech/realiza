package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.gateways.controllers.interfaces.services.ItemManagementController;
import bl.tech.realiza.services.ItemManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item-management")
@Tag(name = "Activate or Delete item")
public class ItemManagementControllerImpl implements ItemManagementController {

    private final ItemManagementService itemManagementService;

    // apenas REALIZA_BASIC+ pode acessar
    @PatchMapping("/activate")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<String> activateItem(@RequestParam String id, @RequestParam ItemManagementService.ActivationItemType item) {

        String response = itemManagementService.activateItem(id,item);

        return ResponseEntity.ok(response);
    }

    // apenas REALIZA_PLUS+ pode acessar
    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public ResponseEntity<Void> deleteItem(@RequestParam String id, @RequestParam ItemManagementService.DeleteItemType item) {

        itemManagementService.deleteItem(id,item);

        return ResponseEntity.noContent().build();
    }
}
