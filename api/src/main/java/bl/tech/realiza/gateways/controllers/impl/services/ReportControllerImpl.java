package bl.tech.realiza.gateways.controllers.impl.services;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.controllers.interfaces.services.ReportController;
import bl.tech.realiza.services.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
@Tag(name = "Report")
public class ReportControllerImpl implements ReportController {

    private final ReportService reportService;

    @GetMapping("/suppliers-by-branch")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Long> countSupplierByBranch(@RequestParam String branchId) {
        return ResponseEntity.ok(reportService.countSuppliersByBranch(branchId));
    }

    @GetMapping("/subcontractor-by-branch")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Long> countSubcontractorByBranch(@RequestParam String branchId) {
        return ResponseEntity.ok(reportService.countSubcontractorsByBranch(branchId));
    }

    @GetMapping("/subcontractor-by-supplier")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Long> countSubcontractorBySupplier(@RequestParam String supplierId) {
        return ResponseEntity.ok(reportService.countSubcontractorsBySupplier(supplierId));
    }

    @GetMapping("/employee-by-enterprise")
    @ResponseStatus(HttpStatus.OK)
    @Override
    public ResponseEntity<Long> countEmployeeByEnterprise(@RequestParam String idEnterprise, @RequestParam Provider.Company companyDegree) {
        return ResponseEntity.ok(reportService.countEmployeesByEnterprise(idEnterprise, companyDegree));
    }

    @GetMapping("/adherece/{idEnterprise}")
    @Override
    public ResponseEntity<Long> countAdherenceByEnterprise(String idEnterprise, Provider.Company companyDegree) {
        return null;
    }

    @GetMapping("/accordance/{idEnterprise}")
    @Override
    public ResponseEntity<Long> countAccordanceByEnterprise(String idEnterprise, Provider.Company companyDegree) {
        return null;
    }
}
