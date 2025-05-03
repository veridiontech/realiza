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
    @NotEmpty
    private String serviceName;
    @NotEmpty
    private String contractReference;
    @NotEmpty
    private String description;
    @NotEmpty
    private Contract.ExpenseType expenseType;
    @NotNull
    private Boolean labor;
    @NotNull
    private Boolean hse;
    private Date dateStart;
    @NotEmpty
    private String idRequester;
    private List<String> idActivities;
    @NotEmpty
    private String idContractSupplier;

    @NotEmpty
    private ContractSupplierPostRequestDto.ProviderDatas providerDatas;
}
