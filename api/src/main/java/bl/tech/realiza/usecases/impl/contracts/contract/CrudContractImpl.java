package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CrudContractImpl implements CrudContract {
    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public String finishContract(String idContract) {
        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        contract.setFinished(true);

        contractRepository.save(contract);

        return "Contract finished successfully";
    }

    @Override
    public String addEmployeeToContract(String idContract, String idEmployee) {
        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));
        Employee employee = employeeRepository.findById(idEmployee)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            if (!Objects.equals(contractProviderSupplier.getProviderSupplier().getIdProvider(),
                    employee.getSupplier().getIdProvider())) {
                throw new IllegalArgumentException("Contract provider does not match employee provider");
            }

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            if (!Objects.equals(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider(),
                    employee.getSubcontract().getIdProvider())) {
                throw new IllegalArgumentException("Contract provider does not match employee provider");
            }
        } else {
            throw new NotFoundException("Invalid contract type");
        }

        contract.getEmployees().add(employee);
        contractRepository.save(contract);

        return "Employee added successfully";
    }

    @Override
    public String removeEmployeeToContract(String idContract, String idEmployee) {
        Contract contract = contractRepository.findById(idContract)
                .orElseThrow(() -> new NotFoundException("Contract not found"));
        Employee employee = employeeRepository.findById(idEmployee)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        if (contract instanceof ContractProviderSupplier contractProviderSupplier) {
            if (!Objects.equals(contractProviderSupplier.getProviderSupplier().getIdProvider(),
                    employee.getSupplier().getIdProvider())) {
                throw new IllegalArgumentException("Contract provider does not match employee provider");
            }

        } else if (contract instanceof ContractProviderSubcontractor contractProviderSubcontractor) {
            if (!Objects.equals(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider(),
                    employee.getSubcontract().getIdProvider())) {
                throw new IllegalArgumentException("Contract provider does not match employee provider");
            }
        } else {
            throw new NotFoundException("Invalid contract type");
        }

        contract.getEmployees().remove(employee);
        contractRepository.save(contract);

        return "Employee added successfully";
    }
}
