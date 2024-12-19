package bl.tech.realiza.usecases.interfaces.documents.providers;

import bl.tech.realiza.gateways.requests.documents.providers.DocumentSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSupplierResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CrudDocumentSupplier {
    DocumentSupplierResponseDto save(DocumentSupplierRequestDto documentSupplierRequestDto);
    Optional<DocumentSupplierResponseDto> findOne(String id);
    Page<DocumentSupplierResponseDto> findAll(Pageable pageable);
    Optional<DocumentSupplierResponseDto> update(DocumentSupplierRequestDto documentSupplierRequestDto);
    void delete(String id);
}
