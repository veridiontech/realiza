package bl.tech.realiza.usecases.impl.documents.employee;

import bl.tech.realiza.gateways.requests.documents.employee.DocumentsEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentsEmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.employee.CrudDocumentsEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudDocumentsEmployeeImpl implements CrudDocumentsEmployee {
    @Override
    public DocumentsEmployeeResponseDto save(DocumentsEmployeeRequestDto documentsEmployeeRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentsEmployeeResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentsEmployeeResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentsEmployeeResponseDto> update(DocumentsEmployeeRequestDto documentsEmployeeRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
