package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Contract;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractSubcontractorPostRequestDto {
    // contract
    private String serviceName;
    private String contractReference;
    private String description;
    private Contract.ExpenseType expenseType;
    private Boolean labor;
    private Boolean hse;
    private Date dateStart;
    private String idRequester;
    private List<String> idActivities;
    private String idContractSupplier;

    private ContractSupplierPostRequestDto.ProviderDatas providerDatas;
}
