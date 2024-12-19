package bl.tech.realiza.usecases.impl.documents.employee;

import bl.tech.realiza.gateways.requests.documents.employee.DocumentEmployeeRequestDto;
import bl.tech.realiza.gateways.responses.documents.employee.DocumentEmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.documents.employee.CrudDocumentEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudDocumentEmployeeImpl implements CrudDocumentEmployee {
    @Override
    public DocumentEmployeeResponseDto save(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        return null;
    }

    @Override
    public Optional<DocumentEmployeeResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<DocumentEmployeeResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<DocumentEmployeeResponseDto> update(DocumentEmployeeRequestDto documentEmployeeRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
