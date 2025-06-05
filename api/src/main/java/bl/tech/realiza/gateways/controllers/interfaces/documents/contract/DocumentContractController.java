package bl.tech.realiza.gateways.controllers.interfaces.documents.contract;

import bl.tech.realiza.gateways.requests.documents.contract.DocumentContractRequestDto;
import bl.tech.realiza.gateways.responses.documents.ContractDocumentAndEmployeeResponseDto;
import bl.tech.realiza.gateways.responses.documents.DocumentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocumentContractController {
    ResponseEntity<ContractDocumentAndEmployeeResponseDto> getDocumentsByContract(String contractId);
}
