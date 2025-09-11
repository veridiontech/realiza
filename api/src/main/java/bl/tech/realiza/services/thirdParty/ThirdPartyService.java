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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThirdPartyService {
    private final AuthService authService;
    private final JwtService jwtService;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    public Boolean employeeStatus(String employeeCpf, LoginRequestDto request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        String clientId = getClientIdByToken(token);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        if (!client.getThirdParty()) {
            throw new ForbiddenException("Client didn't join third party!");
        }
        List<Employee> employees = employeeRepository.findAllByCpf(employeeCpf);
        employees.removeIf(employee -> !(employeeFromSupplierBelongToClient(client, employee)
                || employeeFromSubcontractorBelongToClient(client, employee)));

        return employees.stream().allMatch(employee -> employee.getDocumentEmployees().stream().allMatch(Document::getConforming));
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
