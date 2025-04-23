package bl.tech.realiza.gateways.responses.contracts;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.Requirement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContractSubcontractorResponseDto {
    private String idContract;
    private String serviceType;
    private String serviceDuration;
    private String serviceName;
    private String description;
    private String allocatedLimit;
    private String idResponsible;
    private Contract.ExpenseType expenseType;
    private String contractReference;
    private String idContractSupplier;
    private Contract.IsActive isActive;
    private Activity activity;
    private List<Requirement> requirements;
    private String idSupplier;
    private String nameSupplier;
    private String idSubcontractor;
    private String nameSubcontractor;
}
