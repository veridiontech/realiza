package bl.tech.realiza.services.thirdParty;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.exceptions.ForbiddenException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.requests.services.LoginRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.AuthService;
import bl.tech.realiza.services.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ThirdPartyService {
    private final AuthService authService;
    private final JwtService jwtService;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private static final Pattern CPF_PATTERN = Pattern.compile("^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$");

    public Boolean employeeStatus(String employeeCpf, LoginRequestDto request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        String clientId = getClientIdByToken(token);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        if (!client.getThirdParty()) {
            throw new ForbiddenException("Client didn't join third party!");
        }
        String unformattedCpf = employeeCpf.replaceAll("[.\\-]", "");
        String formattedCpf = formatCpfForSearch(unformattedCpf);
        List<Employee> formattedEmployees = employeeRepository.findAllByCpf(formattedCpf);
        List<Employee> unformattedEmployees = employeeRepository.findAllByCpf(unformattedCpf);
        Set<Employee> uniqueEmployeesSet = new HashSet<>(formattedEmployees);
        uniqueEmployeesSet.addAll(unformattedEmployees);

        List<Employee> employees = new ArrayList<>(uniqueEmployeesSet);

        employees.removeIf(employee -> !(employeeFromSupplierBelongToClient(client, employee)
                || employeeFromSubcontractorBelongToClient(client, employee)));

        return employees.stream().allMatch(employee -> employee.getDocumentEmployees().stream().allMatch(Document::getConforming));
    }

    private String formatCpfForSearch(String employeeCpf) {
        if (employeeCpf == null || !CPF_PATTERN.matcher(employeeCpf).matches()) {
            throw new IllegalArgumentException("Invalid CPF format.");
        }

        if (employeeCpf.length() == 11) {
            return employeeCpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }

        return employeeCpf;
    }

    private String getClientIdByToken(String token) {
        return jwtService.extractAllClaims(token).getIdClient();
    }

    private Boolean employeeFromSupplierBelongToClient(Client client, Employee employee) {
        if (employee.getSupplier() == null) {
            return false;
        } else {
            List<ContractProviderSupplier> contracts = employee.getSupplier().getContractsSupplier();
            return contracts.stream().anyMatch(contractProviderSupplier ->
                    contractProviderSupplier.getBranch().getClient().equals(client));
        }
    }

    private Boolean employeeFromSubcontractorBelongToClient(Client client, Employee employee) {
        if (employee.getSubcontract() == null) {
            return false;
        } else {
            List<ContractProviderSubcontractor> contracts = employee.getSubcontract().getContractsSubcontractor();
            return contracts.stream().anyMatch(contractProviderSupplier ->
                    contractProviderSupplier.getContractProviderSupplier().getBranch().getClient().equals(client));
        }
    }
}
