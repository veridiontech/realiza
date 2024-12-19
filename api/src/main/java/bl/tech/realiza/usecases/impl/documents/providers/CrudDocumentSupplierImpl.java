package bl.tech.realiza.usecases.impl.documents.providers;

import bl.tech.realiza.gateways.requests.documents.providers.DocumentSupplierRequestDto;
import bl.tech.realiza.gateways.responses.documents.providers.DocumentSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.providers.CrudDocumentSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudDocumentSupplierImpl implements CrudDocumentSupplier {
    @Override
    public DocumentSupplierResponseDto save(DocumentSupplierRequestDto documentSupplierRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentSupplierResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentSupplierResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentSupplierResponseDto> update(DocumentSupplierRequestDto documentSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
