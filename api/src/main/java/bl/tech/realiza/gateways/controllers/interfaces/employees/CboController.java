package bl.tech.realiza.gateways.controllers.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.CboRequestDto;
import bl.tech.realiza.gateways.responses.employees.CboResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CboController {
    ResponseEntity<CboResponseDto> save(CboRequestDto cboRequestDto);
    ResponseEntity<CboResponseDto> update(String cboId, CboRequestDto cboRequestDto);
    ResponseEntity<CboResponseDto> findOne(String cboId);
    ResponseEntity<List<CboResponseDto>> findAll();
    ResponseEntity<Void> delete(String cboId);
}
