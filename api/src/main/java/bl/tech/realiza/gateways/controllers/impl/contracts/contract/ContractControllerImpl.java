package bl.tech.realiza.gateways.controllers.impl.contracts.contract;

import bl.tech.realiza.gateways.controllers.interfaces.contracts.contract.ContractController;
import bl.tech.realiza.gateways.requests.contracts.EmployeeToContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractByBranchIdsResponseDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractByEmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContract;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract")
@Tag(name = "Contract")
public class ContractControllerImpl implements ContractController {

    private final CrudContract crudContractImpl;

    @PostMapping("/finish/{idContract}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_CLIENT_MANAGER')")
    @Override
    public ResponseEntity<String> finishContract(@PathVariable String idContract) {
        return ResponseEntity.ok(crudContractImpl.finishContract(idContract));
    }

    @PostMapping("/suspend/{contractId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_CLIENT_MANAGER')")
    public ResponseEntity<String> suspendContract(@PathVariable String contractId) {
        return ResponseEntity.ok(crudContractImpl.suspendContract(contractId));
    }

    @PostMapping("/add-employee/{idContract}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_CLIENT_MANAGER')")
    @Override
    public ResponseEntity<String> addEmployeeToContract(@PathVariable String idContract, @RequestBody EmployeeToContractRequestDto employeeToContractRequestDto) {
        return ResponseEntity.ok(crudContractImpl.addEmployeeToContract(idContract, employeeToContractRequestDto));
    }

    @PostMapping("/remove-employee/{idContract}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_CLIENT_MANAGER')")
    @Override
    public ResponseEntity<String> removeEmployeeFromContract(@PathVariable String idContract, @RequestBody EmployeeToContractRequestDto employeeToContractRequestDto) {
        return ResponseEntity.ok(crudContractImpl.removeEmployeeToContract(idContract, employeeToContractRequestDto));
    }

    @GetMapping("/find-by-employee/{idEmployee}")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Page<ContractByEmployeeResponseDto>> getContractsByEmployee(@RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "5") int size,
                                                                                      @RequestParam(defaultValue = "contractReference") String sort,
                                                                                      @RequestParam(defaultValue = "ASC") Sort.Direction direction,
                                                                                      @PathVariable String idEmployee) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sort));
        return ResponseEntity.ok(crudContractImpl.getContractByEmployee(pageable,idEmployee));
    }

    @GetMapping("/find-by-branchIds")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<List<ContractByBranchIdsResponseDto>> getContractsByBranchIds(@RequestBody List<String> branchIds) {
        return ResponseEntity.ok(crudContractImpl.getContractByBranchIds(branchIds));
    }

}
