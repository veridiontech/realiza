package bl.tech.realiza.gateways.controllers.impl.documents.contract;

import bl.tech.realiza.gateways.controllers.interfaces.documents.contract.DocumentContractController;
import bl.tech.realiza.gateways.responses.documents.ContractDocumentAndEmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.contract.CrudDocumentContract;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/document/contract")
@Tag(name = "Document Contract")
public class DocumentContractControllerImpl implements DocumentContractController {

    private final CrudDocumentContract crudDocumentContract;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<ContractDocumentAndEmployeeResponseDto> getDocumentsByContract(@PathVariable String id) {
        return ResponseEntity.ok(crudDocumentContract.getDocumentAndEmployeeByContractId(id));
    }
}
