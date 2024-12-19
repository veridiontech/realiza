package bl.tech.realiza.usecases.impl.employees;

import bl.tech.realiza.gateways.repositories.employees.EmployeeForeignerRepository;
import bl.tech.realiza.gateways.requests.employees.EmployeeForeignerRequestDto;
import bl.tech.realiza.gateways.responses.employees.EmployeeForeignerResponseDto;
import bl.tech.realiza.usecases.interfaces.employees.CrudEmployeeForeigner;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudEmployeeForeignerImpl implements CrudEmployeeForeigner {

    private final EmployeeForeignerRepository employeeForeignerRepository;

    @Override
    public EmployeeForeignerResponseDto save(EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        return null;
    }

    @Override
    public Optional<EmployeeForeignerResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<EmployeeForeignerResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<EmployeeForeignerResponseDto> update(EmployeeForeignerRequestDto employeeForeignerRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
