package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Contract;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ContractSubcontractorPostRequestDto {
    @NotEmpty
    private String serviceType;
    @NotEmpty
    private String serviceDuration;
    @NotEmpty
    private String serviceName;
    @NotEmpty
    private String description;
    @NotEmpty
    private String allocatedLimit;
    @NotEmpty
    private String idResponsible;
    @NotEmpty
    private String idRequester;
    @NotEmpty
    private Contract.ExpenseType expenseType;
    @NotEmpty
    private String contractReference;
    @NotEmpty
    private String idContractSupplier;
    @NotEmpty
    private String idActivity;
    @NotNull
    private Boolean managementLabor;
    @NotNull
    private Boolean managementHse;
    @NotEmpty
    private List<String> requirements;
    @NotEmpty
    private String corporateName;
    @NotEmpty
    private ContractSupplierPostRequestDto.ProviderDatas providerDatas;
}
