package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Contract;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractSupplierPostRequestDto {
    private String serviceType;
    private String serviceDuration;
    private String serviceName;
    private String description;
    private String allocatedLimit;
    private String idResponsible;
    private String idRequester;
    private Date dateStart;
    private Contract.ExpenseType expenseType;
    private String contractReference;
    private Boolean subcontractPermission;
    private String idActivity;
    private List<String> requirements;
    private String corporateName;
    private String idBranch;
    private ProviderDatas providerDatas;

    @Data
    public class ProviderDatas {
        private String email;
        private String cnpj;
        private String telephone;
    }
}
