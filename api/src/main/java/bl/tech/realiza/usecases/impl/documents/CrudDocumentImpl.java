package bl.tech.realiza.usecases.impl.documents;

import bl.tech.realiza.gateways.requests.documents.client.DocumentBranchRequestDto;
import bl.tech.realiza.gateways.requests.documents.client.DocumentClientRequestDto;
import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.requests.documents.provider.DocumentProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.CrudDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class CrudDocumentImpl implements CrudDocument {
    @Override
    public DocumentResponseDto saveBranch(DocumentBranchRequestDto documentBranchRequestDto) {
        return null;
    }

    @Override
    public DocumentResponseDto saveClient(DocumentClientRequestDto documentClientRequestDto) {
        return null;
    }

    @Override
    public DocumentResponseDto saveEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        return null;
    }

    @Override
    public DocumentResponseDto saveSubcontract(DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto) {
        return null;
    }

    @Override
    public DocumentResponseDto saveSupplier(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentResponseDto> updateBranch(DocumentBranchRequestDto documentBranchRequestDto) {
        return Optional.empty();
    }

    @Override
    public Optional<DocumentResponseDto> updateClient(DocumentClientRequestDto documentClientRequestDto) {
        return Optional.empty();
    }

    @Override
    public Optional<DocumentResponseDto> updateEmployee(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        return Optional.empty();
    }

    @Override
    public Optional<DocumentResponseDto> updateSubcontract(DocumentProviderSubcontractorRequestDto documentProviderSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public Optional<DocumentResponseDto> updateSupplier(DocumentProviderSupplierRequestDto documentProviderSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
