package bl.tech.realiza.gateways.responses.queue;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupMessage {
    private String type;
    private Client client;
    private Branch branch;
    private ContractProviderSupplier contractSupplier;
    private ContractProviderSubcontractor contractSubcontractor;
    private List<String> activitiesId;
    private List<Employee> employees;
}

