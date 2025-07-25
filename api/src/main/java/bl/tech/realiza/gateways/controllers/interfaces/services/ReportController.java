package bl.tech.realiza.gateways.controllers.interfaces.services;

import bl.tech.realiza.domains.providers.Provider;
import org.springframework.http.ResponseEntity;

public interface ReportController {
    ResponseEntity<Long> countSupplierByBranch(String branchId);
    ResponseEntity<Long> countSubcontractorByBranch(String branchId);
    ResponseEntity<Long> countSubcontractorBySupplier(String supplierId);
    ResponseEntity<Long> countEmployeeByEnterprise(String idEnterprise, Provider.Company companyDegree);
}
