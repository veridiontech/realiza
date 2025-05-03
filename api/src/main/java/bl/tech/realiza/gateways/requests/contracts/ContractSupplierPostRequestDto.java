package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Contract;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractSupplierPostRequestDto {
    // contract
    private String serviceName;
    private String contractReference;
    private String description;
    private String idResponsible;
    private Contract.ExpenseType expenseType;
    private Boolean labor;
    private Boolean hse;
    private Date dateStart;
    private String idServiceType;
    private String idRequester;
    private Boolean subcontractPermission;
    private List<String> idActivities;
    private String idBranch;
    private ProviderDatas providerDatas;

    @Data
    public static class ProviderDatas {
        private String corporateName;
        private String email;
        private String cnpj;
        private String telephone;
    }
}
