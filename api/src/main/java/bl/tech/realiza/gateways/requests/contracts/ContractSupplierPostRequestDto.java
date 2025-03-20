package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Contract;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractSupplierPostRequestDto {
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
    private Date dateStart;
    private Contract.ExpenseType expenseType;
    @NotEmpty
    private String contractReference;
    @NotNull
    private Boolean subcontractPermission;
    @NotEmpty
    private String idActivity;
    @NotEmpty
    private List<String> requirements;
    @NotEmpty
    private String corporateName;
    @NotEmpty
    private String idBranch;
    @NotEmpty
    private ProviderDatas providerDatas;

    @Data
    public class ProviderDatas {
        @NotEmpty
        private String email;
        @NotEmpty
        private String cnpj;
        @NotEmpty
        private String telephone;
    }
}
