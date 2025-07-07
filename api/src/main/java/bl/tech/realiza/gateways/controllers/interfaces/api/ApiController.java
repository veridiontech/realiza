package bl.tech.realiza.gateways.controllers.interfaces.api;

import bl.tech.realiza.gateways.responses.api.document.employee.DocumentEmployeeExternalApiResponseDto;
import org.springframework.http.ResponseEntity;

public interface ApiController {
    ResponseEntity<String> createToken(String enterpriseName, String enterpriseCnpj, String password);
    ResponseEntity<Boolean> checkEmployeeStatus(String cpf);
    ResponseEntity<DocumentEmployeeExternalApiResponseDto> checkEmployeeNotApprovedDocuments(String cpf);
}
