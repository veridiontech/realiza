package bl.tech.realiza.gateways.requests.contracts;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeToContractRequestDto {
    private List<String> employees;
}
