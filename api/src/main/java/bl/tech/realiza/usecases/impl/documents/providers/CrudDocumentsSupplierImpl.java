package bl.tech.realiza.usecases.impl.documents.providers;

import bl.tech.realiza.gateways.requests.documents.providers.DocumentsSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentsSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.providers.CrudDocumentsSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudDocumentsSupplierImpl implements CrudDocumentsSupplier {
    @Override
    public DocumentsSupplierResponseDto save(DocumentsSupplierRequestDto documentsSupplierRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentsSupplierResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentsSupplierResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentsSupplierResponseDto> update(String id, DocumentsSupplierRequestDto documentsSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
