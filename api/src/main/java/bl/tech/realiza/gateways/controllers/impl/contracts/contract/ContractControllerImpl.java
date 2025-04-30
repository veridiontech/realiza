package bl.tech.realiza.gateways.controllers.impl.contracts.contract;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.contract.ContractController;
import bl.tech.realiza.usecases.impl.contracts.contract.CrudContractImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract")
@Tag(name = "Contract")
public class ContractControllerImpl implements ContractController {

    private final CrudContractImpl crudContractImpl;

    @PostMapping("/finish/{idContract}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_CLIENT_MANAGER')")
    @Override
    public ResponseEntity<String> finishContract(@PathVariable String idContract) {
        return ResponseEntity.ok(crudContractImpl.finishContract(idContract));
    }
}
