package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.gateways.responses.clients.BranchResponseDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrudEmployee {
    Page<EmployeeResponseDto> findAllByEnterprise(String idSearch, Provider.Company company, Pageable pageable);
    EmployeeResponseDto findAllSelectedDocuments (String id);
    // tem que dar update nos docs, imagino um put completo com o objeto inteiro
    String updateDocumentRequests(String id, List<String> documentCollection);
}
