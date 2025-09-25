package bl.tech.realiza.gateways.requests.contracts;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeToContractRequestDto {
    private List<String> employees;
}
