package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class CrudEmployeeImpl implements CrudEmployee {
    @Override
    public EmployeeResponseDto saveBrazilian(EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        return null;
    }

    @Override
    public EmployeeResponseDto saveForeigner(EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        return null;
    }

    @Override
    public Optional<EmployeeResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<EmployeeResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<EmployeeResponseDto> updateBrazilian(EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        return Optional.empty();
    }

    @Override
    public Optional<EmployeeResponseDto> updateForeigner(EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
