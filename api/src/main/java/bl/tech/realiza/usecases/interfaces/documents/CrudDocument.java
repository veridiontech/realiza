package bl.tech.realiza.usecases.interfaces.documents;

import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocument {
    DocumentResponseDto saveBranch(DocumentBranchRequestDto documentBranchRequestDto);
    DocumentResponseDto saveClient(DocumentClientRequestDto documentClientRequestDto);
    DocumentResponseDto saveEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto);
    DocumentResponseDto saveSubcontract(DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto);
    DocumentResponseDto saveSupplier(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto);
    Optional<DocumentResponseDto> findOne(String id);
    Page<DocumentResponseDto> findAll(Pageable pageable);
    Optional<DocumentResponseDto> updateBranch(DocumentBranchRequestDto documentBranchRequestDto);
    Optional<DocumentResponseDto> updateClient(DocumentClientRequestDto documentClientRequestDto);
    Optional<DocumentResponseDto> updateEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto);
    Optional<DocumentResponseDto> updateSubcontract(DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto);
    Optional<DocumentResponseDto> updateSupplier(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto);
    void delete(String id);
}
