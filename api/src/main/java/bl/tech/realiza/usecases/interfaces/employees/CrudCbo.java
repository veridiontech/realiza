package bl.tech.realiza.usecases.interfaces.employees;

import bl.tech.realiza.gateways.requests.employees.CboRequestDto;
import bl.tech.realiza.gateways.responses.employees.CboResponseDto;

import java.util.List;

public interface CrudCbo {
    CboResponseDto save(CboRequestDto cboRequestDto);
    CboResponseDto findOne(String id);
    CboResponseDto update(String id, CboRequestDto cboRequestDto);
    List<CboResponseDto> findAll();
    void delete(String id);
}
