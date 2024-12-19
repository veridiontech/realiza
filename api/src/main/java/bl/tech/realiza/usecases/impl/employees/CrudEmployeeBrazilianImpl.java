package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.gateways.requests.employees.EmployeeBrazilianRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeBrazilianResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployeeBrazilian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrudEmployeeBrazilianImpl implements CrudEmployeeBrazilian {
    @Override
    public EmployeeBrazilianResponseDto save(EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        return null;
    }

    @Override
    public Optional<EmployeeBrazilianResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<EmployeeBrazilianResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<EmployeeBrazilianResponseDto> update(EmployeeBrazilianRequestDto employeeBrazilianRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
