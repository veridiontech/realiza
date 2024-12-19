package bl.tech.realiza.usecases.interfaces.documents.providers;

import bl.tech.realiza.gateways.requests.documents.providers.DocumentsSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentsSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentsSupplier {
    DocumentsSupplierResponseDto save(DocumentsSupplierRequestDto documentsSupplierRequestDto);
    Optional<DocumentsSupplierResponseDto> findOne(String id);
    Page<DocumentsSupplierResponseDto> findAll(Pageable pageable);
    Optional<DocumentsSupplierResponseDto> update(DocumentsSupplierRequestDto documentsSupplierRequestDto);
    void delete(String id);
}
